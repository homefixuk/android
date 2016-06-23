package com.samdroid.listener;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ShowViewOnTouchListener implements OnTouchListener {
	
	private View view;
	
	public ShowViewOnTouchListener (@NonNull View view) {
		this.view = view;
	}

	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public boolean onTouch (View v, MotionEvent event) {
		if (view == null) return false;
		
		switch (event.getAction()) {
			
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			// show the view
			view.setVisibility(View.VISIBLE);
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			// hide the view
			view.setVisibility(View.GONE);		
			break;

		}

		return false;
	}		
	
}