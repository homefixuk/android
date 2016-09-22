package com.homefix.tradesman.timeslot.base_timeslot;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.base.presenter.BaseFragmentPresenter;
import com.homefix.tradesman.calendar.HomeFixCal;
import com.homefix.tradesman.data.TradesmanController;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.MyLog;
import com.samdroid.network.NetworkManager;

import java.util.Calendar;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by samuel on 7/13/2016.
 */

public class BaseTimeslotFragmentPresenter<V extends BaseTimeslotView> extends BaseFragmentPresenter<V> {

    private Timeslot.TYPE mType;

    public BaseTimeslotFragmentPresenter(V availabilityView, Timeslot.TYPE type) {
        super(availabilityView);
        mType = type;
    }

    public void save(final Timeslot timeslot, Calendar mStart, Calendar mEnd) {
        if (!isViewAttached()) return;

        // if the user has made no changes to the timeslot
        if (getView().getTimeslot() != null && !getView().hasMadeChanges()) {
            getView().onSaveComplete(timeslot);
            return;
        }

        if (mStart == null || mEnd == null || !NetworkManager.hasConnection(getView().getContext())) {
            getView().showErrorDialog();
            return;
        }

        getView().showDialog((timeslot == null ? "Adding" : "Updating") + " timeslot...", true);

        Callback<Timeslot> callback = new Callback<Timeslot>() {
            @Override
            public void onResponse(Call<Timeslot> call, Response<Timeslot> response) {
                Timeslot ts = response.body();

                if (ts == null || ts.getType().isEmpty() || ts.getStart() == 0 || ts.getEnd() == 0) {
                    onFailure(call, null);
                    return;
                }

                MyLog.e(BaseTimeslotFragmentPresenter.class.getSimpleName(), "[onResponse]: " + ts);

                if (timeslot == null) {
                    HomeFixCal.addEvent(ts);

                } else {
                    // update the Homefix calendar
                    HomeFixCal.changeEvent(timeslot, ts);
                }

                getView().onSaveComplete(ts);
            }

            @Override
            public void onFailure(Call<Timeslot> call, Throwable t) {
                if (t != null && MyLog.isIsLogEnabled()) t.printStackTrace();

                if (!isViewAttached()) return;

                getView().showErrorDialog();
            }
        };

        HomeFix.TimeslotMap map = new HomeFix.TimeslotMap(mStart.getTimeInMillis(), mEnd.getTimeInMillis(), true, mType);

        // if the user was adding a timeslot
        if (timeslot == null) {
            HomeFix.getAPI().addTimeslot(TradesmanController.getToken(), map).enqueue(callback);

        } else {
            // else the user was editing a timeslot //
            HomeFix.getAPI().updateTimeslot(TradesmanController.getToken(), timeslot.getId(), map).enqueue(callback);
        }
    }

    public void delete(final Timeslot timeslot) {
        if (!isViewAttached()) return;

        if (timeslot == null || !NetworkManager.hasConnection(getView().getContext())) {
            getView().showErrorDialog();
            return;
        }

        MaterialDialogWrapper.getNegativeConfirmationDialog(
                getView().getBaseActivity(),
                "Are you sure you want to delete this event?",
                "DELETE",
                "CANCEL",
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getView().showDialog("Deleting timeslot...", true);

                        Callback<Map<String, Object>> callback = new Callback<Map<String, Object>>() {
                            @Override
                            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                                Map<String, Object> map = response.body();

                                if (map == null || !(boolean) map.get("success")) {
                                    onFailure(call, null);
                                    return;
                                }

                                if (Timeslot.TYPE.OWN_JOB.name().equals(timeslot.getType()) && timeslot.getService() != null) {
                                    // call deleteService
                                    HomeFix.getAPI().deleteService(TradesmanController.getToken(), timeslot.getId())
                                            .enqueue(new Callback<Map<String, Object>>() {
                                                @Override
                                                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                                                    Map<String, Object> map = response.body();

                                                    if (map == null || !(boolean) map.get("success")) {
                                                        onFailure(call, null);
                                                        return;
                                                    }

                                                    MyLog.e(BaseTimeslotFragmentPresenter.class.getSimpleName(), "[onResponse]: " + map);

                                                    // remove the original timeslot
                                                    HomeFixCal.changeEvent(timeslot, null);

                                                    getView().onDeleteComplete(timeslot);
                                                }

                                                @Override
                                                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                                                    if (t != null && MyLog.isIsLogEnabled())
                                                        t.printStackTrace();

                                                    if (!isViewAttached()) return;

                                                    getView().showErrorDialog();
                                                }
                                            });
                                    return;
                                }

                                MyLog.e(BaseTimeslotFragmentPresenter.class.getSimpleName(), "[onResponse]: " + map);

                                // remove the original timeslot
                                HomeFixCal.changeEvent(timeslot, null);

                                getView().onDeleteComplete(timeslot);
                            }

                            @Override
                            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                                if (t != null && MyLog.isIsLogEnabled()) t.printStackTrace();

                                if (!isViewAttached()) return;

                                getView().showErrorDialog();
                            }

                        };

                        HomeFix.getAPI().deleteTimeslot(TradesmanController.getToken(), timeslot.getId()).enqueue(callback);

                    }

                }, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

}
