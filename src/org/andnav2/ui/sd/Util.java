// Created by plusminus on 17:13:49 - 16.05.2008
package org.andnav2.ui.sd;

import org.andnav2.util.constants.Constants;

import android.os.Bundle;

public class Util implements Constants{

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

	public static String getZipCodeOrCityName(final Bundle b) {
		switch (b.getInt(EXTRAS_MODE)) {
			case EXTRAS_MODE_ZIPSEARCH:
				return b.getString(EXTRAS_ZIPCODE_ID);
			case EXTRAS_MODE_CITYNAMESEARCH:
				return b.getString(EXTRAS_CITYNAME_ID);
			default:
				throw new IllegalArgumentException();
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
