package com.homefix.tradesman.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

@IgnoreExtraProperties
public class CCA extends User {

    public CCA() {
    }

    @Override
    public String getPath() {
        if (Strings.isEmpty(getId())) return null;
        return "/ccas/" + getId() + "/";
    }

}
