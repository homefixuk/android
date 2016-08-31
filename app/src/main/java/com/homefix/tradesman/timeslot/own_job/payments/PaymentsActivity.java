package com.homefix.tradesman.timeslot.own_job.payments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.BaseCloseActivity;
import com.homefix.tradesman.model.Service;
import com.samdroid.common.IntentHelper;

/**
 * Created by samuel on 7/27/2016.
 */

public class PaymentsActivity extends BaseCloseActivity {

    public PaymentsActivity() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String serviceKey = IntentHelper.getStringSafely(i, "serviceKey");

        Service service = Service.getSenderReceiver().remove(serviceKey);

        if (service == null || service.getServiceSet() == null) {
            Toast.makeText(getContext(), "Sorry, something went wrong.", Toast.LENGTH_SHORT).show();
            finishWithAnimation();
            return;
        }

        setActionbarTitle("Payments");

        if (baseFragment == null) {
            baseFragment = new PaymentsFragment();
            ((PaymentsFragment) baseFragment).setService(service);
        }
        replaceFragment(baseFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add && baseFragment != null) {
            ((PaymentsFragment) baseFragment).addClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
