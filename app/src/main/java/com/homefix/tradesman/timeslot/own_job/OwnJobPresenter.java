package com.homefix.tradesman.timeslot.own_job;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.homefix.tradesman.calendar.HomeFixCal;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.model.ServiceSet;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.timeslot.base_service.BaseServiceView;
import com.homefix.tradesman.timeslot.base_timeslot.BaseTimeslotFragmentPresenter;
import com.homefix.tradesman.timeslot.base_timeslot.BaseTimeslotView;
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
            final Calendar start, Calendar end, String jobType, String addressLine1, String addressLine2,
            String addressLine3, String postcode, String country, Double latitude, Double longitude,
            String customerName, String customerEmail, String customerPhone, String customerPropertyRelationship, String description) {

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
            final Calendar start, final Calendar end, String jobType, String addressLine1, String addressLine2,
            String addressLine3, String postcode, String country, Double latitude, Double longitude,
            String customerName, String customerEmail, String customerPhone, String customerPropertyRelationship, String description) {

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
                        getView().setTimeslot(o);
                        getView().setEditing(false);
                        getView().setupView();

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

                        // if the timeslot has a service, remove it too
                        if (Timeslot.TYPE.OWN_JOB.getName().equals(timeslot.getType()) && !Strings.isEmpty(timeslot.getServiceId())) {
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
