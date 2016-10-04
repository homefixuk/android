package com.homefix.tradesman.firebase.callback;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by samuel on 2/19/2016.
 */
public class SaveFirebaseObjectListener<T> implements ValueEventListener {

    T t;
    SaveCallback<T> callback;

    public SaveFirebaseObjectListener(T t, SaveCallback<T> callback) {
        this.t = t;
        this.callback = callback;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (callback == null) return;

        callback.onSave(true, t);
    }

    @Override
    public void onCancelled(DatabaseError error) {
        if (callback == null) return;

        callback.onSave(false, t);
    }

}