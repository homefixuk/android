package com.homefix.tradesman.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.samdroid.string.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samuel on 6/15/2016.
 */

@IgnoreExtraProperties
public abstract class User extends BaseModel {

    private String firstName, lastName, email, mobilePhone, role, homePhone;
    private String homeAddressLine1, homeAddressLine2, homeAddressLine3, homePostcode, homeCountry;
    private String billingAddressLine1, billingAddressLine2, billingAddressLine3, billingPostcode, billingCountry;
    private Map<String, Boolean> groups;

    @Exclude
    public String getName() {
        return Strings.combineNames(firstName, lastName);
    }

    @Exclude
    public void setName(String name) {
        if (Strings.isEmpty(name)) return;

        String[] names = name.split(" ");
        if (names.length > 0) {
            firstName = names[0];

            lastName = "";
            for (int i = 1; i < names.length; i++) {
                lastName += names[i];

                if (i < names.length - 1) lastName += " ";
            }
        }
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

    public String getHomePhone() {
        return Strings.returnSafely(homePhone);
    }

    public String getMobilePhone() {
        return Strings.returnSafely(mobilePhone);
    }

    public String getHomeAddressLine1() {
        return Strings.returnSafely(homeAddressLine1);
    }

    public String getHomeAddressLine2() {
        return Strings.returnSafely(homeAddressLine2);
    }

    public String getHomeAddressLine3() {
        return Strings.returnSafely(homeAddressLine3);
    }

    public String getHomePostcode() {
        return Strings.returnSafely(homePostcode);
    }

    public String getHomeCountry() {
        return Strings.returnSafely(homeCountry);
    }

    public String getBillingAddressLine1() {
        return Strings.returnSafely(billingAddressLine1);
    }

    public String getBillingAddressLine2() {
        return Strings.returnSafely(billingAddressLine2);
    }

    public String getBillingAddressLine3() {
        return Strings.returnSafely(billingAddressLine3);
    }

    public String getBillingPostcode() {
        return Strings.returnSafely(billingPostcode);
    }

    public String getBillingCountry() {
        return Strings.returnSafely(billingCountry);
    }

    public String getRole() {
        return Strings.returnSafely(role);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public void setHomeAddressLine1(String homeAddressLine1) {
        this.homeAddressLine1 = homeAddressLine1;
    }

    public void setHomeAddressLine2(String homeAddressLine2) {
        this.homeAddressLine2 = homeAddressLine2;
    }

    public void setHomeAddressLine3(String homeAddressLine3) {
        this.homeAddressLine3 = homeAddressLine3;
    }

    public void setHomePostcode(String homePostcode) {
        this.homePostcode = homePostcode;
    }

    public void setHomeCountry(String homeCountry) {
        this.homeCountry = homeCountry;
    }

    public void setBillingAddressLine1(String billingAddressLine1) {
        this.billingAddressLine1 = billingAddressLine1;
    }

    public void setBillingAddressLine2(String billingAddressLine2) {
        this.billingAddressLine2 = billingAddressLine2;
    }

    public void setBillingAddressLine3(String billingAddressLine3) {
        this.billingAddressLine3 = billingAddressLine3;
    }

    public void setBillingPostcode(String billingPostcode) {
        this.billingPostcode = billingPostcode;
    }

    public void setBillingCountry(String billingCountry) {
        this.billingCountry = billingCountry;
    }

    public Map<String, Boolean> getGroups() {
        if (groups == null) groups = new HashMap<>();
        return groups;
    }

    public void setGroups(Map<String, Boolean> groups) {
        this.groups = groups;
    }

    @Exclude
    public abstract String getPath();

    @Exclude
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("firstName", getFirstName());
        map.put("lastName", getLastName());
        map.put("email", getEmail());
        map.put("mobilePhone", getMobilePhone());
        map.put("role", getRole());
        map.put("homePhone", getHomePhone());
        map.put("homeAddressLine1", getHomeAddressLine1());
        map.put("homeAddressLine2", getHomeAddressLine2());
        map.put("homeAddressLine3", getHomeAddressLine3());
        map.put("homePostcode", getHomePostcode());
        map.put("homeCountry", getHomeCountry());
        map.put("billingAddressLine1", getBillingAddressLine1());
        map.put("billingAddressLine2", getBillingAddressLine2());
        map.put("billingAddressLine3", getBillingAddressLine3());
        map.put("billingPostcode", getBillingPostcode());
        map.put("billingCountry", getBillingCountry());
        map.put("groups", getGroups());
        return map;
    }

    @Exclude
    @Override
    public void addChangesToMap(Map<String, Object> map) {
        super.addChangesToMap(map);
        if (map == null) return;

        String basePath = getPath();
        if (Strings.isEmpty(basePath)) return;

        map.put(basePath + "firstName", getFirstName());
        map.put(basePath + "lastName", getLastName());
        map.put(basePath + "email", getEmail());
        map.put(basePath + "mobilePhone", getMobilePhone());
        map.put(basePath + "homePhone", getHomePhone());
        map.put(basePath + "homeAddressLine1", getHomeAddressLine1());
        map.put(basePath + "homeAddressLine2", getHomeAddressLine2());
        map.put(basePath + "homeAddressLine3", getHomeAddressLine3());
        map.put(basePath + "homePostcode", getHomePostcode());
        map.put(basePath + "homeCountry", getHomeCountry());
        map.put(basePath + "billingAddressLine1", getBillingAddressLine1());
        map.put(basePath + "billingAddressLine2", getBillingAddressLine2());
        map.put(basePath + "billingAddressLine3", getBillingAddressLine3());
        map.put(basePath + "billingPostcode", getBillingPostcode());
        map.put(basePath + "billingCountry", getBillingCountry());

        if (groups != null && !groups.isEmpty()) map.put(basePath + "groups", getGroups());
    }

}
