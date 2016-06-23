package com.homefix.tradesman.splashscreen;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.HomeFixBaseActivity;

/**
 * Created by samuel on 6/22/2016.
 */

public class SplashScreenActivity extends HomeFixBaseActivity<SplashScreenView, SplashScreenPresenter> implements SplashScreenView {

    public SplashScreenActivity() {
        super(SplashScreenActivity.class.getSimpleName());
    }

    @Override
    public SplashScreenPresenter getPresenter() {
        if (presenter == null) {
            presenter = new SplashScreenPresenter();
            presenter.attachView(this);
        }

        return presenter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash_scree;
    }

    @Override
    protected SplashScreenView getThisView() {
        return this;
    }

}
