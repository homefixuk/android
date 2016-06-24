package com.homefix.tradesman.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.homefix.tradesman.base.presenter.BaseFragmentPresenter;
import com.homefix.tradesman.base.view.BaseFragmentView;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.string.Strings;

import icepick.Icepick;

/**
 * Created by samuel on 5/30/2016.
 */

public abstract class BaseFragment<A extends HomeFixBaseActivity, V extends BaseFragmentView, P extends BaseFragmentPresenter<V>> extends Fragment implements BaseFragmentView {

    protected final String TAG;

    protected P presenter;

    protected MaterialDialog mBaseDialog;

    public BaseFragment(String TAG) {
        this.TAG = TAG;
    }

    protected abstract P getPresenter();

    @LayoutRes
    protected abstract int getLayoutRes();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Icepick.restoreInstanceState(this, savedInstanceState);
        return inflater.inflate(getLayoutRes(), container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        injectDependencies();
    }

    @Override
    public void hideDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBaseDialog != null) mBaseDialog.dismiss();
            }
        });
    }

    @Override
    public void showDialog(final String message, final boolean loading) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    hideDialog();

                    if (loading)
                        mBaseDialog = MaterialDialogWrapper.getLoadingDialog(getActivity(), message).build();
                    else
                        mBaseDialog = MaterialDialogWrapper.getAlertDialog(getActivity(), message).build();

                    mBaseDialog.show();
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void showConfirmDialog(final String message, final String positiveText, final String negative, final ConfirmDialogCallback callback) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    hideDialog();

                    mBaseDialog = MaterialDialogWrapper.getConfirmationDialog(
                            getActivity(),
                            message,
                            positiveText,
                            negative,
                            new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    callback.onPositive();
                                }
                            },
                            new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    callback.onNegative();
                                }
                            });

                    mBaseDialog.show();
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void updateDialogMessage(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBaseDialog == null) return;

                try {
                    mBaseDialog.setContent(Strings.returnSafely(message));
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    /**
     * Inject the dependencies
     */

    protected void injectDependencies() {

    }

    @Override
    public void onPause() {
        getPresenter().onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPresenter().onDestroy();
    }

    @Override
    public void goToApp() {
        if (getBaseActivity() == null) return;

        getBaseActivity().goToApp();
    }

    @Override
    public A getBaseActivity() {
        return (A) getActivity();
    }

}