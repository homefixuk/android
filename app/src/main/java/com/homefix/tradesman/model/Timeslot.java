package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class Timeslot {

    long start, end, length;
    String type;
    Tradesman tradesman;
    Service service;

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getLength() {
        return length;
    }

    public String getType() {
        return type;
    }

    public Tradesman getTradesman() {
        return tradesman;
    }

    public Service getService() {
        return service;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTradesman(Tradesman tradesman) {
        this.tradesman = tradesman;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
