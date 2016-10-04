package com.homefix.tradesman.base.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by samuel on 6/5/2016.
 */
public abstract class MyFirebaseRecyclerAdapter<T extends Object, V extends RecyclerView.ViewHolder> extends FirebaseRecyclerAdapter<T, V> {

    protected Activity mActivity;

    protected String TAG = MyFirebaseRecyclerAdapter.class.getSimpleName();

    public static final int TYPE_NORMAL = 0, TYPE_FOOTER = 1;

    protected boolean isShowingFooter = false;

    public MyFirebaseRecyclerAdapter(
            Activity activity,
            Class<T> modelClass,
            Class<V> holderClass,
            int modelLayout,
            Query ref) {
        super(modelClass, modelLayout, holderClass, ref);
        mActivity = activity;
    }

    public MyFirebaseRecyclerAdapter(
            Activity activity,
            Class<T> modelClass,
            Class<V> holderClass,
            int modelLayout,
            DatabaseReference ref) {
        super(modelClass, modelLayout, holderClass, ref);
        mActivity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowingFooter && position == getItemCount() - 1) return TYPE_FOOTER;

        return TYPE_NORMAL;
    }

    public int getViewTypeCount() {
        return 2;
    }

    /**
     * Set if showing the footer
     *
     * @param isShowingFooter
     */
    public void setIsShowingFooter(boolean isShowingFooter) {
        this.isShowingFooter = isShowingFooter;
        notify();
    }

    public Activity getActivity() {
        return mActivity;
    }

}