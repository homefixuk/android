package com.homefix.tradesman.data;

import android.content.Context;

import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.model.Tradesman;
import com.homefix.tradesman.model.TradesmanPrivate;
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
    private static TradesmanPrivate mTradesmanPrivate;
    private static long lastUpdateTime = 0, refreshTime = 1000 * 60 * 10;

    public synchronized static Tradesman getCurrentUser() {
        return mCurrentUser;
    }

    private synchronized static void setCurrentUser(Tradesman user) {
        mCurrentUser = user;
        CacheUtils.writeObjectFile("current_user", mCurrentUser);
    }

    public synchronized static TradesmanPrivate getCurrentTradesmanPrivate() {
        return mTradesmanPrivate;
    }

    private synchronized static void setTradesmanPrivate(TradesmanPrivate tradesmanPrivate) {
        mTradesmanPrivate = tradesmanPrivate;
        CacheUtils.writeObjectFile("tradesman_private", mTradesmanPrivate);
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

                if (callback != null) callback.onGotThing(getCurrentUser());
            }

        };

        HomeFix.getAPI().getTradesman(token).enqueue(callback1);
    }

    public static void clearCurrentUser() {
        mCurrentUser = null;
        mTradesmanPrivate = null;
        CacheUtils.writeObjectFile("current_user", null);
        CacheUtils.writeObjectFile("tradesman_private", null);
        CacheUtils.writeFile("token", "");
    }

    public static String getToken() {
        return Strings.returnSafely(token);
    }

    public static boolean hasToken() {
        return !Strings.isEmpty(token);
    }

    public static void loadTradesmanPrivate(Context context, final OnGotObjectListener<TradesmanPrivate> callback) {
        mTradesmanPrivate = CacheUtils.readObjectFile("tradesman_private", TradesmanPrivate.class);

        if (mCurrentUser == null && Strings.isEmpty(token)) {
            if (callback != null) callback.onGotThing(mTradesmanPrivate);
            return;
        }

        String API_KEY = context != null ? context.getString(HomeFix.API_KEY_resId) : "";

        Callback<TradesmanPrivate> callback1 = new Callback<TradesmanPrivate>() {
            @Override
            public void onResponse(Call<TradesmanPrivate> call, Response<TradesmanPrivate> response) {
                TradesmanPrivate tradesmanPrivate = response != null ? response.body() : null;

                // if we got a user from the server, update the one we're storing
                if (tradesmanPrivate != null) setTradesmanPrivate(tradesmanPrivate);

                if (callback != null) callback.onGotThing(getCurrentTradesmanPrivate());
            }

            @Override
            public void onFailure(Call<TradesmanPrivate> call, Throwable t) {
                if (MyLog.isIsLogEnabled()) t.printStackTrace();

                if (callback != null) callback.onGotThing(getCurrentTradesmanPrivate());
            }
        };

        HomeFix.getAPI().getTradesmanPrivate(API_KEY, token).enqueue(callback1);
    }

}
