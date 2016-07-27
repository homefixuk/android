package com.homefix.tradesman.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 7/27/2016.
 */

public class ServiceStatusFlow {

    String status;
    List<ServiceStatusFlow> values;

    public ServiceStatusFlow() {
    }

    public String getStatus() {
        return status;
    }

    public List<ServiceStatusFlow> getValues() {
        if (values == null) values = new ArrayList<>();

        return values;
    }

}
