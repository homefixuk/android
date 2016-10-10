package com.homefix.tradesman;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.homefix.tradesman.common.AnalyticsHelper;
import com.homefix.tradesman.firebase.FirebaseConfigHelper;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.model.Tradesman;
import com.homefix.tradesman.service.LocationService;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.common.TimeUtils;

/**
 * Created by samuel on 6/15/2016.
 */
public class HomeFixApplication extends MultiDexApplication {

    private static HomeFixApplication instance;

    protected static FirebaseRemoteConfig mRemoteConfig;

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

    static boolean isFirebaseSetup = false;

    public static boolean isFirebaseSetup(Context context) {
        return FirebaseApp.getApps(context).isEmpty();
    }

    /**
     * Note: cannot be called in the onCreate in this Application class
     */
    public static void setupFirebase(Context context) {
        if (context == null || isFirebaseSetup || FirebaseApp.getApps(context).isEmpty()) return;

        // setup the Firebase database
        FirebaseAuth.getInstance();
        setupFirebaseConfig(context);
        FirebaseUtils.sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken(), null);
        AnalyticsHelper.init(context.getApplicationContext());
        isFirebaseSetup = true;
    }

    public static void setupFirebaseConfig(final Context context) {
        if (context == null || FirebaseApp.getApps(context).isEmpty()) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                final FirebaseRemoteConfig config = getRemoteConfig(context);

                if (config == null) return;

                long cacheExpiration = TimeUtils.getHoursInMillis(1);

                // If developer mode is enabled reduce cacheExpiration to 0 so that
                // each fetch goes to the server. This should not be used in release
                // builds.
                if (config.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
                    cacheExpiration = 0;
                }

                // fetch the config from the server
                config.fetch(cacheExpiration).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        config.activateFetched();

                        // save the boolean to cache so we can access it later without a context
                        CacheUtils.writeObjectFile("set_data_persistence_enabled", config.getBoolean("set_data_persistence_enabled"));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MyLog.e(TAG, "Failed to fetch config: " + e.getMessage());
                        MyLog.printStackTrace(e);
                    }
                });
            }

        }).start();
    }

    public synchronized static FirebaseRemoteConfig getRemoteConfig(Context context) {
        if (FirebaseApp.getApps(context).isEmpty()) return mRemoteConfig;

        if (mRemoteConfig == null) {
            mRemoteConfig = FirebaseRemoteConfig.getInstance();

            if (mRemoteConfig == null) return mRemoteConfig;

            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build();
            mRemoteConfig.setConfigSettings(configSettings);

            mRemoteConfig.setDefaults(FirebaseConfigHelper.getDefaultFirebaseConfig());
        }

        return mRemoteConfig;
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
        Tradesman.setupCurrentTradesman();

        if (context == null) return;
        AnalyticsHelper.setupUser(context.getApplicationContext());
        AnalyticsHelper.track(context.getApplicationContext(), "loggedIn", new Bundle());
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
