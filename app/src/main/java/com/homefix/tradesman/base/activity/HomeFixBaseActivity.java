package com.homefix.tradesman.base.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.homefix.tradesman.BuildConfig;
import com.homefix.tradesman.R;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.base.presenter.BaseActivityPresenter;
import com.homefix.tradesman.base.view.BaseActivityView;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.common.PermissionsHelper;
import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.home.HomeActivity;
import com.homefix.tradesman.login.LoginActivity;
import com.homefix.tradesman.model.CCA;
import com.homefix.tradesman.model.Tradesman;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;

import org.greenrobot.eventbus.EventBus;

import icepick.Icepick;
import icepick.State;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by samuel on 1/7/2016.
 */
public abstract class HomeFixBaseActivity<V extends BaseActivityView, P extends BaseActivityPresenter<V>>
        extends AppCompatActivity
        implements BaseActivityView, OnGotObjectListener<CCA> {

    protected final String TAG;

    private MaterialDialog mBaseDialog;

    protected P presenter;

    protected boolean
            checkPermissions = true,
            checkCCA = false,
            calledPermissionResult = false;

    protected String permissionRequested;

    protected long checkPermissionsDelay = 0;

    protected static CCA mCca;

    public HomeFixBaseActivity(String TAG) {
        this.TAG = TAG;
    }

    /**
     * Create the presenter for this view
     */
    public abstract P getPresenter();

    /**
     * @return the layout resource Id
     */
    public abstract int getLayoutId();

    abstract protected V getThisView();

    /**
     * Inject the views into this activity
     */
    public void injectDependencies() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        setContentView(getLayoutId());
        injectDependencies();

        getPresenter().attachView(getThisView());
        getPresenter().onCreate(savedInstanceState);

        // hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        // set the status bar colour
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        Intent intent = getIntent();
        if (intent != null) {
            checkPermissions = intent.getBooleanExtra("checkPermissions", true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().onResume();

        // make sure we have the CCA
        if (checkCCA && mCca == null) {
            Callback<CCA> callback = new Callback<CCA>() {

                @Override
                public void onResponse(Call<CCA> call, Response<CCA> response) {
                    mCca = response != null ? response.body() : null;

                    if (mCca == null) mCca = CacheUtils.readObjectFile("my_cca", CCA.class);

                    onGotThing(mCca);
                }

                @Override
                public void onFailure(Call<CCA> call, Throwable t) {
                    if (MyLog.isIsLogEnabled()) t.printStackTrace();

                    // if it fails, get the cca from the cache
                    mCca = CacheUtils.readObjectFile("my_cca", CCA.class);
                    onGotThing(mCca);
                }

            };

            if (BuildConfig.FLAVOR.equals("apiary_mock")) {
                HomeFix.getMockAPI().getCCA(getString(HomeFix.API_KEY), UserController.getToken()).enqueue(callback);

            } else if (BuildConfig.FLAVOR.equals("custom")) {
                HomeFix.getAPI().getCCA(getString(HomeFix.API_KEY), UserController.getToken()).enqueue(callback);
            }
        }

        if (checkPermissions && !calledPermissionResult) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    approveAllRequiredPermissions();
                }
            }, checkPermissionsDelay);
        }
    }

    @Override
    public void onPause() {
        getPresenter().onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        getPresenter().onDestroy();
        super.onDestroy();
    }

    public static int getAnimationIn() {
        return 0;
    }

    public static int getAnimationOut() {
        return 0;
    }

    @Override
    public void finishWithIntent(Intent data) {
        if (data == null) setResult(RESULT_OK);
        else setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void finishWithIntentAndAnimation(Intent data) {
        finishWithIntent(data);
        overridePendingTransition(R.anim.expand_in_from_partial, getAnimationOut());
    }

    @Override
    public void finishWithAnimation() {
        finishWithIntent(null);
        overridePendingTransition(R.anim.expand_in_from_partial, getAnimationOut());
    }

    @Override
    public void hideDialog() {
        if (mBaseDialog != null) mBaseDialog.dismiss();
    }

    @Override
    public void showDialog(String message, boolean loading) {
        hideDialog();

        if (loading) mBaseDialog = MaterialDialogWrapper.getLoadingDialog(this, message).build();
        else mBaseDialog = MaterialDialogWrapper.getAlertDialog(this, message).build();

        try {
            mBaseDialog.show();
        } catch (Exception e) {
        }
    }

    @Override
    public void showConfirmDialog(String message, String positiveText, String negative, final ConfirmDialogCallback callback) {
        hideDialog();

        mBaseDialog = MaterialDialogWrapper.getConfirmationDialog(
                this,
                message,
                positiveText,
                negative,
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        callback.onPositive();
                    }
                },
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        callback.onNegative();
                    }
                });

        mBaseDialog.show();
    }

    @Override
    public void updateDialogMessage(String message) {
        if (mBaseDialog == null) return;

        mBaseDialog.setContent(Strings.returnSafely(message));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return super.registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        super.unregisterReceiver(receiver);
    }

    @Override
    public boolean isFinishing() {
        return super.isFinishing();
    }

    @Override
    public boolean isDestroyed() {
        return super.isDestroyed();
    }

    @Override
    public HomeFixBaseActivity getBaseActivity() {
        return this;
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getPresenter().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void goToApp() {
        MyLog.e(TAG, "[goToApp]");
        // check if user logged in
        Tradesman tradesman = UserController.getCurrentUser();

        Intent i;

        // if there is no user
        if (tradesman == null) {
            // go to login activity
            i = new Intent(this, LoginActivity.class);

        } else {
            // else go to HomeActivity //
            i = new Intent(this, HomeActivity.class);
            i.putExtra("previousActivity", TAG);
        }

        if (i != null) startActivity(i);
        finishWithAnimation();
    }

    @Override
    public void onBackPressed() {
        getPresenter().onBackPressed();
    }

    @Override
    public void onScrollingStarted() {

    }

    @Override
    public void onScrollingStopped() {

    }

    /**
     * @return if the app is missing a permission needed to run the app
     */
    private boolean approveAllRequiredPermissions() {
        calledPermissionResult = false;
        permissionRequested = "";

        // make sure the user has granted us all permissions
        for (String s : PermissionsHelper.REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                permissionRequested = s;
                new PermissionsHelper.PermissionClickListener(
                        this,
                        permissionRequested,
                        "Please allow HomeFix this permission",
                        Ids.CODE_GENERIC_PERMISSION,
                        false).onClick(null);
                return false;
            }
        }

        onAllRequiredPermissionsGranted();
        return true;
    }

    protected void onAllRequiredPermissionsGranted() {
        getPresenter().onAllRequiredPermissionsGranted();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        calledPermissionResult = true;

        switch (requestCode) {

            case Ids.CODE_GENERIC_PERMISSION:
                // if we still do not have permission, try again
                if (!PermissionsHelper.hasPermission(getBaseActivity(), permissionRequested)) {
                    new PermissionsHelper.PermissionClickListener(
                            this,
                            permissionRequested,
                            "Please allow HomeFix the permission",
                            Ids.CODE_GENERIC_PERMISSION,
                            true)
                            .onClick(null);

                } else {
                    // else make sure all other permissions have been requested //
                    approveAllRequiredPermissions();
                }
                break;
        }
    }

    @Override
    public void onGotThing(CCA cca) {
        if (cca == null) return;

        CacheUtils.writeObjectFile("my_cca", cca);
    }

}
