package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class TradesmanFinances {

    Tradesman tradesman;
    int numberJobs;
    double hoursWorked, amountEarned;

    public Tradesman getTradesman() {
        return tradesman;
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
