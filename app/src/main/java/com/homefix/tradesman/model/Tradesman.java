package com.homefix.tradesman.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samuel on 6/15/2016.
 */

@IgnoreExtraProperties
public class Tradesman extends User {

    private String type, picture;
    private double rating, experience;
    private Map<String, Boolean> workAreas;
    private Map<String, Object> settings;

    public Tradesman() {
        super();
    }

    public String getType() {
        return Strings.returnSafely(type);
    }

    public String getPicture() {
        return Strings.returnSafely(picture);
    }

    public double getRating() {
        return rating;
    }

    public double getExperience() {
        return experience;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setExperience(double experience) {
        this.experience = experience;
    }

    public Map<String, Boolean> getWorkAreas() {
        if (workAreas == null) workAreas = new HashMap<>();
        return workAreas;
    }

    public void setWorkAreas(Map<String, Boolean> workAreas) {
        this.workAreas = workAreas;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    @Override
    public String getPath() {
        if (Strings.isEmpty(getId())) return null;
        return "/tradesman/" + getId() + "/";
    }

    @Exclude
    public ArrayList<String> getWorkAreasList() {
        if (workAreas == null) workAreas = new HashMap<>();
        return new ArrayList<>(workAreas.keySet());
    }

    ////////////////////////
    /////// Static /////////
    ////////////////////////

    @Exclude
    private static Tradesman mCurrentTradesman;

    @Exclude
    public static synchronized Tradesman getCurrentTradesman() {
        return mCurrentTradesman;
    }

    @Exclude
    private static synchronized void setCurrentTradesman(Tradesman tradesman) {
        mCurrentTradesman = tradesman;
    }

    @Exclude
    public static void onLogout() {
        setCurrentTradesman(null);
        getCurrentTradesmanListeners().clear();
    }

    @Exclude
    public static void setupCurrentTradesman() {
        DatabaseReference currentTradesmanRef = FirebaseUtils.getCurrentTradesmanRef();
        if (currentTradesmanRef == null) return;

        // add a listener to keep the tradesman up to date
        currentTradesmanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || !dataSnapshot.exists()) return;

                setCurrentTradesman(dataSnapshot.getValue(Tradesman.class));
                notifyCurrentTradesmanListeners();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        currentTradesmanRef.keepSynced(true);
    }

    @Exclude
    private static final List<OnGotObjectListener<Tradesman>> mCurrentTradesmanListeners = new ArrayList<>();

    @Exclude
    public static List<OnGotObjectListener<Tradesman>> getCurrentTradesmanListeners() {
        return mCurrentTradesmanListeners;
    }

    @Exclude
    public static void addCurrentTradesmanListener(@NonNull OnGotObjectListener<Tradesman> listener) {
        getCurrentTradesmanListeners().add(listener);
        listener.onGotThing(getCurrentTradesman());
    }

    @Exclude
    public static void removeCurrentTradesmanListener(@NonNull OnGotObjectListener<Tradesman> listener) {
        getCurrentTradesmanListeners().remove(listener);
    }

    @Exclude
    private static void notifyCurrentTradesmanListeners() {
        List<OnGotObjectListener<Tradesman>> listeners = getCurrentTradesmanListeners();
        for (OnGotObjectListener<Tradesman> listener : listeners) {
            if (listener == null) continue;
            listener.onGotThing(getCurrentTradesman());
        }
    }

}
