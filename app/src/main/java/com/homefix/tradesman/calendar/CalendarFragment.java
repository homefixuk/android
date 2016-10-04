package com.homefix.tradesman.calendar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.HomeFixBaseActivity;
import com.homefix.tradesman.base.fragment.BaseFragment;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.timeslot.HomefixServiceHelper;
import com.homefix.tradesman.timeslot.TimeslotActivity;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.homefix.tradesman.BuildConfig;
import com.samdroid.common.IntentHelper;
import com.samdroid.common.MyLog;
import com.samdroid.common.TimeUtils;
import com.samdroid.listener.interfaces.OnGetListListener;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by samuel on 7/5/2016.
 */

public class CalendarFragment<A extends HomeFixBaseActivity>
        extends BaseFragment<A, CalendarView, CalendarPresenter>
        implements CalendarView, WeekView.EventClickListener, MonthLoader.MonthChangeListener,
        WeekView.EventLongPressListener, WeekView.EmptyViewClickListener {

    @BindView(R.id.cover)
    protected View mCover;

    @BindView(R.id.week_view)
    protected WeekView mView;

    @BindView(R.id.compactcalendar_view)
    protected CompactCalendarView compactCalendarView;

    private boolean isShowing = false;
    private Date mFirstDayOfNewMonth = new Date(); // defaults to current day
    final private SparseBooleanArray monthsFromServer = new SparseBooleanArray();

    private static boolean needsNotifying = false;

    public CalendarFragment() {
        super(CalendarFragment.class.getSimpleName());
    }

    @Override
    protected CalendarPresenter getPresenter() {
        if (presenter == null) presenter = new CalendarPresenter(this);

        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_calendar;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        compactCalendarView.setVisibility(View.INVISIBLE);
        isShowing = false;

        mCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCalendar();
            }
        });

        // Set an action when any event is clicked.
        mView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mView.setMonthChangeListener(this);

        mView.setEmptyViewClickListener(this);
        mView.setEventLongPressListener(this);

        // show the now line
        mView.setShowNowLine(true);
        mView.setNowLineColor(Color.argb(255, 255, 255, 0));
        mView.setNowLineThickness(5);

        setNumberDays(5); // default to 5 days showing

        mView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)); // go to the current hour

        mView.setScrollListener(new WeekView.ScrollListener() {
            @Override
            public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {
                // update the calendar view
                if (compactCalendarView != null)
                    compactCalendarView.setCurrentDate(newFirstVisibleDay.getTime());

                notifyOnMonthChangedListeners(newFirstVisibleDay.get(Calendar.MONTH));
            }
        });

        // Set up a date time interpreter which will show short date values when in week view and
        // long date values otherwise
        mView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (mView.getNumberOfVisibleDays() >= 5)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                String s = hour > 11 ? ((hour == 12) ? "12" : (hour - 12)) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");

                if (s.equals("0 PM")) s = "12 PM";

                return s;
            }
        });

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(
                new CompactCalendarView.CompactCalendarViewListener() {
                    @Override
                    public void onDayClick(Date dateClicked) {
//                        List<Event> events = compactCalendarView.getEvents(dateClicked);

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(dateClicked);

                        if (mView != null) {
                            mView.goToDate(cal);
                            if (compactCalendarView != null) toggleCalendar();
                        }

                        notifyOnMonthChangedListeners(cal.get(Calendar.MONTH));
                    }

                    @Override
                    public void onMonthScroll(Date firstDayOfNewMonth) {
                        mFirstDayOfNewMonth = firstDayOfNewMonth;

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(mFirstDayOfNewMonth);

                        // scroll the week view underneath to the first of that month
                        if (mView != null) mView.goToDate(cal);

                        notifyOnMonthChangedListeners(cal.get(Calendar.MONTH));
                    }
                }
        );
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(final int newYear, final int newMonth) {
        if (BuildConfig.FLAVOR.equals("apiary_mock") && newMonth != 9)
            return new ArrayList<>(); // TODO: remove! This is used while testing with Apiary mock server

        // if we have not yet fetched this month
        if (!monthsFromServer.get(HomeFixCal.getMonthKey(newYear, newMonth), false)) {
            monthsFromServer.put(HomeFixCal.getMonthKey(newYear, newMonth), true);

            HomeFixCal.loadMonth(getContext(), newYear, newMonth, new OnGetListListener<Timeslot>() {
                @Override
                public void onGetListFinished(List<Timeslot> list) {
                    // update the week view with the new events
                    if (mView != null) mView.notifyDatasetChanged();
                }
            });
        }

        // get the events currently stored
        List<Timeslot> timeslots = HomeFixCal.getEvents(newYear, newMonth);

        List<HomefixWeekViewEvent> events = HomefixWeekViewEvent.timeslotToWeekViewEvents(timeslots);

        // add the events to the calendar view
        if (compactCalendarView != null) {
            List<Event> calendarEvents = getCalendarMonthEvents(events);

            // remove the calendar events for this month to prevent duplicates from showing
            Calendar monthCal = Calendar.getInstance();
            monthCal.set(newYear, newMonth, 0, 0, 0, 0); // set to beginning of this month
            int monthLastDay = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            Date d;
            for (int i = 1; i <= monthLastDay; i++) {
                d = monthCal.getTime(); // get the current months date
                compactCalendarView.removeEvents(d); // remove events for this day
                monthCal.add(Calendar.DAY_OF_MONTH, 1); // move the calendar on 1 day
            }

            if (calendarEvents != null) compactCalendarView.addEvents(calendarEvents);
        }

        return events;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        // when the event is clicked, go to view it
        goToEvent(event, eventRect, false);
    }

    private void goToEvent(WeekViewEvent event, RectF eventRect, boolean goIntoEditMode) {
        if (event == null) return;

        if (event instanceof HomefixWeekViewEvent) {
            HomefixWeekViewEvent hEvent = (HomefixWeekViewEvent) event;
            HomefixServiceHelper.goToTimeslot(getBaseActivity(), hEvent.getTimeslot(), goIntoEditMode);
        }
    }

    @Override
    public void onEmptyViewClicked(@NonNull final Calendar time) {
        MaterialDialogWrapper.getListDialog(
                getBaseActivity(),
                "New...",
                new CharSequence[]{"Availability", "Break", "Your Own Job"},
                new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        if (dialog != null) dialog.dismiss();

                        Intent i = new Intent(getContext(), TimeslotActivity.class);

                        switch (which) {

                            case 0:
                                i.putExtra("type", Timeslot.TYPE.AVAILABILITY.name());
                                break;

                            case 1:
                                i.putExtra("type", Timeslot.TYPE.BREAK.name());
                                break;

                            case 2:
                                i.putExtra("type", Timeslot.TYPE.OWN_JOB.name());
                                break;

                            default:
                                i = null;
                                break;
                        }

                        if (i != null) {
                            i.putExtra("startTime", time.getTimeInMillis());
                            startActivity(i);
                            getActivity().overridePendingTransition(R.anim.right_slide_in, R.anim.expand_out_partial);
                        }

                    }
                }).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        // when the event is long pressed, go straight into editing the timeslot
        goToEvent(event, eventRect, true);
    }

    /**
     * Update the calendar and week view to go to the current day
     */
    public void goToToday() {
        Calendar cal = Calendar.getInstance();

        mFirstDayOfNewMonth = cal.getTime();

        if (mView != null) mView.goToDate(cal);
        if (compactCalendarView != null) compactCalendarView.setCurrentDate(cal.getTime());

        notifyOnMonthChangedListeners(cal.get(Calendar.MONTH));
    }

    /**
     * Set the number of days to show on the week view
     *
     * @param numberDays
     */
    public void setNumberDays(int numberDays) {
        if (mView == null) return;

        numberDays = Math.min(numberDays, 7); // at most 7

        mView.setNumberOfVisibleDays(numberDays);

//        if (numberDays > 3)
//            mView.setHeaderColumnPadding(getResources().getDimensionPixelSize(R.dimen.base_padding_half));

        // update the view dimensions
        if (numberDays <= 1) {
            // Lets change some dimensions to best fit the view.
            mView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
            mView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
            mView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));

        } else if (numberDays <= 3) {
            mView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
            mView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
            mView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));

        } else {
            // change some dimensions to best fit the view
            mView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
            mView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
            mView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        }

        mView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));

        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mView == null) return;

                mView.goToHour(TimeUtils.getCurrentHour());
            }
        }, 500);
    }

    public void removeTimeslot(Timeslot timeslot) {
        if (timeslot == null || timeslot.getStart() == 0) return;

        HomeFixCal.removeEvent(timeslot);
        if (mView != null) mView.notifyDatasetChanged();
    }

    /**
     * Calendar show/hide listeners
     */
    public interface CalendarToggleListener {

        void onCalendarToggle(boolean isShowing);

    }

    private final List<CalendarToggleListener> calendarToggleListeners = new ArrayList<>();

    public void addCalendarToggleListener(CalendarToggleListener listener) {
        if (listener != null) calendarToggleListeners.add(listener);
    }

    public void removeCalendarToggleListener(CalendarToggleListener listener) {
        if (listener != null) calendarToggleListeners.add(listener);
    }

    private void notifyCalendarToggleListeners(boolean isShowing) {
        for (int i = 0; i < calendarToggleListeners.size(); i++) {
            if (calendarToggleListeners.get(i) != null)
                calendarToggleListeners.get(i).onCalendarToggle(isShowing);
        }
    }

    /**
     * Month change listeners
     */
    public interface OnMonthChangedListener {

        void onMonthChanged(int month);

    }

    private final List<OnMonthChangedListener> monthChangedListeners = new ArrayList<>();

    public void addOnMonthChangedListener(OnMonthChangedListener listener) {
        if (listener != null) monthChangedListeners.add(listener);
    }

    public void removeOnMonthChangedListener(OnMonthChangedListener listener) {
        if (listener != null) monthChangedListeners.add(listener);
    }

    private void notifyOnMonthChangedListeners(int month) {
        for (int i = 0; i < monthChangedListeners.size(); i++) {
            if (monthChangedListeners.get(i) != null)
                monthChangedListeners.get(i).onMonthChanged(month);
        }
    }

    /**
     * Week view cover animations
     */
    private Animation animationFadeIn, animationFadeOut;

    private Animation getAnimationFadeIn() {
        if (animationFadeIn == null) {
            animationFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_650);
            animationFadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mCover.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }

        return animationFadeIn;
    }

    private Animation getAnimationFadeOut() {
        if (animationFadeOut == null) {
            animationFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_650);
            animationFadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (mCover != null) mCover.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCover != null) mCover.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }

        return animationFadeOut;
    }

    /**
     * Show/hide the calendar
     */
    public void toggleCalendar() {
        if (compactCalendarView == null) return;

        if (!isShowing) {
            // fade in the dark cover
            if (mCover != null) mCover.startAnimation(getAnimationFadeIn());

            // show the calendar
            compactCalendarView.setVisibility(View.VISIBLE);
            compactCalendarView.showCalendar();

        } else {
            // fade out the dark cover
            if (mCover != null) mCover.startAnimation(getAnimationFadeOut());

            // hide the calendar
            compactCalendarView.hideCalendar();
        }
        isShowing = !isShowing;

        notifyCalendarToggleListeners(isShowing);
    }

    /**
     * @return if the calendar month view is showing
     */
    public boolean isShowingMonthView() {
        return isShowing;
    }

    /**
     * @return get the current month being shown in a short 3 letter format
     */
    public String getMonthShowing() {
        return TimeUtils.getMonthNameShort(mView != null ? mView.getFirstVisibleDay() : TimeUtils.getCalendar(mFirstDayOfNewMonth));
    }

    /**
     * @param weekViewEvents
     * @return converted week view events into calendar events
     */
    private List<Event> getCalendarMonthEvents(List<HomefixWeekViewEvent> weekViewEvents) {
        List<Event> events = new ArrayList<>();

        if (weekViewEvents == null) return events;

        HomefixWeekViewEvent weekViewEvent;
        for (int i = 0; i < weekViewEvents.size(); i++) {
            weekViewEvent = weekViewEvents.get(i);

            if (weekViewEvent == null) continue;

            events.add(new Event(
                    weekViewEvent.getColor(),
                    weekViewEvent.getStartTime().getTimeInMillis(),
                    weekViewEvent.getName()));
        }

        return events;
    }

    public static void setNeedsNotifying() {
        needsNotifying = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (needsNotifying && mView != null) {
            mView.notifyDatasetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Ids.TIMESLOT_CHANGE) {
            String timeslotId = IntentHelper.getStringSafely(data, "timeslotId");
            if (Strings.isEmpty(timeslotId)) {
                MyLog.e(TAG, "[onActivityResult] timeslotId is empty");
                return;
            }

            Timeslot timeslot = Timeslot.getSenderReceiver().get(timeslotId);
            if (timeslot == null) {
                MyLog.e(TAG, "[onActivityResult] timeslot is NULL");
                return;
            }

            String action = IntentHelper.getStringSafely(data, "action");
            if ("deleted".equals(action)) {
                removeTimeslot(timeslot);

            } else {
                // remove the original timeslot
                HomeFixCal.changeEvent(timeslot, timeslot);
                if (mView != null) mView.notifyDatasetChanged();

                HomeFixCal.updateTimeslotService(timeslot, new OnGotObjectListener<Service>() {

                    @Override
                    public void onGotThing(Service o) {
                        MyLog.e(TAG, "Service update: " + (o == null ? "Failed" : "Success"));
                        if (mView != null) mView.notifyDatasetChanged();
                    }
                });
            }
        }
    }

}
