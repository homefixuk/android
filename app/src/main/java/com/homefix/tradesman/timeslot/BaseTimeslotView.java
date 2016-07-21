package com.homefix.tradesman.timeslot;

import com.homefix.tradesman.base.view.BaseFragmentView;
import com.homefix.tradesman.model.Timeslot;

import java.util.Calendar;

/**
 * Created by samuel on 7/13/2016.
 */

public interface BaseTimeslotView extends BaseFragmentView {

    void setTimeslot(Timeslot timeslot);

    Timeslot getTimeslot();

    void setStartTime(Calendar startTime);

    void setEndTime(Calendar endTime);

    Calendar getStartTime();

    Calendar getEndTime();

    void saveClicked();

    void onSaveComplete(Timeslot timeslot);

    void onDeleteClicked();

    void onDeleteComplete();

}
