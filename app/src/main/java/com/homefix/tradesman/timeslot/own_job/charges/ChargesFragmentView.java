package com.homefix.tradesman.timeslot.own_job.charges;

import com.homefix.tradesman.base.view.BaseFragmentView;
import com.homefix.tradesman.model.Charge;

/**
 * Created by samuel on 10/6/2016.
 */

public interface ChargesFragmentView extends BaseFragmentView {

    void showEditCharge(Charge charge);

    void removeChargeClicked(Charge charge);
}
