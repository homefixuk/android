package com.homefix.tradesman.recent_services;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.HomeFixBaseActivity;
import com.homefix.tradesman.base.adapter.MyFirebaseRecyclerAdapter;
import com.homefix.tradesman.base.fragment.BaseFragment;
import com.homefix.tradesman.common.AnalyticsHelper;
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

    private MyFirebaseRecyclerAdapter<Object, OwnJobViewHolder> adapter;

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

        Query query = FirebaseUtils
                .getBaseRef()
                .child("tradesmanServiceTimeslots")
                .child(tradesmanId)
                .orderByChild("reverseStartTime")
                .startAt(-1L * System.currentTimeMillis()); // only show jobs before right now
        adapter = new MyFirebaseRecyclerAdapter<Object, OwnJobViewHolder>(
                getBaseActivity(),
                Object.class,
                OwnJobViewHolder.class,
                R.layout.own_job_summary_layout,
                query) {

            @Override
            protected void populateViewHolder(final OwnJobViewHolder viewHolder, final Object model, int position) {
                viewHolder.showTimeUntil = false; // do not show the time until time

                // get the timeslot key
                DatabaseReference ref = getRef(position);
                if (ref == null) return;

                String timeslotKey = ref.getKey();
                DatabaseReference timeslotRef = FirebaseUtils.getSpecificTimeslotRef(timeslotKey);
                if (timeslotRef != null) {
                    timeslotRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot == null || !dataSnapshot.exists()) return;

                            Timeslot model = dataSnapshot.getValue(Timeslot.class);
                            viewHolder.bind(getBaseActivity(), model, new OwnJobViewHolder.TimeslotClickedListener() {
                                @Override
                                public void onTimeslotClicked(Timeslot timeslot, boolean longClick) {
                                    HomefixServiceHelper.goToTimeslot(getBaseActivity(), timeslot, longClick);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void refresh() {
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsHelper.track(getContext(), "openRecentJobs", new Bundle());
    }
}
