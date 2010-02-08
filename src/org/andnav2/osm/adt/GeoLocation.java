// Created by plusminus on 00:51:14 - 03.12.2008
package org.andnav2.osm.adt;

import java.util.GregorianCalendar;


public class GeoLocation extends GeoPoint {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private long mTimeStamp;
	private int mAltitude = NOT_SET;
	private int mBearing = NOT_SET;
	private int mSpeed = NOT_SET;

	// ===========================================================
	// Constructors
	// ===========================================================

	public GeoLocation(final int latitudeE6, final int longitudeE6) {
		this(latitudeE6, longitudeE6, new GregorianCalendar().getTimeInMillis());
	}

	public GeoLocation(final int latitudeE6, final int longitudeE6, final long aTimeStamp) {
		super(latitudeE6, longitudeE6);
		this.mTimeStamp = aTimeStamp;
	}

	public GeoLocation(final int latitudeE6, final int longitudeE6, final long aTimeStamp, final int aAltitude, final int aBearing, final int aSpeed) {
		this(latitudeE6, longitudeE6, aTimeStamp);
		this.mAltitude = aAltitude;
		this.mSpeed = aSpeed;
		this.mAltitude = aAltitude;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public long getTimeStamp() {
		return this.mTimeStamp;
	}

	public void setTimeStamp(final long timeStamp) {
		this.mTimeStamp = timeStamp;
	}

	public boolean hasTimeStamp(){
		return this.mTimeStamp != 0;
	}

	public int getAltitude() {
		return this.mAltitude;
	}

	public void setAltitude(final int altitude) {
		this.mAltitude = altitude;
	}

	public boolean hasAltitude(){
		return this.mAltitude != NOT_SET;
	}

	public int getBearing() {
		return this.mBearing;
	}

	public void setBearing(final int bearing) {
		this.mBearing = bearing;
	}

	public boolean hasBearing(){
		return this.mBearing != NOT_SET;
	}

	public int getSpeed() {
		return this.mSpeed;
	}

	public void setSpeed(final int speed) {
		this.mSpeed = speed;
	}

	public boolean hasSpeed(){
		return this.mSpeed != NOT_SET;
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
