package com.homefix.tradesman.splashscreen;

import android.os.Bundle;

import com.homefix.tradesman.base.presenter.BaseActivityPresenter;
import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.model.User;
import com.samdroid.common.MyLog;
import com.samdroid.listener.interfaces.OnGotObjectListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by samuel on 6/22/2016.
 */

public class SplashScreenPresenter extends BaseActivityPresenter<SplashScreenView> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load the current user
        UserController.loadCurrentUser(new OnGotObjectListener<User>() {
            @Override
            public void onGotThing(User user) {
                if (user == null) MyLog.e("HomeFixApplication", "No Current user found");
                else MyLog.e("HomeFixApplication", "Loaded User: " + user.getName());

                // now wait before going into the app
                new Timer().schedule(new TimerTask() {

                    @Override
                    public void run() {
                        getView().goToApp(false, null);
                    }

                }, 3 * 1000);
            }
        });
    }

}
