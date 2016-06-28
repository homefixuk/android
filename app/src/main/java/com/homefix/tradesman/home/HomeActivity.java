package com.homefix.tradesman.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.BaseToolbarNavMenuActivity;
import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.splashscreen.SplashScreenActivity;

/**
 * Created by samuel on 6/22/2016.
 */

public class HomeActivity extends BaseToolbarNavMenuActivity<HomeView, HomePresenter> implements HomeView {

    public HomeActivity() {
        super(HomeActivity.class.getSimpleName());
    }

    @Override
    public HomePresenter getPresenter() {
        if (presenter == null) presenter = new HomePresenter();

        return presenter;
    }

    @Override
    protected HomeView getThisView() {
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item == null) return false;

        String name = item.getTitle().toString();

        if (name.equals(getString(R.string.action_logout))) {
            showConfirmDialog("Are you sure you want to logout?", "LOGOUT", "CANCEL", new ConfirmDialogCallback() {

                @Override
                public void onPositive() {
                    UserController.clearCurrentUser(getContext());
                    startActivity(new Intent(getBaseActivity(), SplashScreenActivity.class));
                    finish();
                }

            });
            return true;
        }

        return false;
    }

}
