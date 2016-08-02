package com.homefix.tradesman.timeslot.own_job;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.homefix.tradesman.R;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceSet;
import com.homefix.tradesman.timeslot.base_service.BaseServiceFragment;
import com.homefix.tradesman.timeslot.base_service.BaseServiceView;
import com.homefix.tradesman.timeslot.own_job.charges.ChargesActivity;
import com.homefix.tradesman.timeslot.own_job.payments.PaymentsActivity;
import com.samdroid.common.ColorUtils;
import com.samdroid.listener.BackgroundColourOnTouchListener;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 7/19/2016.
 */

public class OwnJobFragment extends BaseServiceFragment<OwnJobPresenter> implements BaseServiceView {

    private View mChargesBar, mInvoiceBar, mPaymentsBar;
    private TextView mChargesLblTxt, mChargesTxt, mInvoiceTxt, mPaymentsTxt;

    public OwnJobFragment() {
    }

    @Override
    protected OwnJobPresenter getPresenter() {
        if (presenter == null) presenter = new OwnJobPresenter(this);

        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_own_job;
    }

    @Override
    protected void injectDependencies() {
        super.injectDependencies();

        View view = getView();

        if (view == null) return;

        mChargesBar = view.findViewById(R.id.charges_bar);
        mChargesLblTxt = (TextView) mChargesBar.findViewById(R.id.charges_total_lbl);
        mChargesTxt = (TextView) mChargesBar.findViewById(R.id.charges_total_txt);
        mInvoiceBar = view.findViewById(R.id.invoice_bar);
        mInvoiceTxt = (TextView) mInvoiceBar.findViewById(R.id.invoice_txt);
        mPaymentsBar = view.findViewById(R.id.payments_bar);
        mPaymentsTxt = (TextView) mPaymentsBar.findViewById(R.id.payments_txt);
    }

    @Override
    public void setupView() {
        super.setupView();

        if (mTimeslot != null) {
            Service service = mTimeslot.getService();
            ServiceSet serviceSet = service != null ? service.getService_set() : null;

            if (serviceSet != null) {
                if (mChargesTxt != null) {
                    mChargesTxt.setText(Html.fromHtml("£" + Strings.priceToString(serviceSet.getTotal_cost()) + " total"));
                }

                if (mPaymentsTxt != null) {
                    double amountRemaining = serviceSet.getAmountRemaining();
                    double amountPaid = serviceSet.getAmount_paid();
                    String s = "";

                    if (amountRemaining > 0) {
                        s += Strings.setStringColour("£" + Strings.priceToString(amountRemaining) + " due", ColorUtils.red);
                    }

                    if (amountPaid > 0) {
                        if (amountRemaining > 0) s += " ";
                        s += Strings.setStringColour("(£" + Strings.priceToString(amountPaid) + " paid)", ColorUtils.green);
                    }

                    mPaymentsTxt.setText(Html.fromHtml(s));
                }

            }
        }

        BackgroundColourOnTouchListener touchListener = new BackgroundColourOnTouchListener(getContext(), R.color.transparent, R.color.colorAccentDark);

        if (mChargesBar != null) {
            mChargesBar.setOnTouchListener(touchListener);
            mChargesBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToActivitySendingService(ChargesActivity.class);
                }
            });
        }

        if (mInvoiceBar != null) {
            mInvoiceBar.setOnTouchListener(touchListener);
            mInvoiceBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO
                }
            });
        }

        if (mPaymentsBar != null) {
            mPaymentsBar.setOnTouchListener(touchListener);
            mPaymentsBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToActivitySendingService(PaymentsActivity.class);
                }
            });
        }

        // if not in edit mode
        if (!isEdit) {
            if (mSaveTxt != null) {
                mSaveTxt.setText("DONE");
                mSaveTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getBaseActivity().tryClose();
                    }
                });
            }
            return;
        }

        // else in edit mode //
        if (mSaveTxt != null) {
            mSaveTxt.setText(mTimeslot != null ? "UPDATE" : "CREATE");
            mSaveTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveClicked();
                }
            });
        }
    }

    private void goToActivitySendingService(Class activityClass) {
        Service service = mTimeslot != null ? mTimeslot.getService() : null;

        if (service == null) return;

        // add the service to the cache
        String serviceKey = "" + mTimeslot.getService().hashCode();
        Service.getSenderReceiver().put(serviceKey, mTimeslot.getService());

        // start the ChargesActivity
        Intent i = new Intent(getContext(), activityClass);
        i.putExtra("serviceKey", serviceKey);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_slide_in, R.anim.expand_out_partial);
    }

    @Override
    public void saveClicked() {
        // if the user has not made any changes
        if (!hasMadeChanges) {
            if (mTimeslot == null) {
                Toast.makeText(getContext(), "Job is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // go back into viewing mode
            setEditing(false);
            setupView();
            return;
        }

        // if the user is creating a new job
        if (mTimeslot == null) {
            getPresenter().addNewJob(
                    mStartCal,
                    mEndCal,
                    mJobTypeTxt.getText().toString(),
                    addressLine1,
                    addressLine2,
                    addressLine3,
                    postcode,
                    country,
                    latitude,
                    longitude,
                    mPersonNameTxt.getText().toString(),
                    mPersonEmailTxt.getText().toString(),
                    mPersonPhoneNumberTxt.getText().toString(),
                    mCustomerPropertyType.getText().toString(),
                    mDescriptionTxt.getText().toString());

            return;
        }

        // else they are updating an already existing job //
        getPresenter().updateJob(
                mTimeslot,
                mStartCal,
                mEndCal,
                mJobTypeTxt.getText().toString(),
                addressLine1,
                addressLine2,
                addressLine3,
                postcode,
                country,
                latitude,
                longitude,
                mPersonNameTxt.getText().toString(),
                mPersonEmailTxt.getText().toString(),
                mPersonPhoneNumberTxt.getText().toString(),
                mCustomerPropertyType.getText().toString(),
                mDescriptionTxt.getText().toString());
    }

}
