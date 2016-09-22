package com.homefix.tradesman.profile.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.homefix.tradesman.R;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.base.activity.BaseToolbarActivity;
import com.homefix.tradesman.base.adapter.MyListAdapter;
import com.homefix.tradesman.base.fragment.BaseCloseFragment;
import com.homefix.tradesman.data.TradesmanController;
import com.homefix.tradesman.model.TradesmanPrivate;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.MyLog;
import com.samdroid.listener.BackgroundColourOnTouchListener;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by samuel on 9/1/2016.
 */

public class SettingsFragment<A extends BaseToolbarActivity> extends BaseCloseFragment<A, SettingsView, SettingsPresenter> implements SettingsView {

    @BindView(R.id.list)
    protected ListView mListView;

    protected ArrayAdapter<String> mAdapter;

    protected TradesmanPrivate tradesmanPrivate;

    public SettingsFragment() {
        super(SettingsFragment.class.getSimpleName());
    }

    @Override
    protected SettingsPresenter getPresenter() {
        if (presenter == null) presenter = new SettingsPresenter(this);

        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_profile_settings;
    }

    public List<String> getSettingsOptions() {
        return Arrays.asList("Business", "Bank Account", "VAT Number", "Standard Hourly Rate");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new MyListAdapter<String>(getActivity(), "SettingsAdapter", android.R.layout.simple_list_item_2, getSettingsOptions()) {

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = getActivity().getLayoutInflater().inflate(android.R.layout.simple_list_item_2, parent, false);

                final String option = Strings.returnSafely(getItem(position));

                // set the name of the option
                TextView text1 = ButterKnife.findById(view, android.R.id.text1);
                text1.setText(option);
                text1.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));

