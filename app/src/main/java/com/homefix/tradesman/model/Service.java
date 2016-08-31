package com.homefix.tradesman.model;

import com.homefix.tradesman.common.SendReceiver;
import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 6/15/2016.
 */

public class Service {

    private ServiceSet serviceSet;
    private Tradesman tradesman;
    private Problem problem;
    private String id, status;
    private long requestTime, arrivalTime, departTime, estimatedDuration;
    private int estimatedCost, actualDuration, actualCost;
    private String tradesmanNotes;
    private String keyLocation;
    private List<Service> previousServices;
    private boolean isOwnJob;
    private String incompleteReason, actualDiagnosis, workCompletedDescription;
    private List<Part> partsUsed;

    public ServiceSet getServiceSet() {
        return serviceSet;
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

    public long getRequestTime() {
        return requestTime;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public long getDepartTime() {
        return departTime;
    }

    public long getEstimatedDuration() {
        return estimatedDuration;
    }

    public int getEstimatedCost() {
        return estimatedCost;
    }

    public int getActualDuration() {
        return actualDuration;
    }

    public int getActualCost() {
        return actualCost;
    }

    public String getTradesmanNotes() {
        return Strings.returnSafely(tradesmanNotes);
    }

    public String getKeyLocation() {
        return Strings.returnSafely(keyLocation);
    }

    public List<Service> getPreviousServices() {
        if (previousServices == null) previousServices = new ArrayList<>();

        return previousServices;
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

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setDepartTime(long departTime) {
        this.departTime = departTime;
    }

    public void setEstimatedDuration(long estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public void setEstimatedCost(int estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public void setActualDuration(int actualDuration) {
        this.actualDuration = actualDuration;
    }

    public void setActualCost(int actualCost) {
        this.actualCost = actualCost;
    }

    public void setTradesmanNotes(String tradesmanNotes) {
        this.tradesmanNotes = tradesmanNotes;
    }

    public void setKeyLocation(String keyLocation) {
        this.keyLocation = keyLocation;
    }

    public void setPreviousServices(List<Service> previousServices) {
        this.previousServices = previousServices;
    }

    public boolean isOwnJob() {
        return isOwnJob;
    }

    public String getIncompleteReason() {
        return Strings.returnSafely(incompleteReason);
    }

    public String getActualDiagnosis() {
        return Strings.returnSafely(actualDiagnosis);
    }

    public String getWorkCompletedDescription() {
        return Strings.returnSafely(workCompletedDescription);
    }

    public List<Part> getPartsUsed() {
        if (partsUsed == null) partsUsed = new ArrayList<>();

        return partsUsed;
    }

    private static final SendReceiver<Service> senderReceiver = new SendReceiver<>(Service.class);

    public static SendReceiver<Service> getSenderReceiver() {
        return senderReceiver;
    }

}
