package com.homefix.tradesman.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.BaseToolbarNavMenuActivity;
import com.homefix.tradesman.task.LogoutTask;

/**
 * Created by samuel on 6/22/2016.
 */

public class HomeActivity extends BaseToolbarNavMenuActivity<HomeView, HomePresenter> implements HomeView {

    private int mCurrentPage;
    private HomeFragment homeFragment;

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
        checkCCA = true;
        checkPermissions = true;

        showHomeFragment();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item == null) return false;

        String name = item.getTitle().toString();

        if (name.equals(getString(R.string.action_home))) {
            if (mCurrentPage == R.string.action_home) {
                hideNavMenu();
                return false;
            }

            showHomeFragment();
            return true;

        } else if (name.equals(getString(R.string.action_logout))) {
            if (mCurrentPage == R.string.action_logout) {
                hideNavMenu();
                return false;
            }

            LogoutTask.doLogout(this);
            return true;
        }

        return false;
    }

    private void showHomeFragment() {
        if (homeFragment == null) homeFragment = new HomeFragment();

        replaceFragment(homeFragment);
        mCurrentPage = R.string.action_home;
    }

}
