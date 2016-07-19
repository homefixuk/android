package com.homefix.tradesman.availability;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.homefix.tradesman.R;
import com.homefix.tradesman.base.fragment.BaseCloseFragment;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.TimeUtils;

import java.util.Calendar;

/**
 * Created by samuel on 7/13/2016.
 */

public class AvailabilityFragment extends BaseCloseFragment<AvailabilityActivity, AvailabilityView, AvailabilityPresenter> implements AvailabilityView {

    private boolean isEdit = false, hasMadeChanges = false;
    private Timeslot mTimeslot;

    private TextView mStartDate, mStartTime, mEndDate, mEndTime, mSave;
    private Calendar mStart, mEnd;

    public AvailabilityFragment() {
        super(AvailabilityFragment.class.getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected AvailabilityPresenter getPresenter() {
        if (presenter == null) presenter = new AvailabilityPresenter(this);

        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_availability;
    }

    @Override
    protected void injectDependencies() {
        super.injectDependencies();

        View view = getView();

        if (view == null) return;

        mStartDate = (TextView) view.findViewById(R.id.start_date);
        mStartTime = (TextView) view.findViewById(R.id.start_time);
        mEndDate = (TextView) view.findViewById(R.id.end_date);
        mEndTime = (TextView) view.findViewById(R.id.end_time);
        mSave = (TextView) view.findViewById(R.id.save);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mStart = Calendar.getInstance();
        mStart.set(Calendar.MINUTE, 0);
        mStart.set(Calendar.SECOND, 0);
        mStart.set(Calendar.MILLISECOND, 0);

        mEnd = Calendar.getInstance();
        mEnd.set(Calendar.MINUTE, 0);
        mEnd.set(Calendar.SECOND, 0);
        mEnd.set(Calendar.MILLISECOND, 0);

        if (mTimeslot != null) {
            // make sure if there is a time slot that it is an availability one
            if (Timeslot.TYPE.getTypeEnum(mTimeslot.getType()) != Timeslot.TYPE.AVAILABILITY) {
                Toast.makeText(getContext(), "Sorry, unable to edit this timeslot.", Toast.LENGTH_SHORT).show();
                getBaseActivity().finishWithAnimation();
                return;
            }

            mStart.setTimeInMillis(mTimeslot.getStart());
            mEnd.setTimeInMillis(mTimeslot.getEnd());

        } else {
            mEnd.add(Calendar.MINUTE, 30);
        }

        setStartTime(mStart);
        setEndTime(mEnd);

        hasMadeChanges = false;

        if (mStartDate != null) {
            mStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(
                            getContext(),
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    Calendar calNew = (Calendar) mStart.clone();
                                    calNew.set(Calendar.YEAR, year);
                                    calNew.set(Calendar.MONTH, monthOfYear);
                                    calNew.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    setStartTime(calNew);
                                }

                            },
                            mStart.get(Calendar.YEAR),
                            mStart.get(Calendar.MONTH),
                            mStart.get(Calendar.DAY_OF_MONTH)

                    ).show();
                }
            });
        }

        if (mStartTime != null) {
            mStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePickerDialog(
                            getContext(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    Calendar calNew = (Calendar) mStart.clone();
                                    calNew.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calNew.set(Calendar.MINUTE, minute);
                                    setStartTime(calNew);
                                }

                            },
                            mStart.get(Calendar.HOUR_OF_DAY),
                            mStart.get(Calendar.MINUTE),
                            true)
                            .show();
                }
            });
        }

        if (mEndDate != null) {
            mEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(
                            getContext(),
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    Calendar calNew = (Calendar) mEnd.clone();
                                    calNew.set(Calendar.YEAR, year);
                                    calNew.set(Calendar.MONTH, monthOfYear);
                                    calNew.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    setEndTime(calNew);
                                }

                            },
                            mEnd.get(Calendar.YEAR),
                            mEnd.get(Calendar.MONTH),
                            mEnd.get(Calendar.DAY_OF_MONTH)

                    ).show();
                }
            });
        }

        if (mEndTime != null) {
            mEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePickerDialog(
                            getContext(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    Calendar calNew = (Calendar) mEnd.clone();
                                    calNew.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calNew.set(Calendar.MINUTE, minute);
                                    setEndTime(calNew);
                                }

                            },
                            mEnd.get(Calendar.HOUR_OF_DAY),
                            mEnd.get(Calendar.MINUTE),
                            true)
                            .show();
                }
            });
        }

        if (mSave != null) {
            mSave.setText(isEdit ? "SAVE" : "ADD");
            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveCliked();
                }
            });
        }
    }

    @Override
    public void setTimeslot(Timeslot mTimeslot) {
        this.mTimeslot = mTimeslot;

        // set the edit mode if we have a time slot
        isEdit = this.mTimeslot != null;
    }

    @Override
    public Timeslot getTimeslot() {
        return mTimeslot;
    }

    @Override
    public void setStartTime(Calendar startTime) {
        // if the newly selected start date is after the start date
        if (startTime.getTimeInMillis() > mEnd.getTimeInMillis()) {
            getBaseActivity().showDialog("End date cannot be before start date", false);
            return;
        }

        mStart = startTime;
        hasMadeChanges = true;

        if (mStartDate != null) mStartDate.setText(TimeUtils.getShortDateString(mStart));
        if (mStartTime != null) mStartTime.setText(TimeUtils.getShortTimeString(mStart));
    }

    @Override
    public void setEndTime(Calendar endTime) {
        // if the newly selected end date is before the start date
        if (endTime.getTimeInMillis() < mStart.getTimeInMillis()) {
            getBaseActivity().showDialog("End date cannot be before start date", false);
            return;
        }

        mEnd = endTime;
        hasMadeChanges = true;

        if (mEndDate != null) mEndDate.setText(TimeUtils.getShortDateString(mEnd));
        if (mEndTime != null) mEndTime.setText(TimeUtils.getShortTimeString(mEnd));
    }

    @Override
    public Calendar getStartTime() {
        return mStart;
    }

    @Override
    public Calendar getEndTime() {
        return mEnd;
    }

    @Override
    public void saveCliked() {
        getPresenter().save(mTimeslot, mStart, mEnd);
    }

    @Override
    public void onSaveComplete(Timeslot timeslot) {
        hideDialog();

        getBaseActivity().finishWithAnimation();
    }

    @Override
    public void onDeleteClicked() {
        getPresenter().delete(mTimeslot);
    }

    @Override
    public void onDeleteComplete() {
        hideDialog();

        getBaseActivity().finishWithAnimation();
    }

    @Override
    public boolean canClose() {
        return !hasMadeChanges;
    }

    @Override
    public void onCloseClicked() {
        if (!hasMadeChanges) {
            getBaseActivity().finishWithAnimation();
            return;
        }

        // show a confirm cancel dialog
        MaterialDialogWrapper.getConfirmationDialog(
                getBaseActivity(),
                "Are you sure you want to discard the changes you've made?",
                "DISCARD CHANGES",
                "CANCEL",
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getBaseActivity().finishWithAnimation();
                    }
                }, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (dialog != null) dialog.dismiss();
                    }
                }).show();
    }
}
