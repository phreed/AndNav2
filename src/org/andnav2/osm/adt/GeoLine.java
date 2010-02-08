package org.andnav2.osm.adt;

import org.andnav2.osm.util.Util;

public class GeoLine {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final int mLatA, mLonA;
	protected final int mLatB, mLonB;
	private GeoPoint mGeoPointA;
	private GeoPoint mGeoPointB;

	// ===========================================================
	// Constructors
	// ===========================================================

	public GeoLine(final int pLatA, final int pLonA, final int pLatB, final int pLonB){
		this.mLatA = pLatA;
		this.mLonA = pLonA;
		this.mLatB = pLatB;
		this.mLonB = pLonB;
	}

	public GeoLine(final GeoPoint pGeoPointA, final GeoPoint pGeoPointB){
		this.mLatA = pGeoPointA.getLatitudeE6();
		this.mLonA = pGeoPointA.getLongitudeE6();
		this.mLatB = pGeoPointB.getLatitudeE6();
		this.mLonB = pGeoPointB.getLongitudeE6();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getLength(){
		return getGeoPointA().distanceTo(getGeoPointB());
	}

	public GeoPoint getGeoPointA(){
		if(this.mGeoPointA == null) {
			this.mGeoPointA = new GeoPoint(this.mLatA, this.mLonA);
		}

		return this.mGeoPointA;
	}

	public GeoPoint getGeoPointB(){
		if(this.mGeoPointB == null) {
			this.mGeoPointB = new GeoPoint(this.mLatB, this.mLonB);
		}

		return this.mGeoPointB;
	}

	public GeoPoint getCenter() {
		return GeoPoint.fromCenterBetween(getGeoPointA(), getGeoPointB());
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public boolean intersects(final GeoLine other){
		return Util.linesIntersect(this.mLonA, this.mLatA,
				this.mLonB, this.mLatB,
				other.mLonA, other.mLatA,
				other.mLonB, other.mLatB);
	}

	public static boolean intersect(final GeoLine first, final GeoLine other){
		return first.intersects(other);
	}
}
