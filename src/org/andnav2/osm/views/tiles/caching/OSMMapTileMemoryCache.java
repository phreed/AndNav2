// Created by plusminus on 17:58:57 - 25.09.2008
package org.andnav2.osm.views.tiles.caching;

import org.andnav2.osm.views.util.LRUMapTileCache;
import org.andnav2.osm.views.util.constants.OSMMapViewConstants;

import android.graphics.Bitmap;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class OSMMapTileMemoryCache implements OSMMapViewConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	public static final String FLAG_DIRTY = "_d";

	// ===========================================================
	// Fields
	// ===========================================================

	private final LRUMapTileCache mCachedTiles;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapTileMemoryCache(){
		this(CACHE_MAPTILECOUNT_DEFAULT);
	}

	/**
	 * @param aMaximumCacheSize Maximum amount of MapTiles to be hold within.
	 */
	public OSMMapTileMemoryCache(final int aMaximumCacheSize){
		this.mCachedTiles = new LRUMapTileCache(aMaximumCacheSize);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public synchronized Bitmap getMapTile(final String aFormattedTileURLString) {
		return this.mCachedTiles.get(aFormattedTileURLString);
	}

	public synchronized void putMapTile(final String aFormattedTileURLString, final Bitmap aTile) {
		this.mCachedTiles.put(aFormattedTileURLString, aTile);
	}

	public synchronized void release() {
		for(final Bitmap b : this.mCachedTiles.values()) {
			if(b != null) {
				b.recycle();
			}
		}
		this.mCachedTiles.clear();
	}

	public void onLowMemory() {
		release(); // FIXME might cause problems !!!
	}

	public void onRemoveRecycledBitmapFromMemoryCache(final String pSaveableTileURLString) {
		this.mCachedTiles.remove(pSaveableTileURLString);
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
