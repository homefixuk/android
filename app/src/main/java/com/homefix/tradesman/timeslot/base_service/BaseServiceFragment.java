package com.homefix.tradesman.timeslot.base_service;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.homefix.tradesman.R;
import com.homefix.tradesman.common.HtmlHelper;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.model.Customer;
import com.homefix.tradesman.model.CustomerProperty;
import com.homefix.tradesman.model.Property;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceSet;
import com.homefix.tradesman.timeslot.HomefixServiceHelper;
import com.homefix.tradesman.timeslot.TimeslotActivity;
import com.homefix.tradesman.timeslot.base_timeslot.BaseTimeslotFragment;
import com.homefix.tradesman.timeslot.base_timeslot.BaseTimeslotFragmentPresenter;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.listener.BackgroundColourOnTouchListener;
import com.samdroid.listener.BackgroundViewColourOnTouchListener;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;
import com.samdroid.view.ViewUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by samuel on 7/19/2016.
 */

public abstract class BaseServiceFragment<P extends BaseTimeslotFragmentPresenter<BaseServiceView>>
        extends BaseTimeslotFragment<TimeslotActivity, BaseServiceView, P>
        implements BaseServiceView {

    @BindView(R.id.location_bar)
    protected View mLocationBar;

    @BindView(R.id.job_type_txt)
    protected EditText mJobTypeTxt;

    @BindView(R.id.location_txt)
    protected TextView mLocationTxt;

    @BindView(R.id.property_type_txt)
    protected TextView mCustomerPropertyType;

    @BindView(R.id.location_icon)
    protected ImageView mLocationIcon;

    @BindView(R.id.email_icon)
    protected ImageView mEmailIcon;

    @BindView(R.id.phone_icon)
    protected ImageView mPhoneIcon;

    @BindView(R.id.person_name_txt)
    protected EditText mPersonNameTxt;

    @BindView(R.id.person_email_txt)
    protected EditText mPersonEmailTxt;

    @BindView(R.id.person_phone_number_txt)
    protected EditText mPersonPhoneNumberTxt;

    @BindView(R.id.description_txt)
    protected EditText mDescriptionTxt;

    protected Place mLocationPlace;
    protected String addressLine1, addressLine2, addressLine3, country, postcode;
    protected Double latitude = null, longitude = null;

    private DatabaseReference serviceRef, serviceSetRef, customerPropertyRef, customerRef, propertyRef;


    public BaseServiceFragment() {
    }

    @Override
    public void setupView() {
        super.setupView();

        ViewUtils.setEditTextEditable(mPersonNameTxt, isEdit);
        ViewUtils.setEditTextEditable(mPersonEmailTxt, isEdit);
        ViewUtils.setEditTextEditable(mPersonPhoneNumberTxt, isEdit);
        ViewUtils.setEditTextEditable(mDescriptionTxt, isEdit);
        ViewUtils.setEditTextEditable(mJobTypeTxt, isEdit);

        // if not in edit mode
        if (!isEdit) {
            if (mLocationIcon != null) mLocationIcon.setImageResource(R.drawable.directions);
            if (mEmailIcon != null) mEmailIcon.setVisibility(View.VISIBLE);
            if (mPhoneIcon != null) mPhoneIcon.setVisibility(View.VISIBLE);

            // setup locations
            if (mLocationBar != null)
                mLocationBar.setOnTouchListener(new BackgroundColourOnTouchListener(getContext(), R.color.transparent, R.color.colorAccentDark));

            BackgroundViewColourOnTouchListener listener = new BackgroundViewColourOnTouchListener(
                    mPersonEmailTxt,
                    ContextCompat.getColor(getContext(), R.color.transparent),
                    ContextCompat.getColor(getContext(), R.color.colorAccentDark));
            if (mPersonEmailTxt != null) mPersonEmailTxt.setOnTouchListener(listener);
            if (mEmailIcon != null) mEmailIcon.setOnTouchListener(listener);

            BackgroundViewColourOnTouchListener phoneListener = new BackgroundViewColourOnTouchListener(
                    mPersonPhoneNumberTxt,
                    ContextCompat.getColor(getContext(), R.color.transparent),
                    ContextCompat.getColor(getContext(), R.color.colorAccentDark));
            if (mPersonPhoneNumberTxt != null)
                mPersonPhoneNumberTxt.setOnTouchListener(phoneListener);
            if (mPhoneIcon != null) mPhoneIcon.setOnTouchListener(phoneListener);
            return;
        }

        // else in edit mode //

        // hide action buttons from non-edit mode (directions, email, phone)
        if (mEmailIcon != null) {
            mEmailIcon.setVisibility(View.GONE);
            mEmailIcon.setOnTouchListener(null);
        }
        if (mPersonEmailTxt != null) mPersonEmailTxt.setOnTouchListener(null);
        if (mPersonPhoneNumberTxt != null) mPersonPhoneNumberTxt.setOnTouchListener(null);
        if (mPhoneIcon != null) {
            mPhoneIcon.setVisibility(View.GONE);
            mPhoneIcon.setOnTouchListener(null);
        }

        if (mLocationIcon != null)
            mLocationIcon.setImageResource(R.drawable.ic_map_marker_grey600_48dp);

        // remove the old listener
        if (serviceRef != null) serviceRef.removeEventListener(serviceValueEventListener);

        // setup the new one
        String serviceId = mTimeslot != null ? mTimeslot.getServiceId() : null;
        serviceRef = FirebaseUtils.getSpecificServiceRef(serviceId);
        if (serviceRef != null) serviceRef.addValueEventListener(serviceValueEventListener);
    }

    private ValueEventListener serviceValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Service service = dataSnapshot != null && dataSnapshot.exists() ? dataSnapshot.getValue(Service.class) : null;
            if (service != null) {
                if (mJobTypeTxt != null) mJobTypeTxt.setText(service.getServiceType());

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

            if (mPersonNameTxt != null) mPersonNameTxt.setText(customer.getName());

            if (mPersonEmailTxt != null) {
                final String email = customer.getEmail();
                mPersonEmailTxt.setText(email);
            }

            if (mPersonPhoneNumberTxt != null) {
                String phone = customer.getHomePhone();
                if (Strings.isEmpty(phone)) phone = customer.getMobilePhone();
                mPersonPhoneNumberTxt.setText(phone);
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

            addressLine1 = property.getAddressLine1();
            addressLine2 = property.getAddressLine2();
            addressLine3 = property.getAddressLine3();
            postcode = property.getPostcode();
            country = property.getCountry();
            updateLocationText();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    /**
     * Show the google place picker activity
     */
    private void showPlacePicker() {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(getActivity());
            startActivityForResult(intent, Ids.PLACE_PICKER_REQUEST);
            return;

        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.showErrorDialogFragment(e.getConnectionStatusCode(), getActivity(), this, 0, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                }
            });
        } catch (Exception e) {
        }

        showManualLocationInput();
    }

    public static final String
            _ADDRESS_LINE_1 = "Address line 1",
            _ADDRESS_LINE_2 = "Address line 2",
            _ADDRESS_LINE_3 = "Address line 3",
            _COUNTRY = "Country",
            _POSTCODE = "Postcode";

    private void showManualLocationInput() {
        List<String> keys = new ArrayList<>();
        keys.add(_ADDRESS_LINE_1);
        keys.add(_ADDRESS_LINE_2);
        keys.add(_ADDRESS_LINE_3);
        keys.add(_COUNTRY);
        keys.add(_POSTCODE);

        List<String> values = new ArrayList<>();
        values.add(Strings.returnSafely(addressLine1));
        values.add(Strings.returnSafely(addressLine2));
        values.add(Strings.returnSafely(addressLine3));
        values.add(Strings.returnSafely(country));
        values.add(Strings.returnSafely(postcode));

        MaterialDialogWrapper.getMultiInputDialog(getActivity(), "SET ADDRESS", keys, values, new OnGotObjectListener<HashMap<String, String>>() {

            @Override
            public void onGotThing(HashMap<String, String> newAddress) {
                if (newAddress == null) return;

                addressLine1 = newAddress.get(_ADDRESS_LINE_1);
                addressLine2 = newAddress.get(_ADDRESS_LINE_2);
                addressLine3 = newAddress.get(_ADDRESS_LINE_3);
                country = newAddress.get(_COUNTRY);
                postcode = newAddress.get(_POSTCODE);

                updateLocationText();

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        Address address = getAddress(
                                getContext(),
                                HomefixServiceHelper.getReadableLocationString(
                                        ",",
                                        addressLine1,
                                        addressLine2,
                                        addressLine3,
                                        postcode,
                                        country));
                        if (address != null) {
                            latitude = address.getLatitude();
                            longitude = address.getLongitude();
                        }
                    }

                }).start();
            }

        }).show();
    }

    private void updateLocationText() {
        if (mLocationTxt == null) return;

        mLocationTxt.setText(HtmlHelper.fromHtml(HomefixServiceHelper.getReadableLocationString(
                "<br/>",
                addressLine1,
                addressLine2,
                addressLine3,
                postcode,
                country)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Ids.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                hasMadeChanges = true;

                // store the place
                mLocationPlace = PlacePicker.getPlace(getContext(), data);

                final Address address = getAddress(getContext(), mLocationPlace);
                if (address != null) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            int maxAddressLine = address.getMaxAddressLineIndex();
                            if (maxAddressLine >= 0)
                                addressLine1 = Strings.returnSafely(address.getAddressLine(0));
                            if (maxAddressLine >= 1)
                                addressLine2 = Strings.returnSafely(address.getAddressLine(1));
                            if (maxAddressLine >= 2)
                                addressLine3 = Strings.returnSafely(address.getAddressLine(2));
                            country = Strings.returnSafely(address.getCountryName());
                            postcode = Strings.returnSafely(address.getPostalCode());
                            latitude = address.getLatitude();
                            longitude = address.getLongitude();

                            // make sure the postcode is not repeated
                            addressLine3 = addressLine3.replace(postcode, "");

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateLocationText();
                                }
                            });
                        }

                    }).start();
                }
            }

            MaterialDialogWrapper.getNegativeConfirmationDialog(
                    getActivity(),
                    "Sorry, unable to get the location information. Would you like to try again? Or enter the details manually?",
                    "TRY AGAIN",
                    "MANUAL INPUT",
                    new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();

                            showPlacePicker();
                        }
                    },
                    new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();

                            showManualLocationInput();
                        }
                    });
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @param context
     * @param place
     * @return the Address object from the place
     */
    public static Address getAddress(final Context context, final Place place) {
        if (place == null) return null;

        final Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;
        try {
            LatLng latLng = place.getLatLng();
            if (latLng != null) {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            } else if (!Strings.isEmpty(place.getAddress().toString())) {
                addresses = geocoder.getFromLocationName(place.getAddress().toString(), 1);
            }

        } catch (IOException e) {
            return null;
        }
        if (addresses != null && !addresses.isEmpty())
            return addresses.get(0);
        else
            return null;
    }

    /**
     * @param context
     * @param locationName
     * @return the Address object from the location
     */
    public static Address getAddress(final Context context, final String locationName) {
        if (Strings.isEmpty(locationName)) return null;

        final Geocoder geocoder = new Geocoder(context);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(locationName, 1);

        } catch (IOException e) {
            return null;
        }
        if (addresses != null && !addresses.isEmpty())
            return addresses.get(0);
        else
            return null;
    }

