// Created by plusminus on 5:09:46 PM - Feb 18, 2009
package org.andnav2.ui.map.hud;

import android.view.View.OnClickListener;



public interface IHUDRemainingSummaryView extends IHUDView {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public abstract void setEstimatedRestSeconds(final int pEstimatedRestSeconds);

	public abstract void setMetersDrivenSession(final int pMetersDriven);

	public abstract void setDataConnectionStrength(final int pDataConnectionStrength);

	public abstract void setGPSConnectionStrength(final int connectionStrength);

	public abstract void setRemainingSummaryOnClickListener(final OnClickListener pOnClickListener);
}
