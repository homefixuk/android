package com.homefix.tradesman.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.homefix.tradesman.common.SendReceiver;
import com.samdroid.common.MyLog;
import com.samdroid.common.TimeUtils;
import com.samdroid.string.Strings;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samuel on 6/15/2016.
 */

@IgnoreExtraProperties
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
    private long startTime, endTime, slotLength;
    private String type;
    private String tradesmanId, serviceId;
    private boolean canBeSplit;

    public Timeslot() {
    }

    public Timeslot(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTradesmanId() {
        return tradesmanId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getSlotLength() {
        return slotLength > 0 ? slotLength : endTime - startTime;
    }

    public String getType() {
        return Strings.returnSafely(type);
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
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

    @Exclude
    public boolean isEmpty() {
        return Strings.isEmpty(tradesmanId) || startTime == 0 || endTime == 0;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", getId());
        map.put("startTime", getStartTime());
        map.put("endTime", getEndTime());
        map.put("slotLength", getSlotLength());
        map.put("type", getType());
        map.put("tradesmanId", getTradesmanId());
        map.put("serviceId", getServiceId());
        map.put("canBeSplit", isCanBeSplit());
        return map;
    }

    @Exclude
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
            d.setTime(t.getStartTime());
            String start = TimeUtils.formatDataFormal(d) + " " + TimeUtils.formatDateToHoursMinutes(t.getStartTime());
            d.setTime(t.getEndTime());
            String end = TimeUtils.formatDataFormal(d) + " " + TimeUtils.formatDateToHoursMinutes(t.getEndTime());

            MyLog.e("Timeslot", t.getType() + " " + start + " -> " + end);
        }
    }


    ///////////////////////////////////////
    ///////////////////////////////////////
    /////////// SenderReceiver ////////////
    ///////////////////////////////////////
    ///////////////////////////////////////

    @Exclude
    private static final SendReceiver<Timeslot> senderReceiver = new SendReceiver<>(Timeslot.class);

    @Exclude
    public static SendReceiver<Timeslot> getSenderReceiver() {
        return senderReceiver;
    }

    @Exclude
    public static Calendar getStartCalender(Timeslot timeslot) {
        if (timeslot == null) return null;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeslot.getStartTime());
        return cal;
    }

    @Exclude
    public static Calendar getEndCalender(Timeslot timeslot) {
        if (timeslot == null) return null;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeslot.getEndTime());
        return cal;
    }

}