//    @OnClick(R.id.job_type_txt)
//    public void onJobTypeClicked() {
//        if (!isEdit) return;
//
//        // show list of service type names
//        List<String> namesList = Problem.getProblemTypeNames();
//        CharSequence[] array = namesList.toArray(new String[namesList.size()]);
//
//        MaterialDialogWrapper.getListDialog(getActivity(), "Select service type", array, new MaterialDialog.ListCallback() {
//            @Override
//            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
//                hasMadeChanges = true;
//
//                // set the job type text from the one they selected
//                mJobTypeTxt.setText(text);
//            }
//        }).show();
//    }

    @OnClick(R.id.location_bar)
    public void onLocationClicked() {
        if (isEdit) {
            showPlacePicker();
            return;
        }

        HomefixServiceHelper.onLocationClicked(getActivity(), latitude, longitude, addressLine1, addressLine2, addressLine3, postcode, country);
    }

    @OnLongClick(R.id.location_bar)
    public boolean onLocationLongTouch() {
        if (!isEdit) return false;

        showManualLocationInput();
        return true;
    }

    @OnClick({R.id.email_icon, R.id.person_email_txt})
    public void onEmailClicked() {
        if (isEdit || mPersonEmailTxt == null) return;

        String email = mPersonEmailTxt.getText().toString();

        if (Strings.isEmpty(email)) {
            Toast.makeText(getContext(), "No email to send to", Toast.LENGTH_SHORT).show();
            return;
        }

        HomefixServiceHelper.onEmailClicked(getActivity(), email, "");
    }

    @OnClick({R.id.phone_icon, R.id.person_phone_number_txt})
    public void onPhoneClicked() {
        if (isEdit || mPersonPhoneNumberTxt == null) return;

        String phone = mPersonPhoneNumberTxt.getText().toString();

        HomefixServiceHelper.onPhoneClicked(getActivity(), phone);
    }

    @OnClick(R.id.property_type_txt)
    public void onCustomerPropertyRelationshipClicked() {
        if (!isEdit || mCustomerPropertyType == null) return;

        MaterialDialogWrapper.getListDialog(getActivity(), "Users relation to property", new CharSequence[]{"owner", "tenant", "manager"}, new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                mCustomerPropertyType.setText(text);
            }
        }).show();
    }

}
