// Created by plusminus on 00:51:14 - 03.12.2008
package org.andnav2.osm.adt;

import java.util.Formatter;
import java.util.GregorianCalendar;

import org.andnav2.osm.api.traces.util.Util;



public class GPSGeoLocation extends GeoLocation {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private int mNumSatellites = NOT_SET;

	// ===========================================================
	// Constructors
	// ===========================================================

	public GPSGeoLocation(final int latitudeE6, final int longitudeE6, final long timeStamp) {
		this(latitudeE6, longitudeE6, timeStamp, NOT_SET);
	}

	public GPSGeoLocation(final int latitudeE6, final int longitudeE6, final int aNumSatellites) {
		this(latitudeE6, longitudeE6, new GregorianCalendar().getTimeInMillis(), aNumSatellites);
	}

	public GPSGeoLocation(final int latitudeE6, final int longitudeE6, final long timestamp, final int aNumSatellites) {
		this(latitudeE6, longitudeE6, timestamp, NOT_SET, NOT_SET, NOT_SET, aNumSatellites);
	}

	public GPSGeoLocation(final int latitudeE6, final int longitudeE6, final long timeStamp, final int altitude, final int bearing, final int speed, final int aNumSatellites) {
		super(latitudeE6, longitudeE6, timeStamp, altitude, bearing, speed);
		this.mNumSatellites = aNumSatellites;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getNumSatellites() {
		return this.mNumSatellites;
	}

	public void setNumSatellites(final int aNumSatellites) {
		this.mNumSatellites = aNumSatellites;
	}

	public boolean hasNumSatellites(){
		return this.mNumSatellites != NOT_SET;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void appendToGpxString(final StringBuilder sb, final Formatter f){
		f.format(GPX_TAG_TRACK_SEGMENT_POINT, getLatitudeAsDouble(), getLongitudeAsDouble());
		f.format(GPX_TAG_TRACK_SEGMENT_POINT_TIME, Util.convertTimestampToUTCString(getTimeStamp()));

		if(hasNumSatellites()) {
			f.format(GPX_TAG_TRACK_SEGMENT_POINT_SAT, getNumSatellites());
		}

		if(hasAltitude()) {
			f.format(GPX_TAG_TRACK_SEGMENT_POINT_ELE, getAltitude());
		}

		if(hasBearing()) {
			f.format(GPX_TAG_TRACK_SEGMENT_POINT_SAT, getBearing());
		}

		if(hasSpeed()) {
			f.format(GPX_TAG_TRACK_SEGMENT_POINT_SAT, getSpeed());
		}

		sb.append(GPX_TAG_TRACK_SEGMENT_POINT_CLOSE);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
