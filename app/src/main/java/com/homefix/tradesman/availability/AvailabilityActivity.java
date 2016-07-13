package com.homefix.tradesman.availability;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.homefix.tradesman.base.activity.BaseCloseActivity;

/**
 * Created by samuel on 7/13/2016.
 */

public class AvailabilityActivity extends BaseCloseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionbarTitle("Add Availability");

        baseFragment = null;
    }

}
