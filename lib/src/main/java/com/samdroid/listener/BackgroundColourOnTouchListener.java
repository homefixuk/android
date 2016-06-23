package com.samdroid.listener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class BackgroundColourOnTouchListener implements OnTouchListener {

    protected Context context;
    protected int colNormal, colTouched;

    public BackgroundColourOnTouchListener(
            @NonNull Context context,
            @ColorRes int colNormal,
            @ColorRes int colTouched) {
        this.context = context;
        this.colNormal = colNormal;
        this.colTouched = colTouched;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (context == null) return false;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                v.setBackgroundColor(ContextCompat.getColor(context, colTouched));
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                v.setBackgroundColor(ContextCompat.getColor(context, colNormal));
                break;

        }

        return false;
    }
}