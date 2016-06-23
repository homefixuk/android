package com.samdroid.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samdroid.activity.BaseActivity;

public abstract class BaseFragment extends Fragment {

	protected Activity mActivity;
	
	protected BaseFragment mThisFragment;

	// TAG used for logging
	protected String TAG;

	// resource layout for the fragment
	protected int contentLayoutId;

	// bundle given to the fragment from its managing activity
	protected Bundle mArguments;

	/**
	 * Zero-argument constructor to allow the fragment to be re-created when the Activity is destroyed in the background
	 */
	public BaseFragment () {
	}
	
	/**
	 * Constructor to set the layout for the fragment
	 * 
	 * @param layoutId
	 */
	public BaseFragment (String TAG, int layoutId) {
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
