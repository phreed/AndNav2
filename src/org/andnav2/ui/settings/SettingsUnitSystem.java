// Created by plusminus on 19:49:10 - 22.05.2008
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.adt.UnitSystem;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;


public class SettingsUnitSystem extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_unitsysem);

		this.applyTopButtonListeners();
		this.applyChooseButtonListeners();
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

	private void applyTopButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_unitsystem_close)){
			@Override
			public void onBoth(final View arg0, final boolean focused) {
				if(SettingsUnitSystem.super.mMenuVoiceEnabled && focused) {
					MediaPlayer.create(SettingsUnitSystem.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				SettingsUnitSystem.this.finish();
			}
		};
	}

	private void applyChooseButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.btn_settings_unitsystem_metric)){
			@Override
			public void onClicked(final View me) {
				if(SettingsUnitSystem.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsUnitSystem.this, R.raw.save).start();
				}

				Preferences.saveUnitSystem(SettingsUnitSystem.this, UnitSystem.METRIC);
				SettingsUnitSystem.this.finish();
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.btn_settings_unitsystem_imperial)){
			@Override
			public void onClicked(final View me) {
				if(SettingsUnitSystem.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsUnitSystem.this, R.raw.save).start();
				}

				Preferences.saveUnitSystem(SettingsUnitSystem.this, UnitSystem.IMPERIAL);
				SettingsUnitSystem.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
