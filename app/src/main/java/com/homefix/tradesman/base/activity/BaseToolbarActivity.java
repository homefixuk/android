package com.homefix.tradesman.base.activity;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.presenter.BaseToolbarActivityPresenter;
import com.homefix.tradesman.base.view.BaseToolbarActivityView;
import com.samdroid.string.Strings;
import com.samdroid.view.ScreenUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by samuel on 6/16/2016.
 */

public abstract class BaseToolbarActivity<V extends BaseToolbarActivityView, P extends BaseToolbarActivityPresenter<V>> extends HomeFixBaseActivity<V, P> implements BaseToolbarActivityView {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.title_and_icon_holder)
    protected View mTitleIconHolder;

    @BindView(R.id.toolbar_title_text_override)
    protected TextView mActionBarTitleTxt;

    @BindView(R.id.title_icon_right)
    protected ImageView mActionBarTitleIconRight;

    @BindView(R.id.content_frame)
    protected FrameLayout mContentView;

    private boolean showToolbar = true;

    public BaseToolbarActivity(String TAG) {
        this(TAG, true);
    }

    public BaseToolbarActivity(String TAG, boolean showToolbar) {
        super(TAG);
        this.showToolbar = showToolbar;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_content_fragment_with_app_bar;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing Toolbar and setting it as the actionbar
        setSupportActionBar(toolbar);
        setupToolbar();
    }

    private void setupToolbar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar == null) return;

        if (!showToolbar) {
            actionBar.hide();
            return;
        }

        actionBar.show();

        // disable the home icon, title and logo
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);

        // enable the use of a custom layout
        actionBar.setDisplayShowCustomEnabled(true);

        // get the action bar title text view
        actionBar.setTitle("");

        if (mActionBarTitleTxt != null) {
            // if the screen size is large, set the action bar title to be bold
            int mScreenSize = ScreenUtils.getScreenSize(this);
            if (mScreenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || mScreenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    mActionBarTitleTxt.setTextAppearance(R.style.SingleLineActionBarTheme_Bold);
                else
                    mActionBarTitleTxt.setTextAppearance(this, R.style.SingleLineActionBarTheme_Bold);
            }

            setActionbarTitle(R.string.app_name);
        }

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNavigationClickListener();
                }
            });
        }
    }

    public void setActionbarTitle(String title) {
        if (mActionBarTitleTxt != null) mActionBarTitleTxt.setText(Strings.returnSafely(title));
    }

    protected void setActionbarTitle(int stringResId) {
        if (mActionBarTitleTxt != null) mActionBarTitleTxt.setText(stringResId);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    protected void onNavigationClickListener() {
    }

    protected void setActionBarTitleClickListener(View.OnClickListener listener) {
        if (mTitleIconHolder == null) return;

        mTitleIconHolder.setOnClickListener(listener);
    }

    protected void setTitleIconRight(@DrawableRes int resId) {
        if (mActionBarTitleIconRight == null) return;

        mActionBarTitleIconRight.setImageResource(resId);
        mActionBarTitleIconRight.setVisibility(resId > 0 ? View.VISIBLE : View.GONE);
    }

    protected void animateTitleIconRight(int degrees) {
        if (mActionBarTitleIconRight == null) return;

        mActionBarTitleIconRight.animate().rotation(degrees).start();
    }

    /**
     * Replace the current fragment
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

}
