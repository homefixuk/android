package com.samdroid.adapter;

import java.util.Vector;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter {

	private Vector<View> pages;

	public ViewPagerAdapter (Vector<View> pages) {
		this.pages = pages;
	}

	@Override
	public Object instantiateItem (ViewGroup container, int position) {
		View page = pages.get(position);
		container.addView(page);
		return page;
	}

	@Override
	public int getCount () {
		return pages.size();
	}

	@Override
	public boolean isViewFromObject (View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void destroyItem (ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
	
	/**
     * Add a new view.
     * 
     * @param view
     */
    public void add (View view) {
    	pages.add(view);
    	notifyDataSetChanged();
    }
    
    /**
     * Insert a new view in a position
     * 
     * @param view
     */
    public boolean insert (View view, int position) {
    	if (position < 0) return false;
    	
    	if (position > pages.size() - 1) pages.add(view);
    	else pages.insertElementAt(view, position);
    	
    	notifyDataSetChanged();
    	
    	return true;
    }
    
    /**
     * Remove a view from a position.
     * 
     * @param position
     */
    public View remove (int position) {
    	// return null if the position is out of the index
    	if (position < 0 || position > pages.size() - 1) return null;
    	
    	// else remove and return the view at that position
    	View view = pages.remove(position);
    	notifyDataSetChanged();
    	return view;    			
    }

}