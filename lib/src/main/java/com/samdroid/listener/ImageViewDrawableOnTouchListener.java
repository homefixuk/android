package com.samdroid.listener;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class ImageViewDrawableOnTouchListener implements OnTouchListener {

	protected ImageView imgView;
	protected int drawableNormal, drawableTouched;
	
	/**
	 * @param drawableNormal
	 * @param drawableTouched
	 */
	public ImageViewDrawableOnTouchListener (ImageView imgView, int drawableNormal, int drawableTouched) {
		this.imgView = imgView;
		this.drawableNormal = drawableNormal;
		this.drawableTouched = drawableTouched;
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch (View v, MotionEvent event) {
		if (imgView == null) return false;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			imgView.setImageResource(drawableTouched);
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			imgView.setImageResource(drawableNormal);
			break;	

		}

		return false;
	}		
}