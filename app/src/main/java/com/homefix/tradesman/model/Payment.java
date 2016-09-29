package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

import java.util.Map;

/**
 * Created by samuel on 7/27/2016.
 */

public class Payment extends BaseModel {

    private ServiceSet serviceSet;
    private double amount;
    private String type; // cash/cheque/bank_transfer/card/stripe/etc.

    public Payment() {
    }

    public ServiceSet getServiceSet() {
        return serviceSet;
    }

    public void setServiceSet(ServiceSet serviceSet) {
        this.serviceSet = serviceSet;
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

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        if (serviceSet != null && !Strings.isEmpty(serviceSet.getId()))
            map.put("serviceSet", serviceSet.getId());
        map.put("amount", getAmount());
        map.put("type", getType());
        return map;
    }
}
