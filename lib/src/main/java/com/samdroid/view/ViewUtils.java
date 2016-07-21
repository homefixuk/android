package com.samdroid.view;

import android.content.Context;
import android.os.Handler;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.PasswordTransformationMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.samdroid.listener.OnFinishCallback;
import com.samdroid.string.Strings;

public class ViewUtils {

    /**
     * Set the adapter for a list view, but set the height of the list view
     * to fit all the items in the list, disabling the scrolling.
     *
     * @param listView
     * @param adapter
     */
    public static void setListAdapterFullHeight(
            ListView listView,
            @SuppressWarnings("rawtypes") ArrayAdapter adapter) {
        setListAdapterFullHeightExtraHeight(listView, adapter, 0);
    }

    /**
     * Set the adapter for a list view, but set the height of the list view
     * to fit all the items in the list, disabling the scrolling.
     *
     * @param listView
     * @param adapter
     * @param extraHeight the extra height to add to the list view
     */
    public static void setListAdapterFullHeightExtraHeight(
            ListView listView,
            @SuppressWarnings("rawtypes") ArrayAdapter adapter,
            int extraHeight) {

        // calculate the height for the list with all items
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        // get the layout parameters
        LayoutParams lp = (LayoutParams) listView.getLayoutParams();

        // set the height for the list
        lp.height = totalHeight + extraHeight;
        listView.setLayoutParams(lp);

        // set the adapter to the list
        listView.setAdapter(adapter);
    }

    /**
     * @param listView
     * @param adapter
     * @return the total height a list view is going to take up with the current number of items in the adapter
     */
    public static int getListAdapterFullHeight(
            ListView listView,
            @SuppressWarnings("rawtypes") ArrayAdapter adapter) {

        // calculate the height for the list with all items
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        return totalHeight;
    }

    /**
     * Set the user profiles badges list view height based on its children
     *
     * @param listView
     */
    public static void setBadgesListViewHeight(
            ListView listView,
            @SuppressWarnings("rawtypes") ArrayAdapter adapter,
            int itemExtraHeight) {

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        // show all the items
        int numberToShow = adapter.getCount();

        for (int i = 0; i < numberToShow; i++) {
            view = adapter.getView(i, null, listView);
            if (i == 0)
                view.setLayoutParams(new LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight() + itemExtraHeight;
        }

        LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /**
     * Set the newsfeed posts list view to be the height needed to show all the items in the adapter for it
     *
     * @param listView
     */
    public static void setLatestPostsListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        // show all the items
        int numberToShow = listAdapter.getCount();

        for (int i = 0; i < numberToShow; i++) {
            view = listAdapter.getView(i, null, listView);
            if (i == 0)
                view.setLayoutParams(new LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += 15 + view.getMeasuredHeight();
        }
        LayoutParams params = listView.getLayoutParams();
        params.height = 10 + totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void stripUnderlines(TextView textView) {
        if (textView == null) return;

        String str = textView.getText().toString();
        Spannable.Factory sFactory = Spannable.Factory.getInstance();
        Spannable s = sFactory.newSpannable(str);

        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);

        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }

        textView.setText(s);
    }

    public static class URLSpanNoUnderline extends URLSpan {

        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    /**
     * Show a Toast for a specific length of time
     *
     * @param context
     * @param message
     * @param milliseconds
     */
    public static void showToast(
            Context context,
            String message,
            long milliseconds,
            final OnFinishCallback onFinishCallback) {
        final Toast toast = Toast.makeText(
                context,
                Strings.returnSafely(message),
                Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (toast != null) toast.cancel();

                if (onFinishCallback != null) onFinishCallback.onFinish();
            }

        }, milliseconds);
    }

    public static void setEditTextEditable(EditText editText, boolean isEditable) {
        if (editText == null) return;

        if (!isEditable) {
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.setClickable(false);

        } else {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setClickable(true);
        }
    }

}
