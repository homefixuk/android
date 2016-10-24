package com.homefix.tradesman.timeslot.base_timeslot;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.homefix.tradesman.base.presenter.BaseFragmentPresenter;
import com.homefix.tradesman.calendar.HomeFixCal;
import com.homefix.tradesman.common.AnalyticsHelper;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.network.NetworkManager;
import com.samdroid.string.Strings;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samuel on 7/13/2016.
 */

public class BaseTimeslotFragmentPresenter<V extends BaseTimeslotView> extends BaseFragmentPresenter<V> {

    private Timeslot.TYPE mType;

    public BaseTimeslotFragmentPresenter(V availabilityView, Timeslot.TYPE type) {
        super(availabilityView);
        mType = type;
    }

    public void save(Timeslot timeslot, final Calendar mStart, final Calendar mEnd) {
        if (!isViewAttached()) return;

        // if the user has made no changes to the timeslot
        if (getView().getTimeslot() != null && !getView().hasMadeChanges()) {
            getView().onSaveComplete(timeslot);
            return;
        }

        String tradesmanId = FirebaseUtils.getCurrentTradesmanId();
        if (Strings.isEmpty(tradesmanId) || mStart == null || mEnd == null) {
            getView().showErrorDialog();
            return;
        }

        final boolean isNewTimeslot = timeslot == null;

        getView().showDialog((isNewTimeslot ? "Adding" : "Updating") + " timeslot...", true);

        final String timeslotKey;
        if (isNewTimeslot) {
            // create a new Timeslot record
            timeslotKey = FirebaseUtils.getTimeslotsRef().push().getKey();

            timeslot = new Timeslot(timeslotKey);
            timeslot.setType(mType.getName());

        } else {
            timeslotKey = timeslot.getId();
        }

        // always update the time from the view
        timeslot.setStartTime(mStart.getTimeInMillis());
        timeslot.setEndTime(mEnd.getTimeInMillis());

        if (Strings.isEmpty(timeslotKey)) {
            getView().showErrorDialog();
            return;
        }

        Map<String, Object> timeslotValues = timeslot.toMap();

        // save the timeslot to 2 locations
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/timeslots/" + timeslotKey, timeslotValues);
        childUpdates.put("/tradesmanTimeslots/" + tradesmanId + "/" + timeslotKey, timeslotValues);

        final Timeslot finalTimeslot = timeslot;

        OnSuccessListener<Void> onSuccessListener = new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // track the save
                Bundle b = new Bundle();
                b.putString("timeslotId", finalTimeslot.getId());
                b.putString("type", mType.getName());
                b.putLong("startTime", mStart.getTimeInMillis());
                b.putLong("endTime", mEnd.getTimeInMillis());
                AnalyticsHelper.track(
                        getView().getContext(),
                        isNewTimeslot ? "addTimeslot" : "editTimeslot",
                        b);

                DatabaseReference ref = FirebaseUtils.getSpecificTimeslotRef(timeslotKey);
                if (ref == null) {
                    getView().onSaveComplete(finalTimeslot);
                    return;
                }

                // fetch the saved Timeslot
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Timeslot timeslot1 = dataSnapshot != null ? dataSnapshot.getValue(Timeslot.class) : finalTimeslot;
                        timeslot1.setId(dataSnapshot != null ? dataSnapshot.getKey() : finalTimeslot.getId());
                        getView().onSaveComplete(timeslot1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        getView().onSaveComplete(finalTimeslot);
                    }
                });
            }
        };

        Task<Void> task = FirebaseUtils.getBaseRef().updateChildren(childUpdates);
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                getView().showErrorDialog();
            }
        });
        task.addOnSuccessListener(onSuccessListener);

        // when there's no network connection Firebase won't trigger callbacks
        // http://sumatodev.com/implement-offline-support-android-using-firebase/
        if (!NetworkManager.hasConnection(getView().getContext())) {
            onSuccessListener.onSuccess(null);
        }
    }

    public void delete(final Timeslot timeslot) {
        if (!isViewAttached()) return;

        final String tradesmanId = FirebaseUtils.getCurrentTradesmanId();

        if (Strings.isEmpty(tradesmanId) || timeslot == null || Strings.isEmpty(timeslot.getId())) {
            getView().showErrorDialog();
            return;
        }

        MaterialDialogWrapper.getNegativeConfirmationDialog(
                getView().getBaseActivity(),
                "Are you sure you want to delete this event?",
                "DELETE",
                "CANCEL",
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getView().showDialog("Deleting timeslot...", true);

                        // delete the timeslot from the 2 locations
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/timeslots/" + timeslot.getId(), null);
                        childUpdates.put("/tradesmanTimeslots/" + tradesmanId + "/" + timeslot.getId(), null);

                        // if the timeslot has a service, remove it too
                        if (Timeslot.TYPE.OWN_JOB.getName().equals(timeslot.getType()) && !Strings.isEmpty(timeslot.getServiceId())) {
                            childUpdates.put("/services/" + timeslot.getServiceId(), null);
                            childUpdates.put("/tradesmanServiceTimeslots/" + tradesmanId + "/" + timeslot.getId(), null);
                        }

                        Task<Void> task = FirebaseUtils
                                .getBaseRef()
                                .updateChildren(childUpdates);

                        OnSuccessListener<Void> onSuccessListener = new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // track the delete
                                Bundle b = new Bundle();
                                b.putString("timeslotId", timeslot.getId());
                                b.putString("type", mType.getName());
                                AnalyticsHelper.track(
                                        getView().getContext(),
                                        "deleteTimeslot",
                                        b);

                                // remove the original timeslot
                                HomeFixCal.removeEvent(timeslot);

                                getView().onDeleteComplete(timeslot);
                            }
                        };

                        task.addOnSuccessListener(onSuccessListener);
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                getView().showErrorDialog();
                            }
                        });

                        // when there's no network connection Firebase won't trigger callbacks
                        // http://sumatodev.com/implement-offline-support-android-using-firebase/
                        if (!NetworkManager.hasConnection(getView().getContext())) {
                            onSuccessListener.onSuccess(null);
                        }

                    }

                }, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

}
