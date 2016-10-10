package com.homefix.tradesman.home.home_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.HomeFixBaseActivity;
import com.homefix.tradesman.base.fragment.BaseFragment;
import com.homefix.tradesman.common.AnalyticsHelper;
import com.homefix.tradesman.common.Ids;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.timeslot.TimeslotActivity;
import com.samdroid.common.MyLog;
import com.samdroid.listener.interfaces.OnGotObjectListener;

import java.util.Collections;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by samuel on 6/28/2016.
 */

public class HomeFragment extends BaseFragment<HomeFixBaseActivity, HomeFragmentView, HomeFragmentPresenter> implements HomeFragmentView {

    @BindView(R.id.current_job_txt)
    protected TextView currentJobView;

    @BindView(R.id.next_job_txt)
    protected TextView nextJobView;

    @BindView(R.id.current_job_layout)
    protected View currentJobLayout;

    @BindView(R.id.next_job_layout)
    protected View nextJobLayout;

    private Timeslot currentTimeslot, nextTimeslot;
    private boolean isRefreshingCurrentJob, isRefreshingNextJob = false;

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
    public void onResume() {
        super.onResume();
        presenter.attachView(this);
        refresh();
        AnalyticsHelper.track(getContext(), "openHome", new Bundle());
    }

    @Override
    public void refresh() {
        setCurrentJob(currentTimeslot);
        setNextJob(nextTimeslot);

        isRefreshingCurrentJob = true;
        isRefreshingNextJob = true;

        FirebaseUtils.getCurrentService(new OnGotObjectListener<Timeslot>() {

            @Override
            public void onGotThing(Timeslot o) {
                setCurrentJob(o);
            }

        });

        FirebaseUtils.getNextService(new OnGotObjectListener<Timeslot>() {

            @Override
            public void onGotThing(Timeslot o) {
                setNextJob(o);
            }

        });
    }

    private void setCurrentJob(Timeslot timeslot) {
        currentTimeslot = timeslot;

        if (currentJobLayout == null) return;

        // if the timeslot is empty or not a job
        if (currentTimeslot == null
                || currentTimeslot.isEmpty()) {
            if (MyLog.isIsLogEnabled())
                Timeslot.printList(Collections.singletonList(currentTimeslot));
            currentJobLayout.setVisibility(View.GONE);
            currentJobView.setVisibility(View.GONE);
            return;
        }

        currentJobLayout.setVisibility(View.VISIBLE);
        if (currentJobView != null) currentJobView.setVisibility(View.VISIBLE);

        OwnJobViewHolder viewHolder = new OwnJobViewHolder(currentJobLayout);
        viewHolder.bind(getActivity(), currentTimeslot, getPresenter());
        currentJobLayout.requestLayout();
        setRefreshingCurrentJob(false);
        onFinishRefreshingJobs();
    }

    private void setNextJob(Timeslot timeslot) {
        nextTimeslot = timeslot;

        if (nextJobLayout == null) return;

        // if the timeslot is empty or not a job
        if (nextTimeslot == null
                || nextTimeslot.isEmpty()) {
            if (MyLog.isIsLogEnabled()) Timeslot.printList(Collections.singletonList(nextTimeslot));
            nextJobLayout.setVisibility(View.GONE);
            nextJobView.setVisibility(View.GONE);
            return;
        }

        nextJobLayout.setVisibility(View.VISIBLE);
        if (nextJobView != null) nextJobView.setVisibility(View.VISIBLE);

        OwnJobViewHolder viewHolder = new OwnJobViewHolder(nextJobLayout);
        viewHolder.bind(getActivity(), nextTimeslot, getPresenter());
        nextJobLayout.requestLayout();
        setRefreshingNextJob(false);
        onFinishRefreshingJobs();
    }

    public synchronized boolean isRefreshingCurrentJob() {
        return isRefreshingCurrentJob;
    }

    public synchronized void setRefreshingCurrentJob(boolean refreshingCurrentJob) {
        isRefreshingCurrentJob = refreshingCurrentJob;
    }

    public synchronized boolean isRefreshingNextJob() {
        return isRefreshingNextJob;
    }

    public synchronized void setRefreshingNextJob(boolean refreshingNextJob) {
        isRefreshingNextJob = refreshingNextJob;
    }

    private void onFinishRefreshingJobs() {
        if (isRefreshingCurrentJob() || isRefreshingNextJob()) return;

        // TODO: show loading has finished
    }

    @OnClick(R.id.add_availability_button)
    public void onAddAvailabilityClicked() {
        // go to new timeslot activity for new availability
        Intent i = new Intent(getContext(), TimeslotActivity.class);
        i.putExtra("type", Timeslot.TYPE.AVAILABILITY.name());
        i.putExtra("goIntoEditMode", true);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_slide_in, R.anim.expand_out_partial);

        AnalyticsHelper.track(getContext(), "clickedHomeAddAvailability", new Bundle());
    }

    @OnClick(R.id.add_own_job_button)
    public void onAddOwnJobClicked() {
        // go to new timeslot activity for new job
        Intent i = new Intent(getContext(), TimeslotActivity.class);
        i.putExtra("type", Timeslot.TYPE.OWN_JOB.name());
        i.putExtra("goIntoEditMode", true);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_slide_in, R.anim.expand_out_partial);

        AnalyticsHelper.track(getContext(), "clickedHomeAddOwnJob", new Bundle());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Ids.TIMESLOT_CHANGE) {
            MyLog.e(TAG, "[onActivityResult]");
            refresh();
        }
    }
}
