package com.samdroid.listener;

import com.samdroid.listener.interfaces.OnFinishListener;

/**
 * Created by samuel on 1/28/2016.
 */
public class OnFinishCallback {

    protected OnFinishListener listener;
    protected int requestId;

    public OnFinishCallback(int requestId, OnFinishListener listener) {
        this.requestId = requestId;
        this.listener = listener;
    }

    public void onFinish() {
        if (listener != null) listener.onThingFinished(requestId);
    }

}
