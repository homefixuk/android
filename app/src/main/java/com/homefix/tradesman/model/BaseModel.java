package com.homefix.tradesman.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.samdroid.string.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samuel on 10/5/2016.
 */

@IgnoreExtraProperties
public class BaseModel {

    private String id;

    public BaseModel() {
    }

    public BaseModel(String id) {
        this.id = id;
    }

    public String getId() {
        return Strings.returnSafely(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", getId());
        return map;
    }

    @Exclude
    public void addChangesToMap(Map<String, Object> map) {
        if (map == null) map = new HashMap<>();
    }

}
