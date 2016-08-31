package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samuel on 6/15/2016.
 */

public class Tradesman extends User {

    private String type, picture;
    private double rating, experience, standardHourlyRate;
    private List<String> workAreas;
    private Location currentLocation;
    private Map<String, Object> settings;

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

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public Map<String, Object> getSettings() {
        if (settings == null) settings = new HashMap<>();

        return settings;
    }

    public double getStandardHourlyRate() {
        return standardHourlyRate;
    }

    public void setStandardHourlyRate(double standardHourlyRate) {
        this.standardHourlyRate = standardHourlyRate;
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

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }
}
