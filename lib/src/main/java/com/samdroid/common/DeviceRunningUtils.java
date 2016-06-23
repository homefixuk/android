package com.samdroid.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samdroid.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;

/**
 * Created by samuel on 12/21/2015.
 */
public class DeviceRunningUtils {

    public static final String TAG = "DeviceRunningUtils";

    /**
     * Get the currently running app from logcat
     */
    public String getCurrentRunningAppFromLog() {
        try {
            Process mLogcatProc = null;
            BufferedReader reader = null;
            mLogcatProc = Runtime.getRuntime().exec(new String[]{"logcat", "-d"});

            reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));

            String line;
            final StringBuilder log = new StringBuilder();
            String separator = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null) {
                log.append(line);
                log.append(separator);
            }

            return log.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private void restartApp(Activity activity, Class c) {
        if (activity == null) return;

        // restart the app with the same intent
        Intent startingIntent = activity.getIntent();
        Intent i = new Intent(activity, c);
        if (startingIntent != null && startingIntent.getExtras() != null) {
            // remove any null extras
            Bundle b = startingIntent.getExtras();
            for (String key : b.keySet()) {
                if (b.get(key) == null) b.remove(key);
            }

            i.putExtras(b);
        }

        activity.startActivity(i);
        activity.finish();
    }

}
