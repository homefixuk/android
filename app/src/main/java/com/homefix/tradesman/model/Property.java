package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class Property {

    Group group;
    String addressLine1, addressLine2, addressLine3, country, postcode, phone;
    int number_tennants, number_bedrooms;
    double latitude, longitude;

    public Group getGroup() {
        return group;
    }

    public String getAddressLine1() {
        return Strings.returnSafely(addressLine1);
    }

    public String getAddressLine2() {
        return Strings.returnSafely(addressLine2);
    }

    public String getAddressLine3() {
        return Strings.returnSafely(addressLine3);
    }

    public String getCountry() {
        return Strings.returnSafely(country);
    }

    public String getPostcode() {
        return Strings.returnSafely(postcode);
    }

    public String getPhone() {
        return Strings.returnSafely(phone);
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
        if (!Strings.isEmpty(addressLine1)) addressLine1 = addressLine1.replace(postcode, "");
        if (!Strings.isEmpty(addressLine2)) addressLine2 = addressLine2.replace(postcode, "");
        if (!Strings.isEmpty(addressLine3)) addressLine3 = addressLine3.replace(postcode, "");

        String s = "";
        s += !Strings.isEmpty(addressLine1) ? addressLine1 + "," : "";
        s += !Strings.isEmpty(addressLine2) ? addressLine2 + ", " : "";
        s += !Strings.isEmpty(addressLine3) ? addressLine3 + ", " : "";
        s += !Strings.isEmpty(postcode) ? postcode + ", " : "";
        s += !Strings.isEmpty(country) ? country : "";

        if (s.endsWith(",")) s = s.substring(0, s.length() - 1);

        return s;
    }

}
