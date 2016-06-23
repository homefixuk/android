package com.samdroid.adapter;

import java.util.Vector;

import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.samdroid.fragment.BaseFragment;

public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {
    
	private Vector<BaseFragment> mFragments;

	public BaseFragmentPagerAdapter (FragmentManager fragmentManager) {
        super(fragmentManager);
        mFragments = new Vector<BaseFragment>();
    }
	
	public BaseFragmentPagerAdapter (FragmentManager fragmentManager, Vector<BaseFragment> fragments) {
        super(fragmentManager);
        mFragments = fragments;
    }

    @Override
    public BaseFragment getItem (int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount () {
        return mFragments.size();
    }
    
    /**
     * Add a new fragment.
     * 
     * @param fragment
     */
    public void add (BaseFragment fragment) {
    	mFragments.add(fragment);
    	notifyDataSetChanged();
    }
    
    /**
     * Insert a new fragment in a position
     * 
     * @param fragment
     */
    public boolean insert (BaseFragment fragment, int position) {
    	if (position < 0) return false;
    	
    	if (position > mFragments.size() - 1) mFragments.add(fragment);
    	else mFragments.insertElementAt(fragment, position);
    	
    	notifyDataSetChanged();
    	
    	return true;
    }
    
    /**
     * Remove a fragment from a position.
     * 
     * @param position
     */
    public BaseFragment remove (int position) {
    	// return null if the position is out of the index
    	if (position < 0 || position > mFragments.size() - 1) return null;
    	
    	// else remove and return the fragment at that position
    	BaseFragment fragment = mFragments.remove(position);
    	notifyDataSetChanged();
    	return fragment;    			
    }
}