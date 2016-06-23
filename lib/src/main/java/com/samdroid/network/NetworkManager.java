package com.samdroid.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Network Manager class to check for an Internet connection
 *
 * @author Sam Koch
 */
public class NetworkManager {

    /**
     * Checks if the device has Internet connection
     *
     * @return if the phone is connected to the Internet
     */
    public static boolean hasConnection(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifiNetwork != null && wifiNetwork.isConnected()) {
                    return true;
                }

                NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (mobileNetwork != null && mobileNetwork.isConnected()) {
                    return true;
                }

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnected()) {
                    return true;
                }

            } else {
                Network[] networks = cm.getAllNetworks();
                NetworkInfo info;
                for (Network network : networks) {
                    if (network != null
                            && (info = cm.getNetworkInfo(network)) != null
                            && info.isConnected()) {
                        return true;
                    }
                }

            }

        } catch (Exception e) {
        }

        return false;
    }

}
