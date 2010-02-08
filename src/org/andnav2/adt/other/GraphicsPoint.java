// Created by plusminus on 17:43:50 - 28.08.2008
package org.andnav2.adt.other;

public class GraphicsPoint extends android.graphics.Point {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public GraphicsPoint() {
	}

	public GraphicsPoint(final int longitude, final int latitude) {
		this.x = longitude;
		this.y = latitude;
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

	public static float dotProduct(final android.graphics.Point a, final android.graphics.Point b) {
		return (a.x * b.x + a.y * b.y);
	}

	public static float crossProduct(final android.graphics.Point a, final android.graphics.Point b) {
		return (a.x * b.y - a.y * b.x);
	}

	public static GraphicsPoint difference(final android.graphics.Point a, final android.graphics.Point b) {
		return new GraphicsPoint(a.x-b.x, a.y-b.y);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}