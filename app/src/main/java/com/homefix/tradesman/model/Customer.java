package com.homefix.tradesman.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 6/15/2016.
 */

public class Customer extends BaseModel {

    private User user;
    private String priority;
    private List<Tradesman> preferredTradesman;

    public Customer() {
        super();
    }

    public String getPriority() {
        return priority;
    }

    public List<Tradesman> getPreferredTradesman() {
        if (preferredTradesman == null) preferredTradesman = new ArrayList<>();

        return preferredTradesman;
    }

    public User getUser() {
        return user;
    }
}
