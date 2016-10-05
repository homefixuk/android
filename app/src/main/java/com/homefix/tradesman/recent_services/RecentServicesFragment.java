package com.homefix.tradesman.recent_services;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.Query;
import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.HomeFixBaseActivity;
import com.homefix.tradesman.base.adapter.MyFirebaseRecyclerAdapter;
import com.homefix.tradesman.base.fragment.BaseFragment;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.home.home_fragment.OwnJobViewHolder;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.timeslot.HomefixServiceHelper;
import com.samdroid.string.Strings;

import butterknife.BindView;

/**
 * Created by samuel on 9/16/2016.
 */

public class RecentServicesFragment<A extends HomeFixBaseActivity>
        extends BaseFragment<A, RecentServicesView, RecentServicesPresenter>
        implements RecentServicesView {

    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;

    private MyFirebaseRecyclerAdapter<Timeslot, OwnJobViewHolder> adapter;

    public RecentServicesFragment() {
        super(RecentServicesFragment.class.getSimpleName());
    }

    @Override
    protected RecentServicesPresenter getPresenter() {
        if (presenter == null) presenter = new RecentServicesPresenter(this);

        presenter.attachView(this);
        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_recycler_view_loading;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String tradesmanId = FirebaseUtils.getCurrentTradesmanId();
        if (Strings.isEmpty(tradesmanId)) {
            // TODO: handle no tradesman
            return;
        }

        Query query = FirebaseUtils.getBaseRef().child("tradesmanTimeslots").child(tradesmanId).orderByChild("reverseStartTime");
        adapter = new MyFirebaseRecyclerAdapter<Timeslot, OwnJobViewHolder>(
                getBaseActivity(),
                Timeslot.class,
                OwnJobViewHolder.class,
                R.layout.own_job_summary_layout,
                query) {

            @Override
            protected void populateViewHolder(OwnJobViewHolder viewHolder, Timeslot model, int position) {
                //            DatabaseReference ref = getRef(position);
//            String key = ref != null ? ref.getKey() : "";
//
//            if (Strings.isEmpty(key)) return;

                viewHolder.showTimeUntil = false; // do not show the time until time
                viewHolder.bind(getBaseActivity(), model, new OwnJobViewHolder.TimeslotClickedListener() {
                    @Override
                    public void onTimeslotClicked(Timeslot timeslot, boolean longClick) {
                        HomefixServiceHelper.goToTimeslot(getBaseActivity(), timeslot, longClick);
                    }
                });
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void refresh() {
    }

}
