package com.samdroid.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {
	
	private boolean isPagingEnabled = true;

	public MyViewPager (Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public boolean onTouchEvent (MotionEvent event) {
	    if (isPagingEnabled) return super.onTouchEvent(event);

	    return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
	    if (isPagingEnabled) return super.onInterceptTouchEvent(event);

	    return false;
	}

	/**
	 * Set the paging to be enabled or not
	 * 
	 * @param enabled
	 */
	public void setPagingEnabled (boolean enabled) {
	    isPagingEnabled = enabled;
	}

}
