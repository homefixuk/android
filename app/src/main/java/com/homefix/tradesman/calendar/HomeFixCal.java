package com.homefix.tradesman.calendar;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseBooleanArray;

import com.google.gson.stream.JsonReader;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.model.Timeslot;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.common.TimeUtils;
import com.samdroid.listener.interfaces.OnGetListListener;
import com.samdroid.network.NetworkManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by samuel on 7/12/2016.
 */

public class HomeFixCal {

    private final static String TAG = HomeFixCal.class.getSimpleName();

    final static private SparseArray<Month> months = new SparseArray<>();

    public static class Month implements Comparator, Comparable {

        int year, month;
        List<Timeslot> events;

        public Month(int year, int month, List<Timeslot> events) {
            this.year = year;
            this.month = month;
            this.events = events;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public List<Timeslot> getEvents() {
            return events != null ? events : new ArrayList<Timeslot>();
        }

        public int getNumberEvents() {
            return getEvents().size();
        }

        public Calendar getCalendarStart() {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, 0);
            return cal;
        }

        public List<Timeslot> getEvents(int dayOfMonth) {
            if (events == null || events.size() == 0) return new ArrayList<>();

            List<Timeslot> e = new ArrayList<>();

            Calendar cal = getCalendarStart();
            Timeslot timeslot;

            for (int i = 0; i < events.size(); i++) {
                timeslot = events.get(i);

                if (timeslot == null) continue;

                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // if it is not on the date we are looking for ignore it
                if (!TimeUtils.isOnDate(timeslot.getStart(), cal)) continue;

                e.add(timeslot);
            }

            return e;
        }

        public int getNumberEvents(int dayOfMonth) {
            return getEvents(dayOfMonth).size();
        }

        @Override
        public int compare(Object lhs, Object rhs) {
            if (lhs == null && rhs == null) return 0;
            if (lhs != null && rhs == null) return -1;
            if (lhs == null && rhs != null) return 1;

            if (!(lhs instanceof Month) && !(rhs instanceof Month)) return 0;
            if (lhs instanceof Month && !(rhs instanceof Month)) return -1;
            if (!(lhs instanceof Month) && rhs instanceof Month) return 1;

            Month m1 = (Month) lhs;
            Month m2 = (Month) lhs;

            if (m1.year < m2.year) return -1;
            if (m1.year > m2.year) return 1;
            if (m1.month < m2.month) return -1;
            if (m1.month > m2.month) return 1;
            return 0;
        }

        @Override
        public int compareTo(Object another) {
            return compare(this, another);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Month)) return false;

            Month m = (Month) o;
            return year == m.year && month == m.month;
        }
    }

    public synchronized static SparseArray<Month> getMonths() {
        return months;
    }

    public static int getMonthKey(int year, int month) {
        String k = "" + year + "" + month;
        return Integer.valueOf(k);
    }

    public static boolean containsMonth(int year, int month) {
        return getMonths().get(getMonthKey(year, month), null) != null;
    }

    public static int numberEventsInMonth(int year, int month) {
        Month m = getMonths().get(getMonthKey(year, month), null);

        if (m == null) return 0;

        return m.getNumberEvents();
    }

    public static int numberEventsInMonth(int year, int month, int day) {
        Month m = getMonths().get(getMonthKey(year, month), null);

        if (m == null) return 0;

        return m.getNumberEvents(day);
    }

    public static void addMonth(int year, int month, List<Timeslot> events) {
        if (year <= 0 || month < 0 || month > 12 || events == null || events.size() == 0) return;

        Month m = new Month(year, month, events);
        getMonths().put(getMonthKey(year, month), m);

        // save it to the local cache
        CacheUtils.writeObjectFile("month_" + year + "_" + month, m);
    }

    public static List<Timeslot> getEvents(int year, int month) {
        Month m = getMonths().get(getMonthKey(year, month), null);

        if (m == null) return new ArrayList<>();

        return m.getEvents();
    }

    public static List<Timeslot> getEvents(int year, int month, int day) {
        Month m = getMonths().get(getMonthKey(year, month), null);

        if (m == null) return new ArrayList<>();

        return m.getEvents(day);
    }

    public static List<Timeslot> getEventsToday() {
        Calendar cal = Calendar.getInstance();

        Month m = getMonths().get(getMonthKey(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)), null);

        if (m == null) return new ArrayList<>();

        return m.getEvents(cal.get(Calendar.DAY_OF_MONTH));
    }

    public static void loadMonth(Context context, final int year, final int month, final OnGetListListener<Timeslot> listener) {
        if (year < 0 || month < 0) {
            if (listener != null) listener.onGetListFinished(null);
            return;
        }

        // if there is no network connection, try and load from cache
        if (!NetworkManager.hasConnection(context)) {
            Month m = CacheUtils.readObjectFile("month_" + year + "_" + month, Month.class);
            if (listener != null)
                listener.onGetListFinished(m != null ? m.events : new ArrayList<Timeslot>());
            return;
        }

        // else load from the server
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        HashMap<String, Object> params = new HashMap<>();

        // start from beginning of month
        cal.set(year, month, 0);
        params.put("start_time", cal.getTimeInMillis());

        // until the end of the month
        cal.set(year, month, cal.getActualMaximum(Calendar.DAY_OF_MONTH) + 1);
        params.put("end_time", cal.getTimeInMillis());

        HomeFix.getAPI().getTradesmanEvents(UserController.getToken(), params).enqueue(new Callback<List<Timeslot>>() {

            @Override
            public void onResponse(Call<List<Timeslot>> call, Response<List<Timeslot>> response) {
                List<Timeslot> timeslots = response != null ? response.body() : new ArrayList<Timeslot>();

                addMonth(year, month, timeslots);
                if (listener != null) listener.onGetListFinished(timeslots);
            }

            @Override
            public void onFailure(Call<List<Timeslot>> call, Throwable t) {
                if (MyLog.isIsLogEnabled() && t != null) t.printStackTrace();
                MyLog.e(TAG, call.toString());

                if (listener != null) listener.onGetListFinished(new ArrayList<Timeslot>());
            }

        });
    }

}
