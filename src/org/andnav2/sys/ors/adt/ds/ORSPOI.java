// Created by plusminus on 19:27:52 - 15.11.2008
package org.andnav2.sys.ors.adt.ds;

import org.andnav2.osm.adt.GeoPoint;


public class ORSPOI {
	// ===========================================================
	// Constants
	// ===========================================================

	protected String mName;
	protected POIType mPOIType;
	protected GeoPoint mGeoPoint;
	protected int mDistance;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public ORSPOI() {

	}

	ORSPOI(final String name, final POIType type, final GeoPoint geoPoint) {
		this.mName = name;
		this.mPOIType = type;
		this.mGeoPoint = geoPoint;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getName() {
		return this.mName;
	}

	public POIType getPOIType() {
		return this.mPOIType;
	}

	public GeoPoint getGeoPoint() {
		return this.mGeoPoint;
	}

	public void setName(final String name) {
		this.mName = name;
	}

	public void setPOIType(final POIType type) {
		this.mPOIType = type;
	}

	public void setGeoPoint(final GeoPoint geoPoint) {
		this.mGeoPoint = geoPoint;
	}

	public void setDistance(final int d) {
		this.mDistance = d;
	}

	public int getDistance() {
		return this.mDistance;
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
