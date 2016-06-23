package com.samdroid.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class MyFrameLayout extends FrameLayout {

	private ViewTreeObserver.OnPreDrawListener preDrawListener = null;

	private float xFraction = 0, yFraction = 0;

	public MyFrameLayout (Context context) {
		super(context);
	}

	public MyFrameLayout (Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyFrameLayout (Context context, AttributeSet attrs, int defStyleAttr) {
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
