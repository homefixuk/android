package com.samdroid.listener;

import android.annotation.SuppressLint;
import android.support.annotation.ColorInt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.samdroid.common.MyLog;

public class BackgroundViewColourOnTouchListener implements OnTouchListener {

    protected View view;
    protected int colNormal, colTouched;

    public BackgroundViewColourOnTouchListener(View view, @ColorInt int colNormal, @ColorInt int colTouched) {
        this.view = view;
        this.colNormal = colNormal;
        this.colTouched = colTouched;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (view == null) {
            MyLog.e("BackgroundViewColourOnTouchListener", "view is null");
            return false;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                view.setBackgroundColor(colTouched);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                view.setBackgroundColor(colNormal);
                break;
        }

        return false;
    }
}