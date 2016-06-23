package com.samdroid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samdroid.activity.BaseActivity;

public abstract class BaseV4Fragment extends Fragment {

	// TAG used for logging
	protected String TAG;

	protected Activity mActivity;

	protected BaseV4Fragment mThisFragment;

	// resource layout for the fragment
	protected int contentLayoutId;

	// bundle given to the fragment from its managing activity
	protected Bundle mArguments;

	/**
	 * Constructor to set the layout for the fragment
	 * 
	 * @param layoutId
	 */
	public BaseV4Fragment (String TAG, int layoutId) {
		this.TAG = TAG;
		this.contentLayoutId = layoutId;
	}

	@Override
	public void onAttach (Activity activity) {
		super.onAttach(activity);

		mActivity = activity;
		mThisFragment = this;
	}

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(contentLayoutId, container, false);
	}

	/**
	 * @return if the fragment is empty
	 */
	public boolean isEmpty () {
		return false;
	}

	public boolean isActivityClosing () {
		try { return ((BaseActivity) mActivity).isActivityClosing(); }
		catch (Exception e) {}

		return false;
	}

}
