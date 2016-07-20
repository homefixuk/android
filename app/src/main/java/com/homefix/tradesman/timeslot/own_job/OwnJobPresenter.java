package com.homefix.tradesman.timeslot.own_job;

import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.timeslot.BaseTimeslotFragmentPresenter;
import com.homefix.tradesman.timeslot.BaseTimeslotView;

import java.util.Calendar;

/**
 * Created by samuel on 7/19/2016.
 */

public class OwnJobPresenter extends BaseTimeslotFragmentPresenter<OwnJobView> {

    public OwnJobPresenter(OwnJobView view) {
        super(view, Timeslot.TYPE.OWN_SERVICE);
    }

    public void addNewJob(Calendar start, Calendar end) {

    }

    public void updateJob(Timeslot timeslot, Calendar start, Calendar end) {

    }

}
