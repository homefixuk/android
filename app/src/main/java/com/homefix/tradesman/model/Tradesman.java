package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samuel on 6/15/2016.
 */

public class Tradesman extends BaseModel {

    private User user;
    private String type, picture;
    private double rating, experience;
    private List<String> workAreas;
    private String settings;
//    private TradesmanLocation currentLocation;

    public Tradesman() {
        super();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<String> getWorkAreas() {
        if (workAreas == null) workAreas = new ArrayList<>();

        return workAreas;
    }

    public String getSettings() {
        return Strings.returnSafely(settings);
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

    public void setWorkAreas(List<String> workAreas) {
        this.workAreas = workAreas;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

//    public TradesmanLocation getCurrentLocation() {
//        return currentLocation;
//    }
//
//    public void setCurrentLocation(TradesmanLocation currentLocation) {
//        this.currentLocation = currentLocation;
//    }
}
