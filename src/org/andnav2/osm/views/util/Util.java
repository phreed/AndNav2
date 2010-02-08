// Created by plusminus on 17:53:07 - 25.09.2008
package org.andnav2.osm.views.util;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.util.constants.MathConstants;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.osm.views.tiles.adt.OSMTileInfo;
import org.andnav2.osm.views.tiles.caching.LRUCache;
import org.andnav2.osm.views.util.constants.OSMMapViewConstants;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class Util implements OSMMapViewConstants, OSMConstants, MathConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

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

	public static OSMTileInfo[] calculateNeededTilesForZoomLevelInBoundingBox(final int zoom, final BoundingBoxE6 bbE6Visible) {
		final OSMTileInfo upperLeftTile = getMapTileFromCoordinates(bbE6Visible.getLatNorthE6(), bbE6Visible.getLonWestE6(), zoom);
		final OSMTileInfo lowerRightTile = getMapTileFromCoordinates(bbE6Visible.getLatSouthE6(), bbE6Visible.getLonEastE6(), zoom);

		final int countOfTilesLat = Math.abs(upperLeftTile.y - lowerRightTile.y) + 1;
		final int countOfTilesLon = Math.abs(upperLeftTile.x - lowerRightTile.x) + 1;


		final OSMTileInfo[] out = new OSMTileInfo[countOfTilesLat * countOfTilesLon];

		for(int i = 0; i < countOfTilesLat; i++) {
			for(int j = 0; j < countOfTilesLon; j++) {
				out[countOfTilesLon * i + j] = new OSMTileInfo(upperLeftTile.x + j, upperLeftTile.y + i, zoom);
			}
		}

		return out;
	}

	public static OSMTileInfo getMapTileFromCoordinates(final GeoPoint gp, final int zoom) {
		return getMapTileFromCoordinates(gp.getLatitudeE6() / 1E6, gp.getLongitudeE6() / 1E6, zoom);
	}

	public static OSMTileInfo getMapTileFromCoordinates(final int aLat, final int aLon, final int zoom) {
		return getMapTileFromCoordinates(aLat / 1E6, aLon / 1E6, zoom);
	}

	public static OSMTileInfo getMapTileFromCoordinates(final double aLat, final double aLon, final int zoom) {
		final int y = (int) Math.floor((1 - Math.log(Math.tan(aLat * PI / 180) + 1 / Math.cos(aLat * PI / 180)) / PI) / 2 * (1 << zoom));
		final int x = (int) Math.floor((aLon + 180) / 360 * (1 << zoom));

		return new OSMTileInfo(x, y, zoom);
	}

	// Conversion of a MapTile to a BoudingBox

	private final static LRUCache<OSMTileInfo, BoundingBoxE6> TILETOBOUNDINGBOX_CACHE = new LRUCache<OSMTileInfo, BoundingBoxE6>(10);

	public static BoundingBoxE6 getBoundingBoxFromMapTile(final OSMTileInfo aTileInfo) {
		//		final long startMs = System.currentTimeMillis();
		//		try{
		final BoundingBoxE6 cached = TILETOBOUNDINGBOX_CACHE.get(aTileInfo);
		if(cached != null){
			//			Log.d(DEBUGTAG, "######### HIT");
			return cached;
		}else{
			//			Log.d(DEBUGTAG, "######### MISS");
			final BoundingBoxE6 bb = new BoundingBoxE6(tile2lat(aTileInfo.y, aTileInfo.zoom), tile2lon(aTileInfo.x + 1, aTileInfo.zoom), tile2lat(aTileInfo.y + 1, aTileInfo.zoom), tile2lon(aTileInfo.x, aTileInfo.zoom));
			TILETOBOUNDINGBOX_CACHE.put(aTileInfo, bb);
			return bb;
		}
		//		}finally{
		//			final long endMs = System.currentTimeMillis();
		//			Log.d(DEBUGTAG, "CACHED: " + (endMs - startMs));
		//		}
	}

	private static double tile2lon(final int x, final int aZoom) {
		return (360.0f * x / (1 << aZoom)) - 180;
	}

	private static double tile2lat(final int y, final int aZoom) {
		final float n = PI - ((2.0f * PI * y) / (1 << aZoom));
		return 180.0f / PI * Math.atan(0.5f * (Math.exp(n) - Math.exp(-n)));
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
