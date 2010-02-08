// Created by plusminus on 21:46:22 - 25.09.2008
package org.andnav2.osm.views.tiles;

import org.andnav2.R;
import org.andnav2.osm.exceptions.ExternalStorageNotMountedException;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.osm.views.tiles.adt.OSMTileInfo;
import org.andnav2.osm.views.tiles.caching.OSMMapTileFilesystemCache;
import org.andnav2.osm.views.tiles.caching.OSMMapTileMemoryCache;
import org.andnav2.osm.views.tiles.caching.OSMMapTileFilesystemCache.StoragePolicy;
import org.andnav2.osm.views.tiles.downloader.OSMMapTileDownloadProvider;
import org.andnav2.osm.views.tiles.renderer.OSMMapTileRenderProvider;
import org.andnav2.osm.views.util.constants.OSMMapViewConstants;
import org.andnav2.preferences.Preferences;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class OSMMapTileManager implements OSMConstants, OSMMapViewConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final Bitmap MAPTILE_DOWNLOADING;
	private final Bitmap MAPTILE_LOADING;
	private final Bitmap MAPTILE_GENERATING;

	private Context mCtx;
	private OSMMapTileMemoryCache mMemoryTileCache;
	private OSMMapTileFilesystemCache mFSTileCache;
	private OSMAbstractMapTileProvider mTileProvider;
	private OSMMapTileProviderInfo mProviderInfo;
	private final Handler mLoadCallbackHandler = new LoadCallbackHandler();
	private final Handler mLoadFinishedListenerHander;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapTileManager(final Context ctx, final OSMMapTileProviderInfo pProviderInfo, final Handler aLoadFinishedListener) {
		this.mCtx = ctx;
		this.MAPTILE_LOADING = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.maptile_loading);
		this.MAPTILE_DOWNLOADING = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.maptile_downloading);
		this.MAPTILE_GENERATING = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.maptile_generating);
		this.mMemoryTileCache = new OSMMapTileMemoryCache();
		try {
			this.mFSTileCache = new OSMMapTileFilesystemCache(ctx, Preferences.getFilesystemCachePolicy(ctx), Preferences.getMaxCacheSizeUpperLimit(ctx) * 1024 * 1024, this.mMemoryTileCache, pProviderInfo);
		} catch (final ExternalStorageNotMountedException e) {
			Toast.makeText(ctx, "No external media attached, caching to internal ROM!", Toast.LENGTH_LONG).show(); // TODO i18n
			Log.e(DEBUGTAG, "StoragePolicy was EXTERNAL, but no External Media attached!", e);
			try {
				this.mFSTileCache = new OSMMapTileFilesystemCache(ctx, StoragePolicy.INTERNALROM, Preferences.getMaxCacheSizeUpperLimit(ctx) * 1024 * 1024, this.mMemoryTileCache, pProviderInfo);
			} catch (final ExternalStorageNotMountedException e1) {
				// Can not happen!
			}
		}

		switch (pProviderInfo.PROVIDER_TYPE) {
			case LOCAL_PROVIDER:
				this.mTileProvider = new OSMMapTileRenderProvider(ctx, pProviderInfo, this.mFSTileCache);
				break;
			case DOWNLOAD_PROVIDER:
			default:
				this.mTileProvider = new OSMMapTileDownloadProvider(ctx, pProviderInfo, this.mFSTileCache);
				break;
		}

		this.mProviderInfo = pProviderInfo;
		this.mLoadFinishedListenerHander = aLoadFinishedListener;
	}


	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * Changes the ProviderInfo of the underlying Provider and the caches.
	 */
	public void setProviderInfo(final OSMMapTileProviderInfo pProviderInfo){
		if(pProviderInfo == this.mProviderInfo) {
			return;
		}

		if(this.mProviderInfo.PROVIDER_TYPE == pProviderInfo.PROVIDER_TYPE){
			this.mTileProvider.setProviderInfo(pProviderInfo);
		}else{ /* PROVIDER_TYPE of the Provider has changed! */
			switch (pProviderInfo.PROVIDER_TYPE) {
				case LOCAL_PROVIDER:
					this.mTileProvider = new OSMMapTileRenderProvider(this.mCtx, pProviderInfo, this.mFSTileCache);
					break;
				case DOWNLOAD_PROVIDER:
				default:
					this.mTileProvider = new OSMMapTileDownloadProvider(this.mCtx, pProviderInfo, this.mFSTileCache);
					break;
			}
		}
		this.mFSTileCache.setProviderInfo(pProviderInfo);
		this.mProviderInfo = pProviderInfo;
	}

	public OSMMapTileFilesystemCache getFileSystemCache(){
		return this.mFSTileCache;
	}

	public void release() {
		this.mMemoryTileCache.release();
		this.mMemoryTileCache = null;
		this.mFSTileCache.release();
		this.mTileProvider.release();
		this.MAPTILE_DOWNLOADING.recycle();
		this.MAPTILE_GENERATING.recycle();
		this.MAPTILE_LOADING.recycle();
		this.mCtx = null;
	}

	public void onLowMemory() {
		this.mMemoryTileCache.onLowMemory();
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * @param coords to be used to construct the uri to the tile. <b>Example:</b> <code>[11187, 17171]</code> will result i.e. in <code>http://c.tile.openstreetmap.org/15/<b>17171/11187</b>.png</code>
	 * @param zoomLevel used together with <code>coords</code> to determine the final url. <b>Example:</b> <code>15</code> will result i.e. in <code>http://c.tile.openstreetmap.org/<b>15</b>/17171/11187.png</code>
	 * @param h the Handler to send a message to success or failure.
	 * @return <code>false</code>, when MapTile is already pending for download or already existing on the FS. <code>false</code> otherwise.
	 */
	public boolean preloadMaptileAsync(final OSMTileInfo aTileInfo, final Handler h){
		final String aRawTileURLString = this.mProviderInfo.getTileURLString(aTileInfo);
		final String aSaveableURLString = this.mProviderInfo.getSaveableTileURLString(aTileInfo, this.mFSTileCache.getStoragePolicy());

		if(this.mFSTileCache.exists(aRawTileURLString, aSaveableURLString)) {
			return false;
		}

		return this.mTileProvider.requestMapTileAsync(aTileInfo, aRawTileURLString, aSaveableURLString, h);
	}

	/**
	 * Get a MapTile, based on <code>coords</code> and <code>zoomLevel</code>. Used the handler passed with the constructor, to inform of successful or failed loadings.
	 * @param coords to be used to construct the uri to the tile. <b>Example:</b> <code>[11187, 17171]</code> will result i.e. in <code>http://c.tile.openstreetmap.org/15/<b>17171/11187</b>.png</code>
	 * @param zoomLevel used together with <code>coords</code> to determine the final url. <b>Example:</b> <code>15</code> will result i.e. in <code>http://c.tile.openstreetmap.org/<b>15</b>/17171/11187.png</code>
	 * @return Either: <ul><li><code>MAPTILE_DOWNLOADING</code> when the tile is currently being downloaded. </li><li><code>MAPTILE_LOADING</code> if the tile is currently being loaded from the FileSystem to the MemoryCache.</li><li> The actual bitmap if already was downloaded and loaded to the MemoryCache.</li></ul>
	 */
	public Bitmap getMapTile(final OSMTileInfo aTileInfo) {
		final String aSaveableTileURLString = this.mProviderInfo.getSaveableTileURLString(aTileInfo, this.mFSTileCache.getStoragePolicy());

		final Bitmap ret = this.mMemoryTileCache.getMapTile(aSaveableTileURLString);
		if(ret != null){
			if(DEBUGMODE) {
				Log.i(DEBUGTAG, "MapTileCache succeded for: " + aSaveableTileURLString);
			}

			if(ret.isRecycled()){
				this.mMemoryTileCache.onRemoveRecycledBitmapFromMemoryCache(aSaveableTileURLString);
			}else{
				return ret;
			}
		}

		final String formattedTileURLStringDirty = aSaveableTileURLString + OSMMapTileMemoryCache.FLAG_DIRTY;
		final Bitmap retDirty = this.mMemoryTileCache.getMapTile(formattedTileURLStringDirty);

		final String aRawTileURLString = this.mProviderInfo.getTileURLString(aTileInfo);

		if(DEBUGMODE) {
			Log.i(DEBUGTAG, "Cache failed, trying from FS.");
		}

		/* Check if file is either being loaded from FS or is pending for download. */
		if(this.mFSTileCache.isPending(aSaveableTileURLString)){
			if(retDirty == null) {
				return this.MAPTILE_LOADING;
			}
		}else if(this.mTileProvider.isPending(aRawTileURLString)){
			if(retDirty == null){
				switch (this.mProviderInfo.PROVIDER_TYPE) {
					case LOCAL_PROVIDER:
						return this.MAPTILE_GENERATING;
					case DOWNLOAD_PROVIDER:
					default:
						return this.MAPTILE_DOWNLOADING;
				}
			}
		}

		final boolean existsInFSAndNowLoading = this.mFSTileCache.loadMapTileToMemCacheAsync(aTileInfo, aSaveableTileURLString, this.mLoadCallbackHandler);
		if(existsInFSAndNowLoading){
			if(retDirty == null) {
				return this.MAPTILE_LOADING;
			}
		}else{
			if(DEBUGMODE) {
				Log.d(DEBUGTAG, "File: '" + aSaveableTileURLString + "' not found in Filesystem");
			}

			/* If possible, try to create a bitmap from the parent and put it to the memcache. */
			if(aTileInfo.zoom > 0 && retDirty == null && !this.mFSTileCache.isPending(formattedTileURLStringDirty)) {
				this.mFSTileCache.tryLoadMapTileByParentToMemCacheAsync(aTileInfo, aSaveableTileURLString, this.mLoadCallbackHandler);
			}
		}
		/* FS did not contain the MapTile, we need to download it asynchronous. */
		if(DEBUGMODE) {
			Log.i(DEBUGTAG, "Requesting Maptile for download.");
		}

		this.mTileProvider.requestMapTileAsync(aTileInfo, aRawTileURLString, aSaveableTileURLString, this.mLoadCallbackHandler);
		if(retDirty == null){
			switch (this.mProviderInfo.PROVIDER_TYPE) {
				case LOCAL_PROVIDER:
					return this.MAPTILE_GENERATING;
				case DOWNLOAD_PROVIDER:
				default:
					return this.MAPTILE_DOWNLOADING;
			}
		}

		return retDirty;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private class LoadCallbackHandler extends Handler{
		@Override
		public void handleMessage(final Message msg) {
			final int what = msg.what;
			switch(what){
				case OSMAbstractMapTileProvider.MAPTILEPROVIDER_SUCCESS_ID:
					if(OSMMapTileManager.this.mLoadFinishedListenerHander != null) {
						OSMMapTileManager.this.mLoadFinishedListenerHander.sendMessage(Message.obtain(msg));
					}
					if(DEBUGMODE) {
						Log.i(DEBUGTAG, "MapTile download success.");
					}
					break;
				case OSMAbstractMapTileProvider.MAPTILEPROVIDER_FAIL_ID:
					if(DEBUGMODE) {
						Log.e(DEBUGTAG, "MapTile download error.");
					}
					break;

				case OSMMapTileFilesystemCache.MAPTILEFSCACHE_SUCCESS_ID:
					if(OSMMapTileManager.this.mLoadFinishedListenerHander != null) {
						OSMMapTileManager.this.mLoadFinishedListenerHander.sendMessage(Message.obtain(msg));
					}
					if(DEBUGMODE) {
						Log.i(DEBUGTAG, "MapTile fs->cache success.");
					}
					break;
				case OSMMapTileFilesystemCache.MAPTILEFSCACHE_FAIL_ID:
					if(DEBUGMODE) {
						Log.e(DEBUGTAG, "MapTile cache error.");
					}
					break;
			}
		}
	}
}
