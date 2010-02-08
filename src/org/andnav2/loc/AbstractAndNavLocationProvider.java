package org.andnav2.loc;

import org.andnav2.adt.AndNavLocation;
import org.andnav2.nav.util.Util;


/**
 * 
 * @author Nicolas Gramlich
 * @since 00:24:28 - 19.07.2009
 */
public abstract class AbstractAndNavLocationProvider {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final AndNavLocationCallback mLocationCallback;
	private AndNavLocation mLastKnownLocation;

	// ===========================================================
	// Constructors
	// ===========================================================

	public AbstractAndNavLocationProvider(final AndNavLocationCallback pCallback){
		this.mLocationCallback = pCallback;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean hasLastKnownLocation(){
		return this.mLastKnownLocation != null;
	}

	public boolean hasBearing(){
		return this.hasLastKnownLocation() && this.mLastKnownLocation.hasBearing();
	}

	public boolean hasSpeed(){
		return this.hasLastKnownLocation() && this.mLastKnownLocation.hasSpeed();
	}

	public boolean hasNumberOfLandmarks() {
		return this.hasLastKnownLocation() && this.mLastKnownLocation.hasNumberOfLandmarks();
	}

	public boolean hasHorizontalPositioningError() {
		return this.hasLastKnownLocation() && this.mLastKnownLocation.hasHorizontalPositioningError();
	}

	public AndNavLocation getLastKnownLocation(){
		return this.mLastKnownLocation;
	}

	public float getBearing(){
		return this.mLastKnownLocation.getBearing();
	}

	public float getSpeed(){
		return this.mLastKnownLocation.getSpeed();
	}

	public int getHorizontalPositioningError() {
		return this.mLastKnownLocation.getHorizontalPositioningError();
	}

	public int getNumberOfLandmarks() {
		return this.mLastKnownLocation.getNumberOfLandmarks();
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public abstract void onCreate();
	public abstract void onResume();
	public abstract void onStop();
	public abstract void onDestroy();

	protected void onPublishLocation(final AndNavLocation pLocation){
		if(pLocation == null) {
			return;
		}

		/* TODO Extract some info. */
		if(!pLocation.hasBearing()){
			/* Calculate the bearing. */
			if (this.mLastKnownLocation != null) {
				/* ... if location really changed. */
				if (!this.mLastKnownLocation.equals(pLocation)) {
					pLocation.setBearing(Util.calculateBearing(this.mLastKnownLocation, pLocation));
				}
			}
		}

		if(!pLocation.hasSpeed()){
			if (this.mLastKnownLocation != null) {
				final float dist = pLocation.distanceTo(this.mLastKnownLocation);
				final long timeDeltaMs = pLocation.getTimeStamp() - this.mLastKnownLocation.getTimeStamp();
				final float calculatedSpeed = (dist / (timeDeltaMs  / 1000.0f));

				final float interpolatedSpeed;
				if(this.mLastKnownLocation.hasSpeed()) {
					interpolatedSpeed = (calculatedSpeed + this.mLastKnownLocation.getSpeed()) / 2;
				} else {
					interpolatedSpeed = calculatedSpeed;
				}

				pLocation.setSpeed(interpolatedSpeed);
			}
		}

		this.mLastKnownLocation = pLocation;
		this.mLocationCallback.fireLocationChanged(pLocation);
	}

	protected void onLocationLost() {
		this.mLocationCallback.fireLocationLost(this.mLastKnownLocation);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface AndNavLocationCallback {
		public void fireLocationChanged(final AndNavLocation pLocation);
		public void fireLocationLost(final AndNavLocation pLocation);
	}
}
