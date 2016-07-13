package com.homefix.tradesman.base.fragment;

import com.homefix.tradesman.base.activity.HomeFixBaseActivity;
import com.homefix.tradesman.base.presenter.BaseFragmentPresenter;
import com.homefix.tradesman.base.view.BaseFragmentView;

/**
 * Created by samuel on 7/13/2016.
 */

public abstract class BaseCloseFragment<A extends HomeFixBaseActivity, V extends BaseFragmentView, P extends BaseFragmentPresenter<V>> extends BaseFragment<A, V, P> {

    public BaseCloseFragment(String TAG) {
        super(TAG);
    }

    /**
     * @return whether the fragment is allowed to be closed
     */
    public boolean canClose() {
        return true;
    }

    /**
     * Called when the fragment is attempted to be closed
     */
    public void onCloseClicked() {

    }

}
