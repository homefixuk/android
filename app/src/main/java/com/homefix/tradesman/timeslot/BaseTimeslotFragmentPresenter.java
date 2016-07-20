package com.homefix.tradesman.timeslot;

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

public class BaseTimeslotFragmentPresenter<V extends BaseTimeslotView> extends BaseFragmentPresenter<V> {

    private Timeslot.TYPE mType;

    public BaseTimeslotFragmentPresenter(V availabilityView, Timeslot.TYPE type) {
        super(availabilityView);
        mType = type;
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
            HomeFix.getAPI().addTimeslot(UserController.getToken(), map).enqueue(callback);

        } else {
            // else the user was editing a timeslot //
            HomeFix.getAPI().updateTimeslot(UserController.getToken(), timeslot.getObjectId(), map).enqueue(callback);
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

                MyLog.e(BaseTimeslotFragmentPresenter.class.getSimpleName(), "[onResponse]: " + map);

                // remove the original timeslot
                HomeFixCal.changeEvent(timeslot, null);

                getView().onDeleteComplete();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (t != null && MyLog.isIsLogEnabled()) t.printStackTrace();

                if (!isViewAttached()) return;

                // TODO: check error contents

                getView().showErrorDialog();
            }

        };

        HomeFix.getAPI().deleteTimeslot(UserController.getToken(), timeslot.getObjectId()).enqueue(callback);
    }

}
