// Created by plusminus on 19:06:15 - 25.05.2008
package org.andnav2.nav;

import java.util.List;

import org.andnav2.osm.adt.GeoPoint;


public interface WayPointListener {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void onWaypointPassed(List<GeoPoint> waypointsLeft);

	public void onTargetReached();
}
