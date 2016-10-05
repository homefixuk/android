package com.homefix.tradesman;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.homefix.tradesman.model.Tradesman;
import com.homefix.tradesman.service.LocationService;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;

/**
 * Created by samuel on 6/15/2016.
 */
public class HomeFixApplication extends MultiDexApplication {

    private static HomeFixApplication instance;

    private static final String TAG = HomeFixApplication.class.getSimpleName();

    public HomeFixApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // configure CacheUtilsLibrary
        CacheUtils.configureCache(this);

        MyLog.setLoggingEnabled(BuildConfig.DEBUG);

        Fresco.initialize(this);
    }

    public static void startLocationTracking(Context context) {
        if (context == null) return;

        Intent i = new Intent(context, LocationService.class);
        context.startService(i);
    }

    public static void stopLocationTracking(Context context) {
        if (context == null) return;

        Intent i = new Intent(context, LocationService.class);
        context.stopService(i);
    }

    public static void setupAppAfterLogin(Context context) {
        Tradesman.getCurrentTradesman();
    }

    public static HomeFixApplication getInstance() {
        return instance;
    }

    public static boolean hasNetwork() {
        return instance != null && instance.checkIfHasNetwork();
    }

    public boolean checkIfHasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
