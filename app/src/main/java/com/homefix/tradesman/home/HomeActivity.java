package com.homefix.tradesman.home;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import com.samdroid.network.NetworkManager;
import com.samdroid.string.Strings;
import com.samdroid.thread.MyThreadPool;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by samuel on 6/22/2016.
 */

public class HomeActivity extends BaseToolbarNavMenuActivity<HomeView, HomePresenter> implements HomeView {

    private int mCurrentPage;
    private HomeFragment homeFragment;
    private CalendarFragment<HomeActivity> calendarFragment;

    // internet
    public static int TYPE_WIFI = 1, TYPE_MOBILE = 2, TYPE_NOT_CONNECTED = 0;
    private boolean internetConnected = true;
    private boolean clickedNoNetworkConnection = false;

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

    @Override
    public void onResume() {
        super.onResume();
        if (clickedNoNetworkConnection) {
            setSnackbarMessage(getConnectivityStatus(getContext()), true);
            clickedNoNetworkConnection = false;
        }
        registerInternetCheckReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    /**
     * Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = getConnectivityStatus(context);
            setSnackbarMessage(status, true);
        }
    };

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) status = "Wifi enabled";
        else if (conn == TYPE_MOBILE) status = "Mobile data enabled";
        else if (conn == TYPE_NOT_CONNECTED) status = "Not connected to Internet";

        return status;
    }

    private void setSnackbarMessage(int status, boolean showBar) {
        if (status == -1) return;

        String internetStatus = "";
        if (status == TYPE_WIFI || status == TYPE_MOBILE) {
            internetStatus = "Internet Connected";
        } else {
            internetStatus = "No Network Connection";
        }

        snackbar = Snackbar.make(drawerLayout, internetStatus, Snackbar.LENGTH_LONG);
        snackbar.setAction("X", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });

        // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = ButterKnife.findById(sbView, android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        if (status == TYPE_WIFI || status == TYPE_MOBILE) {
            if (!internetConnected) {
                internetConnected = true;
                if (showBar) {
                    sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                    sbView.setOnClickListener(null);
                    snackbar.setDuration(Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

        } else {
            if (internetConnected) {
                internetConnected = false;
                if (showBar) {
                    sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                    sbView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // open their network connection settings
                            try {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                                startActivity(intent);

                            } catch (Exception e1) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_MAIN);
                                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } catch (Exception e2) {
                                }
                            }

                            clickedNoNetworkConnection = true;
                        }
                    });
                    snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                }
            }
        }
    }

}
