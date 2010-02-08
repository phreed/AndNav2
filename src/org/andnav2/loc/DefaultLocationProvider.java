package org.andnav2.loc;

import org.andnav2.adt.AndNavLocation;
import org.andnav2.util.constants.Constants;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

/**
 * 
 * @author Nicolas Gramlich
 * @since 00:22:05 - 19.07.2009
 */
public class DefaultLocationProvider extends AbstractAndNavLocationProvider implements Constants {
	// ===========================================================
	// Constants
	// ===========================================================

	protected final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 0; // in Meters
	protected final long MINIMUM_TIME_BETWEEN_UPDATE = 0; // in Milliseconds

	private static final String PROVIDER_NAME = LocationManager.GPS_PROVIDER;

	// ===========================================================
	// Fields
	// ===========================================================

	protected SampleLocationListener mLocationListener;

	protected LocationManager mLocationManager;
	public int mNumSatellites;

	// ===========================================================
	// Constructors
	// ===========================================================

	public DefaultLocationProvider(final Context ctx, final AndNavLocationCallback pCallback) {
		super(pCallback);

		// register location listener
		initLocationManager(ctx);

		onPublishLocation(convert(this.mLocationManager.getLastKnownLocation(PROVIDER_NAME)));
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	private void initLocationManager(final Context ctx) {
		this.mLocationListener = new SampleLocationListener();
		this.mLocationManager = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
	}

	private AndNavLocation convert(final Location pLastKnownLocation) {
		if(pLastKnownLocation == null) {
			return null;
		}

		return new AndNavLocation(PROVIDER_NAME,
				(int)(pLastKnownLocation.getLatitude() * 1E6),
				(int)(pLastKnownLocation.getLongitude() * 1E6),
				((pLastKnownLocation.hasAccuracy()) ? (int)pLastKnownLocation.getAccuracy() : NOT_SET),
				NOT_SET,
				this.mNumSatellites,
				((pLastKnownLocation.hasBearing()) ? pLastKnownLocation.getBearing() : NOT_SET),
				((pLastKnownLocation.hasAltitude()) ? (int)pLastKnownLocation.getAltitude() : NOT_SET),
				pLastKnownLocation.getTime(),
				((pLastKnownLocation.hasSpeed()) ? pLastKnownLocation.getSpeed() : NOT_SET));
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onCreate() {
		this.mLocationListener = new SampleLocationListener();
		this.mLocationManager.requestLocationUpdates(PROVIDER_NAME, MINIMUM_TIME_BETWEEN_UPDATE, MINIMUM_DISTANCECHANGE_FOR_UPDATE, this.mLocationListener);
	}

	@Override
	public void onResume() {
		
	}

	@Override
	public void onStop() {

	}

	@Override
	public void onDestroy() {
		this.mLocationManager.removeUpdates(this.mLocationListener);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	/**
	 * Logs all Location-changes to <code>mRouteRecorder</code>.
	 * @author plusminus
	 */
	private class SampleLocationListener implements LocationListener {
		public void onLocationChanged(final Location loc) {
			if (loc != null){
				DefaultLocationProvider.this.onPublishLocation(convert(loc));
			}else{
				DefaultLocationProvider.this.onLocationLost();
			}
		}

		public void onStatusChanged(final String a, final int status, final Bundle extras) {
			DefaultLocationProvider.this.mNumSatellites = extras.getInt("satellites", NOT_SET);
			switch(status){
				case LocationProvider.AVAILABLE:
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
				case LocationProvider.OUT_OF_SERVICE:
					//        			OpenStreetMapActivity.this.mGPSStatus = status;
					break;
			}
		}

		public void onProviderEnabled(final String a) { /* ignore  */ }
		public void onProviderDisabled(final String a) { /* ignore  */ }
	}
}
