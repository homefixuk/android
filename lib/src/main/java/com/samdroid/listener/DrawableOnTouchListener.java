package com.samdroid.listener;

import android.annotation.SuppressLint;
import android.support.annotation.DrawableRes;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class DrawableOnTouchListener implements OnTouchListener {

    protected int drawableNormal, drawableTouched;

    /**
     * @param drawableNormal
     * @param drawableTouched
     */
    public DrawableOnTouchListener(
            @DrawableRes int drawableNormal,
            @DrawableRes int drawableTouched) {
        this.drawableNormal = drawableNormal;
        this.drawableTouched = drawableTouched;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setBackgroundResource(drawableTouched);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                v.setBackgroundResource(drawableNormal);
                break;

        }

        return false;
    }
}