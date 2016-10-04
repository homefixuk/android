package com.homefix.tradesman.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by samuel on 6/15/2016.
 */

public class Location extends HashMap<String, Double> {

    public double getDoubleSafely(String name) {
        try {
            return get(name);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public double getLatitude() {
        return getDoubleSafely("latitude");
    }

    public double getLongitude() {
        return getDoubleSafely("longitude");
    }

}
