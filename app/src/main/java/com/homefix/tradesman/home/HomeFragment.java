package com.homefix.tradesman.home;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.BaseFragment;
import com.homefix.tradesman.base.HomeFixBaseActivity;
import com.homefix.tradesman.listener.OnNewLocationListener;
import com.homefix.tradesman.service.LocationService;

import java.util.Date;

/**
 * Created by samuel on 6/28/2016.
 */

public class HomeFragment extends BaseFragment<HomeFixBaseActivity, HomeFragmentView, HomeFragmentPresenter> implements HomeFragmentView, OnNewLocationListener {

    TextView textView;

    public HomeFragment() {
        super(HomeFragment.class.getSimpleName());
    }

    @Override
    protected HomeFragmentPresenter getPresenter() {
        if (presenter == null) presenter = new HomeFragmentPresenter(this);

        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home;
    }

    @Override
    protected void injectDependencies() {
        super.injectDependencies();

        textView = (TextView) getView().findViewById(R.id.text);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        onNewLocationReceived(null);
    }

    @Override
    public void onNewLocationReceived(Location location) {
        if (textView == null) return;

        if (location == null) textView.setText(new Date().toString() + "\n\nnull location");
        else
            textView.setText(new Date().toString() + "\n\n(" + location.getLatitude() + ", " + location.getLongitude() + ")");
    }

    @Override
    public void onResume() {
        super.onResume();
        LocationService.addOnNewLocationListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocationService.removeOnNewLocationListener(this);
    }

}
