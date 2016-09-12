package com.homefix.tradesman.common;

import android.app.Activity;

/**
 * Created by samuel on 9/12/2016.
 */

public class ActivityHelper {

    public static boolean canActivityDo(Activity activity) {
        return activity != null && !activity.isFinishing() && !activity.isDestroyed();
    }

}
