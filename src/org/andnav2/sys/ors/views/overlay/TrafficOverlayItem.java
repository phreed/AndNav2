// Created by plusminus on 23:47:17 - 18.01.2009
package org.andnav2.sys.ors.views.overlay;

import org.andnav2.osm.views.overlay.OSMMapViewOverlayItem;
import org.andnav2.sys.ors.adt.ts.TrafficItem;

import android.content.Context;


public class TrafficOverlayItem extends OSMMapViewOverlayItem {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public TrafficOverlayItem(final Context ctx, final TrafficItem pFeature) {
		super(pFeature.getSeverity().name(), pFeature.getDescription(), pFeature.getGeoPoint());
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

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
