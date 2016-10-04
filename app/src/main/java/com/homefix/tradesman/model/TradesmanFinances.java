package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class TradesmanFinances {

    private String tradesmanId;
    private int numberJobs;
    private double hoursWorked, amountEarned;

    public String getTradesman() {
        return tradesmanId;
    }

    public int getNumberJobs() {
        return numberJobs;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public double getAmountEarned() {
        return amountEarned;
    }
}
