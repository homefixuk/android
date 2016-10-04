package com.homefix.tradesman.recent_services;

import android.app.Activity;
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
import com.homefix.tradesman.base.activity.HomeFixBaseActivity;
import com.homefix.tradesman.base.adapter.MyFirebaseRecyclerAdapter;
import com.homefix.tradesman.base.fragment.BaseFragment;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.homefix.tradesman.home.home_fragment.OwnJobViewHolder;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.timeslot.HomefixServiceHelper;
import com.homefix.tradesman.R;
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

    private RecentJobsAdapter adapter;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (adapter == null) {
            String id = FirebaseUtils.getCurrentTradesmanId();
            if (Strings.isEmpty(id)) {
                // TODO: handle no tradesman
                return;
            }

            Query query = FirebaseUtils.getBaseRef().child("tradesmanTimeslots").child(id).orderByChild("startTime");
            adapter = new RecentJobsAdapter(getBaseActivity(), Object.class, OwnJobViewHolder.class, R.layout.own_job_summary_layout, query);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void refresh() {
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private class RecentJobsAdapter extends MyFirebaseRecyclerAdapter<Object, OwnJobViewHolder> {

        public RecentJobsAdapter(Activity activity, Class<Object> modelClass, Class<OwnJobViewHolder> holderClass, int modelLayout, Query ref) {
            super(activity, modelClass, holderClass, modelLayout, ref);
        }

        @Override
        protected void populateViewHolder(final OwnJobViewHolder holder, Object model, final int position) {
            DatabaseReference ref = getRef(position);
            String key = ref != null ? ref.getKey() : "";

            if (Strings.isEmpty(key)) return;

            FirebaseUtils.getBaseRef().child("timeslots").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Timeslot timeslot = dataSnapshot != null ? dataSnapshot.getValue(Timeslot.class) : null;

                    holder.showTimeUntil = false; // do not show the time until time
                    holder.bind(getBaseActivity(), timeslot, new OwnJobViewHolder.TimeslotClickedListener() {
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
}
