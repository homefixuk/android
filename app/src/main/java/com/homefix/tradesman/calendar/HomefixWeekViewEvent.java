package com.homefix.tradesman.calendar;

import com.alamkanak.weekview.WeekViewEvent;
import com.homefix.tradesman.R;
import com.samdroid.common.TimeUtils;
import com.samdroid.string.Strings;

import java.util.Calendar;

/**
 * Created by samuel on 7/5/2016.
 */

public class HomefixWeekViewEvent extends WeekViewEvent {

    public static final int TYPE_HOMEFIX_SERVICE = 0, TYPE_AVAILABILITY = 1, TYPE_BREAK = 2, TYPE_OWN_SERVICE = 3;

    int type;
    String description;

    public HomefixWeekViewEvent() {

    }

    public HomefixWeekViewEvent(int type, String description) {
        this.type = type;
        this.description = description;
    }

    public HomefixWeekViewEvent(long id, String name, int startYear, int startMonth, int startDay, int startHour, int startMinute, int endYear, int endMonth, int endDay, int endHour, int endMinute, int type, String description) {
        super(id, name, startYear, startMonth, startDay, startHour, startMinute, endYear, endMonth, endDay, endHour, endMinute);
        this.type = type;
        this.description = description;
        setColor(getTypeColor(type, isEndBeforeToday()));
    }

    public HomefixWeekViewEvent(long id, String name, String location, Calendar startTime, Calendar endTime, int type, String description) {
        super(id, name, location, startTime, endTime);
        this.type = type;
        this.description = description;
        setColor(getTypeColor(type, isEndBeforeToday()));
    }

    public HomefixWeekViewEvent(long id, String name, Calendar startTime, Calendar endTime, int type, String description) {
        super(id, name, startTime, endTime);
        this.type = type;
        this.description = description;
        setColor(getTypeColor(type, isEndBeforeToday()));
    }

    public boolean isEndBeforeToday() {
        return TimeUtils.isDayBeforeToday(getEndTime());
    }

    public static int getTypeColor(int type, boolean isDayBeforeToday) {
        // if is before today, use more pale colors
        if (isDayBeforeToday) {
            switch (type) {
                case TYPE_HOMEFIX_SERVICE:
                    return R.color.colorPrimaryLight;

                case TYPE_AVAILABILITY:
                    return R.color.green; // TODO: change to pale green

                case TYPE_BREAK:
                    return R.color.brown; // TODO: change to pale brown

                case TYPE_OWN_SERVICE:
                    return R.color.colorAccent;

                default:
                    return R.color.grey;
            }
        }

        // else use fully bold colors
        switch (type) {
            case TYPE_HOMEFIX_SERVICE:
                return R.color.colorPrimary;

            case TYPE_AVAILABILITY:
                return R.color.green;

            case TYPE_BREAK:
                return R.color.brown;

            case TYPE_OWN_SERVICE:
                return R.color.red;

            default:
                return R.color.colorAccentDark;
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        setColor(getTypeColor(this.type, isEndBeforeToday()));
    }

    public String getDescription() {
        return Strings.returnSafely(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
