package com.homefix.tradesman.base.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.presenter.BaseToolbarNavMenuActivityPresenter;
import com.homefix.tradesman.base.view.BaseToolbarNavMenuActivityView;
import com.homefix.tradesman.common.HtmlHelper;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.common.PermissionsHelper;
import com.homefix.tradesman.model.CCA;
import com.homefix.tradesman.model.Tradesman;
import com.homefix.tradesman.profile.ProfileFragment;
import com.samdroid.common.IntentHelper;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by samuel on 6/22/2016.
 */

public abstract class BaseToolbarNavMenuActivity<V extends BaseToolbarNavMenuActivityView, P extends BaseToolbarNavMenuActivityPresenter<V>>
        extends BaseToolbarActivity<V, P>
        implements BaseToolbarNavMenuActivityView, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.navigation_view)
    protected NavigationView navigationView;

    @BindView(R.id.drawer)
    protected DrawerLayout drawerLayout;

    @BindView(R.id.fab)
    protected FloatingActionButton mFab;

    protected TextView mUserNameTxt, mUserInfoTxt;

    @BindView(R.id.call_cca_text)
    protected TextView mCallCcaTxt;

    protected Snackbar snackbar;

    private String ccaPhoneNumber;

    protected ProfileFragment<BaseToolbarNavMenuActivity> mProfileFragment;

    public BaseToolbarNavMenuActivity(String TAG) {
        super(TAG);
    }

    public BaseToolbarNavMenuActivity(String TAG, boolean showToolbar) {
        super(TAG, showToolbar);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_toolbar_navigation_menu;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();

        if (navigationView != null) {
            View navHeaderView = navigationView.getHeaderView(0);
            if (navHeaderView != null) {
                mUserNameTxt = ButterKnife.findById(navHeaderView, R.id.user_name);
                mUserInfoTxt = ButterKnife.findById(navHeaderView, R.id.user_info);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Menu menu = navigationView.getMenu();

        // Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);
        if (navHeaderView != null) {
            navHeaderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onNavigationProfileClicked();
                }
            });
        }

        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(
                        this,
                        drawerLayout,
                        getToolbar(),
                        R.string.openDrawer,
                        R.string.closeDrawer) {

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);

                        // un-check every menu item when the menu closes
                        Menu menu = navigationView.getMenu();
                        for (int i = 0; i < menu.size(); i++) {
                            MenuItem menuItem = menu.getItem(i);
                            menuItem.setChecked(false);
                        }
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                    }
                };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        drawerLayout.closeDrawers();

        mFab.setImageResource(R.drawable.homefix_icon);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().onFabClicked();
            }
        });
    }

    protected void resetActionBarTitle() {
        setActionbarTitle(R.string.app_name);
        setTitleIconRight(0);
        setActionBarTitleClickListener(null);
    }

    protected void onNavigationProfileClicked() {
        if (mProfileFragment == null) {
            mProfileFragment = new ProfileFragment<>();
        }

        replaceFragment(mProfileFragment);

        resetActionBarTitle();
        setActionbarTitle("My Profile");
        supportInvalidateOptionsMenu();

        if (drawerLayout != null) drawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showNavMenu() {
        if (drawerLayout == null) return;

        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void hideNavMenu() {
        if (drawerLayout == null) return;

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onResume() {
        super.onResume();
        Tradesman.addCurrentTradesmanListener(tradesmanListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        Tradesman.removeCurrentTradesmanListener(tradesmanListener);
    }

    private final OnGotObjectListener<Tradesman> tradesmanListener = new OnGotObjectListener<Tradesman>() {
        @Override
        public void onGotThing(Tradesman tradesman) {
            if (tradesman == null) return;

            if (mUserNameTxt != null) mUserNameTxt.setText(tradesman.getName());
            if (mUserInfoTxt != null) mUserInfoTxt.setText(tradesman.getEmail());
        }
    };

    public void setCCANumber(String phone) {
        if (mCallCcaTxt == null) return;

        ccaPhoneNumber = phone;

        if (Strings.isEmpty(phone)) {
            mCallCcaTxt.setText("Call CCA");
            mCallCcaTxt.setOnClickListener(null);
            return;
        }

        mCallCcaTxt.setText(HtmlHelper.fromHtml("Call CCA " + Strings.setStringTags(ccaPhoneNumber, Strings.HTML_TAG.BOLD)));
        mCallCcaTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionsHelper.isMissingPermission(getBaseActivity(), Manifest.permission.CALL_PHONE)) {
                    PermissionsHelper.requestPermission(getBaseActivity(), Manifest.permission.CALL_PHONE, Ids.CODE_CALL_PHONE_PERMISSION);
                    return;
                }

                IntentHelper.callPhoneNumber(getContext(), ccaPhoneNumber);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        calledPermissionResult = true;

        switch (requestCode) {

            case Ids.CODE_CALL_PHONE_PERMISSION:
                permissionRequested = Manifest.permission.CALL_PHONE;

                // if we still do not have permission, try again
                if (!PermissionsHelper.hasPermission(getBaseActivity(), permissionRequested)) {
                    new PermissionsHelper.PermissionClickListener(
                            this,
                            permissionRequested,
                            "Please allow HomeFix to open your phone dialer from this app.",
                            Ids.CODE_CALL_PHONE_PERMISSION,
                            true).onClick(null);
                } else {
                    // else we just got the permission, so open the dialer //
                    IntentHelper.callPhoneNumber(getContext(), ccaPhoneNumber);
                }
                break;

        }
    }

    @Override
    public void onGotThing(CCA cca) {
        super.onGotThing(cca);

        if (cca != null) setCCANumber(cca.getMobilePhone());
    }

    /**
     * Replace the current fragment with a new one
     *
     * @param fragment
     */
    protected void replaceFragment(Fragment fragment) {
        if (fragment == null) return;

        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();

        } catch (Exception e) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Ids.WORK_AREAS_CODE) {
            if (resultCode == RESULT_CANCELED || mProfileFragment == null || data == null) return;

            ArrayList<String> list = data.getStringArrayListExtra("list");
            if (list == null) return;

            mProfileFragment.onNewWorkAreasReturned(list);
            return;
        }
    }

    protected void makeSnackbar(String internetStatus) {
        snackbar = Snackbar
                .make(drawerLayout, internetStatus, Snackbar.LENGTH_LONG)
                .setAction("X", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
    }
}
