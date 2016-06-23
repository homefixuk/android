package com.homefix.tradesman.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.homefix.tradesman.R;
import com.homefix.tradesman.base.presenter.BaseActivityPresenter;
import com.homefix.tradesman.base.view.BaseActivityView;
import com.homefix.tradesman.common.PermissionsHelper;
import com.homefix.tradesman.home.HomeActivity;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.MyLog;
import com.samdroid.string.Strings;

import icepick.Icepick;

/**
 * Created by samuel on 1/7/2016.
 */
public abstract class HomeFixBaseActivity<V extends BaseActivityView, P extends BaseActivityPresenter<V>> extends AppCompatActivity implements BaseActivityView {

    protected final String TAG;

    private MaterialDialog mBaseDialog;

    protected P presenter;

    protected View mLogoView;

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
        mLogoView = findViewById(R.id.logo_img);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.e(TAG, "[onCreate]");
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
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().onResume();
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
    public void onAppInstalled(String androidPackage) {

    }

    @Override
    public void onAppUninstalled(String androidPackage) {

    }

    @Override
    public void goToApp(boolean isUserNew, View logoView) {
        Bundle optionsBundle = new Bundle();
        if (logoView != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, logoView, "logo");
            optionsBundle = options.toBundle();
        }

        Object user = "";

        // if there is no user
        if (user == null) {
//            startActivity(new Intent(this, LoginSignupActivity.class));

        } else {
            if (PermissionsHelper.isMissingPermission(this)) {
                // else if there is a missing permissions //
//                startActivity(new Intent(this, PermissionsActivity.class));

            } else {
                // else go to HomeActivity //
                Intent i = new Intent(this, HomeActivity.class);
                i.putExtra("previousActivity", TAG);
                startActivity(i);
            }
        }

        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        getPresenter().onBackPressed();
    }

    @Override
    public View getLogoView() {
        return mLogoView;
    }

    @Override
    public void onScrollingStarted() {

    }

    @Override
    public void onScrollingStopped() {

    }
}
