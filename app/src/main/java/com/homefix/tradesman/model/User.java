package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class User extends BaseModel {

    private String firstName, lastName, email, mobile, role, homePhone;
    private String homeAddressLine1, homeAddressLine2, homeAddressLine3, homePostcode, homeCountry;
    private String billingAddressLine1, billingAddressLine2, billingAddressLine3, billingPostcode, billingCountry;

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

    public String getHomePhone() {
        return Strings.returnSafely(homePhone);
    }

    public String getMobile() {
        return Strings.returnSafely(mobile);
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

    public void setMobile(String mobile) {
        this.mobile = mobile;
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
}
