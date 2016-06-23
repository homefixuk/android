package com.samdroid.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;

/**
 * Created by samuel on 3/16/2016.
 */
public class OnRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    public interface ScrollCallbacks {

        void onScrollingUp();

        void onScrollingDown();

        void onScrollingStopped();

        void onScrollingStarted();

        void onScrolledToBottom();

        void onScrolledToTop();

    }

    protected ScrollCallbacks scrollCallbacks;
    LinearLayoutManager mLayoutManager;

    public OnRecyclerViewScrollListener(LinearLayoutManager layoutManager, ScrollCallbacks scrollCallbacks) {
        this.scrollCallbacks = scrollCallbacks;
        mLayoutManager = layoutManager;
    }

    @Override
    public void onScrollStateChanged(RecyclerView view, int scrollState) {
        if (scrollCallbacks == null) return;

        switch (scrollState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                scrollCallbacks.onScrollingStopped();
                break;

            case RecyclerView.SCROLL_STATE_DRAGGING:
                scrollCallbacks.onScrollingStarted();
                break;

        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (scrollCallbacks == null) return;

//        visibleItemCount = recyclerView.getChildCount();
//        totalItemCount = mLayoutManager.getItemCount();
//        firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
//
//        if (loading) {
//            if (totalItemCount > previousTotal) {
//                loading = false;
//                previousTotal = totalItemCount;
//            }
//        }
//
//        if (!loading && (totalItemCount - visibleItemCount)
//                <= (firstVisibleItem + visibleThreshold)) {
//            // end has been reached
//            scrollCallbacks.onScrolledToBottom();
//            loading = true;
//        }

        if (!recyclerView.canScrollVertically(-1)) scrollCallbacks.onScrolledToTop();
        else if (!recyclerView.canScrollVertically(1))
            scrollCallbacks.onScrolledToBottom();
        else if (dy < 0) scrollCallbacks.onScrollingUp();
        else if (dy > 0) scrollCallbacks.onScrollingDown();
    }

}
