// Created by plusminus on 5:22:00 PM - Feb 18, 2009
package org.andnav2.ui.map.hud;

import org.andnav2.adt.UnitSystem;


public interface IHUDView {
	// ===========================================================
	// Methods
	// ===========================================================

	/** Do not use setOnClickListener() on your view!
	 * React on clicks to the view in this method. */
	public abstract void onClick();

	/** Set distances or so to zero. */
	public abstract void reset();

	/** Tip: Recycle Bitmaps in here or set drawables/contexts to null. */
	public abstract void recycle();

	/**
	 * @param aMeterDistance in meters!
	 */
	public abstract void setDistance(final int aMeterDistance);

	public abstract void setUnitSystem(final UnitSystem aUnitSystem);

	/** From 0(best) to 3(worst). */
	public abstract void setDisplayQuality(final int aDisplayQuality);
}
