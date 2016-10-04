package com.homefix.tradesman.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.samdroid.string.Strings;

import java.util.Map;

/**
 * Created by samuel on 6/15/2016.
 */

@IgnoreExtraProperties
public class Group {

    private String name, picture;
    private Map<String, Boolean> properties, tradesman;
    private Map<String, String> users;

    public String getName() {
        return Strings.returnSafely(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Map<String, Boolean> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Boolean> properties) {
        this.properties = properties;
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public void setUsers(Map<String, String> users) {
        this.users = users;
    }

    public Map<String, Boolean> getTradesman() {
        return tradesman;
    }

    public void setTradesman(Map<String, Boolean> tradesman) {
        this.tradesman = tradesman;
    }
}
