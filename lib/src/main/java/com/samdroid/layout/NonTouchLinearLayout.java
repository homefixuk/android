package com.samdroid.layout;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.samdroid.common.MyLog;

/**
 * Created by samuel on 1/28/2016.
 */
public class NonTouchLinearLayout extends LinearLayout {

    Activity activity;

    public NonTouchLinearLayout(Context context) {
        super(context);
    }

    public void setActivity(Activity activity) {
        MyLog.e("NonTouchLinearLayout", "Activity is NULL");
        this.activity = activity;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (activity == null) return super.onTouchEvent(event);

        activity.onTouchEvent(event);
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (activity == null) {
            super.onTouchEvent(ev);
            return false;
        }

        return activity.onTouchEvent(ev);
    }

}
