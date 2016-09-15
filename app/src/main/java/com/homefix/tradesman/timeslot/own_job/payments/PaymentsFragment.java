package com.homefix.tradesman.timeslot.own_job.payments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.homefix.tradesman.BuildConfig;
import com.homefix.tradesman.R;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.base.fragment.BaseCloseFragment;
import com.homefix.tradesman.base.presenter.BaseFragmentPresenter;
import com.homefix.tradesman.base.presenter.DefaultFragementPresenter;
import com.homefix.tradesman.base.view.BaseFragmentView;
import com.homefix.tradesman.data.TradesmanController;
import com.homefix.tradesman.model.Payment;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceSet;
import com.homefix.tradesman.timeslot.own_job.charges.ChargesActivity;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.ColorUtils;
import com.samdroid.listener.BackgroundColourOnTouchListener;
import com.samdroid.string.Strings;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by samuel on 7/27/2016.
 */

public class PaymentsFragment extends BaseCloseFragment<ChargesActivity, BaseFragmentView, BaseFragmentPresenter<BaseFragmentView>> {

    private Service service;

    @BindView(R.id.total_cost)
    protected TextView mTotalCost;

    @BindView(R.id.total_paid)
    protected TextView mTotalPaid;

    @BindView(R.id.remaining)
    protected TextView mRemainingTxt;

    @BindView(R.id.list)
    protected ListView mListView;

    private ArrayAdapter<Payment> mAdapter;

    public PaymentsFragment() {
        super(PaymentsFragment.class.getSimpleName());
    }

    @Override
    protected BaseFragmentPresenter getPresenter() {
        if (presenter == null) presenter = new DefaultFragementPresenter(this);

        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.payments_layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ServiceSet serviceSet = service != null ? service.getServiceSet() : null;

        if (serviceSet != null) {
            if (mTotalPaid != null)
                mTotalPaid.setText(String.format("£%s", Strings.priceToString(serviceSet.getAmountPaid())));

            if (mTotalCost != null)
                mTotalCost.setText(String.format("£%s", Strings.priceToString(serviceSet.getTotalCost())));

            if (mRemainingTxt != null) {
                double remaining = serviceSet.getAmountRemaining();
                String s = String.format("£%s", Strings.priceToString(remaining));
                s = Strings.setStringColour(s, remaining > 0 ? ColorUtils.red : ColorUtils.green);
                mRemainingTxt.setText(Html.fromHtml(s));
            }
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<Payment>(getActivity(), R.layout.payments_item_layout) {

                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    View view = convertView;

                    if (view == null) {
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        view = inflater.inflate(R.layout.payments_item_layout, parent, false);
                    }

                    TextView mLbl = ButterKnife.findById(view, R.id.label);
                    TextView mAmount = ButterKnife.findById(view, R.id.amount);

                    final Payment payment = getItem(position);
                    if (payment == null) {
                        view.setVisibility(View.GONE);
                        return view;
                    }

                    view.setVisibility(View.VISIBLE);

                    mLbl.setText(payment.getType());
                    mAmount.setText(String.format("£%s", Strings.priceToString(payment.getAmount())));

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showEditPayment(payment);
                        }
                    });
                    view.setOnTouchListener(new BackgroundColourOnTouchListener(getContext(), R.color.transparent, R.color.light_grey));

                    // show long touch listener to ask if the user wants to delete the charge
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            MaterialDialogWrapper.getNegativeConfirmationDialog(
                                    getActivity(),
                                    "Would you like to delete this payment?",
                                    "DELETE",
                                    "CANCEL",
                                    new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            // call API to delete charge
                                            removePaymentClicked(payment);
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

                    return view;
                }
            };
            List<Payment> paymentList = serviceSet != null ? serviceSet.getPayments() : null;
            if (paymentList != null) {
                mAdapter.addAll(paymentList);
                mAdapter.notifyDataSetChanged();
            }
        }

        mListView.setAdapter(mAdapter);
    }

    private void removePaymentClicked(final Payment payment) {
        if (payment == null || Strings.isEmpty(payment.getId())) return;

        // show loading dialog
        showDialog("Removing Payment...", true);

        Callback<Map<String, Object>> callback = new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> results = response.body();

                // if not successful
                if (results == null || !results.containsKey("success") || !(Boolean) results.get("success")) {
                    onFailure(call, null);
                    return;
                }

                // else successful //
                hideDialog();
                if (mAdapter != null) mAdapter.remove(payment);
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (BuildConfig.DEBUG && t != null) t.printStackTrace();

                hideDialog();
                Toast.makeText(getContext(), "Sorry, unable to remove the charge right now. Please try again", Toast.LENGTH_SHORT).show();
            }
        };

        HomeFix.getAPI().deleteCharge(TradesmanController.getToken(), payment.getId()).enqueue(callback);
    }

    @Override
    public boolean canClose() {
        return super.canClose();
    }

    public void addClicked() {
        showEditPayment(null);
    }

    private void showEditPayment(Payment payment) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

        final AddPaymentView view = (AddPaymentView) getActivity().getLayoutInflater().inflate(R.layout.add_payment_layout, null);

        final boolean isEmpty = payment == null;

        if (isEmpty) {
            payment = new Payment();
            payment.setServiceSet(service.getServiceSet());
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
        if (view == null || newPayment == null) return false;

        // show loading dialog
        showDialog((originalPayment == null ? "Adding" : "Updating") + " Payment...", true);

        Callback<Payment> callback = new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                Payment payment = response.body();

                if (payment == null) {
                    onFailure(call, null);
                    return;
                }

                hideDialog();
                updatePaymentInAdapter(originalPayment, payment); // take the Charge returned from the server
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                if (BuildConfig.DEBUG && t != null) t.printStackTrace();

                hideDialog();
                showEditPayment(originalPayment);
                Toast.makeText(getContext(), "Sorry, unable to do this right now. Please try again", Toast.LENGTH_SHORT).show();
            }
        };

        // send the request to the server
        if (originalPayment == null) {
            HomeFix.getAPI().addPayment(TradesmanController.getToken(), newPayment).enqueue(callback);
        } else {
            HomeFix.getAPI().updatePayment(TradesmanController.getToken(), originalPayment.getId(), newPayment).enqueue(callback);
        }

        return true;
    }

    private void updatePaymentInAdapter(Payment originalPayment, Payment newPayment) {
        if (mAdapter == null) return;

        // update the Charge in the adapter
        for (int i = 0, len = mAdapter.getCount(); i < len; i++) {
            if (mAdapter.getItem(i).equals(originalPayment)) {
                mAdapter.remove(originalPayment);
                mAdapter.insert(newPayment, i);
                return;
            }
        }

        // else if it was not originally in the adapter, add it to the end
        mAdapter.add(newPayment);
    }

    public void setService(Service service) {
        this.service = service;
    }

}
