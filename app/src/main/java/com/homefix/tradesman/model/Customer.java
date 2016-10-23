package com.homefix.tradesman.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.samdroid.string.Strings;

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

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("priority", getPriority());
        map.put("preferredTradesman", getPreferredTradesman());
        map.put("customerProperties", getCustomerProperties());
        map.put("invoices", getInvoices());
        map.put("totalSpent", getTotalSpent());
        return map;
    }

    @Override
    public void addChangesToMap(Map<String, Object> map) {
        super.addChangesToMap(map);

        if (Strings.isEmpty(getId())) return;
        String basePath = "/customers/" + getId() + "/";

        map.put(basePath + "id", getId());
        map.put(basePath + "priority", getPriority());

        if (preferredTradesman != null && !preferredTradesman.isEmpty())
            map.put(basePath + "preferredTradesman", preferredTradesman);

        if (customerProperties != null && !customerProperties.isEmpty())
            map.put(basePath + "customerProperties", customerProperties);

        if (invoices != null && !invoices.isEmpty())
            map.put(basePath + "invoices", invoices);

        map.put(basePath + "totalSpent", getTotalSpent());
    }

}
