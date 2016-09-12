package com.homefix.tradesman.timeslot;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ShareCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.homefix.tradesman.R;
import com.homefix.tradesman.common.ActivityHelper;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.IntentHelper;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 9/12/2016.
 */

public class HomefixServiceHelper {

    public static String getReadableLocationString(String delimiter, String addressLine1, String addressLine2, String addressLine3, String postcode, String country) {
        String s = "";

        delimiter = Strings.returnSafely(delimiter);

        // make sure the postcode is not in any of the address lines
        if (!Strings.isEmpty(addressLine1)) addressLine1 = addressLine1.replace(postcode, "");
        if (!Strings.isEmpty(addressLine2)) addressLine2 = addressLine2.replace(postcode, "");
        if (!Strings.isEmpty(addressLine3)) addressLine3 = addressLine3.replace(postcode, "");

        // combine the strings
        s += !Strings.isEmpty(addressLine1) ? addressLine1 + delimiter : "";
        s += !Strings.isEmpty(addressLine2) ? addressLine2 + delimiter : "";
        s += !Strings.isEmpty(addressLine3) ? addressLine3 + delimiter : "";
        s += !Strings.isEmpty(postcode) ? postcode + delimiter : "";
        s += !Strings.isEmpty(country) ? country : "";

        if (s.endsWith(delimiter)) s = s.substring(0, s.length() - 1);

        return s;
    }

    public static void onPhoneClicked(final Activity activity, String phone) {
        if (!ActivityHelper.canActivityDo(activity)) return;

        if (Strings.isEmpty(phone)) {
            MaterialDialogWrapper.getConfirmationDialog(
                    activity,
                    "No phone number for the customer. Open the dialer anyway?",
                    "OPEN DIALER",
                    "CANCEL",
                    new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();

                            IntentHelper.callPhoneNumber(activity, "");
                        }
                    },
                    new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    }).show();
            return;
        }

        IntentHelper.callPhoneNumber(activity, phone);
    }

    public static void onLocationClicked(Activity activity, Double latitude, Double longitude, String addressLine1, String addressLine2, String addressLine3, String postcode, String country) {
        if (!ActivityHelper.canActivityDo(activity)) return;

        // get directions
        if (latitude != null && longitude != null)
            IntentHelper.googleMapsDirections(activity, latitude, longitude);
        else
            IntentHelper.googleMapsDirections(
                    activity,
                    HomefixServiceHelper.getReadableLocationString(
                            ",",
                            addressLine1,
                            addressLine2,
                            addressLine3,
                            postcode,
                            country));
    }

    public static void onEmailClicked(Activity activity, String email, String message) {
        if (!ActivityHelper.canActivityDo(activity)) return;

        ShareCompat.IntentBuilder.from(activity)
                .setType("message/rfc822")
                .addEmailTo(email)
                .setSubject("Homefix")
                .setText(message)
                .startChooser();
    }

    public static void goToTimeslot(Activity activity, Timeslot timeslot, boolean goIntoEditMode) {
        if (activity == null || timeslot == null) return;

        Intent i = new Intent(activity, TimeslotActivity.class);

        Timeslot.TYPE type = Timeslot.TYPE.getTypeEnum(timeslot.getType());

        switch (type) {

            case SERVICE:
                i = null;
                break;

            case AVAILABILITY:
            case BREAK:
            case OWN_JOB:
                break;

            default:
                i = null;
                break;
        }

        if (i != null) {
            i.putExtra("timeslotKey", Timeslot.getSenderReceiver().put(timeslot));
            i.putExtra("type", type != null ? type.name() : Timeslot.TYPE.NONE.name());
            i.putExtra("goIntoEditMode", goIntoEditMode);
            activity.startActivityForResult(i, Ids.TIMESLOT_CHANGE);
            activity.overridePendingTransition(R.anim.right_slide_in, R.anim.expand_out_partial);
        }
    }
}
