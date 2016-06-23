package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class Activity {

    Service service;
    String type;
    long latitude, longitude;
    Attachment attachment;

    public Service getService() {
        return service;
    }

    public String getType() {
        return type;
    }

    public long getLatitude() {
        return latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public Attachment getAttachment() {
        return attachment;
    }
}
