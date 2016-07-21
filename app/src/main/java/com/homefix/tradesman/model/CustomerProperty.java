package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class CustomerProperty {

    Property property;
    Customer customer;
    String type;
    long from, until;

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
        return type;
    }

    public long getFrom() {
        return from;
    }

    public long getUntil() {
        return until;
    }

}