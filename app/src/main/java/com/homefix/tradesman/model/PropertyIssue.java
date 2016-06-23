package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class PropertyIssue {

    PropertyUser property_user;
    Property property;
    long created_at, resolved_at;
    String customer_description;

    public PropertyUser getProperty_user() {
        return property_user;
    }

    public Property getProperty() {
        return property;
    }

    public long getCreated_at() {
        return created_at;
    }

    public long getResolved_at() {
        return resolved_at;
    }

    public String getCustomer_description() {
        return customer_description;
    }
}
