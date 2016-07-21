package com.homefix.tradesman.calendar;

import android.graphics.Color;

import com.alamkanak.weekview.WeekViewEvent;
import com.homefix.tradesman.model.Timeslot;
import com.samdroid.common.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by samuel on 7/5/2016.
 */

public class HomefixWeekViewEvent extends WeekViewEvent {

    Timeslot timeslot;

    public HomefixWeekViewEvent() {
    }

    public HomefixWeekViewEvent(Timeslot timeslot) {
        this.timeslot = timeslot;

        setup();
    }

    private void setup() {
        if (timeslot == null) return;

        setId(timeslot.hashCode());

        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(timeslot.getStart());
        setStartTime(cal);

        Calendar endCal = (Calendar) cal.clone();
        endCal.setTimeInMillis(timeslot.getEnd());
        setEndTime(endCal);

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
                if (timeslot.getService() != null) setName(timeslot.getService().getId());
                else setName("Homefix");
                break;
            case OWN_JOB:
                if (timeslot.getService() != null) setName(timeslot.getService().getId());
                else setName("Own Job");
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
                    return Color.argb(255, 169, 184, 191); // pale dark primary

                case AVAILABILITY:
                    return Color.argb(255, 153, 204, 153); // pale green

                case BREAK:
                    return Color.argb(255, 176, 156, 147); // pale brown

                case OWN_JOB:
                    return Color.argb(255, 215, 178, 215); // pale purple

                default:
                    return Color.argb(255, 255, 178, 178); // pale red
            }
        }

        // else use fully bold colors //
        switch (type) {
            case SERVICE:
                return Color.argb(255, 40, 79, 97); // dark primary

            case AVAILABILITY:
                return Color.argb(255, 0, 128, 0); // green

            case BREAK:
                return Color.argb(255, 97, 58, 40); // brown

            case OWN_JOB:
                return Color.argb(255, 124, 0, 124); // purple

            default:
                return Color.argb(255, 255, 0, 0); // red
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
