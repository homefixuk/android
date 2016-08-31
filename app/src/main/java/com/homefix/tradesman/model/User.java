package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class User {

    String id, firstName, lastName, email, mobile, role;
    String shippingNumberOrName, shippingStreet, shippingCity, shippingState, shippingPostalCode, shippingCountry;
    String billingNameOrNumber, billingStreet, billingCity, billingState, billingPostalCode, billingCountry;

    public String getId() {
        return Strings.returnSafely(id);
    }

    public String getName() {
        return Strings.combineNames(firstName, lastName);
    }

    public String getFirstName() {
        return Strings.returnSafely(firstName);
    }

    public String getLastName() {
        return Strings.returnSafely(lastName);
    }

    public String getEmail() {
        return Strings.returnSafely(email);
    }

    public String getMobile() {
        return Strings.returnSafely(mobile);
    }

    public String getShippingNumberOrName() {
        return Strings.returnSafely(shippingNumberOrName);
    }

    public String getShippingStreet() {
        return Strings.returnSafely(shippingStreet);
    }

    public String getShippingCity() {
        return Strings.returnSafely(shippingCity);
    }

    public String getShippingState() {
        return Strings.returnSafely(shippingState);
    }

    public String getShippingPostalCode() {
        return Strings.returnSafely(shippingPostalCode);
    }

    public String getShippingCountry() {
        return Strings.returnSafely(shippingCountry);
    }

    public String getBillingNameOrNumber() {
        return Strings.returnSafely(billingNameOrNumber);
    }

    public String getBillingStreet() {
        return Strings.returnSafely(billingStreet);
    }

    public String getBillingCity() {
        return Strings.returnSafely(billingCity);
    }

    public String getBillingState() {
        return Strings.returnSafely(billingState);
    }

    public String getBillingPostalCode() {
        return Strings.returnSafely(billingPostalCode);
    }

    public String getBillingCountry() {
        return Strings.returnSafely(billingCountry);
    }

    public String getRole() {
        return Strings.returnSafely(role);
    }
}
