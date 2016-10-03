package com.homefix.tradesman.recent_services;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.homefix.tradesman.R;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.base.activity.HomeFixBaseActivity;
import com.homefix.tradesman.base.fragment.BaseFragment;
import com.homefix.tradesman.data.TradesmanController;
import com.homefix.tradesman.home.home_fragment.OwnJobViewHolder;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.timeslot.HomefixServiceHelper;
import com.samdroid.network.NetworkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by samuel on 9/16/2016.
 */

public class RecentServicesFragment<A extends HomeFixBaseActivity>
        extends BaseFragment<A, RecentServicesView, RecentServicesPresenter>
        implements RecentServicesView {

    @BindView(R.id.list)
    protected ListView listView;

    private ArrayAdapter<Timeslot> adapter;

    private boolean isLoading = false;

    private long lastStartTime = 0;

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
        return R.layout.fragment_list_view_loading;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.base_padding));

        listView.setOnScrollListener(new InfiniteScrollListener(5) {

            @Override
            public void loadMore(int page, int totalItemsCount) {
                getData();
            }

        });

        if (adapter != null) listView.setAdapter(adapter);

        getData();
    }

    private void getData() {
        if (isLoading) return;

        isLoading = true;

        if (NetworkManager.hasConnection(getActivity())) {

            HashMap<String, Object> params = new HashMap<>();
            params.put("startTime", getLastStartTime());
            params.put("limit", 10);
            params.put("type", Timeslot.TYPE.OWN_JOB.getName());

            HomeFix.getAPI().getTradesmanEvents(TradesmanController.getToken(), params).enqueue(new Callback<List<Timeslot>>() {
                @Override
                public void onResponse(Call<List<Timeslot>> call, Response<List<Timeslot>> response) {
                    List<Timeslot> list = response != null ? response.body() : null;
                    if (list == null) list = new ArrayList<>();

                    if (adapter == null) {
                        adapter = new ArrayAdapter<Timeslot>(getActivity(), R.layout.own_job_summary_layout) {

                            @NonNull
                            @Override
                            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                                View view = convertView;
                                OwnJobViewHolder holder;

                                if (view == null) {
                                    view = getActivity().getLayoutInflater().inflate(R.layout.own_job_summary_layout, parent, false);
                                    holder = new OwnJobViewHolder(view);
                                    view.setTag(holder);

                                } else {
                                    holder = (OwnJobViewHolder) view.getTag();
                                }

                                holder.showTimeUntil = false; // do not show the time until time

                                holder.bind(getBaseActivity(), getItem(position), new OwnJobViewHolder.TimeslotClickedListener() {
                                    @Override
                                    public void onTimeslotClicked(Timeslot timeslot, boolean longClick) {
                                        HomefixServiceHelper.goToTimeslot(getBaseActivity(), timeslot, longClick);
                                    }
                                });
                                view.requestLayout();

                                return view;
                            }

                            @Override
                            public void add(Timeslot object) {
                                if (object == null || object.isEmpty() || !object.getType().equals("own_job"))
                                    return;

                                // make sure not to add duplicates
                                String id = object.getId();
                                for (int i = 0, len = getCount(); i < len; i++) {
                                    if (id.equals(getItem(i).getId())) return;
                                }

//                                // update the last start time
//                                if (object.getStart() > lastStartTime) {
//                                    lastStartTime = object.getEnd();
//                                }

                                super.add(object);
                            }

                            @Override
                            public void addAll(Timeslot... items) {
                                for (int i = 0; i < items.length; i++) add(items[i]);
                                notifyDataSetChanged();
                            }

                        };
                        listView.setAdapter(adapter);
                    }

                    Timeslot[] timeslots = new Timeslot[list.size()];
                    for (int i = 0; i < list.size(); i++) timeslots[i] = list.get(i);
                    adapter.addAll(timeslots);

                    isLoading = false;
                }

                @Override
                public void onFailure(Call<List<Timeslot>> call, Throwable t) {
                    isLoading = false;
                }
            });

        } else {
            Toast.makeText(getActivity(), "No network connection", Toast.LENGTH_SHORT).show();
        }
    }

    public long getLastStartTime() {
        if (adapter == null || adapter.isEmpty()) return 0;

        long time = 0;
        Timeslot timeslot;
        for (int i = 0, len = adapter.getCount(); i < len; i++) {
            timeslot = adapter.getItem(i);
            if (timeslot == null || timeslot.getStart() == 0) continue;

            if (timeslot.getStart() > time) time = timeslot.getStart();
        }

        return time;
    }

    @Override
    public void refresh() {
        if (adapter != null) adapter.notifyDataSetChanged();
    }
}
