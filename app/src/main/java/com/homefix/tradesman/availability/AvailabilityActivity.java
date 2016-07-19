package com.homefix.tradesman.availability;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.BaseCloseActivity;
import com.homefix.tradesman.model.Timeslot;
import com.samdroid.common.IntentHelper;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 7/13/2016.
 */

public class AvailabilityActivity extends BaseCloseActivity {

    private String timeslotKey;
    private boolean hasTimeslot = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        timeslotKey = IntentHelper.getStringSafely(i, "timeslotKey");

        baseFragment = new AvailabilityFragment();

        // try and get the timeslot from the cache
        Timeslot timeslot = null;
        if (!Strings.isEmpty(timeslotKey)) {
            timeslot = Timeslot.getSenderReceiver().remove(timeslotKey);

            if (timeslot != null) {
                hasTimeslot = true;
                ((AvailabilityFragment) baseFragment).setTimeslot(timeslot);
                supportInvalidateOptionsMenu();
            }
        }

        setActionbarTitle((timeslot != null ? "Edit" : "Add") + " Availability");

        // setup and show the fragment
        replaceFragment(baseFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!hasTimeslot) return super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timeslot, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            if (baseFragment == null) {
                showErrorDialog();
                return false;
            }

            ((AvailabilityFragment) baseFragment).onDeleteClicked();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

}
