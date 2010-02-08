//Created by plusminus on 19:32:25 - 01.02.2008
package org.andnav2;

import org.andnav2.preferences.Preferences;
import org.andnav2.sys.serverdowntime.DowntimeManager;
import org.andnav2.ui.Menu;
import org.andnav2.ui.StartupWarning;
import org.andnav2.ui.common.CommonCallbackAdapter;
import org.andnav2.ui.common.CommonDialogFactory;
import org.andnav2.ui.common.views.AdView;
import org.andnav2.ui.settings.SettingsORSServer;
import org.andnav2.util.Util;
import org.andnav2.util.constants.AdFreeConstants;
import org.andnav2.util.constants.Constants;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager.BadTokenException;

public class Splash extends Activity implements Constants, AdFreeConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int REQUESTCODE_STARTUPWARNING = 0;
	private static final int REQUESTCODE_SETORSSERVER = REQUESTCODE_STARTUPWARNING + 1;

	private static final int SPLASH_DISPLAY_LENGHT = 2500;
	private static final int DIALOG_SHOW_ACCEPT_EULA = 0;
	private static final int DIALOG_SHOW_GPS_NOT_ENABLED = DIALOG_SHOW_ACCEPT_EULA + 1;
	private static final int DIALOG_SHOW_ORSSERVER_CHANGEABLE_INFO = DIALOG_SHOW_GPS_NOT_ENABLED + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	/** 
	 * 
	 * Called when the activity is first created. 
	 * 
	 * @param icicle ?
	 */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		setContentView(R.layout.splashscreen);
		setTitle(R.string.app_name_splash);

		this.setRequestedOrientation(Preferences.getRequestedScreenOrientation(this));

		try{
			final Intent adfreeintent = new Intent(ANDNAV_ADFREE_ACTION);
			adfreeintent.putExtra(ANDNAV_ADFREE_CODE, Util.getVersionName(this));
			this.sendBroadcast(adfreeintent);			
			
			
			AdView.preloadAdAsync(this);

			DowntimeManager.requestDowntimesAsync(this);

			//			android.provider.Settings.System.putInt(this.getContentResolver(), android.provider.Settings.System.SOUND_EFFECTS_ENABLED, 1);

			//			ISpatialDataOrganizer<GeoPoint> iim = new ListBackedSpatialIndexOrganizer<GeoPoint>();
			//			Collection<GeoPoint> items = new ArrayList<GeoPoint>();
			//			items.add(new GeoPoint(0,0));
			//			items.add(new GeoPoint(10,10));
			//			items.add(new GeoPoint(0,10));
			//			items.add(new GeoPoint(10,0));
			//			items.add(new GeoPoint(6,6));
			//			iim.addAll(items);
			//			iim.buildIndex();
			//
			//			List<GeoPoint> close = iim.getClosest(new GeoPoint(7,7), 2);
			//			for (GeoPoint geoPoint : close) {
			//				System.out.println(geoPoint);
			//			}
		}catch(final Exception e){
			Log.d(DEBUGTAG, "SplashError", e);
		}

		/* Load the simple 'Zoom-In' Animation and apply it to the Splash-Logo. */
		//		final Animation logoZoomAnimation = AnimationUtils.loadAnimation(Splash.this, R.anim.splash_zoomin);
		//		logoZoomAnimation.setRepeatCount(0);
		//		this.findViewById(R.id.iv_splash_logo).startAnimation(logoZoomAnimation);
		
		if(Preferences.isEulaAccepted(Splash.this)){
			doEulaAcceptedStartup();
		}else{
			showDialog(DIALOG_SHOW_ACCEPT_EULA);
		}
	}

	private void doEulaAcceptedStartup() {
		/* Check if there is a need to register a SkyHook-Account. */
		if(Preferences.getSkyHookWPSAuthentication(this, false) == null){
			//			Util.registerAndSaveNewSkyHookUserAsync(this); // TODO remove when
		}

		/* New Handler to start the Menu-Activity
		 * and close this Splash-Screen after some seconds.*/
		new Handler().postDelayed(new Runnable(){
			public void run() {
				proceedWithGPSEnabledCheck();
			}
		}, SPLASH_DISPLAY_LENGHT);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void proceedWithGPSEnabledCheck() {
		final LocationManager lm = (LocationManager)Splash.this.getSystemService(LOCATION_SERVICE);
		final boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if(gpsEnabled){
			proceedWithORSServerSetCheck();
		}else{ /* GPS is required to run AndNav! */
			try{
				showDialog(DIALOG_SHOW_GPS_NOT_ENABLED);
			}catch(final BadTokenException bte){ /* Nothing. */ }
		}
	}

	private void proceedWithORSServerChangeableDialog(){
		showDialog(DIALOG_SHOW_ORSSERVER_CHANGEABLE_INFO);
	}

	private void proceedWithORSServerSetCheck() {
		if(Preferences.hasORSServer(this)){
			proceedWithStartUpWarning();
		}else{
			/* No server has been set so far, select one. */
			final Intent setServerIntent = new Intent(Splash.this, SettingsORSServer.class);
			Splash.this.startActivityForResult(setServerIntent, REQUESTCODE_SETORSSERVER);
		}
	}

	private void proceedWithStartUpWarning() {
		final boolean showStartupWarning = !Preferences.getShowStartupWarningNeverAgain(Splash.this);
		if(showStartupWarning){
			final Intent warningIntent = new Intent(Splash.this, StartupWarning.class);
			Splash.this.startActivityForResult(warningIntent, REQUESTCODE_STARTUPWARNING);
		}else{
			/* Start the Menu-Activity. */
			startMenuActivity();
		}
	}

	/**
	 * Launches the Menu-Activity as a separated Activity and finished this Activity.
	 */
	protected void startMenuActivity() {
		final Intent mainIntent = new Intent(this, Menu.class);
		Splash.this.startActivity(mainIntent);
		Splash.this.finish();
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	/** React on returning Activities. */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch(requestCode){
			case REQUESTCODE_STARTUPWARNING:
				startMenuActivity();
				break;
			case REQUESTCODE_SETORSSERVER:
				proceedWithORSServerChangeableDialog();
				break;
		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		switch(id){
			case DIALOG_SHOW_ORSSERVER_CHANGEABLE_INFO:
				return CommonDialogFactory.createORSServerChangeableInfoDialog(this, new CommonCallbackAdapter<Void>(){
					@Override
					public void onSuccess(final Void result) {
						proceedWithStartUpWarning();
					}
				});
			case DIALOG_SHOW_ACCEPT_EULA:
				return CommonDialogFactory.createAcceptEulaDialog(this, new CommonCallbackAdapter<Boolean>(){
					@Override
					public void onSuccess(final Boolean accepted) {
						if(accepted){
							Preferences.saveEulaAccepted(Splash.this);
							doEulaAcceptedStartup();
						}else{
							Splash.this.finish();
						}
					}
				});
			case DIALOG_SHOW_GPS_NOT_ENABLED:
				return CommonDialogFactory.createEnableGPSDialog(this, new CommonCallbackAdapter<Boolean>(){
					@Override
					public void onSuccess(final Boolean result) {
						if(result) {
							startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
						}

						Splash.this.finish();
					}
				});
			default:
				return null;
		}
	}
}
