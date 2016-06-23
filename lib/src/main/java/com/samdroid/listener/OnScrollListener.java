package com.samdroid.listener;

import android.view.View;
import android.widget.AbsListView;

/**
 * Created by samuel on 3/16/2016.
 */
public class OnScrollListener implements AbsListView.OnScrollListener {

    public interface ScrollCallbacks {

        public void onScrollingUp();

        public void onScrollingDown();

        public void onScrollingStopped();

        public void onScrollingStarted();

        public void onScrollingFling();

        public void onScrolledToBottom();

        public void onScrolledToTop();

    }

    protected int mLastFirstVisibleItem;
    protected boolean mIsScrollingUp;
    protected ScrollCallbacks scrollCallbacks;

    public OnScrollListener(ScrollCallbacks scrollCallbacks) {
        this.scrollCallbacks = scrollCallbacks;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollCallbacks == null) return;

        switch (scrollState) {
            case SCROLL_STATE_IDLE:
                scrollCallbacks.onScrollingStopped();
                break;

            case SCROLL_STATE_TOUCH_SCROLL:
                scrollCallbacks.onScrollingStarted();
                break;

            case SCROLL_STATE_FLING:
                scrollCallbacks.onScrollingFling();
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (scrollCallbacks == null) return;

        // check if scrolled to bottom or top
        if (view.getAdapter().getCount() > 0 && view.getLastVisiblePosition() == view.getAdapter().getCount() - 1) {
            View v = view.getChildAt(view.getChildCount() - 1);
            if (v != null && v.getBottom() <= view.getHeight()) {
                scrollCallbacks.onScrolledToBottom();

            } else if (view.getFirstVisiblePosition() == 0 && view.getChildAt(0).getTop() >= 0) {
                // else if it is scrolled all the way to the top //
                scrollCallbacks.onScrolledToTop();
            }
        }

        int currentFirstVisibleItem = view.getFirstVisiblePosition();

        // if the user is scrolling down
        if (currentFirstVisibleItem > mLastFirstVisibleItem) {
            mIsScrollingUp = false;
            scrollCallbacks.onScrollingDown();

        } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
            // else if the user is scrolling up //
            mIsScrollingUp = true;
            scrollCallbacks.onScrollingUp();
        }

        mLastFirstVisibleItem = currentFirstVisibleItem;
    }
}
