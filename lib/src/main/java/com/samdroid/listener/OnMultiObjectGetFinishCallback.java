package com.samdroid.listener;

import com.samdroid.listener.interfaces.OnFinishListener;
import com.samdroid.listener.interfaces.OnGetListListener;

import java.util.List;

/**
 * Created by samuel on 1/28/2016.
 */
public class OnMultiObjectGetFinishCallback<O extends Object> implements OnGetListListener<O> {

    private int numFinished = 0, numRequests = 0;
    private OnGetListListener<O> callback;

    public OnMultiObjectGetFinishCallback(int numRequests, OnGetListListener<O> callback) {
        this.numRequests = numRequests;
        this.callback = callback;
    }

    @Override
    public void onGetListFinished(List<O> list) {
        numFinished++;

        if (numFinished >= numRequests) callback.onGetListFinished(list);
    }
}
