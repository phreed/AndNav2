// Created by plusminus on 19:24:16 - 12.11.2008
package org.andnav2.osm.views.tiles.util;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.util.ValuePair;
import org.andnav2.osm.util.Util.PixelSetter;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.osm.views.tiles.OSMAbstractMapTileProvider;
import org.andnav2.osm.views.tiles.OSMMapTileManager;
import org.andnav2.osm.views.tiles.OSMMapTileProviderInfo;
import org.andnav2.osm.views.tiles.adt.OSMTileInfo;
import org.andnav2.osm.views.util.Util;
import org.andnav2.osm.views.util.constants.OSMMapViewConstants;
import org.andnav2.sys.ors.adt.rs.Route;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class OSMMapTilePreloader implements OSMConstants, OSMMapViewConstants {
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

	/**
	 * Loads all MapTiles needed to cover a route at a specific zoomlevel.
	 */
	public void loadAllToCacheAsync(final Route aRoute, final int aZoomLevel, final OSMMapTileProviderInfo aRendererInfo, final OSMMapTileManager pTileProvider, final OnProgressChangeListener pProgressListener, final boolean pSmoothed) throws IllegalArgumentException {
		loadAllToCacheAsync(OSMMapTilePreloader.getNeededMaptiles(aRoute, aZoomLevel, aRendererInfo, pSmoothed), aZoomLevel, aRendererInfo, pTileProvider, pProgressListener);
	}

	/**
	 * Loads a series of MapTiles to the various caches at a specific zoomlevel.
	 */
	public void loadAllToCacheAsync(final OSMTileInfo[][] pTiles, final int uptoZoomLevel, final OSMMapTileProviderInfo aRendererInfo, final OSMMapTileManager pTileManager, final OnProgressChangeListener pProgressListener){
		int tmpCount = 0;
		for(final OSMTileInfo[] tiles : pTiles) {
			tmpCount += tiles.length;
		}

		final int overallCount = tmpCount;

		final Counter overallCounter = new Counter();
		final Counter successCounter = new Counter();

		final Handler h = new Handler(){
			@Override
			public void handleMessage(final Message msg) {
				final int what = msg.what;
				overallCounter.increment();
				switch(what){
					case OSMAbstractMapTileProvider.MAPTILEPROVIDER_SUCCESS_ID:
						successCounter.increment();
						pProgressListener.onProgressChange(successCounter.getCount(), overallCount);
						if(DEBUGMODE) {
							Log.i(DEBUGTAG, "MapTile download success.");
						}
						break;
					case OSMAbstractMapTileProvider.MAPTILEPROVIDER_FAIL_ID:
						if(DEBUGMODE) {
							Log.e(DEBUGTAG, "MapTile download error.");
						}
						break;
				}
				if(overallCounter.getCount() == overallCount
						&& successCounter.getCount() != overallCount) {
					pProgressListener.onProgressChange(overallCount, overallCount);
				}

				super.handleMessage(msg);
			}
		};

		new Thread(new Runnable(){
			public void run() {
				for(int i = 0; i < pTiles.length; i++){
					final OSMTileInfo[] tileSet = pTiles[i];
					for (final OSMTileInfo tile : tileSet) {
						if(!pTileManager.preloadMaptileAsync(tile, h)) {
							h.sendEmptyMessage(OSMAbstractMapTileProvider.MAPTILEPROVIDER_SUCCESS_ID);
						}
					}
				}
			}
		}, "Maptile-Preloader preparer").start();
	}

	/**
	 * Loads a series of MapTiles to the various caches at a specific zoomlevel.
	 */
	public void loadAllToCacheAsync(final OSMTileInfo[] pTiles, final int aZoomLevel, final OSMMapTileProviderInfo aRendererInfo, final OSMMapTileManager pTileManager, final OnProgressChangeListener pProgressListener){
		final int overallCount = pTiles.length;

		final Counter overallCounter = new Counter();
		final Counter successCounter = new Counter();
		final Handler h = new Handler(){
			@Override
			public void handleMessage(final Message msg) {
				final int what = msg.what;
				overallCounter.increment();
				switch(what){
					case OSMAbstractMapTileProvider.MAPTILEPROVIDER_SUCCESS_ID:
						successCounter.increment();
						pProgressListener.onProgressChange(successCounter.getCount(), overallCount);
						if(DEBUGMODE) {
							Log.i(DEBUGTAG, "MapTile download success.");
						}
						break;
					case OSMAbstractMapTileProvider.MAPTILEPROVIDER_FAIL_ID:
						if(DEBUGMODE) {
							Log.e(DEBUGTAG, "MapTile download error.");
						}
						break;
				}
				if(overallCounter.getCount() == overallCount
						&& successCounter.getCount() != overallCount) {
					pProgressListener.onProgressChange(overallCount, overallCount);
				}

				super.handleMessage(msg);
			}
		};

		new Thread(new Runnable(){
			public void run() {
				for (final OSMTileInfo tile : pTiles) {
					if(!pTileManager.preloadMaptileAsync(tile, h)) {
						h.sendEmptyMessage(OSMAbstractMapTileProvider.MAPTILEPROVIDER_SUCCESS_ID);
					}
				}
			}
		}, "Maptile-Preloader preparer").start();
	}


	/**
	 * 
	 * @param aRoute
	 * @param aZoomLevel
	 * @param aProviderInfo
	 * @param pSmoothed Smoothed by a Bresenham-Algorithm
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static OSMTileInfo[] getNeededMaptiles(final Route aRoute, final int aZoomLevel, final OSMMapTileProviderInfo aProviderInfo, final boolean pSmoothed) throws IllegalArgumentException {
		return getNeededMaptiles(aRoute.getPolyLine(), aZoomLevel, aProviderInfo, pSmoothed);
	}

	/**
	 * 
	 * @param aPath
	 * @param aZoomLevel
	 * @param aProviderInfo
	 * @param pSmoothed Smoothed by a Bresenham-Algorithm
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static OSMTileInfo[] getNeededMaptiles(final List<GeoPoint> aPath, final int aZoomLevel, final OSMMapTileProviderInfo aProviderInfo, final boolean pSmoothed) throws IllegalArgumentException {
		if(aZoomLevel > aProviderInfo.ZOOM_MAXLEVEL) {
			throw new IllegalArgumentException("Zoomlevel higher than Renderer supplies!");
		}

		/* We need only unique MapTile-indices, so we use a Set. */
		final Set<ValuePair> needed = new TreeSet<ValuePair>(new Comparator<ValuePair>(){
			public int compare(final ValuePair a, final ValuePair b) {
				return a.compareTo(b);
			}
		});

		/* Contains the values of a single line. */
		final Set<ValuePair> rasterLine = new TreeSet<ValuePair>(new Comparator<ValuePair>(){
			public int compare(final ValuePair a, final ValuePair b) {
				return a.compareTo(b);
			}
		});

		final PixelSetter rasterLinePixelSetter = new PixelSetter(){
			public void setPixel(final int x, final int y) {
				rasterLine.add(new ValuePair(x,y));
			}
		};

		OSMTileInfo cur = null;

		GeoPoint previous = null;
		/* Get the mapTile-coords of every point in the polyline and add to the set. */
		for (final GeoPoint gp : aPath) {
			cur = Util.getMapTileFromCoordinates(gp, aZoomLevel);
			needed.add(new ValuePair(cur.x, cur.y));

			if(previous != null){
				final int prevX = cur.x;
				final int prevY = cur.y;

				cur = Util.getMapTileFromCoordinates(GeoPoint.getGeoPointBetween(gp, previous), aZoomLevel);

				final int curX = cur.x;
				final int curY = cur.y;

				rasterLine.clear();
				org.andnav2.osm.util.Util.rasterLine(prevX, prevY, curX, curY, rasterLinePixelSetter);

				/* If wanted smooth that line. */
				if(pSmoothed){
					org.andnav2.osm.util.Util.smoothLine(rasterLine);
				}

				needed.addAll(rasterLine);
			}

			previous = gp;
		}

		/* Put the unique MapTile-indices into an array. */
		final int countNeeded = needed.size();
		final OSMTileInfo[] out = new OSMTileInfo[countNeeded];

		int i = 0;
		for (final ValuePair valuePair : needed) {
			out[i++] = new OSMTileInfo(valuePair.getValueA(), valuePair.getValueB(), aZoomLevel);
		}

		return out;
	}


	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public interface OnProgressChangeListener{
		/** Between 0 and 100 (including). */
		void onProgressChange(final int aProgress, final int aMax);
	}

	private static class Counter{
		int mCount;

		public void increment() {
			this.mCount++;
		}

		public int getCount() {
			return this.mCount;
		}
	}
}
