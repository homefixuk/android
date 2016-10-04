package com.homefix.tradesman.firebase.callback;

/**
 * Created by samuel on 2/19/2016.
 */
public interface SaveCallback<T> {

    public void onSave(boolean success, T t);

}
