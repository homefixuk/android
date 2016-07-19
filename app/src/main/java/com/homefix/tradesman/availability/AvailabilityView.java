package com.homefix.tradesman.availability;

import com.homefix.tradesman.base.view.BaseFragmentView;
import com.homefix.tradesman.model.Timeslot;

import java.util.Calendar;

/**
 * Created by samuel on 7/13/2016.
 */

public interface AvailabilityView extends BaseFragmentView {

    void setTimeslot(Timeslot timeslot);

    Timeslot getTimeslot();

    void setStartTime(Calendar startTime);

    void setEndTime(Calendar endTime);

    Calendar getStartTime();

    Calendar getEndTime();

    void saveCliked();

    void onSaveComplete(Timeslot timeslot);

    void onDeleteClicked();

    void onDeleteComplete();

}
