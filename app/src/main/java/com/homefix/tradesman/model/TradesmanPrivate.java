package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 7/27/2016.
 */

public class TradesmanPrivate {
    private Tradesman tradesman;
    private String accountName, accountNumber, sortCode, vatNumber;

    public TradesmanPrivate() {
    }

    public Tradesman getTradesman() {
        return tradesman;
    }

    public void setTradesman(Tradesman tradesman) {
        this.tradesman = tradesman;
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
}
