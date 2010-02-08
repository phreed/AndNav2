// Created by plusminus on 6:18:37 PM - Mar 27, 2009
package org.andnav2.osm.adt;


public interface IGeoPoint {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public int getLongitudeE6();
	public int getLatitudeE6();

	public double getLongitudeAsDouble();
	public double getLatitudeAsDouble();
}
