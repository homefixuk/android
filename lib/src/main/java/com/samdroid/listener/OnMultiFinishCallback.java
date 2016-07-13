package com.samdroid.listener;

import com.samdroid.listener.interfaces.OnFinishListener;
import com.samdroid.listener.interfaces.OnGetListListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 1/28/2016.
 */
public class OnMultiFinishCallback implements OnGetListListener {

    private int numFinished = 0, numRequests = 0;
    private OnFinishListener onFinishListener;

    public OnMultiFinishCallback(int numRequests, OnFinishListener onFinishListener) {
        this.numRequests = numRequests;
        this.onFinishListener = onFinishListener;
    }

    @Override
    public void onGetListFinished(List list) {
        numFinished++;

        if (numFinished >= numRequests) onFinishListener.onThingFinished(0);
    }
}
