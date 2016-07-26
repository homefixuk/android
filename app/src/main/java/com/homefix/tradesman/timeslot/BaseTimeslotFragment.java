package com.homefix.tradesman.timeslot;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

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

public class BaseTimeslotFragment<A extends TimeslotActivity, V extends BaseTimeslotView, P extends BaseTimeslotFragmentPresenter<V>> extends BaseCloseFragment<A, V, P> implements BaseTimeslotView {

    protected Timeslot.TYPE mType;
    protected boolean isEdit = false, hasMadeChanges = false;
    protected Timeslot mTimeslot;

    protected ImageView mIcon;
    protected TextView mStartDateTxt, mStartTimeTxt, mEndDateTxt, mEndTimeTxt, mSaveTxt;
    protected Calendar mStartCal, mEndCal;

    public BaseTimeslotFragment() {
        super(BaseTimeslotFragment.class.getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected P getPresenter() {
        if (presenter == null) presenter = (P) new BaseTimeslotFragmentPresenter(this, mType);

        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_timeslot;
    }

    @Override
    protected void injectDependencies() {
        super.injectDependencies();

        View view = getView();

        if (view == null) return;

        mIcon = (ImageView) view.findViewById(R.id.icon);
        mStartDateTxt = (TextView) view.findViewById(R.id.start_date);
        mStartTimeTxt = (TextView) view.findViewById(R.id.start_time);
        mEndDateTxt = (TextView) view.findViewById(R.id.end_date);
        mEndTimeTxt = (TextView) view.findViewById(R.id.end_time);
        mSaveTxt = (TextView) view.findViewById(R.id.save);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // if there is no timeslot to make changes to, we are in edit mode for the new timeslot
        if (mTimeslot == null) isEdit = true;

        setupView();
    }

    public boolean isEditing() {
        return isEdit;
    }

    public void setEditing(boolean edit) {
        isEdit = edit;
        setupView();
        getActivity().supportInvalidateOptionsMenu();
        getActivity().invalidateOptionsMenu();
    }

    public void setupView() {
        if (mIcon != null) {
            if (mType == Timeslot.TYPE.BREAK)
                mIcon.setImageResource(R.drawable.ic_food_grey600_48dp);
            else mIcon.setImageResource(R.drawable.ic_av_timer_grey600_48dp);
        }

        mStartCal = Calendar.getInstance();
        mStartCal.set(Calendar.MINUTE, 0);
        mStartCal.set(Calendar.SECOND, 0);
        mStartCal.set(Calendar.MILLISECOND, 0);

        mEndCal = Calendar.getInstance();
        mEndCal.set(Calendar.MINUTE, 0);
        mEndCal.set(Calendar.SECOND, 0);
        mEndCal.set(Calendar.MILLISECOND, 0);

        if (mTimeslot != null) {
            mStartCal.setTimeInMillis(mTimeslot.getStart());
            mEndCal.setTimeInMillis(mTimeslot.getEnd());

        } else {
            mEndCal.add(Calendar.MINUTE, 30);
        }

        setStartTime(mStartCal);
        setEndTime(mEndCal);

        hasMadeChanges = false;

        // if not in edit mode
        if (!isEdit) {
            mStartDateTxt.setOnClickListener(null);
            mStartTimeTxt.setOnClickListener(null);
            mEndDateTxt.setOnClickListener(null);
            mEndTimeTxt.setOnClickListener(null);

            mSaveTxt.setText("DONE");
            mSaveTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getBaseActivity().tryClose();
                }
            });

            return;
        }

