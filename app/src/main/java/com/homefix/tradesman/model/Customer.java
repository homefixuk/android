package com.homefix.tradesman.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by samuel on 6/15/2016.
 */

@IgnoreExtraProperties
public class Customer extends User {

    private String priority;
    private List<Tradesman> preferredTradesman;
    private Map<String, Boolean> customerProperties, invoices;
    private double totalSpent;

    public String getPriority() {
        return priority;
    }

    public List<Tradesman> getPreferredTradesman() {
        if (preferredTradesman == null) preferredTradesman = new ArrayList<>();

        return preferredTradesman;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setPreferredTradesman(List<Tradesman> preferredTradesman) {
        this.preferredTradesman = preferredTradesman;
    }

    public Map<String, Boolean> getCustomerProperties() {
        return customerProperties;
    }

    public void setCustomerProperties(Map<String, Boolean> customerProperties) {
        this.customerProperties = customerProperties;
    }

    public Map<String, Boolean> getInvoices() {
        return invoices;
    }

    public void setInvoices(Map<String, Boolean> invoices) {
        this.invoices = invoices;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }
}
