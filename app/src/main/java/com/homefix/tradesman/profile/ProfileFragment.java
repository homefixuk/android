package com.homefix.tradesman.profile;

import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.homefix.tradesman.R;
import com.homefix.tradesman.api.API;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.base.activity.BaseToolbarNavMenuActivity;
import com.homefix.tradesman.base.activity.EditListActivity;
import com.homefix.tradesman.base.fragment.BaseFragment;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.data.TradesmanController;
import com.homefix.tradesman.model.Tradesman;
import com.homefix.tradesman.model.User;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.MyLog;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;
import com.samdroid.string.ToCommaParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnLongClick;
import butterknife.Optional;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private Tradesman mCurrentTradesman;

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
        refreshView();
    }

    private void refreshView() {
        TradesmanController.loadCurrentUser(false, new OnGotObjectListener<Tradesman>() {
            @Override
            public void onGotThing(Tradesman o) {
                mCurrentTradesman = o;
                setupView();
            }
        });
    }

    private void setupView() {
        if (mCurrentTradesman == null) return;

        // load the profile image
        Uri imageUri = Uri.parse(Strings.checkUrl(mCurrentTradesman.getPicture()));
        imageView.setImageURI(imageUri);

        User user = mCurrentTradesman.getUser();
        nameView.setText(Strings.returnSafely(user != null ? user.getName() : "No Name"));
        emailView.setText(Strings.returnSafely(user != null ? user.getEmail() : "no@email.com"));
        homePhoneView.setText(Strings.returnSafely(user != null ? user.getHomePhone() : "Long hold here to set your home phone"));
        mobilePhoneView.setText(Strings.returnSafely(user != null ? user.getMobile() : "Long hold here to set your mobile phone"));
        addressView.setText(Strings.returnSafely(getReadableLocationString(", "), "Long hold here to set your address"));
        yearsExperienceView.setText(Strings.formatRaised(mCurrentTradesman.getExperience()));

        workAreasView.setText(Strings.returnSafely(Strings.flattenList(mCurrentTradesman.getWorkAreas(), ", "), "Long hold here to set your work areas"));
    }

    protected String getReadableLocationString(String delimiter) {
        if (mCurrentTradesman == null) return "";

        User user = mCurrentTradesman.getUser();
        if (user == null) return "";

        String s = "";

        delimiter = Strings.returnSafely(delimiter);

        s += !Strings.isEmpty(user.getHomeAddressLine1()) ? user.getHomeAddressLine1() + delimiter : "";
        s += !Strings.isEmpty(user.getHomeAddressLine2()) ? user.getHomeAddressLine2() + delimiter : "";
        s += !Strings.isEmpty(user.getHomeAddressLine3()) ? user.getHomeAddressLine3() + delimiter : "";
        s += !Strings.isEmpty(user.getHomePostcode()) ? user.getHomePostcode() + delimiter : "";
        s += !Strings.isEmpty(user.getHomeCountry()) ? user.getHomeCountry() : "";

        if (s.endsWith(delimiter)) s = s.substring(0, s.length() - delimiter.length());

        return s;
    }

    @OnLongClick(R.id.home_phone)
    public boolean onHomePhoneLongTouch() {
        if (mCurrentTradesman == null) return false;

        User user = mCurrentTradesman.getUser();
        if (user == null) return false;

        MaterialDialogWrapper.getEditTextDialog(
                getActivity(),
                "",
                user.getHomePhone(),
                "Home Phone Number",
                "SAVE",
                InputType.TYPE_CLASS_PHONE,
                new MaterialDialogWrapper.SubmitObjectChangesCallback() {
                    @Override
                    public void onChangeSubmitted(Object original, Object changed) {
                        // if there was no change, do nothing
                        if ((original != null && original.equals(changed))
                                || (original == null && changed == null)) return;

                        String newMobile = Strings.returnSafely(String.valueOf(changed));

                        showDialog("Updating Home Phone...", true);

                        Map<String, Object> changes = new HashMap<>();
                        changes.put("homePhone", newMobile);
                        HomeFix.getAPI()
                                .updateTradesmanDetails(TradesmanController.getToken(), changes)
                                .enqueue(new Callback<Tradesman>() {
                                    @Override
                                    public void onResponse(Call<Tradesman> call, Response<Tradesman> response) {
                                        if (response == null || response.body() == null) {
                                            showErrorDialog();
                                            return;
                                        }

                                        mCurrentTradesman = response.body();
                                        hideDialog();
                                        setupView();
                                    }

                                    @Override
                                    public void onFailure(Call<Tradesman> call, Throwable t) {
                                        if (t != null && MyLog.isIsLogEnabled())
                                            t.printStackTrace();

                                        showErrorDialog();
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

        User user = mCurrentTradesman.getUser();
        if (user == null) return false;

        MaterialDialogWrapper.getEditTextDialog(
                getActivity(),
                null,
                user.getMobile(),
                "Mobile Phone Number",
                "SAVE",
                InputType.TYPE_CLASS_PHONE,
                new MaterialDialogWrapper.SubmitObjectChangesCallback() {
                    @Override
                    public void onChangeSubmitted(Object original, Object changed) {
                        // if there was no change, do nothing
                        if ((original != null && original.equals(changed))
                                || (original == null && changed == null)) return;

                        String newMobile = Strings.returnSafely(String.valueOf(changed));

                        showDialog("Updating Mobile Phone...", true);

                        Map<String, Object> changes = new HashMap<>();
                        changes.put("mobile", newMobile);
                        HomeFix.getAPI()
                                .updateTradesmanDetails(TradesmanController.getToken(), changes)
                                .enqueue(new Callback<Tradesman>() {
                                    @Override
                                    public void onResponse(Call<Tradesman> call, Response<Tradesman> response) {
                                        if (response == null || response.body() == null) {
                                            showErrorDialog();
                                            return;
                                        }

                                        mCurrentTradesman = response.body();
                                        hideDialog();
                                        setupView();
                                    }

                                    @Override
                                    public void onFailure(Call<Tradesman> call, Throwable t) {
                                        if (t != null && MyLog.isIsLogEnabled())
                                            t.printStackTrace();

                                        showErrorDialog();
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

        User user = mCurrentTradesman.getUser();
        if (user == null) return false;

        // show a multi edit dialog so the address can be changed

        List<String> keys = new ArrayList<>();
        keys.add(_ADDRESS_LINE_1);
        keys.add(_ADDRESS_LINE_2);
        keys.add(_ADDRESS_LINE_3);
        keys.add(_COUNTRY);
        keys.add(_POSTCODE);

        List<String> values = new ArrayList<>();
        values.add(Strings.returnSafely(user.getHomeAddressLine1()));
        values.add(Strings.returnSafely(user.getHomeAddressLine2()));
        values.add(Strings.returnSafely(user.getHomeAddressLine3()));
        values.add(Strings.returnSafely(user.getHomeCountry()));
        values.add(Strings.returnSafely(user.getHomePostcode()));

        MaterialDialogWrapper.getMultiInputDialog(getActivity(), "SET ADDRESS", keys, values, new OnGotObjectListener<HashMap<String, String>>() {

            @Override
            public void onGotThing(HashMap<String, String> newAddress) {
                if (newAddress == null) return;

                showDialog("Updating Address...", true);

                // send the changes to the server
                Map<String, Object> changes = new HashMap<>();
                changes.put("addressLine1", Strings.returnSafely(newAddress.get(_ADDRESS_LINE_1)));
                changes.put("addressLine2", Strings.returnSafely(newAddress.get(_ADDRESS_LINE_2)));
                changes.put("addressLine3", Strings.returnSafely(newAddress.get(_ADDRESS_LINE_3)));
                changes.put("country", Strings.returnSafely(newAddress.get(_COUNTRY)));
                changes.put("postcode", Strings.returnSafely(newAddress.get(_POSTCODE)));
                HomeFix.getAPI()
                        .updateTradesmanDetails(TradesmanController.getToken(), changes)
                        .enqueue(new Callback<Tradesman>() {
                            @Override
                            public void onResponse(Call<Tradesman> call, Response<Tradesman> response) {
                                if (response == null || response.body() == null) {
                                    showErrorDialog();
                                    return;
                                }

                                // update the view with the user profile returned
                                mCurrentTradesman = response.body();
                                hideDialog();
                                setupView();
                            }

                            @Override
                            public void onFailure(Call<Tradesman> call, Throwable t) {
                                if (t != null && MyLog.isIsLogEnabled())
                                    t.printStackTrace();

                                showErrorDialog();
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
        i.putStringArrayListExtra("list", new ArrayList<>(mCurrentTradesman != null ? mCurrentTradesman.getWorkAreas() : new ArrayList<String>()));
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

        showDialog("Updating Work Areas...", true);

        // convert to comma separated parameters
        ToCommaParameters.run(new ToCommaParameters.ToCommaParametersCallback() {
            @Override
            public void onToCommaParametersCalled(String... s) {
                HomeFix.getAPI().updateTradesmanWorkAreas2(TradesmanController.getToken(), s)
                        .enqueue(new Callback<Tradesman>() {
                            @Override
                            public void onResponse(Call<Tradesman> call, Response<Tradesman> response) {
                                if (response == null || response.body() == null) {
                                    showErrorDialog();
                                    return;
                                }

                                mCurrentTradesman = response.body();
                                hideDialog();
                                setupView();
                            }

                            @Override
                            public void onFailure(Call<Tradesman> call, Throwable t) {
                                if (t != null && MyLog.isIsLogEnabled())
                                    t.printStackTrace();

                                showErrorDialog();
                            }
                        });
            }
        }, workAreas);
    }

}
