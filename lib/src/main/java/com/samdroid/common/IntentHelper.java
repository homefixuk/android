package com.samdroid.common;

import android.app.Activity;
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
	public static void goToWebURL (@NonNull Activity activity, String url) {
		if (Strings.isEmptyTrimmed(url)) return;
		
		// Note: avoids Activity not found for intent
		if (!url.startsWith("https://") && !url.startsWith("http://")){
		    url = "http://" + url;
		}
		
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		activity.startActivity(i);
	}

	/**
	 * @param intent
	 * @param extraName
	 * @return safely the string in the intent, or an empty string
	 */
	public static String getStringSafely (@Nullable Intent intent, @NonNull String extraName) {
		if (intent == null || Strings.isEmptyTrimmed(extraName) || !intent.hasExtra(extraName)) return "";

        return Strings.returnSafely(intent.getStringExtra(extraName));
	}

}
