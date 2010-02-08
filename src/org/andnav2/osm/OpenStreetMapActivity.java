// Created by plusminus on 00:14:42 - 02.10.2008
package org.andnav2.osm;

import org.andnav2.adt.AndNavLocation;
import org.andnav2.loc.AbstractAndNavLocationProvider;
import org.andnav2.loc.NetworkFallbackLocationProvider;
import org.andnav2.loc.AbstractAndNavLocationProvider.AndNavLocationCallback;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.api.traces.TraceManager;
import org.andnav2.osm.api.traces.util.RouteRecorder;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.osm.views.OSMMapView;
import org.andnav2.osm.views.util.constants.OSMMapViewConstants;
import org.andnav2.ui.common.DataStateChangedListener;
import org.andnav2.ui.common.MyDataStateChangedWatcher;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

/**
 * Baseclass for Activities who want to contribute to the OpenStreetMap Project.
 * @author Nicolas Gramlich
 *
 */
public abstract class OpenStreetMapActivity extends Activity implements DataStateChangedListener, OSMConstants, OSMMapViewConstants, AndNavLocationCallback {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected AbstractAndNavLocationProvider mLocationProvider;

	protected OSMMapView mOSMapView;

	private final RouteRecorder mRouteRecorder = new RouteRecorder();

	private boolean mDoGPSRecordingAndContributing;

	private MyDataStateChangedWatcher mDscw;

	private final int mDataConnectionStrength = 5;

	private boolean mTreatVolumeKeysAsZoom = false;

