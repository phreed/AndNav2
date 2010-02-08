// Created by plusminus on 14:00:09 - 31.01.2009
package org.andnav2.ui.sd.util;

import org.andnav2.R;
import org.andnav2.sys.ors.adt.lus.Country;

import android.content.Context;


public class StreetInfoExtractor {
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

	public static String constructCityLineInfo(final Context ctx, final String cityname, final String postalcode, final Country aNationality) throws IllegalArgumentException{
		final StringBuilder sb = new StringBuilder();

		switch(aNationality){
			case GERMANY:
				if(cityname != null && cityname.length() > 0){
					if(postalcode != null && postalcode.length() > 0) {
						sb.append(postalcode).append(" ").append(cityname);
					} else {
						sb.append(cityname);
					}
				}
				break;
			default:
				if(cityname != null && cityname.length() > 0){
					sb.append(cityname);

					if(postalcode != null && postalcode.length() > 0) {
						sb.append(' ').append(postalcode);
					}
				}
				break;
		}
		return sb.toString();
	}

	public static String constructStreetLineInfo(final Context ctx, final String streetName, final String streetNumber, final Country aNationality) throws IllegalArgumentException{
		if(streetName == null || streetName.length() == 0){
			return ctx.getString(R.string.dlg_sd_resolver_streetline_nostreet);
		}else{
			final StringBuilder sb = new StringBuilder();
			switch(aNationality){
				case FRANCE:
				case CANADA:
				case USA:
					if(streetName != null && streetName.length() > 0){
						if(streetNumber != null && streetNumber.length() > 0) {
							sb.append(streetNumber).append(" ").append(streetName);
						} else {
							sb.append(streetName);
						}
					}
					break;
				case GERMANY:
				default:
					if(streetName != null && streetName.length() > 0){
						sb.append(streetName);

						if(streetNumber != null && streetNumber.length() > 0) {
							sb.append(' ').append(streetNumber);
						}
					}
					break;
			}
			return sb.toString();
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
