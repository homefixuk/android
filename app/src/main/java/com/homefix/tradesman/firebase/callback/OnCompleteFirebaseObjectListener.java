package com.homefix.tradesman.firebase.callback;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by samuel on 2/19/2016.
 */
public class OnCompleteFirebaseObjectListener<T> implements OnCompleteListener<T> {

    SaveCallback<T> callback;

    public OnCompleteFirebaseObjectListener(SaveCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void onComplete(@NonNull Task<T> task) {
        if (callback == null) return;

        if (!task.isSuccessful()) callback.onSave(false, null);

        callback.onSave(true, task.getResult());
    }
}