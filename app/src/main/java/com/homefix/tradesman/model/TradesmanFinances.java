package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class TradesmanFinances {

    Tradesman tradesman;
    int number_jobs;
    double hours_worked, amount_earned;

    public Tradesman getTradesman() {
        return tradesman;
    }

    public int getNumber_jobs() {
        return number_jobs;
    }

    public double getHours_worked() {
        return hours_worked;
    }

    public double getAmount_earned() {
        return amount_earned;
    }
}
