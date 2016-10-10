package com.samdroid.common;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.samdroid.string.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

    public static boolean openEmailWithAttachment(Activity activity, String toEmail, String subject, String content, String filePath) {
        if (activity == null) return false;

        Intent email = new Intent(Intent.ACTION_SEND);
        email.setData(Uri.parse("mailto:" + toEmail)); // only email apps should handle this
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, content);

        try {
            if (Strings.isEmpty(filePath)) throw new NullPointerException();

            File file = new File(filePath);
            Uri uri = Uri.fromFile(file);
            email.putExtra(Intent.EXTRA_STREAM, uri);
            email.setType("application/pdf");

        } catch (Exception e) {
            email.setType("text/plain");
        }

        try {
            activity.startActivity(Intent.createChooser(email, "Send to Homefix"));
            return true;

        } catch (android.content.ActivityNotFoundException ex) {
            return false;
        }
    }

    public static boolean sendEmail(
            final Context context,
            final List<String> toEmails,
            final String emailSubject,
            final String emailBody,
            final ArrayList<String> attachments) {
        try {
            PackageManager pm = context.getPackageManager();
            ResolveInfo selectedEmailActivity = null;

            Intent emailDummyIntent = new Intent(Intent.ACTION_SENDTO);
            emailDummyIntent.setData(Uri.parse("mailto:" + VariableUtils.listToString(toEmails, ",")));

            List<ResolveInfo> emailActivities = pm.queryIntentActivities(emailDummyIntent, 0);
            if (null == emailActivities || emailActivities.size() == 0) {
                Intent emailDummyIntentRFC822 = new Intent(Intent.ACTION_SEND_MULTIPLE);
                emailDummyIntentRFC822.setType("message/rfc822");

                emailActivities = pm.queryIntentActivities(emailDummyIntentRFC822, 0);
            }

            if (null != emailActivities) {
                if (emailActivities.size() == 1) {
                    selectedEmailActivity = emailActivities.get(0);

                } else {
                    for (ResolveInfo currAvailableEmailActivity : emailActivities) {
                        if (currAvailableEmailActivity.isDefault) {
                            selectedEmailActivity = currAvailableEmailActivity;
                        }
                    }
                }

                if (null != selectedEmailActivity) {
                    // Send email using the only/default email activity
                    sendEmailUsingSelectedEmailApp(context, toEmails, emailSubject, emailBody, attachments, selectedEmailActivity);

                } else {
                    final List<ResolveInfo> emailActivitiesForDialog = emailActivities;

                    String[] availableEmailAppsName = new String[emailActivitiesForDialog.size()];
                    for (int i = 0; i < emailActivitiesForDialog.size(); i++) {
                        availableEmailAppsName[i] = emailActivitiesForDialog.get(i).activityInfo.applicationInfo.loadLabel(pm).toString();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Choose an email app");
                    builder.setItems(availableEmailAppsName, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendEmailUsingSelectedEmailApp(context, toEmails, emailSubject, emailBody, attachments, emailActivitiesForDialog.get(which));
                        }
                    });

                    builder.create().show();
                }

            } else {
                sendEmailUsingSelectedEmailApp(context, toEmails, emailSubject, emailBody, attachments, null);
            }

        } catch (Exception ex) {
            MyLog.e(IntentHelper.class.getSimpleName(), "Can't send email");
            MyLog.printStackTrace(ex);
            return false;
        }

        return true;
    }

    protected static boolean sendEmailUsingSelectedEmailApp(
            final Context context,
            final List<String> toEmails,
            final String emailSubject,
            final String emailBody,
            final ArrayList<String> attachments,
            final ResolveInfo selectedEmailApp) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

            String aEmailList[] = new String[toEmails.size()];
            aEmailList = toEmails.toArray(aEmailList);

            emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, null != emailSubject ? emailSubject : "");
            emailIntent.putExtra(Intent.EXTRA_TEXT, null != emailBody ? emailBody : "");

            if (null != attachments && attachments.size() > 0) {
                ArrayList<Uri> attachmentsUris = new ArrayList<>();

                // Convert from paths to Android friendly Parcelable Uri's
                for (String currAttachemntPath : attachments) {
                    File fileIn = new File(currAttachemntPath);
                    Uri currAttachemntUri = Uri.fromFile(fileIn);
                    attachmentsUris.add(currAttachemntUri);
                }
                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachmentsUris);
            }

            if (null != selectedEmailApp) {
                MyLog.d(IntentHelper.class.getSimpleName(), "Sending email using " + selectedEmailApp);

                emailIntent.setComponent(new ComponentName(selectedEmailApp.activityInfo.packageName, selectedEmailApp.activityInfo.name));
                context.startActivity(emailIntent);

            } else {
                Intent emailAppChooser = Intent.createChooser(emailIntent, "Select Email app");
                context.startActivity(emailAppChooser);
            }

        } catch (Exception ex) {
            MyLog.e(IntentHelper.class.getSimpleName(), "Error sending email");
            MyLog.printStackTrace(ex);
            return false;
        }

        return true;
    }

}
