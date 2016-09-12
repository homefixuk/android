package com.homefix.tradesman.home.home_fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
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
    protected LinearLayout currentJobLayout;

    @BindView(R.id.next_job_layout)
    protected LinearLayout nextJobLayout;

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
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HomeFix.getAPI().getCurrentService(UserController.getToken()).enqueue(new Callback<Timeslot>() {
            @Override
            public void onResponse(Call<Timeslot> call, Response<Timeslot> response) {
                if (response == null || response.body() == null) {
                    onFailure(call, new Throwable());
                    return;
                }

                if (currentJobLayout == null) return;

                currentTimeslot = response.body();
                OwnJobViewHolder viewHolder = new OwnJobViewHolder(currentJobLayout);
                viewHolder.bind(getActivity(), currentTimeslot);
            }

            @Override
            public void onFailure(Call<Timeslot> call, Throwable t) {
                // TODO: handle error
            }
        });

        HomeFix.getAPI().getCurrentService(UserController.getToken()).enqueue(new Callback<Timeslot>() {
            @Override
            public void onResponse(Call<Timeslot> call, Response<Timeslot> response) {
                if (response == null || response.body() == null) {
                    onFailure(call, new Throwable());
                    return;
                }

                if (nextJobLayout == null) return;

                nextTimeslot = response.body();
                OwnJobViewHolder viewHolder = new OwnJobViewHolder(nextJobLayout);
                viewHolder.bind(getActivity(), nextTimeslot);
            }

            @Override
            public void onFailure(Call<Timeslot> call, Throwable t) {
                // TODO: handle error
            }
        });
    }

}
