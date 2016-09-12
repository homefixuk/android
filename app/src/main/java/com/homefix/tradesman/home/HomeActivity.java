package com.homefix.tradesman.home;

import android.content.Context;
import android.content.Intent;
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
import com.homefix.tradesman.profile.settings.SettingsActivity;
import com.homefix.tradesman.profile.settings.SettingsFragment;
import com.homefix.tradesman.task.LogoutTask;
import com.samdroid.common.MyLog;
import com.samdroid.common.TimeUtils;
import com.samdroid.listener.interfaces.OnGetListListener;
import com.samdroid.string.Strings;
import com.samdroid.thread.MyThreadPool;

import java.io.File;
import java.io.FileOutputStream;
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

        if (mCurrentPage > 0 && name.equals(getString(mCurrentPage))) {
            hideNavMenu();
            return false;
        }

        if (name.equals(getString(R.string.action_home))) {
            hideNavMenu();
            showHome();
            return true;

        } else if (name.equals(getString(R.string.action_calendar))) {
            hideNavMenu();
            showCalendar();
            return true;

        } else if (name.equals(getString(R.string.action_logout))) {
            LogoutTask.doLogout(this);
            return true;

        } else if (name.equals(getString(R.string.action_recent))) {
//            scrape();
            return true;

        } else if (name.equals(getString(R.string.action_help))) {
//            String filename = "scrape_" + System.currentTimeMillis() + ".csv";
//            try {
//                File file = new File(getStorageDir(getContext(), "scraper"), filename);
//                file.createNewFile();
//                writeToFile(file, getCsv());
//                MyLog.e(TAG, "Saved CSV to file");
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return true;
        }

        return false;
    }

    @Override
    protected void onNavigationProfileClicked() {
        super.onNavigationProfileClicked();

        mCurrentPage = R.string.action_profile;
    }

    private String csv = "";

    public synchronized String getCsv() {
        return csv;
    }

    private synchronized void setCsv(String csv) {
        this.csv = csv;
    }

    public void addToCsv(String s) {
        if (Strings.isEmpty(s)) return;

        setCsv(getCsv() + "\n" + s);
    }

    private void scrape() {
        final File f = getStorageDir(getContext(), "scraper");
        if (f != null && f.exists()) f.delete();

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int typeIndex = 0; typeIndex < CheckatraderScraper.types.size(); typeIndex++) {
                    for (int loc = 0; loc < CheckatraderScraper.locations.length; loc++) {
                        for (int page = 0; page < 30; page++) {
                            try {
                                Thread.sleep(1000, 0);
                            } catch (InterruptedException e) {
                                MyLog.printStackTrace(e);
                            }

                            int t = CheckatraderScraper.types.keyAt(typeIndex);
                            String l = CheckatraderScraper.locations[loc];

                            if (Strings.isEmpty(l)) continue;

                            MyThreadPool.post(new CheckatraderScraper(t, l, page, new OnGetListListener<CheckatraderScraper.CheckATrader>() {
                                @Override
                                public void onGetListFinished(List<CheckatraderScraper.CheckATrader> list) {
                                    String csv = "";

                                    for (CheckatraderScraper.CheckATrader trader : list) {
                                        if (trader == null || Strings.isEmpty(trader.companyName) || Strings.isEmpty(trader.email))
                                            continue;

                                        csv += trader.toCsvString() + "\n";
                                    }

                                    addToCsv(csv);
                                }
                            }));
                        }
                    }
                }
            }
        }).start();
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

    private void showHome() {
        if (homeFragment == null) homeFragment = new HomeFragment();

        replaceFragment(homeFragment);
        setCurrentPage(R.string.action_home);
        resetActionBarTitle();

        supportInvalidateOptionsMenu();
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

        supportInvalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (mCurrentPage == R.string.action_home) {
            // inflate the profile menu
            inflater.inflate(R.menu.home, menu);
            return true;

        } else if (mCurrentPage == R.string.action_calendar) {
            // update the title
            if (calendarFragment != null) setActionbarTitle(calendarFragment.getMonthShowing());

            inflater.inflate(R.menu.calendar, menu);

            if (menu != null) {
                MenuItem todayItem = menu.findItem(R.id.action_today);
                if (todayItem != null)
                    todayItem.setTitle("Today (" + Calendar.getInstance().get(Calendar.DATE) + ")");
            }

            return true;

        } else if (mCurrentPage == R.string.action_profile) {
            // inflate the profile menu
            inflater.inflate(R.menu.profile, menu);
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_refresh:
                if (homeFragment != null) homeFragment.refresh();
                return true;

            case R.id.action_today:
                if (calendarFragment != null) {
                    calendarFragment.goToToday();
                    supportInvalidateOptionsMenu(); // update toolbar
                }
                return true;

            case R.id.action_one_day:
                if (calendarFragment != null) calendarFragment.setNumberDays(1);
                return true;

            case R.id.action_three_days:
                if (calendarFragment != null) calendarFragment.setNumberDays(3);
                return true;

            case R.id.action_five_days:
                if (calendarFragment != null) calendarFragment.setNumberDays(5);
                return true;

            case R.id.action_seven_days:
                if (calendarFragment != null) calendarFragment.setNumberDays(7);
                return true;

            case R.id.action_settings:
                if (mCurrentPage != R.string.action_profile) return false;

                // go to the settings page
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.right_slide_in, R.anim.expand_out_partial);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // if in the settings, go back to the profile
        if (mCurrentPage == R.id.action_settings) {
            onNavigationProfileClicked();
            return;
        }

        showConfirmDialog("Exit app?", "EXIT", "CANCEL", new ConfirmDialogCallback() {
            @Override
            public void onPositive() {
                finish();
            }
        });
    }
}
