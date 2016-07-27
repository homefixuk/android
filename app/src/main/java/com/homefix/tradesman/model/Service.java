package com.homefix.tradesman.model;

import com.homefix.tradesman.common.SendReceiver;
import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 6/15/2016.
 */

public class Service {

    private ServiceSet service_set;
    private Tradesman tradesman;
    private Problem problem;
    private String id, status;
    private long request_time, arrival_time, depart_time, estimated_duration;
    private int estimated_cost, actual_duration, actual_cost;
    private String tradesman_notes;
    private String key_location;
    private List<Service> previous_services;
    private boolean is_own_job;
    private String incomplete_reason, actual_diagnosis, work_completed_description;
    private List<Part> parts_used;

    public ServiceSet getService_set() {
        return service_set;
    }

    public Tradesman getTradesman() {
        return tradesman;
    }

    public Problem getProblem() {
        return problem;
    }

    public String getId() {
        return Strings.returnSafely(id);
    }

    public String getStatus() {
        return Strings.returnSafely(status);
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
        return Strings.returnSafely(tradesman_notes);
    }

    public String getKey_location() {
        return Strings.returnSafely(key_location);
    }

    public List<Service> getPrevious_services() {
        if (previous_services == null) previous_services = new ArrayList<>();

        return previous_services;
    }

    public void setTradesman(Tradesman tradesman) {
        this.tradesman = tradesman;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
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

    public boolean is_own_job() {
        return is_own_job;
    }

    public String getIncomplete_reason() {
        return Strings.returnSafely(incomplete_reason);
    }

    public String getActual_diagnosis() {
        return Strings.returnSafely(actual_diagnosis);
    }

    public String getWork_completed_description() {
        return Strings.returnSafely(work_completed_description);
    }

    public List<Part> getParts_used() {
        if (parts_used == null) parts_used = new ArrayList<>();

        return parts_used;
    }

    private static final SendReceiver<Service> senderReceiver = new SendReceiver<>(Service.class);

    public static SendReceiver<Service> getSenderReceiver() {
        return senderReceiver;
    }

}
