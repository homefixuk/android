package com.homefix.tradesman.model;

import java.util.List;

/**
 * Created by samuel on 6/15/2016.
 */

public class Service {

    Tradesman tradesman;
    ServiceType service_type;
    String status;
    long request_time, arrival_time, depart_time, estimated_duration;
    int estimated_cost, actual_duration, actual_cost;
    String tradesman_notes;
    String key_location;
    List<Service> previous_services;

    public Tradesman getTradesman() {
        return tradesman;
    }

    public ServiceType getService_type() {
        return service_type;
    }

    public String getStatus() {
        return status;
    }

    public long getRequest_time() {
        return request_time;
    }

    public long getArrival_time() {
        return arrival_time;
    }

    public long getDepart_time() {
        return depart_time;
    }

    public long getEstimated_duration() {
        return estimated_duration;
    }

    public int getEstimated_cost() {
        return estimated_cost;
    }

    public int getActual_duration() {
        return actual_duration;
    }

    public int getActual_cost() {
        return actual_cost;
    }

    public String getTradesman_notes() {
        return tradesman_notes;
    }

    public String getKey_location() {
        return key_location;
    }

    public List<Service> getPrevious_services() {
        return previous_services;
    }

    public void setTradesman(Tradesman tradesman) {
        this.tradesman = tradesman;
    }

    public void setService_type(ServiceType service_type) {
        this.service_type = service_type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRequest_time(long request_time) {
        this.request_time = request_time;
    }

    public void setArrival_time(long arrival_time) {
        this.arrival_time = arrival_time;
    }

    public void setDepart_time(long depart_time) {
        this.depart_time = depart_time;
    }

    public void setEstimated_duration(long estimated_duration) {
        this.estimated_duration = estimated_duration;
    }

    public void setEstimated_cost(int estimated_cost) {
        this.estimated_cost = estimated_cost;
    }

    public void setActual_duration(int actual_duration) {
        this.actual_duration = actual_duration;
    }

    public void setActual_cost(int actual_cost) {
        this.actual_cost = actual_cost;
    }

    public void setTradesman_notes(String tradesman_notes) {
        this.tradesman_notes = tradesman_notes;
    }

    public void setKey_location(String key_location) {
        this.key_location = key_location;
    }

    public void setPrevious_services(List<Service> previous_services) {
        this.previous_services = previous_services;
    }
}
