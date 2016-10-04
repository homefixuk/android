package com.homefix.tradesman.timeslot.base_timeslot;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import com.homefix.tradesman.timeslot.TimeslotActivity;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.TimeUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by samuel on 7/13/2016.
 */

public class BaseTimeslotFragment<A extends TimeslotActivity, V extends BaseTimeslotView, P extends BaseTimeslotFragmentPresenter<V>>
        extends BaseCloseFragment<A, V, P>
        implements BaseTimeslotView {

    protected Timeslot.TYPE mType;
    protected boolean isEdit = false, hasMadeChanges = false, didMakeChanges = false;
    protected Timeslot mTimeslot;

    @BindView(R.id.icon)
    protected ImageView mIcon;

    @BindView(R.id.start_date)
    protected TextView mStartDateTxt;

    @BindView(R.id.start_time)
    protected TextView mStartTimeTxt;

    @BindView(R.id.end_date)
    protected TextView mEndDateTxt;

    @BindView(R.id.end_time)
    protected TextView mEndTimeTxt;

    @BindView(R.id.save)
    protected TextView mSaveTxt;

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
        if (getActivity() != null) {
            getActivity().supportInvalidateOptionsMenu();
            getActivity().invalidateOptionsMenu();
        }
    }

    public void setStartInEditMode(boolean startInEditMode) {
        isEdit = startInEditMode;
    }

    public void setupView() {
        if (mIcon != null) {
            if (mType == Timeslot.TYPE.BREAK)
                mIcon.setImageResource(R.drawable.ic_food_grey600_48dp);
            else mIcon.setImageResource(R.drawable.ic_av_timer_grey600_48dp);
        }

        mStartCal = TimeUtils.setToTopOfHour(mStartCal);
        mEndCal = TimeUtils.setToTopOfHour(mEndCal);

        if (mTimeslot != null) {
            mStartCal.setTimeInMillis(mTimeslot.getStartTime());
            mEndCal.setTimeInMillis(mTimeslot.getEndTime());

        } else if (mEndCal.getTimeInMillis() < mStartCal.getTimeInMillis() + TimeUtils.getHoursInMillis(1)) {
            mEndCal.setTimeInMillis(mStartCal.getTimeInMillis() + TimeUtils.getHoursInMillis(1));
        }

        setStartTime(mStartCal);
        setEndTime(mEndCal);

        if (mSaveTxt != null) {
            mSaveTxt.setText(!isEdit ? "DONE" : (mTimeslot != null ? "SAVE" : "ADD"));
        }

        hasMadeChanges = false;
    }

    @Override
    public void setTimeslot(Timeslot timeslot) {
        this.mTimeslot = timeslot;

        if (mTimeslot != null) mType = Timeslot.TYPE.getTypeEnum(mTimeslot.getType());
    }

    private boolean isRefreshingService = false;

    public void refreshService() {
        if (isRefreshingService) return;
        isRefreshingService = true;

//        final Service service = mTimeslot != null ? mTimeslot.getService() : null;
//        // if there is no timeslot just refresh the view
//        if (service == null) {
//            setupView();
//            return;
//        }
//
//        HomeFix
//                .getAPI()
//                .getService(TradesmanController.getToken(), service.getId())
//                .enqueue(new Callback<Service>() {
//                    @Override
//                    public void onResponse(Call<Service> call, Response<Service> response) {
//                        Service service1 = response != null ? response.body() : null;
//                        if (service1 == null || service1.isEmpty()) {
//                            onFailure(call, null);
//                            return;
//                        }
//
//                        if (mTimeslot != null) {
//                            // save the service into the Timeslot and refresh the view
//                            mTimeslot.setService(service1);
//                            setupView();
//                        }
//
//                        isRefreshingService = false;
//                    }
//
//                    @Override
//                    public void onFailure(Call<Service> call, Throwable t) {
//                        MyLog.e(TAG, "Error refreshing Service");
//                        if (t != null && MyLog.isIsLogEnabled()) t.printStackTrace();
//
//                        isRefreshingService = false;
//                    }
//                });
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
    public void setStartTime(@NonNull Calendar startTime) {
        long currentLength = 0L;
        if (mEndCal != null && mStartCal != null) {
            currentLength = mEndCal.getTimeInMillis() - mStartCal.getTimeInMillis();
        }

        mStartCal = startTime;

        hasMadeChanges = true;

        if (mStartDateTxt != null) mStartDateTxt.setText(TimeUtils.getShortDateString(mStartCal));
        if (mStartTimeTxt != null) mStartTimeTxt.setText(TimeUtils.getShortTimeString(mStartCal));

        // if the newly selected start date is after the end date
        if (mEndCal == null || mStartCal.getTimeInMillis() > mEndCal.getTimeInMillis()) {
            // automatically set the end time after the new start time
            mEndCal = Calendar.getInstance();
            mEndCal.setTimeInMillis(mStartCal.getTimeInMillis() + (currentLength > 0 ? currentLength : TimeUtils.getHoursInMillis(1)));
            if (mEndDateTxt != null) mEndDateTxt.setText(TimeUtils.getShortDateString(mEndCal));
            if (mEndTimeTxt != null) mEndTimeTxt.setText(TimeUtils.getShortTimeString(mEndCal));
        }
    }

    @Override
    public boolean hasMadeChanges() {
        return hasMadeChanges;
    }

    @Override
    public boolean didMakeChanges() {
        return didMakeChanges;
    }

    @Override
    public void setEndTime(Calendar endTime) {
        // if the newly selected end date is before the start date
        if (mStartCal != null && endTime.getTimeInMillis() < mStartCal.getTimeInMillis()) {
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
    public void onSaveComplete(Timeslot timeslot) {
        hideDialog();

        hasMadeChanges = false;
        didMakeChanges = true;
        isEdit = false;
        if (timeslot != null) setTimeslot(timeslot); // update the timeslot
        if (getBaseActivity() != null) {
            getBaseActivity().supportInvalidateOptionsMenu();
            getBaseActivity().invalidateOptionsMenu();
        }
        setupView();
    }

    @Override
    public void onDeleteClicked() {
        getPresenter().delete(mTimeslot);
    }

    @Override
    public void onDeleteComplete(Timeslot timeslot) {
        hideDialog();

        if (timeslot == null) {
            getBaseActivity().finishWithAnimation();
            return;
        }

        Timeslot.getSenderReceiver().put(timeslot.getId(), timeslot);
        Intent data = new Intent();
        data.putExtra("timeslotId", timeslot.getId());
        data.putExtra("action", "deleted");
        getBaseActivity().finishWithIntentAndAnimation(data);
    }

    @Override
    public boolean canClose() {
        return !hasMadeChanges;
    }

    @Override
    public void onCloseClicked() {
        final Intent data = getCloseIntent(null);

        if (!hasMadeChanges) {
            if (getBaseActivity() != null) getBaseActivity().finishWithIntentAndAnimation(data);
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
                        if (getBaseActivity() != null)
                            getBaseActivity().finishWithIntentAndAnimation(data);

                    }
                }, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (dialog != null) dialog.dismiss();
                    }
                }).show();
    }

    private Intent getCloseIntent(String action) {
        Intent data = new Intent();

        if (mTimeslot != null) {
            Timeslot.getSenderReceiver().put(mTimeslot.getId(), mTimeslot);
            data.putExtra("timeslotId", mTimeslot.getId());
            if ("deleted".equals(action)) data.putExtra("deleted", true);
        }

        return data;
    }

    @OnClick(R.id.start_date)
    public void onStartDateClicked() {
        if (!isEdit) return;

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

    @OnClick(R.id.start_time)
    public void onStartTimeClicked() {
        if (!isEdit) return;

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

    @OnClick(R.id.end_date)
    public void onEndDateClicked() {
        if (!isEdit) return;

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

    @OnClick(R.id.end_time)
    public void onEndTimeClicked() {
        if (!isEdit) return;

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

    @Override
    @OnClick(R.id.save)
    public void saveClicked() {
        if (!isEdit) {
            getBaseActivity().tryClose();
            return;
        }

        getPresenter().save(mTimeslot, mStartCal, mEndCal);
    }

}
