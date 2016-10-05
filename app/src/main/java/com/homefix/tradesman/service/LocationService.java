package com.homefix.tradesman.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.homefix.tradesman.common.PermissionsHelper;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.listener.OnNewLocationListener;
import com.samdroid.common.MyLog;
import com.samdroid.common.TimeUtils;
import com.samdroid.common.VariableUtils;
import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class LocationService extends Service {

    private static final String TAG = LocationService.class.getSimpleName();

    public static final int MIN_TIME_REQUEST = 60 * 1000; // milliseconds
    public static final float MIN_DISTANCE = 0f; // metres
    private static final long MIN_LOCATION_UPDATE_TIME = TimeUtils.getMinutesInMillis(2);

    private static Location currentLocation, prevLocation;
    private static DetectedActivity currentActivity;

    private static String provider = LocationManager.GPS_PROVIDER;

    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private static ArrayList<OnNewLocationListener> arrOnNewLocationListener = new ArrayList<>();
    private static boolean isListenerAttached = false;
    private static long lastLocationTime;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.e(TAG, "[onStartCommand]");

        if (!hasLocationPermissionsGranted(getApplicationContext())) return START_NOT_STICKY;

        stopLocationListener(getApplicationContext());

        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // if the location provider is enabled
        if (locationManager.isProviderEnabled(provider)) {
            try {
                locationManager.requestLocationUpdates(
                        provider,
                        MIN_TIME_REQUEST,
                        MIN_DISTANCE,
                        getLocationListener());
                isListenerAttached = true;

                Location gotLoc = getLastKnownLocation(getApplicationContext());
                gotLocation(gotLoc);

            } catch (SecurityException e) {
                MyLog.printStackTrace(e);
            }

        } else {
            Toast t = Toast.makeText(getApplicationContext(), "Please turn on your GPS", Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();

            Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(settingsIntent);
        }

        try {
            SmartLocation.with(getApplicationContext()).activity()
                    .start(new OnActivityUpdatedListener() {
                        @Override
                        public void onActivityUpdated(DetectedActivity detectedActivity) {
                            setCurrentActivity(detectedActivity);
                        }
                    });
        } catch (Exception e) {
            MyLog.printStackTrace(e);
        }

        return START_STICKY;
    }

    private static boolean hasLocationPermissionsGranted(Context context) {
        return PermissionsHelper.hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                && PermissionsHelper.hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private static LocationListener getLocationListener() {
        if (locationListener == null) locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                try {
                    String strStatus = "";
                    switch (status) {
                        case GpsStatus.GPS_EVENT_FIRST_FIX:
                            strStatus = "GPS_EVENT_FIRST_FIX";
                            break;
                        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                            strStatus = "GPS_EVENT_SATELLITE_STATUS";
                            break;
                        case GpsStatus.GPS_EVENT_STARTED:
                            strStatus = "GPS_EVENT_STARTED";
                            break;
                        case GpsStatus.GPS_EVENT_STOPPED:
                            strStatus = "GPS_EVENT_STOPPED";
                            break;
                        default:
                            strStatus = String.valueOf(status);
                            break;
                    }
                    MyLog.e(TAG, "Status: " + strStatus);

                } catch (Exception e) {
                    MyLog.printStackTrace(e);
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {
                try {
                    gotLocation(location);

                } catch (Exception e) {
                    MyLog.printStackTrace(e);
                }
            }
        };

        return locationListener;
    }

    private static Location getLastKnownLocation(Context context) {
        if (!hasLocationPermissionsGranted(context)) return null;

        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        Location bestLocation = null;

        try {
            List<String> providers = locationManager.getProviders(true);
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) continue;

                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }

        } catch (SecurityException e) {
            MyLog.printStackTrace(e);
        }

        return bestLocation;
    }

    private static void gotLocation(Location location) {
        prevLocation = currentLocation == null ? null : new Location(currentLocation);
        currentLocation = location;
        if (isLocationNew()) {
            OnNewLocationReceived(location);

            MyLog.e(TAG, location.toString());
            return;
        }

        MyLog.e(TAG, "[gotLocation] location is not new: " + (location != null ? location.toString() : "<null>"));
    }

    private static boolean isLocationNew() {
        if (currentLocation == null) return false;
        else if (prevLocation == null) return true;
        else if (currentLocation.getTime() == prevLocation.getTime()) return false;

        return true;
    }

    public static void stopLocationListener(Context context) {
        if (locationManager == null) return;

        try {
            locationManager.removeUpdates(getLocationListener());
        } catch (SecurityException e) {
            MyLog.printStackTrace(e);
        }

        try {
            SmartLocation.with(context).activity().stop();
        } catch (Exception e) {
        }

        isListenerAttached = false;
    }

    // Allows the user to set a OnNewLocationListener outside of this class
    // and react to the event
    public static void addOnNewLocationListener(OnNewLocationListener listener) {
        if (listener == null) return;

        arrOnNewLocationListener.add(listener);

        // trigger the listener with the current location
        if (currentLocation != null) listener.onNewLocationReceived(currentLocation);
    }

    public static void removeOnNewLocationListener(OnNewLocationListener listener) {
        arrOnNewLocationListener.remove(listener);
    }

    private static void clearOnNewLocationListeners() {
        arrOnNewLocationListener.clear();
    }

    private static String getDetectedActivityName(DetectedActivity detectedActivity) {
        String s = DetectedActivity.zztz(detectedActivity.getType());

        switch (s) {
            case "IN_VEHICLE":
                return "in_vehicle";
            case "STILL":
                return "still";
            case "WALKING":
            case "ON_FOOT":
                return "walking";
            case "RUNNING":
                return "running";
            default:
                return "unknown";
        }
    }

    /**
     * Called after the new location received
     */
    private static void OnNewLocationReceived(Location location) {
        String tradesmanId = FirebaseUtils.getCurrentTradesmanId();
        if (location != null && !Strings.isEmpty(tradesmanId)) {
            long time = System.currentTimeMillis();

            // if it has not been enough time between updates, do not send the updated location
            if (time < lastLocationTime + MIN_LOCATION_UPDATE_TIME) return;
            lastLocationTime = time;

            // send the update to the server
            final HashMap<String, Object> locationMap = new HashMap<>();
            locationMap.put("time", time);
            locationMap.put("latitude", location.getLatitude());
            locationMap.put("longitude", location.getLongitude());

            if (currentActivity != null)
                locationMap.put("activity", getDetectedActivityName(currentActivity));

            FirebaseUtils
                    .getBaseRef()
                    .child("tradesmanLocations")
                    .child(tradesmanId)
                    .child("" + time)
                    .updateChildren(locationMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                MyLog.printStackTrace(databaseError.toException());
                                return;
                            }

                            MyLog.e(TAG, "Tradesman Location updated");
                        }
                    });
        }

        notifyListeners(location);
    }

    private static void notifyListeners(Location location) {
        if (location == null || arrOnNewLocationListener == null || arrOnNewLocationListener.size() == 0)
            return;

        // Only trigger the event, when we have any listener
        for (int i = arrOnNewLocationListener.size() - 1; i >= 0; i--) {
            arrOnNewLocationListener.get(i).onNewLocationReceived(location);
        }
    }

    public static Location getCurrentLocation() {
        return currentLocation;
    }

    public static Location getPrevLocation() {
        return prevLocation;
    }

    public synchronized static DetectedActivity getCurrentActivity() {
        return currentActivity;
    }

    private synchronized static void setCurrentActivity(DetectedActivity currentActivity) {
        LocationService.currentActivity = currentActivity;
    }

    public static boolean isRunning() {
        return locationManager != null && locationListener != null && isListenerAttached;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopLocationListener(getApplicationContext());
    }

}