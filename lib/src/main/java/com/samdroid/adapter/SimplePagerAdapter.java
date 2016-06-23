package com.samdroid.adapter;

import java.util.Vector;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SimplePagerAdapter extends PagerAdapter {

	private Vector<ListView> pages;

	public SimplePagerAdapter (Vector<ListView> pages) {
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

}