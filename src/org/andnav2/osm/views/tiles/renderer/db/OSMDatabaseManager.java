// Created by plusminus on 14:22:37 - 10.02.2009
package org.andnav2.osm.views.tiles.renderer.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.andnav2.db.DataBaseException;
import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.views.tiles.renderer.db.adt.OSMNode;
import org.andnav2.osm.views.tiles.renderer.db.adt.OSMWay;
import org.andnav2.osm.views.tiles.renderer.util.constants.OSMDatabaseConstants;
import org.andnav2.osm.views.util.MercatorSpherical;
import org.andnav2.util.constants.Constants;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class OSMDatabaseManager implements OSMDatabaseConstants, Constants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static SQLiteDatabase mInstance;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static void release(){
		closeDB();
		mInstance = null;
	}

	private static SQLiteDatabase ensureDBInstanceExists(final Context ctx) throws DataBaseException {
		if(mInstance == null) {
			mInstance = SQLiteDatabase.openDatabase("/sdcard/andnav2/osm/osm.sqlite.db", null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		}
		/* Create the Database (no Errors if it already exists) */
		return mInstance;
	}

	private static void closeDB() {
		if (mInstance != null) {
			mInstance.close();
		}
	}

	public static List<OSMWay> getWaysFromBoundingBox(final Context ctx, final BoundingBoxE6 bbE6) throws DataBaseException {
		final Map<Long, OSMWay> wayMapper = new HashMap<Long, OSMWay>();

		/* Output should be ordered by the zOrder. */
		final List<OSMWay> ways;

		ensureDBInstanceExists(ctx);

		final Cursor c = mInstance.rawQuery(buildSelectWayNodesFromBoundingBoxQuery(bbE6), null);
		if(c != null){
			ways = new ArrayList<OSMWay>(c.getCount());
			try{
				final int colOSMID = c.getColumnIndexOrThrow(T_PLANET_OSM_LINE_COORDS_COL_OSM_ID);
				final int colX = c.getColumnIndexOrThrow(T_PLANET_OSM_LINE_COORDS_COL_X);
				final int colY = c.getColumnIndexOrThrow(T_PLANET_OSM_LINE_COORDS_COL_Y);
				final int colName = c.getColumnIndexOrThrow(T_PLANET_OSM_LINE_COL_NAME);
				final int colHighway = c.getColumnIndexOrThrow(T_PLANET_OSM_LINE_COL_HIGHWAY);
				final int colZOrder = c.getColumnIndexOrThrow(T_PLANET_OSM_LINE_COL_ZORDER);

				if(c.moveToFirst()){
					do{
						final long osmid = c.getLong(colOSMID);
						final int lonE6 = (int)(MercatorSpherical.x2lon(c.getDouble(colX)) * 1E6);
						final int latE6 = (int)(MercatorSpherical.y2lat(c.getDouble(colY)) * 1E6);
						final String name = c.getString(colName);
						final String highway = c.getString(colHighway);
						final int zOrder = c.getInt(colZOrder);

						final OSMNode nodeToAdd = new OSMNode(-1, latE6, lonE6, name);

						/* Check if the way exists. */
						final OSMWay existingWay = wayMapper.get(osmid);
						if(existingWay == null){
							final OSMWay newWay = new OSMWay(osmid, name, highway, zOrder);
							wayMapper.put(osmid, newWay);
							ways.add(newWay);
							newWay.add(nodeToAdd);
						}else{
							existingWay.add(nodeToAdd);
						}
					}while(c.moveToNext());
				}
			}finally{
				c.close();
			}
		}else{
			ways = new ArrayList<OSMWay>(1);
		}

		Collections.sort(ways, new Comparator<OSMWay>(){
			public int compare(final OSMWay pWayA, final OSMWay pWayB) {
				return pWayA.getZOrder() - pWayB.getZOrder();
			}
		});
		return ways;
	}

	public static List<OSMNode> getNodesFromBoundingBox(final Context ctx, final BoundingBoxE6 bbE6) throws DataBaseException {
		final List<OSMNode> out = new LinkedList<OSMNode>();
		ensureDBInstanceExists(ctx);

		final Cursor c = mInstance.rawQuery(buildSelectNodesFromBoundingBoxQuery(bbE6), null);
		if(c != null){
			try{
				final int colOSMID = c.getColumnIndexOrThrow(T_PLANET_OSM_POINT_COORDS_COL_OSM_ID);
				final int colX = c.getColumnIndexOrThrow(T_PLANET_OSM_POINT_COORDS_COL_X);
				final int colY = c.getColumnIndexOrThrow(T_PLANET_OSM_POINT_COORDS_COL_Y);
				final int colName = c.getColumnIndexOrThrow(T_PLANET_OSM_POINT_COL_NAME);

				if(c.moveToFirst()){
					do{
						final long osmid = c.getLong(colOSMID);
						final int lonE6 = (int)(MercatorSpherical.x2lon(c.getDouble(colX)) * 1E6);
						final int latE6 = (int)(MercatorSpherical.y2lat(c.getDouble(colY)) * 1E6);
						final String name = c.getString(colName);
						out.add(new OSMNode(osmid, latE6, lonE6, name));
					}while(c.moveToNext());
				}
			}finally{
				c.close();
			}
		}

		return out;
	}

	private static String buildSelectWayNodesFromBoundingBoxQuery(final BoundingBoxE6 pBBE6) {
		final double mercLonWest = MercatorSpherical.lon2x(pBBE6.getLonWest());
		final double mercLonEast = MercatorSpherical.lon2x(pBBE6.getLonEast());
		final double mercLatSouth = MercatorSpherical.lat2y(pBBE6.getLatSouth());
		final double mercLatNorth = MercatorSpherical.lat2y(pBBE6.getLatNorth());

		return new StringBuilder()
		.append("SELECT DISTINCT "
				+ T_PLANET_OSM_LINE_COORDS_COL_OSM_ID +","
				+ T_PLANET_OSM_LINE_COORDS_COL_X + ","
				+ T_PLANET_OSM_LINE_COORDS_COL_Y + ","
				+ T_PLANET_OSM_LINE_COL_NAME + ","
				+ T_PLANET_OSM_LINE_COL_HIGHWAY + ","
				+ T_PLANET_OSM_LINE_COL_ZORDER + " "
				+ " FROM "
				+ T_PLANET_OSM_LINE_COORDS
				+ " INNER JOIN "
				+ T_PLANET_OSM_LINE
				+ " ON "
				+ T_PLANET_OSM_LINE + "." + T_PLANET_OSM_LINE_COL_ID
				+ " = "
				+ T_PLANET_OSM_LINE_COORDS + "." + T_PLANET_OSM_LINE_COORDS_COL_OSM_ID)
				.append(" WHERE x > ").append(mercLonWest).append(" AND ")
				.append("x < ").append(mercLonEast).append(" AND ")
				.append("y > ").append(mercLatSouth).append(" AND ")
				.append("y < ").append(mercLatNorth)
				.toString();
	}


	private static String buildSelectNodesFromBoundingBoxQuery(final BoundingBoxE6 pBBE6){
		final double mercLonWest = MercatorSpherical.lon2x(pBBE6.getLonWest());
		final double mercLonEast = MercatorSpherical.lon2x(pBBE6.getLonEast());
		final double mercLatSouth = MercatorSpherical.lat2y(pBBE6.getLatSouth());
		final double mercLatNorth = MercatorSpherical.lat2y(pBBE6.getLatNorth());

		return new StringBuilder()
		.append("SELECT DISTINCT "
				+ T_PLANET_OSM_POINT_COORDS_COL_OSM_ID +","
				+ T_PLANET_OSM_POINT_COORDS_COL_X + ","
				+ T_PLANET_OSM_POINT_COORDS_COL_Y + ","
				+ T_PLANET_OSM_POINT_COL_NAME + " "
				+ " FROM "
				+ T_PLANET_OSM_POINT_COORDS
				+ " INNER JOIN "
				+ T_PLANET_OSM_POINT
				+ " ON "
				+ T_PLANET_OSM_POINT + "." + T_PLANET_OSM_POINT_COL_ID
				+ " = "
				+ T_PLANET_OSM_POINT_COORDS + "." + T_PLANET_OSM_POINT_COORDS_COL_OSM_ID)
				.append(" WHERE x > ").append(mercLonWest).append(" AND ")
				.append("x < ").append(mercLonEast).append(" AND ")
				.append("y > ").append(mercLatSouth).append(" AND ")
				.append("y < ").append(mercLatNorth)
				.toString();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
