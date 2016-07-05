package com.homefix.tradesman.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.homefix.tradesman.HomeFixApplication;

/**
 * Created by samuel on 7/5/2016.
 */

public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        HomeFixApplication.startLocationTracking(context);
    }

}
