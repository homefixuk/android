package com.homefix.tradesman.timeslot.own_job;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.text.Html;
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
import com.homefix.tradesman.R;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.model.Customer;
import com.homefix.tradesman.model.CustomerProperty;
import com.homefix.tradesman.model.Property;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceType;
import com.homefix.tradesman.timeslot.BaseTimeslotFragment;
import com.homefix.tradesman.timeslot.TimeslotActivity;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.IntentHelper;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by samuel on 7/19/2016.
 */

public class OwnJobFragment extends BaseTimeslotFragment<TimeslotActivity, OwnJobView, OwnJobPresenter> implements OwnJobView {

    protected TextView mJobTypeTxt, mLocationTxt, mCustomerPropertyType;
    protected ImageView mLocationIcon, mEmailIcon, mPhoneIcon;
    protected EditText mPersonNameTxt, mPersonEmailTxt, mPersonPhoneNumberTxt, mDescriptionTxt;
    protected Place mLocationPlace;
    protected String addressLine1, addressLine2, addressLine3, country, postcode;
    protected Double latitude = null, longitude = null;

    public OwnJobFragment() {
    }

    @Override
    protected OwnJobPresenter getPresenter() {
        if (presenter == null) presenter = new OwnJobPresenter(this);

        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_own_job;
    }

    @Override
    protected void injectDependencies() {
        super.injectDependencies();

        View view = getView();

        if (view == null) return;

        mJobTypeTxt = (TextView) view.findViewById(R.id.job_type_txt);
        mLocationTxt = (TextView) view.findViewById(R.id.location_txt);
        mPersonNameTxt = (EditText) view.findViewById(R.id.person_name_txt);
        mPersonEmailTxt = (EditText) view.findViewById(R.id.person_email_txt);
        mPersonPhoneNumberTxt = (EditText) view.findViewById(R.id.person_phone_number_txt);
        mCustomerPropertyType = (TextView) view.findViewById(R.id.property_type_txt);
        mDescriptionTxt = (EditText) view.findViewById(R.id.description_txt);

        mLocationIcon = (ImageView) view.findViewById(R.id.location_icon);
        mEmailIcon = (ImageView) view.findViewById(R.id.email_icon);
        mPhoneIcon = (ImageView) view.findViewById(R.id.phone_icon);
    }

    @Override
    protected void setupView() {
        super.setupView();

        if (mTimeslot != null) {
            // setup values from Timeslot
            Service service = mTimeslot.getService();
            CustomerProperty customerProperty = service.getService_set().getCustomer_property();
            Customer customer = customerProperty.getCustomer();
            Property property = customerProperty.getProperty();

            addressLine1 = property.getAddress_line_1();
            addressLine2 = property.getAddress_line_2();
            addressLine3 = property.getAddress_line_3();
            postcode = property.getPostcode();
            country = property.getCountry();

            mJobTypeTxt.setText(service.getService_type().getName());
            mLocationTxt.setText(getReadableStringFormat("<br/>"));
            mPersonNameTxt.setText(customer.getName());
            mPersonEmailTxt.setText(customer.getEmail());
            mPersonPhoneNumberTxt.setText(customer.getMobile());
            mCustomerPropertyType.setText(customerProperty.getType());
            mDescriptionTxt.setText(service.getTradesman_notes());
        }

        mPersonEmailTxt.setEnabled(isEdit);
        mPersonEmailTxt.setEnabled(isEdit);
        mPersonPhoneNumberTxt.setEnabled(isEdit);
        mDescriptionTxt.setEnabled(isEdit);

        // if not in edit mode
        if (!isEdit) {
            mJobTypeTxt.setOnClickListener(null);

            mLocationIcon.setImageResource(R.drawable.directions);
            mEmailIcon.setVisibility(View.VISIBLE);
            mPhoneIcon.setVisibility(View.VISIBLE);

            // setup locations
            View.OnClickListener directionsClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (latitude != null && longitude != null)
                        IntentHelper.googleMapsDirections(getActivity(), latitude, longitude);
                    else
                        IntentHelper.googleMapsDirections(getActivity(), getReadableStringFormat(","));
                }
            };
            mLocationTxt.setOnClickListener(directionsClickListener);
            mLocationIcon.setOnClickListener(directionsClickListener);
            mLocationTxt.setOnLongClickListener(null);

            // setup email
            View.OnClickListener emailClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = mPersonEmailTxt.getText().toString();

