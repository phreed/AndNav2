package org.andnav2.osm.views.tiles.caching;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.andnav2.osm.exceptions.ExternalStorageNotMountedException;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.osm.views.tiles.OSMMapTileProviderInfo;
import org.andnav2.osm.views.tiles.adt.OSMTileInfo;
import org.andnav2.osm.views.tiles.caching.OSMMapTileFilesystemCache.StoragePolicy;
import org.andnav2.osm.views.util.constants.OSMMapViewConstants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.util.Log;

/**
 * 
 * @author Nicolas Gramlich
 * @since 21:41:58 - 18.07.2009
 */
public abstract class AbstractOSMMapTileFilesystemCache implements OSMConstants, OSMMapViewConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final int MAXIMUM_CACHESIZE = 30 * 1024 * 1024; // 30MB

	public static final int MAPTILEFSCACHE_SUCCESS_ID = 1000;
	public static final int MAPTILEFSCACHE_FAIL_ID = MAPTILEFSCACHE_SUCCESS_ID + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Context mContext;
	protected int mMaxFSCacheByteSize;
	protected int mCurrentFSCacheByteSize;
	protected final ExecutorService mThreadPool = Executors.newCachedThreadPool();
	protected final OSMMapTileMemoryCache mCache;

	protected final HashSet<String> mPending = new HashSet<String>();
	protected final HashSet<String> mPendingDirty = new HashSet<String>();

	protected OSMMapTileProviderInfo mProviderInfo;

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * @param ctx
	 * @param aStoragePolicy
	 * @param aMaxFSCacheByteSize the size of the cached MapTiles will not exceed this size.
	 * @param aCache to load fs-tiles to. Can be null, i.e. to get just the stats
	 * @throws ExternalStorageNotMountedException when Policy is {@link StoragePolicy.EXTERNAL} and it is not mounted.
	 */
	public AbstractOSMMapTileFilesystemCache(final Context ctx, final int aMaxFSCacheByteSize, final OSMMapTileMemoryCache aCache, final OSMMapTileProviderInfo pProviderInfo) throws ExternalStorageNotMountedException {
		this.mContext = ctx;
		
		this.mProviderInfo = pProviderInfo;
		this.mMaxFSCacheByteSize = aMaxFSCacheByteSize;
		this.mCache = aCache;

		if(DEBUGMODE) {
			Log.i(DEBUGTAG, "Currently used cache-size is: " + this.mCurrentFSCacheByteSize + " of " + this.mMaxFSCacheByteSize + " Bytes");
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setProviderInfo(final OSMMapTileProviderInfo pProviderInfo) {
		this.mProviderInfo = pProviderInfo;
	}

	public void release() {
		this.mPending.clear();
		this.mThreadPool.shutdownNow();
		this.mContext = null;
	}

	public boolean isPending(final String aFormattedTileURLString){
		return this.mPending.contains(aFormattedTileURLString);
	}

	public int getCurrentFSCacheByteSize() {
		return this.mCurrentFSCacheByteSize;
	}

	public int getMaxFSCacheByteSize() {
		return this.mMaxFSCacheByteSize;
	}

	public void setMaxFSCacheByteSize(final int aSize) {
		this.mMaxFSCacheByteSize = aSize;
	}

	public abstract boolean exists(final String pRawTileURLString, final String pSaveableURLString);

	/**
	 * 
	 * @param aTileInfo
	 * @param aSaveableTileURLString
	 * @param loadCallbackHandler
	 * @return
	 */
	public abstract boolean tryLoadMapTileByParentToMemCacheAsync(final OSMTileInfo aTileInfo, final String aSaveableTileURLString, final Handler loadCallbackHandler);

	protected abstract int updateCurrentFSCacheByteSize();
	
	/**
	 * 
	 * @param coords
	 * @param aSaveableTileURLString
	 * @param loadCallbackHandler
	 * @return <code>true</code> when file is now loaded. <code>false</code> if file did not exist and should be downloaded/rendered.
	 * @throws FileNotFoundException
	 */
	public abstract boolean loadMapTileToMemCacheAsync(final OSMTileInfo aTileInfo, final String aSaveableTileURLString, final Handler loadCallbackHandler);

	public void saveFile(final String aSaveableURLString, final Bitmap bmp) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream(20000);

		bmp.compress(CompressFormat.PNG, 100, bos);

		bos.flush();
		bos.close();

		saveFile(aSaveableURLString, bos.toByteArray());
	}

	/**
	 * Saves the file based on this instances StoragePolicy
	 * @param aSaveableURLString
	 * @param someData
	 * @throws IOException
	 */
	public abstract void saveFile(final String aSaveableURLString, final byte[] someData) throws IOException;
	
	public void clearCurrentFSCache(){
		cutCurrentFSCacheBy(Integer.MAX_VALUE); // Delete all
	}

	public abstract void cutCurrentFSCacheBy(final int pBytesToCut);

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