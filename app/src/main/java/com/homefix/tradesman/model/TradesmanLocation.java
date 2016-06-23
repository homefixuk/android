package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class TradesmanLocation extends Location {

    Tradesman tradesman;
    boolean is_going_to_job;
    long timestmap;

    public Tradesman getTradesman() {
        return tradesman;
    }

    public boolean is_going_to_job() {
        return is_going_to_job;
    }

    public long getTimestmap() {
        return timestmap;
    }
}