                    if (Strings.isEmpty(email)) {
                        Toast.makeText(getContext(), "No email to send to", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ShareCompat.IntentBuilder.from(getActivity())
                            .setType("message/rfc822")
                            .addEmailTo(email)
                            .setSubject("Homefix")
                            .setText("")
                            .startChooser();
                }
            };
            mEmailIcon.setOnClickListener(emailClickListener);
            mPersonEmailTxt.setOnClickListener(emailClickListener);

            View.OnClickListener phoneClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = mPersonPhoneNumberTxt.getText().toString();

                    if (Strings.isEmpty(phone)) {
                        Toast.makeText(getContext(), "No phone to call", Toast.LENGTH_SHORT).show();
                        return;
                    }


                }
            };
            mPhoneIcon.setOnClickListener(phoneClickListener);
            mPersonPhoneNumberTxt.setOnClickListener(phoneClickListener);

            mCustomerPropertyType.setOnClickListener(null);

            mSaveTxt.setText("DONE");
            mSaveTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getBaseActivity().tryClose();
                }
            });

            return;
        }

        // if in edit mode

        // hide action buttons from non-edit mode (directions, email, phone)
        mLocationIcon.setImageResource(R.drawable.ic_map_marker_grey600_48dp);
        mLocationIcon.setOnClickListener(null);
        mEmailIcon.setVisibility(View.GONE);
        mEmailIcon.setOnClickListener(null);
        mPersonEmailTxt.setOnClickListener(null);
        mPersonPhoneNumberTxt.setOnClickListener(null);
        mPhoneIcon.setVisibility(View.GONE);
        mPhoneIcon.setOnClickListener(null);

        mJobTypeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show list of service type names
                List<String> namesList = ServiceType.getServiceTypeNames();
                CharSequence[] array = namesList.toArray(new String[namesList.size()]);

                MaterialDialogWrapper.getListDialog(getActivity(), "Select service type", array, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        hasMadeChanges = true;

                        // set the job type text from the one they selected
                        mJobTypeTxt.setText(text);
                    }
                }).show();
            }
        });

        mLocationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlacePicker();
            }
        });
        mLocationTxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showManualLocationInput();
                return true;
            }
        });

        mCustomerPropertyType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialogWrapper.getListDialog(getActivity(), "Users relation to property", new CharSequence[]{"owner", "tenant", "manager"}, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        if (dialog != null) dialog.dismiss();

                        mCustomerPropertyType.setText(text);
                    }
                }).show();
            }
        });

        mSaveTxt.setText(mTimeslot != null ? "UPDATE" : "CREATE");
    }

    @Override
    public void saveCliked() {
        // TODO: call save function in presenter
    }

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
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), getActivity(), 0);
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
                        Address address = getAddress(getContext(), getReadableStringFormat(","));
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

        if (!Strings.isEmpty(addressLine1)) addressLine1 = addressLine1.replace(postcode, "");
        if (!Strings.isEmpty(addressLine2)) addressLine2 = addressLine2.replace(postcode, "");
        if (!Strings.isEmpty(addressLine3)) addressLine3 = addressLine3.replace(postcode, "");

        mLocationTxt.setText(Html.fromHtml(getReadableStringFormat("<br/>")));
    }

    protected String getReadableStringFormat(String delimiter) {
        String s = "";

        delimiter = Strings.returnSafely(delimiter);

        s += !Strings.isEmpty(addressLine1) ? addressLine1 + delimiter : "";
        s += !Strings.isEmpty(addressLine2) ? addressLine2 + delimiter : "";
        s += !Strings.isEmpty(addressLine3) ? addressLine3 + delimiter : "";
        s += !Strings.isEmpty(postcode) ? postcode + delimiter : "";
        s += !Strings.isEmpty(country) ? country : "";

        if (s.endsWith(delimiter)) s = s.substring(0, s.length() - 1);

        return s;
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
                            if (maxAddressLine >= 0) addressLine1 = address.getAddressLine(0);
                            if (maxAddressLine >= 1) addressLine2 = address.getAddressLine(1);
                            if (maxAddressLine >= 2) addressLine3 = address.getAddressLine(2);
                            country = address.getCountryName();
                            postcode = address.getPostalCode();
                            latitude = address.getLatitude();
                            longitude = address.getLongitude();

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
                            if (dialog != null) dialog.dismiss();

                            showPlacePicker();
                        }
                    },
                    new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (dialog != null) dialog.dismiss();

                            showManualLocationInput();
                        }
                    });
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

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

}