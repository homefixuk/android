package com.samdroid.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * ImageView that keeps aspect ratio when scaled
 */
public class ScaleImageView extends SelectableRoundedImageView {

	public ScaleImageView (Context context) {
		super(context);
	}

	public ScaleImageView (Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScaleImageView (Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
		try {
			Drawable drawable = getDrawable();
			int dWidth = drawable.getIntrinsicWidth();
			int dHeight = drawable.getIntrinsicHeight();

			// if the image is square or portrait
			if (dWidth <= dHeight) {
				// set it to fit the center
				this.setScaleType(ScaleType.FIT_CENTER);
			} else {
				// else set it to crop center
				this.setScaleType(ScaleType.CENTER_CROP);			
			}

			setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

		} catch (Exception e) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

}