package com.homefix.tradesman.timeslot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.BaseCloseActivity;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.model.Problem;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.timeslot.base_timeslot.BaseTimeslotFragment;
import com.homefix.tradesman.timeslot.own_job.OwnJobFragment;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.IntentHelper;
import com.samdroid.common.TimeUtils;
import com.samdroid.string.Strings;

import java.sql.Time;
import java.util.Calendar;

/**
 * Created by samuel on 7/13/2016.
 */

public class TimeslotActivity extends BaseCloseActivity {

    private boolean hasTimeslot = false;
    private Timeslot.TYPE type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String timeslotKey = IntentHelper.getStringSafely(i, "timeslotKey");
        String typeStr = IntentHelper.getStringSafely(i, "type");
        long startTime = IntentHelper.getLongSafely(i, "startTime", 0L);
        boolean goIntoEditMode = IntentHelper.getBooleanSafely(i, "goIntoEditMode", false);

        type = Timeslot.TYPE.getTypeEnum(typeStr);

        if (type == Timeslot.TYPE.OWN_JOB) baseFragment = new OwnJobFragment();
        else baseFragment = new BaseTimeslotFragment();

        // try and get the timeslot from the cache
        Timeslot timeslot = null;
        if (!Strings.isEmpty(timeslotKey)) {
            timeslot = Timeslot.getSenderReceiver().remove(timeslotKey);

            if (timeslot != null) {
                hasTimeslot = true;
                type = Timeslot.TYPE.getTypeEnum(timeslot.getType());
                ((BaseTimeslotFragment) baseFragment).setTimeslot(timeslot);
                ((BaseTimeslotFragment) baseFragment).setStartInEditMode(goIntoEditMode);
                supportInvalidateOptionsMenu();
            }
        }

        if (startTime > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(startTime);
            cal = TimeUtils.setToTopOfHour(cal);
            ((BaseTimeslotFragment) baseFragment).setStartTime(cal);
//
//            cal.setTimeInMillis(cal.getTimeInMillis() + TimeUtils.getHoursInMillis(1));
//            ((BaseTimeslotFragment) baseFragment).setEndTime(cal);
        }

        ((BaseTimeslotFragment) baseFragment).setType(type);

        updateActionBarTitle();

        // setup and show the fragment
        replaceFragment(baseFragment);
    }

    private void updateActionBarTitle() {
        if (baseFragment == null) {
            setActionbarTitle("");
            return;
        }

        Timeslot timeslot = ((BaseTimeslotFragment) baseFragment).getTimeslot();

        String title = (timeslot != null ? (((BaseTimeslotFragment) baseFragment).isEditing() ? "Edit" : "") : "Add") + " ";
        if (type == Timeslot.TYPE.AVAILABILITY) title += "Availability";
        else if (type == Timeslot.TYPE.BREAK) title += "Break";
        else if (type == Timeslot.TYPE.OWN_JOB) {
            String timeslotName = "";
            if (timeslot != null) {
                Service service = timeslot.getService();
                if (service != null) {
                    Problem problem = service.getProblem();
                    if (problem == null) {
                        timeslotName = service.getId();

                    } else {
                        timeslotName = problem.getName();
                    }
                }
            }

            title += Strings.isEmpty(timeslotName) ? "Own Job" : timeslotName;
        }

        setActionbarTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timeslot, menu);

        MenuItem itemOptions = menu.findItem(R.id.action_options);
        MenuItem itemEdit = menu.findItem(R.id.action_edit);

        if (itemEdit != null) {
            boolean isEditing = baseFragment != null && ((BaseTimeslotFragment) baseFragment).isEditing();
            itemEdit.setIcon(ContextCompat.getDrawable(getContext(), isEditing ? R.drawable.ic_check_white_48dp : R.drawable.ic_pencil_white_48dp));
            itemEdit.setTitle(isEditing ? "Save" : "Edit");
        }
        if (itemOptions != null) itemOptions.setVisible(hasTimeslot);

        updateActionBarTitle();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            if (baseFragment == null) {
                showErrorDialog();
                return false;
            }

            if (((BaseTimeslotFragment) baseFragment).isEditing()) {
                // if in edit mode, the save button is clicked
                ((BaseTimeslotFragment) baseFragment).saveClicked();
            } else {
                // else go into edit mode //
                ((BaseTimeslotFragment) baseFragment).setEditing(true);
            }

        } else if (item.getItemId() == R.id.action_delete) {
            if (baseFragment == null) {
                showErrorDialog();
                return false;
            }

            ((BaseTimeslotFragment) baseFragment).onDeleteClicked();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Ids.PLACE_PICKER_REQUEST && baseFragment != null) {
            baseFragment.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void tryClose() {
        // if we can close right away
        if (baseFragment == null) {
            finishWithAnimation();
            return;
        }

        if (baseFragment.canClose()) {
            // set to results ok when they made a change
            if (baseFragment instanceof BaseTimeslotFragment) {
                setResult(((BaseTimeslotFragment) baseFragment).didMakeChanges() ? RESULT_OK : RESULT_CANCELED);
            }
            finishWithAnimation();
            return;
        }

        // else show a confirm close dialog //
        MaterialDialogWrapper.getNegativeConfirmationDialog(
                this,
                "Are you sure you want to close? Changes will be lost",
                "DELETE CHANGES",
                "CANCEL",
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        finishWithAnimation();
                        return;
                    }
                },
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }

                }).show();
    }


}
