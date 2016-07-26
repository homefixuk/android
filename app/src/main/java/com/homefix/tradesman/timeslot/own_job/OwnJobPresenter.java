package com.homefix.tradesman.timeslot.own_job;

import com.homefix.tradesman.BuildConfig;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.model.Customer;
import com.homefix.tradesman.model.CustomerProperty;
import com.homefix.tradesman.model.Problem;
import com.homefix.tradesman.model.Property;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.timeslot.BaseTimeslotFragmentPresenter;
import com.samdroid.string.Strings;

import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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

        //noinspection UnnecessaryUnboxing
        HomeFix.getAPI().createService(
                UserController.getToken(),
                customerName,
                customerEmail,
                customerPhone,
                customerPropertyRelationship,
                addressLine1,
                Strings.returnSafely(addressLine2),
                Strings.returnSafely(addressLine3),
                postcode,
                country,
                latitude != null ? latitude.doubleValue() : 0,
                longitude != null ? longitude.doubleValue() : 0,
                jobType,
                start.getTimeInMillis(),
                end.getTimeInMillis(),
                Strings.returnSafely(description)).enqueue(callback);
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
                if (BuildConfig.DEBUG && t != null) t.printStackTrace();

                getView().showDialog("Sorry, something went wrong updating your job.", false);

                getView().setEditing(false);
                getView().setupView();
                getView().hideDialog();
            }
        };

        // add all the changes made
        Map<String, Object> changes = new HashMap<>();

        Service service = originalTimeslot.getService();
        if (service != null) {
            CustomerProperty customerProperty = service.getService_set().getCustomer_property();
            if (customerProperty != null) {
                if (!customerProperty.getType().equals(customerPropertyRelationship))
                    changes.put("customer_property_relationship", customerPropertyRelationship);

                Customer customer = customerProperty.getCustomer();
                if (customer != null) {
                    if (!customer.getName().equals(customerName))
                        changes.put("customer_name", customerName);
                    if (!customer.getEmail().equals(customerEmail))
                        changes.put("customer_email", customerEmail);
                    if (!customer.getMobile().equals(customerPhone))
                        changes.put("customer_phone", customerPhone);
                }

                Property property = customerProperty.getProperty();
                if (property != null) {
                    if (!property.getAddress_line_1().equals(addressLine1))
                        changes.put("address_line_1", addressLine1);
                    if (!property.getAddress_line_2().equals(addressLine2))
                        changes.put("address_line_1", addressLine2);
                    if (!property.getAddress_line_3().equals(addressLine3))
                        changes.put("address_line_1", Strings.returnSafely(addressLine3));
                    if (!property.getPostcode().equals(postcode)) changes.put("postcode", postcode);
                    if (!property.getCountry().equals(country)) changes.put("country", country);
                    if (latitude != null && !latitude.equals(property.getLatitude()))
                        changes.put("latitude", latitude);
                    if (longitude != null && !longitude.equals(property.getLongitude()))
                        changes.put("longitude", longitude);
                }
            }

            Problem problem = service.getProblem();
            if (problem != null && !problem.getName().equals(jobType))
                changes.put("problem_name", jobType);

            if (start.getTimeInMillis() > 0 && originalTimeslot.getStart() != start.getTimeInMillis())
                changes.put("start_time", start.getTimeInMillis());
            if (end.getTimeInMillis() > 0 && originalTimeslot.getEnd() != end.getTimeInMillis())
                changes.put("end_time", end.getTimeInMillis());

            if (!service.getTradesman_notes().equals(description))
                changes.put("tradesman_notes", description);
        }

        Call<Service> call = HomeFix.getAPI().updateService(
                originalTimeslot.getObjectId(),
                UserController.getToken(),
                changes);

        call.enqueue(callback);
    }

}
