package org.andnav2.loc;

import org.andnav2.adt.AndNavLocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

/**
 * 
 * @author Nicolas Gramlich
 * @since 00:21:56 - 19.07.2009
 */
public class NetworkFallbackLocationProvider extends DefaultLocationProvider {
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 0; // in Meters
	protected static final long MINIMUM_TIME_BETWEEN_UPDATE = 0; // in Milliseconds

	private static final String NETWORK_PROVIDER_NAME = LocationManager.NETWORK_PROVIDER;

	// ===========================================================
	// Fields
	// ===========================================================

	protected SampleLocationListener mNetworkLocationListener;

	// ===========================================================
	// Constructors
	// ===========================================================

	public NetworkFallbackLocationProvider(final Context ctx, final AndNavLocationCallback pCallback) {
		super(ctx, pCallback);

		// register location listener
		initLocationManager(ctx);

		/* Check if superclass already got a location. */
		if(!hasLastKnownLocation()){
			final Location networkLocation = this.mLocationManager.getLastKnownLocation(NETWORK_PROVIDER_NAME);		
			onPublishLocation(convert(networkLocation));
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	private void initLocationManager(final Context ctx) {
		this.mNetworkLocationListener = new SampleLocationListener();
	}

	
	private AndNavLocation convert(final Location pLastKnownLocation) {
		if(pLastKnownLocation == null) {
			return null;
		}

		return new AndNavLocation(NETWORK_PROVIDER_NAME,
				(int)(pLastKnownLocation.getLatitude() * 1E6),
				(int)(pLastKnownLocation.getLongitude() * 1E6),
				((pLastKnownLocation.hasAccuracy()) ? (int)pLastKnownLocation.getAccuracy() : NOT_SET),
				NOT_SET,
				1, // Triangulation between 1 and 3 cell towers. ? 
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
		super.onCreate();
		this.mNetworkLocationListener = new SampleLocationListener();
		this.mLocationManager.requestLocationUpdates(NETWORK_PROVIDER_NAME, MINIMUM_TIME_BETWEEN_UPDATE, MINIMUM_DISTANCECHANGE_FOR_UPDATE, this.mNetworkLocationListener);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.mLocationManager.removeUpdates(this.mNetworkLocationListener);
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
			if(!NetworkFallbackLocationProvider.this.hasLastKnownLocation()){
				if (loc != null){
					NetworkFallbackLocationProvider.this.onPublishLocation(convert(loc));
				}else{
					NetworkFallbackLocationProvider.this.onLocationLost();
				}
			}
		}

		public void onStatusChanged(final String a, final int status, final Bundle extras) {
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
