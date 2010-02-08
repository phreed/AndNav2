package org.andnav2.app;

import org.andnav2.preferences.Preferences;
import org.andnav2.util.constants.AdFreeConstants;
import org.andnav2.util.constants.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Nicolas Gramlich
 * @since 09:51:38 - 04.09.2009
 */
public class AdFreeResultIntentReceiver extends BroadcastReceiver implements Constants, AdFreeConstants {
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
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onReceive(final Context context, final Intent intent) {
		Log.d(Constants.DEBUGTAG, "AdFreeSuccessIntent recieved. Raw: " + intent.toString());
		
		final String action = intent.getAction();
		
		if(action.equals(ANDNAV_ADFREE_SUCCESS_ACTION)){
			Preferences.saveAdFreeState(context, true);
//			Toast.makeText(context, intent.getStringExtra(ANDNAV_ADFREE_SUCCESS_CODE), Toast.LENGTH_LONG).show();
		}else if(action.equals(ANDNAV_ADFREE_FAIL_ACTION)){
			Preferences.saveAdFreeState(context, false);
			Toast.makeText(context, "Please update your 'AndNav2 AdFree Plugin' from the Android Market!", Toast.LENGTH_LONG).show();			
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
