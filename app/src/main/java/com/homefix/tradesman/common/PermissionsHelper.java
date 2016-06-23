package com.homefix.tradesman.common;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.samdroid.string.Strings;

import java.util.Arrays;
import java.util.List;

/**
 * Created by samuel on 4/11/2016.
 */
public class PermissionsHelper {

    public static final List<String> DANGEROUS_PERMISSIONS =
            Arrays.asList(
                   // Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    //Manifest.permission.READ_EXTERNAL_STORAGE
                    );

    /**
     * @param activity
     * @return if the app is missing a core permission the user has to grant manually
     */
    public static boolean isMissingPermission(Activity activity) {
        // make sure the user has granted us all permissions
        for (String s : DANGEROUS_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED)
                return true;
        }

        return false;
    }

    /**
     * @param activity
     * @param permission
     * @return if we have a permission
     */
    public static boolean hasPermission(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request a permission
     *
     * @param activity
     * @param permission
     * @param requestCode
     */
    public static void requestPermission(Activity activity, String permission, int requestCode) {
        if (activity == null || Strings.isEmpty(permission)) return;

        ActivityCompat.requestPermissions(activity,
                new String[]{permission},
                requestCode);
    }

}
