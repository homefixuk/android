package com.homefix.tradesman.availability;

import com.homefix.tradesman.BuildConfig;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.base.presenter.BaseFragmentPresenter;
import com.homefix.tradesman.calendar.HomeFixCal;
import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.model.Timeslot;
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

public class AvailabilityPresenter extends BaseFragmentPresenter<AvailabilityView> {

    public AvailabilityPresenter(AvailabilityView availabilityView) {
        super(availabilityView);
    }

    public void save(final Timeslot timeslot, Calendar mStart, Calendar mEnd) {
        if (!isViewAttached()) return;

        if (mStart == null || mEnd == null || !NetworkManager.hasConnection(getView().getContext())) {
            getView().showErrorDialog();
            return;
        }

        getView().showDialog((timeslot == null ? "Adding" : "Updating") + " timeslot...", true);

        Callback<Timeslot> callback = new Callback<Timeslot>() {
            @Override
            public void onResponse(Call<Timeslot> call, Response<Timeslot> response) {
                Timeslot ts = response.body();

                if (ts == null) {
                    onFailure(call, null);
                    return;
                }

                MyLog.e(AvailabilityPresenter.class.getSimpleName(), "[onResponse]: " + ts);

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

        HomeFix.TimeslotMap map = new HomeFix.TimeslotMap(mStart.getTimeInMillis(), mEnd.getTimeInMillis(), true, Timeslot.TYPE.AVAILABILITY);

        // if the user was adding a timeslot
        if (timeslot == null) {
            if (BuildConfig.FLAVOR.equals("apiary_mock")) {
                HomeFix.getMockAPI().addTimeslot(UserController.getToken(), map).enqueue(callback);

            } else if (BuildConfig.FLAVOR.equals("custom")) {
                HomeFix.getAPI().addTimeslot(UserController.getToken(), map).enqueue(callback);
            }

        } else {
            // else the user was editing a timeslot //

            if (BuildConfig.FLAVOR.equals("apiary_mock")) {
                HomeFix.getMockAPI().updateTimeslot(UserController.getToken(), timeslot.getObjectId(), map).enqueue(callback);

            } else if (BuildConfig.FLAVOR.equals("custom")) {
                HomeFix.getAPI().updateTimeslot(UserController.getToken(), timeslot.getObjectId(), map).enqueue(callback);
            }
        }
    }

    public void delete(final Timeslot timeslot) {
        if (!isViewAttached()) return;

        if (timeslot == null || !NetworkManager.hasConnection(getView().getContext())) {
            getView().showErrorDialog();
            return;
        }

        getView().showDialog("Deleting timeslot...", true);

        Callback<Map<String, Object>> callback = new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> map = response.body();

                if (map == null || !(boolean) map.get("success")) {
                    onFailure(call, null);
                    return;
                }

                MyLog.e(AvailabilityPresenter.class.getSimpleName(), "[onResponse]: " + map);

                // remove the original timeslot
                HomeFixCal.changeEvent(timeslot, null);

                getView().onDeleteComplete();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (t != null && MyLog.isIsLogEnabled()) t.printStackTrace();

                if (!isViewAttached()) return;

                getView().showErrorDialog();
            }

        };

        if (BuildConfig.FLAVOR.equals("apiary_mock")) {
            HomeFix.getMockAPI().deleteTimeslot(UserController.getToken(), timeslot.getObjectId()).enqueue(callback);

        } else if (BuildConfig.FLAVOR.equals("custom")) {
            HomeFix.getAPI().deleteTimeslot(UserController.getToken(), timeslot.getObjectId()).enqueue(callback);
        }
    }

}
