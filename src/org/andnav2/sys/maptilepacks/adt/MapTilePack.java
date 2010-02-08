// Created by plusminus on 11:04:35 PM - Mar 10, 2009
package org.andnav2.sys.maptilepacks.adt;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.util.TimeUtils;


public class MapTilePack {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final long mStartTimestamp;
	private final String mName;
	private final int mMaxZoom;
	private final String mDownloadURL;
	private final String mScreenshotURL;
	private final BoundingBoxE6 mBBE6;
	private final int mDownloadSizeMB;

	// ===========================================================
	// Constructors
	// ===========================================================

	public MapTilePack(final String pName, final String pTimeStampString, final int pMaxZoom, final String pDownloadURL, final String pScreenshotURL, final int pDownloadSizeMB, final BoundingBoxE6 pBBE6) {
		this(pName, TimeUtils.durationTimeString(pTimeStampString), pMaxZoom, pDownloadURL, pScreenshotURL, pDownloadSizeMB, pBBE6);
	}

	public MapTilePack(final String pName, final long pStartTimestamp, final int pMaxZoom, final String pDownloadURL, final String pScreenshotURL, final int pDownloadSizeMB, final BoundingBoxE6 pBBE6) {
		this.mName = pName;
		this.mStartTimestamp = pStartTimestamp;
		this.mMaxZoom = pMaxZoom;
		this.mDownloadURL = pDownloadURL;
		this.mScreenshotURL = pScreenshotURL;
		this.mBBE6 = pBBE6;
		this.mDownloadSizeMB = pDownloadSizeMB;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getName() {
		return this.mName;
	}

	public int getMaxZoom() {
		return this.mMaxZoom;
	}

	public String getDownloadURL() {
		return this.mDownloadURL;
	}

	public String getScreenshotURL() {
		return this.mScreenshotURL;
	}

	public long getStartTimestamp() {
		return this.mStartTimestamp;
	}

	public BoundingBoxE6 getBBE6() {
		return this.mBBE6;
	}

	public int getDownloadSizeMB() {
		return this.mDownloadSizeMB;
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

