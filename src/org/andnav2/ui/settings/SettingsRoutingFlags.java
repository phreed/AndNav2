// Created by plusminus on 19:38:55 - 07.08.2008
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.rs.RoutePreferenceType;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingsRoutingFlags extends AndNavBaseActivity{
	// ===========================================================
	// Constants
	// ===========================================================

	public static final String IS_DIALOG_MODE = "mode";

	private static final int MENU_ALWAYSREMEMBER_ID = Menu.FIRST;

	// ===========================================================
	// Fields
	// ===========================================================

	protected boolean mDialogMode = false;

	protected RadioButton mRadCar;
	protected RadioButton mRadPedestrian;
	protected RadioButton mRadBicycle;
	protected RadioButton mRadShortest;
	protected RadioButton mRadFastest;
	protected CheckBox mChkAvoidHighways;
	protected CheckBox mChkAvoidTolls;
	protected CheckBox mChkAlwaysRemember;
	protected CheckBox mChkRealtimeNavigation;
	protected CheckBox mChkSaveInitialRoute;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);

		final Bundle b = this.getIntent().getExtras();
		if(b != null) {
			this.mDialogMode = b.getBoolean(IS_DIALOG_MODE);
		}

		if(this.mDialogMode){
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.setTheme(android.R.style.Theme_Dialog);
			this.setContentView(R.layout.settings_routingflags_dialog);
			//			this.setTitle(R.string.app_name_settings_routingflags_dialog);
		}else{
			Preferences.applySharedSettings(this);
			this.setContentView(R.layout.settings_routingflags);
			this.setTitle(R.string.app_name_settings_routingflags);
		}

		this.mChkAvoidHighways = (CheckBox) this.findViewById(R.id.chk_settings_routingflags_avoidhighways);
		this.mChkAvoidTolls = (CheckBox) this.findViewById(R.id.chk_settings_routingflags_avoidtolls);
		this.mChkAlwaysRemember = (CheckBox) this.findViewById(R.id.chk_settings_routingflags_alwaysremember);
		this.mChkSaveInitialRoute = (CheckBox) this.findViewById(R.id.chk_settings_routingflags_saveinitial);

		this.mRadCar = (RadioButton) this.findViewById(R.id.rad_settings_routingflags_car);
		this.mRadPedestrian = (RadioButton) this.findViewById(R.id.rad_settings_routingflags_pedestrian);
		this.mRadBicycle = (RadioButton) this.findViewById(R.id.rad_settings_routingflags_bicycle);

		{ // Workaround for false treating of RadioButtons when they are not direct children of RadioGroup
			this.mRadCar.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				public void onCheckedChanged(final CompoundButton b, final boolean isChecked) {
					if(isChecked){
						SettingsRoutingFlags.this.mRadBicycle.setChecked(false);
						SettingsRoutingFlags.this.mRadPedestrian.setChecked(false);
					}
				}
			});
			this.mRadPedestrian.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				public void onCheckedChanged(final CompoundButton b, final boolean isChecked) {
					if(isChecked){
						SettingsRoutingFlags.this.mRadBicycle.setChecked(false);
						SettingsRoutingFlags.this.mRadCar.setChecked(false);
					}
				}
			});
			this.mRadBicycle.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				public void onCheckedChanged(final CompoundButton b, final boolean isChecked) {
					if(isChecked){
						SettingsRoutingFlags.this.mRadCar.setChecked(false);
						SettingsRoutingFlags.this.mRadPedestrian.setChecked(false);
					}
				}
			});
		}
		this.mRadFastest = (RadioButton) this.findViewById(R.id.rad_settings_routingflags_fastest);
		this.mRadShortest = (RadioButton) this.findViewById(R.id.rad_settings_routingflags_shortest);
		this.mChkRealtimeNavigation = (CheckBox) this.findViewById(R.id.chk_settings_routingflags_realtime);

		this.applyButtonListeners();

		this.loadSavedFlagsToViews();
	}

	private void applyButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this, R.id.btn_settings_routingflags_ok){
			@Override
			public void onBoth(final View me, final boolean justGotFocus) {
				if(SettingsRoutingFlags.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsRoutingFlags.this, R.raw.ok).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				final RoutePreferenceType routePrefType;
				if(SettingsRoutingFlags.this.mRadCar.isChecked()) {
					if(SettingsRoutingFlags.this.mRadFastest.isChecked()) {
						routePrefType = RoutePreferenceType.FASTEST;
					} else {
						routePrefType = RoutePreferenceType.SHORTEST;
					}
				} else if(SettingsRoutingFlags.this.mRadPedestrian.isChecked()) {
					routePrefType = RoutePreferenceType.PEDESTRIAN;
				} else {
					routePrefType = RoutePreferenceType.BICYCLE;
					//				else
					//					routePrefType = RoutePreferenceType.PUBLICTRANSIT;
				}

				Preferences.saveRoutePreferenceType(SettingsRoutingFlags.this, routePrefType);
				Preferences.saveRealTimeNav(SettingsRoutingFlags.this, SettingsRoutingFlags.this.mChkRealtimeNavigation.isChecked());
				Preferences.saveAvoidHighways(SettingsRoutingFlags.this, SettingsRoutingFlags.this.mChkAvoidHighways.isChecked());
				Preferences.saveAvoidTolls(SettingsRoutingFlags.this, SettingsRoutingFlags.this.mChkAvoidTolls.isChecked());
				Preferences.saveSaveInitialRoute(SettingsRoutingFlags.this, SettingsRoutingFlags.this.mChkSaveInitialRoute.isChecked());
				if(SettingsRoutingFlags.this.mChkAlwaysRemember != null){
					Preferences.saveNavSettingsRemember(SettingsRoutingFlags.this, SettingsRoutingFlags.this.mChkAlwaysRemember.isChecked());
				}
				SettingsRoutingFlags.this.setResult(SUBACTIVITY_RESULTCODE_SUCCESS);
				SettingsRoutingFlags.this.finish();
			}
		};
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, MENU_ALWAYSREMEMBER_ID, 0, R.string.chk_settings_routingflags_alwaysremember_caption).setIcon(R.drawable.checked).setCheckable(true);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		final MenuItem rememberItem = menu.findItem(MENU_ALWAYSREMEMBER_ID);
		if(Preferences.getNavSettingsRemember(this)){
			rememberItem.setIcon(R.drawable.checked);
		}else{
			rememberItem.setIcon(R.drawable.unchecked);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch(item.getItemId()){
			case MENU_ALWAYSREMEMBER_ID:
				final boolean wasSet = Preferences.getNavSettingsRemember(this);
				Preferences.saveNavSettingsRemember(this, !wasSet);
				if(this.mChkAlwaysRemember != null) {
					this.mChkAlwaysRemember.setChecked(!wasSet);
				}
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		switch(keyCode){
			case KeyEvent.KEYCODE_BACK:
				SettingsRoutingFlags.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SettingsRoutingFlags.this.finish();
				return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void loadSavedFlagsToViews() {
		if(this.mChkAlwaysRemember != null){
			this.mChkAlwaysRemember.setChecked(Preferences.getNavSettingsRemember(this));
		}
		this.mChkAvoidHighways.setChecked(Preferences.getAvoidHighways(this));
		this.mChkAvoidTolls.setChecked(Preferences.getAvoidTolls(this));
		this.mChkRealtimeNavigation.setChecked(Preferences.getRealTimeNav(this));
		this.mChkSaveInitialRoute.setChecked(Preferences.getSaveInitialRoute(this));
		final RoutePreferenceType rpt = Preferences.getRoutePreferenceType(this);
		switch(rpt){
			case PEDESTRIAN:
				this.mRadFastest.setChecked(true);
				this.mRadPedestrian.setChecked(true);
				break;
			case BICYCLE:
				this.mRadFastest.setChecked(true);
				this.mRadBicycle.setChecked(true);
				break;
			case FASTEST:
				this.mRadFastest.setChecked(true);
				this.mRadCar.setChecked(true);
				break;
			case SHORTEST:
				this.mRadShortest.setChecked(true);
				this.mRadCar.setChecked(true);
				break;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
