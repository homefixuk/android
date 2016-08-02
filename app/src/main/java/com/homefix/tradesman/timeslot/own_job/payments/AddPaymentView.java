package com.homefix.tradesman.timeslot.own_job.payments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.homefix.tradesman.R;
import com.homefix.tradesman.model.Payment;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 7/27/2016.
 */

public class AddPaymentView extends LinearLayout {

    private Payment payment;

    public AddPaymentView(Context context) {
        super(context);
    }

    public AddPaymentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AddPaymentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AddPaymentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void attach(Payment payment) {
        this.payment = payment;

        setType(payment.getType());
        setAmount(payment.getAmount());
    }

    public void setType(String type) {
        if (payment != null) payment.setType(Strings.returnSafely(type));

        Spinner spinner = (Spinner) findViewById(R.id.type_spinner);

        if (spinner == null) return;

        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        if (adapter == null) {
            // Create an ArrayAdapter using the string array and a default spinner layout
            adapter = ArrayAdapter.createFromResource(
                    getContext(),
                    R.array.payment_types,
                    android.R.layout.simple_spinner_item);

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
        }

        // set the type in the adapter
        type = Strings.returnSafely(type);
        for (int i = 0, len = adapter.getCount(); i < len; i++) {
            if (adapter.getItem(i).equals(type)) {
                spinner.setSelection(i);
                i = len;
            }
        }
    }

    public void setAmount(double amount) {
        if (payment != null) payment.setAmount(amount);

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
                if (payment != null) payment.setAmount(Strings.parseDouble(s.toString()));
            }
        });
    }

    public String getType() {
        Spinner spinner = (Spinner) findViewById(R.id.type_spinner);

        if (spinner == null) return "unknown";

        return String.valueOf(spinner.getSelectedItem());
    }

    public double getAmount() {
        TextView txt = (TextView) findViewById(R.id.amount_txt);
        return Double.parseDouble(txt != null ? txt.getText().toString() : "0");
    }

    public Payment getPayment() {
        return payment;
    }

}
