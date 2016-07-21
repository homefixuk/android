package com.samdroid.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.samdroid.string.Strings;

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
        googleMapsDirections(context, latitude + "," + longitude);
    }

    /**
     * Open google maps with directions to a location
     *
     * @param context
     * @param locationName
     */
    public static void googleMapsDirections(Context context, String locationName) {
        if (context == null) return;

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Strings.returnSafely(locationName));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
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

}
