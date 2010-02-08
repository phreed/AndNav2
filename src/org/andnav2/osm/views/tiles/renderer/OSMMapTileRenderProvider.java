// Created by plusminus on 21:31:36 - 25.09.2008
package org.andnav2.osm.views.tiles.renderer;

import java.io.InputStream;
import java.io.OutputStream;

import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.osm.views.tiles.OSMAbstractMapTileProvider;
import org.andnav2.osm.views.tiles.OSMMapTileProviderInfo;
import org.andnav2.osm.views.tiles.adt.OSMTileInfo;
import org.andnav2.osm.views.tiles.caching.OSMMapTileFilesystemCache;
import org.andnav2.osm.views.util.StreamUtils;
import org.andnav2.osm.views.util.constants.OSMMapViewConstants;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class OSMMapTileRenderProvider extends OSMAbstractMapTileProvider implements OSMConstants, OSMMapViewConstants {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private IOSMRenderer mRenderer;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapTileRenderProvider(final Context ctx, final OSMMapTileProviderInfo pTileProviderInfo, final OSMMapTileFilesystemCache pMapTileFSProvider){
		super(ctx, pTileProviderInfo, pMapTileFSProvider, 1);

		/* TODO Extract the correct renderer out of */
		this.mRenderer = RendererRegistry.resolve(ctx, pTileProviderInfo);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void release() {
		this.mRenderer.release();
	}

	@Override
	public void setProviderInfo(final OSMMapTileProviderInfo pTileProviderInfo) {
		if(this.mRenderer != null) {
			this.mRenderer.release();
		}

		this.mRenderer = RendererRegistry.resolve(this.mCtx, pTileProviderInfo);

		super.setProviderInfo(pTileProviderInfo);
	}

	@Override
	public boolean requestMapTileAsync(final OSMTileInfo pTileInfo, final String aRawTileURLString, final String aSaveableURLString, final Handler callback) {
		if(this.mPending.contains(aRawTileURLString)) {
			return false;
		}

		this.mPending.add(aRawTileURLString);
		getRemoteImageAsync(pTileInfo, aRawTileURLString, aSaveableURLString, callback);

		return true;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	/** Sets the Child-ImageView of this to the URL passed. */
	protected void getRemoteImageAsync(final OSMTileInfo pTileInfo, final String aRawTileURLString, final String aSaveableURLString, final Handler callback) {
		this.mThreadPool.execute(new RenderRunner(pTileInfo, aRawTileURLString, callback, aSaveableURLString));
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class RenderRunner implements Runnable, Comparable<RenderRunner>{

		private final OSMTileInfo mTileInfo;
		private final String mSaveableURLString;
		private final String mRawURLString;
		private final Handler mCallback;
		private final long mTimeStamp = System.currentTimeMillis();

		public RenderRunner(final OSMTileInfo pTileInfo, final String aRawTileURLString, final Handler aCallback, final String aSaveableURLString) {
			this.mTileInfo = pTileInfo;
			this.mRawURLString = aRawTileURLString;
			this.mCallback = aCallback;
			this.mSaveableURLString = aSaveableURLString;
		}

		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_LESS_FAVORABLE);

			final InputStream in = null;
			final OutputStream out = null;

			try {
				if(DEBUGMODE) {
					Log.i(DEBUGTAG, "Downloading Maptile from url: " + this.mRawURLString);
				}

				final Bitmap bmp = OSMMapTileRenderProvider.this.mRenderer.renderTile(this.mTileInfo);

				OSMMapTileRenderProvider.this.mMapTileFSCache.saveFile(this.mSaveableURLString, bmp);
				if(DEBUGMODE) {
					Log.i(DEBUGTAG, "Maptile saved to: " + this.mRawURLString);
				}

				OSMMapTileRenderProvider.this.mPending.remove(this.mRawURLString);

				final Message successMessage = Message.obtain(this.mCallback, MAPTILEPROVIDER_SUCCESS_ID);
				successMessage.arg1 = this.mTileInfo.x;
				successMessage.arg2 = this.mTileInfo.y;
				successMessage.sendToTarget();
			} catch (final Exception e) {
				OSMMapTileRenderProvider.this.mPending.remove(this.mRawURLString);

				final Message failMessage = Message.obtain(this.mCallback, MAPTILEPROVIDER_FAIL_ID);
				failMessage.sendToTarget();
				if(DEBUGMODE) {
					Log.e(DEBUGTAG, "Error Downloading MapTile. Exception: " + e.getClass().getSimpleName(), e);
				}

			} finally {
				StreamUtils.closeStream(in);
				StreamUtils.closeStream(out);
			}
		}

		public int compareTo(final RenderRunner another) {
			return -(int)(this.mTimeStamp - another.mTimeStamp);
		}
	}
}

