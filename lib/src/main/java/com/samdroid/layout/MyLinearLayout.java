package com.samdroid.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MyLinearLayout extends LinearLayout {

	private ViewTreeObserver.OnPreDrawListener preDrawListener = null;

	private float xFraction = 0, yFraction = 0;

	public MyLinearLayout(Context context) {
		super(context);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public float getXFraction () {
		return this.xFraction;
	}

	public void setXFraction (float fraction) {
		this.xFraction = fraction;

		if (getWidth() == 0) {
			if (preDrawListener == null) {
				setupPreDrawListener();
				getViewTreeObserver().addOnPreDrawListener(preDrawListener);
			}
			return;
		}

		float translationX = getWidth() * fraction;
		setTranslationX(translationX);
	}

	private void setupPreDrawListener() {
		preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
				setXFraction(xFraction);
				setYFraction(yFraction);
				return true;
			}
		};
	}

	public void setYFraction (float fraction) {
		this.yFraction = fraction;

		if (getHeight() == 0) {
			if (preDrawListener == null) {
				setupPreDrawListener();
				getViewTreeObserver().addOnPreDrawListener(preDrawListener);
			}
			return;
		}

		float translationY = getHeight() * fraction;
		setTranslationY(translationY);
	}

	public float getYFraction () {
		return this.yFraction;
	}

}
