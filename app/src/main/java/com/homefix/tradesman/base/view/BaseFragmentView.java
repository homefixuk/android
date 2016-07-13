package com.homefix.tradesman.base.view;

import com.homefix.tradesman.base.activity.HomeFixBaseActivity;

/**
 * Created by samuel on 5/31/2016.
 */

public interface BaseFragmentView extends BaseView {

    HomeFixBaseActivity getBaseActivity();

    void onResume();

    void onPause();

    void goToApp();

}
