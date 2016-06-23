package com.samdroid.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.samdroid.R;
import com.samdroid.string.Strings;

/**
 * Custom EditText to use custom font, used as follows:
 * <p>
 * <p>
 * <com.samdroid.view.TypefacedTextView>
 * xmlns:android="http://schemas.android.com/apk/res/android"
 * xmlns:your_namespace="http://schemas.android.com/apk/res/your.package"
 * android:layout_width="match_parent"
 * android:layout_height="wrap_content"
 * android:text="Custom fonts in XML are easy"
 * android:textColor="#FFF"
 * your_namespace:typeface="custom.ttf" />
 * <p>
 * <p>
 * Created by samuel on 1/27/2016.
 */
public class TypefacedTextView extends TextView {

    // apps main/assets folder
    String mFontName;

    public TypefacedTextView(Context context) {
        super(context);

        setFontTypeface(context);
    }

    public TypefacedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public TypefacedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @TargetApi(21)
    public TypefacedTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        // Typeface.createFromAsset doesn't work in the layout editor. Skipping...
        if (isInEditMode() || attrs == null || context == null) {
            return;
        }

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefacedTextView);
        mFontName = styledAttrs.getString(R.styleable.TypefacedTextView_typeface);
        styledAttrs.recycle();

        setFontTypeface(context);
    }

    /**
     * Set typeface for this text view.
     *
     * @param context
     */
    public void setFontTypeface(Context context) {
        if (context == null || Strings.isEmpty(getFontName())) return;

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), getFontName());
        setTypeface(typeface);
    }

    public String getFontName() {
        return mFontName;
    }

}