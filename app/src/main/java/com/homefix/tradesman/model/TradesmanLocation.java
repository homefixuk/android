package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class TradesmanLocation {

    private Tradesman tradesman;
    private boolean isGoingToJob;
    private long timestmap;
    private String activity;
    private Location location;

    public Tradesman getTradesman() {
        return tradesman;
    }

    public boolean isGoingToJob() {
        return isGoingToJob;
    }

    public long getTimestmap() {
        return timestmap;
    }

    public void setTradesman(Tradesman tradesman) {
        this.tradesman = tradesman;
    }

    public void setGoingToJob(boolean goingToJob) {
        isGoingToJob = goingToJob;
    }

    public void setTimestmap(long timestmap) {
        this.timestmap = timestmap;
    }

    public String getActivity() {
        return Strings.returnSafely(activity);
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Location getLocation() {
        return location != null ? location : new Location();
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
