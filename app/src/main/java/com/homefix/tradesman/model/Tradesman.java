package com.homefix.tradesman.model;

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


    @Exclude
    public ArrayList<String> getWorkAreasList() {
        if (workAreas == null) workAreas = new HashMap<>();
        return new ArrayList<>(workAreas.keySet());
    }

    ////////////////////////
    /////// Static /////////
    ////////////////////////

    private static Tradesman mCurrentTradesman;

    public static synchronized Tradesman getCurrentTradesman() {
        return mCurrentTradesman;
    }

    private static synchronized void setCurrentTradesman(Tradesman tradesman) {
        mCurrentTradesman = tradesman;
    }

    public static void setupCurrentTradesman() {
        DatabaseReference currentTradesmanRef = FirebaseUtils.getCurrentTradesmanRef();
        if (currentTradesmanRef == null) return;

        // add a listener to keep the tradesman up to date
        currentTradesmanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || !dataSnapshot.exists()) return;

                setCurrentTradesman(dataSnapshot.getValue(Tradesman.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


}
