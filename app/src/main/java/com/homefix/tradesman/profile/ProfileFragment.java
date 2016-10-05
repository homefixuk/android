package com.homefix.tradesman.profile;

import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.BaseToolbarNavMenuActivity;
import com.homefix.tradesman.base.activity.EditListActivity;
import com.homefix.tradesman.base.fragment.BaseFragment;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.model.Tradesman;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnLongClick;
import butterknife.Optional;

/**
 * Created by samuel on 8/31/2016.
 */

public class ProfileFragment<A extends BaseToolbarNavMenuActivity> extends BaseFragment<A, ProfileView, ProfilePresenter> implements ProfileView {

    @BindView(R.id.profile_image)
    protected SimpleDraweeView imageView;

    @BindView(R.id.name)
    protected TextView nameView;

    @BindView(R.id.email)
    protected TextView emailView;

    @BindView(R.id.home_phone)
    protected TextView homePhoneView;

    @BindView(R.id.mobile_phone)
    protected TextView mobilePhoneView;

    @BindView(R.id.address)
    protected TextView addressView;

    @BindView(R.id.years_experience)
    protected TextView yearsExperienceView;

    @BindView(R.id.total_hours_worked)
    protected TextView totalHoursWorkedView;

    @BindView(R.id.total_hours_worked_this_week)
    protected TextView totalHoursWorkedThisWeekView;

    @BindView(R.id.work_areas)
    protected TextView workAreasView;

    protected Tradesman mCurrentTradesman;

    public ProfileFragment() {
        super(ProfileFragment.class.getSimpleName());
    }

    @Override
    protected ProfilePresenter getPresenter() {
        if (presenter == null) presenter = new ProfilePresenter(this);

        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onResume() {
        super.onResume();
        Tradesman.addCurrentTradesmanListener(tradesmanListener);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private final OnGotObjectListener<Tradesman> tradesmanListener = new OnGotObjectListener<Tradesman>() {
        @Override
        public void onGotThing(Tradesman tradesman) {
            mCurrentTradesman = tradesman;
            setupView();
        }
    };

    private final ValueEventListener currentTradesmanRef = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mCurrentTradesman = dataSnapshot != null ? dataSnapshot.getValue(Tradesman.class) : mCurrentTradesman;
            setupView();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void setupView() {
        if (mCurrentTradesman == null) return;

        // load the profile image
        Uri imageUri = Uri.parse(Strings.checkUrl(mCurrentTradesman.getPicture()));
        imageView.setImageURI(imageUri);

        nameView.setText(Strings.returnSafely(mCurrentTradesman.getName(), "No Name"));
        emailView.setText(Strings.returnSafely(mCurrentTradesman.getEmail(), "your@email.com"));
        homePhoneView.setText(Strings.returnSafely(mCurrentTradesman.getHomePhone(), "Long hold here to set your home phone"));
        mobilePhoneView.setText(Strings.returnSafely(mCurrentTradesman.getMobilePhone(), "Long hold here to set your mobile phone"));
        addressView.setText(Strings.returnSafely(getReadableLocationString(", "), "Long hold here to set your address"));
        yearsExperienceView.setText(Strings.formatRaised(mCurrentTradesman.getExperience()));

        workAreasView.setText(Strings.returnSafely(Strings.flattenMap(mCurrentTradesman.getWorkAreas(), ", "), "Long hold here to set your work areas"));
    }

    protected String getReadableLocationString(String delimiter) {
        if (mCurrentTradesman == null) return "";

        String s = "";

        delimiter = Strings.returnSafely(delimiter);

        s += !Strings.isEmpty(mCurrentTradesman.getHomeAddressLine1()) ? mCurrentTradesman.getHomeAddressLine1() + delimiter : "";
        s += !Strings.isEmpty(mCurrentTradesman.getHomeAddressLine2()) ? mCurrentTradesman.getHomeAddressLine2() + delimiter : "";
        s += !Strings.isEmpty(mCurrentTradesman.getHomeAddressLine3()) ? mCurrentTradesman.getHomeAddressLine3() + delimiter : "";
        s += !Strings.isEmpty(mCurrentTradesman.getHomePostcode()) ? mCurrentTradesman.getHomePostcode() + delimiter : "";
        s += !Strings.isEmpty(mCurrentTradesman.getHomeCountry()) ? mCurrentTradesman.getHomeCountry() : "";

        if (s.endsWith(delimiter)) s = s.substring(0, s.length() - delimiter.length());

        return s;
    }

    @OnLongClick(R.id.home_phone)
    public boolean onHomePhoneLongTouch() {
        if (mCurrentTradesman == null) return false;

        MaterialDialogWrapper.getEditTextDialog(
                getActivity(),
                "",
                mCurrentTradesman.getHomePhone(),
                "Home Phone Number",
                "SAVE",
                InputType.TYPE_CLASS_PHONE,
                new MaterialDialogWrapper.SubmitObjectChangesCallback() {
                    @Override
                    public void onChangeSubmitted(Object original, Object changed) {
                        // if there was no change, do nothing
                        if ((original != null && original.equals(changed))
                                || (original == null && changed == null)) return;

                        DatabaseReference ref = FirebaseUtils.getCurrentTradesmanRef();
                        if (ref == null) {
                            Toast.makeText(getContext(), "Sorry, unable to make changes rightb now", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String homePhoneNew = Strings.returnSafely(String.valueOf(changed));

                        showDialog("Updating Home Phone...", true);


                        ref.child("homePhone").setValue(homePhoneNew, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    showErrorDialog();
                                    return;
                                }

                                hideDialog();
                            }
                        });
                    }

                    @Override
                    public void onChangeCancelled(Object original) {
                    }

                }).show();

        return true;
    }

    @OnLongClick(R.id.mobile_phone)
    public boolean onMobilePhoneLongTouch() {
        if (mCurrentTradesman == null) return false;

        MaterialDialogWrapper.getEditTextDialog(
                getActivity(),
                null,
                mCurrentTradesman.getMobilePhone(),
                "Mobile Phone Number",
                "SAVE",
                InputType.TYPE_CLASS_PHONE,
                new MaterialDialogWrapper.SubmitObjectChangesCallback() {
                    @Override
                    public void onChangeSubmitted(Object original, Object changed) {
                        // if there was no change, do nothing
                        if ((original != null && original.equals(changed))
                                || (original == null && changed == null)) return;

                        DatabaseReference ref = FirebaseUtils.getCurrentTradesmanRef();
                        if (ref == null) {
                            Toast.makeText(getContext(), "Sorry, unable to make changes rightb now", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String mobilePhoneNew = Strings.returnSafely(String.valueOf(changed));

                        showDialog("Updating Mobile Phone...", true);

                        ref.child("mobilePhone").setValue(mobilePhoneNew, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    showErrorDialog();
                                    return;
                                }

                                hideDialog();
                            }
                        });
                    }

                    @Override
                    public void onChangeCancelled(Object original) {
                    }

                }).show();

        return true;
    }

