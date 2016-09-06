package com.homefix.tradesman.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.presenter.BaseToolbarActivityPresenter;
import com.homefix.tradesman.base.view.BaseToolbarActivityView;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by samuel on 9/1/2016.
 */

public class EditListActivity extends BaseToolbarActivity<BaseToolbarActivityView, BaseToolbarActivityPresenter<BaseToolbarActivityView>> {

    protected ListView mListView;

    protected ArrayAdapter<String> mAdapter;

    protected List<String> originalList;

    public EditListActivity() {
        super(EditListActivity.class.getSimpleName());
    }

    @Override
    public BaseToolbarActivityPresenter getPresenter() {
        if (presenter == null) {
            presenter = new BaseToolbarActivityPresenter<>();
            presenter.attachView(this);
        }

        return presenter;
    }

    @Override
    protected BaseToolbarActivityView getThisView() {
        return this;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_content_fragment_with_app_bar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            // return from the activity sending the items in the adapter
            Intent i = new Intent();
            i.putStringArrayListExtra("list", getItemsInAdapter());
            finishWithIntent(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate the activity into the content frame
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_edit_list, mContentView, true);

        // get the list view from the list
        mListView = ButterKnife.findById(view, R.id.list);

        // set the on click listener for the add button
        View addButton = ButterKnife.findById(view, R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddClicked();
            }
        });

        setActionbarTitle("Update the list");

        Intent i = getIntent();
        originalList = i != null ? i.getStringArrayListExtra("list") : new ArrayList<String>();
        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, originalList) {

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                final String s = Strings.returnSafely(getItem(position));

                TextView text1 = ButterKnife.findById(view, android.R.id.text1);
                text1.setText(s);
                text1.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                // add click listener to allow the current items to be removed
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showConfirmDialog(
                                "Would you like to remove: " + s + "?",
                                "REMOVE",
                                "CANCEL",
                                new ConfirmDialogCallback() {
                                    @Override
                                    public void onPositive() {
                                        mAdapter.remove(s);
                                    }
                                }
                        );
                    }
                });

                return view;
            }

            @Override
            public void add(String object) {
                if (Strings.isEmpty(object)) return;

                // do not add duplicates
                for (int i = 0, len = mAdapter.getCount(); i < len; i++) {
                    if (object.equals(mAdapter.getItem(i))) return;
                }

                super.add(object);
            }

        };
        mListView.setAdapter(mAdapter);
    }

    public ArrayList<String> getItemsInAdapter() {
        if (mAdapter == null || mAdapter.getCount() == 0) return new ArrayList<>();

        ArrayList<String> list = new ArrayList<>();
        String s;
        for (int i = 0, len = mAdapter.getCount(); i < len; i++) {
            s = mAdapter.getItem(i);
            if (Strings.isEmpty(s)) continue;
            list.add(s);
        }

        return list;
    }

    public void onAddClicked() {
        MaterialDialogWrapper.getEditTextDialog(
                this,
                "",
                "New item",
                "ADD",
                new MaterialDialogWrapper.SubmitObjectChangesCallback() {

                    @Override
                    public void onChangeSubmitted(Object original, Object changed) {
                        if (changed == null) return;

                        String s = String.valueOf(changed);
                        if (Strings.isEmpty(s)) return;

                        mAdapter.add(s);
                    }

                    @Override
                    public void onChangeCancelled(Object original) {
                    }
                }

        ).show();
    }

}
