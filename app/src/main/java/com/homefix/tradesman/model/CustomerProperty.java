package com.homefix.tradesman.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

@IgnoreExtraProperties
public class CustomerProperty {

    private String propertyId, customerId;
    private String type;
    private long from, until;

    public CustomerProperty() {
        super();
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public void setUntil(long until) {
        this.until = until;
    }

    public String getType() {
        return Strings.returnSafely(type);
    }

    public long getFrom() {
        return from;
    }

    public long getUntil() {
        return until;
    }

}
