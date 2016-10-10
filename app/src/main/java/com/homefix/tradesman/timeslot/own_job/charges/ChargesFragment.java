package com.homefix.tradesman.timeslot.own_job.charges;

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
import com.homefix.tradesman.common.AnalyticsHelper;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.model.Charge;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceSet;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.listener.BackgroundColourOnTouchListener;
import com.samdroid.string.Strings;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by samuel on 7/27/2016.
 */

public class ChargesFragment
        extends BaseCloseFragment<ChargesActivity, ChargesFragmentView, ChargesFragmentPresenter>
        implements ChargesFragmentView {

    private Service service;

    @BindView(R.id.amount)
    protected TextView mTotalCost;

    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    private MyFirebaseRecyclerAdapter<Charge, ChargeViewHolder> adapter;
    private DatabaseReference chargesRef;

    public ChargesFragment() {
        super(ChargesFragment.class.getSimpleName());
    }

    @Override
    protected ChargesFragmentPresenter getPresenter() {
        if (presenter == null) presenter = new ChargesFragmentPresenter(this);

        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.charges_layout;
    }

    public static class ChargeViewHolder extends RecyclerView.ViewHolder {

        public ChargeViewHolder(View view) {
            super(view);
        }

        public void bind(final ChargesFragmentView view, final Charge charge) {
            if (view == null) return;

            TextView mLbl = ButterKnife.findById(itemView, R.id.label);
            TextView mAmount = ButterKnife.findById(itemView, R.id.amount);
            TextView mQuantity = ButterKnife.findById(itemView, R.id.quantity);

            mLbl.setText(charge.getDescription());
            mAmount.setText(String.format("£%s", Strings.priceToString(charge.getAmount())));
            mQuantity.setText(String.format("%s", Strings.priceToString(charge.getQuantity())));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.showEditCharge(charge);
                }
            });
            itemView.setOnTouchListener(new BackgroundColourOnTouchListener(view.getContext(), R.color.transparent, R.color.light_grey));

            // show long touch listener to ask if the user wants to delete the charge
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MaterialDialogWrapper.getNegativeConfirmationDialog(
                            view.getBaseActivity(),
                            "Would you like to delete this charge?",
                            "DELETE",
                            "CANCEL",
                            new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    // call API to delete charge
                                    view.removeChargeClicked(charge);
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

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chargesRef = getChargesRef();
        if (chargesRef == null) {
            Toast.makeText(getContext(), "Unable to update the charges", Toast.LENGTH_SHORT).show();
            getBaseActivity().finishWithIntentAndAnimation(null);
            return;
        }

        chargesRef.addValueEventListener(new ValueEventListener() {
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
                            updateTotalCostView(serviceSet.getTotalCost());
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

        adapter = new MyFirebaseRecyclerAdapter<Charge, ChargeViewHolder>(
                getActivity(),
                Charge.class,
                ChargeViewHolder.class,
                R.layout.charges_item_layout,
                chargesRef) {
            @Override
            protected void populateViewHolder(ChargeViewHolder viewHolder, Charge model, int position) {
                viewHolder.bind(getThisView(), model);
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setAdapter(adapter);
    }

    public ChargesFragmentView getThisView() {
        return this;
    }

    private DatabaseReference getChargesRef() {
        if (chargesRef != null) return chargesRef;

        String serviceSetId = service != null ? service.getServiceSetId() : null;
        DatabaseReference ref = FirebaseUtils.getSpecificServiceSetRef(serviceSetId);
        return ref == null ? chargesRef : ref.child("charges");
    }

    private void updateTotalCostView(double totalCost) {
        if (mTotalCost != null)
            mTotalCost.setText(String.format("£%s", Strings.priceToString(totalCost)));
    }

    public void removeChargeClicked(final Charge charge) {
        chargesRef = getChargesRef();
        if (chargesRef == null || charge == null || Strings.isEmpty(charge.getId())) return;

        // show loading dialog
        showDialog("Removing Charge...", true);

        chargesRef.child(charge.getId()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                hideDialog();

                if (databaseError != null) {
                    Toast.makeText(getContext(), "Sorry, unable to remove the charge right now. Please try again", Toast.LENGTH_SHORT).show();
                } else {
                    Bundle b = new Bundle();
                    b.putDouble("amount", charge.getAmount());
                    b.putDouble("quantity", charge.getQuantity());
                    b.putDouble("markup", charge.getMarkup());
                    b.putBoolean("isWithVat", charge.isWithVat());
                    b.putBoolean("isMarkupBeforeVat", charge.isMarkupBeforeVat());
                    b.putDouble("totalCost", charge.getTotalCost());
                    AnalyticsHelper.track(
                            getView().getContext(),
                            "removeCharge",
                            b);
                }
            }
        });
    }

    public void addClicked() {
        showEditCharge(null);
    }

    public void showEditCharge(Charge charge) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

        final AddChargeView view = (AddChargeView) getActivity().getLayoutInflater().inflate(R.layout.add_charge_layout, null);

        final boolean isEmpty = charge == null;

        if (isEmpty) {
            charge = new Charge();
            charge.setId("" + System.currentTimeMillis());
        }

        final Charge finalCharge = charge;

        // setup the charge in the view
        view.attach(finalCharge);

        builder.customView(view, true);
        builder.positiveText(!isEmpty ? "UPDATE" : "ADD");
        builder.negativeText("CANCEL");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                if (addOrEditCharge(view, isEmpty ? null : finalCharge, view.getCharge())) {
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

    private boolean addOrEditCharge(AddChargeView view, final Charge originalCharge, final Charge newCharge) {
        chargesRef = getChargesRef();
        if (chargesRef == null || view == null || newCharge == null) {
            Toast.makeText(getContext(), "Sorry, unable to add/edit charge", Toast.LENGTH_SHORT).show();
            return false;
        }

        // make sure there's a valid name or description
        if (Strings.isEmpty(newCharge.getDescription())) {
            view.setNameError("Please enter a name or description");
            return false;
        }

        // make sure the quantity is valid
        if (newCharge.getQuantity() <= 0) {
            view.setQuantityError("Please enter a positive quantity");
            return false;
        }

        // show loading dialog
        showDialog((originalCharge == null ? "Adding" : "Updating") + " Charge...", true);

        chargesRef.child(newCharge.getId()).setValue(newCharge, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                hideDialog();

                if (databaseError != null) {
                    Toast.makeText(getContext(), "Sorry, unable to add/edit charge", Toast.LENGTH_SHORT).show();
                } else {
                    Bundle b = new Bundle();
                    b.putDouble("amount", newCharge.getAmount());
                    b.putDouble("quantity", newCharge.getQuantity());
                    b.putDouble("markup", newCharge.getMarkup());
                    b.putBoolean("isWithVat", newCharge.isWithVat());
                    b.putBoolean("isMarkupBeforeVat", newCharge.isMarkupBeforeVat());
                    b.putDouble("totalCost", newCharge.getTotalCost());
                    AnalyticsHelper.track(
                            getView().getContext(),
                            originalCharge == null ? "addCharge" : "updateCharge",
                            b);
                }
            }
        });

        return true;
    }

    public void setService(Service service) {
        this.service = service;
    }

}
