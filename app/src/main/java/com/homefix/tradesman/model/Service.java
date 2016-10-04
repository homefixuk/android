package com.homefix.tradesman.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.homefix.tradesman.common.SendReceiver;
import com.samdroid.string.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samuel on 6/15/2016.
 */

@IgnoreExtraProperties
public class Service {

    private String id, serviceSetId, tradesmanId, status, serviceType;
    private long requestTime, arrivalTime, departTime, estimatedDuration;
    private int estimatedCost, actualDuration, actualCost;
    private String tradesmanNotes;
    private String keyLocation;
    private Map<String, Boolean> previousServices;
    private boolean isOwnJob;
    private String incompleteReason, actualDiagnosis, workCompletedDescription;

    public Service(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Map<String, Boolean> getPreviousServices() {
        if (previousServices == null) previousServices = new HashMap<>();

        return previousServices;
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

    public void setPreviousServices(Map<String, Boolean> previousServices) {
        this.previousServices = previousServices;
    }

    public boolean isOwnJob() {
        return isOwnJob;
    }

    public void setOwnJob(boolean ownJob) {
        isOwnJob = ownJob;
    }

    public void setIncompleteReason(String incompleteReason) {
        this.incompleteReason = incompleteReason;
    }

    public void setActualDiagnosis(String actualDiagnosis) {
        this.actualDiagnosis = actualDiagnosis;
    }

    public void setWorkCompletedDescription(String workCompletedDescription) {
        this.workCompletedDescription = workCompletedDescription;
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

    public String getServiceType() {
        return Strings.returnSafely(serviceType);
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    private static final SendReceiver<Service> senderReceiver = new SendReceiver<>(Service.class);

    public static SendReceiver<Service> getSenderReceiver() {
        return senderReceiver;
    }

    public String getServiceSetId() {
        return Strings.returnSafely(serviceSetId);
    }

    public void setServiceSetId(String serviceSetId) {
        this.serviceSetId = serviceSetId;
    }

    public String getTradesmanId() {
        return tradesmanId;
    }

    public void setTradesmanId(String tradesmanId) {
        this.tradesmanId = tradesmanId;
    }
}
