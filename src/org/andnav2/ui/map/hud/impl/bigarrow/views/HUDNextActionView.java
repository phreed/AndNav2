// Created by plusminus on 7:56:23 PM - Feb 23, 2009
package org.andnav2.ui.map.hud.impl.bigarrow.views;

import org.andnav2.R;
import org.andnav2.adt.UnitSystem;
import org.andnav2.ui.map.hud.IHUDNextActionView;
import org.andnav2.util.constants.Constants;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;


public class HUDNextActionView extends LinearLayout implements IHUDNextActionView{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final TextView mTvTurnDistance;
	private final ImageView mIvTurnAngle;
	private UnitSystem mUnitSystem = UnitSystem.METRIC;

	// ===========================================================
	// Constructors
	// ===========================================================

	public HUDNextActionView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.setOrientation(LinearLayout.VERTICAL);

		/* The arrowView. */
		this.mIvTurnAngle = new ImageView(context);
		this.mIvTurnAngle.setScaleType(ScaleType.CENTER_INSIDE);
		final LayoutParams arrowLayoutParams = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		arrowLayoutParams.weight = 1;

		this.addView(this.mIvTurnAngle, arrowLayoutParams);

		/* The distanceView. */
		this.mTvTurnDistance = new TextView(context);
		final LayoutParams distanceLayoutParams = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		this.mTvTurnDistance.setGravity(Gravity.CENTER);
		this.mTvTurnDistance.setTextSize(32);
		this.mTvTurnDistance.setTextColor(Color.BLACK);
		this.addView(this.mTvTurnDistance, distanceLayoutParams);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void setNextActionOnClickListener(final OnClickListener pOnClickListener) {
		this.setOnClickListener(pOnClickListener);
	}

	public void setCurrentMeterSpeed(final float aMeterSpeed) {
		final int currentSpeed = (int)(aMeterSpeed * this.mUnitSystem.mScaleToMetersPerSecond);
		this.mTvTurnDistance.setText(currentSpeed + this.mUnitSystem.mAbbrKilometersPerHourScale);
	}

	public void setTurnAngle(final float angle) {
		this.mIvTurnAngle.setImageResource(getArrowResourceIDFromAngle((int)angle));
	}

	public void showTargetReached() {
		this.mIvTurnAngle.setImageResource(R.drawable.flag_destination_big);
	}

	public void onClick() {
		/* Nothing */
	}

	public void recycle() {
		/* Nothing. */
	}

	public void reset() {
		this.mTvTurnDistance.setText("");
	}

	public void setDisplayQuality(final int displayQuality) {
		/* Nothing as we are using common views. */
	}

	public void setDistance(final int pMeterDistance) {
		if(pMeterDistance != Constants.NOT_SET){
			final String[] values = this.mUnitSystem.getDistanceString(pMeterDistance, null);
			this.mTvTurnDistance.setText(values[UnitSystem.DISTSTRINGS_DIST_ID] + " " + values[UnitSystem.DISTSTRINGS_UNIT_ID]);
		}
	}

	public void setUnitSystem(final UnitSystem pUnitSystem) {
		this.mUnitSystem  = pUnitSystem;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected static final int getArrowResourceIDFromAngle(final int turnAngle) {
		if(turnAngle > 60) {
			return R.drawable.turn_left_90_big;
		} else if(turnAngle > 35) {
			return R.drawable.turn_left_45_big;
		} else if(turnAngle > 15) {
			return R.drawable.turn_left_25_big;
		} else if(turnAngle <= 15 && turnAngle >= -15) {
			return R.drawable.turn_straight_big;
		} else if(turnAngle > -35) {
			return R.drawable.turn_right_25_big;
		} else if(turnAngle > -60) {
			return R.drawable.turn_right_45_big;
		} else {
			return R.drawable.turn_right_90_big;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
