package com.homefix.tradesman.timeslot.own_job.payments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.homefix.tradesman.R;
import com.homefix.tradesman.base.adapter.MyFirebaseRecyclerAdapter;
import com.homefix.tradesman.base.fragment.BaseCloseFragment;
import com.homefix.tradesman.common.HtmlHelper;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.model.Payment;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceSet;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.ColorUtils;
import com.samdroid.listener.BackgroundColourOnTouchListener;
import com.samdroid.string.Strings;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by samuel on 7/27/2016.
 */

public class PaymentsFragment
        extends BaseCloseFragment<PaymentsActivity, PaymentsFragmentView, PaymentsFragmentPresenter>
        implements PaymentsFragmentView {

    private Service service;

    @BindView(R.id.total_cost)
    protected TextView mTotalCost;

    @BindView(R.id.total_paid)
    protected TextView mTotalPaid;

    @BindView(R.id.remaining)
    protected TextView mRemainingTxt;

    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    private MyFirebaseRecyclerAdapter<Payment, PaymentViewHolder> adapter;
    private DatabaseReference paymentsRef;

    public PaymentsFragment() {
        super(PaymentsFragment.class.getSimpleName());
    }

    @Override
    protected PaymentsFragmentPresenter getPresenter() {
        if (presenter == null) presenter = new PaymentsFragmentPresenter(this);

        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.payments_layout;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        paymentsRef = getPaymentsRef();
        if (paymentsRef == null) {
            Toast.makeText(getContext(), "Unable to update the payments", Toast.LENGTH_SHORT).show();
            getBaseActivity().finishWithIntentAndAnimation(null);
            return;
        }

        paymentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference ref = FirebaseUtils.getSpecificServiceSetRef(service != null ? service.getServiceSetId() : null);
                if (ref == null) return;
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot == null || !dataSnapshot.exists()) return;

                        ServiceSet serviceSet = dataSnapshot.getValue(ServiceSet.class);
                        if (serviceSet != null) {
                            serviceSet.update();
                            updateServiceSetView(serviceSet);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new MyFirebaseRecyclerAdapter<Payment, PaymentViewHolder>(
                getBaseActivity(),
                Payment.class,
                PaymentViewHolder.class,
                R.layout.payments_item_layout,
                paymentsRef) {
            @Override
            protected void populateViewHolder(PaymentViewHolder viewHolder, Payment model, int position) {
                viewHolder.bind(getThisView(), model);
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setAdapter(adapter);
    }

    public PaymentsFragmentView getThisView() {
        return this;
    }

    private void updateServiceSetView(ServiceSet serviceSet) {
        if (serviceSet == null) return;

        if (mTotalPaid != null)
            mTotalPaid.setText(String.format("£%s", Strings.priceToString(serviceSet.getAmountPaid())));

        if (mTotalCost != null)
            mTotalCost.setText(String.format("£%s", Strings.priceToString(serviceSet.getTotalCost())));

        if (mRemainingTxt != null) {
            double remaining = serviceSet.getAmountRemaining();
            String s = String.format("£%s", Strings.priceToString(remaining));
            s = Strings.setStringColour(s, remaining > 0 ? ColorUtils.red : ColorUtils.green);
            mRemainingTxt.setText(HtmlHelper.fromHtml(s));
        }
    }

    private DatabaseReference getPaymentsRef() {
        if (paymentsRef != null) return paymentsRef;

        String serviceSetId = service != null ? service.getServiceSetId() : null;
        DatabaseReference ref = FirebaseUtils.getSpecificServiceSetRef(serviceSetId);
        return ref == null ? paymentsRef : ref.child("payments");
    }

    public static class PaymentViewHolder extends RecyclerView.ViewHolder {

        public PaymentViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final PaymentsFragmentView view, final Payment payment) {
            if (view == null) return;

            TextView mLbl = ButterKnife.findById(itemView, R.id.label);
            TextView mAmount = ButterKnife.findById(itemView, R.id.amount);

            if (payment == null) {
                itemView.setVisibility(View.GONE);
                return;
            }

            itemView.setVisibility(View.VISIBLE);

            mLbl.setText(payment.getType());
            mAmount.setText(String.format("£%s", Strings.priceToString(payment.getAmount())));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.showEditPayment(payment);
                }
            });
            itemView.setOnTouchListener(new BackgroundColourOnTouchListener(view.getContext(), R.color.transparent, R.color.light_grey));

            // show long touch listener to ask if the user wants to delete the charge
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MaterialDialogWrapper.getNegativeConfirmationDialog(
                            view.getBaseActivity(),
                            "Would you like to delete this payment?",
                            "DELETE",
                            "CANCEL",
                            new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    // call API to delete charge
                                    view.removePaymentClicked(payment);
                                    dialog.dismiss();
                                }
                            }, new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).show();

                    return true;
                }
            });
        }
    }

    public void removePaymentClicked(final Payment payment) {
        paymentsRef = getPaymentsRef();
        if (paymentsRef == null || payment == null || Strings.isEmpty(payment.getId())) return;

        // show loading dialog
        showDialog("Removing Payment...", true);

        paymentsRef.child(payment.getId()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                hideDialog();

                if (databaseError != null) {
                    Toast.makeText(getContext(), "Sorry, unable to remove the payment right now. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean canClose() {
        return super.canClose();
    }

    public void addClicked() {
        showEditPayment(null);
    }

    public void showEditPayment(Payment payment) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

        final AddPaymentView view = (AddPaymentView) getActivity().getLayoutInflater().inflate(R.layout.add_payment_layout, null);

        final boolean isEmpty = payment == null;

        if (isEmpty) {
            payment = new Payment();
            payment.setId("" + System.currentTimeMillis());
        }

        final Payment finalPayment = payment;

        // setup the charge in the view
        view.attach(finalPayment);

        builder.customView(view, true);
        builder.positiveText(!isEmpty ? "UPDATE" : "ADD");
        builder.negativeText("CANCEL");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                if (addOrEditCharge(view, isEmpty ? null : finalPayment, view.getPayment())) {
                    dialog.dismiss();
                }
            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
        builder.negativeColorRes(R.color.black);

        builder.autoDismiss(false);
        builder.cancelable(false);

        builder.show();
    }

    private boolean addOrEditCharge(AddPaymentView view, final Payment originalPayment, final Payment newPayment) {
        paymentsRef = getPaymentsRef();
        if (paymentsRef == null || view == null || newPayment == null) {
            Toast.makeText(getContext(), "Sorry, unable to add/edit charge", Toast.LENGTH_SHORT).show();
            return false;
        }

        // show loading dialog
        showDialog((originalPayment == null ? "Adding" : "Updating") + " Payment...", true);

        paymentsRef.child(newPayment.getId()).setValue(newPayment, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                hideDialog();

                if (databaseError != null) {
                    Toast.makeText(getContext(), "Sorry, unable to add/edit payment", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return true;
    }

    public void setService(Service service) {
        this.service = service;
    }

}
