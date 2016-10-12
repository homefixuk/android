package com.homefix.tradesman.firebase;

import android.content.Context;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.homefix.tradesman.HomeFixApplication;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.VariableUtils;
import com.samdroid.string.Strings;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by samuel on 8/24/2016.
 */

public class FirebaseConfigHelper {

    private static final HashMap<String, Object> defaultFirebaseConfig = new HashMap<>();

    static {
        defaultFirebaseConfig.put("set_data_persistence_enabled", false);
        // save the boolean to cache so we can access it later without a context
        CacheUtils.writeObjectFile("set_data_persistence_enabled", false);

        defaultFirebaseConfig.put("needAccountEmails", "[\"info@homefix.co.uk\",\"george@homefix.co.uk\",\"sokratis@homefix.co.uk\"]");
    }

    public static HashMap<String, Object> getDefaultFirebaseConfig() {
        return defaultFirebaseConfig;
    }

    public static String getConfigString(Context context, String key) {
        FirebaseRemoteConfig config = HomeFixApplication.getRemoteConfig(context);
        if (config == null) {
            return defaultFirebaseConfig.containsKey(key) ? (String) defaultFirebaseConfig.get(key) : "";
        }

        return Strings.returnSafely(config.getString(key));
    }

    public static boolean getConfigBoolean(Context context, String key) {
        FirebaseRemoteConfig config = HomeFixApplication.getRemoteConfig(context);
        if (config == null) {
            return defaultFirebaseConfig.containsKey(key) && (boolean) defaultFirebaseConfig.get(key);
        }

        return config.getBoolean(key);
    }

    public static int getConfigInteger(Context context, String key) {
        FirebaseRemoteConfig config = HomeFixApplication.getRemoteConfig(context);
        if (config == null) {
            return defaultFirebaseConfig.containsKey(key) ? (int) defaultFirebaseConfig.get(key) : 0;
        }

        return (int) config.getLong(key);
    }

    public static long getConfigLong(Context context, String key) {
        FirebaseRemoteConfig config = HomeFixApplication.getRemoteConfig(context);
        if (config == null) {
            return defaultFirebaseConfig.containsKey(key) ? (long) defaultFirebaseConfig.get(key) : 0L;
        }

        return config.getLong(key);
    }

    public static double getConfigDouble(Context context, String key) {
        FirebaseRemoteConfig config = HomeFixApplication.getRemoteConfig(context);
        if (config == null) {
            return defaultFirebaseConfig.containsKey(key) ? (double) defaultFirebaseConfig.get(key) : 0.0;
        }

        return config.getDouble(key);
    }

    public static List<String> getConfigStringList(Context context, String key) {
        FirebaseRemoteConfig config = HomeFixApplication.getRemoteConfig(context);
        String value;
        if (config == null) {
            if (!defaultFirebaseConfig.containsKey(key)) return new ArrayList<>();
            value = (String) defaultFirebaseConfig.get(key);

        } else {
            value = config.getString(key);
        }

        try {
            JSONArray jsonArray = new JSONArray(value);
            return VariableUtils.jsonStringsArrayToList(jsonArray);

        } catch (JSONException e) {
            return new ArrayList<>();
        }
    }

    public static List<Integer> getConfigIntegerList(Context context, String key) {
        FirebaseRemoteConfig config = HomeFixApplication.getRemoteConfig(context);
        String value;
        if (config == null) {
            if (!defaultFirebaseConfig.containsKey(key)) return new ArrayList<>();
            value = (String) defaultFirebaseConfig.get(key);

        } else {
            value = config.getString(key);
        }

        try {
            JSONArray jsonArray = new JSONArray(value);
            return VariableUtils.jsonArrayToIntegerList(jsonArray);

        } catch (JSONException e) {
            return new ArrayList<>();
        }
    }


}
