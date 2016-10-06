package com.homefix.tradesman.timeslot.own_job.payments;

import com.homefix.tradesman.base.view.BaseFragmentView;
import com.homefix.tradesman.model.Charge;
import com.homefix.tradesman.model.Payment;

/**
 * Created by samuel on 10/6/2016.
 */

public interface PaymentsFragmentView extends BaseFragmentView {

    void showEditPayment(Payment payment);

    void removePaymentClicked(Payment payment);
}
