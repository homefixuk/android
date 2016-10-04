package com.homefix.tradesman.splashscreen;

import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.homefix.tradesman.HomeFixApplication;
import com.homefix.tradesman.base.presenter.BaseActivityPresenter;
import com.homefix.tradesman.service.LocationService;
import com.samdroid.common.MyLog;

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

        // Initialize Firebase Auth
        // Firebase instance variables
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            MyLog.e("HomeFixApplication", "No Current user found");

        } else {
            // else there is a user logged in
            MyLog.e("HomeFixApplication", "Loaded User: " + mFirebaseUser.getDisplayName());

            HomeFixApplication.setupAppAfterLogin(getView().getBaseActivity().getApplicationContext());
        }

        try {
            // if the location service is not running
            if (!LocationService.isRunning() && getView().getContext() != null) {
                getView().getContext().startService(new Intent(getView().getContext(), LocationService.class));
            }
        } catch (Exception e) {
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
}
