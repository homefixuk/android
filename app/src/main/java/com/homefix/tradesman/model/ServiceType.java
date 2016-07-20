package com.homefix.tradesman.model;

import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.data.UserController;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by samuel on 6/15/2016.
 */

public class ServiceType {

    String name, description;
    long time;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    final private static List<ServiceType> mServiceTypes = new ArrayList<>();

    public static void loadServiceTypes() {
        if (!UserController.hasToken()) {
            MyLog.e("ServiceType", "[loadServiceTypes] no user token");
            return;
        }

        // get the service types from the server
        HomeFix.getAPI().getServiceTypes(UserController.getToken()).enqueue(new Callback<List<ServiceType>>() {

            @Override
            public void onResponse(Call<List<ServiceType>> call, Response<List<ServiceType>> response) {
                // store them in the static list
                getServiceTypes().clear();
                getServiceTypes().addAll(response.body());
            }

            @Override
            public void onFailure(Call<List<ServiceType>> call, Throwable t) {
                MyLog.e("ServiceType", "[loadServiceTypes => onFailure]");
                if (MyLog.isIsLogEnabled() && t != null) t.printStackTrace();
            }

        });
    }

    public synchronized static List<ServiceType> getServiceTypes() {
        return mServiceTypes;
    }

    public static List<String> getServiceTypeNames() {
        List<String> names = new ArrayList<>();

        List<ServiceType> types = getServiceTypes();

        for (int i = 0, len = types.size(); i < len; i++) names.add(types.get(i).getName());

        return names;
    }

}
