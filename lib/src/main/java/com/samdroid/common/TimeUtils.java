package com.samdroid.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;

public class TimeUtils {

    public static long getWeeksInMillis(int weeks) {
        return weeks * 7 * 24 * 60 * 60 * 1000;
    }

    public static long getDaysInMillis(int days) {
        return days * 24 * 60 * 60 * 1000;
    }

    public static long getHoursInMillis(int hours) {
        return hours * 60 * 60 * 1000;
    }

    public static long getMinutesInMillis(int millis) {
        return millis * 60 * 1000;
    }

    public static long getMillisInWeeks(long millis) {
        return Math.round(millis / (7 * 24 * 60 * 60 * 1000));
    }

    public static long getMillisInDays(long millis) {
        return Math.round(millis / (24 * 60 * 60 * 1000));
    }

    public static long getMillisInHours(long millis) {
        return Math.round(millis / (60 * 60 * 1000));
    }

    public static long getMillisInMinutes(long millis) {
        return Math.round(millis / (60 * 1000));
    }

    public static String formatDateToHoursMinutesSeconds(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);

        // make sure the time does not go below 0
        seconds = Math.max(seconds, 0);
        minutes = Math.max(minutes, 0);
        hours = Math.max(hours, 0);

        return "" + hours + " HOURS " + minutes + " MINUTES " + seconds + " SECONDS";
    }

    public static String formatDateToHoursMinutes(long millis) {
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);

        // make sure the time does not go below 0
        minutes = Math.max(minutes, 0);
        hours = Math.max(hours, 0);

        return "" + hours + " hours and " + minutes + " minutes";
    }

    /**
     * @param d the date to format
     * @return the date in a formal string
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatDataFormal(Date d) {
        // make a calendar with the date
        final Calendar c = Calendar.getInstance();
        c.setTime(d);

        // get the day of the month
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        String s = "";

        // get the day
        SimpleDateFormat formatter = new SimpleDateFormat("d");
        s = formatter.format(d);

        // add the day of month suffix
        s += getDayOfMonthSuffix(dayOfMonth);

        // add the full month
        formatter = new SimpleDateFormat("MMMM");
        s += " " + formatter.format(d);

        // add the full year
        formatter = new SimpleDateFormat("yyyy");
        s += " " + formatter.format(d);

        return s;
    }

    /**
     * @param dayOfMonth the day of the month
     * @return the suffix of the day of month (e.x. 1st, 2nd, 3rd, 11th, 13th etc.)
     */
    public static String getDayOfMonthSuffix(final int dayOfMonth) {
        if (dayOfMonth < 1 || dayOfMonth > 31) return "";

        if (dayOfMonth >= 11 && dayOfMonth <= 13) {
            return "th";
        }

        switch (dayOfMonth % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    /**
     * @param a
     * @param b
     * @return the days between two dates
     */
    public static long getDaysBetween(Date a, Date b) {
        Calendar calA = Calendar.getInstance();
        calA.setTime(a);

        Calendar calB = Calendar.getInstance();
        calB.setTime(b);

        long diff = calB.getTimeInMillis() - calA.getTimeInMillis(); // result in milliseconds
        long days = diff / (24 * 60 * 60 * 1000); // convert to days

        return days;
    }

    /**
     * @return the hour of the day
     */
    public static int getCurrentHour() {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * @return the users current time zone abbreviation (EST, GMT, PST, etc.)
     */
    public static String getTimezoneAbbr() {
        TimeZone timezone = TimeZone.getDefault();
        return timezone.getDisplayName(timezone.inDaylightTime(new Date()), TimeZone.SHORT);
    }

    public static int getYearsBetween(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

}
