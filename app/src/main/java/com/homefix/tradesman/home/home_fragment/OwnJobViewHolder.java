package com.homefix.tradesman.home.home_fragment;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.homefix.tradesman.R;
import com.homefix.tradesman.common.ActivityHelper;
import com.homefix.tradesman.model.Customer;
import com.homefix.tradesman.model.CustomerProperty;
import com.homefix.tradesman.model.Problem;
import com.homefix.tradesman.model.Property;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.model.User;
import com.homefix.tradesman.timeslot.HomefixServiceHelper;
import com.samdroid.common.TimeUtils;
import com.samdroid.string.Strings;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by samuel on 9/12/2016.
 */

public class OwnJobViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.start_date_time)
    public TextView startDateTimeView;

    @BindView(R.id.duration)
    public TextView durationView;

    @BindView(R.id.service_name)
    public TextView serviceNameView;

    @BindView(R.id.person_name_txt)
    public TextView contactNameView;

    @BindView(R.id.person_email_txt)
    public TextView contactEmailView;

    @BindView(R.id.person_phone_number_txt)
    public TextView contactPhoneView;

    @BindView(R.id.location_txt)
    public TextView addressView;

    private Timeslot mTimeslot;
    private TimeslotClickedListener mClickedListener;

    public OwnJobViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public interface TimeslotClickedListener {

        void onTimeslotClicked(Timeslot timeslot, boolean longClick);

    }

    public void bind(final Activity activity, Timeslot timeslot, TimeslotClickedListener clickedListener) {
        if (!ActivityHelper.canActivityDo(activity) || timeslot == null) return;

        mTimeslot = timeslot;
        mClickedListener = clickedListener;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickedListener != null) mClickedListener.onTimeslotClicked(mTimeslot, false);
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mClickedListener != null) {
                    mClickedListener.onTimeslotClicked(mTimeslot, false);
                    return true;
                }

                return false;
            }
        });

        // set the start and end time
        if (startDateTimeView != null) {
            String startDateTime = TimeUtils.getShortDateString(timeslot.getStart());
            if (!Strings.isEmpty(startDateTime)) startDateTime += ", ";
            startDateTime += TimeUtils.getShortTimeString(timeslot.getStart());
            startDateTimeView.setText(startDateTime);

            if (timeslot.getSlotLength() > 0) {
                String duration = TimeUtils.formatShortDateToHoursMinutes(timeslot.getSlotLength());

                if (Strings.isEmpty(startDateTime)) startDateTimeView.setText(duration);
                else durationView.setText(duration);
            }
        }

        Service service = timeslot.getService();
        if (service != null) {
            if (serviceNameView != null) {
                String timeslotName = "";
                Problem problem = service.getProblem();
                if (problem == null) timeslotName = service.getId();
                else timeslotName = problem.getName();

                serviceNameView.setText(Strings.isEmpty(timeslotName) ? "Own Job" : timeslotName);
            }

            CustomerProperty customerProperty = service.getServiceSet().getCustomerProperty();

            if (customerProperty != null) {
                // set the address
                Property property = customerProperty.getProperty();
                if (property != null) {
                    final String addressLine1 = property.getAddressLine1();
                    final String addressLine2 = property.getAddressLine2();
                    final String addressLine3 = property.getAddressLine3();
                    final String postcode = property.getPostcode();
                    final String country = property.getCountry();
                    if (addressView != null) {
                        addressView.setText(HomefixServiceHelper.getReadableLocationString(", ", addressLine1, addressLine2, addressLine3, postcode, country));

                        final double latitude = property.getLatitude();
                        final double longitude = property.getLongitude();
                        addressView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HomefixServiceHelper.onLocationClicked(activity, latitude, longitude, addressLine1, addressLine2, addressLine3, postcode, country);
                            }
                        });
                    }
                }

                // set the customer info
                Customer customer = customerProperty.getCustomer();
                User user = customer != null ? customer.getUser() : null;
                if (user != null) {
                    if (contactNameView != null) {
                        contactNameView.setText(user.getName());
                    }

                    if (contactEmailView != null) {
                        final String email = Strings.returnSafely(user.getEmail());
                        contactEmailView.setText(email);
                        contactEmailView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HomefixServiceHelper.onEmailClicked(activity, email, "");
                            }
                        });
                    }

                    if (contactPhoneView != null) {
                        String phone = Strings.returnSafely(user.getHomePhone());
                        if (Strings.isEmpty(phone))
                            phone = Strings.returnSafely(user.getMobile());
                        contactPhoneView.setText(phone);
                        final String finalPhone = phone;
                        contactPhoneView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HomefixServiceHelper.onPhoneClicked(activity, finalPhone);
                            }
                        });
                    }
                }
            }
        }
    }

}
