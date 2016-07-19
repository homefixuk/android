package com.homefix.tradesman.availability;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.homefix.tradesman.R;
import com.homefix.tradesman.base.fragment.BaseCloseFragment;
import com.homefix.tradesman.model.Timeslot;

/**
 * Created by samuel on 7/13/2016.
 */

public class AvailabilityFragment extends BaseCloseFragment<AvailabilityActivity, AvailabilityView, AvailabilityPresenter> implements AvailabilityView {

    private boolean isEdit = false;

    private Timeslot mTimeslot;

    public AvailabilityFragment() {
        super(AvailabilityFragment.class.getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void setTimeslot(Timeslot mTimeslot) {
        this.mTimeslot = mTimeslot;

        // set the edit mode if we have a timeslot
        isEdit = this.mTimeslot != null;
    }

}
