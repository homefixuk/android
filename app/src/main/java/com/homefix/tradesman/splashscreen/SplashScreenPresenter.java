package com.homefix.tradesman.splashscreen;

import android.content.Intent;

import com.homefix.tradesman.HomeFixApplication;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.base.presenter.BaseActivityPresenter;
import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.model.ServiceType;
import com.homefix.tradesman.model.Tradesman;
import com.homefix.tradesman.service.LocationService;
import com.samdroid.common.MyLog;
import com.samdroid.listener.interfaces.OnGotObjectListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by samuel on 6/22/2016.
 */

public class SplashScreenPresenter extends BaseActivityPresenter<SplashScreenView> {

    @Override
    public void onAllRequiredPermissionsGranted() {
        super.onAllRequiredPermissionsGranted();

        if (!isViewAttached()) return;

        // load the current user
        UserController.loadCurrentUser(true, new OnGotObjectListener<Tradesman>() {
            @Override
            public void onGotThing(Tradesman user) {
                if (user == null) {
                    MyLog.e("HomeFixApplication", "No Current user found");

                } else {
                    // else there is a user logged in
                    MyLog.e("HomeFixApplication", "Loaded User: " + user.getName());

                    HomeFixApplication.setupAppAfterLogin();
                }

                // if the location service is not running
                if (!LocationService.isRunning() && getView().getContext() != null) {
                    getView().getContext().startService(new Intent(getView().getContext(), LocationService.class));
                }

                // now wait before going into the app
                new Timer().schedule(new TimerTask() {

                    @Override
                    public void run() {
                        if (!isViewAttached()) return;

                        getView().goToApp();
                    }

                }, 1000);
            }
        });
    }
}
