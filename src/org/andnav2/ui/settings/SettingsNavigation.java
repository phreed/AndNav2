// Created by plusminus on 6:20:06 PM - Feb 21, 2009
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.preferences.PreferenceConstants;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class SettingsNavigation extends AndNavBaseActivity implements PreferenceConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	/* REQUEST-CODES for SubActivities. */
	protected static final int REQUESTCODE_CENTERMODE = 0x2737;
	protected static final int REQUESTCODE_ROTATEMODE = REQUESTCODE_CENTERMODE + 1;
	protected static final int REQUESTCODE_MORE = REQUESTCODE_ROTATEMODE + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_navigation);


		this.updateRotateModeButtonImage();
		this.updatecentermodeButtonImage();

		applyTopButtonListeners();
		applyButtonListeners();
	}

	protected void updatecentermodeButtonImage() {
		final int centermode = Preferences.getCenterMode(this);

		int drawableID = 0;
		switch(centermode){
			case PREF_CENTERMODE_CENTERUSER:
				drawableID = R.drawable.centermode_user;
				break;
			case PREF_CENTERMODE_UPTO_NEXTTURN:
				drawableID = R.drawable.centermode_uptonextturn;
				break;
		}
		((ImageView)this.findViewById(R.id.ibtn_settings_navigation_centermode)).setImageResource(drawableID);
	}

	protected void updateRotateModeButtonImage() {
		final int rotateMode = Preferences.getRotateMode(this);

		int drawableID = 0;
		switch(rotateMode){
			case PREF_ROTATEMODE_NORTH_UP:
				drawableID = R.drawable.rotatemode_northup;
				break;
			case PREF_ROTATEMODE_DRIVINGDIRECTION_UP:
				drawableID = R.drawable.rotatemode_drivingdirectionup;
				break;
		}
		((ImageView)this.findViewById(R.id.ibtn_settings_navigation_rotatemode)).setImageResource(drawableID);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	/** React on returning Activities (like centermode and Rotatemode), i.e. to update the button-images. */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

		// TODO Handle Chainclose.
		switch(requestCode){
			case REQUESTCODE_CENTERMODE:
			case REQUESTCODE_ROTATEMODE:
				this.updatecentermodeButtonImage();
				this.updateRotateModeButtonImage();
				break;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void applyTopButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_navigation_close)){
			@Override
			public void onBoth(final View arg0, final boolean focused) {
				if(SettingsNavigation.super.mMenuVoiceEnabled && focused) {
					MediaPlayer.create(SettingsNavigation.this, R.raw.close).start();
				}
			}

			// No onFocusChange, because there is no Quickinfo-Text

			@Override
			public void onClicked(final View me) {
				SettingsNavigation.this.finish();
			}
		};
	}

	private void applyButtonListeners(){
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_navigation_more)){
			@Override
			public void onClicked(final View me) {
				final Intent centermodeIntent = new Intent(SettingsNavigation.this, SettingsRouteViewing.class);
				SettingsNavigation.this.startActivityForResult(centermodeIntent, REQUESTCODE_MORE);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_navigation_centermode)){
			@Override
			public void onClicked(final View me) {
				final Intent centermodeIntent = new Intent(SettingsNavigation.this, SettingsCenterMode.class);
				SettingsNavigation.this.startActivityForResult(centermodeIntent, REQUESTCODE_CENTERMODE);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_navigation_rotatemode)){
			@Override
			public void onClicked(final View me) {
				final Intent rotateModeIntent = new Intent(SettingsNavigation.this, SettingsRotateMode.class);
				SettingsNavigation.this.startActivityForResult(rotateModeIntent, REQUESTCODE_ROTATEMODE);
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
