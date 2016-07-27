package com.homefix.tradesman.timeslot.own_job;

import android.view.View;
import android.widget.Toast;

import com.homefix.tradesman.R;
import com.homefix.tradesman.timeslot.base_service.BaseServiceFragment;
import com.homefix.tradesman.timeslot.base_service.BaseServiceView;

/**
 * Created by samuel on 7/19/2016.
 */

public class OwnJobFragment extends BaseServiceFragment<OwnJobPresenter> implements BaseServiceView {

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
