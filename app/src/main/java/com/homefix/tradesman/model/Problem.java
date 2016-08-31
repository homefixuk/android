package com.homefix.tradesman.model;

import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.data.UserController;
import com.samdroid.common.MyLog;
import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by samuel on 6/15/2016.
 */

public class Problem {

    private String name, description;
    private long time;
    private List<Part> potentialParts;

    public Problem() {

    }

    public String getName() {
        return Strings.returnSafely(name);
    }

    public String getDescription() {
        return Strings.returnSafely(description);
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

    public List<Part> getPotentialParts() {
        if (potentialParts == null) potentialParts = new ArrayList<>();

        return potentialParts;
    }

    public void setTime(long time) {
        this.time = time;
    }

    final private static List<Problem> M_PROBLEMs = new ArrayList<>();

    public static void loadServiceTypes() {
        if (!UserController.hasToken()) {
            MyLog.e("Problem", "[loadServiceTypes] no user token");
            return;
        }

        // get the service types from the server
        HomeFix.getAPI().getServiceTypes(UserController.getToken()).enqueue(new Callback<List<Problem>>() {

            @Override
            public void onResponse(Call<List<Problem>> call, Response<List<Problem>> response) {
                // store them in the static list
                getProblemTypes().clear();
                getProblemTypes().addAll(response.body());
            }

            @Override
            public void onFailure(Call<List<Problem>> call, Throwable t) {
                MyLog.e("Problem", "[loadServiceTypes => onFailure]");
                if (MyLog.isIsLogEnabled() && t != null) t.printStackTrace();
            }

        });
    }

    public synchronized static List<Problem> getProblemTypes() {
        return M_PROBLEMs;
    }

    public static List<String> getProblemTypeNames() {
        List<String> names = new ArrayList<>();

        List<Problem> types = getProblemTypes();

        for (int i = 0, len = types.size(); i < len; i++) names.add(types.get(i).getName());

        return names;
    }

}
