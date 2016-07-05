package com.homefix.tradesman.calendar;

import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.homefix.tradesman.R;
import com.homefix.tradesman.base.BaseFragment;
import com.homefix.tradesman.base.HomeFixBaseActivity;
import com.samdroid.common.MyLog;
import com.samdroid.common.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by samuel on 7/5/2016.
 */

public class CalendarFragment<A extends HomeFixBaseActivity> extends BaseFragment<A, CalendarView, CalendarPresenter> implements CalendarView, WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener {

    WeekView mView;
    CompactCalendarView compactCalendarView;
    private boolean isShowing = false;
    private Date mFirstDayOfNewMonth;

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

        mView = (WeekView) getView().findViewById(R.id.week_view);
        compactCalendarView = (CompactCalendarView) getView().findViewById(R.id.compactcalendar_view);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        compactCalendarView.setVisibility(View.GONE);
        compactCalendarView.hideCalendar();
        isShowing = false;

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
                if (isShowing) toggleCalendar();
            }
        });

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                MyLog.d(TAG, "Day was clicked: " + dateClicked + " with events " + events);

                if (mView != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateClicked);
                    mView.goToDate(cal);

                    if (compactCalendarView != null) toggleCalendar();
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                MyLog.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                mFirstDayOfNewMonth = firstDayOfNewMonth;
            }
        });
    }


    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<HomefixWeekViewEvent> events = new ArrayList<>();

        return events;
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }

    public void goToToday() {
        if (mView != null) mView.goToDate(Calendar.getInstance());
        if (compactCalendarView != null) compactCalendarView.setCurrentDate(new Date());
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

    private CalendarToggleListener calendarToggleListener;

    public void setCalendarToggleListener(CalendarToggleListener listener) {
        calendarToggleListener = listener;
    }

    public void toggleCalendar() {
        if (compactCalendarView == null) return;

        if (!isShowing) {
            compactCalendarView.showCalendar();
        } else {
            compactCalendarView.hideCalendar();
        }
        isShowing = !isShowing;

        if (calendarToggleListener != null) calendarToggleListener.onCalendarToggle(isShowing);
    }

    public boolean isShowingMonthView() {
        return isShowing;
    }

    public String getMonthShowing() {
        return TimeUtils.getMonthNameShort(TimeUtils.getCalendar(mFirstDayOfNewMonth));
    }

}
