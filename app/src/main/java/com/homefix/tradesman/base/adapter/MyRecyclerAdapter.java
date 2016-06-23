package com.homefix.tradesman.base.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 1/25/2016.
 */
abstract class MyRecyclerAdapter<T extends Object, U extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<U> {

    protected String TAG;

    protected Context context;

    final protected List<T> mList;

    protected boolean allowedDuplicateItems = true;

    public MyRecyclerAdapter(@NonNull Context context, @NonNull String TAG) {
        this.context = context;
        this.TAG = TAG;

        mList = new ArrayList<>();
    }

    public MyRecyclerAdapter(@NonNull Context context, @NonNull String TAG, List<T> objects) {
        this(context, TAG);

        if (objects != null) mList.addAll(objects);
    }

    protected Context getContext() {
        return context;
    }

    public void addItem(T po) {
        // make sure not to add duplicates if not allowed to
        if (!allowedDuplicateItems && containsItem(po)) return;

        mList.add(po);
        notifyDataSetChanged();
    }

    public void insertItem(T po, int index) {
        // make sure not to add duplicates if not allowed to
        if (!allowedDuplicateItems && containsItem(po)) return;

        mList.add(index, po);
        notifyItemInserted(index);
    }

    public T removeItem(int position) {
        T t = mList.remove(position);
        notifyItemRemoved(position);
        return t;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public boolean isEmpty() {
        return mList.isEmpty();
    }

    public void setAllowedDuplicateItems(boolean allowedDuplicateItems) {
        this.allowedDuplicateItems = allowedDuplicateItems;
    }

    public boolean getAllowedDuplicateItems() {
        return this.allowedDuplicateItems;
    }

    /**
     * @param po
     * @return if the list contains the item already
     */
    public boolean containsItem(T po) {
        try {
            for (int i = 0, len = getItemCount(); i < len; i++)
                if (po.equals(getItem(i))) return true;

        } catch (Exception e) {
        }

        return false;
    }

}
