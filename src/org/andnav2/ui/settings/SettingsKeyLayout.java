// Created by plusminus on 23:19:07 - 15.05.2008
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.adt.keyboardlayouts.KeyBoardLayoutImpls;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class SettingsKeyLayout extends AndNavBaseActivity {
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
		this.setContentView(R.layout.settings_keylayout);

		this.applyTopButtonListeners();
		this.applyKeyLayoutButtonListeners();
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
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_keylayout_close)){
			@Override
			public void onBoth(final View arg0, final boolean focused) {
				if(SettingsKeyLayout.super.mMenuVoiceEnabled && focused) {
					MediaPlayer.create(SettingsKeyLayout.this, R.raw.close).start();
				}
			}

			// No onFocusChange, because there is no Quickinfo-Text

			@Override
			public void onClicked(final View me) {
				SettingsKeyLayout.this.finish();
			}
		};
	}

	private void applyKeyLayoutButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_keylayout_abcdef)){
			@Override
			public void onClicked(final View me) {
				if(SettingsKeyLayout.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsKeyLayout.this, R.raw.save).start();
				}

				Preferences.saveKeyboardLayout(SettingsKeyLayout.this, new KeyBoardLayoutImpls.ABCKeyboardLayout());
				SettingsKeyLayout.this.finish();
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_keylayout_qwerty)){
			@Override
			public void onClicked(final View me) {
				if(SettingsKeyLayout.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsKeyLayout.this, R.raw.save).start();
				}

				Preferences.saveKeyboardLayout(SettingsKeyLayout.this, new KeyBoardLayoutImpls.QWERTYKeyBoardLayout());
				SettingsKeyLayout.this.finish();
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_keylayout_qwertz)){
			@Override
			public void onClicked(final View me) {
				if(SettingsKeyLayout.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsKeyLayout.this, R.raw.save).start();
				}

				Preferences.saveKeyboardLayout(SettingsKeyLayout.this, new KeyBoardLayoutImpls.QWERTZKeyBoardLayout());
				SettingsKeyLayout.this.finish();
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_keylayout_cyrillic)){
			@Override
			public void onClicked(final View me) {
				if(SettingsKeyLayout.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsKeyLayout.this, R.raw.save).start();
				}

				Preferences.saveKeyboardLayout(SettingsKeyLayout.this, new KeyBoardLayoutImpls.CyrillicKeyBoardLayout());
				SettingsKeyLayout.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
