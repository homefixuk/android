package com.homefix.tradesman.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.TextView;

import com.homefix.tradesman.R;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.api.ServiceFactory;
import com.homefix.tradesman.base.BaseToolbarNavMenuActivity;
import com.homefix.tradesman.model.Timeslot;
import com.samdroid.common.MyLog;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by samuel on 6/22/2016.
 */

public class HomeActivity extends BaseToolbarNavMenuActivity<HomeView, HomePresenter> implements HomeView {

    public HomeActivity() {
        super(HomeActivity.class.getSimpleName());
    }

    @Override
    public HomePresenter getPresenter() {
        if (presenter == null) presenter = new HomePresenter();

        return presenter;
    }

    @Override
    protected HomeView getThisView() {
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        HomeFix.API service = HomeFix.getAPI();
//
//        Map<String, String> params = new HashMap<>();
//        params.put("email", "test@gmail.com");
//        params.put("password", "doivjfivjfv");
//        service.login(params)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Tradesman>() {
//                    @Override
//                    public final void onCompleted() {
//                        // do nothing
//                        MyLog.e("HomeActivity", "onComplete");
//                    }
//
//                    @Override
//                    public final void onError(Throwable e) {
//                        MyLog.e("HomeActivity", e.getMessage());
//                    }
//
//                    @Override
//                    public final void onNext(Tradesman response) {
//                        MyLog.e("HomeActivity", "Tradesman first name: " + response.getFirst_name());
//                    }
//                });
//
//        service.getTradesmanEvents("id", params)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Timeslot>() {
//                    @Override
//                    public final void onCompleted() {
//                        // do nothing
//                        MyLog.e("HomeActivity", "onComplete");
//                    }
//
//                    @Override
//                    public final void onError(Throwable e) {
//                        MyLog.e("HomeActivity", e.getMessage());
//                    }
//
//                    @Override
//                    public final void onNext(Timeslot response) {
//                        MyLog.e("HomeActivity", "Timeslot start: " + response.getStart());
//                    }
//                });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

}
