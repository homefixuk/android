package com.homefix.tradesman.model;

import com.homefix.tradesman.common.SendReceiver;
import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 6/15/2016.
 */

public class ServiceSet extends BaseModel {

    private CustomerProperty customerProperty;
    private long createdAt, resolvedAt;
    private String customerDescription;
    private int numberServices;
    private double totalCost, amountPaid;
    private List<Payment> payments;
    private List<Charge> charges;

    public ServiceSet() {
    }

    public CustomerProperty getCustomerProperty() {
        return customerProperty;
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

    public List<Payment> getPayments() {
        if (payments == null) payments = new ArrayList<>();

        return payments;
    }

    public List<Charge> getCharges() {
        if (charges == null) charges = new ArrayList<>();

        return charges;
    }

    public double getTotalFromCharges() {
        if (charges == null || charges.size() == 0) return 0d;

        double total = 0d;

        for (Charge charge : charges) {
            if (charge == null) continue;

            total += charge.totalCost();
        }

        return total;
    }

    private static final SendReceiver<ServiceSet> senderReceiver = new SendReceiver<>(ServiceSet.class);

    public static SendReceiver<ServiceSet> getSenderReceiver() {
        return senderReceiver;
    }

}
