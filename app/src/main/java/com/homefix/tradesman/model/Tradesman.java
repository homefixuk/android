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
    private double rating, experience, standard_hourly_rate;
    private List<String> work_areas;
    private Location current_location;
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

    public List<String> getWork_areas() {
        if (work_areas == null) work_areas = new ArrayList<>();

        return work_areas;
    }

    public Location getCurrent_location() {
        return current_location;
    }

    public Map<String, Object> getSettings() {
        if (settings == null) settings = new HashMap<>();

        return settings;
    }

    public double getStandard_hourly_rate() {
        return standard_hourly_rate;
    }

    public void setStandard_hourly_rate(double standard_hourly_rate) {
        this.standard_hourly_rate = standard_hourly_rate;
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

    public void setWork_areas(List<String> work_areas) {
        this.work_areas = work_areas;
    }

    public void setCurrent_location(Location current_location) {
        this.current_location = current_location;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }
}
