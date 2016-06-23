package com.samdroid.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.samdroid.common.IntentHelper;
import com.samdroid.common.MyLog;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 1/30/2016.
 */
public class GoToURLListener {

    Activity activity;
    String url;

    public GoToURLListener(Activity activity, String url) {
        this.activity = activity;
        this.url = url;
    }

    public void goToUrl() {
        if (activity == null || Strings.isEmpty(url)) {
            MyLog.e("GoToUrlListener", "Context or url is empty");
            return;
        }

        IntentHelper.goToWebURL(activity, url);
    }

}
