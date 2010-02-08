package org.andnav2.sys.mylieu.api;

import org.andnav2.adt.AndNavLocation;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


/**
 * @author Nicolas Gramlich
 * @since 3:26:30 PM - May 23, 2009
 */
public class Requester {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static boolean submit(final String pServerBaseURL, final String pUsername, final String pPassword, final AndNavLocation pLocation){
		final StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(pServerBaseURL).append("/setUserPosition.php").append("?latitude=").append(pLocation.getLatitudeAsDouble()).append("&longitude=").append(pLocation.getLongitudeAsDouble());

		if(pLocation.hasBearing()) {
			urlBuilder.append("&dir=").append(pLocation.getBearing());
		}

		if(pLocation.hasSpeed()) {
			urlBuilder.append("&speed=").append(pLocation.getSpeed());
		}

		if(pLocation.hasAltitude()) {
			urlBuilder.append("&alt=").append(pLocation.getAltitude());
		}

		if(pLocation.hasHorizontalPositioningError()) {
			urlBuilder.append("&accuracy=").append(pLocation.getHorizontalPositioningError());
		}

		urlBuilder.append("&user=").append(pUsername).append("&key=").append(pPassword);

		final HttpClient httpClient = new DefaultHttpClient();
		final HttpGet request = new HttpGet(urlBuilder.toString());

		try {
			httpClient.execute(request);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

