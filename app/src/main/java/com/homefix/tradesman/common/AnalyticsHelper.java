package com.homefix.tradesman.common;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 10/10/2016.
 */

public class AnalyticsHelper {

    private static FirebaseAnalytics firebaseAnalytics;

    public static void init(Context applicationContext) {
        if (applicationContext == null) return;

        firebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext);
    }

    public static void setupUser(Context applicationContext) {
        if (applicationContext == null) return;

        String uid = FirebaseUtils.getCurrentTradesmanId();
        if (Strings.isEmpty(uid)) return;

        if (firebaseAnalytics == null) init(applicationContext);
        firebaseAnalytics.setUserId(uid);
    }

    public static void setUserProperty(Context applicationContext, String name, String value) {
        if (applicationContext == null || Strings.isEmpty(name)) return;

        if (firebaseAnalytics == null) init(applicationContext);
        firebaseAnalytics.setUserProperty(name, Strings.returnSafely(value));
    }

    public static void track(Context applicationContext, String eventName, Bundle properties) {
        if (applicationContext == null || Strings.isEmpty(eventName)) return;

        properties = properties != null ? properties : new Bundle();

        if (firebaseAnalytics == null) init(applicationContext);
        firebaseAnalytics.logEvent(eventName, properties);
    }

}
