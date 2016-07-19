package com.homefix.tradesman.timeslot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.BaseCloseActivity;
import com.homefix.tradesman.model.Timeslot;
import com.samdroid.common.IntentHelper;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 7/13/2016.
 */

public class TimeslotActivity extends BaseCloseActivity {

    private boolean hasTimeslot = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String timeslotKey = IntentHelper.getStringSafely(i, "timeslotKey");
        String typeStr = IntentHelper.getStringSafely(i, "type");

        Toast.makeText(getContext(), "TypeStr: " + typeStr, Toast.LENGTH_LONG).show();

        Timeslot.TYPE type = Timeslot.TYPE.getTypeEnum(typeStr);

        baseFragment = new BaseTimeslotFragment();

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
        else title += "Event";
        setActionbarTitle(title);

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

            ((BaseTimeslotFragment) baseFragment).onDeleteClicked();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

}
