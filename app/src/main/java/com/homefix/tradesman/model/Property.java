package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class Property {

    Group group;
    String name_or_number, street, county, country, postcode, phone;
    int number_tennants, number_bedrooms;

    public Group getGroup() {
        return group;
    }

    public String getName_or_number() {
        return name_or_number;
    }

    public String getStreet() {
        return street;
    }

    public String getCounty() {
        return county;
    }

    public String getCountry() {
        return country;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getPhone() {
        return phone;
    }

    public int getNumber_tennants() {
        return number_tennants;
    }

    public int getNumber_bedrooms() {
        return number_bedrooms;
    }
}
