// Created by plusminus on 23:19:42 - 11.11.2008
package org.andnav2.sys.ors.aas;

import java.util.ArrayList;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.sys.ors.adt.aoi.Polygon;


public class AASResponse {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected ArrayList<Polygon> mPolygons;
	protected BoundingBoxE6 mBoundingBoxE6;

	// ===========================================================
	// Constructors
	// ===========================================================

	AASResponse() {

	}

	public AASResponse(final ArrayList<Polygon> pPolygons, final BoundingBoxE6 pBoundingBoxE6) {
		this.mPolygons = pPolygons;
		this.mBoundingBoxE6 = pBoundingBoxE6;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<Polygon> getPolygons() {
		return this.mPolygons;
	}

	public BoundingBoxE6 getBoundingBoxE6() {
		return this.mBoundingBoxE6;
	}

	void setPolygons(final ArrayList<Polygon> polygons) {
		this.mPolygons = polygons;
	}

	void setBoundingBoxE6(final BoundingBoxE6 boundingBoxE6) {
		this.mBoundingBoxE6 = boundingBoxE6;
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
