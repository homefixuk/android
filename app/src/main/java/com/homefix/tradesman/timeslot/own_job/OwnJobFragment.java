package com.homefix.tradesman.timeslot.own_job;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.homefix.tradesman.R;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.model.ServiceType;
import com.homefix.tradesman.timeslot.BaseTimeslotFragment;
import com.homefix.tradesman.timeslot.TimeslotActivity;
import com.homefix.tradesman.view.MaterialDialogWrapper;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by samuel on 7/19/2016.
 */

public class OwnJobFragment extends BaseTimeslotFragment<TimeslotActivity, OwnJobView, OwnJobPresenter> implements OwnJobView {

    protected TextView mJobTypeTxt, mLocationTxt;
    protected EditText mPersonNameTxt, mPersonEmailTxt, mPersonPhoneNumberTxt, mDescriptionTxt;
    protected Place mLocationPlace;

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

        mJobTypeTxt = (TextView) view.findViewById(R.id.job_type_txt);
        mLocationTxt = (TextView) view.findViewById(R.id.location_txt);
        mPersonNameTxt = (EditText) view.findViewById(R.id.person_name_txt);
        mPersonEmailTxt = (EditText) view.findViewById(R.id.person_email_txt);
        mPersonPhoneNumberTxt = (EditText) view.findViewById(R.id.person_phone_number_txt);
        mDescriptionTxt = (EditText) view.findViewById(R.id.description_txt);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mJobTypeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show list of service type names
                List<String> namesList = ServiceType.getServiceTypeNames();
                CharSequence[] array = namesList.toArray(new String[namesList.size()]);

                MaterialDialogWrapper.getListDialog(getActivity(), "Select service type", array, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        hasMadeChanges = true;

                        // set the job type text from the one they selected
                        mJobTypeTxt.setText(text);
                    }
                }).show();
            }
        });

        mLocationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlacePicker();
            }
        });
    }

    /**
     * Show the google place picker activity
     */
    private void showPlacePicker() {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(getActivity());
            startActivityForResult(intent, Ids.PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), getActivity(), 0);
        } catch (Exception e) {
            showManualLocationInput();
        }
    }

    private void showManualLocationInput() {
        // TODO
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Ids.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                hasMadeChanges = true;

                // store the place
                mLocationPlace = PlacePicker.getPlace(getContext(), data);
                CharSequence address = mLocationPlace.getAddress();

                // show the address
                mLocationTxt.setText(address);

            } else {
                MaterialDialogWrapper.getNegativeConfirmationDialog(
                        getActivity(),
                        "Sorry, unable to get the location information. Would you like to try again? Or enter the details manually?",
                        "TRY AGAIN",
                        "MANUAL INPUT",
                        new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (dialog != null) dialog.dismiss();

                                showPlacePicker();
                            }
                        },
                        new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (dialog != null) dialog.dismiss();

                                showManualLocationInput();
                            }
                        });
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
