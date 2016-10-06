package com.homefix.tradesman.task;

import android.content.Intent;
import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseAuth;
import com.homefix.tradesman.HomeFixApplication;
import com.homefix.tradesman.base.view.BaseActivityView;
import com.homefix.tradesman.base.view.BaseView;
import com.homefix.tradesman.model.Tradesman;
import com.homefix.tradesman.splashscreen.SplashScreenActivity;

import io.nlopez.smartlocation.SmartLocation;

/**
 * Created by samuel on 6/28/2016.
 */

public class LogoutTask extends AsyncTask<Void, Void, Boolean> {

    private BaseActivityView baseView;

    private LogoutTask(BaseActivityView baseView) {
        this.baseView = baseView;
    }

    public static boolean doLogout(final BaseActivityView baseView) {
        if (baseView == null) return false;

        baseView.showConfirmDialog("Are you sure you want to logout?", "LOGOUT", "CANCEL", new BaseView.ConfirmDialogCallback() {

            @Override
            public void onPositive() {
                new LogoutTask(baseView).execute();
            }

        });

        return true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (baseView != null) baseView.showDialog("Logging out...", true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (baseView != null) {
            // cancel the location tracking
            HomeFixApplication.startLocationTracking(baseView.getContext());

            if (baseView.getContext() != null)
                SmartLocation.with(baseView.getContext()).activity().stop();
        }

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signOut();

        Tradesman.onLogout();

        return mFirebaseAuth.getCurrentUser() == null;
    }

    @Override
    protected void onPostExecute(Boolean loggedOut) {
        if (baseView == null) return;

        if (!loggedOut) {
            baseView.showDialog("Sorry, unable to log out right now. Please try again", false);
            return;
        }

        // restart the app
        baseView.getBaseActivity().startActivity(new Intent(baseView.getContext(), SplashScreenActivity.class));
        baseView.getBaseActivity().finish();
    }

}
