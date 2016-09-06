package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class Group extends BaseModel {

    private String name;

    public String getName() {
        return Strings.returnSafely(name);
    }
}
