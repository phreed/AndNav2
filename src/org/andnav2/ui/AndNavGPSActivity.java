// Created by plusminus on 22:59:23 - 09.08.2008
package org.andnav2.ui;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.adt.util.TypeConverter;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;



public abstract class AndNavGPSActivity extends AndNavBaseActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final String PROVIDER_NAME = LocationManager.GPS_PROVIDER;

	// ===========================================================
	// Fields
	// ===========================================================

	private final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 20; // in Meters
	private final long MINIMUM_TIME_BETWEEN_UPDATE = 2000; // in Milliseconds

	/* IntentReceiver, that will react on the
	 * Intents we made our LocationManager send to us. */
	private MyLocationChangedListener mLocationChangedListener;

	protected LocationManager mLocationManager;
	protected Location mMyLocation;
	/** Indicates whether onLocationChanged of subclasses will get called. */
	private boolean mDoCallOnLocationChanged = true;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);

		this.initLocation();

		this.mMyLocation = this.mLocationManager.getLastKnownLocation(PROVIDER_NAME);
		if(this.mMyLocation == null){
			this.mMyLocation = getFallbackLocation();
		}
	}

	private void initLocation() {
		try{
			this.mLocationChangedListener = new MyLocationChangedListener();
			getLocationManager().requestLocationUpdates(PROVIDER_NAME, this.MINIMUM_TIME_BETWEEN_UPDATE, this.MINIMUM_DISTANCECHANGE_FOR_UPDATE, this.mLocationChangedListener);
		}catch(final Throwable t){
			Log.e(DEBUGTAG, "Error in initLocation()", t);
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/** Offers the current GPS-position to the Overlay. */
	public Location getCurrentLocation() {
		if(this.mMyLocation == null){
			this.mMyLocation = getFallbackLocation();
		}			
		return this.mMyLocation;
	}

	/** Offers the current GPS-position to the Overlay as a mapPoint. */
	public GeoPoint getCurrentLocationAsGeoPoint() {
		return TypeConverter.locationToGeoPoint(this.mMyLocation);
	}

	private LocationManager getLocationManager() {
		if(this.mLocationManager == null) {
			this.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		return this.mLocationManager;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	/** Restart the receiving, when we are back on line. */
	@Override
	public void onResume() {
		super.onResume();
		this.mDoCallOnLocationChanged = true;
	}

	/** Make sure to stop the animation when we're no longer on screen,
	 * failing to do so will cause a lot of unnecessary cpu-usage! */
	@Override
	public void onPause() {
		this.mDoCallOnLocationChanged = false;
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getLocationManager().removeUpdates(this.mLocationChangedListener);
	}

	// ===========================================================
	// Abstract Methods
	// ===========================================================

	/** Gets called always when the current (GPS) Location has changed. */
	protected abstract void onLocationChanged();
	protected abstract void onLocationLost();

	// ===========================================================
	// Methods
	// ===========================================================

	private Location getFallbackLocation() {
		return this.mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class MyLocationChangedListener implements LocationListener {
		public void onLocationChanged(final Location loc) {
			AndNavGPSActivity.this.mMyLocation = loc;
			if(AndNavGPSActivity.this.mDoCallOnLocationChanged){
				if(loc == null) {
					AndNavGPSActivity.this.onLocationChanged();
				} else {
					AndNavGPSActivity.this.onLocationLost();
				}
			}
		}

		public void onStatusChanged(final String arg0, final int arg1, final Bundle extras) {
			// ignore
		}

		public void onProviderEnabled(final String arg0) {
			// ignore
		}

		public void onProviderDisabled(final String arg0) {
			// ignore
		}
	}
}
