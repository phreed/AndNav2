// Created by plusminus on 7:20:47 PM - Feb 18, 2009
package org.andnav2.ui.map.hud;

import android.view.View;


public interface IHUDImpl {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public int getID();

	public int getNameResourceID();
	public int getDescriptionResourceID();

	public void init(final View pMapRootView);

	/**
	 * This method has to return sth for: <code>IHUDImplVariation.VARIATION_DEFAULT_ID</code> otherwise it will throw an {@link IllegalArgumentException}
	 * @param pVariation
	 * @param pVariationID
	 * @return
	 * @throws IllegalArgumentException
	 */
	public IHUDImplVariation getVariation(final int pVariationID) throws IllegalArgumentException;
	public IHUDImplVariation[] getVariations();
	public int getCountOfVariations();

	public IHUDRemainingSummaryView getRemainingSummaryView();
	public IHUDNextActionView getNextActionView();
	public IHUDNextActionView getUpperNextActionView();
	public IHUDTurnDescriptionView getTurnTurnDescriptionView();

	public void setUpperNextActionViewNecessary(final boolean pNecessary);
	public void invalidateViews();
}
