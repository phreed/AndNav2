// Created by plusminus on 23:46:51 - 05.11.2008
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class SettingsScreenOrientation extends AndNavBaseActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected TextView mTvQUickInfo;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_screenorientation);

		this.mTvQUickInfo = (TextView)this.findViewById(R.id.tv_settings_screenorientation_quickinfo);
		this.findViewById(R.id.ibtn_settings_screenorientation_user).requestFocus();

		applyButtonListeners();
	}

	private void applyButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_screenorientation_close)){
			@Override
			public void onBoth(final View arg0, final boolean focused) {
				if(SettingsScreenOrientation.super.mMenuVoiceEnabled && focused) {
					MediaPlayer.create(SettingsScreenOrientation.this, R.raw.close).start();
				}
			}

			// No onFocusChange, because there is no Quickinfo-Text

			@Override
			public void onClicked(final View me) {
				SettingsScreenOrientation.this.finish();
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_screenorientation_landscape)){
			@Override
			public void onBoth(final View arg0, final boolean focused) {
				if(focused) {
					SettingsScreenOrientation.this.mTvQUickInfo.setText(R.string.tv_settings_screenorientation_landscape);
				}
			}

			@Override
			public void onClicked(final View me) {
				if(SettingsScreenOrientation.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsScreenOrientation.this, R.raw.save).start();
				}

				SettingsScreenOrientation.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				Preferences.saveRequestedScreenRotation(SettingsScreenOrientation.this, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_screenorientation_portrait)){
			@Override
			public void onBoth(final View arg0, final boolean focused) {
				if(focused) {
					SettingsScreenOrientation.this.mTvQUickInfo.setText(R.string.tv_settings_screenorientation_portrait);
				}
			}

			@Override
			public void onClicked(final View me) {
				if(SettingsScreenOrientation.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsScreenOrientation.this, R.raw.save).start();
				}

				SettingsScreenOrientation.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				Preferences.saveRequestedScreenRotation(SettingsScreenOrientation.this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_screenorientation_sensor)){
			@Override
			public void onBoth(final View arg0, final boolean focused) {
				if(focused) {
					SettingsScreenOrientation.this.mTvQUickInfo.setText(R.string.tv_settings_screenorientation_sensor);
				}
			}

			@Override
			public void onClicked(final View me) {
				if(SettingsScreenOrientation.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsScreenOrientation.this, R.raw.save).start();
				}

				SettingsScreenOrientation.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				Preferences.saveRequestedScreenRotation(SettingsScreenOrientation.this, ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
		};
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_screenorientation_user)){
			@Override
			public void onBoth(final View arg0, final boolean focused) {
				if(focused) {
					SettingsScreenOrientation.this.mTvQUickInfo.setText(R.string.tv_settings_screenorientation_user);
				}
			}

			@Override
			public void onClicked(final View me) {
				if(SettingsScreenOrientation.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsScreenOrientation.this, R.raw.save).start();
				}

				SettingsScreenOrientation.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
				Preferences.saveRequestedScreenRotation(SettingsScreenOrientation.this, ActivityInfo.SCREEN_ORIENTATION_USER);
			}
		};
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

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
