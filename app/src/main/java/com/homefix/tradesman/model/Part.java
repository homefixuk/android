package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/15/2016.
 */

public class Part {

    Service service;
    String name, from, description, installation_info, image, source;
    double cost, with_vat;

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

    public String getInstallation_info() {
        return Strings.returnSafely(installation_info);
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

    public double getWith_vat() {
        return with_vat;
    }
}
