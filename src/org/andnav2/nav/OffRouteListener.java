// Created by plusminus on 23:36:41 - 24.02.2008
package org.andnav2.nav;


public interface OffRouteListener {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	/** Route was left. */
	public void onRouteMissed();

	/** Route was entered or resumed. */
	public void onRouteResumed();
}
