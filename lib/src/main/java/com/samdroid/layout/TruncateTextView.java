package com.samdroid.layout;

import android.content.Context;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class TruncateTextView extends TextView {

	private int mMaxLines;
	private float mLastLineRatio = 0.5f;
	private float mLastLineWidth;
	private float mAllowLength;

	public TruncateTextView (Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged (int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		mLastLineWidth = getWidth()*mLastLineRatio;
		
		mAllowLength = (mMaxLines-1)*getWidth() + mLastLineWidth;
		
		if (!TextUtils.isEmpty(getText())) {
			truncateText(getText());
		}
	}

	@Override
	public void setText (CharSequence text, BufferType type) {
		super.setText(text, type);
		
		if (mAllowLength == 0) return;
		
		truncateText(text);
	}

	@Override
	public void setMaxLines (int maxlines) {
		super.setMaxLines(maxlines);
		
		mMaxLines = maxlines;
	}

	private void truncateText (CharSequence text){
		float width = Layout.getDesiredWidth(text, getPaint());
		
		int end = text.length();
		
		while (width > mAllowLength) {
			width = Layout.getDesiredWidth(text, 0, end, getPaint());
			end--;
		}
		
		if (end == text.length()) return;
		
		CharSequence processed = text.subSequence(0, end);
		
		setText(processed);
	}
}
