package com.homefix.tradesman.recent_services;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.homefix.tradesman.R;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.base.activity.HomeFixBaseActivity;
import com.homefix.tradesman.base.fragment.BaseFragment;
import com.homefix.tradesman.data.TradesmanController;
import com.homefix.tradesman.model.Service;
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

    private ArrayAdapter<Service> adapter;

    private boolean isLoading = false;

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

        listView.setOnScrollListener(new InfiniteScrollListener(5) {

            @Override
            public void loadMore(int page, int totalItemsCount) {
                getData();
            }

        });

        getData();
    }

    private void getData() {
        if (isLoading) return;

        isLoading = true;

        if (NetworkManager.hasConnection(getActivity())) {

            HashMap<String, Object> params = new HashMap<>();
            params.put("skip", adapter != null ? adapter.getCount() : 0);

            HomeFix.getAPI().getServices(TradesmanController.getToken(), params).enqueue(new Callback<List<Service>>() {
                @Override
                public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                    List<Service> list = response != null ? response.body() : null;
                    if (list == null) list = new ArrayList<>();

                    if (adapter == null) {
                        adapter = new ArrayAdapter<Service>(getActivity(), android.R.layout.simple_list_item_1, list) {

                            @NonNull
                            @Override
                            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);

                                // TODO: setup view
                                TextView tv = (TextView) view;
                                tv.setText(getItem(position).getId());

                                return view;
                            }
                        };
                        listView.setAdapter(adapter);

                    } else {
                        adapter.addAll(list);
                    }

                    isLoading = false;
                }

                @Override
                public void onFailure(Call<List<Service>> call, Throwable t) {
                    isLoading = false;
                }
            });

        } else {
            Toast.makeText(getActivity(), "No network connection", Toast.LENGTH_SHORT).show();
        }
    }

}
