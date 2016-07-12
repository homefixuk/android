package com.homefix.tradesman.base;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.presenter.BaseToolbarActivityPresenter;
import com.homefix.tradesman.base.view.BaseToolbarActivityView;
import com.samdroid.string.Strings;
import com.samdroid.view.ScreenUtils;

/**
 * Created by samuel on 6/16/2016.
 */

public abstract class BaseToolbarActivity<V extends BaseToolbarActivityView, P extends BaseToolbarActivityPresenter<V>> extends HomeFixBaseActivity<V, P> implements BaseToolbarActivityView {

    private Toolbar toolbar;
    private TextView mActionBarTitleTxt;
    private ImageView mActionBarTitleIconRight;

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
    public void injectDependencies() {
        super.injectDependencies();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionBarTitleTxt = (TextView) findViewById(R.id.toolbar_title_text_override);
        mActionBarTitleIconRight = (ImageView) findViewById(R.id.title_icon_right);
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
        if (mActionBarTitleTxt == null) return;

        mActionBarTitleTxt.setOnClickListener(listener);
    }

    protected void setActionBarTitleTouchListener(View.OnTouchListener listener) {
        if (mActionBarTitleTxt == null) return;

        mActionBarTitleTxt.setOnTouchListener(listener);
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

}
