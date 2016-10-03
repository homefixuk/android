package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (!Strings.isEmpty(_id)) map.put("_id", _id);
        return map;
    }

    public boolean isEmpty() {
        return Strings.isEmpty(_id);
    }

}
