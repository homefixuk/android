package com.homefix.tradesman.data;

import com.homefix.tradesman.BuildConfig;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.model.Tradesman;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by samuel on 6/24/2016.
 */

public class UserController {

    private static String token = null;
    private static Tradesman mCurrentUser = null;
    private static long lastUpdateTime = 0, refreshTime = 1000 * 60 * 10;

    public synchronized static Tradesman getCurrentUser() {
        return mCurrentUser;
    }

    private synchronized static void setCurrentUser(Tradesman user) {
        mCurrentUser = user;
        CacheUtils.writeObjectFile("current_user", mCurrentUser);
    }

    public static void loadCurrentUser(boolean forceUpdate, final OnGotObjectListener<Tradesman> callback) {
        mCurrentUser = CacheUtils.readObjectFile("current_user", Tradesman.class);

        token = CacheUtils.readFile("token");

        if (mCurrentUser == null && Strings.isEmpty(token)) {
            if (callback != null) callback.onGotThing(mCurrentUser);
            return;
        }

        // if we already have a current user and we are not forcing an update or the refresh time has not expired
        if (mCurrentUser != null && !(forceUpdate || System.currentTimeMillis() - lastUpdateTime > refreshTime)) {
            // return the recently fetched user
            if (callback != null) callback.onGotThing(mCurrentUser);
            return;
        }

        Callback<Tradesman> callback1 = new Callback<Tradesman>() {
            @Override
            public void onResponse(Call<Tradesman> call, Response<Tradesman> response) {
                Tradesman user = response != null ? response.body() : null;

                // if we got a user from the server, update the one we're storing
                if (user != null) setCurrentUser(user);

                lastUpdateTime = System.currentTimeMillis();

                if (callback != null) callback.onGotThing(getCurrentUser());
            }

            @Override
            public void onFailure(Call<Tradesman> call, Throwable t) {
                if (MyLog.isIsLogEnabled()) t.printStackTrace();
            }
        };

        if (BuildConfig.FLAVOR.equals("apiary_mock")) {
            HomeFix.getMockAPI().getTradesman(token).enqueue(callback1);

        } else if (BuildConfig.FLAVOR.equals("custom")) {
            HomeFix.getAPI().getTradesman(token).enqueue(callback1);
        }
    }

    public static void clearCurrentUser() {
        mCurrentUser = null;
        CacheUtils.writeObjectFile("current_user", null);
        CacheUtils.writeFile("token", "");
    }

    public static String getToken() {
        return Strings.returnSafely(token);
    }

    public static boolean hasToken() {
        return !Strings.isEmpty(token);
    }

}