    public static final String
            _ADDRESS_LINE_1 = "Address line 1",
            _ADDRESS_LINE_2 = "Address line 2",
            _ADDRESS_LINE_3 = "Address line 3",
            _COUNTRY = "Country",
            _POSTCODE = "Postcode";

    @OnLongClick(R.id.address)
    public boolean onAddressLongTouch() {
        if (mCurrentTradesman == null) return false;

        // show a multi edit dialog so the address can be changed

        List<String> keys = new ArrayList<>();
        keys.add(_ADDRESS_LINE_1);
        keys.add(_ADDRESS_LINE_2);
        keys.add(_ADDRESS_LINE_3);
        keys.add(_COUNTRY);
        keys.add(_POSTCODE);

        List<String> values = new ArrayList<>();
        values.add(mCurrentTradesman.getHomeAddressLine1());
        values.add(mCurrentTradesman.getHomeAddressLine2());
        values.add(mCurrentTradesman.getHomeAddressLine3());
        values.add(mCurrentTradesman.getHomeCountry());
        values.add(mCurrentTradesman.getHomePostcode());

        MaterialDialogWrapper.getMultiInputDialog(getActivity(), "SET ADDRESS", keys, values, new OnGotObjectListener<HashMap<String, String>>() {

            @Override
            public void onGotThing(HashMap<String, String> newAddress) {
                if (newAddress == null) return;

                DatabaseReference ref = FirebaseUtils.getCurrentTradesmanRef();
                if (ref == null) {
                    Toast.makeText(getContext(), "Sorry, unable to make changes rightb now", Toast.LENGTH_SHORT).show();
                    return;
                }

                showDialog("Updating Address...", true);

                // send the changes to the server
                Map<String, Object> changes = new HashMap<>();
                changes.put("homeAddressLine1", Strings.returnSafely(newAddress.get(_ADDRESS_LINE_1)));
                changes.put("homeAddressLine2", Strings.returnSafely(newAddress.get(_ADDRESS_LINE_2)));
                changes.put("homeAddressLine3", Strings.returnSafely(newAddress.get(_ADDRESS_LINE_3)));
                changes.put("homeCountry", Strings.returnSafely(newAddress.get(_COUNTRY)));
                changes.put("homePostcode", Strings.returnSafely(newAddress.get(_POSTCODE)));

                ref.updateChildren(changes, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            showErrorDialog();
                            return;
                        }

                        hideDialog();
                    }
                });

            }

        }).show();

        return true;
    }

    @Optional
    @OnLongClick(R.id.work_areas)
    public boolean onWorkAreasLongTouch() {
        Intent i = new Intent(getActivity(), EditListActivity.class);
        i.putStringArrayListExtra("list", mCurrentTradesman != null ? mCurrentTradesman.getWorkAreasList() : new ArrayList<String>());
        getActivity().startActivityForResult(i, Ids.WORK_AREAS_CODE);
        return true;
    }

    public static class WorkArea {

        public String workArea;

        public WorkArea(String workArea) {
            this.workArea = workArea;
        }

    }

    public void onNewWorkAreasReturned(ArrayList<String> workAreas) {
        if (workAreas == null) workAreas = new ArrayList<>();

        DatabaseReference ref = FirebaseUtils.getCurrentTradesmanRef();
        if (ref == null) {
            Toast.makeText(getContext(), "Sorry, unable to make changes rightb now", Toast.LENGTH_SHORT).show();
            return;
        }

        showDialog("Updating Work Areas...", true);

        Map<String, Object> workAreasMap = new HashMap<>();
        for (String workArea : workAreas) {
            if (Strings.isEmpty(workArea)) continue;

            workAreasMap.put(workArea, true);
        }

        ref.child("workAreas").setValue(workAreasMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    showErrorDialog();
                    return;
                }

                hideDialog();
            }
        });
    }

}
