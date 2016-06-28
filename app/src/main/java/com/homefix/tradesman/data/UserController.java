package com.homefix.tradesman.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.homefix.tradesman.HomeFixApplication;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.model.Tradesman;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by samuel on 6/24/2016.
 */

public class UserController {

    private static Tradesman mCurrentUser;

    public synchronized static Tradesman getCurrentUser() {
        return mCurrentUser;
    }

    private synchronized static void setCurrentUser(Context context, Tradesman user) {
        mCurrentUser = user;
        CacheUtils.writeObjectFile("current_user", mCurrentUser);
    }

    public static void loadCurrentUser(final Context context, final OnGotObjectListener<Tradesman> callback) {
        mCurrentUser = CacheUtils.readObjectFile("current_user", Tradesman.class);

        if (mCurrentUser == null) {
            if (callback != null) callback.onGotThing(null);
            return;
        }

        HomeFix.getAPI().getTradesman(mCurrentUser.getId()).enqueue(new Callback<Tradesman>() {
            @Override
            public void onResponse(Call<Tradesman> call, Response<Tradesman> response) {
                Tradesman user = response != null ? response.body() : null;

                // if we got a user from the server, update the one we're storing
                if (user != null) setCurrentUser(context, user);

                if (callback != null) callback.onGotThing(getCurrentUser());
            }

            @Override
            public void onFailure(Call<Tradesman> call, Throwable t) {
                if (MyLog.isIsLogEnabled()) t.printStackTrace();
            }
        });
    }

    public static void clearCurrentUser(Context context) {
        mCurrentUser = null;
        CacheUtils.writeObjectFile("current_user", null);
    }

}
