package com.homefix.tradesman;

import android.support.multidex.MultiDexApplication;

import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.model.User;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.listener.interfaces.OnGotObjectListener;

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

        UserController.loadCurrentUser(new OnGotObjectListener<User>() {
            @Override
            public void onGotThing(User user) {
                if (user == null) {
                    MyLog.e("HomeFixApplication", "No User found");
                    return;
                }
                MyLog.e("HomeFixApplication", "Loaded User: " + user.getName());
            }
        });
    }

}
