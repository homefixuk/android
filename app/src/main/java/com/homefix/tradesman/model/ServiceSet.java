package com.homefix.tradesman.model;

import com.google.android.gms.common.api.BooleanResult;
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
public class ServiceSet {

    private String customerPropertyId;
    private long createdAt, resolvedAt;
    private String customerDescription;
    private int numberServices;
    private double totalCost, amountPaid;
    private Map<String, Payment> payments;
    private Map<String, Charge> charges;
    private Map<String, Boolean> services;

    public ServiceSet() {
    }

    public String getCustomerPropertyId() {
        return customerPropertyId;
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

    public Map<String, Boolean> getServices() {
        return services;
    }

    public void setServices(Map<String, Boolean> services) {
        this.services = services;
    }

    public void setCustomerPropertyId(String customerPropertyId) {
        this.customerPropertyId = customerPropertyId;
    }
}
