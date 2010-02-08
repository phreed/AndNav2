package org.andnav2.osm.views.tiles;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.andnav2.osm.views.tiles.adt.OSMTileInfo;
import org.andnav2.osm.views.tiles.caching.OSMMapTileFilesystemCache;

import android.content.Context;
import android.os.Handler;

public abstract class OSMAbstractMapTileProvider {

	// ===========================================================
	// Constants
	// ===========================================================

	public static final int MAPTILEPROVIDER_SUCCESS_ID = 0;
	public static final int MAPTILEPROVIDER_FAIL_ID = MAPTILEPROVIDER_SUCCESS_ID + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	protected final HashSet<String> mPending = new HashSet<String>();
	protected Context mCtx;
	protected final OSMMapTileFilesystemCache mMapTileFSCache;
	protected final ExecutorService mThreadPool;
	protected OSMMapTileProviderInfo mProviderInfo;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMAbstractMapTileProvider(final Context ctx, final OSMMapTileProviderInfo aProviderInfo, final OSMMapTileFilesystemCache aMapTileFSProvider, final int aThreadPoolSize){
		this.mCtx = ctx;
		this.mMapTileFSCache = aMapTileFSProvider;
		this.mProviderInfo = aProviderInfo;
		this.mThreadPool = new ThreadPoolExecutor(aThreadPoolSize, aThreadPoolSize,
				60L, TimeUnit.SECONDS,
				new PriorityBlockingQueue<Runnable>());
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setProviderInfo(final OSMMapTileProviderInfo pTileProviderInfo){
		this.mProviderInfo = pTileProviderInfo;
	}

	public boolean isPending(final String formattedTileURLString) {
		return this.mPending.contains(formattedTileURLString);
	}

	// ===========================================================
	// Methods from/for SuperClass/Interfaces
	// ===========================================================

	/**
	 * TODO Update comment
	 * @return <code>false</code>, when MapTile is already pending for download. <code>false</code> otherwise.
	 */
	public abstract boolean requestMapTileAsync(final OSMTileInfo aTileInfo, final String aRawTileURLString, final String aSaveableURLString, final Handler callback);

	public void release() {
		this.mThreadPool.shutdownNow();
		this.mCtx = null;
	}

	// ===========================================================
	// Methods
	// ===========================================================

}