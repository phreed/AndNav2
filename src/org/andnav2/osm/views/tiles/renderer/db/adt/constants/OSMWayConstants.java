// Created by plusminus on 10:33:46 - 12.02.2009
package org.andnav2.osm.views.tiles.renderer.db.adt.constants;


public interface OSMWayConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final String OSMWAY_HIGHWAY_RESIDENTIAL = "residential";
	public static final String OSMWAY_HIGHWAY_PRIMARY = "primary";
	public static final String OSMWAY_HIGHWAY_SECONDARY = "secondary";
	public static final String OSMWAY_HIGHWAY_MOTORWAY = "motorway";
	public static final String OSMWAY_HIGHWAY_MOTORWAY_LINK = "motorway_link";
	public static final String OSMWAY_HIGHWAY_PRIMARY_LINK = "primary_link";
	public static final String OSMWAY_HIGHWAY_UNCLASSIFIED = "unclassified";
	public static final String OSMWAY_HIGHWAY_FOOTWAY = "footway";
	public static final String OSMWAY_HIGHWAY_STEPS = "steps";
	public static final String OSMWAY_HIGHWAY_CYCLEWAY = "cycleway";
	public static final String OSMWAY_HIGHWAY_SERVICE = "service";

	public static final int OSMWAY_HIGHWAY_RESIDENTIAL_ID = 0;
	public static final int OSMWAY_HIGHWAY_PRIMARY_ID = OSMWAY_HIGHWAY_RESIDENTIAL_ID + 1;
	public static final int OSMWAY_HIGHWAY_SECONDARY_ID = OSMWAY_HIGHWAY_PRIMARY_ID + 1;
	public static final int OSMWAY_HIGHWAY_MOTORWAY_ID = OSMWAY_HIGHWAY_SECONDARY_ID + 1;
	public static final int OSMWAY_HIGHWAY_MOTORWAY_LINK_ID = OSMWAY_HIGHWAY_MOTORWAY_ID + 1;
	public static final int OSMWAY_HIGHWAY_PRIMARY_LINK_ID = OSMWAY_HIGHWAY_MOTORWAY_LINK_ID + 1;
	public static final int OSMWAY_HIGHWAY_UNCLASSIFIED_ID = OSMWAY_HIGHWAY_PRIMARY_LINK_ID + 1;
	public static final int OSMWAY_HIGHWAY_FOOTWAY_ID = OSMWAY_HIGHWAY_UNCLASSIFIED_ID + 1;
	public static final int OSMWAY_HIGHWAY_STEPS_ID = OSMWAY_HIGHWAY_FOOTWAY_ID + 1;
	public static final int OSMWAY_HIGHWAY_CYCLEWAY_ID = OSMWAY_HIGHWAY_STEPS_ID + 1;
	public static final int OSMWAY_HIGHWAY_SERVICE_ID = OSMWAY_HIGHWAY_CYCLEWAY_ID + 1;
	// ===========================================================
	// Methods
	// ===========================================================
}