	/** When the Map-Activity gets stopped, it writes the last location to the preferences.
	 * This location gets remembered in onCreate(). */
	private GeoPoint mRememberedMapCenterFromLastSession;

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * Calls <code>onCreate(final Bundle savedInstanceState, final boolean pDoGPSRecordingAndContributing)</code> with <code>pDoGPSRecordingAndContributing == true</code>.<br/>
	 * That means it automatically contributes to the OpenStreetMap Project in the background.
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		onCreate(savedInstanceState, true, true);
	}

	/**
	 * Called when the activity is first created. Registers LocationListener.
	 * @param savedInstanceState
	 * @param pDoGPSRecordingAndContributing If <code>true</code>, it automatically contributes to the OpenStreetMap Project in the background.
	 * @param pShowTitleBarInMap <code>true</code> if the title bar should remain visible.
	 */
	protected void onCreate(final Bundle savedInstanceState, final boolean pDoGPSRecordingAndContributing, final boolean pShowTitleBarInMap) {
		super.onCreate(savedInstanceState);

		//		this.mLocationProvider = new SkyhookAndNavLocationProvider(this, this);
		//		this.mLocationProvider = new DefaultLocationProvider(this, this);
		this.mLocationProvider = new NetworkFallbackLocationProvider(this, this);

		final String gpString = getPreferences(Context.MODE_PRIVATE).getString(PREF_MAPCENTER_ID, null);
		this.mRememberedMapCenterFromLastSession = (gpString == null) ? null : GeoPoint.fromIntString(gpString);

		if(pDoGPSRecordingAndContributing) {
			this.enableDoGPSRecordingAndContributing();
		} else {
			this.disableDoGPSRecordingAndContributing(false);
		}

		/* The app-title-bar just takes some pixels away without being useful. */
		if(!pShowTitleBarInMap) {
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}

		this.mLocationProvider.onCreate();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public RouteRecorder getRouteRecorder() {
		return this.mRouteRecorder;
	}

	/** Offers the Height of the MapView to the Overlay. */
	public int getMapViewHeight() {
		return this.mOSMapView.getHeight();
	}

	/** Offers the Height of the MapView to the Overlay. */
	public int getMapViewWidth() {
		return this.mOSMapView.getWidth();
	}

	/** Offers the current GPS-position to the Overlay. */
	public AbstractAndNavLocationProvider getAndNavLocationProvider() {
		return this.mLocationProvider;
	}

	/** Offers the current GPS-position to the Overlay as a mapPoint.
	 * Wraps around the mLocationProvider, adding a  */
	public GeoPoint getLastKnownLocation(final boolean pUseRemembered) {
		if(this.mLocationProvider.hasLastKnownLocation()) {
			return this.mLocationProvider.getLastKnownLocation();
		} else if(pUseRemembered) {
			return this.mRememberedMapCenterFromLastSession;
		} else {
			return null;
		}
	}

	public int getDataConnectionStrength(){
		return this.mDataConnectionStrength;
	}

	/**
	 * Default: FALSE
	 * @param b
	 */
	public void setTreatVolumeKeysAsZoom(final boolean b){
		this.mTreatVolumeKeysAsZoom = b;
	}

	/**
	 * Default: FALSE
	 * @param b
	 */
	public boolean isTreatVolumeKeysAsZoom(){
		return this.mTreatVolumeKeysAsZoom;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onLowMemory() {
		Log.d(DEBUGTAG, "[onLowMemory] FreeMemory: " + Runtime.getRuntime().freeMemory());
		this.mOSMapView.getMapTileManager().onLowMemory();
		super.onLowMemory();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.mLocationProvider.onResume();
		this.onResumeForDataStateChangedListener();
	}

	public void onDataStateChanged(final int strength) {
		// TODO
	}

	protected abstract void onLocationChanged(final AndNavLocation pLocation);
	protected abstract void onLocationLost(final AndNavLocation pLocation);

	public void fireLocationChanged(final AndNavLocation pLocation) {
		if(OpenStreetMapActivity.this.mDoGPSRecordingAndContributing) {
			OpenStreetMapActivity.this.mRouteRecorder.add(pLocation);
		}

		//		Log.d(DEBUGTAG, "[onLocationChanged] FreeMemory: " + Runtime.getRuntime().freeMemory());
		onLocationChanged(pLocation);
	}

	public void fireLocationLost(final AndNavLocation pLocation) {
		// TODO Vllt neues segment im recorder?
		onLocationLost(pLocation);
	}

	@Override
	protected void onStop() {
		super.onStop();
		final GeoPoint gp = getLastKnownLocation(true);
		if(gp != null && Math.abs(gp.getLatitudeE6()) > 100 && Math.abs(gp.getLongitudeE6()) > 100){
			getPreferences(Context.MODE_PRIVATE).edit().putString(PREF_MAPCENTER_ID, gp.toString()).commit();
		}
		this.mLocationProvider.onStop();
		this.onResumeForDataStateChangedListener();
	}

	/**
	 * Called when activity is destroyed. Unregisters LocationListener.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.mLocationProvider.onDestroy();
		if(this.mDoGPSRecordingAndContributing){
			TraceManager.contributeAsync(this, this.mRouteRecorder.getRecordedGeoPoints());
		}

		this.mOSMapView.release();
	}

	/**
	 * Release resources, to free memory.
	 * Especially set all references (also static!!) to {@link Context}s and {@link Drawable} null.
	 * {@link Bitmap}s can be recycle()'d.
	 */
	protected abstract void release();

	/** Make sure to stop the animation when we're no longer on screen,
	 * failing to do so will cause a lot of unnecessary cpu-usage! */
	@Override
	protected void onPause() {
		this.onPauseForDataStateChangedListener();
		super.onPause();
	}

	public void onPauseForDataStateChangedListener() {
		this.mDscw.unregister();
	}

	public void onResumeForDataStateChangedListener() {
		/** Initiates the local field <code>dsir</code> a
		 * DataStateChangedWatcher to notify this class
		 * on changes to the Connection-State... */
		this.mDscw = new MyDataStateChangedWatcher(this, this);
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		switch(keyCode){
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				if(this.mTreatVolumeKeysAsZoom){
					this.mOSMapView.zoomIn();
					return true;
				}
				break;
			case KeyEvent.KEYCODE_VOLUME_UP:
				if(this.mTreatVolumeKeysAsZoom){
					this.mOSMapView.zoomOut();
					return true;
				}
				break;
			case KeyEvent.KEYCODE_I:
				// Zooming In
				this.mOSMapView.zoomIn();
				return true;
			case KeyEvent.KEYCODE_O:
				// Zooming Out
				this.mOSMapView.zoomOut();
				return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void openMenu(){
		this.getWindow().openPanel(Window.FEATURE_OPTIONS_PANEL, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MENU));
	}

	public void enableDoGPSRecordingAndContributing(){
		/* If already true, return. */
		if(this.mDoGPSRecordingAndContributing) {
			return;
		}

		this.mDoGPSRecordingAndContributing = true;
	}

	public void disableDoGPSRecordingAndContributing(final boolean pContributdeCurrentRoute){
		/* If already false, return. */
		if(!this.mDoGPSRecordingAndContributing) {
			return;
		}

		if(pContributdeCurrentRoute){
			TraceManager.contributeAsync(this, this.mRouteRecorder.getRecordedGeoPoints());
		}

		/* 'Clear' the recordedGeopoints.
		 * this.mRouteRecorder.getRecordedGeoPoints().clear();
		 * is not possible, as the previous 'contributeAsync' is asynchronous. */
		this.mRouteRecorder.newRecordedGeoPoints();

		this.mDoGPSRecordingAndContributing = false;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
