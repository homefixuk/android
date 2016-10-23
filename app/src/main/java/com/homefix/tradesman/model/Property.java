package com.homefix.tradesman.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.samdroid.string.Strings;

import java.util.Map;

/**
 * Created by samuel on 6/15/2016.
 */

@IgnoreExtraProperties
public class Property extends BaseModel {

    private String groupId, addressLine1, addressLine2, addressLine3, country, postcode, phone;
    private int numberTennants, numberBedrooms;
    private double latitude, longitude;
    private Map<String, Boolean> customerProperties;

    public Property() {
    }

    public String getGroupId() {
        return Strings.returnSafely(groupId);
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public int getNumberTennants() {
        return numberTennants;
    }

    public int getNumberBedrooms() {
        return numberBedrooms;
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

    public void setGroup(String groupId) {
        this.groupId = groupId;
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

    public void setNumberTennants(int numberTennants) {
        this.numberTennants = numberTennants;
    }

    public void setNumberBedrooms(int numberBedrooms) {
        this.numberBedrooms = numberBedrooms;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Map<String, Boolean> getCustomerProperties() {
        return customerProperties;
    }

    public void setCustomerProperties(Map<String, Boolean> customerProperties) {
        this.customerProperties = customerProperties;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("groupId", getGroupId());
        map.put("addressLine1", getAddressLine1());
        map.put("addressLine2", getAddressLine2());
        map.put("addressLine3", getAddressLine3());
        map.put("country", getCountry());
        map.put("postcode", getPostcode());
        map.put("phone", getPhone());
        map.put("numberTennants", getNumberTennants());
        map.put("numberBedrooms", getNumberBedrooms());
        map.put("latitude", getLatitude());
        map.put("longitude", getLongitude());
        map.put("customerProperties", getCustomerProperties());
        return map;
    }

    @Override
    public void addChangesToMap(Map<String, Object> map) {
        super.addChangesToMap(map);

        if (Strings.isEmpty(getId())) return;
        String basePath = "/properties/" + getId() + "/";

        map.put(basePath + "id", getId());
        map.put(basePath + "groupId", getGroupId());
        map.put(basePath + "addressLine1", getAddressLine1());
        map.put(basePath + "addressLine2", getAddressLine2());
        map.put(basePath + "addressLine3", getAddressLine3());
        map.put(basePath + "country", getCountry());
        map.put(basePath + "postcode", getPostcode());
        map.put(basePath + "phone", getPhone());
        map.put(basePath + "numberTennants", getNumberTennants());
        map.put(basePath + "numberBedrooms", getNumberBedrooms());
        map.put(basePath + "latitude", getLatitude());
        map.put(basePath + "longitude", getLongitude());

        if (customerProperties != null && !customerProperties.isEmpty())
            map.put(basePath + "customerProperties", customerProperties);
    }

}

