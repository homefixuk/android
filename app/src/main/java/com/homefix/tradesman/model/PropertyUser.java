package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class PropertyUser {

    Property property;
    User user;
    String type;
    long from, until;

    public Property getProperty() {
        return property;
    }

    public User getUser() {
        return user;
    }

    public String getType() {
        return type;
    }

    public long getFrom() {
        return from;
    }

    public long getUntil() {
        return until;
    }
}
