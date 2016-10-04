package com.homefix.tradesman.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 7/27/2016.
 */

@IgnoreExtraProperties
public class TradesmanPrivate {

    private String tradesmanId;
    private String accountName, accountNumber, sortCode, nameOnAccount, vatNumber, businessName;
    private double standardHourlyRate;

    public TradesmanPrivate() {
    }

    public String getTradesmanId() {
        return Strings.returnSafely(tradesmanId);
    }

    public void setTradesmanId(String tradesmanId) {
        this.tradesmanId = tradesmanId;
    }

    public String getAccountName() {
        return Strings.returnSafely(accountName);
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return Strings.returnSafely(accountNumber);
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSortCode() {
        return Strings.returnSafely(sortCode);
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getVatNumber() {
        return Strings.returnSafely(vatNumber);
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getBusinessName() {
        return Strings.returnSafely(businessName);
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public double getStandardHourlyRate() {
        return standardHourlyRate;
    }

    public void setStandardHourlyRate(double standardHourlyRate) {
        this.standardHourlyRate = standardHourlyRate;
    }

    public String getNameOnAccount() {
        return nameOnAccount;
    }

    public void setNameOnAccount(String nameOnAccount) {
        this.nameOnAccount = nameOnAccount;
    }
}
