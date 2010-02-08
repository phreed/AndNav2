// Created by plusminus on 14:31:57 - 15.02.2008
package org.andnav2.adt;

import org.andnav2.osm.adt.GeoPoint;

public class Favorite extends GeoPoint{

	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final String mName;
	protected final int mUses;

	// ===========================================================
	// Constructors
	// ===========================================================

	public Favorite(final String aName, final int aLatitude, final int aLongitude, final int aUses) {
		super(aLatitude, aLongitude);
		this.mName = aName;
		this.mUses = aUses;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getName() {
		return this.mName;
	}

	public String getFullString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Name: ");
		sb.append(this.mName);
		sb.append("    Lat: ");
		sb.append(this.mLatitudeE6);
		sb.append("  Lng: ");
		sb.append(this.mLongitudeE6);
		return sb.toString();
	}


	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public String toString() {
		return new StringBuilder()
		.append(this.mName)
		.append(" (#")
		.append(this.mUses)
		.append(")")
		.toString();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
