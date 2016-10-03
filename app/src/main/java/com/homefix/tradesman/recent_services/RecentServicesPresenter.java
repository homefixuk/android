package com.homefix.tradesman.recent_services;

import android.app.Activity;
import android.content.Intent;

import com.homefix.tradesman.base.presenter.BaseFragmentPresenter;
import com.homefix.tradesman.common.Ids;

/**
 * Created by samuel on 9/16/2016.
 */

public class RecentServicesPresenter extends BaseFragmentPresenter<RecentServicesView> {

    public RecentServicesPresenter(RecentServicesView recentServicesView) {
        super(recentServicesView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // refresh the view when a successful change happened
        if (resultCode == Activity.RESULT_OK && requestCode == Ids.TIMESLOT_CHANGE) {
            getView().refresh();
        }
    }

}
