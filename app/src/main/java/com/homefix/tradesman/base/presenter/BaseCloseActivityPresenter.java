package com.homefix.tradesman.base.presenter;

import com.homefix.tradesman.base.view.BaseCloseActivityView;

/**
 * Created by samuel on 6/17/2016.
 */

public class BaseCloseActivityPresenter<V extends BaseCloseActivityView> extends BaseToolbarActivityPresenter<V> {

    public void onCloseClicked() {
        if (!isViewAttached()) return;

        getView().tryClose();
    }

    @Override
    public void onBackPressed() {
        onCloseClicked();
    }

    public void close() {
        if (!isViewAttached()) return;

        getView().finishWithAnimation();
    }

}
