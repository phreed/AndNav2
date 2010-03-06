//Created by plusminus on 19:47:09 - 01.02.2008
package org.andnav2.ui;

import org.andnav2.R;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.paypal.ui.Donate;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.ui.common.CommonCallbackAdapter;
import org.andnav2.ui.common.CommonDialogFactory;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.map.OpenStreetDDMap;
import org.andnav2.ui.map.WhereAmIMap;
import org.andnav2.ui.sd.SDMainChoose;
import org.andnav2.ui.sd.SDPOISearchList;
import org.andnav2.ui.settings.SettingsMenu;
import org.andnav2.ui.settings.SettingsSelectHome;
import org.andnav2.ui.util.Util;
import org.andnav2.util.BetaExpirationBlocker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
//FIXME import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;


public class Menu extends AndNavGPSActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	/* REQUEST-CODES for SubActivities. */
	private static final int REQUESTCODE_ROUTEHOME = 0x1337;
	private static final int REQUESTCODE_SETTINGS = REQUESTCODE_ROUTEHOME + 1;
	private static final int REQUESTCODE_SD_MAINCHOOSE = REQUESTCODE_SETTINGS + 1;
	private static final int REQUESTCODE_WHEREAMI = REQUESTCODE_SD_MAINCHOOSE + 1;
	private static final int REQUESTCODE_FIRSTAID = REQUESTCODE_WHEREAMI + 1;
	private static final int REQUESTCODE_SETHOME = REQUESTCODE_FIRSTAID + 1;
	private static final int REQUESTCODE_ABOUT = REQUESTCODE_SETHOME + 1;
	private static final int REQUESTCODE_DONATE = REQUESTCODE_ABOUT + 1;
	private static final int REQUESTCODE_TTS_DATA_CHECK_CODE = REQUESTCODE_DONATE + 1;

	private static final int MENU_ABOUT_ID = android.view.Menu.FIRST;
	private static final int MENU_VERSIONINFO_ID = MENU_ABOUT_ID + 1;
	private static final int MENU_BUGREPORT_ID = MENU_VERSIONINFO_ID + 1;
	
	private static final int DIALOG_SHOW_REPORT_BUG = 0;
	private static final int DIALOG_SHOW_VERSIONINFO = DIALOG_SHOW_REPORT_BUG + 1;
	private static final int DIALOG_SHOW_TTS_INSTALL = DIALOG_SHOW_VERSIONINFO + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preferences.applySharedSettings(this);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.setContentView(R.layout.menu);

