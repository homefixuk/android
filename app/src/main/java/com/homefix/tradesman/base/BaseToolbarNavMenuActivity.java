package com.homefix.tradesman.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.presenter.BaseToolbarNavMenuActivityPresenter;
import com.homefix.tradesman.base.view.BaseToolbarNavMenuActivityView;

/**
 * Created by samuel on 6/22/2016.
 */

public abstract class BaseToolbarNavMenuActivity<V extends BaseToolbarNavMenuActivityView, P extends BaseToolbarNavMenuActivityPresenter<V>> extends BaseToolbarActivity<V, P>
        implements BaseToolbarNavMenuActivityView, NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private FloatingActionButton mFab;

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

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide the recommendations option if it is set to false in the config
        Menu menu = navigationView.getMenu();

        // Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(this);

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

}
