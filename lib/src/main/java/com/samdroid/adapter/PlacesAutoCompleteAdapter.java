package com.samdroid.adapter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.samdroid.R;
import com.samdroid.common.MyLog;

/**
 * https://developers.google.com/places/training/autocomplete-android
 * 
 * @author Sam Koch
 */
public abstract class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

	private static final String TAG = "PlacesAutoCompleteAdapter";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";

	/**
	 * Resource string for your own GoogleAPI key must be given
	 */
	private int googleApiResId;
	
	private ArrayList<String> resultList;

	PlacesListener callback;
	public interface PlacesListener {

		/**
		 * Called when a location from the auto complete list is selected.
		 * 
		 * @param location
		 */
		public void onLocationClicked(String location);
	}

	public PlacesAutoCompleteAdapter (Context context, int textViewResourceId, int googleApiResId) {
		super(context, textViewResourceId);
		this.googleApiResId = googleApiResId;
	}

	public PlacesAutoCompleteAdapter (Activity activity, Context context, int textViewResourceId, int googleApiResId) {
		super(context, textViewResourceId);
		this.googleApiResId = googleApiResId;

		// cast the activity so the callback function can be called
		this.callback = (PlacesListener) activity;
	}

	public PlacesAutoCompleteAdapter (Fragment fragment, Context context, int textViewResourceId, int googleApiResId) {
		super(context, textViewResourceId);
		this.googleApiResId = googleApiResId;

		// cast the activity so the callback function can be called
		this.callback = (PlacesListener) fragment;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);

		// if the convertView is not null
		if (convertView != null) {
			// if there is a callback
			if (callback != null) {
				// give the item an on click listener to use the callback
				convertView.setOnClickListener(new PlacesOnClickListener(position));

				// give it an on touch listener too
				convertView.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch (View v, MotionEvent event) {
						switch (event.getAction()) {

						case MotionEvent.ACTION_DOWN:
							// set its background to light grey
							v.setBackgroundColor(getContext().getResources().getColor(R.color.light_grey));
							break;

						case MotionEvent.ACTION_UP:
							// set its background to transparent
							v.setBackgroundColor(getContext().getResources().getColor(R.color.white));

							// call the on click listener
							v.performClick();
							break;

						case MotionEvent.ACTION_CANCEL:
						case MotionEvent.ACTION_OUTSIDE:
							// set its background to transparent
							v.setBackgroundColor(getContext().getResources().getColor(R.color.white));


							break;	
						}

						return true;
					}
				});				
			}			
		}

		return convertView;
	}	

	@Override
	public int getCount () {
		return resultList != null ? resultList.size() : 0;
	}

	@Override
	public String getItem (int index) {
		return resultList != null ? resultList.get(index) : "";
	}

	private ArrayList<String> autocomplete (String input) {
		ArrayList<String> resultList = null;

		String API_KEY = getContext().getResources().getString(googleApiResId);

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + API_KEY);
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

//	        sb.append("&components=country:us");

			// just return cities
//			sb.append("&types=(cities)");

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());
			
			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
			
		} catch (MalformedURLException e) {
			MyLog.e(TAG, "Error processing Places API URL: " + e.getLocalizedMessage());
			return resultList;

		} catch (IOException e) {
			MyLog.e(TAG, "Error connecting to Places API: " + e.getLocalizedMessage());
			return resultList;

		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
			}

		} catch (JSONException e) {
			MyLog.e(TAG, "Cannot process JSON results: " + e.getLocalizedMessage());
		}

		return resultList;
	}

	@Override
	public Filter getFilter () {
		Filter filter = new Filter() {
			
			@Override
			protected FilterResults performFiltering (CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					// Retrieve the auto complete results.
					resultList = autocomplete(constraint.toString());

					// Assign the data to the FilterResults
					filterResults.values = resultList;
					filterResults.count = resultList.size();
				}
				
				return filterResults;
			}

			@Override
			protected void publishResults (CharSequence constraint, final FilterResults results) {
				((Activity) getContext()).runOnUiThread(new Runnable() {
					
					@Override
					public void run () {
						if (results != null && results.count > 0) {
							notifyDataSetChanged();
						}
						else {
							notifyDataSetInvalidated();
						}
					}
				});
				
			}};
			return filter;
	}

	private class PlacesOnClickListener implements OnClickListener {

		int index;

		public PlacesOnClickListener (int index) {
			this.index = index;
		}

		@Override
		public void onClick (View v) {
			if (callback == null) return;

			if (resultList == null || resultList.size() < 0) return;

			if (index < 0 || index >= resultList.size()) return;

			// else call the callback function
			callback.onLocationClicked(resultList.get(index));
		}

	}
}
