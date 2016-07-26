package com.homefix.tradesman.timeslot.own_job;

import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.timeslot.BaseTimeslotFragmentPresenter;

import java.sql.Time;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        Callback<Service> callback = new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                Service service = response.body();

                if (service == null) {
                    onFailure(call, null);
                    return;
                }

                // update the service in the timeslot being shown
                Timeslot timeslot = getView().getTimeslot();

                if (timeslot == null) timeslot = new Timeslot();

                timeslot.setStart(getView().getStartTime().getTimeInMillis());
                timeslot.setEnd(getView().getEndTime().getTimeInMillis());
                timeslot.setLength(getView().getEndTime().getTimeInMillis() - getView().getStartTime().getTimeInMillis());
                timeslot.setTradesman(UserController.getCurrentUser());
                timeslot.setType(Timeslot.TYPE.OWN_JOB.getName());
                timeslot.setService(service);

                // update the view
                getView().setTimeslot(timeslot);
                getView().setEditing(false);
                getView().setupView();

                getView().hideDialog();
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                getView().showDialog("Sorry, something went wrong", false);
            }
        };

        @SuppressWarnings("UnnecessaryUnboxing") Call<Service> call = HomeFix.getAPI().createService(
                UserController.getToken(),
                customerName,
                customerEmail,
                customerPhone,
                customerPropertyRelationship,
                addressLine1,
                addressLine2,
                addressLine3,
                postcode,
                country,
                latitude != null ? latitude.doubleValue() : 0,
                longitude != null ? longitude.doubleValue() : 0,
                jobType,
                start.getTimeInMillis(),
                end.getTimeInMillis(),
                description);

        call.enqueue(callback);
    }

    public void updateJob(
            Timeslot originalTimeslot, Calendar start, Calendar end, String jobType, String addressLine1, String addressLine2,
            String addressLine3, String postcode, String country, Double latitude, Double longitude,
            String customerName, String customerEmail, String customerPhone, String customerPropertyRelationship, String description) {

        if (!isViewAttached()) return;

        getView().showDialog("Updating job...", true);

        Callback<Service> callback = new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                Service service = response.body();

                if (service == null) {
                    onFailure(call, null);
                    return;
                }

                Timeslot timeslot = getView().getTimeslot();

                if (timeslot == null) timeslot = new Timeslot();

                timeslot.setStart(getView().getStartTime().getTimeInMillis());
                timeslot.setEnd(getView().getEndTime().getTimeInMillis());
                timeslot.setLength(getView().getEndTime().getTimeInMillis() - getView().getStartTime().getTimeInMillis());
                timeslot.setTradesman(UserController.getCurrentUser());
                timeslot.setType(Timeslot.TYPE.OWN_JOB.getName());
                timeslot.setService(service);

                // update the view
                getView().setTimeslot(timeslot);
                getView().setEditing(false);
                getView().setupView();

                getView().hideDialog();
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                // TODO: show dialog with why it failed

                getView().setEditing(false);
                getView().setupView();
                getView().hideDialog();
            }
        };

        Call<Service> call = HomeFix.getAPI().createService(
                UserController.getToken(),
                customerName,
                customerEmail,
                customerPhone,
                customerPropertyRelationship,
                addressLine1,
                addressLine2,
                addressLine3,
                postcode,
                country,
                latitude != null ? latitude.doubleValue() : 0,
                longitude != null ? longitude.doubleValue() : 0,
                jobType,
                start.getTimeInMillis(),
                end.getTimeInMillis(),
                description);

        call.enqueue(callback);
    }

}
