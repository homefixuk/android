package com.samdroid.listener;

import android.app.Activity;
import android.view.View;

/**
 * Created by samuel on 1/30/2016.
 */
public class URLLongTouchListener extends GoToURLListener implements View.OnLongClickListener {

    public URLLongTouchListener(Activity activity, String url) {
        super(activity, url);
    }

    @Override
    public boolean onLongClick(View v) {
        goToUrl();
        return true;
    }
}
