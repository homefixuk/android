package com.homefix.tradesman.base.view;

import android.view.View;

import com.homefix.tradesman.base.HomeFixBaseActivity;

/**
 * Created by samuel on 5/31/2016.
 */

public interface BaseFragmentView extends BaseView {

    HomeFixBaseActivity getBaseActivity();

    void onResume();

    void onPause();

    void goToApp(boolean isUserNew, View logoView);

}
