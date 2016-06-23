package com.homefix.tradesman.base.adapter;

import android.app.Activity;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by samuel on 1/25/2016.
 */
public abstract class MyListAdapter<T> extends ArrayAdapter<T> {

    protected String TAG = MyListAdapter.class.getSimpleName();

    private Activity activity;

    private boolean allowedDuplicateItems = true;

    public MyListAdapter(Activity activity, String TAG, int layoutId) {
        super(activity, layoutId);
        this.activity = activity;
        this.TAG = TAG;
    }

    public MyListAdapter(Activity activity, String TAG, int layoutId, List<T> objects) {
        this(activity, TAG, layoutId);
        addAll(objects);
        notifyDataSetChanged();
    }

//    @Override
//    public void add(T object) {
//        if (object == null || (!allowedDuplicateItems && containsItem(object))) return;
//
//        super.add(object);
//    }
//
//    @Override
//    public void insert(T object, int index) {
//        if (object == null || (!allowedDuplicateItems && containsItem(object))) return;
//
//        super.insert(object, index);
//    }
//
//    @Override
//    public void addAll(T... items) {
//        if (items == null) return;
//
//        List<T> newObjects = new ArrayList<>();
//
//        for (T item : items) {
//            if (!allowedDuplicateItems && containsItem(item)) continue;
//
//            newObjects.add(item);
//        }
//
//        super.addAll(newObjects);
//    }
//
//    @Override
//    public void addAll(Collection<? extends T> collection) {
//        if (collection == null) return;
//
//        List<T> newObjects = new ArrayList<>();
//
//        Iterator<? extends T> iterator = collection.iterator();
//        T t;
//        while (iterator.hasNext()) {
//            t = iterator.next();
//            if (!allowedDuplicateItems && containsItem(t)) continue;
//
//            newObjects.add(t);
//        }
//
//        super.addAll(newObjects);
//    }

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
            for (int i = 0, len = getCount(); i < len; i++)
                if (po.equals(getItem(i))) return true;

        } catch (Exception e) {
        }

        return false;
    }

    public Activity getActivity() {
        return activity;
    }
}
