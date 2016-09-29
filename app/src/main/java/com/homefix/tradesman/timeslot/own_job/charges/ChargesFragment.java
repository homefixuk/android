package com.homefix.tradesman.timeslot.own_job.charges;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.homefix.tradesman.model.Charge;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceSet;
import com.homefix.tradesman.view.MaterialDialogWrapper;
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

public class ChargesFragment extends BaseCloseFragment<ChargesActivity, BaseFragmentView, BaseFragmentPresenter<BaseFragmentView>> {

    private Service service;

    @BindView(R.id.amount)
    protected TextView mTotalCost;

    @BindView(R.id.list)
    protected ListView mListView;

    private ArrayAdapter<Charge> mAdapter;

    public ChargesFragment() {
        super(ChargesFragment.class.getSimpleName());
    }

    @Override
    protected BaseFragmentPresenter getPresenter() {
        if (presenter == null) presenter = new DefaultFragementPresenter(this);

        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.charges_layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ServiceSet serviceSet = service != null ? service.getServiceSet() : null;

        if (mTotalCost != null && serviceSet != null)
            mTotalCost.setText(String.format("£%s", Strings.priceToString(serviceSet.getTotalCost())));

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<Charge>(getActivity(), R.layout.charges_item_layout) {

                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    View view = convertView;

                    if (view == null) {
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        view = inflater.inflate(R.layout.charges_item_layout, parent, false);
                    }

                    TextView mLbl = ButterKnife.findById(view, R.id.label);
                    TextView mAmount = ButterKnife.findById(view, R.id.amount);
                    TextView mQuantity = ButterKnife.findById(view, R.id.quantity);

                    final Charge charge = getItem(position);

                    mLbl.setText(charge.getDescription());
                    mAmount.setText(String.format("£%s", Strings.priceToString(charge.getAmount())));
                    mQuantity.setText(String.format("%s", Strings.priceToString(charge.getQuantity())));

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showEditCharge(charge);
                        }
                    });
                    view.setOnTouchListener(new BackgroundColourOnTouchListener(getContext(), R.color.transparent, R.color.light_grey));

                    // show long touch listener to ask if the user wants to delete the charge
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            MaterialDialogWrapper.getNegativeConfirmationDialog(
                                    getActivity(),
                                    "Would you like to delete this charge?",
                                    "DELETE",
                                    "CANCEL",
                                    new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            // call API to delete charge
                                            removeChargeClicked(charge);
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

            List<Charge> chargeList = serviceSet != null ? serviceSet.getCharges() : null;
            if (chargeList != null) {
                mAdapter.addAll(chargeList);
                mAdapter.notifyDataSetChanged();
            }
        }

        mListView.setAdapter(mAdapter);
    }

    private void removeChargeClicked(final Charge charge) {
        if (charge == null || Strings.isEmpty(charge.getId())) return;

        // show loading dialog
        showDialog("Removing Charge...", true);

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
                if (mAdapter != null) mAdapter.remove(charge);
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (BuildConfig.DEBUG && t != null) t.printStackTrace();

                hideDialog();
                Toast.makeText(getContext(), "Sorry, unable to remove the charge right now. Please try again", Toast.LENGTH_SHORT).show();
            }
        };

        HomeFix.getAPI().deleteCharge(TradesmanController.getToken(), charge.getId()).enqueue(callback);
    }

    @Override
    public boolean canClose() {
        return super.canClose();
    }

    public void addClicked() {
        showEditCharge(null);
    }

    private void showEditCharge(Charge charge) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

        final AddChargeView view = (AddChargeView) getActivity().getLayoutInflater().inflate(R.layout.add_charge_layout, null);

        final boolean isEmpty = charge == null;

        if (isEmpty) {
            charge = new Charge();
            charge.setService(service);
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
        if (view == null || newCharge == null) return false;

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

        Callback<Charge> callback = new Callback<Charge>() {
            @Override
            public void onResponse(Call<Charge> call, Response<Charge> response) {
                Charge charge1 = response.body();

                if (charge1 == null) {
                    onFailure(call, null);
                    return;
                }

                hideDialog();
                updateChargeInAdapter(originalCharge, charge1); // take the Charge returned from the server
            }

            @Override
            public void onFailure(Call<Charge> call, Throwable t) {
                if (BuildConfig.DEBUG && t != null) t.printStackTrace();

                hideDialog();
                showEditCharge(originalCharge);
                Toast.makeText(getContext(), "Sorry, unable to do this right now. Please try again", Toast.LENGTH_SHORT).show();
            }
        };

        // send the request to the server
        if (originalCharge == null) {
            HomeFix.getAPI().addCharge(TradesmanController.getToken(), newCharge.toMap()).enqueue(callback);
        } else {
            HomeFix.getAPI().updateCharge(TradesmanController.getToken(), originalCharge.getId(), newCharge.toMap()).enqueue(callback);
        }

        return true;
    }

    private void updateChargeInAdapter(Charge originalCharge, Charge newCharge) {
        if (mAdapter == null) return;

        // update the Charge in the adapter
        for (int i = 0, len = mAdapter.getCount(); i < len; i++) {
            if (mAdapter.getItem(i).equals(originalCharge)) {
                mAdapter.remove(originalCharge);
                mAdapter.insert(newCharge, i);
                return;
            }
        }

        // else if it was not originally in the adapter, add it to the end
        mAdapter.add(newCharge);
    }

    public void setService(Service service) {
        this.service = service;
    }

}
