package com.homefix.tradesman.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.Timeslot;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.common.TimeUtils;
import com.samdroid.common.VariableUtils;
import com.samdroid.listener.interfaces.OnGetListListener;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.network.NetworkManager;
import com.samdroid.string.Strings;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by samuel on 7/12/2016.
 */

public class HomeFixCal {

    private final static String TAG = HomeFixCal.class.getSimpleName();

    final static private SparseArray<Month> months = new SparseArray<>();

    public static class Month implements Comparator, Comparable {

        private int year, month;
        private List<Timeslot> events;

        public Month(int year, int month, List<Timeslot> evs) {
            this.year = year;
            this.month = month;
            this.events = evs;

            if (events == null) events = new ArrayList<>();

            // remove invalid events in this month
            int thisMonthKey = getMonthKey(year, month);
            Timeslot ev;
            for (int i = 0, len = events.size(); i < len; i++) {
                ev = events.get(i);

                // if the event is empty or does not belong in this month, remove it
                if (ev == null || thisMonthKey != HomeFixCal.getMonthKey(ev)) {
                    events.remove(i);
                    i--;
                    len--;
                }
            }
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public synchronized List<Timeslot> getEvents() {
            return events != null ? events : new ArrayList<Timeslot>();
        }

        /**
         * @param timeslot
         * @return if the time slot was added
         */
        public boolean addEvent(Timeslot timeslot) {
            if (timeslot == null || Strings.isEmpty(timeslot.getId())) return false;

            List<Timeslot> evs = getEvents();
            if (evs == null) evs = new ArrayList<>();

            Calendar cal = Timeslot.getStartCalender(timeslot);

            // if it is not in this month
            if (cal.get(Calendar.YEAR) != year || cal.get(Calendar.MONTH) != month) return false;

            if (evs.size() == 0) {
                evs.add(timeslot);
                return true;
            }

            // if it already exists, do not add it again
            Timeslot ev = getFromId(timeslot.getId());
            if (ev != null) return true;

            Timeslot temp;
            for (int i = 0; i < evs.size(); i++) {
                temp = evs.get(i);

                if (temp == null) continue;

                // if this time slot is before the one in the loop, add it before it
                if (timeslot.getStartTime() <= temp.getStartTime()) {
                    evs.add(i, timeslot);
                    return true;
                }
            }

            // add it normally otherwise
            evs.add(timeslot);
            return true;
        }

        /**
         * Update an event in the month
         *
         * @param original
         * @param changed
         * @return if the changed time slot was added
         */
        public boolean updateEvent(Timeslot original, Timeslot changed) {
            List<Timeslot> evs = getEvents();
            if (evs == null) return changed != null && addEvent(changed);

            // if the original event does not already exist, add the changed one
            Timeslot ev = getFromId(original.getId());
            if (ev == null) {
                addEvent(changed);
                return true;
            }

            evs.remove(ev); // remove the original event

            // add the changed one in the appropriate place
            return addEvent(changed);
        }

        /**
         * Remove an event in the month
         *
         * @param original
         * @return if the changed time slot was added
         */
        public boolean removeEvent(Timeslot original) {
            List<Timeslot> evs = getEvents();
            if (evs == null) return true;

            if (original == null || Strings.isEmpty(original.getId())) return false;

            Timeslot ev = getFromId(original.getId());
            return ev != null && evs.remove(ev);
        }

        public void updateTimeslotService(Timeslot timeslot, OnGotObjectListener<Service> onGotObjectListener) {
        }

        /**
         * @param timeslotId
         * @return the Timeslot from its ID
         */
        public Timeslot getFromId(String timeslotId) {
            if (Strings.isEmpty(timeslotId)) return null;

            List<Timeslot> evs = getEvents();
            for (Timeslot ev : evs) {
                if (ev == null) continue;

                if (timeslotId.equals(ev.getId())) return ev;
            }

            return null;
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
            List<Timeslot> evs = getEvents();

            if (evs == null || evs.size() == 0) return new ArrayList<>();

            List<Timeslot> e = new ArrayList<>();

            Calendar cal = getCalendarStart();
            Timeslot timeslot;

            for (int i = 0; i < evs.size(); i++) {
                timeslot = evs.get(i);

                if (timeslot == null) continue;

                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // if it is not on the date we are looking for ignore it
                if (!TimeUtils.isOnDate(timeslot.getStartTime(), cal)) continue;

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
            if (lhs == null) return 1;

            if (!(lhs instanceof Month) && !(rhs instanceof Month)) return 0;
            if (lhs instanceof Month && !(rhs instanceof Month)) return -1;
            if (!(lhs instanceof Month)) return 1;

            Month m1 = (Month) lhs;
            Month m2 = (Month) lhs;

            if (m1.year < m2.year) return -1;
            if (m1.year > m2.year) return 1;
            if (m1.month < m2.month) return -1;
            if (m1.month > m2.month) return 1;
            return 0;
        }

        @Override
        public int compareTo(@NonNull Object another) {
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

    public static Month addMonth(int year, int month, List<Timeslot> events) {
        if (year <= 0 || month < 0 || month > 12 || events == null)
            return null;

        Month m = new Month(year, month, events);
        getMonths().put(getMonthKey(year, month), m);

        // save it to the local cache
        CacheUtils.writeObjectFile("month_" + year + "_" + month, m);
        CalendarFragment.setNeedsNotifying();
        return m;
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

    public static Month getMonth(Timeslot timeslot) {
        if (timeslot == null) return null;

        return getMonths().get(getMonthKey(timeslot));
    }

    public static int getMonthKey(Timeslot timeslot) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeslot.getStartTime());
        return getMonthKey(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
    }

    /**
     * Add an event time slot to the appropriate month
     *
     * @param timeslot
     * @return the month it was added to, or null if the time slot was null
     */
    public static Month addEvent(Timeslot timeslot) {
        if (timeslot == null) return null;

        Month month = getMonth(timeslot);

        if (month == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timeslot.getStartTime());
            month = addMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), new ArrayList<Timeslot>());
            CalendarFragment.setNeedsNotifying();
        }

        if (month != null) {
            month.addEvent(timeslot);
            CalendarFragment.setNeedsNotifying();
        }

        return month;
    }

