package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class Property extends BaseModel {

    private Group group;
    private String addressLine1, addressLine2, addressLine3, country, postcode, phone;
    private int number_tennants, number_bedrooms;
    private double latitude, longitude;

    public Property() {
    }

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

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNumber_tennants(int number_tennants) {
        this.number_tennants = number_tennants;
    }

    public void setNumber_bedrooms(int number_bedrooms) {
        this.number_bedrooms = number_bedrooms;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
