// Created by plusminus on 12:31:48 AM - Mar 27, 2009
package org.andnav2.sys.ors.adt.ts;

import java.util.List;

import org.andnav2.sys.ors.views.overlay.TrafficOverlayItem;

public class TrafficOverlayManager extends ListBackedQuadTreeOrganizer<TrafficOverlayItem> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public TrafficOverlayManager() {
		super();
	}

	public TrafficOverlayManager(final List<TrafficOverlayItem> pItems) {
		super(pItems);
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
