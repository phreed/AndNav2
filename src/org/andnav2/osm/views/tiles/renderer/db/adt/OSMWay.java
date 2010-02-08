// Created by plusminus on 22:33:10 - 11.02.2009
package org.andnav2.osm.views.tiles.renderer.db.adt;

import java.util.ArrayList;

import org.andnav2.osm.views.tiles.renderer.db.adt.constants.OSMWayConstants;
import org.andnav2.util.constants.Constants;

import android.util.Log;


public class OSMWay extends ArrayList<OSMNode> implements IOSMDataType, OSMWayConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final long serialVersionUID = -2292352609046329426L;

	// ===========================================================
	// Fields
	// ===========================================================

	private final long mOSMID;
	private final String mName;
	private final int mHighwayTypeID;
	private final int mZOrder;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMWay(final long pOSMID) {
		this(pOSMID, null, null, 0);
	}

	public OSMWay(final long pOSMID, final String pName, final String pHighway, final int pZOrder) {
		this.mOSMID = pOSMID;
		this.mName = (pName != null && pName.length() > 0) ? pName : null;
		this.mHighwayTypeID = (pHighway != null && pHighway.length() > 0) ? resolveToStatic(pHighway) : OSMWAY_HIGHWAY_UNCLASSIFIED_ID;
		this.mZOrder = pZOrder;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getZOrder() {
		return this.mZOrder;
	}

	public int getHighwayID() {
		return this.mHighwayTypeID;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean equals(final Object other){
		return other != null && other instanceof OSMWay && this.mOSMID == ((OSMWay)other).mOSMID;
	}

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

	private int resolveToStatic(final String pHighway) {
		if(OSMWAY_HIGHWAY_FOOTWAY.compareTo(pHighway) == 0) {
			return OSMWAY_HIGHWAY_FOOTWAY_ID;
		} else if(OSMWAY_HIGHWAY_MOTORWAY.compareTo(pHighway) == 0) {
			return OSMWAY_HIGHWAY_MOTORWAY_ID;
		} else if(OSMWAY_HIGHWAY_MOTORWAY_LINK.compareTo(pHighway) == 0) {
			return OSMWAY_HIGHWAY_MOTORWAY_LINK_ID;
		} else if(OSMWAY_HIGHWAY_PRIMARY.compareTo(pHighway) == 0) {
			return OSMWAY_HIGHWAY_PRIMARY_ID;
		} else if(OSMWAY_HIGHWAY_PRIMARY_LINK.compareTo(pHighway) == 0) {
			return OSMWAY_HIGHWAY_PRIMARY_LINK_ID;
		} else if(OSMWAY_HIGHWAY_RESIDENTIAL.compareTo(pHighway) == 0) {
			return OSMWAY_HIGHWAY_RESIDENTIAL_ID;
		} else if(OSMWAY_HIGHWAY_SECONDARY.compareTo(pHighway) == 0) {
			return OSMWAY_HIGHWAY_SECONDARY_ID;
		} else if(OSMWAY_HIGHWAY_STEPS.compareTo(pHighway) == 0) {
			return OSMWAY_HIGHWAY_STEPS_ID;
		} else if(OSMWAY_HIGHWAY_UNCLASSIFIED.compareTo(pHighway) == 0) {
			return OSMWAY_HIGHWAY_UNCLASSIFIED_ID;
		} else if(OSMWAY_HIGHWAY_CYCLEWAY.compareTo(pHighway) == 0) {
			return OSMWAY_HIGHWAY_CYCLEWAY_ID;
		} else if(OSMWAY_HIGHWAY_SERVICE.compareTo(pHighway) == 0) {
			return OSMWAY_HIGHWAY_SERVICE_ID;
		} else{
			Log.d(Constants.DEBUGTAG, "Unawaited Highway-Type: '" + pHighway + "'");
			return OSMWAY_HIGHWAY_UNCLASSIFIED_ID;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
