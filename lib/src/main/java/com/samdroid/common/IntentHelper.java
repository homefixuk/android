package com.samdroid.common;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.samdroid.string.Strings;

import java.io.File;
import java.util.Locale;

public class IntentHelper {

    /**
     * Start an intent go open the users web browser at the URL
     *
     * @param activity
     * @param url
     */
    public static void goToWebURL(@NonNull Activity activity, String url) {
        if (Strings.isEmptyTrimmed(url)) return;

        // Note: avoids Activity not found for intent
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "http://" + url;
        }

        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(i);
    }

    /**
     * Open google maps with directions to a geo point
     *
     * @param context
     * @param latitude
     * @param longitude
     */
    public static void googleMapsDirections(Context context, double latitude, double longitude) {
        if (context == null) return;

        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", latitude, longitude, latitude + "," + longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        try {
            context.startActivity(intent);

        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(unrestrictedIntent);

            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(context, "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Open google maps with directions to a location
     *
     * @param context
     * @param locationName
     */
    public static void googleMapsDirections(Context context, String locationName) {
        if (context == null) return;

        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%s", locationName);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

        try {
            context.startActivity(intent);

        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(unrestrictedIntent);

            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(context, "Please install a maps or internet browser application", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * @param intent
     * @param extraName
     * @return safely the string in the intent, or an empty string
     */
    public static String getStringSafely(@Nullable Intent intent, @NonNull String extraName) {
        if (intent == null || Strings.isEmptyTrimmed(extraName) || !intent.hasExtra(extraName))
            return "";

        return Strings.returnSafely(intent.getStringExtra(extraName));
    }

    public static long getLongSafely(@Nullable Intent intent, @NonNull String extraName, long defaultVal) {
        if (intent == null || !intent.hasExtra(extraName)) return defaultVal;

        try {
            return intent.getLongExtra(extraName, defaultVal);
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static boolean getBooleanSafely(@Nullable Intent intent, @NonNull String extraName, boolean defaultVal) {
        if (intent == null || !intent.hasExtra(extraName)) return defaultVal;

        return intent.getBooleanExtra(extraName, defaultVal);
    }

    public static void callPhoneNumber(Context context, String num) {
        if (context == null) return;

        // open the dialer
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", num, null));
        context.startActivity(intent);
    }

    public static void openEmailWithAttachment(Context context, String toEmail, String subject, String content, String filePath) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, content);

        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("application/pdf");
        email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(email);
    }

}
