package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class Property {

    Group group;
    String address_line_1, address_line_2, address_line_3, country, postcode, phone;
    int number_tennants, number_bedrooms;
    double latitude, longitude;

    public Group getGroup() {
        return group;
    }

    public String getAddress_line_1() {
        return address_line_1;
    }

    public String getAddress_line_2() {
        return address_line_2;
    }

    public String getAddress_line_3() {
        return address_line_3;
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getReadableAddress() {
        if (!Strings.isEmpty(address_line_1)) address_line_1 = address_line_1.replace(postcode, "");
        if (!Strings.isEmpty(address_line_2)) address_line_2 = address_line_2.replace(postcode, "");
        if (!Strings.isEmpty(address_line_3)) address_line_3 = address_line_3.replace(postcode, "");

        String s = "";
        s += !Strings.isEmpty(address_line_1) ? address_line_1 + "," : "";
        s += !Strings.isEmpty(address_line_2) ? address_line_2 + ", " : "";
        s += !Strings.isEmpty(address_line_3) ? address_line_3 + ", " : "";
        s += !Strings.isEmpty(postcode) ? postcode + ", " : "";
        s += !Strings.isEmpty(country) ? country : "";

        if (s.endsWith(",")) s = s.substring(0, s.length() - 1);

        return s;
    }

}
