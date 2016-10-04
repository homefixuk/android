package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class TradesmanNotification {

    private String tradesmanId;
    private String type, title, content;

    public TradesmanNotification() {

    }

    public String getTradesmanId() {
        return tradesmanId;
    }

    public void setTradesmanId(String tradesmanId) {
        this.tradesmanId = tradesmanId;
    }

    public String getType() {
        return Strings.returnSafely(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return Strings.returnSafely(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return Strings.returnSafely(content);
    }

    public void setContent(String content) {
        this.content = content;
    }
}
