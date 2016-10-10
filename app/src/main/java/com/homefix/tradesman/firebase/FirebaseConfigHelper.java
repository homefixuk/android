package com.homefix.tradesman.firebase;

import com.lifeofcoding.cacheutlislibrary.CacheUtils;

import java.util.HashMap;

/**
 * Created by samuel on 8/24/2016.
 */

public class FirebaseConfigHelper {

    private static final HashMap<String, Object> defaultFirebaseConfig = new HashMap<>();

    static {
        defaultFirebaseConfig.put("set_data_persistence_enabled", false);
        // save the boolean to cache so we can access it later without a context
        CacheUtils.writeObjectFile("set_data_persistence_enabled", false);
    }

    public static HashMap<String, Object> getDefaultFirebaseConfig() {
        return defaultFirebaseConfig;
    }

}
