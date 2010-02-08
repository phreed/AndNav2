// Created by plusminus on 01:08:10 - 12.10.2008
package org.andnav2.ui.map;

import java.util.List;

import org.andnav2.R;
import org.andnav2.osm.OpenStreetMapActivity;
import org.andnav2.osm.views.overlay.OSMMapViewOverlay;
import org.andnav2.osm.views.overlay.OSMMapViewSimpleTraceOverlay;
import org.andnav2.preferences.PreferenceConstants;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.map.overlay.ColorSchemeOverlay;
import org.andnav2.util.BetaExpirationBlocker;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;


public abstract class OpenStreetMapAndNavBaseActivity extends OpenStreetMapActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int MENU_SHOWTRACE_ID = 100;
	private static final int MENU_SUBMITTRACE_ID = MENU_SHOWTRACE_ID + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	protected boolean mMenuVoiceEnabled = false;
	//
	//	protected boolean mIsLandScapeMode = false;

	protected ColorSchemeOverlay mColorSchemeOverlay;
	protected OSMMapViewSimpleTraceOverlay mSimpleTraceOverlay;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		this.onCreate(savedInstanceState, true, true);
	}

	/** Called when the activity is first created.
	 * @param savedInstanceState
	 * @param pDoGPSRecordingAndContributing If <code>true</code>, it automatically contributes to the OpenStreetMap Project in the background.
	 * @param pShowTitleBarInMap <code>true</code> if the title bar should remain visible.
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState, final boolean pDoGPSRecordingAndContributing, final boolean pShowTitleBarInMap) {
		super.onCreate(savedInstanceState, pDoGPSRecordingAndContributing, pShowTitleBarInMap);

//		if(BetaExpirationBlocker.finishWhenExpired(this)) {
//			return;
//		}

		final boolean showTitleBarInMap = Preferences.getShowTitleBarInMap(this);
		/* The app-title-bar just takes some pixels away without being useful. */
		if(!showTitleBarInMap) {
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}

		this.onSetupContentView();

		final List<OSMMapViewOverlay> overlays = this.mOSMapView.getOverlays();

		this.mSimpleTraceOverlay = new OSMMapViewSimpleTraceOverlay(this, super.getRouteRecorder().getRecordedGeoPoints(), PreferenceConstants.PREF_DISPLAYQUALITY_HIGH);
		this.mSimpleTraceOverlay.setVisible(false);
		overlays.add(this.mSimpleTraceOverlay);

		this.mColorSchemeOverlay = new ColorSchemeOverlay();
		overlays.add(this.mColorSchemeOverlay);

		this.mMenuVoiceEnabled = Preferences.getMenuVoiceEnabled(this);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onResume() {
		this.mColorSchemeOverlay.setColorScheme(Preferences.getSharedThemeID(this));
		this.setRequestedOrientation(Preferences.getRequestedScreenOrientation(this));
		super.onResume();
	}

	protected abstract void onSetupContentView();

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		int pos = 100;
		{ // Trace-Item
			menu.add(pos, MENU_SHOWTRACE_ID, pos, getString(R.string.maps_menu_trace_show))
			.setIcon(R.drawable.trace)
			.setAlphabeticShortcut('t');
			pos++;
		}

		{ // Submit-Item
			menu.add(pos, MENU_SUBMITTRACE_ID, pos, getString(R.string.maps_menu_trace_submit))
			.setIcon(R.drawable.trace_submit)
			.setAlphabeticShortcut('s');
			pos++;
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch(item.getItemId()){
			case MENU_SHOWTRACE_ID:
				this.mSimpleTraceOverlay.setVisible(!this.mSimpleTraceOverlay.isVisible());
				return true;
			case MENU_SUBMITTRACE_ID:
				disableDoGPSRecordingAndContributing(true);
				enableDoGPSRecordingAndContributing();
				return true;
			default:
				return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		/* Refresh Show/Hide Trace item. */
		menu.findItem(MENU_SHOWTRACE_ID).setTitle((this.mSimpleTraceOverlay.isVisible()) ? R.string.maps_menu_trace_hide : R.string.maps_menu_trace_show);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void disableDoGPSRecordingAndContributing(final boolean pContributdeCurrentRoute) {
		super.disableDoGPSRecordingAndContributing(pContributdeCurrentRoute);

		/* Make the Overlay use the new Polyline. */
		this.mSimpleTraceOverlay.setPolyline(super.getRouteRecorder().getRecordedGeoPoints());
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