        // if in edit mode
        if (mStartDateTxt != null) {
            mStartDateTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(
                            getContext(),
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    Calendar calNew = (Calendar) mStartCal.clone();
                                    calNew.set(Calendar.YEAR, year);
                                    calNew.set(Calendar.MONTH, monthOfYear);
                                    calNew.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    setStartTime(calNew);
                                }

                            },
                            mStartCal.get(Calendar.YEAR),
                            mStartCal.get(Calendar.MONTH),
                            mStartCal.get(Calendar.DAY_OF_MONTH)

                    ).show();
                }
            });
        }

        if (mStartTimeTxt != null) {
            mStartTimeTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePickerDialog(
                            getContext(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    Calendar calNew = (Calendar) mStartCal.clone();
                                    calNew.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calNew.set(Calendar.MINUTE, minute);
                                    setStartTime(calNew);
                                }

                            },
                            mStartCal.get(Calendar.HOUR_OF_DAY),
                            mStartCal.get(Calendar.MINUTE),
                            true)
                            .show();
                }
            });
        }

        if (mEndDateTxt != null) {
            mEndDateTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(
                            getContext(),
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    Calendar calNew = (Calendar) mEndCal.clone();
                                    calNew.set(Calendar.YEAR, year);
                                    calNew.set(Calendar.MONTH, monthOfYear);
                                    calNew.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    setEndTime(calNew);
                                }

                            },
                            mEndCal.get(Calendar.YEAR),
                            mEndCal.get(Calendar.MONTH),
                            mEndCal.get(Calendar.DAY_OF_MONTH)

                    ).show();
                }
            });
        }

        if (mEndTimeTxt != null) {
            mEndTimeTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePickerDialog(
                            getContext(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    Calendar calNew = (Calendar) mEndCal.clone();
                                    calNew.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calNew.set(Calendar.MINUTE, minute);
                                    setEndTime(calNew);
                                }

                            },
                            mEndCal.get(Calendar.HOUR_OF_DAY),
                            mEndCal.get(Calendar.MINUTE),
                            true)
                            .show();
                }
            });
        }

        if (mSaveTxt != null) {
            mSaveTxt.setText(isEdit ? "SAVE" : "ADD");
            mSaveTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveClicked();
                }
            });
        }
    }

    @Override
    public void setTimeslot(Timeslot timeslot) {
        this.mTimeslot = timeslot;

        if (mTimeslot != null) mType = Timeslot.TYPE.getTypeEnum(mTimeslot.getType());
    }

    public void setType(Timeslot.TYPE type) {
        mType = type;
    }

    public Timeslot.TYPE getType() {
        return mType;
    }

    @Override
    public Timeslot getTimeslot() {
        return mTimeslot;
    }

    @Override
    public void setStartTime(Calendar startTime) {
        // if the newly selected start date is after the start date
        if (startTime.getTimeInMillis() > mEndCal.getTimeInMillis()) {
            getBaseActivity().showDialog("End date cannot be before start date", false);
            return;
        }

        mStartCal = startTime;
        hasMadeChanges = true;

        if (mStartDateTxt != null) mStartDateTxt.setText(TimeUtils.getShortDateString(mStartCal));
        if (mStartTimeTxt != null) mStartTimeTxt.setText(TimeUtils.getShortTimeString(mStartCal));
    }

    @Override
    public void setEndTime(Calendar endTime) {
        // if the newly selected end date is before the start date
        if (endTime.getTimeInMillis() < mStartCal.getTimeInMillis()) {
            getBaseActivity().showDialog("End date cannot be before start date", false);
            return;
        }

        mEndCal = endTime;
        hasMadeChanges = true;

        if (mEndDateTxt != null) mEndDateTxt.setText(TimeUtils.getShortDateString(mEndCal));
        if (mEndTimeTxt != null) mEndTimeTxt.setText(TimeUtils.getShortTimeString(mEndCal));
    }

    @Override
    public Calendar getStartTime() {
        return mStartCal;
    }

    @Override
    public Calendar getEndTime() {
        return mEndCal;
    }

    @Override
    public void saveClicked() {
        getPresenter().save(mTimeslot, mStartCal, mEndCal);
    }

    @Override
    public void onSaveComplete(Timeslot timeslot) {
        hideDialog();

        hasMadeChanges = false;
        isEdit = false;
        getBaseActivity().supportInvalidateOptionsMenu();
        setupView();
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
