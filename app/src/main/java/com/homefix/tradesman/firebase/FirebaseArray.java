/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.homefix.tradesman.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements an array-like collection on top of a Firebase location.
 */
public class FirebaseArray<O> implements ChildEventListener {

    public interface OnChangedListener {
        enum EventType {Added, Changed, Removed, Moved}

        void onChanged(EventType type, int index, int oldIndex);
    }

    private Class<O> mClass;
    private Query mQuery;
    private ArrayList<OnChangedListener> mListeners;
    private ArrayList<DataSnapshot> mSnapshots;

    public FirebaseArray(Class<O> clazz, Query ref) {
        mClass = clazz;
        mQuery = ref;
        mSnapshots = new ArrayList<>();
        mListeners = new ArrayList<>();
        mQuery.addChildEventListener(this);
    }

    public void cleanup() {
        mQuery.removeEventListener(this);
    }

    public int getCount() {
        return mSnapshots.size();
    }

    public boolean isEmpty() {
        return mSnapshots.isEmpty();
    }

    public DataSnapshot getSnapshot(int index) {
        return mSnapshots.get(index);
    }

    public O getItem(int position) {
        return getObjectFromSnapshot(getSnapshot(position));
    }

    private O getObjectFromSnapshot(DataSnapshot snapshot) {
        try {
            return snapshot.getValue(mClass);
        } catch (Exception e) {
        }

        return null;
    }

    /**
     * @param key
     * @return the object from the key
     */
    public O getItem(String key) {
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) return getObjectFromSnapshot(snapshot);
        }

        return null;
    }

    /**
     * @return get all the items
     */
    public List<O> getAllItems() {
        List<O> list = new ArrayList<>();
        for (int i = 0, len = getCount(); i < len; i++) {
            list.add(getItem(i));
        }
        return list;
    }

    private int getIndexForKey(String key) {
        int index = 0;
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }

    public List<String> getKeys() {
        if (mSnapshots == null || mSnapshots.isEmpty()) return new ArrayList<>();

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < mSnapshots.size(); i++) {
            keys.add(mSnapshots.get(i).getKey());
        }

        return keys;
    }

    // Start of ChildEventListener methods
    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
        int index = 0;
        if (previousChildKey != null) {
            index = getIndexForKey(previousChildKey) + 1;
        }
        mSnapshots.add(index, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.Added, index);
    }

    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.set(index, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.Changed, index);
    }

    public void onChildRemoved(DataSnapshot snapshot) {
        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(index);
        notifyChangedListeners(OnChangedListener.EventType.Removed, index);
    }

    public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
        int oldIndex = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(oldIndex);
        int newIndex = previousChildKey == null ? 0 : (getIndexForKey(previousChildKey) + 1);
        mSnapshots.add(newIndex, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.Moved, newIndex, oldIndex);
    }

    public void onCancelled(DatabaseError firebaseError) {
        // TODO: what do we do with this?
    }
    // End of ChildEventListener methods

    public void addOnChangedListener(OnChangedListener listener) {
        if (listener == null) return;

        mListeners.add(listener);
    }

    public boolean removeOnChangeListener(OnChangedListener listener) {
        if (listener == null) return false;

        return mListeners.remove(listener);
    }

    protected void notifyChangedListeners(OnChangedListener.EventType type, int index) {
        notifyChangedListeners(type, index, -1);
    }

    protected void notifyChangedListeners(OnChangedListener.EventType type, int index, int oldIndex) {
        for (OnChangedListener listener : mListeners) {
            if (listener == null) continue;

            listener.onChanged(type, index, oldIndex);
        }
    }
}
