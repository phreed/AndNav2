//Created by plusminus on 19:51:45 - 21.05.2008
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.map.SetHomeMap;
import org.andnav2.ui.sd.SDMainChoose;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;


public class SettingsSelectHome extends AndNavBaseActivity{
	// ===========================================================
	// Final Fields
	// ===========================================================

	protected static final int SETHOME_VIA_MAP_REQUESTCODE = 0;
	protected static final int SETHOME_VIA_SD_REQUESTCODE = SETHOME_VIA_MAP_REQUESTCODE + 1;

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
		this.setContentView(R.layout.settings_select_sethome);

		this.findViewById(R.id.iv_settings_sethome_via_map).setFocusable(false);
		this.findViewById(R.id.iv_settings_sethome_via_sd).setFocusable(false);

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
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_sethome_close)){
			@Override
			public void onBoth(final View arg0, final boolean focused) {
				if(SettingsSelectHome.super.mMenuVoiceEnabled && focused) {
					MediaPlayer.create(SettingsSelectHome.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				SettingsSelectHome.this.finish();
			}
		};
	}

	private void applyChooseButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.btn_settings_sethome_via_map), this.findViewById(R.id.iv_settings_sethome_via_map)){
			@Override
			public void onClicked(final View me) {
				/* Load SetHomeMap-MapActivity. */
				final Intent setHomeViaMapIntent = new Intent(SettingsSelectHome.this, SetHomeMap.class);
				SettingsSelectHome.this.startActivityForResult(setHomeViaMapIntent, SettingsSelectHome.this.SETHOME_VIA_MAP_REQUESTCODE);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.btn_settings_sethome_via_sd), this.findViewById(R.id.iv_settings_sethome_via_sd)){
			@Override
			public void onClicked(final View me) {
				/* Load SDCountry-Activity. */
				final Intent sdMainchooseIntent = new Intent(SettingsSelectHome.this, SDMainChoose.class);

				final Bundle b = new Bundle();
				b.putInt(MODE_SD, MODE_SD_SETHOME);

				sdMainchooseIntent.putExtras(b);
				SettingsSelectHome.this.startActivityForResult(sdMainchooseIntent, SettingsSelectHome.this.SETHOME_VIA_SD_REQUESTCODE);
			}
		};
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch(requestCode){
			case SETHOME_VIA_MAP_REQUESTCODE:
				if(resultCode == SUBACTIVITY_RESULTCODE_SUCCESS || resultCode == SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS){
					this.setResult(SUBACTIVITY_RESULTCODE_SUCCESS);
					this.finish();
				}
				break;
			case SETHOME_VIA_SD_REQUESTCODE:
				if(resultCode == SUBACTIVITY_RESULTCODE_SUCCESS || resultCode == SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS){
					//					if(SettingsSelectHome.super.getDataConnectionStrength() == 0)
					//						; // TODO Notify User. Better would be to check on start of that SubActivity.

					final Bundle extras = data.getExtras();


					final int mode = extras.getInt(EXTRAS_MODE);
					switch(mode){
						case EXTRAS_MODE_DIRECT_LATLNG:
							final int latE6 = extras.getInt(EXTRAS_DESTINATION_LATITUDE_ID);
							final int lonE6 = extras.getInt(EXTRAS_DESTINATION_LONGITUDE_ID);
							final GeoPoint gp = new GeoPoint(latE6, lonE6);
							Preferences.saveHomeGeoPoint(SettingsSelectHome.this, gp);
							SettingsSelectHome.this.setResult(SUBACTIVITY_RESULTCODE_SUCCESS);
							SettingsSelectHome.this.finish();
							break;
					}

				}
				break;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
