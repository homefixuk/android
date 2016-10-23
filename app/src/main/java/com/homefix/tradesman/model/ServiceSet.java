package com.homefix.tradesman.model;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.homefix.tradesman.common.SendReceiver;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.samdroid.common.MyLog;
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

        // only add them if we have some
        Map<String, Payment> payments = getPayments();
        if (payments.size() > 0) map.put("payments", payments);

        // only add them if we have some
        Map<String, Charge> charges = getCharges();
        if (charges.size() > 0) map.put("charges", charges);

        // only add them if we have some
        Map<String, Object> services = getServices();
        if (services.size() > 0) map.put("services", services);

        return map;
    }

    @Override
    public void addChangesToMap(Map<String, Object> map) {
        super.addChangesToMap(map);

        if (Strings.isEmpty(getId())) return;
        String basePath = "/serviceSets/" + getId() + "/";

        map.put(basePath + "id", getId());
        map.put(basePath + "customerPropertyId", getCustomerPropertyId());
        map.put(basePath + "createdAt", getCreatedAt());
        map.put(basePath + "resolvedAt", getResolvedAt());
        map.put(basePath + "customerDescription", getCustomerDescription());
        map.put(basePath + "numberServices", getNumberServices());

        if (totalCost > 0) map.put(basePath + "totalCost", totalCost);
        if (amountPaid > 0) map.put(basePath + "amountPaid", amountPaid);

        // only add them if we have some
        if (payments != null && !payments.isEmpty()) map.put(basePath + "payments", payments);

        // only add them if we have some
        if (charges != null && !charges.isEmpty()) map.put(basePath + "charges", charges);

        // only add them if we have some
        if (services != null && !services.isEmpty()) map.put(basePath + "services", services);
    }

    @Exclude
    public void update() {
        if (Strings.isEmpty(getId())) return;

        // sum up the total cost from the charges
        Map<String, Charge> cs = getCharges();
        Set<String> keys = cs.keySet();
        double totalCharges = 0;
        for (String key : keys) {
            Charge c = cs.get(key);
            if (c == null) continue;
            totalCharges += c.getTotalCost();
        }
        this.totalCost = totalCharges;

        Map<String, Payment> ps = getPayments();
        keys = ps.keySet();
        double totalPayments = 0;
        for (String key : keys) {
            Payment p = ps.get(key);
            if (p == null) continue;
            totalPayments += p.getAmount();
        }
        this.amountPaid = totalPayments;

        Map<String, Object> changes = new HashMap<>();
        changes.put("totalCost", totalCost);
        changes.put("amountPaid", amountPaid);

        // update the service set with the total cost of the charges
        DatabaseReference ref = FirebaseUtils.getSpecificServiceSetRef(getId());
        if (ref != null) {
            ref.updateChildren(changes, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null)
                        MyLog.e(ServiceSet.class.getSimpleName(), "Error updating service set: " + databaseError.getMessage());
                    else MyLog.e(ServiceSet.class.getSimpleName(), "Updated service set");
                }
            });
        }
    }
}
