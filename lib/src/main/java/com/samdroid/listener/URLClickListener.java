package com.samdroid.listener;

import android.app.Activity;
import android.view.View;

/**
 * Created by samuel on 1/30/2016.
 */
public class URLClickListener extends GoToURLListener implements View.OnClickListener {

    public URLClickListener(Activity activity, String url) {
        super(activity, url);
    }

    @Override
    public void onClick(View v) {
        goToUrl();
    }

}
