package com.homefix.tradesman.home.home_fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.homefix.tradesman.R;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.base.activity.HomeFixBaseActivity;
import com.homefix.tradesman.base.fragment.BaseFragment;
import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.model.Timeslot;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    }

    @Override
    public void refresh() {
        setCurrentJob(currentTimeslot);
        setNextJob(nextTimeslot);

        // if we need to refresh the jobs
        HomeFix.getAPI().getCurrentService(UserController.getToken()).enqueue(new Callback<Timeslot>() {
            @Override
            public void onResponse(Call<Timeslot> call, Response<Timeslot> response) {
                if (response == null) {
                    onFailure(call, new Throwable());
                    return;
                }

                setCurrentJob(response.body());
            }

            @Override
            public void onFailure(Call<Timeslot> call, Throwable t) {
                // TODO: handle error
            }
        });

        HomeFix.getAPI().getCurrentService(UserController.getToken()).enqueue(new Callback<Timeslot>() {
            @Override
            public void onResponse(Call<Timeslot> call, Response<Timeslot> response) {
                if (response == null) {
                    onFailure(call, new Throwable());
                    return;
                }

                setNextJob(response.body());
            }

            @Override
            public void onFailure(Call<Timeslot> call, Throwable t) {
                // TODO: handle error
            }
        });
    }

    private void setCurrentJob(Timeslot timeslot) {
        currentTimeslot = timeslot;

        if (currentJobLayout == null) return;

        if (currentTimeslot == null) {
            currentJobLayout.setVisibility(View.GONE);
            currentJobView.setVisibility(View.GONE);
            return;
        }

        currentJobLayout.setVisibility(View.VISIBLE);
        if (currentJobView != null) currentJobView.setVisibility(View.VISIBLE);

        OwnJobViewHolder viewHolder = new OwnJobViewHolder(currentJobLayout);
        viewHolder.bind(getActivity(), currentTimeslot, getPresenter());
        currentJobLayout.requestLayout();
    }

    private void setNextJob(Timeslot timeslot) {
        nextTimeslot = timeslot;

        if (nextJobLayout == null) return;

        if (nextTimeslot == null) {
            nextJobLayout.setVisibility(View.GONE);
            nextJobView.setVisibility(View.GONE);
            return;
        }

        nextJobLayout.setVisibility(View.VISIBLE);
        if (nextJobView != null) nextJobView.setVisibility(View.VISIBLE);

        OwnJobViewHolder viewHolder = new OwnJobViewHolder(nextJobLayout);
        viewHolder.bind(getActivity(), nextTimeslot, getPresenter());
        nextJobLayout.requestLayout();
    }

}
