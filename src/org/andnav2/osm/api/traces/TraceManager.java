package org.andnav2.osm.api.traces;

import java.util.ArrayList;

import org.andnav2.R;
import org.andnav2.Splash;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.api.traces.util.Util;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.common.CommonCallback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class TraceManager implements OSMConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int FALLBACK_NOTIFICATION_ID = 1234;

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

	/**
	 * @param pContext can be null, when aOSMContributionPolicy is not UPLOADTOOWNOSMACCOUNT
	 */
	public static void contributeAsync(final Context pContext, final ArrayList<GeoPoint> pRecordedGeoPoints){
		try{
			if(Preferences.getTracePolicyExternal(pContext)) {
				doSaveToExternal(pContext, pRecordedGeoPoints);
			}

			if(Preferences.getTracePolicyOSM(pContext)) {
				doUploadToOSMAccount(pContext, pRecordedGeoPoints);
			}

			if(Preferences.getTracePolicyAndnavOrg(pContext)) {
				GpxToPHPUploader.uploadAsync(pRecordedGeoPoints);
			}
		}catch(final Throwable t){
			/* Ensure nothing fails in here! */
			Log.e(DEBUGTAG, "Trace-Error", t);
		}
	}

	private static void doSaveToExternal(final Context pContext, final ArrayList<GeoPoint> pRecordedGeoPoints) {
		// Check if External Media is mounted.
		if(pContext != null && android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			if(Preferences.getMinimalTraceFilteringEnabled(pContext)){
				if(Util.isSufficienDataForUpload(pRecordedGeoPoints)) {
					GPXToFileWriter.writeToFileAsync(pRecordedGeoPoints);
				}
			}else{
				GPXToFileWriter.writeToFileAsync(pRecordedGeoPoints);
			}
		}
	}

	private static void doUploadToOSMAccount(final Context pContext, final ArrayList<GeoPoint> pRecordedGeoPoints) {
		if(pContext != null){
			/* Prepare the callback. */
			final CommonCallback<Void> callback = new CommonCallback<Void>(){
				public void onFailure(final Throwable t) {
					// look up the notification manager service
					final NotificationManager nm = (NotificationManager)pContext.getSystemService(Context.NOTIFICATION_SERVICE);

					/* The intent to be launched when the notification was clicked. */
					final Intent contentIntent = new Intent(pContext, Splash.class);
					contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					/* The actual notification. */
					final Notification notification = new Notification(R.drawable.icon, pContext.getString(R.string.notif_settings_tracepolicy_osmcontribution_failed_fallback_used_title), System.currentTimeMillis());

					/* The PendingIntent used to invoke the contentIntent. */
					final PendingIntent appIntent = PendingIntent.getActivity(pContext, 0, contentIntent, 0);

					/* Set the textual notification-description and the PendingIntent to the Notification. */
					notification.setLatestEventInfo(pContext, pContext.getText(R.string.notif_settings_tracepolicy_osmcontribution_failed_fallback_used_title), pContext.getText(R.string.notif_settings_tracepolicy_osmcontribution_failed_fallback_used_message), appIntent);

					/* Fire the notification. */
					nm.notify(FALLBACK_NOTIFICATION_ID, notification);

					/* Write to SD-Card as a fallback. */
					doSaveToExternal(pContext, pRecordedGeoPoints);
				}

				public void onSuccess(final Void result) {
					/* Nothing. */
				}
			};

			if(Preferences.getMinimalTraceFilteringEnabled(pContext)){
				if(Util.isSufficienDataForUpload(pRecordedGeoPoints)) {
					OSMUploader.uploadAsync(pRecordedGeoPoints, Preferences.getOSMAccountUsername(pContext), Preferences.getOSMAccountPassword(pContext), callback);
				}
			}else{
				OSMUploader.uploadAsync(pRecordedGeoPoints, Preferences.getOSMAccountUsername(pContext), Preferences.getOSMAccountPassword(pContext), callback);
			}
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
