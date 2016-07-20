package com.homefix.tradesman.timeslot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.BaseCloseActivity;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.model.Timeslot;
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

        if (type == Timeslot.TYPE.OWN_SERVICE) baseFragment = new OwnJobFragment();
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
        else if (type == Timeslot.TYPE.OWN_SERVICE) title += "Own Job";
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Ids.PLACE_PICKER_REQUEST) {
            baseFragment.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
