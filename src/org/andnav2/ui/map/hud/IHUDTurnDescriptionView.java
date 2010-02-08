// Created by plusminus on 8:39:01 PM - Feb 18, 2009
package org.andnav2.ui.map.hud;

import android.view.View.OnClickListener;


public interface IHUDTurnDescriptionView {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void reset();
	public CharSequence getTurnDescription();
	public void setTurnDescription(final String aTurnDescription);
	public void setTurnDescriptionOnClickListener(final OnClickListener pOnClickListener);
}
