package com.homefix.tradesman.model;

import com.homefix.tradesman.common.SendReceiver;
import com.samdroid.common.MyLog;
import com.samdroid.common.TimeUtils;
import com.samdroid.string.Strings;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by samuel on 6/15/2016.
 */

public class Timeslot {

    public enum TYPE {

        NONE, AVAILABILITY, BREAK, SERVICE, OWN_JOB;

        public boolean equals(String type) {
            return !Strings.isEmpty(type) && (name().equals(type) || name().equals(type.toLowerCase()));
        }

        public static TYPE getTypeEnum(String type) {
            if (Strings.isEmpty(type)) return NONE;

            type = type.toUpperCase();
            try {
                return TYPE.valueOf(type);
            } catch (Exception e) {
                return NONE;
            }
        }

        public String getName() {
            return name().toLowerCase();
        }

    }

    private String id;
    private long start, end, slotLength;
    private String type;
    private String tradesmanId, serviceId;
    private boolean canBeSplit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTradesmanId() {
        return tradesmanId;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getSlotLength() {
        return slotLength > 0 ? slotLength : end - start;
    }

    public String getType() {
        return Strings.returnSafely(type);
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setSlotLength(long slotLength) {
        this.slotLength = slotLength;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCanBeSplit() {
        return canBeSplit;
    }

    public void setCanBeSplit(boolean canBeSplit) {
        this.canBeSplit = canBeSplit;
    }

    public void setTradesmanId(String tradesmanId) {
        this.tradesmanId = tradesmanId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    //////////////
    /// Helper ///
    //////////////

    public boolean isEmpty() {
        return Strings.isEmpty(tradesmanId) || start == 0 || end == 0;
    }

    public static void printList(List<Timeslot> list) {
        if (list == null) return;

        Timeslot t;
        for (int i = 0; i < list.size(); i++) {
            t = list.get(i);

            if (t == null) {
                MyLog.e("Timeslot", i + ": is NULL");
                continue;
            }

            Date d = new Date();
            d.setTime(t.getStart());
            String start = TimeUtils.formatDataFormal(d) + " " + TimeUtils.formatDateToHoursMinutes(t.getStart());
            d.setTime(t.getEnd());
            String end = TimeUtils.formatDataFormal(d) + " " + TimeUtils.formatDateToHoursMinutes(t.getEnd());

            MyLog.e("Timeslot", t.getType() + " " + start + " -> " + end);
        }
    }


    ///////////////////////////////////////
    ///////////////////////////////////////
    /////////// SenderReceiver ////////////
    ///////////////////////////////////////
    ///////////////////////////////////////

    private static final SendReceiver<Timeslot> senderReceiver = new SendReceiver<>(Timeslot.class);

    public static SendReceiver<Timeslot> getSenderReceiver() {
        return senderReceiver;
    }

    public static Calendar getStartCalender(Timeslot timeslot) {
        if (timeslot == null) return null;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeslot.getStart());
        return cal;
    }

    public static Calendar getEndCalender(Timeslot timeslot) {
        if (timeslot == null) return null;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeslot.getEnd());
        return cal;
    }

}
