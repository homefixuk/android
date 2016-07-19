package com.homefix.tradesman.availability;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.homefix.tradesman.base.activity.BaseCloseActivity;
import com.homefix.tradesman.common.SendReceiver;
import com.homefix.tradesman.model.Timeslot;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.string.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by samuel on 7/13/2016.
 */

public class AvailabilityActivity extends BaseCloseActivity {

    private String timeslotKey;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        if (i != null) {
            timeslotKey = i.getStringExtra("timeslotKey");
        }

        baseFragment = new AvailabilityFragment();

        // try and get the timeslot from the cache
        Timeslot timeslot = null;
        if (!Strings.isEmpty(timeslotKey)) {
            timeslot = Timeslot.getSenderReceiver().remove(timeslotKey);

            if (timeslot != null) ((AvailabilityFragment) baseFragment).setTimeslot(timeslot);
        }

        setActionbarTitle((timeslot != null ? "Edit" : "Add") + " Availability");

        // setup and show the fragment
        replaceFragment(baseFragment);
    }

}
