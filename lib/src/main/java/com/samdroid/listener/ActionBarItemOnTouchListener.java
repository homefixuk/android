package com.samdroid.listener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.samdroid.R;

public class ActionBarItemOnTouchListener implements OnTouchListener {

	private Context context;

	public ActionBarItemOnTouchListener(Context context){
		this.context = context;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch (View v, MotionEvent event) {
		if (context == null) return false;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// set its background to the semi-transparent grey
			v.setBackgroundColor(context.getResources().getColor(R.color.grey_20_percent, null));
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			// set its background to transparent
			v.setBackgroundColor(context.getResources().getColor(R.color.transparent, null));
			break;	

		}

		return false;
	}		
}