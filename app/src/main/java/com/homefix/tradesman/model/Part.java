package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class Part {

    private Service service;
    private String name, from, description, installationInfo, image, source;
    private double cost, withVat;

    public Part() {
    }

    public String getName() {
        return Strings.returnSafely(name);
    }

    public String getDescription() {
        return Strings.returnSafely(description);
    }

    public Service getService() {
        return service;
    }

    public String getInstallationInfo() {
        return Strings.returnSafely(installationInfo);
    }

    public String getImage() {
        return Strings.returnSafely(image);
    }

    public String getSource() {
        return Strings.returnSafely(source);
    }

    public double getCost() {
        return cost;
    }

    public String getFrom() {
        return from;
    }

    public double getWithVat() {
        return withVat;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInstallationInfo(String installationInfo) {
        this.installationInfo = installationInfo;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setWithVat(double withVat) {
        this.withVat = withVat;
    }
}
