package com.homefix.tradesman.model;

import com.homefix.tradesman.common.SendReceiver;
import com.samdroid.common.MyLog;
import com.samdroid.common.TimeUtils;
import com.samdroid.common.VariableUtils;
import com.samdroid.string.Strings;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * Created by samuel on 6/15/2016.
 */

public class Timeslot {

    public enum TYPE {

        NONE, AVAILABILITY, BREAK, SERVICE, OWN_SERVICE;

        public boolean equals(String type) {
            if (Strings.isEmpty(type)) return false;

            return name().equals(type);
        }

        public static TYPE getTypeEnum(String type) {
            if (Strings.isEmpty(type)) return TYPE.NONE;

            type = type.toUpperCase();
            return TYPE.valueOf(type);
        }

    }

    private long start, end, length;
    private String type;
    private Tradesman tradesman;
    private Service service;

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getLength() {
        return length;
    }

    public String getType() {
        return type;
    }

    public Tradesman getTradesman() {
        return tradesman;
    }

    public Service getService() {
        return service;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTradesman(Tradesman tradesman) {
        this.tradesman = tradesman;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public static void printList(List<Timeslot> list) {
        if (list == null) return;

        Timeslot t;
        for (int i = 0; i < list.size(); i++) {
            t = list.get(i);

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

}
