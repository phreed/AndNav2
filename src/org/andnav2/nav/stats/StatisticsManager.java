// Created by plusminus on 13:59:37 - 29.06.2008
package org.andnav2.nav.stats;

import org.andnav2.adt.AndNavLocation;
import org.andnav2.adt.UnitSystem;
import org.andnav2.preferences.Preferences;
import org.andnav2.util.constants.Constants;

import android.app.Activity;
import android.util.Log;


public class StatisticsManager {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected long mMetersDrivenSession;
	protected long mInitialSeconds;
	protected UnitSystem mUnitSystem;
	protected Activity mContext;
	protected AndNavLocation mCurrentLocation;
	protected long mLastTick;

	/** In Meters per seconds. */
	protected int mMaxSpeedMPS;
	protected float mCurrentSpeed;

	// ===========================================================
	// Constructors
	// ===========================================================

	public StatisticsManager(final Activity pContext,final UnitSystem pUnitSystem, final long pInitialMilliseconds){
		this.mContext = pContext;
		this.mInitialSeconds = pInitialMilliseconds;
		this.mUnitSystem = pUnitSystem;
		Preferences.startStatisticsSession(this.mContext);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * Current speed in Meters per second.
	 */
	public float getCurrentSpeed(){
		return this.mCurrentSpeed;
	}

	public long getMetersScaleDrivenSession(){
		return this.mMetersDrivenSession;
	}

	/**
	 * In Meters per second.
	 * @return
	 */
	public float getAverageSpeedSession(){
		return this.mMetersDrivenSession / ((System.currentTimeMillis() - this.mInitialSeconds) / 1000.0f);
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void tick(final AndNavLocation pNewLocation){
		final long now = System.currentTimeMillis();
		if(this.mCurrentLocation != null){
			final float dist = pNewLocation.distanceTo(this.mCurrentLocation);
			final long timeDeltaMs = now - this.mLastTick;
			if(dist > 20 && timeDeltaMs > 2000){
				this.mMetersDrivenSession += (int)dist;
				final int curSpeed;
				//				if(pNewLocation.hasSpeed()){
				//					this.mCurrentSpeed = pNewLocation.getSpeed();
				//					curSpeed = (int)this.mCurrentSpeed;
				//				}else{
				this.mCurrentSpeed = (int)(dist / (timeDeltaMs  / 1000.0f));
				curSpeed = (int)this.mCurrentSpeed;
				//				}
				//				Log.d(Constants.DEBUGTAG, "Speed: " + curSpeed + " mps  [Dist: "+ dist + "m]  [Time: " + timeDeltaMs + "]");

				this.mMaxSpeedMPS = Math.max(this.mMaxSpeedMPS, curSpeed);
				this.mLastTick = now;
				this.mCurrentLocation = pNewLocation;
			}
		}else{
			this.mLastTick = now;
			this.mCurrentLocation = pNewLocation;
		}
	}

	/**
	 * Writes the data collected through to the Database.
	 * This method should be called before the NavMap gets closed.
	 */
	public void writeThrough(){
		Log.d(Constants.DEBUGTAG, "Adding SessionMeters to Preferences: " + this.mMetersDrivenSession);
		Log.d(Constants.DEBUGTAG, "Updating Session MAXSpeed to Preferences: " + this.mMaxSpeedMPS);
		Preferences.updateSessionEndTimeStamp(this.mContext);
		Preferences.addStatisticsMetersDrivenSession(this.mContext, this.mMetersDrivenSession);
		this.mMetersDrivenSession = 0;
		Preferences.updateStatisticsMaxSpeedSession(this.mContext, this.mMaxSpeedMPS);
	}

	public void finish(){
		writeThrough();
		Preferences.endStatisticsSession(this.mContext);
		Log.d(Constants.DEBUGTAG, "STATISTICS END");
		Log.d(Constants.DEBUGTAG, "Overall Meters: " + Preferences.getStatisticsMetersDrivenOverall(this.mContext));
		Log.d(Constants.DEBUGTAG, "Max Speed: " + Preferences.getStatisticsMaxSpeedBeforeSession(this.mContext));
	}

	/**
	 * Sets InitialSeconds to <code>System.currentTimeMillis();</code><br />
	 * and metersDriven to <code>0</code>.
	 */
	public void reset(){
		this.mInitialSeconds = System.currentTimeMillis();
		this.mMetersDrivenSession = 0;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
