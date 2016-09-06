package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class TradesmanReview extends BaseModel {

    private Tradesman tradesman;
    private User user;
    private double rating;
    private String review;

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
        return Strings.returnSafely(review);
    }
}
