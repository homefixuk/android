package com.homefix.tradesman.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.BaseToolbarNavMenuActivity;
import com.homefix.tradesman.calendar.CalendarFragment;
import com.homefix.tradesman.task.LogoutTask;
import com.samdroid.string.Strings;

import java.util.Calendar;

/**
 * Created by samuel on 6/22/2016.
 */

public class HomeActivity extends BaseToolbarNavMenuActivity<HomeView, HomePresenter> implements HomeView {

    private int mCurrentPage;
    private HomeFragment homeFragment;
    private CalendarFragment<HomeActivity> calendarFragment;

    public HomeActivity() {
        super(HomeActivity.class.getSimpleName());
    }

    @Override
    public HomePresenter getPresenter() {
        if (presenter == null) presenter = new HomePresenter();

        return presenter;
    }

    @Override
    protected HomeView getThisView() {
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkCCA = true;
        checkPermissions = true;

        showHome();
    }

    private void setCurrentPage(int page) {
        mCurrentPage = page;
        supportInvalidateOptionsMenu();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item == null) return false;

        String name = item.getTitle().toString();

        if (name.equals(getString(R.string.action_home))) {
            hideNavMenu();

            if (mCurrentPage == R.string.action_home) return false;
            else {
                showHome();
                return true;
            }

        } else if (name.equals(getString(R.string.action_calendar))) {
            hideNavMenu();

            if (mCurrentPage == R.string.action_calendar) return false;
            else {
                showCalendar();
                return true;
            }

        } else if (name.equals(getString(R.string.action_logout))) {
            if (mCurrentPage == R.string.action_logout) {
                hideNavMenu();
                return false;
            }

            LogoutTask.doLogout(this);
            return true;
        }

        return false;
    }

    private void showHome() {
        if (homeFragment == null) homeFragment = new HomeFragment();

        replaceFragment(homeFragment);
        setCurrentPage(R.string.action_home);
    }

    private void showCalendar() {
        if (calendarFragment == null) {
            calendarFragment = new CalendarFragment<>();

            // update the toolbar if the calendar changed visibility
            calendarFragment.setCalendarToggleListener(new CalendarFragment.CalendarToggleListener() {
                @Override
                public void onCalendarToggle(boolean isShowing) {
                    supportInvalidateOptionsMenu();
                }
            });
        }

        replaceFragment(calendarFragment);
        setCurrentPage(R.string.action_calendar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mCurrentPage == R.string.action_calendar) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.calendar, menu);

            MenuItem showMonth = menu.findItem(R.id.action_choose_day);

            // make sure the icon points down when the calendar is not showing, or up when it is
            if (calendarFragment == null || !calendarFragment.isShowingMonthView())
                showMonth.setIcon(ContextCompat.getDrawable(this, R.drawable.chevron_down));
            else showMonth.setIcon(ContextCompat.getDrawable(this, R.drawable.chevron_up));

//            String month = calendarFragment != null ? calendarFragment.getMonthShowing() : "";
//            if (Strings.isEmpty(month)) month = "Month";
//            showMonth.setTitle(month);

            MenuItem todayItem = menu.findItem(R.id.action_today);
            todayItem.setTitle("Today [" + Calendar.getInstance().get(Calendar.DATE) + "]");

            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_choose_day) {
            if (calendarFragment != null) {
                calendarFragment.toggleCalendar();
                supportInvalidateOptionsMenu(); // update toolbar
            }
            return true;

        } else if (item.getItemId() == R.id.action_today) {
            if (calendarFragment != null) {
                calendarFragment.goToToday();
                supportInvalidateOptionsMenu(); // update toolbar
            }
            return true;

        } else if (item.getItemId() == R.id.action_one_day) {
            if (calendarFragment != null) calendarFragment.setNumberDays(1);
            return true;

        } else if (item.getItemId() == R.id.action_three_days) {
            if (calendarFragment != null) calendarFragment.setNumberDays(3);
            return true;

        } else if (item.getItemId() == R.id.action_seven_days) {
            if (calendarFragment != null) calendarFragment.setNumberDays(7);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