                // set the data already retrieved for the option
                TextView text2 = ButterKnife.findById(view, android.R.id.text2);
                switch (option) {

                    case "Business":
                        if (tradesmanPrivate == null) break;

                        text2.setText(tradesmanPrivate.getBusinessName());
                        break;

                    case "Bank Account":
                        if (tradesmanPrivate == null) break;

                        text2.setText(tradesmanPrivate.getAccountName());
                        break;

                    case "VAT Number":
                        if (tradesmanPrivate == null) break;

                        text2.setText(tradesmanPrivate.getVatNumber());
                        break;

                    case "Standard Hourly Rate":
                        if (tradesmanPrivate == null) break;

                        text2.setText(String.format("£%s", Strings.formatRaised(tradesmanPrivate.getStandardHourlyRate())));
                        break;
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (option) {

                            case "Business":
                                onBusinessClicked();
                                break;

                            case "Bank Account":
                                onBankAccountClicked();
                                break;

                            case "VAT Number":
                                onVatNumberClicked();
                                break;

                            case "Standard Hourly Rate":
                                onStandardHourlyRateClicked();
                                break;

                        }
                    }
                });

                view.setOnTouchListener(new BackgroundColourOnTouchListener(getContext(), R.color.transparent, R.color.colorPrimaryLight));

                return view;
            }
        };
        mListView.setAdapter(mAdapter);

        TradesmanController.loadTradesmanPrivate(getContext(), new OnGotObjectListener<TradesmanPrivate>() {
            @Override
            public void onGotThing(TradesmanPrivate o) {
                tradesmanPrivate = o;

                if (mAdapter != null) mAdapter.notifyDataSetChanged();
            }
        });
    }

    private final Callback<TradesmanPrivate> privateCallback = new Callback<TradesmanPrivate>() {
        @Override
        public void onResponse(Call<TradesmanPrivate> call, Response<TradesmanPrivate> response) {
            TradesmanPrivate tp = response != null ? response.body() : null;
            if (tp == null) {
                showErrorDialog();
                return;
            }

            tradesmanPrivate = tp;
            if (mAdapter != null) mAdapter.notifyDataSetChanged();
            hideDialog();
        }

        @Override
        public void onFailure(Call<TradesmanPrivate> call, Throwable t) {
            if (t != null) {
                if (MyLog.isIsLogEnabled()) t.printStackTrace();

                MyLog.e(TAG, t.getMessage());
            }

            showErrorDialog();
        }
    };

    private void onStandardHourlyRateClicked() {
        MaterialDialogWrapper.getEditTextDialog(
                getActivity(),
                "Update your standard hourly rate",
                tradesmanPrivate != null ? String.valueOf(tradesmanPrivate.getStandardHourlyRate()) : "",
                "Standard Hourly Rate (£)",
                "SAVE",
                InputType.TYPE_CLASS_NUMBER,
                new MaterialDialogWrapper.SubmitObjectChangesCallback() {
                    @Override
                    public void onChangeSubmitted(Object original, Object changed) {
                        // if there was no change, do nothing
                        if ((original != null && original.equals(changed))
                                || (original == null && changed == null)) return;

                        Double d = null;
                        try {
                            String value = Strings.returnSafely(String.valueOf(changed));
                            d = Double.valueOf(value);

                            if (Strings.isEmpty(value)) throw new Exception();

                        } catch (Exception e) {
                        }

                        if (d == null || d <= 0) {
                            showConfirmDialog(
                                    "Sorry, please enter a valid amount",
                                    "OK",
                                    "CANCEL",
                                    new ConfirmDialogCallback() {
                                        @Override
                                        public void onPositive() {
                                            onStandardHourlyRateClicked();
                                        }
                                    });
                            return;
                        }

                        showDialog("Saving hourly rate...", true);

                        Map<String, Object> changes = new HashMap<>();
                        changes.put("standardHourlyRate", d);
                        HomeFix.getAPI()
                                .updateTradesmanPrivateDetails(
                                        TradesmanController.getToken(),
                                        getString(HomeFix.API_KEY_resId),
                                        changes)
                                .enqueue(privateCallback);
                    }

                    @Override
                    public void onChangeCancelled(Object original) {
                    }

                }).show();
    }

    private void onVatNumberClicked() {
        final String originalVatNumber = tradesmanPrivate != null ? tradesmanPrivate.getVatNumber() : "";

        MaterialDialogWrapper.getEditTextDialog(
                getActivity(),
                null,
                originalVatNumber,
                "VAT Number",
                "SAVE",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS,
                new MaterialDialogWrapper.SubmitObjectChangesCallback() {
                    @Override
                    public void onChangeSubmitted(Object original, Object changed) {
                        // if there was no change, do nothing
                        if ((original != null && original.equals(changed))
                                || (original == null && changed == null)) return;

                        String s = Strings.returnSafely(String.valueOf(changed));

                        if (Strings.isEmpty(originalVatNumber) && Strings.isEmpty(s)) {
                            showConfirmDialog(
                                    "Sorry, please enter a valid VAT number",
                                    "OK",
                                    "CANCEL",
                                    new ConfirmDialogCallback() {
                                        @Override
                                        public void onPositive() {
                                            onVatNumberClicked();
                                        }
                                    });
                            return;
                        }

                        showDialog("Saving VAT Number...", true);

                        Map<String, Object> changes = new HashMap<>();
                        changes.put("vatNumber", s);
                        HomeFix.getAPI()
                                .updateTradesmanPrivateDetails(
                                        TradesmanController.getToken(),
                                        getString(HomeFix.API_KEY_resId),
                                        changes)
                                .enqueue(privateCallback);
                    }

                    @Override
                    public void onChangeCancelled(Object original) {
                    }

                }).show();
    }

    private void onBankAccountClicked() {
        if (tradesmanPrivate == null) {
            Toast.makeText(getContext(), "Sorry, unable to update your bank account right now", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> keys = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> values = new ArrayList<>();

        keys.add("accountName");
        labels.add("Account Name");
        values.add(tradesmanPrivate.getAccountName());

        keys.add("accountNumber");
        labels.add("Account Number");
        values.add(tradesmanPrivate.getAccountNumber());

        keys.add("sortCode");
        labels.add("Sort-code");
        values.add(tradesmanPrivate.getSortCode());

        keys.add("nameOnAccount");
        labels.add("Name On Account");
        values.add(tradesmanPrivate.getNameOnAccount());

        MaterialDialogWrapper.getMultiInputDialogWithLabels(
                getActivity(),
                "Your bank details",
                "SAVE",
                keys,
                labels,
                values,
                new OnGotObjectListener<HashMap<String, String>>() {
                    @Override
                    public void onGotThing(HashMap<String, String> newValues) {
                        if (newValues == null || newValues.isEmpty()) {
                            Toast.makeText(getContext(), "Sorry, something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // put all the changes into a object map
                        Map<String, Object> changes = new HashMap<>();
                        Set<String> keys = newValues.keySet();
                        for (String key : keys) {
                            if (Strings.isEmpty(key)) continue;
                            changes.put(key, newValues.get(key));
                        }

                        showDialog("Saving bank account info...", true);

                        // send the updates to the server
                        HomeFix.getAPI()
                                .updateTradesmanPrivateDetails(
                                        TradesmanController.getToken(),
                                        getString(HomeFix.API_KEY_resId),
                                        changes)
                                .enqueue(privateCallback);
                    }
                }

        ).show();
    }

    private void onBusinessClicked() {
        final String originalBusinessName = tradesmanPrivate != null ? tradesmanPrivate.getBusinessName() : "";

        MaterialDialogWrapper.getEditTextDialog(
                getActivity(),
                null,
                originalBusinessName,
                "Business Name (will appear on invoices)",
                "SAVE",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS,
                new MaterialDialogWrapper.SubmitObjectChangesCallback() {
                    @Override
                    public void onChangeSubmitted(Object original, Object changed) {
                        // if there was no change, do nothing
                        if ((original != null && original.equals(changed))
                                || (original == null && changed == null)) return;

                        String s = Strings.returnSafely(String.valueOf(changed));

                        if (Strings.isEmpty(originalBusinessName) && Strings.isEmpty(s)) {
                            showConfirmDialog(
                                    "Sorry, please enter a valid business name",
                                    "OK",
                                    "CANCEL",
                                    new ConfirmDialogCallback() {
                                        @Override
                                        public void onPositive() {
                                            onBusinessClicked();
                                        }
                                    });
                            return;
                        }

                        showDialog("Saving business name...", true);

                        Map<String, Object> changes = new HashMap<>();
                        changes.put("businessName", s);
                        HomeFix.getAPI()
                                .updateTradesmanPrivateDetails(
                                        TradesmanController.getToken(),
                                        getString(HomeFix.API_KEY_resId),
                                        changes)
                                .enqueue(privateCallback);
                    }

                    @Override
                    public void onChangeCancelled(Object original) {
                    }

                }).show();
    }

}
