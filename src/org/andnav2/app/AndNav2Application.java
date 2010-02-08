// Created by plusminus on 01:40:43 - 20.11.2008
package org.andnav2.app;

import org.andnav2.util.constants.TimeConstants;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.tomgibara.android.veecheck.Veecheck;
import com.tomgibara.android.veecheck.util.PrefSettings;


public class AndNav2Application extends Application implements TimeConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String VERSIONS_URL = "http://www.andnav.org/sys/veecheck/andnav2/versions.xml";

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate() {
		super.onCreate();

		final SharedPreferences prefs = PrefSettings.getSharedPrefs(this);
		//assign some default settings if necessary
		//		if (!VERSIONS_URL.equals(prefs.getString(PrefSettings.KEY_CHECK_URI, null))) {
		final Editor editor = prefs.edit();
		editor.putBoolean(PrefSettings.KEY_ENABLED, true);
		editor.putLong(PrefSettings.KEY_PERIOD, SECONDSPERHOUR * 1000L); // milliseconds
		editor.putLong(PrefSettings.KEY_CHECK_INTERVAL, SECONDSPERHOUR * 2 * 1000L); // milliseconds
		editor.putString(PrefSettings.KEY_CHECK_URI, VERSIONS_URL);
		editor.commit();
		//		}

		//reschedule the checks - we need to do this if the settings have changed (as above)
		//it may also necessary in the case where an application has been updated
		//here for simplicity, we do it every time the application is launched
		final Intent intent = new Intent(Veecheck.getRescheduleAction(this));
		sendBroadcast(intent);
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