    /**
     * Update an event time slot
     *
     * @param original
     * @return the month the changed time slot was added to, or null if the time slot was null
     */
    public static Month changeEvent(Timeslot original, Timeslot changed) {
        if (original != null) {
            Month month = getMonth(original);
            if (month == null) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(original.getStartTime());
                month = addMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), new ArrayList<Timeslot>());
            }

            if (month != null && month.updateEvent(original, changed)) {
                CalendarFragment.setNeedsNotifying();
                return month;
            }
        }

        CalendarFragment.setNeedsNotifying();
        return addEvent(changed);
    }

    /**
     * Remove an event time slot
     *
     * @param original
     * @return the month the remove time slot was in, or null if the timeslot was null
     */
    public static Month removeEvent(Timeslot original) {
        Month month = getMonth(original);
        if (month == null) return null;

        if (month.removeEvent(original)) CalendarFragment.setNeedsNotifying();
        return month;
    }

    public static void updateTimeslotService(final Timeslot timeslot, final @NonNull OnGotObjectListener<Service> onGotObjectListener) {
//        final Month month = getMonth(timeslot);
//        final String serviceId = timeslot != null ? timeslot.getServiceId() : null;
//        if (month == null || Strings.isEmpty(serviceId)) {
//            onGotObjectListener.onGotThing(null);
//            return;
//        }
//
//
//
//        HomeFix
//                .getAPI()
//                .getService(TradesmanController.getToken(), service.getId())
//                .enqueue(new Callback<Service>() {
//                    @Override
//                    public void onResponse(Call<Service> call, Response<Service> response) {
//                        Service serviceNew = response != null ? response.body() : null;
//
//                        // if the returned service is not valid
//                        if (serviceNew == null || serviceNew.isEmpty()) {
//                            onGotObjectListener.onGotThing(null);
//                            return;
//                        }
//
//                        // else it's valid so update timeslot and the month it's from
//                        timeslot.setService(serviceNew);
//                        month.updateEvent(timeslot, timeslot);
//                        onGotObjectListener.onGotThing(serviceNew);
//                    }
//
//                    @Override
//                    public void onFailure(Call<Service> call, Throwable t) {
//                        if (t != null && MyLog.isIsLogEnabled()) t.printStackTrace();
//                        onGotObjectListener.onGotThing(null);
//                    }
//                });
    }

    private static final HashMap<String, Query> referenceHashMap = new HashMap<>();

    public static void loadMonth(Context context, final int year, final int month, @NonNull final OnGetListListener<Timeslot> listener) {
        String tradesmanId = FirebaseUtils.getCurrentTradesmanId();
        if (Strings.isEmpty(tradesmanId) || year < 0 || month < 0) {
            listener.onGetListFinished(null);
            return;
        }

        // if we have already setup a ref for this month
        if (referenceHashMap.containsKey("" + getMonthKey(year, month))) {
            listener.onGetListFinished(getEvents(year, month));
            return;
        }

        // else load from the server
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        // start from beginning of month
        cal.set(year, month, 0);
        long startTime = cal.getTimeInMillis();

        // until the end of the month
        cal.set(year, month, cal.getActualMaximum(Calendar.DAY_OF_MONTH) + 1);
        long endTime = cal.getTimeInMillis();

        Query query = FirebaseUtils
                .getBaseRef()
                .child("tradesmanTimeslots")
                .child(tradesmanId)
                .orderByChild("startTime")
                .startAt(startTime)
                .endAt(endTime);
        query.keepSynced(true);
        MyLog.e(TAG, "Getting tradesmanTimeslots between " + startTime + " and " + endTime);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MyLog.e(TAG, "got tradesmanTimeslots: " + (dataSnapshot != null && dataSnapshot.exists()));

                if (dataSnapshot == null) {
                    listener.onGetListFinished(null);
                    return;
                }

                // add all the timeslots to a list
                List<Timeslot> timeslots = new ArrayList<>();
                Iterable<DataSnapshot> childrenSnapshots = dataSnapshot.getChildren();
                for (DataSnapshot childrenSnapshot : childrenSnapshots) {
                    timeslots.add(childrenSnapshot.getValue(Timeslot.class));
                }

                addMonth(year, month, timeslots);
                listener.onGetListFinished(timeslots);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null) MyLog.printStackTrace(databaseError.toException());
                listener.onGetListFinished(null);
            }
        });

        // save the query so we never lose updates to the events
        referenceHashMap.put("" + getMonthKey(year, month), query);
    }

}
