package com.homefix.tradesman.model;

import com.homefix.tradesman.common.SendReceiver;
import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 6/15/2016.
 */

public class ServiceSet {

    CustomerProperty customer_property;
    long created_at, resolved_at;
    String customer_description;
    int number_services;
    double total_cost, amount_paid;
    private List<Payment> payments;
    private List<Charge> charges;

    public ServiceSet() {
    }

    public CustomerProperty getCustomer_property() {
        return customer_property;
    }

    public long getCreated_at() {
        return created_at;
    }

    public long getResolved_at() {
        return resolved_at;
    }

    public String getCustomer_description() {
        return Strings.returnSafely(customer_description);
    }

    public int getNumber_services() {
        return number_services;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    public double getAmount_paid() {
        return amount_paid;
    }

    public double getAmountRemaining() {
        return total_cost - amount_paid;
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
