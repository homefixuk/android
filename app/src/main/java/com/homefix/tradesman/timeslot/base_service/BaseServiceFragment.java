package com.homefix.tradesman.timeslot.base_service;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
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
import com.homefix.tradesman.model.Problem;
import com.homefix.tradesman.model.Property;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.timeslot.TimeslotActivity;
import com.homefix.tradesman.timeslot.base_timeslot.BaseTimeslotFragment;
import com.homefix.tradesman.timeslot.base_timeslot.BaseTimeslotFragmentPresenter;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.IntentHelper;
import com.samdroid.listener.BackgroundColourOnTouchListener;
import com.samdroid.listener.BackgroundViewColourOnTouchListener;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;
import com.samdroid.view.ViewUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by samuel on 7/19/2016.
 */

public abstract class BaseServiceFragment<P extends BaseTimeslotFragmentPresenter<BaseServiceView>> extends BaseTimeslotFragment<TimeslotActivity, BaseServiceView, P> implements BaseServiceView {

    protected View mLocationBar;
    protected TextView mJobTypeTxt, mLocationTxt, mCustomerPropertyType;
    protected ImageView mLocationIcon, mEmailIcon, mPhoneIcon;
    protected EditText mPersonNameTxt, mPersonEmailTxt, mPersonPhoneNumberTxt, mDescriptionTxt;
    protected Place mLocationPlace;
    protected String addressLine1, addressLine2, addressLine3, country, postcode;
    protected Double latitude = null, longitude = null;

    public BaseServiceFragment() {
    }

    @Override
    protected void injectDependencies() {
        super.injectDependencies();

        View view = getView();

        if (view == null) return;

        mJobTypeTxt = (TextView) view.findViewById(R.id.job_type_txt);
        mLocationBar = view.findViewById(R.id.location_bar);
        mLocationTxt = (TextView) mLocationBar.findViewById(R.id.location_txt);
        mPersonNameTxt = (EditText) view.findViewById(R.id.person_name_txt);
        mPersonEmailTxt = (EditText) view.findViewById(R.id.person_email_txt);
        mPersonPhoneNumberTxt = (EditText) view.findViewById(R.id.person_phone_number_txt);
        mCustomerPropertyType = (TextView) view.findViewById(R.id.property_type_txt);
        mDescriptionTxt = (EditText) view.findViewById(R.id.description_txt);

        mLocationIcon = (ImageView) mLocationBar.findViewById(R.id.location_icon);
        mEmailIcon = (ImageView) view.findViewById(R.id.email_icon);
        mPhoneIcon = (ImageView) view.findViewById(R.id.phone_icon);
    }

    @Override
    public void setupView() {
        super.setupView();

        if (mTimeslot != null) {
            // setup values from Timeslot
            Service service = mTimeslot.getService();

            if (service != null) {
                CustomerProperty customerProperty = service.getService_set().getCustomer_property();

                if (customerProperty != null) {
                    mCustomerPropertyType.setText(customerProperty.getType());

                    Property property = customerProperty.getProperty();
                    if (property != null) {
                        addressLine1 = property.getAddress_line_1();
                        addressLine2 = property.getAddress_line_2();
                        addressLine3 = property.getAddress_line_3();
                        postcode = property.getPostcode();
                        country = property.getCountry();
                        latitude = property.getLatitude();
                        longitude = property.getLongitude();
                    }

                    Customer customer = customerProperty.getCustomer();
                    if (customer != null) {
                        mPersonNameTxt.setText(customer.getName());
                        mPersonEmailTxt.setText(customer.getEmail());
                        mPersonPhoneNumberTxt.setText(customer.getMobile());
                    }

                    Problem problem = service.getProblem();
                    mJobTypeTxt.setText(problem != null ? problem.getName() : "");
                }

                mDescriptionTxt.setText(service.getTradesman_notes());
                updateLocationText();
            }
        }

        ViewUtils.setEditTextEditable(mPersonNameTxt, isEdit);
        ViewUtils.setEditTextEditable(mPersonEmailTxt, isEdit);
        ViewUtils.setEditTextEditable(mPersonPhoneNumberTxt, isEdit);
        ViewUtils.setEditTextEditable(mDescriptionTxt, isEdit);

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
                        IntentHelper.googleMapsDirections(getActivity(), getReadableLocationString(","));
                }
            };
            mLocationBar.setOnClickListener(directionsClickListener);
            mLocationBar.setOnTouchListener(new BackgroundColourOnTouchListener(getContext(), R.color.transparent, R.color.colorAccentDark));

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

            BackgroundViewColourOnTouchListener listener = new BackgroundViewColourOnTouchListener(
                    mPersonEmailTxt,
                    ContextCompat.getColor(getContext(), R.color.transparent),
                    ContextCompat.getColor(getContext(), R.color.colorAccentDark));
            mPersonEmailTxt.setOnTouchListener(listener);
            mEmailIcon.setOnTouchListener(listener);

            View.OnClickListener phoneClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = mPersonPhoneNumberTxt.getText().toString();

                    if (Strings.isEmpty(phone)) {
                        MaterialDialogWrapper.getConfirmationDialog(
                                getActivity(),
                                "No phone number for the customer. Open the dialer anyway?",
                                "OPEN DIALER",
                                "CANCEL",
                                new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();

                                        IntentHelper.callPhoneNumber(getContext(), "");
                                    }
                                },
                                new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                        return;
                    }

                    IntentHelper.callPhoneNumber(getContext(), phone);
                }
            };
            mPhoneIcon.setOnClickListener(phoneClickListener);
            mPersonPhoneNumberTxt.setOnClickListener(phoneClickListener);

            BackgroundViewColourOnTouchListener phoneListener = new BackgroundViewColourOnTouchListener(
                    mPersonPhoneNumberTxt,
                    ContextCompat.getColor(getContext(), R.color.transparent),
                    ContextCompat.getColor(getContext(), R.color.colorAccentDark));
            mPersonPhoneNumberTxt.setOnTouchListener(phoneListener);
            mPhoneIcon.setOnTouchListener(phoneListener);

            mCustomerPropertyType.setOnClickListener(null);

            return;
        }

        // else in edit mode //

        // hide action buttons from non-edit mode (directions, email, phone)
        mEmailIcon.setVisibility(View.GONE);
        mEmailIcon.setOnClickListener(null);
        mEmailIcon.setOnTouchListener(null);
        mPersonEmailTxt.setOnClickListener(null);
        mPersonEmailTxt.setOnTouchListener(null);
        mPersonPhoneNumberTxt.setOnClickListener(null);
        mPersonPhoneNumberTxt.setOnTouchListener(null);
        mPhoneIcon.setVisibility(View.GONE);
        mPhoneIcon.setOnClickListener(null);
        mPhoneIcon.setOnTouchListener(null);

        mJobTypeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show list of service type names
                List<String> namesList = Problem.getProblemTypeNames();
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

        mLocationIcon.setImageResource(R.drawable.ic_map_marker_grey600_48dp);
        mLocationBar.setOnTouchListener(null);
        mLocationBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlacePicker();
            }
        });
        mLocationBar.setOnLongClickListener(new View.OnLongClickListener() {
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
                        mCustomerPropertyType.setText(text);
                    }
                }).show();
            }
        });
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
                        Address address = getAddress(getContext(), getReadableLocationString(","));
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

        mLocationTxt.setText(Html.fromHtml(getReadableLocationString("<br/>")));
    }

    protected String getReadableLocationString(String delimiter) {
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

}
