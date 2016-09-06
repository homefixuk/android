package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class TradesmanLocation extends Location {

    private Tradesman tradesman;
    private boolean isGoingToJob;
    private long timestmap;

    public Tradesman getTradesman() {
        return tradesman;
    }

    public boolean isGoingToJob() {
        return isGoingToJob;
    }

    public long getTimestmap() {
        return timestmap;
    }
}
