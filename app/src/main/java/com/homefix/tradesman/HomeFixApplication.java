package com.homefix.tradesman;

import android.support.multidex.MultiDexApplication;

import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;

/**
 * Created by samuel on 6/15/2016.
 */
public class HomeFixApplication extends MultiDexApplication {

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

}
