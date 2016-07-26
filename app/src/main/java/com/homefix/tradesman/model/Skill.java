package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class Skill {

    String name, description;

    public String getName() {
        return Strings.returnSafely(name);
    }

    public String getDescription() {
        return Strings.returnSafely(description);
    }
}
