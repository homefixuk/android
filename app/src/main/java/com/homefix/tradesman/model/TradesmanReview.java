package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class TradesmanReview {

    Tradesman tradesman;
    User user;
    double rating;
    String review;

    public Tradesman getTradesman() {
        return tradesman;
    }

    public User getUser() {
        return user;
    }

    public double getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }
}
