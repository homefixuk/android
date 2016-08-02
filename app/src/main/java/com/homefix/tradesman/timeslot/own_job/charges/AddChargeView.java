package com.homefix.tradesman.timeslot.own_job.charges;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.homefix.tradesman.R;
import com.homefix.tradesman.model.Charge;
import com.samdroid.common.ColorUtils;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 7/27/2016.
 */

public class AddChargeView extends LinearLayout {

    private Charge charge;

    public AddChargeView(Context context) {
        super(context);
    }

    public AddChargeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AddChargeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AddChargeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void attach(Charge charge) {
        this.charge = charge;

        setName(charge.getDescription());
        setQuantity(charge.getQuantity());
        setAmount(charge.getAmount());
        setChargeVAT(charge.isWith_vat());
        setMarkup(charge.getMarkup());
        setMarkupBeforeVAT(charge.isMarkup_before_vat());
    }

    public void setName(String name) {
        if (charge != null) charge.setDescription(Strings.returnSafely(name));

        TextView txt = (TextView) findViewById(R.id.name_txt);

        if (txt == null) return;

        txt.setText(Strings.returnSafely(name));

        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (charge != null) charge.setDescription(s.toString());
            }
        });
    }

    public void setNameError(String error) {
        TextView txt = (TextView) findViewById(R.id.name_txt);

        if (txt == null) return;

        txt.setError(Strings.returnSafely(error));
    }

    public void setQuantity(double quantity) {
        if (charge != null) charge.setQuantity(quantity);

        TextView txt = (TextView) findViewById(R.id.quantity_txt);

        if (txt == null) return;

        txt.setText(String.format("%s", Strings.priceToString(quantity)));

        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (charge != null) charge.setQuantity(Strings.parseDouble(s.toString()));
            }
        });
    }

    public void setQuantityError(String error) {
        TextView txt = (TextView) findViewById(R.id.quantity_txt);

        if (txt == null) return;

        txt.setError(Strings.returnSafely(error));
    }

    public void setAmount(double amount) {
        if (charge != null) charge.setAmount(amount);

        EditText txt = (EditText) findViewById(R.id.amount_txt);

        if (txt == null) return;

        txt.setText(String.format("%s", Strings.priceToString(amount)));

        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (charge != null) charge.setAmount(Strings.parseDouble(s.toString()));
            }
        });
    }

    public void setChargeVAT(boolean chargeVAT) {
        if (charge != null) charge.setWith_vat(chargeVAT);

        TextView txt = (TextView) findViewById(R.id.charge_vat_txt);

        if (txt == null) return;

        String s = Strings.setStringColour(String.format("%s VAT", chargeVAT ? "+ 20%" : "No"), chargeVAT ? ColorUtils.green : ColorUtils.red);

        txt.setText(Html.fromHtml(s));

        txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // toggle the value
                setChargeVAT(!charge.isWith_vat());
            }
        });
    }

    public void setMarkup(double markup) {
        if (charge != null) charge.setMarkup(markup);

        EditText txt = (EditText) findViewById(R.id.markup_txt);

        if (txt == null) return;

        txt.setText(String.format("%s", Strings.priceToString(markup)));

        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (charge != null) charge.setMarkup(Strings.parseDouble(s.toString()));
            }
        });
    }

    public void setMarkupBeforeVAT(boolean markupBeforeVAT) {
        if (charge != null) charge.setMarkup_before_vat(markupBeforeVAT);

        TextView txt = (TextView) findViewById(R.id.markup_before_vat_txt);

        if (txt == null) return;

        txt.setText(String.format("%s VAT", markupBeforeVAT ? "Before" : " After"));

        txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // toggle the value
                setMarkupBeforeVAT(!charge.isMarkup_before_vat());
            }
        });
    }

    public String getName() {
        TextView txt = (TextView) findViewById(R.id.name_txt);
        return txt != null ? txt.getText().toString() : "";
    }

    public double getQuantity() {
        TextView txt = (TextView) findViewById(R.id.quantity_txt);
        return Double.parseDouble(txt != null ? txt.getText().toString() : "0");
    }

    public double getAmount() {
        TextView txt = (TextView) findViewById(R.id.amount_txt);
        return Double.parseDouble(txt != null ? txt.getText().toString() : "0");
    }

    public double getMarkup() {
        TextView txt = (TextView) findViewById(R.id.markup_txt);
        return Double.parseDouble(txt != null ? txt.getText().toString() : "0");
    }

    public Charge getCharge() {
        return charge;
    }

}
