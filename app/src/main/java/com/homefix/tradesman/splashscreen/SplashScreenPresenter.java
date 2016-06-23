package com.homefix.tradesman.splashscreen;

import android.os.Bundle;

import com.homefix.tradesman.base.presenter.BaseActivityPresenter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by samuel on 6/22/2016.
 */

public class SplashScreenPresenter extends BaseActivityPresenter<SplashScreenView> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                getView().goToApp(false, null);
            }

        }, 3 * 1000);
    }

}
