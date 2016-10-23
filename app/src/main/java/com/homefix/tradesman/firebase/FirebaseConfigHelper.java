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
        defaultFirebaseConfig.put("set_data_persistence_enabled", true);
        defaultFirebaseConfig.put("needAccountEmails", "[\"info@homefix.co.uk\",\"george@homefix.co.uk\",\"sokratis@homefix.co.uk\"]");
    }

    public static HashMap<String, Object> getDefaultFirebaseConfig() {
        return defaultFirebaseConfig;
    }

    public static String getConfigString(String key) {
        FirebaseRemoteConfig config = HomeFixApplication.getRemoteConfig();
        if (config == null) {
            return defaultFirebaseConfig.containsKey(key) ? (String) defaultFirebaseConfig.get(key) : "";
        }

        return Strings.returnSafely(config.getString(key));
    }

    public static boolean getConfigBoolean(String key) {
        FirebaseRemoteConfig config = HomeFixApplication.getRemoteConfig();
        if (config == null) {
            return defaultFirebaseConfig.containsKey(key) && (boolean) defaultFirebaseConfig.get(key);
        }

        return config.getBoolean(key);
    }

    public static int getConfigInteger(String key) {
        FirebaseRemoteConfig config = HomeFixApplication.getRemoteConfig();
        if (config == null) {
            return defaultFirebaseConfig.containsKey(key) ? (int) defaultFirebaseConfig.get(key) : 0;
        }

        return (int) config.getLong(key);
    }

    public static long getConfigLong(String key) {
        FirebaseRemoteConfig config = HomeFixApplication.getRemoteConfig();
        if (config == null) {
            return defaultFirebaseConfig.containsKey(key) ? (long) defaultFirebaseConfig.get(key) : 0L;
        }

        return config.getLong(key);
    }

    public static double getConfigDouble(String key) {
        FirebaseRemoteConfig config = HomeFixApplication.getRemoteConfig();
        if (config == null) {
            return defaultFirebaseConfig.containsKey(key) ? (double) defaultFirebaseConfig.get(key) : 0.0;
        }

        return config.getDouble(key);
    }

    public static List<String> getConfigStringList(String key) {
        FirebaseRemoteConfig config = HomeFixApplication.getRemoteConfig();
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

    public static List<Integer> getConfigIntegerList(String key) {
        FirebaseRemoteConfig config = HomeFixApplication.getRemoteConfig();
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
