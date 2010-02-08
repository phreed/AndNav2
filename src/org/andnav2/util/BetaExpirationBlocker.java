// Created by plusminus on 12:34:36 - 26.10.2008
package org.andnav2.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.andnav2.R;
import org.andnav2.util.constants.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;


public class BetaExpirationBlocker implements Constants{
	// ===========================================================
	// Constants
	// ===========================================================

	public static final String LASTUSE_ID = "LASTUSE";
	public static final GregorianCalendar expires = new GregorianCalendar(2009, Calendar.DECEMBER, 31, 22, 0, 0);

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

	public static final boolean finishWhenExpired(final Activity a){
		final SharedPreferences spref = a.getPreferences(Context.MODE_PRIVATE);

		long lastuse = spref.getLong(LASTUSE_ID, NOT_SET);
		lastuse = Math.max(lastuse, System.currentTimeMillis());
		spref.edit().putLong(LASTUSE_ID, lastuse).commit();

		final GregorianCalendar lastuseDay = new GregorianCalendar();
		lastuseDay.setTimeInMillis(lastuse);

		if(lastuseDay.after(expires)){
			new AlertDialog.Builder(a)
			.setTitle(R.string.instructions)
			.setIcon(R.drawable.information)
			.setMessage("Sorry, but this version of AndNav2 has expired!\n\nCheck http://download.andnav.org for the latest version?\n")
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
				public void onClick(final DialogInterface arg0, final int arg1) {
					final Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://download.andnav.org"));
					a.startActivity(i);
					a.finish();
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
				public void onClick(final DialogInterface arg0, final int arg1) {
					a.finish();
				}
			})
			.create().show();
			return true;
		}else{
			return false;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
