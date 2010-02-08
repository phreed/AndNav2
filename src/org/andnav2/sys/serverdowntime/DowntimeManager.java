// Created by plusminus on 13:43:19 - 04.02.2009
package org.andnav2.sys.serverdowntime;

import java.util.Collections;

import org.andnav2.R;
import org.andnav2.Splash;
import org.andnav2.sys.serverdowntime.adt.Downtime;
import org.andnav2.sys.serverdowntime.adt.DowntimeList;
import org.andnav2.util.TimeUtils;
import org.andnav2.util.TimeUtils.DurationFormatter;
import org.andnav2.util.constants.Constants;
import org.andnav2.util.constants.TimeConstants;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class DowntimeManager implements Constants, TimeConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final long MINIMUM_MILLISECONDS_IN_FUTURE_TO_WARN_FOR_DOWNTIME = 2 * SECONDSPERDAY * 1000; // 2 Days

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

	public static void requestDowntimesAsync(final Context ctx){
		new Thread(new Runnable(){
			public void run() {
				try {
					/* Load sort and filter the downtimes from the server. */
					final DowntimeList dts = DowntimeRequester.request();
					Collections.sort(dts);
					dts.filterPassed();

					final long now = System.currentTimeMillis();
					final Downtime nextDowntime = dts.getNextOccuringDowntimeStartingFrom(now);
					if(nextDowntime == null) {
						return;
					}

					/* Check if the downtime is to far in the future. */
					final long nextOccurenceStartingFromNow = nextDowntime.getNextOccurenceStartingFrom(now);

					final long milliSecondsInFuture = nextOccurenceStartingFromNow - now;
					if(milliSecondsInFuture > MINIMUM_MILLISECONDS_IN_FUTURE_TO_WARN_FOR_DOWNTIME) {
						return;
					}

					// look up the notification manager service
					final NotificationManager nm = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

					/* The intent to be launched when the notification was clicked. */
					final Intent contentIntent = new Intent(ctx, Splash.class);
					contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					/* The actual notification. */
					final long timeToDisplay = Math.max(now, nextDowntime.getStartTimestamp());
					final Notification notification = new Notification(R.drawable.icon, ctx.getString(R.string.notif_serverdowntime_tickertext), timeToDisplay);

					/* The PendingIntent used to invoke the contentIntent. */
					final PendingIntent appIntent = PendingIntent.getActivity(ctx, 0, contentIntent, 0);

					/* Set the textual notification-description and the PendingIntent to the Notification. */
					final String title = ctx.getText(R.string.notif_serverdowntime_title) + " " + new DurationFormatter(ctx).format(nextDowntime.getDurationSeconds());
					final String message = ctx.getText(R.string.notif_serverdowntime_message) + " " + TimeUtils.convertTimestampToTimeString(nextOccurenceStartingFromNow);
					notification.setLatestEventInfo(ctx, title, message, appIntent);

					/* Fire the notification. */
					final int pseudoUniqueID = (int)(nextOccurenceStartingFromNow % Integer.MAX_VALUE);
					nm.notify(pseudoUniqueID, notification);

					Log.d(DEBUGTAG, nextDowntime.toString());
				} catch (final Throwable t) {
					//					Exceptor.e("Error receiving Downtimes.", t, ctx);
				}
			}
		}, "DowntimeManager-Thread").start();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
