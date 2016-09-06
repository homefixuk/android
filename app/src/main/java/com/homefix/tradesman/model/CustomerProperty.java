package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class CustomerProperty extends BaseModel {

    private Property property;
    private Customer customer;
    private String type;
    private long from, until;

    public CustomerProperty() {
        super();
    }

    public Property getProperty() {
        return property;
    }

    public Customer getCustomer() {
        return customer;
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
