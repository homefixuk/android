package com.homefix.tradesman.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.samdroid.string.Strings;

import java.util.Map;

/**
 * Created by samuel on 7/27/2016.
 */

@IgnoreExtraProperties
public class Charge extends BaseModel {

    private double amount = 0;
    private String description; // labour/part/other
    private double quantity = 1.0;
    private boolean withVat = true;
    private double markup = 0;
    private boolean markupBeforeVat = false;

    public Charge() {
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return Strings.returnSafely(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getQuantity() {
        return quantity;
    }

    public boolean isWithVat() {
        return withVat;
    }

    public double getMarkup() {
        return markup;
    }

    public boolean isMarkupBeforeVat() {
        return markupBeforeVat;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setWithVat(boolean withVat) {
        this.withVat = withVat;
    }

    public void setMarkup(double markup) {
        this.markup = markup;
    }

    public void setMarkupBeforeVat(boolean markupBeforeVat) {
        this.markupBeforeVat = markupBeforeVat;
    }

    @Exclude
    public double getAmountWithVatAndMarkup() {
        return getTotalCost() / quantity;
    }

    /**
     * @return the total cost combining the VAT and markup multiplied by the quantity
     */
    @Exclude
    public double getTotalCost() {
        // if there is no markup
        if (markup == 0) return quantity * amount * (withVat ? 1.2 : 1.0);

        double VAT = quantity * amount * (withVat ? 0.2 : 0.0);

        if (markupBeforeVat) return (quantity * amount * (1.0 + markup * 0.01)) + VAT;

        // else markup is after VAT
        return ((quantity * amount) + VAT) * (1.0 + markup * 0.01);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("amount", getAmount());
        map.put("description", getDescription());
        map.put("quantity", getQuantity());
        map.put("withVat", isWithVat());
        map.put("markup", getMarkup());
        map.put("markupBeforeVat", isMarkupBeforeVat());
        return map;
    }
}
