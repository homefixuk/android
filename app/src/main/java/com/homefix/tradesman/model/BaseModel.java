package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 9/6/2016.
 */

public class BaseModel {

    private String _id;

    public BaseModel(String _id) {
        this._id = _id;
    }

    public BaseModel() {
    }

    public String getId() {
        return Strings.returnSafely(_id);
    }

    public void setId(String id) {
        this._id = id;
    }
}
