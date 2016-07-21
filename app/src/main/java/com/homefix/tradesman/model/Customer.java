package com.homefix.tradesman.model;

import java.util.List;

/**
 * Created by samuel on 6/15/2016.
 */

public class Customer extends User {

    String priority;
    List<Tradesman> preferredTradesman;

    public Customer() {
        super();
    }

    public String getPriority() {
        return priority;
    }

    public List<Tradesman> getPreferredTradesman() {
        return preferredTradesman;
    }
}
