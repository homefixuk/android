package com.homefix.tradesman.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.homefix.tradesman.base.view.BaseActivityView;
import com.homefix.tradesman.base.view.BaseView;
import com.samdroid.string.Strings;

import java.util.Arrays;
import java.util.List;

/**
 * Created by samuel on 4/11/2016.
 */
public class PermissionsHelper {

    public static final List<String> DANGEROUS_PERMISSIONS =
            Arrays.asList(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            );

    public static final List<String> REQUIRED_PERMISSIONS =
            Arrays.asList(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
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
     * @return if the app is missing a permission needed to run the app
     */
    public static boolean isMissingRequiredPermission(Activity activity) {
        // make sure the user has granted us all permissions
        for (String s : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED)
                return true;
        }

        return false;
    }

    /**
     * @param activity
     * @param permission
     * @return if the app is missing the given permission
     */
    public static boolean isMissingPermission(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @param context
     * @param permission
     * @return if we have a permission
     */
    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
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

    public static class PermissionClickListener implements View.OnClickListener {

        BaseActivityView view;
        String permission;
        String message;
        int requestCode;
        boolean fromPermissionResults;

        public PermissionClickListener(BaseActivityView view, String permission, String message, int requestCode, boolean fromPermissionResults) {
            this.view = view;
            this.permission = permission;
            this.message = message;
            this.requestCode = requestCode;
            this.fromPermissionResults = fromPermissionResults;
        }

        @Override
        public void onClick(View v) {
            if (view == null || Strings.isEmpty(permission)) return;

            if (ContextCompat.checkSelfPermission(view.getContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {

                // should we show an explanation? (because we have already been denied by the user
                if (ActivityCompat.shouldShowRequestPermissionRationale(view.getBaseActivity(), permission)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    view.showConfirmDialog(
                            Strings.returnSafely(message),
                            "REQUEST PERMISSION",
                            "CANCEL",
                            new BaseView.ConfirmDialogCallback() {

                                @Override
                                public void onPositive() {
                                    PermissionsHelper.requestPermission(view.getBaseActivity(), permission, requestCode);
                                }
                            });

                } else if (fromPermissionResults) {
                    view.showConfirmDialog(
                            "HomeFix needs this permission to work and give you the best service." +
                                    "\n\n" +
                                    "Please enable this permission in the app settings on your device.",
                            "GO TO SETTINGS",
                            "CANCEL",
                            new BaseView.ConfirmDialogCallback() {

                                @Override
                                public void onPositive() {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", view.getBaseActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    view.getBaseActivity().startActivity(intent);
                                }

                            });

                } else {
                    // else no explanation needed, we can request the permission //
                    PermissionsHelper.requestPermission(view.getBaseActivity(), permission, requestCode);
                }
            }
        }

    }

}
