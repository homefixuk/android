package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class User {

    String id, first_name, last_name, email, mobile, role;
    String shipping_number_or_name, shipping_street, shipping_city, shipping_state, shipping_postal_code, shipping_country;
    String billing_name_or_number, billing_street, billing_city, billing_state, billing_postal_code, billing_country;

    public String getId() {
        return Strings.returnSafely(id);
    }

    public String getName() {
        return Strings.combineNames(first_name, last_name);
    }

    public String getFirst_name() {
        return Strings.returnSafely(first_name);
    }

    public String getLast_name() {
        return Strings.returnSafely(last_name);
    }

    public String getEmail() {
        return Strings.returnSafely(email);
    }

    public String getMobile() {
        return Strings.returnSafely(mobile);
    }

    public String getShipping_number_or_name() {
        return Strings.returnSafely(shipping_number_or_name);
    }

    public String getShipping_street() {
        return Strings.returnSafely(shipping_street);
    }

    public String getShipping_city() {
        return Strings.returnSafely(shipping_city);
    }

    public String getShipping_state() {
        return Strings.returnSafely(shipping_state);
    }

    public String getShipping_postal_code() {
        return Strings.returnSafely(shipping_postal_code);
    }

    public String getShipping_country() {
        return Strings.returnSafely(shipping_country);
    }

    public String getBilling_name_or_number() {
        return Strings.returnSafely(billing_name_or_number);
    }

    public String getBilling_street() {
        return Strings.returnSafely(billing_street);
    }

    public String getBilling_city() {
        return Strings.returnSafely(billing_city);
    }

    public String getBilling_state() {
        return Strings.returnSafely(billing_state);
    }

    public String getBilling_postal_code() {
        return Strings.returnSafely(billing_postal_code);
    }

    public String getBilling_country() {
        return Strings.returnSafely(billing_country);
    }

    public String getRole() {
        return Strings.returnSafely(role);
    }
}
