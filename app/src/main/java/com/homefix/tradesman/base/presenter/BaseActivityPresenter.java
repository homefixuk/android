package com.homefix.tradesman.base.presenter;

import android.content.Intent;
import android.os.Bundle;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.homefix.tradesman.base.view.BaseActivityView;
import com.samdroid.network.NetworkManager;

/**
 * Created by samuel on 5/31/2016.
 */

public abstract class BaseActivityPresenter<V extends BaseActivityView> extends MvpBasePresenter<V> {

    public void onCreate(Bundle savedInstanceState) {
        if (!isViewAttached()) return;
    }

    public void onResume() {
        if (!isViewAttached()) return;
    }

    public void onPause() {
        if (!isViewAttached()) return;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void onBackPressed() {
        if (!isViewAttached()) return;

        getView().finishWithAnimation();
    }

    public void onDestroy() {

    }

    /**
     * @return if there is currently a network connection
     */
    public boolean hasNetworkConnection() {
        return isViewAttached() && NetworkManager.hasConnection(getView().getContext());
    }

    /**
     * Called after activity onResume when all core required permissions have been granted
     */
    public void onAllRequiredPermissionsGranted() {
    }

}
