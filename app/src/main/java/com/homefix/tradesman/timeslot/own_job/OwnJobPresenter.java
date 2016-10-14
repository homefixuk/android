package com.homefix.tradesman.timeslot.own_job;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.homefix.tradesman.calendar.HomeFixCal;
import com.homefix.tradesman.common.AnalyticsHelper;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.model.ServiceSet;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.timeslot.base_timeslot.BaseTimeslotFragmentPresenter;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.network.NetworkManager;
import com.samdroid.string.Strings;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samuel on 7/19/2016.
 */

public class OwnJobPresenter extends BaseTimeslotFragmentPresenter<OwnJobView> {

    public OwnJobPresenter(OwnJobView view) {
        super(view, Timeslot.TYPE.OWN_JOB);
    }

    public void addNewJob(
            final Calendar start, final Calendar end, final String jobType, final String addressLine1, String addressLine2,
            String addressLine3, final String postcode, final String country, Double latitude, Double longitude,
            final String customerName, final String customerEmail, final String customerPhone, final String customerPropertyRelationship, final String description) {

        if (!isViewAttached()) return;

        getView().showDialog("Creating new job...", true);

        FirebaseUtils.createService(true, start, end, jobType, addressLine1, addressLine2, addressLine3, postcode, country, latitude, longitude,
                customerName, customerEmail, customerPhone, customerPropertyRelationship, description, new OnGotObjectListener<Timeslot>() {

                    @Override
                    public void onGotThing(Timeslot o) {
                        if (!isViewAttached()) return;

                        if (o == null) {
                            getView().showDialog("Sorry, something went wrong", false);
                            return;
                        }

                        // update the view
                        getView().setTimeslot(o);
                        getView().setEditing(false);
                        getView().setupView();

                        // track the add
                        Bundle b = new Bundle();
                        b.putString("timeslotId", o.getId());
                        b.putString("type", o.getType());
                        b.putBoolean("isOwnJob", true);
                        b.putLong("startTime", start.getTimeInMillis());
                        b.putLong("endTime", end.getTimeInMillis());
                        b.putString("jobType", Strings.returnSafely(jobType));
                        b.putString("addressLine1", Strings.returnSafely(addressLine1));
                        b.putString("postcode", Strings.returnSafely(postcode));
                        b.putString("country", Strings.returnSafely(country));
                        b.putString("customerName", Strings.returnSafely(customerName));
                        b.putString("customerEmail", Strings.returnSafely(customerEmail));
                        b.putString("customerPhone", Strings.returnSafely(customerPhone));
                        b.putString("customerPropertyRelationship", Strings.returnSafely(customerPropertyRelationship));
                        b.putString("description", Strings.returnSafely(description));
                        AnalyticsHelper.track(
                                getView().getContext(),
                                "addTimeslot",
                                b);

                        getView().hideDialog();
                    }
                });
    }

    public void updateJob(
            final String timeslotId,
            final String serviceId,
            final String serviceSetId,
            final String customerId,
            final String propertyId,
            final String customerPropertyInfoId,
            final Calendar start, final Calendar end, final String jobType, final String addressLine1, String addressLine2,
            String addressLine3, final String postcode, final String country, Double latitude, Double longitude,
            final String customerName, final String customerEmail, final String customerPhone, final String customerPropertyRelationship, final String description) {

        if (!isViewAttached()) return;

        FirebaseUtils.updateJob(
                timeslotId, serviceId, serviceSetId, customerId, propertyId, customerPropertyInfoId,
                true, start, end, jobType, addressLine1, addressLine2, addressLine3, postcode, country, latitude, longitude,
                customerName, customerEmail, customerPhone, customerPropertyRelationship, description, new OnGotObjectListener<Timeslot>() {

                    @Override
                    public void onGotThing(Timeslot o) {
                        if (!isViewAttached()) return;

                        if (o == null) {
                            getView().showDialog("Sorry, unable to update your job", false);
                            return;
                        }

                        // update the view
//                        getView().setTimeslot(o);
                        getView().setEditing(false);
                        getView().setupView();

                        // track the add
                        Bundle b = new Bundle();
                        b.putString("timeslotId", o.getId());
                        b.putBoolean("isOwnJob", true);
                        b.putString("serviceId", Strings.returnSafely(serviceId));
                        b.putString("serviceSetId", Strings.returnSafely(serviceSetId));
                        b.putString("customerId", Strings.returnSafely(customerId));
                        b.putString("propertyId", Strings.returnSafely(propertyId));
                        b.putString("custPropInfoId", Strings.returnSafely(customerPropertyInfoId));
                        b.putString("type", o.getType());
                        b.putLong("startTime", start.getTimeInMillis());
                        b.putLong("endTime", end.getTimeInMillis());
                        b.putString("jobType", Strings.returnSafely(jobType));
                        b.putString("addressLine1", Strings.returnSafely(addressLine1));
                        b.putString("postcode", Strings.returnSafely(postcode));
                        b.putString("country", Strings.returnSafely(country));
                        b.putString("customerName", Strings.returnSafely(customerName));
                        b.putString("customerEmail", Strings.returnSafely(customerEmail));
                        b.putString("customerPhone", Strings.returnSafely(customerPhone));
                        b.putString("custPropRel", Strings.returnSafely(customerPropertyRelationship));
                        b.putString("description", Strings.returnSafely(description));
                        AnalyticsHelper.track(
                                getView().getContext(),
                                "updateTimeslot",
                                b);

                        getView().hideDialog();
                    }
                });
    }

    @Override
    public void delete(final Timeslot timeslot) {
        if (!isViewAttached()) return;

        final String tradesmanId = FirebaseUtils.getCurrentTradesmanId();

        if (Strings.isEmpty(tradesmanId) || timeslot == null || Strings.isEmpty(timeslot.getId()) || !NetworkManager.hasConnection(getView().getContext())) {
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
                        childUpdates.put("/tradesmanServiceTimeslots/" + tradesmanId + "/" + timeslot.getId(), null);

                        // remove the service too
                        if (!Strings.isEmpty(timeslot.getServiceId())) {
                            childUpdates.put("/services/" + timeslot.getServiceId(), null);

                            // remove service from service set
                            ServiceSet serviceSet = getView().getServiceSet();
                            String serviceSetId = serviceSet != null ? serviceSet.getId() : null;
                            if (!Strings.isEmpty(serviceSetId)) {
                                childUpdates.put("/serviceSets/" + serviceSetId + "/services/" + timeslot.getServiceId(), null);
                            }
                        }

                        FirebaseUtils.getBaseRef().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    getView().showErrorDialog();
                                    return;
                                }

                                // track the delete
                                Bundle b = new Bundle();
                                b.putString("timeslotId", timeslot.getId());
                                b.putString("serviceId", timeslot.getServiceId());
                                b.putBoolean("isOwnJob", true);
                                if (!Strings.isEmpty(timeslot.getType()))
                                    b.putString("type", timeslot.getType());
                                if (timeslot.getStartTime() > 0)
                                    b.putLong("startTime", timeslot.getStartTime());
                                if (timeslot.getEndTime() > 0)
                                    b.putLong("endTime", timeslot.getEndTime());
                                AnalyticsHelper.track(
                                        getView().getContext(),
                                        "deleteTimeslot",
                                        b);

                                // remove the original timeslot
                                HomeFixCal.removeEvent(timeslot);

                                getView().onDeleteComplete(timeslot);
                            }
                        });

                    }

                }, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

}
