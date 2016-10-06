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

    public Activity getActivity() {
        return mActivity;
    }

}