package com.homefix.tradesman.data;

import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.model.Tradesman;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.listener.interfaces.OnGotObjectListener;

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

    private synchronized static void setCurrentUser(Tradesman user) {
        mCurrentUser = user;
        CacheUtils.writeObjectFile("current_user", mCurrentUser);
    }

    public static void loadCurrentUser(final OnGotObjectListener<Tradesman> callback) {
        // first load from cache
        Tradesman user = CacheUtils.readObjectFile("current_user", Tradesman.class);
        setCurrentUser(user);

        if (user == null) {
            if (callback != null) callback.onGotThing(mCurrentUser);
            return;
        }

        HomeFix.getAPI().getTradesman(mCurrentUser.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Tradesman>() {
                    @Override
                    public final void onCompleted() {
                        MyLog.e("HomeActivity", "[getTradesman] onComplete");
                    }

                    @Override
                    public final void onError(Throwable e) {
                        MyLog.e("HomeActivity", e.getMessage());
                    }

                    @Override
                    public final void onNext(Tradesman user) {
                        // if we got a user from the server, update the one we're storing
                        if (user != null) setCurrentUser(user);

                        if (callback != null) callback.onGotThing(getCurrentUser());
                    }
                });
    }

    public static void clearCurrentUser() {
        mCurrentUser = null;
        CacheUtils.writeObjectFile("current_user", null);
    }

}
