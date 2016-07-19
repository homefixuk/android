package com.homefix.tradesman.base.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.fragment.BaseCloseFragment;
import com.homefix.tradesman.base.presenter.BaseCloseActivityPresenter;
import com.homefix.tradesman.base.view.BaseCloseActivityView;

/**
 * Created by samuel on 7/13/2016.
 */

public class BaseCloseActivity extends BaseToolbarActivity<BaseCloseActivityView, BaseCloseActivityPresenter<BaseCloseActivityView>> implements BaseCloseActivityView {

    protected BaseCloseFragment baseFragment;

    public BaseCloseActivity() {
        super(BaseCloseActivity.class.getSimpleName());
    }

    @Override
    public BaseCloseActivityPresenter getPresenter() {
        if (presenter == null) {
            presenter = new BaseCloseActivityPresenter<>();
            presenter.attachView(this);
        }

        return presenter;
    }

    @Override
    protected BaseCloseActivityView getThisView() {
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the action bar icon to close
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_48dp);
        }
    }

    @Override
    protected void onNavigationClickListener() {
        getPresenter().onCloseClicked();
    }

    @Override
    public void finishWithAnimation() {
        finishWithIntent(null);
        overridePendingTransition(R.anim.expand_in_from_partial, R.anim.right_slide_out);
    }

    @Override
    public void tryClose() {
        // if we can close
        if (baseFragment == null || baseFragment.canClose()) getPresenter().close();

        // else call the fragment to handle the user trying to close //
        baseFragment.onCloseClicked();
    }

}
