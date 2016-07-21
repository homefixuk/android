package com.homefix.tradesman;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.homefix.tradesman.model.Problem;
import com.homefix.tradesman.service.LocationService;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;

/**
 * Created by samuel on 6/15/2016.
 */
public class HomeFixApplication extends MultiDexApplication {

    private static final String TAG = HomeFixApplication.class.getSimpleName();

    public HomeFixApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // configure CacheUtilsLibrary
        CacheUtils.configureCache(this);

        MyLog.setLoggingEnabled(BuildConfig.DEBUG);
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

    public static void setupAppAfterLogin() {

        Problem.loadServiceTypes();
    }
}