//		if(BetaExpirationBlocker.finishWhenExpired(this)) {
//			return;
//		}

		this.findViewById(R.id.ibtn_whereami).requestFocus();

		this.applyMenuButtonListeners();

		findViewById(R.id.ibtn_whereami).setSoundEffectsEnabled(true);
		findViewById(R.id.ibtn_searchdestination).setSoundEffectsEnabled(false);

		/* Check if TTS is installed and the dialog was not permanently dismissed. */
		if(Preferences.showTTSNotInstalledInfo(this)){
			try{
				Intent checkIntent = new Intent();
				// FIXME checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
				startActivityForResult(checkIntent, REQUESTCODE_TTS_DATA_CHECK_CODE);
			}catch(Throwable t){
				
			}
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private void applyMenuButtonListeners() {
		findViewById(R.id.btn_bug).setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				Util.sendSupportEmail(Menu.this);
			}
		});
		findViewById(R.id.btn_wiki).setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				final Intent faqWebIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://wiki.andnav.org"));
				startActivity(faqWebIntent);
			}
		});
		findViewById(R.id.btn_donate).setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				final Intent donateIntent = new Intent(Menu.this, Donate.class);
				startActivityForResult(donateIntent, REQUESTCODE_DONATE);
			}
		});

		/* Set OnClickListener for Where-am-I-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_whereami)){
			@Override
			public void onBoth(final View v, final boolean justGotFocus) {
				if(justGotFocus){
					if(Menu.super.mMenuVoiceEnabled) {
						MediaPlayer.create(Menu.this, R.raw.where_am_i).start();
					}
				}
			}

			@Override
			public void onClicked(final View v) {
				startWhereAmIActivity();
			}
		};

		/* Set OnClickListener for Search-Destination-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_searchdestination)){
			@Override
			public void onBoth(final View v, final boolean justGotFocus) {
				if(justGotFocus){
					if(Menu.super.mMenuVoiceEnabled) {
						MediaPlayer.create(Menu.this, R.raw.search_destination).start();
					}
				}
			}

			@Override
			public void onClicked(final View v) {
				startSearchDestinationActivity();
			}
		};

		/* Set OnClickListener for Route-Home-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_routehome)){
			@Override
			public void onBoth(final View v, final boolean justGotFocus) {
				if(justGotFocus){
					if(Menu.super.mMenuVoiceEnabled) {
						MediaPlayer.create(Menu.this, R.raw.home_sweet_home).start();
					}
				}
			}

			@Override
			public void onClicked(final View v) {
				startNavHomeActivity(); // Or opens SetHome
			}
		};

		/* Set OnClickListener for Settings-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings)){
			@Override
			public void onBoth(final View v, final boolean justGotFocus) {
				if(justGotFocus){
					if(Menu.super.mMenuVoiceEnabled) {
						MediaPlayer.create(Menu.this, R.raw.settings).start();
					}
				}
			}

			@Override
			public void onClicked(final View v) {
				startSettingsActivity();
			}
		};

		/* Set OnClickListener for FirstAid-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_hospital)){
			@Override
			public void onBoth(final View v, final boolean justGotFocus) {
				if(justGotFocus){
					if(Menu.super.mMenuVoiceEnabled) {
						MediaPlayer.create(Menu.this, R.raw.first_aid).start();
					}
				}
			}

			@Override
			public void onClicked(final View v) {
				startFirstAidActivity();
			}
		};

		/* Set OnClickListener for Exit-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_quit)){
			@Override
			public void onBoth(final View v, final boolean justGotFocus) {
				if(justGotFocus){
					if(Menu.super.mMenuVoiceEnabled) {
						MediaPlayer.create(Menu.this, R.raw.quit).start();
					}
				}
			}

			@Override
			public void onClicked(final View v) {
				Menu.this.finish();
			}
		};
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		switch(keyCode){
			case KeyEvent.KEYCODE_W: // Where am I
				startWhereAmIActivity();
				break;
			case KeyEvent.KEYCODE_S: // Search destination
				startSearchDestinationActivity();
				break;
			case KeyEvent.KEYCODE_H: // Home sweet home
				startNavHomeActivity();
				break;
			case KeyEvent.KEYCODE_C: // Config (aka Settings)
				startSettingsActivity();
				break;
			case KeyEvent.KEYCODE_F: // First Aid
				startFirstAidActivity();
				break;
			case KeyEvent.KEYCODE_Q: // Quit
				this.finish();
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch(requestCode){
			case REQUESTCODE_SETTINGS:
				if(resultCode == SUBACTIVITY_RESULTCODE_SUCCESS || resultCode == SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS){
					final GeoPoint mpHome = Preferences.getHomeGeoPoint(Menu.this);
					if(mpHome != null){
						final Intent navHomeIntent = new Intent(Menu.this, OpenStreetDDMap.class);

						final Bundle b = new Bundle();
						b.putInt(EXTRAS_MODE, EXTRAS_MODE_HOME);

						navHomeIntent.putExtras(b);

						Menu.this.startActivityForResult(navHomeIntent, REQUESTCODE_ROUTEHOME);
					}
				}
				break;
			case REQUESTCODE_SETHOME:
				if(resultCode == SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS || resultCode == SUBACTIVITY_RESULTCODE_SUCCESS) {
					doNavHome();
				}
				break;
			case REQUESTCODE_TTS_DATA_CHECK_CODE:
// FIXME 
//				if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
//					// success, TTS is available
//				} else {
//					showDialog(DIALOG_SHOW_TTS_INSTALL);
//
//				}
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final android.view.Menu menu) {
		menu.add(0, MENU_BUGREPORT_ID, android.view.Menu.NONE, getString(R.string.report_bugs)).setIcon(R.drawable.terminal);
		menu.add(1, MENU_VERSIONINFO_ID, android.view.Menu.NONE, getString(R.string.versioninfo)).setIcon(R.drawable.hardhat);
		menu.add(2, MENU_ABOUT_ID, android.view.Menu.NONE, getString(R.string.about)).setIcon(R.drawable.questionmark_small);
		return true;
	}



	@Override
	protected Dialog onCreateDialog(final int id) {
		switch(id){
			case DIALOG_SHOW_TTS_INSTALL:
				return new AlertDialog.Builder(this)
				.setMessage(R.string.tts_not_installed_message)
				.setTitle(R.string.tts_not_installed_title)
				.setIcon(R.drawable.information)
				.setPositiveButton(R.string.install, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface pDialog, int pWhich) {
						// missing data, install it
						Intent installIntent = new Intent();
						// FIXME installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
						startActivity(installIntent);
					}
				})
				.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
					public void onClick(final DialogInterface d, final int which) {
						d.dismiss();
					}
				})
				.setNegativeButton(R.string.nevershowagain, new DialogInterface.OnClickListener(){
					public void onClick(final DialogInterface d, final int which) {
						d.dismiss();
						Preferences.saveShowTTSNotInstalledInfo(Menu.this, false);
					}
				}).create();
			case DIALOG_SHOW_REPORT_BUG:
				return CommonDialogFactory.createReportBugDialog(this, new CommonCallbackAdapter<Boolean>(){
					@Override
					public void onSuccess(final Boolean result) {
						if(result) {
							Util.sendSupportEmail(Menu.this);
						}
					}
				});
			case DIALOG_SHOW_VERSIONINFO:
				return CommonDialogFactory.createVersionInfoDialog(this, new CommonCallbackAdapter<Void>(){
					@Override
					public void onSuccess(final Void result) {
						// Nothing
					}
				});
			default:
				return null;
		}
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch (item.getItemId()) {
			case MENU_ABOUT_ID:
				final Intent aboutIntent = new Intent(this, About.class);
				startActivityForResult(aboutIntent, REQUESTCODE_ABOUT);
				return true;
			case MENU_BUGREPORT_ID:
				showDialog(DIALOG_SHOW_REPORT_BUG);
				return true;
			case MENU_VERSIONINFO_ID:
				showDialog(DIALOG_SHOW_VERSIONINFO);
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void doNavHome() {
		final Intent navHomeIntent = new Intent(Menu.this, OpenStreetDDMap.class);

		final Bundle b = new Bundle();
		b.putInt(EXTRAS_MODE, EXTRAS_MODE_HOME);

		navHomeIntent.putExtras(b);

		Menu.this.startActivityForResult(navHomeIntent, REQUESTCODE_ROUTEHOME);
	}

	private void startFirstAidActivity() {
		/* Load FirstAid-Activity. */
		//		final Intent whereAmIIntent = new Intent(Menu.this, FirstAid.class);
		//		Menu.this.startActivityForResult(whereAmIIntent, FIRSTAID_REQUESTCODE);

		final Intent favIntent = new Intent(this, SDPOISearchList.class);

		final Bundle b = new Bundle();
		b.putInt(SDPOISearchList.EXTRAS_POISEARCH_MODE, SDPOISearchList.EXTRAS_POISEARCH_MODE_ORS_CATEGORY_SEARCH);
		b.putInt(SDPOISearchList.EXTRAS_POISEARCH_MODE, SDPOISearchList.EXTRAS_POISEARCH_MODE_ORS_CATEGORY_SEARCH);
		b.putInt(SDPOISearchList.EXTRAS_POISEARCH_RADIUS, 50000);
		b.putString(SDPOISearchList.EXTRAS_POISEARCH_CATEGORY, POIType.HOSPITAL.RAWNAME);
		favIntent.putExtras(b);
		this.startActivityForResult(favIntent, REQUESTCODE_FIRSTAID);
	}

	private void startSearchDestinationActivity() {
		/* Load SDMainChoose-Activity. */
		final Intent sdCountryIntent = new Intent(Menu.this, SDMainChoose.class);

		final Bundle b = new Bundle();
		b.putInt(MODE_SD, MODE_SD_DESTINATION);

		sdCountryIntent.putExtras(b);
		Menu.this.startActivityForResult(sdCountryIntent, REQUESTCODE_SD_MAINCHOOSE);
	}

	private void startSettingsActivity() {
		/* Load Settings-Activity. */
		final Intent settingsIntent = new Intent(Menu.this, SettingsMenu.class);
		Menu.this.startActivityForResult(settingsIntent, REQUESTCODE_SETTINGS);
	}

	private void startNavHomeActivity() {
		/* Load AndNavRoutehome-Activity. */
		final GeoPoint mpHome = Preferences.getHomeGeoPoint(Menu.this);
		if(mpHome == null){
			final Intent setHomeIntent = new Intent(Menu.this, SettingsSelectHome.class);
			Menu.this.startActivityForResult(setHomeIntent, REQUESTCODE_SETHOME);
		}else{
			doNavHome();
		}
	}

	private void startWhereAmIActivity() {
		/* Load WhereAmI-MapActivity. */
		final Intent whereAmIIntent = new Intent(Menu.this, WhereAmIMap.class);
		Menu.this.startActivityForResult(whereAmIIntent, REQUESTCODE_WHEREAMI);
	}

	@Override
	protected void onLocationChanged() {
		// Nothing, we just want GPS enabled :)
	}

	@Override
	protected void onLocationLost() {
		// Nothing, we just want GPS enabled :)
	}
}
