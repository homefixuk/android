package com.homefix.tradesman.availability;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.fragment.BaseCloseFragment;

/**
 * Created by samuel on 7/13/2016.
 */

public class AvailabilityFragment extends BaseCloseFragment<AvailabilityActivity, AvailabilityView, AvailabilityPresenter> implements AvailabilityView {

    private boolean isEdit = false;

    public AvailabilityFragment() {
        super(AvailabilityFragment.class.getSimpleName());
    }

    @Override
    protected AvailabilityPresenter getPresenter() {
        if (presenter == null) presenter = new AvailabilityPresenter(this);

        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_availability;
    }

    @Override
    protected void injectDependencies() {
        super.injectDependencies();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}
