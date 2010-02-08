// Created by plusminus on 14:33:47 - 10.02.2009
package org.andnav2.osm.views.tiles.renderer.db.adt;

import org.andnav2.osm.adt.GeoPoint;


public class OSMNode extends GeoPoint implements IOSMDataType {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final long mOSMID;
	private final String mName;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMNode(final long pOSMID, final int pLatitudeE6, final int pLongitudeE6) {
		this(pOSMID, pLatitudeE6, pLongitudeE6, null);
	}

	public OSMNode(final long pOSMID, final int pLatitudeE6, final int pLongitudeE6, final String pName) {
		super(pLatitudeE6, pLongitudeE6);
		this.mOSMID = pOSMID;
		this.mName = (pName != null && pName.length() > 0) ? pName : null;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public boolean hasName() {
		return this.mName != null && this.mName.length() > 0;
	}

	public boolean hasOSMID() {
		return this.mOSMID != -1;
	}

	public long getOSMID() {
		return this.mOSMID;
	}

	public String getName() {
		return this.mName;
	}


	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
