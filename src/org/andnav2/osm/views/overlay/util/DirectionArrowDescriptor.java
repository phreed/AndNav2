// Created by plusminus on 12:22:45 AM - Feb 19, 2009
package org.andnav2.osm.views.overlay.util;

import android.graphics.Point;


public class DirectionArrowDescriptor {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final Point mCenter;
	private final int mDrawableID;

	// ===========================================================
	// Constructors
	// ===========================================================

	public DirectionArrowDescriptor(final int pDrawableID) {
		this(null, pDrawableID);
	}

	public DirectionArrowDescriptor(final Point pCenter, final int pDrawableID) {
		this.mCenter = pCenter;
		this.mDrawableID = pDrawableID;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================


	public Point getCenter() {
		return this.mCenter;
	}

	public int getDrawableID() {
		return this.mDrawableID;
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
