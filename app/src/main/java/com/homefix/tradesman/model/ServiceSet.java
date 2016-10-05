package com.homefix.tradesman.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.homefix.tradesman.common.SendReceiver;
import com.samdroid.string.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by samuel on 6/15/2016.
 */

@IgnoreExtraProperties
public class ServiceSet extends BaseModel {

    private String customerPropertyId;
    private long createdAt, resolvedAt;
    private String customerDescription;
    private int numberServices;
    private double totalCost, amountPaid;
    private Map<String, Payment> payments;
    private Map<String, Charge> charges;
    private Map<String, Object> services;

    public ServiceSet() {
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setResolvedAt(long resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public void setCustomerDescription(String customerDescription) {
        this.customerDescription = customerDescription;
    }

    public void setNumberServices(int numberServices) {
        this.numberServices = numberServices;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public void setPayments(Map<String, Payment> payments) {
        this.payments = payments;
    }

    public void setCharges(Map<String, Charge> charges) {
        this.charges = charges;
    }

    public String getCustomerPropertyId() {
        return Strings.returnSafely(customerPropertyId);
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getResolvedAt() {
        return resolvedAt;
    }

    public String getCustomerDescription() {
        return Strings.returnSafely(customerDescription);
    }

    public int getNumberServices() {
        return numberServices;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public double getAmountRemaining() {
        return totalCost - amountPaid;
    }

    public Map<String, Payment> getPayments() {
        if (payments == null) payments = new HashMap<>();
        return payments;
    }

    public Map<String, Charge> getCharges() {
        if (charges == null) charges = new HashMap<>();
        return charges;
    }

    @Exclude
    public double getTotalFromCharges() {
        if (charges == null || charges.size() == 0) return 0d;

        double total = 0d;

        Set<String> keys = charges.keySet();
        Charge charge;
        for (String key : keys) {
            charge = charges.get(key);
            if (charge == null) continue;

            total += charge.getTotalCost();
        }

        return total;
    }

    @Exclude
    public double getTotalFromPayments() {
        if (payments == null || payments.size() == 0) return 0d;

        double total = 0d;

        Set<String> keys = payments.keySet();
        Payment payment;
        for (String key : keys) {
            payment = payments.get(key);
            if (payment == null) continue;

            total += payment.getAmount();
        }

        return total;
    }

    private static final SendReceiver<ServiceSet> senderReceiver = new SendReceiver<>(ServiceSet.class);

    public static SendReceiver<ServiceSet> getSenderReceiver() {
        return senderReceiver;
    }

    public Map<String, Object> getServices() {
        if (services == null) services = new HashMap<>();
        return services;
    }

    public void setServices(Map<String, Object> services) {
        this.services = services;
    }

    public void setCustomerPropertyId(String customerPropertyId) {
        this.customerPropertyId = customerPropertyId;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("customerPropertyId", getCustomerPropertyId());
        map.put("createdAt", getCreatedAt());
        map.put("resolvedAt", getResolvedAt());
        map.put("customerDescription", getCustomerDescription());
        map.put("numberServices", getNumberServices());
        map.put("totalCost", getTotalCost());
        map.put("amountPaid", getAmountPaid());
        map.put("payments", getPayments());
        map.put("charges", getCharges());
        map.put("services", getServices());
        return map;
    }

}
