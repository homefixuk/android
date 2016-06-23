package com.samdroid.adapter;

import java.util.Vector;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
    
	private Vector<Fragment> mFragments;
	
	public FragmentPagerAdapter (FragmentManager fragmentManager) {
        super(fragmentManager);
        mFragments = new Vector<>();
    }
	
	public FragmentPagerAdapter (FragmentManager fragmentManager, Vector<Fragment> fragments) {
        super(fragmentManager);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem (int position) {
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
    public void add (Fragment fragment) {
    	mFragments.add(fragment);
    	notifyDataSetChanged();
    }
    
    /**
     * Insert a new fragment in a position
     * 
     * @param fragment
     */
    public boolean insert (Fragment fragment, int position) {
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
    public Fragment remove (int position) {
    	// return null if the position is out of the index
    	if (position < 0 || position > mFragments.size() - 1) return null;
    	
    	// else remove and return the fragment at that position
    	Fragment fragment = mFragments.remove(position);
    	notifyDataSetChanged();
    	return fragment;    			
    }
}