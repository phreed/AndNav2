// Created by plusminus on 16:40:21 - 28.01.2009
package org.andnav2.skyhook;

import org.andnav2.preferences.PreferenceConstants;
import org.andnav2.preferences.Preferences;

import android.content.Context;

import com.skyhookwireless.wps.RegistrationCallback;
import com.skyhookwireless.wps.WPS;
import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSReturnCode;



public class Util {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static void registerAndSaveNewSkyHookUserAsync(final Context ctx) {
		new Thread(new Runnable(){
			public void run() {
				/* Retrieve the IMEI (Device-unique ID) to set up a username for SkyHook. */
				final String imeiHashed = org.andnav2.util.Util.getDeviceIDHashed(ctx);

				final WPSAuthentication skyHookWPSRegistrationAuthentication = Preferences.getSkyHookWPSRegistrationAuthentication();
				final String newUsername = PreferenceConstants.PREF_SKYHOOKFALLBACKUSERNAME_DEFAULT + "_" + imeiHashed;
				final WPSAuthentication newUser = new WPSAuthentication(newUsername, skyHookWPSRegistrationAuthentication.getRealm());


				new WPS(ctx).registerUser(skyHookWPSRegistrationAuthentication, newUser, new RegistrationCallback(){
					public void handleSuccess() {
						Preferences.saveSkyHookWPSAuthentication(ctx, newUser);
					}

					public void done() {
						// Nothing
					}

					public WPSContinuation handleError(final WPSReturnCode error) {
						return WPSContinuation.WPS_STOP;
					}
				});
			}
		}).start();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
