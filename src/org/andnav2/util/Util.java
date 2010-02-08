// Created by plusminus on 22:41:12 - 15.08.2008
package org.andnav2.util;

import java.util.ArrayList;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.util.constants.Constants;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;


public class Util {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String IMEI_HASHED_DEFAULT = "CUSTOM_BUILD_NO_IMEI_AVAILABLE";
	
	// ===========================================================
	// Fields
	// ===========================================================

	private static String IMEI_HASHED;

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

	public static int getVersionNumber(final Context ctx) {
		try {
			final PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			return pi.versionCode;
		} catch (final PackageManager.NameNotFoundException e) {
			Log.e(Constants.DEBUGTAG, "Package name not found", e);
			return Constants.NOT_SET;
		}
	}

	public static String getVersionName(final Context ctx) {
		try {
			final PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			return pi.versionName;
		} catch (final PackageManager.NameNotFoundException e) {
			Log.e(Constants.DEBUGTAG, "Package name not found", e);
			return "0";
		}
	}

	public static String getDeviceIDHashed(final Context ctx) {
		if(IMEI_HASHED == null){
			try{
				final TelephonyManager tm = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
				final String imei = tm.getDeviceId();
				IMEI_HASHED = MD5.hash(imei);
			}catch(Throwable t){
				/* Workaround for custom builds, that deliver no IMEI or are not MD5 capable. */
				IMEI_HASHED = IMEI_HASHED_DEFAULT + "_"+ System.currentTimeMillis();
			}
		}
		return IMEI_HASHED;
	}

	public static String merge(final String[] src, final String seperator){
		return merge(src, 0, src.length, seperator);
	}

	public static String merge(final String[] src, final int start, final int length, final String seperator){
		final StringBuilder sb = new StringBuilder();
		for (int i = start; i < length; i++) {
			sb.append(src[i]).append(seperator);
		}
		sb.setLength(sb.length() - seperator.length());
		return sb.toString();
	}

	public static ArrayList<GeoPoint> cloneDeep(final ArrayList<GeoPoint> wayPoints) {
		final ArrayList<GeoPoint> out = new ArrayList<GeoPoint>();

		for (final GeoPoint gp : wayPoints) {
			out.add(gp.cloneDeep());
		}

		return out;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
