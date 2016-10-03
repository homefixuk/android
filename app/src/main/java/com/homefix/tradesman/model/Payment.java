package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

import java.util.Map;

/**
 * Created by samuel on 7/27/2016.
 */

public class Payment extends BaseModel {

    private Object serviceSet;
    private double amount;
    private String type; // cash/cheque/bank_transfer/card/stripe/etc.

    public Payment() {
    }

    public Object getServiceSet() {
        return serviceSet;
    }

    public void setServiceSet(Object serviceSet) {
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

        if (serviceSet != null) {
            if (serviceSet instanceof String) {
                String serviceId = (String) serviceSet;
                if (!Strings.isEmpty(serviceId)) map.put("serviceSet", serviceId);

            } else if (serviceSet instanceof ServiceSet) {
                ServiceSet service1 = (ServiceSet) serviceSet;
                if (!Strings.isEmpty(service1.getId())) map.put("serviceSet", service1.getId());

            } else if (serviceSet instanceof Map) {
                Map<String, Object> serviceMap = (Map<String, Object>) serviceSet;
                if (serviceMap.containsKey("_id") && !Strings.isEmpty((String) serviceMap.get("_id")))
                    map.put("serviceSet", serviceMap.get("_id"));
            }
        }

        map.put("amount", getAmount());
        map.put("type", getType());
        return map;
    }
}
