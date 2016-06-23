package com.samdroid.common;

import android.util.Log;

import com.samdroid.string.Strings;

public class MyLog {

    public static final String TAG = MyLog.class.getSimpleName();

    protected static boolean isLogEnabled = true;

    public static void d(String TAG, String message) {
        d(TAG, message, isLogEnabled);
    }

    public static void e(String TAG, String message) {
        e(TAG, message, isLogEnabled);
    }

    public static void i(String TAG, String message) {
        i(TAG, message, isLogEnabled);
    }

    public static void w(String TAG, String message) {
        w(TAG, message, isLogEnabled);
    }

    public static void Log_v(String TAG, String message) {
        Log_v(TAG, message, isLogEnabled);
    }

    public static void d(String TAG, String message, boolean showLog) {
        if (!showLog || Strings.isEmpty(TAG) || Strings.isEmpty(message)) return;
        Log.d(TAG, message);
    }

    public static void e(String TAG, String message, boolean showLog) {
        if (!showLog || Strings.isEmpty(TAG) || Strings.isEmpty(message)) return;
        Log.e(TAG, message);
    }

    public static void i(String TAG, String message, boolean showLog) {
        if (!showLog || Strings.isEmpty(TAG) || Strings.isEmpty(message)) return;
        Log.i(TAG, message);
    }

    public static void w(String TAG, String message, boolean showLog) {
        if (!showLog || Strings.isEmpty(TAG) || Strings.isEmpty(message)) return;
        Log.w(TAG, message);
    }

    public static void Log_v(String TAG, String message, boolean showLog) {
        if (!showLog || Strings.isEmpty(TAG) || Strings.isEmpty(message)) return;
        Log.v(TAG, message);
    }

    /**
     * Print an exception if debugging is enabled
     *
     * @param e
     */
    public static void printStackTrace(Exception e) {
        if (!isLogEnabled || e == null) return;

        e.printStackTrace();
    }

    /**
     * Print an exception if debugging is enabled
     *
     * @param e
     * @param log
     */
    public static void printStackTrace(Exception e, boolean log) {
        if (!(isLogEnabled && log) || e == null) return;

        e.printStackTrace();
    }

    /**
     * Enable or disable actual logging
     *
     * @param enabled
     */
    public static void setLoggingEnabled(boolean enabled) {
        isLogEnabled = enabled;
    }

    /**
     * @return if debugging is enabled
     */
    public static boolean isIsLogEnabled() {
        return isLogEnabled;
    }

}
