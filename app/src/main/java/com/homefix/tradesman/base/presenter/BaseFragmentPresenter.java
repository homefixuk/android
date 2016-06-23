package com.homefix.tradesman.base.presenter;

import android.content.Intent;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.homefix.tradesman.base.view.BaseView;


/**
 * Created by samuel on 5/31/2016.
 */

public abstract class BaseFragmentPresenter<V extends BaseView> extends MvpBasePresenter<V> {

    public BaseFragmentPresenter(V v) {
        attachView(v);
    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onDestroy() {
        detachView(false);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

}
