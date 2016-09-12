package com.homefix.tradesman.home.home_fragment;

import android.app.Activity;
import android.content.Intent;

import com.homefix.tradesman.base.presenter.BaseFragmentPresenter;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.timeslot.HomefixServiceHelper;

/**
 * Created by samuel on 6/28/2016.
 */

public class HomeFragmentPresenter extends BaseFragmentPresenter<HomeFragmentView> implements OwnJobViewHolder.TimeslotClickedListener {

    public HomeFragmentPresenter(HomeFragmentView homeFragmentView) {
        super(homeFragmentView);
    }

    @Override
    public void onTimeslotClicked(Timeslot timeslot, boolean longClick) {
        if (!isViewAttached()) return;

        HomefixServiceHelper.goToTimeslot(getView().getBaseActivity(), timeslot, longClick);
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
