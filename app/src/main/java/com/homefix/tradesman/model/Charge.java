package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

/**
 * Created by samuel on 7/27/2016.
 */

public class Charge {

    private String id;
    private Service service;
    private double amount = 0;
    private String description; // labour/part/other
    private double quantity = 1;
    private boolean with_vat = true;
    private double markup = 0;
    private boolean markup_before_vat = false;

    public Charge() {
    }

    public String getId() {
        return Strings.returnSafely(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public Service getService() {
        return service;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getQuantity() {
        return quantity;
    }

    public boolean isWith_vat() {
        return with_vat;
    }

    public double getMarkup() {
        return markup;
    }

    public boolean isMarkup_before_vat() {
        return markup_before_vat;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setWith_vat(boolean with_vat) {
        this.with_vat = with_vat;
    }

    public void setMarkup(double markup) {
        this.markup = markup;
    }

    public void setMarkup_before_vat(boolean markup_before_vat) {
        this.markup_before_vat = markup_before_vat;
    }

    public double getAmountWithVatAndMarkup() {
        return totalCost() / quantity;
    }

    public double totalCost() {
        // if there is no markup
        if (markup == 0) return quantity * amount * (with_vat ? 1.2 : 1.0);

        double VAT = quantity * amount * (with_vat ? 0.2 : 0.0);

        if (markup_before_vat) return (quantity * amount * (1.0 + markup)) + VAT;

        // else markup is after VAT
        return ((quantity * amount) + VAT) * (1.0 + markup);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) || (o != null && o instanceof Charge && getId().equals(((Charge) o).getId()));
    }

}
