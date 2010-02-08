// Created by plusminus on 00:50:30 - 20.01.2009
package org.andnav2.sys.ors.adt.ts;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.adt.IGeoPoint;
import org.andnav2.traffic.tpeg.adt.rtm.table.RTM31_general_magnitude;


public class TrafficItem implements IGeoPoint {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private GeoPoint mGeoPoint;
	private String mDescription;
	private RTM31_general_magnitude mSeverity;

	// ===========================================================
	// Constructors
	// ===========================================================

	public TrafficItem(){

	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public double getLatitudeAsDouble() {
		return this.mGeoPoint.getLatitudeAsDouble();
	}

	public double getLongitudeAsDouble() {
		return this.mGeoPoint.getLongitudeAsDouble();
	}

	public GeoPoint getGeoPoint() {
		return this.mGeoPoint;
	}

	public void setGeoPoint(final GeoPoint geoPoint) {
		this.mGeoPoint = geoPoint;
	}

	public String getDescription() {
		return this.mDescription;
	}

	public void setDescription(final String description) {
		this.mDescription = description;
	}

	public RTM31_general_magnitude getSeverity() {
		return this.mSeverity;
	}

	public void setSeverity(final RTM31_general_magnitude severity) {
		this.mSeverity = severity;
	}

	public int getLatitudeE6() {
		return this.getGeoPoint().getLatitudeE6();
	}

	public int getLongitudeE6() {
		return this.getGeoPoint().getLongitudeE6();
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
