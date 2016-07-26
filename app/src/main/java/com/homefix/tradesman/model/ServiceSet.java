package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class ServiceSet {

    CustomerProperty customer_property;
    long created_at, resolved_at;
    String customer_description;
    int number_services;
    double total_cost;
    long total_work_time;

    public CustomerProperty getCustomer_property() {
        return customer_property;
    }

    public long getCreated_at() {
        return created_at;
    }

    public long getResolved_at() {
        return resolved_at;
    }

    public String getCustomer_description() {
        return Strings.returnSafely(customer_description);
    }

    public int getNumber_services() {
        return number_services;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    public long getTotal_work_time() {
        return total_work_time;
    }
}
