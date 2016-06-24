package com.homefix.tradesman.model;

import java.util.List;
import java.util.Map;

/**
 * Created by samuel on 6/15/2016.
 */

public class Tradesman extends User {

    String type, picture;
    double rating, experience;
    List<String> work_areas;
    Location current_location;
    Map<String, Object> settings;

    public String getType() {
        return type;
    }

    public String getPicture() {
        return picture;
    }

    public double getRating() {
        return rating;
    }

    public double getExperience() {
        return experience;
    }

    public List<String> getWork_areas() {
        return work_areas;
    }

    public Location getCurrent_location() {
        return current_location;
    }

    public Map<String, Object> getSettings() {
        return settings;
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
