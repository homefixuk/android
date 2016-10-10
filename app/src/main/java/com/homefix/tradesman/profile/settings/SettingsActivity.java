package com.homefix.tradesman.profile.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.homefix.tradesman.base.activity.BaseCloseActivity;
import com.homefix.tradesman.common.AnalyticsHelper;

/**
 * Created by samuel on 9/6/2016.
 */

public class SettingsActivity extends BaseCloseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionbarTitle("Profile Settings");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (baseFragment == null) {
            baseFragment = new SettingsFragment<>();
            replaceFragment(baseFragment);
        }

        AnalyticsHelper.track(getContext(), "openProfileSettings", new Bundle());
    }
}
