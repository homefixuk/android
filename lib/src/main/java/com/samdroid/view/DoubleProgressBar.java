package com.samdroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class DoubleProgressBar extends ProgressBar {

	boolean defaultMax;

	// allows increments of 0.01
	protected static final double multiplier = 100.0;

	public DoubleProgressBar (Context context) {
		super(context);

		init();
	}

	public DoubleProgressBar (Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public DoubleProgressBar (Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		init();
	}

	public synchronized void setMax (double max) {
		super.setMax(0); // clear the old max
		
		int maxInt = (int) (max*multiplier);

		super.setMax(maxInt);

		// set to be the default max if it is 100 times the multiplier
		defaultMax = (maxInt == 100*multiplier);		
	}

	public synchronized void setSecondaryProgress (double secondaryProgress) {
		int progress = getProgress();

		// clear the current progresses
		super.setProgress(0);
		super.setSecondaryProgress(0);
		super.setMax(getMax());

		super.setProgress(progress); // reset the main progress
		
		int secondaryProgressInt = (int) (secondaryProgress*multiplier);

		super.setSecondaryProgress(secondaryProgressInt);
	}

	public synchronized void setProgress (double progress) {
		// clear the current progresses
		super.setProgress(0);
		super.setSecondaryProgress(0);
		super.setMax(getMax());
		
		int progressInt = (int) (progress*multiplier);

		super.setProgress(progressInt);
	}

	/**
	 * Setup the progress bar
	 */
	private void init () {
		// set it to be not intermediate
		setIndeterminate(false);

		// set the max to 100 times the multiplier
		setMax(100.0);
	}

	/**
	 * Set the main progress from a percent 0-100.
	 * 
	 * @param percent 0-100
	 */
	public void setPercentageProgress (int percent) {
		// clear the current progresses
		super.setProgress(0);
		super.setSecondaryProgress(0);
		super.setMax(getMax());
		
		// if the default is still set
		if (defaultMax) {
			// set the progress straight up
			setProgress(percent);
			return;
		}

		// else calculate the percentage from the current max
		this.setProgress(((percent/100.0) * 1.0*getMax()));
	}

	/**
	 * Set the secondary progress from a percent 0-100.
	 * 
	 * @param percent 0-100
	 */
	public void setSecondaryPercentageProgress (int percent) {
		int progress = getProgress();

		// clear the current progresses
		super.setProgress(0);
		super.setSecondaryProgress(0);
		super.setMax(super.getMax());

		setProgress(progress); // reset the main progress
		
		// if the default is still set
		if (defaultMax) {
			// set the progress straight up
			setSecondaryProgress(percent);
			return;
		}

		// else calculate the percentage from the current max
		setSecondaryProgress((percent/100.0) * 1.0*getMax());
	}

}
