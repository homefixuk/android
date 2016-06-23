package com.samdroid.common;

import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;

/**
 * Created by samuel on 3/16/2016.
 */
public class DeviceInfoUtils {

    /**
     * @param context
     * @return the device hardware ID
     */
    public static String getDeviceID(Context context) {
        if (context == null) return "";

        return Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
