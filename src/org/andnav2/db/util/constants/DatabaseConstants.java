//Created by plusminus on 14:02:36 - 15.02.2008
package org.andnav2.db.util.constants;


public interface DatabaseConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final String DATETIME_NOW = "DATETIME('NOW')";

	// ===========================================================
	// TABLES AND COLUMNS
	// ===========================================================

	public static final String T_POIHISTORY = "t_poihistory";
	public static final String T_POIHISTORY_COL_POINAME = "name";
	public static final String T_POIHISTORY_COL_LAT = "lat";
	public static final String T_POIHISTORY_COL_LNG = "lng";

	public static final String T_FAVS = "t_favorites";
	public static final String T_FAVS_COL_ID = "id";
	public static final String T_FAVS_COL_FAVNAME = "favname";
	public static final String T_FAVS_COL_LAT = "lat";
	public static final String T_FAVS_COL_LNG = "lng";
	public static final String T_FAVS_COL_USES = "uses";
	public static final String T_FAVS_COL_LAST_USE = "lastuse";

	public static final String T_STREETS = "t_streets";
	public static final String T_STREETS_ID = "id";
	public static final String T_STREETS_NAME = "codeorname";
	public static final String T_STREETS_ZIPORCITY_ID = "zoc_id";

	public static final String T_ZIPSNCITIES = "t_zipsncities";
	public static final String T_ZIPSNCITIES_ID = "id";
	public static final String T_ZIPSNCITIES_NAME = "codeorname";
	public static final String T_ZIPSNCITIES_ISZIP = "iszip";
	public static final String T_ZIPSNCITIES_COUNTRYCODE_ID = "ccode_id";

	public static final String T_COUNTRYCODES = "t_countrycodes";
	public static final String T_COUNTRYCODES_ID = "id";
	public static final String T_COUNTRYCODES_NAME = "name";

	public static final String T_TRAFFICFEEDS = "t_trafficfeeds";
	public static final String T_TRAFFICFEEDS_NAME = "name";
	public static final String T_TRAFFICFEEDS_URL = "url";
	public static final String T_TRAFFICFEEDS_COUNTRYCODE = "cc";

	public static final String CREATE_POIHISTORY_TABLE = "CREATE TABLE IF NOT EXISTS " + T_POIHISTORY
	+ " ("
	+ T_POIHISTORY_COL_POINAME + " VARCHAR(255),"
	+ T_POIHISTORY_COL_LAT + " INTEGER NOT NULL,"
	+ T_POIHISTORY_COL_LNG + " INTEGER NOT NULL,"
	+ " PRIMARY KEY(" + T_POIHISTORY_COL_POINAME + "));";

	public static final String CREATE_TRAFFICFEED_TABLE = "CREATE TABLE IF NOT EXISTS " + T_TRAFFICFEEDS
	+ " ("
	+ T_TRAFFICFEEDS_NAME + " VARCHAR(100),"
	+ T_TRAFFICFEEDS_URL + " VARCHAR(500),"
	+ T_TRAFFICFEEDS_COUNTRYCODE + " INTEGER NOT NULL,"
	+ " PRIMARY KEY(" + T_TRAFFICFEEDS_URL + "));";

	public static final String CREATE_COUNTRYCODE_TABLE = "CREATE TABLE IF NOT EXISTS " + T_COUNTRYCODES
	+ " ("
	+ T_COUNTRYCODES_ID + " INTEGER,"
	+ T_COUNTRYCODES_NAME + " INTEGER NOT NULL,"
	+ " PRIMARY KEY(" + T_COUNTRYCODES_ID + "));";

	public static final String CREATE_STREETS_TABLE = "CREATE TABLE IF NOT EXISTS " + T_STREETS
	+ " ("
	+ T_STREETS_ID + " INTEGER,"
	+ T_STREETS_NAME + " VARCHAR(80) NOT NULL,"
	+ T_STREETS_ZIPORCITY_ID + " INTEGER NOT NULL,"
	+ " PRIMARY KEY(" + T_STREETS_ID + "));";

	public static final String CREATE_ZIPSANDCITIES_TABLE = "CREATE TABLE IF NOT EXISTS " + T_ZIPSNCITIES
	+ " ("
	+ T_ZIPSNCITIES_ID + " INTEGER,"
	+ T_ZIPSNCITIES_NAME + " VARCHAR(80) NOT NULL,"
	+ T_ZIPSNCITIES_COUNTRYCODE_ID + " INTEGER NOT NULL,"
	+ T_ZIPSNCITIES_ISZIP + " INTEGER NOT NULL,"
	+ " PRIMARY KEY(" + T_ZIPSNCITIES_ID + "));";

	public static final String CREATE_FAVORITES_TABLE = "CREATE TABLE IF NOT EXISTS " + T_FAVS
	+ " ("
	+ T_FAVS_COL_ID + " INTEGER NOT NULL,"
	+ T_FAVS_COL_FAVNAME + " VARCHAR(255) UNIQUE NOT NULL,"
	+ T_FAVS_COL_LAT + " INTEGER NOT NULL,"
	+ T_FAVS_COL_LNG + " INTEGER NOT NULL,"
	+ T_FAVS_COL_USES + " INTEGER NOT NULL DEFAULT 1,"
	+ T_FAVS_COL_LAST_USE + " DATE NOT NULL,"
	+ " PRIMARY KEY(" + T_FAVS_COL_ID + "));";

	// ===========================================================
	// Methods
	// ===========================================================
}
