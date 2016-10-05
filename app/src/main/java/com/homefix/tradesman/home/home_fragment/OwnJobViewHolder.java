package com.homefix.tradesman.home.home_fragment;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.homefix.tradesman.R;
import com.homefix.tradesman.common.ActivityHelper;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.model.Customer;
import com.homefix.tradesman.model.CustomerProperty;
import com.homefix.tradesman.model.Property;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceSet;
import com.homefix.tradesman.model.Timeslot;
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

    private DatabaseReference serviceRef, serviceSetRef, customerPropertyRef, customerRef, propertyRef;

    private Activity activity;

    public boolean showTimeUntil = true, setHelperClickListeners = true;

    public OwnJobViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public interface TimeslotClickedListener {

        void onTimeslotClicked(Timeslot timeslot, boolean longClick);

    }

    public void bind(final Activity activity, Timeslot timeslot, TimeslotClickedListener clickedListener) {
        if (!ActivityHelper.canActivityDo(activity) || timeslot == null || itemView == null) return;

        this.activity = activity;

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
            String startDateTime = TimeUtils.getShortDateString(timeslot.getStartTime());
            if (!Strings.isEmpty(startDateTime)) startDateTime += ", ";
            startDateTime += TimeUtils.getShortTimeString(timeslot.getStartTime());
            startDateTimeView.setText(startDateTime);

            if (showTimeUntil && timeslot.getSlotLength() > 0) {
                String duration = TimeUtils.formatShortDateToHoursMinutes(timeslot.getSlotLength());

                if ("0 mins".equals(duration)) {
                    durationView.setVisibility(View.GONE);

                } else {
                    durationView.setVisibility(View.VISIBLE);
                    if (Strings.isEmpty(startDateTime)) startDateTimeView.setText(duration);
                    else durationView.setText(duration);
                }
            }
        }

        // remove the old listener
        if (serviceRef != null) serviceRef.removeEventListener(serviceValueEventListener);

        // setup the new one
        String serviceId = timeslot.getServiceId();
        serviceRef = FirebaseUtils.getSpecificServiceRef(serviceId);
//        if (serviceRef != null) serviceRef.addValueEventListener(serviceValueEventListener);
    }

    private ValueEventListener serviceValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Service service = dataSnapshot != null && dataSnapshot.exists() ? dataSnapshot.getValue(Service.class) : null;
            if (service != null) {
                if (serviceNameView != null)
                    serviceNameView.setText(Strings.isEmpty(service.getServiceType()) ? "Own Job" : service.getServiceType());

                setupServiceSet(service.getServiceSetId());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void setupServiceSet(String serviceSetId) {
        if (Strings.isEmpty(serviceSetId)) return;

        // remove the old listener
        if (serviceSetRef != null) serviceSetRef.removeEventListener(serviceSetValueEventListener);

        // setup the new one
        serviceSetRef = FirebaseUtils.getSpecificServiceSetRef(serviceSetId);
        if (serviceSetRef != null)
            serviceSetRef.addValueEventListener(serviceSetValueEventListener);
    }

    private ValueEventListener serviceSetValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ServiceSet serviceSet = dataSnapshot != null && dataSnapshot.exists() ? dataSnapshot.getValue(ServiceSet.class) : null;
            if (serviceSet == null) return;

            setupCustomerProperty(serviceSet.getCustomerPropertyId());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void setupCustomerProperty(String customerPropertyId) {
        if (Strings.isEmpty(customerPropertyId)) return;

        // remove the old listener
        if (customerPropertyRef != null)
            customerPropertyRef.removeEventListener(customerPropertyIdListener);

        // setup the new one
        customerPropertyRef = FirebaseUtils.getBaseRef().child("customerProperties").child(customerPropertyId);
        customerPropertyRef.addValueEventListener(customerPropertyIdListener);
    }

    private ValueEventListener customerPropertyIdListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            CustomerProperty customerProperty = dataSnapshot != null && dataSnapshot.exists() ? dataSnapshot.getValue(CustomerProperty.class) : null;
            if (customerProperty == null) return;

            String customerId = customerProperty.getCustomerId();
            setupCustomer(customerId);

            String propertyId = customerProperty.getPropertyId();
            setupProperty(propertyId);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void setupCustomer(String customerId) {
        if (Strings.isEmpty(customerId)) return;

        // remove the old listener
        if (customerRef != null) customerRef.removeEventListener(customerListener);

        // setup the new one
        customerRef = FirebaseUtils.getBaseRef().child("customers").child(customerId);
        customerRef.addValueEventListener(propertyListener);
    }

    private ValueEventListener customerListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Customer customer = dataSnapshot != null && dataSnapshot.exists() ? dataSnapshot.getValue(Customer.class) : null;
            if (customer == null) return;

            if (contactNameView != null) contactNameView.setText(customer.getName());

            if (contactEmailView != null) {
                final String email = customer.getEmail();
                contactEmailView.setText(email);

                if (setHelperClickListeners) {
                    contactEmailView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HomefixServiceHelper.onEmailClicked(activity, email, "");
                        }
                    });
                } else {
                    contactEmailView.setClickable(false);
                }
            }

            if (contactPhoneView != null) {
                String phone = customer.getHomePhone();
                if (Strings.isEmpty(phone)) phone = customer.getMobilePhone();
                contactPhoneView.setText(phone);

                if (setHelperClickListeners) {
                    final String finalPhone = phone;
                    contactPhoneView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HomefixServiceHelper.onPhoneClicked(activity, finalPhone);
                        }
                    });
                } else {
                    contactPhoneView.setClickable(false);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void setupProperty(String propertyId) {
        if (Strings.isEmpty(propertyId)) return;

        // remove the old listener
        if (propertyRef != null) propertyRef.removeEventListener(propertyListener);

        // setup the new one
        propertyRef = FirebaseUtils.getBaseRef().child("properties").child(propertyId);
        propertyRef.addValueEventListener(propertyListener);
    }

    private ValueEventListener propertyListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Property property = dataSnapshot != null && dataSnapshot.exists() ? dataSnapshot.getValue(Property.class) : null;
            if (property == null) return;

            final String addressLine1 = property.getAddressLine1();
            final String addressLine2 = property.getAddressLine2();
            final String addressLine3 = property.getAddressLine3();
            final String postcode = property.getPostcode();
            final String country = property.getCountry();
            if (addressView != null) {
                addressView.setText(HomefixServiceHelper.getReadableLocationString(", ", addressLine1, addressLine2, addressLine3, postcode, country));

                if (setHelperClickListeners) {
                    final double latitude = property.getLatitude();
                    final double longitude = property.getLongitude();
                    addressView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HomefixServiceHelper.onLocationClicked(activity, latitude, longitude, addressLine1, addressLine2, addressLine3, postcode, country);
                        }
                    });
                } else {
                    addressView.setClickable(false);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

}
