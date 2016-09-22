package com.homefix.tradesman.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by samuel on 6/15/2016.
 */

public class Location extends JSONObject {

    public double getDoubleSafely(String name) {
        try {
            return getDouble(name);
        } catch (Exception e) {
            return 0;
        }
    }

    public double getLatitude() {
        return getDoubleSafely("latitude");
    }

    public double getLongitude() {
        return getDoubleSafely("longitude");
    }

}
