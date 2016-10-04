package com.homefix.tradesman.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 7/27/2016.
 */

@IgnoreExtraProperties
public class Payment {

    private double amount;
    private String type; // cash/cheque/bank_transfer/card/stripe/etc.

    public Payment() {
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return Strings.returnSafely(type);
    }

    public void setType(String type) {
        this.type = type;
    }

}
