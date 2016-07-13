package com.homefix.tradesman.calendar;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.homefix.tradesman.R;
import com.homefix.tradesman.base.BaseFragment;
import com.homefix.tradesman.base.HomeFixBaseActivity;
import com.homefix.tradesman.model.Timeslot;
import com.samdroid.common.MyLog;
import com.samdroid.common.TimeUtils;
import com.samdroid.common.VariableUtils;
import com.samdroid.listener.interfaces.OnGetListListener;
import com.samdroid.network.NetworkManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by samuel on 7/5/2016.
 */

public class CalendarFragment<A extends HomeFixBaseActivity> extends BaseFragment<A, CalendarView, CalendarPresenter> implements CalendarView, WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener {

    private View mCover;
    private WeekView mView;
    private CompactCalendarView compactCalendarView;
    private boolean isShowing = false;
    private Date mFirstDayOfNewMonth = new Date(); // defaults to current day
    final private SparseBooleanArray monthsFromServer = new SparseBooleanArray();

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
    protected void injectDependencies() {
        super.injectDependencies();

        if (getView() == null) return;

        mView = (WeekView) getView().findViewById(R.id.week_view);
        mCover = getView().findViewById(R.id.cover);
        compactCalendarView = (CompactCalendarView) getView().findViewById(R.id.compactcalendar_view);
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

        // Set long press listener for events.
        mView.setEventLongPressListener(this);

        setNumberDays(3); // default to 3 days showing

        mView.setScrollListener(new WeekView.ScrollListener() {
            @Override
            public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {
                notifyOnMonthChangedListeners(newFirstVisibleDay.get(Calendar.MONTH));

                // update the calendar view
                if (compactCalendarView != null)
                    compactCalendarView.setCurrentDate(newFirstVisibleDay.getTime());
            }
        });

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);

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
        });
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(final int newYear, final int newMonth) {
        // if we have not yet fetched this month and we have a network connection
        if (!monthsFromServer.get(HomeFixCal.getMonthKey(newYear, newMonth), false)
                && NetworkManager.hasConnection(getContext())) {
            monthsFromServer.append(HomeFixCal.getMonthKey(newYear, newMonth), true);

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

        MyLog.e(TAG, "Month: " + newYear + "/" + newMonth);
        Timeslot.printList(timeslots);

        List<HomefixWeekViewEvent> events = HomefixWeekViewEvent.timeslotToWeekViewEvents(timeslots);
        VariableUtils.printList(events);
        return events;
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

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

    public void setNumberDays(int numberDays) {
        if (mView == null) return;

        mView.setNumberOfVisibleDays(Math.min(numberDays, 5)); // at most 5

        if (numberDays > 3) {
            mView.setHeaderColumnPadding(getResources().getDimensionPixelSize(R.dimen.base_padding));
        }
    }

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

    public boolean isShowingMonthView() {
        return isShowing;
    }

    public String getMonthShowing() {
        return TimeUtils.getMonthNameShort(TimeUtils.getCalendar(mFirstDayOfNewMonth));
    }

}
