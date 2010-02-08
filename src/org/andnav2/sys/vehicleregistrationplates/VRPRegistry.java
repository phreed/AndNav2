// Created by plusminus on 2:24:00 PM - Mar 6, 2009
package org.andnav2.sys.vehicleregistrationplates;

import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.vehicleregistrationplates.tables.IVRPElement;
import org.andnav2.sys.vehicleregistrationplates.tables.VRP_DE;


public class VRPRegistry {
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

	public static String getCountrySignName(final Country pNationality){
		if(pNationality.hasVRPTableID()) {
			return getCountrySignName(pNationality.getVRPTableID());
		} else {
			throw new IllegalArgumentException();
		}
	}

	private static String getCountrySignName(final int pTableID) {
		switch(pTableID){
			case VRP_DE.ID:
				return VRP_DE.SIGNNAME;
			default:
				throw new IllegalArgumentException();
		}
	}

	public static IVRPElement resolve(final Country pNationality, final String pSign){
		return resolve(pNationality.getVRPTableID(), pSign);
	}

	public static IVRPElement resolve(final int pTableID, final String pSign) {
		final String normalizedSign = pSign.toUpperCase().trim();

		IVRPElement out;
		try{
			switch(pTableID){
				case VRP_DE.ID:
					out = VRP_DE.valueOf(normalizedSign);
					break;
				default:
					return null;
			}
		}catch(final IllegalArgumentException iae){
			/* Nothing found, try to make it shorter. */
			if(normalizedSign.length() == 0){
				return null;
			}else{
				return resolve(pTableID, normalizedSign.substring(0, normalizedSign.length() - 1));
			}
		}

		return out;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
