// Created by plusminus on 19:38:55 - 07.08.2008
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.adt.UnitSystem;
import org.andnav2.adt.UnitSystem.DistanceStringReturnValue;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingsRouteViewing extends AndNavBaseActivity{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private CheckBox mChkAutozoomEnable;
	private CheckBox mChkSnapToRouteEnable;
	private Spinner mSpinMaxZoomlevel;
	private Spinner mSpinSnapToRouteRadius;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);

		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_routeviewing);

		this.mSpinMaxZoomlevel = (Spinner)this.findViewById(R.id.spin_settings_routeviewing_maxlevel);
		initAutoZoomMaxLevelSpinner();

		this.mSpinSnapToRouteRadius = (Spinner)this.findViewById(R.id.spin_settings_routeviewing_snaptoroute_radius);
		initSnapToRouteSpinner();

		this.mChkAutozoomEnable = (CheckBox)this.findViewById(R.id.chk_settings_routeviewing_enable);
		this.mChkSnapToRouteEnable = (CheckBox)this.findViewById(R.id.chk_settings_routeviewing_snaptoroute_enable);

		this.applyTopButtonListeners();
		this.applyViewListeners();

		this.loadSavedFlagsToViews();
	}

	private void initSnapToRouteSpinner() {
		final int[] valDist;
		final UnitSystem us = Preferences.getUnitSystem(this);
		switch(us){
			case IMPERIAL:
				valDist = getResources().getIntArray(R.array.settings_snaptoroute_radius_imperial);
				break;
			case METRIC:
			default:
				valDist = getResources().getIntArray(R.array.settings_snaptoroute_radius_metric);
		}

		final String[] valStr = new String[valDist.length];

		for (int i = 0; i < valDist.length; i++){
			final int cur = valDist[i];
			final DistanceStringReturnValue distStringParts = us.getDistanceString(cur);
			valStr[i] = distStringParts.LENGTH_STRING + " " + getString(distStringParts.UNIT_RESID);
		}

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valStr);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.mSpinSnapToRouteRadius.setAdapter(adapter);

		this.mSpinSnapToRouteRadius.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id){
				Preferences.saveSnapToRouteRadius(SettingsRouteViewing.this, position);
			}

			public void onNothingSelected(final AdapterView<?> arg0) {

			}
		});
	}

	private void initAutoZoomMaxLevelSpinner() {
		final String[] zoomLevelsRaw = getResources().getStringArray(R.array.preloader_rectangle_zoomlevels);
		final String[] zoomLevelStrings = new String[zoomLevelsRaw.length];
		for(int i = 0; i < zoomLevelStrings.length; i++) {
			zoomLevelStrings[i] = (zoomLevelsRaw[i] != null) ? zoomLevelsRaw[i] : "" + i;
		}

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, zoomLevelStrings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.mSpinMaxZoomlevel.setAdapter(adapter);

		this.mSpinMaxZoomlevel.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id){
				Preferences.saveAutoZoomMaxLevel(SettingsRouteViewing.this, position);
			}

			public void onNothingSelected(final AdapterView<?> arg0) {

			}
		});
	}

	private void applyTopButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_routeviewing_close)){
			@Override
			public void onBoth(final View arg0, final boolean focused) {
				if(SettingsRouteViewing.super.mMenuVoiceEnabled && focused) {
					MediaPlayer.create(SettingsRouteViewing.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				SettingsRouteViewing.this.finish();
			}
		};
	}

	private void applyViewListeners() {
		{
			this.mChkAutozoomEnable.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				public void onCheckedChanged(final CompoundButton b, final boolean isChecked) {
					Preferences.saveAutoZoomEnabled(SettingsRouteViewing.this, isChecked);
				}
			});
		}
		{
			this.mChkSnapToRouteEnable.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				public void onCheckedChanged(final CompoundButton b, final boolean isChecked) {
					Preferences.saveSnapToRoute(SettingsRouteViewing.this, isChecked);
				}
			});
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private void loadSavedFlagsToViews() {
		this.mChkAutozoomEnable.setChecked(Preferences.getAutoZoomEnabled(this));
		this.mChkSnapToRouteEnable.setChecked(Preferences.getSnapToRoute(this));

		final int maxLevel = Math.min(Preferences.getAutoZoomMaxLevel(this), this.mSpinMaxZoomlevel.getCount() - 1);
		this.mSpinMaxZoomlevel.setSelection(maxLevel);

		final int snapDistIndex = Math.min(Preferences.getSnapToRouteRadiusIndex(this), this.mSpinSnapToRouteRadius.getCount() - 1);
		this.mSpinSnapToRouteRadius.setSelection(snapDistIndex);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
