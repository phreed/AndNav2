// Created by plusminus on 21:31:36 - 25.09.2008
package org.andnav2.osm.views.tiles.downloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.osm.views.tiles.OSMAbstractMapTileProvider;
import org.andnav2.osm.views.tiles.OSMMapTileProviderInfo;
import org.andnav2.osm.views.tiles.adt.OSMTileInfo;
import org.andnav2.osm.views.tiles.caching.OSMMapTileFilesystemCache;
import org.andnav2.osm.views.util.StreamUtils;
import org.andnav2.osm.views.util.constants.OSMMapViewConstants;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class OSMMapTileDownloadProvider extends OSMAbstractMapTileProvider implements OSMConstants, OSMMapViewConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapTileDownloadProvider(final Context ctx, final OSMMapTileProviderInfo pTileProviderInfo, final OSMMapTileFilesystemCache pMapTileFSProvider){
		super(ctx, pTileProviderInfo, pMapTileFSProvider, 4);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

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
		this.mThreadPool.execute(new DownloadRunner(pTileInfo, aRawTileURLString, callback, aSaveableURLString));
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class DownloadRunner implements Runnable, Comparable<DownloadRunner>{

		private final OSMTileInfo mTileInfo;
		private final String mSaveableURLString;
		private final String mRawURLString;
		private final Handler mCallback;
		private final long mTimeStamp = System.currentTimeMillis();

		public DownloadRunner(final OSMTileInfo pTileInfo, final String aRawTileURLString, final Handler aCallback, final String aSaveableURLString) {
			this.mTileInfo = pTileInfo;
			this.mRawURLString = aRawTileURLString;
			this.mCallback = aCallback;
			this.mSaveableURLString = aSaveableURLString;
		}

		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_LESS_FAVORABLE);

			InputStream in = null;
			OutputStream out = null;

			try {
				if(DEBUGMODE) {
					Log.i(DEBUGTAG, "Downloading Maptile from url: " + this.mRawURLString);
				}

				in = new BufferedInputStream(new URL(this.mRawURLString).openStream(), StreamUtils.IO_BUFFER_SIZE);

				final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
				out = new BufferedOutputStream(dataStream, StreamUtils.IO_BUFFER_SIZE);
				StreamUtils.copy(in, out);
				out.flush();

				final byte[] data = dataStream.toByteArray();
				if(data.length < 10){
					Log.w(DEBUGTAG, "MapTile to small: " + data.length + " Bytes");
				}else{
					OSMMapTileDownloadProvider.this.mMapTileFSCache.saveFile(this.mSaveableURLString, data);

					if(DEBUGMODE) {
						Log.i(DEBUGTAG, "Maptile saved to: " + this.mRawURLString);
					}
				}

				OSMMapTileDownloadProvider.this.mPending.remove(this.mRawURLString);

				final Message successMessage = Message.obtain(this.mCallback, MAPTILEPROVIDER_SUCCESS_ID);
				successMessage.arg1 = this.mTileInfo.x;
				successMessage.arg2 = this.mTileInfo.y;
				successMessage.sendToTarget();
			} catch (final Exception e) {
				OSMMapTileDownloadProvider.this.mPending.remove(this.mRawURLString);

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

		public int compareTo(final DownloadRunner another) {
			return -(int)(this.mTimeStamp - another.mTimeStamp);
		}
	}
}
