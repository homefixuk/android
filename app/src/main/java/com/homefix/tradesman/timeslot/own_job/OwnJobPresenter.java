package com.homefix.tradesman.timeslot.own_job;

import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.timeslot.BaseTimeslotFragmentPresenter;

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
            Calendar start, Calendar end, String jobType, String addressLine1, String addressLine2,
            String addressLine3, String postcode, String country, double latitude, double longitude,
            String customerName, String customerEmail, String customerPhone, String customerPropertyRelationship, String description) {

        Callback<Service> callback = new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {

            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {

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
                latitude,
                longitude,
                jobType,
                start.getTimeInMillis(),
                end.getTimeInMillis(),
                description);

        call.enqueue(callback);
    }

    public void updateJob(Timeslot timeslot, Calendar start, Calendar end) {

    }

}
