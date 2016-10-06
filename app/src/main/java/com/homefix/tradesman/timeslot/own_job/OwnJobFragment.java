package com.homefix.tradesman.timeslot.own_job;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.pdf.PdfViewActivity;
import com.homefix.tradesman.common.HtmlHelper;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceSet;
import com.homefix.tradesman.model.Tradesman;
import com.homefix.tradesman.model.TradesmanPrivate;
import com.homefix.tradesman.timeslot.base_service.BaseServiceFragment;
import com.homefix.tradesman.timeslot.base_service.BaseServiceView;
import com.homefix.tradesman.timeslot.own_job.charges.ChargesActivity;
import com.homefix.tradesman.timeslot.own_job.invoice.OwnJobInvoice;
import com.homefix.tradesman.timeslot.own_job.payments.PaymentsActivity;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.ColorUtils;
import com.samdroid.common.IntentHelper;
import com.samdroid.listener.BackgroundColourOnTouchListener;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by samuel on 7/19/2016.
 */

public class OwnJobFragment extends BaseServiceFragment<OwnJobView, OwnJobPresenter> implements OwnJobView {

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

        BackgroundColourOnTouchListener touchListener = new BackgroundColourOnTouchListener(getContext(), R.color.transparent, R.color.colorAccentDark) {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTimeslot != null && super.onTouch(v, event);
            }

        };

        if (mInvoiceBar != null) mInvoiceBar.setOnTouchListener(touchListener);
        if (mChargesBar != null) mChargesBar.setOnTouchListener(touchListener);
        if (mPaymentsBar != null) mPaymentsBar.setOnTouchListener(touchListener);
    }

    @Override
    protected void setupServiceSetView(ServiceSet serviceSet) {
        super.setupServiceSetView(serviceSet);

        if (serviceSet == null) return;

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

    private void goToActivitySendingService(Class activityClass, int requestCode) {
        if (mService == null) return;

        // add the service to the cache
        String serviceKey = "" + mService.hashCode();
        Service.getSenderReceiver().put(serviceKey, mService);

        // start the ChargesActivity
        Intent i = new Intent(getContext(), activityClass);
        i.putExtra("serviceKey", serviceKey);
        startActivityForResult(i, requestCode);
        getActivity().overridePendingTransition(R.anim.right_slide_in, R.anim.expand_out_partial);
    }

    @Override
    @OnClick(R.id.save)
    public void saveClicked() {
        if (!isEdit) {
            onCloseClicked();
            return;
        }

        // if the user has not made any changes
        if (!hasMadeChanges) {
            if (mTimeslot == null) {
                Toast.makeText(getContext(), "Job is empty", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // if the user is creating a new job
        if (mTimeslot == null) {
            getPresenter().addNewJob(
                    mStartCalNew != null ? mStartCalNew : mStartCal,
                    mEndCalNew != null ? mEndCalNew : mEndCal,
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
                mTimeslot != null ? mTimeslot.getId() : "",
                mService != null ? mService.getId() : "",
                mServiceSet != null ? mServiceSet.getId() : "",
                mCustomer != null ? mCustomer.getId() : "",
                mProperty != null ? mProperty.getId() : "",
                mCustomerProperty != null ? mCustomerProperty.getId() : "",
                mStartCalNew != null ? mStartCalNew : mStartCal,
                mEndCalNew != null ? mEndCalNew : mEndCal,
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
        if (mTimeslot == null) {
            Toast.makeText(getContext(), "Please create the job before adding charges", Toast.LENGTH_SHORT).show();
            return;
        }

        goToActivitySendingService(ChargesActivity.class, Ids.UPDATE_CHARGES);
    }

    @OnClick(R.id.invoice_bar)
    public void onInvoiceClicked() {
        if (mTimeslot == null) {
            Toast.makeText(getContext(), "Please create the job before invoicing", Toast.LENGTH_SHORT).show();
            return;
        }

        MaterialDialogWrapper.getListDialog(
                getActivity(),
                "Customer Invoice",
                new CharSequence[]{"Send to Customer", "View Invoice"},
                new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(final MaterialDialog dialog, View itemView, final int which, CharSequence text) {
                        generateInvoice(new OnGotObjectListener<OwnJobInvoice>() {
                            @Override
                            public void onGotThing(OwnJobInvoice invoice) {
                                File file = invoice != null ? invoice.getFile() : null;
                                if (file == null) {
                                    showDialog("Sorry, something went wrong! Please try again.", false);
                                    return;
                                }

                                if (which == 0) {
                                    String content = "";

                                    content += "Hi " + invoice.getCustomerFirstName() + ",\n\n";
                                    content += "Please see your attached invoice";
                                    if (mService != null && mService.getDepartTime() > 0) {
                                        Date d = new Date();
                                        d.setTime(mTimeslot.getStartTime());
                                        content += " for the work done on the " + SimpleDateFormat.getInstance().format(d) + ".";
                                    } else {
                                        content += ".";
                                    }
                                    content += "\n\n";
                                    content += "Kind Regards,\n";

                                    Tradesman tradesman = Tradesman.getCurrentTradesman();
                                    content += tradesman != null ? tradesman.getName() : "Your Homefix Tradesman";

                                    IntentHelper.openEmailWithAttachment(
                                            getBaseActivity(),
                                            invoice.getCustomerEmail(),
                                            invoice.getSubject(),
                                            content,
                                            file.getAbsolutePath());

                                    dialog.dismiss();

                                } else if (which == 1) {
                                    try {
                                        invoice.view(getContext());
                                    } catch (Exception e) {
                                        Intent i = new Intent(getContext(), PdfViewActivity.class);
                                        i.putExtra("filePath", invoice.getFile().getAbsoluteFile());
                                        startActivity(i);
                                        getActivity().overridePendingTransition(R.anim.right_slide_in, R.anim.expand_out_partial);
                                    }

                                    // Note: do not dismiss dialog so the invoice can be sent after viewing it
                                }
                            }
                        });
                    }
                }

        ).show();
    }

    private void generateInvoice(@NonNull final OnGotObjectListener<OwnJobInvoice> callback) {
        final Tradesman currentTradesman = Tradesman.getCurrentTradesman();
        final String serviceId = mTimeslot != null ? mTimeslot.getServiceId() : null;
        DatabaseReference ref = FirebaseUtils.getCurrentTradesmanPrivateRef();
        if (currentTradesman == null || Strings.isEmpty(serviceId) || ref == null) {
            callback.onGotThing(null);
            return;
        }

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || !dataSnapshot.exists()) {
                    onCancelled(null);
                    return;
                }

                TradesmanPrivate tradesmanPrivate = dataSnapshot.getValue(TradesmanPrivate.class);

                OwnJobInvoice invoice = new OwnJobInvoice(
                        currentTradesman,
                        mService,
                        mServiceSet,
                        mCustomer,
                        mProperty,
                        tradesmanPrivate);

                invoice.generate();
                callback.onGotThing(invoice);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onGotThing(null);
            }
        });
    }

    @OnClick(R.id.payments_bar)
    public void onPaymentsClicked() {
        if (mTimeslot == null) {
            Toast.makeText(getContext(), "Please create the job before adding payments", Toast.LENGTH_SHORT).show();
            return;
        }

        goToActivitySendingService(PaymentsActivity.class, Ids.UPDATE_PAYMENTS);
    }

}
