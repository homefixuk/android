package com.samdroid.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by samuel on 5/16/2016.
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;
    private boolean showAfterLastItem = true;

    public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
        mVerticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect,
                               View view,
                               RecyclerView parent,
                               RecyclerView.State state) {
        if (showAfterLastItem && parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = mVerticalSpaceHeight;
        }
    }

    /**
     * Defaulted to true
     *
     * @param showAfterLastItem
     */
    public void setShowAfterLastItem(boolean showAfterLastItem) {
        this.showAfterLastItem = showAfterLastItem;
    }
}