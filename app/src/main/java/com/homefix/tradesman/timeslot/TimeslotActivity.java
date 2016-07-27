package com.homefix.tradesman.timeslot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.BaseCloseActivity;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.timeslot.base_timeslot.BaseTimeslotFragment;
import com.homefix.tradesman.timeslot.base_service.BaseServiceFragment;
import com.homefix.tradesman.timeslot.own_job.OwnJobFragment;
import com.samdroid.common.IntentHelper;
import com.samdroid.string.Strings;

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
                supportInvalidateOptionsMenu();
            }
        }

        ((BaseTimeslotFragment) baseFragment).setType(type);

        String title = (timeslot != null ? "Edit" : "Add") + " ";
        if (type == Timeslot.TYPE.AVAILABILITY) title += "Availability";
        else if (type == Timeslot.TYPE.BREAK) title += "Break";
        else if (type == Timeslot.TYPE.OWN_JOB) {
            String timeslotName = timeslot != null && timeslot.getService() != null ? Strings.returnSafely(timeslot.getService().getId()) : "";
            title += Strings.isEmpty(timeslotName) ? "Own Job" : timeslotName;
        } else title += "Event";
        setActionbarTitle(title);

        // setup and show the fragment
        replaceFragment(baseFragment);
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
}
