package com.homefix.tradesman.timeslot.own_job;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.homefix.tradesman.R;
import com.homefix.tradesman.common.HtmlHelper;
import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceSet;
import com.homefix.tradesman.timeslot.base_service.BaseServiceFragment;
import com.homefix.tradesman.timeslot.base_service.BaseServiceView;
import com.homefix.tradesman.timeslot.own_job.charges.ChargesActivity;
import com.homefix.tradesman.timeslot.own_job.invoice.OwnJobInvoice;
import com.homefix.tradesman.timeslot.own_job.payments.PaymentsActivity;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.ColorUtils;
import com.samdroid.common.IntentHelper;
import com.samdroid.listener.BackgroundColourOnTouchListener;
import com.samdroid.string.Strings;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by samuel on 7/19/2016.
 */

public class OwnJobFragment extends BaseServiceFragment<OwnJobPresenter> implements BaseServiceView {

    @BindView(R.id.invoice_bar)
    protected View mInvoiceBar;

    @BindView(R.id.charges_bar)
    protected View mChargesBar;

    @BindView(R.id.payments_bar)
    protected View mPaymentsBar;

    @BindView(R.id.charges_total_txt)
    protected TextView mChargesTxt;

    @BindView(R.id.payments_txt)
    protected TextView mPaymentsTxt;

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
    public void setupView() {
        super.setupView();

        if (mTimeslot != null) {
            Service service = mTimeslot.getService();
            ServiceSet serviceSet = service != null ? service.getServiceSet() : null;

            if (serviceSet != null) {
                if (mChargesTxt != null) {
                    mChargesTxt.setText(HtmlHelper.fromHtml("£" + Strings.priceToString(serviceSet.getTotalCost()) + " total"));
                }

                if (mPaymentsTxt != null) {
                    double amountRemaining = serviceSet.getAmountRemaining();
                    double amountPaid = serviceSet.getAmountPaid();
                    String s = "";

                    if (amountRemaining > 0) {
                        s += Strings.setStringColour("£" + Strings.priceToString(amountRemaining) + " due", ColorUtils.red);
                    }

                    if (amountPaid > 0) {
                        if (amountRemaining > 0) s += " ";
                        s += Strings.setStringColour("(£" + Strings.priceToString(amountPaid) + " paid)", ColorUtils.green);
                    }

                    mPaymentsTxt.setText(HtmlHelper.fromHtml(s));
                }

            }
        }

        BackgroundColourOnTouchListener touchListener = new BackgroundColourOnTouchListener(getContext(), R.color.transparent, R.color.colorAccentDark);
        if (mInvoiceBar != null) mInvoiceBar.setOnTouchListener(touchListener);
        if (mChargesBar != null) mChargesBar.setOnTouchListener(touchListener);
        if (mPaymentsBar != null) mPaymentsBar.setOnTouchListener(touchListener);
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

    @OnClick(R.id.charges_bar)
    public void onChargesClicked() {
        goToActivitySendingService(ChargesActivity.class);
    }

    @OnClick(R.id.invoice_bar)
    public void onInvoiceClicked() {
        MaterialDialogWrapper.getListDialog(
                getActivity(),
                "Customer Invoice",
                new CharSequence[]{"Send to Customer", "View Invoice"},
                new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        OwnJobInvoice invoice = new OwnJobInvoice(mTimeslot != null ? mTimeslot.getService() : null);

                        invoice.generate();
                        File file = invoice.getFile();

                        if (file == null) {
                            showDialog("Sorry, something went wrong! Please try again.", false);
                            if (dialog != null) dialog.dismiss();
                            return;
                        }

                        if (which == 0) {
                            Service service = mTimeslot != null ? mTimeslot.getService() : null;
                            String content = "";

                            content += "Hi " + invoice.getCustomerFirstName() + ",\n\n";
                            content += "Please see your attached invoice";
                            if (service != null && service.getDepartTime() > 0) {
                                Date d = new Date();
                                d.setTime(service.getDepartTime());
                                content += " for the work done on the " + SimpleDateFormat.getInstance().format(d) + ".";
                            } else {
                                content += ".";
                            }
                            content += "\n\n";
                            content += "Kind Regards,\n";
                            content += UserController.getCurrentUser().getName();

                            IntentHelper.openEmailWithAttachment(
                                    getContext(),
                                    invoice.getCustomerEmail(),
                                    invoice.getSubject(),
                                    content,
                                    file.getAbsolutePath());

                            dialog.dismiss();

                        } else if (which == 1) {
                            invoice.view(getContext());

                            // Note: do not dismiss dialog so the invoice can be sent after viewing it
                        }
                    }
                }

        ).show();
    }

    @OnClick(R.id.payments_bar)
    public void onPaymentsClicked() {
        goToActivitySendingService(PaymentsActivity.class);
    }

}
