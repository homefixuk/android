package com.homefix.tradesman.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.BaseToolbarNavMenuActivity;
import com.homefix.tradesman.calendar.CalendarFragment;
import com.homefix.tradesman.common.CheckatraderScraper;
import com.homefix.tradesman.home.home_fragment.HomeFragment;
import com.homefix.tradesman.task.LogoutTask;
import com.samdroid.common.MyLog;
import com.samdroid.common.TimeUtils;
import com.samdroid.file.FileManager;
import com.samdroid.listener.interfaces.OnGetListListener;
import com.samdroid.string.Strings;
import com.samdroid.thread.MyThreadPool;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;

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

        } else if (name.equals(getString(R.string.action_recent))) {
            scrape();
            return true;
        }

        return false;
    }

    private void scrape() {
        final File f = getStorageDir(getContext(), "scraper");
        if (f != null && f.exists()) f.delete();

        for (int type = 0; type < CheckatraderScraper.types.length; type++) {
            for (int loc = 0; loc < CheckatraderScraper.locations.length; loc++) {
                for (int page = 0; page < 30; page++) {

                    final int finalType = type;
                    final int finalLoc = loc;
                    final int finalPage = page;

                    int t = CheckatraderScraper.types[type];
                    String l = CheckatraderScraper.locations[loc];

                    if (Strings.isEmpty(l)) continue;

                    MyThreadPool.post(new CheckatraderScraper(t, l, page, new OnGetListListener<CheckatraderScraper.CheckATrader>() {
                        @Override
                        public void onGetListFinished(List<CheckatraderScraper.CheckATrader> list) {
                            String csv = "";

                            for (CheckatraderScraper.CheckATrader trader : list) {
                                if (trader == null) continue;

                                csv += trader.toCsvString() + "\n";
                            }

                            String filename = "scrape" + finalType + "" + finalLoc + "" + finalPage + ".txt";
                            try {
                                File file = new File(getStorageDir(getContext(), "scraper"), filename);
                                file.createNewFile();
                                writeToFile(file, csv);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }));
                }
            }
        }
    }

    public File getStorageDir(Context context, String name) {
        // Get the directory for the app's private pictures directory.
        File folder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), name);
        } else {
            folder = new File(Environment.getExternalStorageDirectory(), name);
        }

        // make sure the folder exists
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                MyLog.i(TAG, name + " folder failed to create");
            } else {
                MyLog.i(TAG, name + " folder created");
            }
        }

        return folder;
    }

    private void writeToFile(File file, String data) {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(data.getBytes());
            stream.close();

        } catch (Exception e) {
        }
    }

    private void resetActionBarTitle() {
        setActionbarTitle(R.string.app_name);
        setTitleIconRight(0);
        setActionBarTitleClickListener(null);
    }

    private void showHome() {
        if (homeFragment == null) homeFragment = new HomeFragment();

        replaceFragment(homeFragment);
        setCurrentPage(R.string.action_home);
        resetActionBarTitle();
    }

    private void showCalendar() {
        if (calendarFragment == null) {
            calendarFragment = new CalendarFragment<>();

            // update the toolbar if the calendar changed visibility
            calendarFragment.addCalendarToggleListener(new CalendarFragment.CalendarToggleListener() {
                @Override
                public void onCalendarToggle(boolean isShowing) {
                    supportInvalidateOptionsMenu();

                    setTitleIconRight(isShowing ? R.drawable.ic_chevron_up_white_48dp : R.drawable.ic_chevron_down_white_48dp);
                    animateTitleIconRight(isShowing ? 180 : -180);
                }
            });

            calendarFragment.addOnMonthChangedListener(new CalendarFragment.OnMonthChangedListener() {
                @Override
                public void onMonthChanged(int month) {
                    // update the title to the new month
                    setActionbarTitle(calendarFragment.getMonthShowing());
                }
            });
        }

        replaceFragment(calendarFragment);
        setCurrentPage(R.string.action_calendar);
        setActionbarTitle(TimeUtils.getMonthNameShort());
        setTitleIconRight(R.drawable.ic_chevron_down_white_48dp);
        setActionBarTitleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarFragment != null) calendarFragment.toggleCalendar();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mCurrentPage == R.string.action_calendar) {
            // update the title
            if (calendarFragment != null) setActionbarTitle(calendarFragment.getMonthShowing());

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.calendar, menu);

            if (menu != null) {
                MenuItem todayItem = menu.findItem(R.id.action_today);
                if (todayItem != null)
                    todayItem.setTitle("Today (" + Calendar.getInstance().get(Calendar.DATE) + ")");
            }

            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_today) {
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

        } else if (item.getItemId() == R.id.action_five_days) {
            if (calendarFragment != null) calendarFragment.setNumberDays(5);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
