// Created by plusminus on 22:57:59 - 02.02.2009
package org.andnav2.adt;

import org.andnav2.osm.adt.GeoPoint;

import android.location.LocationManager;


public class AndNavLocation extends GeoPoint {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final String PROVIDER_GPS = LocationManager.GPS_PROVIDER;
	public static final String PROVIDER_NETWORK = LocationManager.NETWORK_PROVIDER;

	// ===========================================================
	// Fields
	// ===========================================================

	private final int mHorizontalPositioningError;
	private final int mVerticalPositioningError;
	private final int mNumberOfLandmarks;
	private float mBearing;
	private final int mAltitude;
	private final long mTimeStamp;
	private float mSpeed;
	private final String mProvider;

	// ===========================================================
	// Constructors
	// ===========================================================

	public AndNavLocation(final String pProvider, final int pLatitudeE6, final int pLongitudeE6, final int pHorizontalPositioningError, final int pVerticalPositioningError, final int pNumberOfLandmarks, final float pBearing, final int pAltitude, final long pTimeStamp, final float pSpeed) {
		super(pLatitudeE6, pLongitudeE6);
		this.mHorizontalPositioningError = pHorizontalPositioningError;
		this.mVerticalPositioningError = pVerticalPositioningError;
		this.mNumberOfLandmarks = pNumberOfLandmarks;
		this.mBearing = (pBearing != 0.0f) ? pBearing : NOT_SET; // LocationManager will do the thing
		this.mAltitude = pAltitude;
		this.mTimeStamp = pTimeStamp;
		this.mSpeed = pSpeed;
		this.mProvider = pProvider;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean hasHorizontalPositioningError() {
		return this.mHorizontalPositioningError != NOT_SET;
	}

	public boolean hasVerticalPositioningError() {
		return this.mVerticalPositioningError != NOT_SET;
	}

	public boolean hasNumberOfLandmarks() {
		return this.mNumberOfLandmarks != NOT_SET;
	}

	public boolean hasBearing() {
		return this.mBearing != NOT_SET;
	}

	public boolean hasAltitude() {
		return this.mAltitude != NOT_SET;
	}

	public boolean hasSpeed() {
		return this.mSpeed != NOT_SET;
	}


	public int getHorizontalPositioningError() {
		return this.mHorizontalPositioningError;
	}

	public int getVerticalPositioningError() {
		return this.mVerticalPositioningError;
	}

	public int getNumberOfLandmarks() {
		return this.mNumberOfLandmarks;
	}

	/** TODO CHeck description
	 * Provides the calculated bearing (between the last two Locations supplied)
	 * and returns the Angle in the following (GPS-likely) manner: <br />
	 * <code>N:0°, E:90°, S:180°, W:270°</code>
	 */
	public float getBearing() {
		return this.mBearing;
	}

	public int getAltitude() {
		return this.mAltitude;
	}

	public long getTimeStamp() {
		return this.mTimeStamp;
	}

	public float getSpeed() {
		return this.mSpeed;
	}

	public String getProvider() {
		return this.mProvider;
	}

	public void setBearing(final float pBearing) {
		this.mBearing = pBearing;
	}

	public void setSpeed(final float pSpeed) {
		this.mSpeed = pSpeed;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
