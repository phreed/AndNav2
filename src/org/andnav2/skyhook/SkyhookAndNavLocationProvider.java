// Created by plusminus on 23:19:34 - 02.02.2009
package org.andnav2.skyhook;

import java.io.File;

import org.andnav2.adt.AndNavLocation;
import org.andnav2.loc.AbstractAndNavLocationProvider;
import org.andnav2.osm.util.Util;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.preferences.Preferences;
import org.andnav2.util.constants.Constants;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.skyhookwireless.wps.TilingListener;
import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSPeriodicLocationCallback;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.XPS;


public class SkyhookAndNavLocationProvider extends AbstractAndNavLocationProvider implements Constants {
	// ===========================================================
	// Constants
	// ===========================================================

	private final int TIME_BETWEEN_UPDATES = 0; // in seconds
	private final int ACCURACY = 0; // in meters

	private static final int WPSLOCATION_MESSAGE = 1;
	private static final int ERROR_MESSAGE = WPSLOCATION_MESSAGE + 1;
	private static final int DONE_MESSAGE = ERROR_MESSAGE + 1;

	public static final String PROVIDER_SKYHOOK = "skyhook";

	// ===========================================================
	// Fields
	// ===========================================================

	private final XPS mXPS;
	private final WPSAuthentication mWPSAuthentication;

	private Handler mXPSHandler;

	private final MyLocationCallback mLocationCallback = new MyLocationCallback();
	protected AndNavLocation mLastKnownExactLocation;

	// ===========================================================
	// Constructors
	// ===========================================================

	public SkyhookAndNavLocationProvider(final Context ctx, final AndNavLocationCallback callback) {
		super(callback);

		this.mWPSAuthentication = Preferences.getSkyHookWPSAuthentication(ctx, true);
		this.mXPS = new XPS(ctx);
		this.mXPS.setLocalFilePaths(null);

		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			final String tilingPath = Util.getAndNavExternalStoragePath() + OSMConstants.SDCARD_SKYHOOKCACHE_PATH;
			new File(tilingPath).mkdirs();
			this.mXPS.setTiling(tilingPath, 200 * 1024, 1000 * 1024, new TilingListener(){
				public WPSContinuation tilingCallback(final int tileNumber, final int tileTotal) {
					return WPSContinuation.WPS_CONTINUE;
				}
			});
		}else{
			this.mXPS.setTiling("", 0, 0, null);
		}


		initXPSHandler();
	}

	private void initXPSHandler() {
		this.mXPSHandler =  new Handler() {
			@Override
			public void handleMessage(final Message msg) {
				switch (msg.what) {
					case WPSLOCATION_MESSAGE:
						final WPSLocation wpsLoc = (WPSLocation) msg.obj;
						if (wpsLoc != null){
							SkyhookAndNavLocationProvider.super.onPublishLocation(convert(wpsLoc));
						}else{
							SkyhookAndNavLocationProvider.super.onLocationLost();
						}
						return;
					case ERROR_MESSAGE:
						// TODO Nothing for now
						return;
					case DONE_MESSAGE:
						return;
				}
			}
		};
	}


	// ===========================================================
	// Getter & Setter
	// ===========================================================

	private AndNavLocation convert(final WPSLocation aLocation){
		if(aLocation == null) {
			return null;
		}

		return new AndNavLocation(PROVIDER_SKYHOOK,
				(int)(aLocation.getLatitude() * 1E6),
				(int)(aLocation.getLongitude() * 1E6),
				(int)aLocation.getHPE(),
				NOT_SET,
				aLocation.getNAP(),
				(float)aLocation.getBearing(),
				NOT_SET,
				System.currentTimeMillis(),
				(float)aLocation.getSpeed());
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onCreate() {

	}

	@Override
	public void onResume() {
		this.mXPS.getXPSLocation(this.mWPSAuthentication, this.TIME_BETWEEN_UPDATES, this.ACCURACY, this.mLocationCallback);
	}

	@Override
	public void onStop() {
		this.mXPS.abort();
	}

	@Override
	public void onDestroy() {

	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	/**
	 * @author plusminus
	 */
	private class MyLocationCallback implements WPSPeriodicLocationCallback {
		public WPSContinuation handleWPSPeriodicLocation(final WPSLocation location) {
			SkyhookAndNavLocationProvider.this.mXPSHandler.sendMessage(SkyhookAndNavLocationProvider.this.mXPSHandler.obtainMessage(WPSLOCATION_MESSAGE, location));
			return WPSContinuation.WPS_CONTINUE;
		}
		
		public void done() {
			// tell the UI thread to re-enable the buttons
			SkyhookAndNavLocationProvider.this.mXPSHandler.sendMessage(SkyhookAndNavLocationProvider.this.mXPSHandler.obtainMessage(DONE_MESSAGE));
		}

		public WPSContinuation handleError(final WPSReturnCode error) {
			switch(error){
				//					return WPSContinuation.WPS_STOP; // TODO Temporary solution until WiFi-Issues are solved.
				case WPS_ERROR_NO_WIFI_IN_RANGE:
				case WPS_ERROR_WIFI_NOT_AVAILABLE:
				case WPS_ERROR:
				case WPS_ERROR_LOCATION_CANNOT_BE_DETERMINED:
				case WPS_ERROR_SERVER_UNAVAILABLE:
				case WPS_ERROR_UNAUTHORIZED:
				case WPS_OK:
				default:
					Log.d(DEBUGTAG, "ERROR Received: " + error.name());
					return WPSContinuation.WPS_CONTINUE;
			}
		}
	}
}
