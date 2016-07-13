package com.homefix.tradesman.calendar;

import com.alamkanak.weekview.WeekViewEvent;
import com.homefix.tradesman.R;
import com.homefix.tradesman.model.Timeslot;
import com.samdroid.common.TimeUtils;
import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by samuel on 7/5/2016.
 */

public class HomefixWeekViewEvent extends WeekViewEvent {

    Timeslot timeslot;

    public HomefixWeekViewEvent(Timeslot timeslot) {
        this.timeslot = timeslot;

        setup();
    }

    private void setup() {
        if (timeslot == null) return;

        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(timeslot.getStart());
        super.setStartTime(cal);

        cal.setTimeInMillis(timeslot.getEnd());
        super.setEndTime(cal);

        // set the name
        Timeslot.TYPE type = Timeslot.TYPE.getTypeEnum(timeslot.getType());
        switch (type) {

            case AVAILABILITY:
                setName("Available");
                break;
            case BREAK:
                setName("Break");
                break;
            case SERVICE:
                if (timeslot.getService() != null) setName(timeslot.getService().getName());
                else setName("Homefix");
                break;
            case OWN_SERVICE:
                setName("Own Job");
                break;

            default:
                setName("unknown event");
                break;
        }

        setColor(getTypeColor(Timeslot.TYPE.getTypeEnum(timeslot.getType()), isEndBeforeToday()));
    }

    public boolean isEndBeforeToday() {
        return TimeUtils.isDayBeforeToday(getEndTime());
    }

    public static int getTypeColor(Timeslot.TYPE type, boolean isDayBeforeToday) {
        // if is before today, use more pale colors
        if (isDayBeforeToday) {
            switch (type) {
                case SERVICE:
                    return R.color.colorPrimaryLight;

                case AVAILABILITY:
                    return R.color.green; // TODO: change to pale green

                case BREAK:
                    return R.color.brown; // TODO: change to pale brown

                case OWN_SERVICE:
                    return R.color.colorAccent; // TODO: change to pale colorAccent

                default:
                    return R.color.black_60_percent; // TODO: change to pale colorAccentDark
            }
        }

        // else use fully bold colors
        switch (type) {
            case SERVICE:
                return R.color.colorPrimary;

            case AVAILABILITY:
                return R.color.green;

            case BREAK:
                return R.color.brown;

            case OWN_SERVICE:
                return R.color.red;

            default:
                return R.color.colorAccentDark;
        }
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public static List<HomefixWeekViewEvent> timeslotToWeekViewEvents(List<Timeslot> timeslots) {
        if (timeslots == null || timeslots.size() == 0) return new ArrayList<>();

        List<HomefixWeekViewEvent> events = new ArrayList<>();
        for (int i = 0; i < timeslots.size(); i++) {
            if (timeslots.get(i) == null) continue;

            events.add(new HomefixWeekViewEvent(timeslots.get(i)));
        }

        return events;
    }

}
