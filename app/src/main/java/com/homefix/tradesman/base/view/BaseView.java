package com.homefix.tradesman.base.view;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by samuel on 5/30/2016.
 */

public interface BaseView extends MvpView {

    public static class ConfirmDialogCallback {

        public void onPositive() {

        }

        public void onNegative() {

        }

    }

    void showDialog(String message, boolean loading);

    void hideDialog();

    void showConfirmDialog(String message, String positiveText, String negative, ConfirmDialogCallback callback);

    void updateDialogMessage(String message);

    Context getContext();

}
