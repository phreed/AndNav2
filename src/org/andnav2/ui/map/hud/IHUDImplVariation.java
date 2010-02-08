// Created by plusminus on 7:20:47 PM - Feb 18, 2009
package org.andnav2.ui.map.hud;

import org.andnav2.osm.views.overlay.util.DirectionArrowDescriptor;



public interface IHUDImplVariation {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static int VARIATION_DEFAULT_ID = 0;

	// ===========================================================
	// Methods
	// ===========================================================

	public int getVariationID();
	/**
	 * This method has to return a valid resourceID.
	 * @return the resourceID (R.layout.xyz)
	 */
	public int getLayoutID();

	public int getScreenshotResourceID();
	public int getDescriptionStringID();
	public DirectionArrowDescriptor getDirectionArrowDescriptor();
}
