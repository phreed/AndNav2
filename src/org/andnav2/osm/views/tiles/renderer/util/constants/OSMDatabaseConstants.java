// Created by plusminus on 13:55:54 - 08.02.2009
package org.andnav2.osm.views.tiles.renderer.util.constants;


public interface OSMDatabaseConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final String DB_PATH = "osm/osm.sqlite.db";

	public static final String T_PLANET_OSM_LINE = "planet_osm_line";
	public static final String T_PLANET_OSM_LINE_COL_ID = "osm_id";
	public static final String T_PLANET_OSM_LINE_COL_NAME = "name";
	public static final String T_PLANET_OSM_LINE_COL_HIGHWAY = "highway";

	public static final String T_PLANET_OSM_LINE_COORDS = T_PLANET_OSM_LINE + "_coords";
	public static final String T_PLANET_OSM_LINE_COORDS_COL_ID = "id";
	public static final String T_PLANET_OSM_LINE_COORDS_COL_OSM_ID = T_PLANET_OSM_LINE + "_id";
	public static final String T_PLANET_OSM_LINE_COORDS_COL_X = "x";
	public static final String T_PLANET_OSM_LINE_COORDS_COL_Y = "y";


	public static final String T_PLANET_OSM_POINT = "planet_osm_point";
	public static final String T_PLANET_OSM_POINT_COL_ID = "osm_id";
	public static final String T_PLANET_OSM_POINT_COL_NAME = "name";
	public static final String T_PLANET_OSM_LINE_COL_ZORDER = "z_order";

	public static final String T_PLANET_OSM_POINT_COORDS = T_PLANET_OSM_POINT + "_coords";
	public static final String T_PLANET_OSM_POINT_COORDS_COL_ID = "id";
	public static final String T_PLANET_OSM_POINT_COORDS_COL_OSM_ID = T_PLANET_OSM_POINT + "_id";
	public static final String T_PLANET_OSM_POINT_COORDS_COL_X = "x";
	public static final String T_PLANET_OSM_POINT_COORDS_COL_Y = "y";

	// ===========================================================
	// Methods
	// ===========================================================
}
