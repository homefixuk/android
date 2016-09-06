package com.homefix.tradesman.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ListCallback;
import com.homefix.tradesman.R;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import butterknife.ButterKnife;
import clojure.lang.Obj;

public class MaterialDialogWrapper {

    /**
     * Get a MaterialDialog builder with the positive confirm and negative other strings,
     * where the confirm is green and the other is red
     *
     * @param context
     * @param message
     * @return a confirm dialog
     */
    public static MaterialDialog.Builder getConfirmationDialog(
            Activity context,
            String message,
            String positive,
            String negative) {
        return new MaterialDialog.Builder(context)
                .content(message)
                .positiveText(positive)
                .negativeText(negative)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.black);
    }

    /**
     * Get a MaterialDialog builder with the positive confirm and negative other strings,
     * where the confirm is green and the other is red
     *
     * @param context
     * @param message
     * @return a confirm dialog
     */
    public static MaterialDialog getConfirmationDialog(
            Activity context,
            String message,
            String positive,
            String negative,
            MaterialDialog.SingleButtonCallback onPositive,
            MaterialDialog.SingleButtonCallback onNegative) {
        return getConfirmationDialog(context, message, positive, negative, R.color.colorPrimary, R.color.black, onPositive, onNegative, true);
    }

    /**
     * Get a MaterialDialog builder with the positive confirm and negative other strings,
     * where the confirm and cancel text colour is specified
     *
     * @param context
     * @param message
     * @param positive
     * @param negative
     * @param positiveResId
     * @param negativeResId
     * @param autoDismiss
     * @return
     */
    public static MaterialDialog getConfirmationDialog(
            Activity context,
            String message,
            String positive,
            String negative,
            int positiveResId,
            int negativeResId,
            MaterialDialog.SingleButtonCallback onPositive,
            MaterialDialog.SingleButtonCallback onNegative,
            boolean autoDismiss) {

        return new MaterialDialog.Builder(context)
                .content(message)
                .positiveText(positive)
                .negativeText(negative)
                .positiveColorRes(positiveResId)
                .negativeColorRes(negativeResId)
                .onPositive(onPositive)
                .onNegative(onNegative)
                .autoDismiss(autoDismiss)
                .canceledOnTouchOutside(false)
                .build();
    }

    /**
     * Get a MaterialDialog builder with the confirm and other strings, where
     * the confirm is red and the other is black
     *
     * @param context
     * @param message
     * @return a confirm dialog
     */
    public static MaterialDialog.Builder getNegativeConfirmationDialog(
            Activity context,
            String message,
            String confirm,
            String other,
            MaterialDialog.SingleButtonCallback onConfirmNegative,
            MaterialDialog.SingleButtonCallback onOther) {
        return new MaterialDialog.Builder(context)
                .content(message)
                .positiveText(confirm)
                .negativeText(other)
                .positiveColorRes(R.color.colorAccent)
                .negativeColorRes(R.color.black)
                .onPositive(onConfirmNegative)
                .onNegative(onOther);
    }

    /**
     * @param context
     * @param message
     * @return a simple alert dialog with a positive button
     */
    public static MaterialDialog.Builder getAlertDialog(
            Activity context,
            String message) {
        return new MaterialDialog.Builder(context)
                .content(Strings.returnSafely(message))
                .positiveText("Ok")
                .positiveColorRes(R.color.colorPrimary);
    }

    /**
     * Show an alert dialog
     *
     * @param context
     * @param message
     */
    public static void showAlertDialog(Activity context, String message) {
        getAlertDialog(context, message).show();
    }

    /**
     * Class to show an alert dialog when something is clicked
     */
    public static class AlertDialogOnClickListener implements OnClickListener {

        private Activity context;
        private String message;

        public AlertDialogOnClickListener(Activity context, String message) {
            this.context = context;
            this.message = message;
        }

        @Override
        public void onClick(View v) {
            if (context == null) return;

            showAlertDialog(context, Strings.returnSafely(message));
        }
    }

    /**
     * @param context
     * @param message
     * @return a simple dialog just showing a message, with no actions
     */
    public static MaterialDialog.Builder getPlainDialog(
            Activity context,
            String message) {

        return new MaterialDialog.Builder(context)
                .content(message);
    }

    /**
     * @param context
     * @param message
     * @param onPositive
     * @return a simple alert dialog with a callback that calls the positive selection
     */
    public static MaterialDialog.Builder getAlertDialog(
            Activity context,
            String message,
            MaterialDialog.SingleButtonCallback onPositive) {

        return new MaterialDialog.Builder(context)
                .content(message)
                .positiveText("Ok")
                .positiveColorRes(R.color.colorPrimary)
                .autoDismiss(false)
                .cancelable(false)
                .onPositive(onPositive);
    }

    /**
     * @param context
     * @param title
     * @return a simple loading dialog
     */
    public static MaterialDialog.Builder getLoadingDialog(Activity context, String title) {
        return new MaterialDialog.Builder(context)
                .title(title)
                .customView(R.layout.loading_view_padded, false)
                .cancelable(false);
    }

    public interface SubmitObjectChangesCallback {

        public void onChangeSubmitted(Object original, Object changed);

        public void onChangeCancelled(Object original);

    }

    /**
     * Multi input dialog. Pass in a map with the fields you want the user to input, the callback
     * will return the hashmap with the updated (or same if the user cancels) values
     *
     * @param activity
     * @param positiveTxt
     * @param keys
     * @param defaults
     * @param callback
     * @return
     */
    @SuppressLint("InflateParams")
    public static MaterialDialog getMultiInputDialog(
            final Activity activity,
            final String positiveTxt,
            final List<String> keys,
            final List<String> defaults,
            final OnGotObjectListener<HashMap<String, String>> callback) {

        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.empty_vertical_scroll_view, null, false);
        final LinearLayout content = ButterKnife.findById(view, R.id.content);

        EditText firstEdtTxt = null;

        final HashMap<String, String> defaultMaps = new HashMap<>();

        for (int i = 0; i < keys.size(); i++) {
            String key = Strings.returnSafely(keys.get(i));

            if (Strings.isEmpty(key)) continue;

            // set the current content
            EditText edtTxt = (EditText) inflater.inflate(R.layout.edit_text_padded, null);
            edtTxt.setHint(Strings.returnSafely(key));

            String value = Strings.returnSafely(defaults.get(i));

            edtTxt.setText(value);
            edtTxt.setTag(key);

            edtTxt.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

            defaultMaps.put(key, value);

            if (firstEdtTxt == null) firstEdtTxt = edtTxt;

            content.addView(edtTxt);
        }

        final EditText finalFirstEdtTxt = firstEdtTxt;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .customView(view, true)
                .positiveText(positiveTxt)
                .positiveColorRes(R.color.black)
                .negativeText("CANCEL")
                .negativeColorRes(R.color.black)
                .showListener(new OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {
                        finalFirstEdtTxt.setSelection(finalFirstEdtTxt.getText().length());
                        finalFirstEdtTxt.requestFocus();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        HashMap<String, String> results = new HashMap<>();

                        int childCount = content.getChildCount();
                        EditText editText;
                        String key;
                        for (int i = 0; i < childCount; i++) {
                            editText = (EditText) content.getChildAt(i);
                            key = (String) editText.getTag();
                            results.put(key, editText.getText().toString());
                        }

                        if (dialog != null) dialog.dismiss();

                        if (callback != null) callback.onGotThing(results);
                    }

                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        if (dialog != null) dialog.dismiss();
                        if (callback != null) callback.onGotThing(defaultMaps);
                    }

                })
                .autoDismiss(true)
                .cancelable(false);

        return builder.build();
    }


    @SuppressLint("InflateParams")
    public static MaterialDialog getEditTextDialog(
            final Activity context,
            final String initialString,
            final String hint,
            final String positiveTxt,
            final SubmitObjectChangesCallback callback) {
        return getEditTextDialog(
                context,
                "",
                initialString,
                hint,
                positiveTxt,
                0,
                callback
        );
    }

    @SuppressLint("InflateParams")
    public static MaterialDialog getEditTextDialog(
            final Activity context,
            final String title,
            final String initialString,
            final String hint,
            final String positiveTxt,
            final int inputType,
            final SubmitObjectChangesCallback callback) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.edit_text_padded, null, false);

        // set the current content
        final EditText edtTxt = ButterKnife.findById(view, R.id.edit_text);
        edtTxt.setHint(Strings.returnSafely(hint));
        edtTxt.setText(Strings.returnSafely(initialString));
        edtTxt.setInputType(inputType);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .customView(view, true)
                .positiveText(positiveTxt)
                .positiveColorRes(R.color.black)
                .negativeText("CANCEL")
                .negativeColorRes(R.color.black)
                .showListener(new OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {
                        edtTxt.setSelection(edtTxt.getText().length());
                        edtTxt.requestFocus();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        if (edtTxt == null) {
                            try {
                                getAlertDialog(
                                        context,
                                        "Sorry, unable to submit this right now. " +
                                                "Please try again later")
                                        .build()
                                        .show();

                            } catch (Exception e) {
                            }

                            return;
                        }

                        if (callback != null)
                            callback.onChangeSubmitted(
                                    initialString,
                                    Strings.returnSafely(edtTxt.getText().toString()));
                    }

                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        if (callback != null)
                            callback.onChangeCancelled(initialString);
                    }

                })
                .autoDismiss(true)
                .cancelable(false);

        if (!Strings.isEmpty(title)) builder.title(title);

        return builder.build();
    }

    public static MaterialDialog getListDialog(
            final Activity context,
            String title,
            CharSequence[] items,
            ListCallback itemsCallback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        return builder
                .title(title)
                .items(items)
                .itemsCallback(itemsCallback)
                .build();
    }

    public static MaterialDialog getDateTimeChangeDialog(
            final Activity context,
            String title,
            final Date date,
            final SubmitObjectChangesCallback callback) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        return builder
                .title(title)
                .items(new CharSequence[]{"Date", "Time"})
                .autoDismiss(true)
                .itemsCallback(new ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        if (text.equals("Date")) {
                            new DatePickerDialog(
                                    context,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                            if (callback == null) return;

                                            Calendar calNew = (Calendar) cal.clone();
                                            calNew.set(Calendar.YEAR, year);
                                            calNew.set(Calendar.MONTH, monthOfYear);
                                            calNew.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                            callback.onChangeSubmitted(date, calNew.getTime());
                                        }

                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)

                            ).show();

                        } else if (text.equals("Time")) {
                            new TimePickerDialog(
                                    context,
                                    new TimePickerDialog.OnTimeSetListener() {

                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            if (callback == null) return;

                                            Calendar calNew = (Calendar) cal.clone();
                                            calNew.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            calNew.set(Calendar.MINUTE, minute);
                                            callback.onChangeSubmitted(date, calNew.getTime());
                                        }

                                    },
                                    cal.get(Calendar.HOUR_OF_DAY),
                                    cal.get(Calendar.MINUTE),
                                    false)
                                    .show();
                        }
                    }

                })
                .build();
    }

}
