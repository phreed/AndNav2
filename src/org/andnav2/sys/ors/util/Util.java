package org.andnav2.sys.ors.util;

import org.andnav2.sys.ors.util.constants.ORSXMLConstants;

import android.content.Context;


/**
 * @since 2008-04-06 19:03:54
 * @author Nicolas 'plusminus' Gramlich
 * License:
 * @see Creative Commons Attribution-Noncommercial-Share Alike 2.0 Germany License .
 * Permissions beyond the scope of this license may be requested at plusminus {at} anddev {dot} org
 */
public class Util implements ORSXMLConstants{

	// ===========================================================
	// Final Fields
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

	public static String getORSClientName(final Context ctx){
		return CLIENTNAME_ANDNAV_PREFIX + CLIENTNAME_SPACER + org.andnav2.util.Util.getVersionName(ctx) + CLIENTNAME_SPACER + org.andnav2.util.Util.getDeviceIDHashed(ctx);
	}

	public static String removeHtmlTags(final String pInput) {
		return pInput.replaceAll("\\<.*?\\>", "");
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
