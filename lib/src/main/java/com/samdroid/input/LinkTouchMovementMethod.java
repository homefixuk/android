package com.samdroid.input;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

public class LinkTouchMovementMethod extends LinkMovementMethod {

	/**
	 * Force there to only ever be 1 instance of this class
	 */
	private LinkTouchMovementMethod () {}

	public static MovementMethod getInstance () {
		return new LinkTouchMovementMethod();
	}

	@Override
	public boolean onTouchEvent (TextView widget, Spannable buffer, MotionEvent event) {
		int action = event.getAction();
	
		if (action == MotionEvent.ACTION_UP 
				|| action == MotionEvent.ACTION_DOWN
				|| action == MotionEvent.ACTION_CANCEL) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			x -= widget.getTotalPaddingLeft();
			y -= widget.getTotalPaddingTop();

			x += widget.getScrollX();
			y += widget.getScrollY();

			Layout layout = widget.getLayout();
			int line = layout.getLineForVertical(y);
			int off = layout.getOffsetForHorizontal(line, x);

			TouchableSpan[] link = buffer.getSpans(off, off, TouchableSpan.class);

			if (link.length > 0) {
				if (action == MotionEvent.ACTION_UP) {
					// call the on touch event with the up action
					link[0].onTouch(widget, event);
					return true;

				} else if (action == MotionEvent.ACTION_DOWN) {
					// call the on touch event with the down action
					link[0].onTouch(widget,event);

					Selection.setSelection(
							buffer,
							buffer.getSpanStart(link[0]),
							buffer.getSpanEnd(link[0]));
					return true;

				} else if (action == MotionEvent.ACTION_CANCEL) {
					// when one span is cancelled, cancel all the spans
					clearAllSpanHighlights(widget, buffer, event);
					return true;
				}

			} else if (action == MotionEvent.ACTION_CANCEL) {
				// else if the action is a cancel, clear all the spans //
				clearAllSpanHighlights(widget, buffer, event);
			}
		}

		try { return super.onTouchEvent(widget, buffer, event);
		} catch (Exception e) { e.printStackTrace(); }
		
		return true;
	}

	/**
	 * Clear all highlight spans over the text view
	 * 
	 * @param widget
	 * @param buffer
	 * @param event
	 */
	protected void clearAllSpanHighlights (TextView widget, Spannable buffer, MotionEvent event) {
		TouchableSpan[] links = buffer.getSpans(0, buffer.toString().length(), TouchableSpan.class);
		for (int i = 0, len = links.length; i < len; i++) links[i].onTouch(widget, event);
	}

}
