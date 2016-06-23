package com.samdroid.input;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public abstract class HighlightTextTouchableSpan extends TouchableSpan {

	protected Context context;
	protected String matchingStr;	
	protected Spannable spannable;
	protected BackgroundColorSpan highlightSpan;
	protected boolean 
	touchDown = false,
	makeBold = false,
	clickingEnabled = true;

	/**
	 * Constructor to highlight the matching string in the text view this is attached to 
	 * when the matching string is touched.
	 * 
	 * @param context
	 * @param matchingStr the string to highlight when touched
	 * @param spannable the original spannable used to set for the text view
	 */
	public HighlightTextTouchableSpan (Context context, String matchingStr, Spannable spannable) {
		this(context, matchingStr, spannable, true); // default to enabled links
	}
	
	/**
	 * Constructor to highlight the matching string in the text view this is attached to 
	 * when the matching string is touched.
	 * 
	 * @param context
	 * @param matchingStr the string to highlight when touched
	 * @param spannable the original spannable used to set for the text view
	 * @param whether to enable the links
	 */
	public HighlightTextTouchableSpan (Context context, String matchingStr, Spannable spannable, boolean clickingEnabled) {
		this(context, matchingStr, spannable, clickingEnabled, false);
	}
	
	/**
	 * Constructor to highlight the matching string in the text view this is attached to 
	 * when the matching string is touched.
	 * 
	 * @param context
	 * @param matchingStr the string to highlight when touched
	 * @param spannable the original spannable used to set for the text view
	 * @param whether to enable the links
	 */
	public HighlightTextTouchableSpan (Context context, String matchingStr, Spannable spannable, boolean clickingEnabled, boolean makeBold) {
		this.matchingStr = matchingStr;
		this.context = context;
		this.spannable = spannable;
		this.clickingEnabled = clickingEnabled;
		this.makeBold = makeBold;

		// make what happens when the matching text is touched span
		highlightSpan = new BackgroundColorSpan(Color.LTGRAY);	
	}

	@Override
	public void updateDrawState (TextPaint ds) {
		ds.setColor(Color.BLACK); // set to the link colour to black 
		ds.setUnderlineText(false); // set to false to remove underline
		
		if (makeBold) ds.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
	}

	@Override
	public boolean onTouch (View widget, MotionEvent m) {
		// if clicks are not enabled, return
		if (!clickingEnabled) return true;
				
		try {
			// get the text view
			TextView tv = (TextView) widget;

			// get the action made on the text view
			int action = m.getAction();

			// if the action was up and was still in the view
			if (action == MotionEvent.ACTION_UP) { 
				// remove the background span on the 
				spannable.removeSpan(highlightSpan);

				// if the touch was down
				if (touchDown) {
					touchDown = false;

					// call the on click function
					onClick(widget);
				}

				// update the text view
				tv.setText(spannable);

				return true;

			} else if (action == MotionEvent.ACTION_DOWN) {
				// else if the action was down //

				// set the touch to be down
				touchDown = true;

				// get the text view text
				String str = tv.getText().toString();

				// get the index of the expand text
				int ofe = str.indexOf(matchingStr, 0);   

				// for each character
				for (int ofs = 0; ofs < str.length() && ofe != -1; ofs = ofe + 1) {  
					ofe = str.indexOf(matchingStr, ofs);   
					if (ofe == -1)
						break;
					else {  
						spannable.setSpan(highlightSpan, ofe, ofe + matchingStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);	
					}
				}

				// update the text view
				tv.setText(spannable);

				return true;

			} else if (action == MotionEvent.ACTION_CANCEL) {
				// else if the action was to cancel the touch/click on the expand text //
				
				// remove the background span on the 
				spannable.removeSpan(highlightSpan);

				// update the text view
				tv.setText(spannable);

				return true;
			} 

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}
	
	public void setBold (boolean makeBold) {
		this.makeBold = makeBold;
	}

}
