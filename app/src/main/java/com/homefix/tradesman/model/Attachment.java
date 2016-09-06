package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class Attachment extends BaseModel {

    Service service;
    String type, text, file;

    public Service getService() {
        return service;
    }

    public String getType() {
        return Strings.returnSafely(type);
    }

    public String getText() {
        return Strings.returnSafely(text);
    }

    public String getFile() {
        return Strings.returnSafely(file);
    }
}
