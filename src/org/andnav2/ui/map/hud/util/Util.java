// Created by plusminus on 7:01:38 PM - Feb 23, 2009
package org.andnav2.ui.map.hud.util;

import org.andnav2.ui.map.hud.IHUDImplVariation;


public class Util {
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

	public static IHUDImplVariation getVariation(final IHUDImplVariation[] pHUDImplVariations, final int pVariationID){
		for(final IHUDImplVariation v : pHUDImplVariations) {
			if(v.getVariationID() == pVariationID) {
				return v;
			}
		}

		if(pVariationID != IHUDImplVariation.VARIATION_DEFAULT_ID) {
			return getVariation(pHUDImplVariations, IHUDImplVariation.VARIATION_DEFAULT_ID);
		} else {
			throw new IllegalArgumentException("Variation: " + pVariationID + "  and VARIATION_DEFAULT_ID not found.");
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
