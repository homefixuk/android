package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class Attachment {

    Service service;
    String type, text, file;

    public Service getService() {
        return service;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getFile() {
        return file;
    }
}
