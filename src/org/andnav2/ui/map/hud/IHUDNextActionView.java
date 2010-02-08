// Created by plusminus on 5:29:31 PM - Feb 18, 2009
package org.andnav2.ui.map.hud;

import org.andnav2.adt.UnitSystem;

import android.view.View.OnClickListener;



public interface IHUDNextActionView extends IHUDView {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public abstract void setTurnAngle(final float aAngle);

	public abstract void setCurrentMeterSpeed(final float aMeterSpeed);

	public abstract void showTargetReached();

	public abstract void setNextActionOnClickListener(final OnClickListener pOnClickListener);

	/** DummyAdapter doing nothing.  */
	public static class IHUDNextActionViewAdapter implements IHUDNextActionView{

		public void setCurrentMeterSpeed(final float meterSpeed) { }

		public void setNextActionOnClickListener(final OnClickListener onClickListener) { }

		public void setTurnAngle(final float angle) { }

		public void showTargetReached() { }

		public void onClick() { }

		public void recycle() { }

		public void reset() { }

		public void setDisplayQuality(final int displayQuality) { }

		public void setDistance(final int meterDistance) { }

		public void setUnitSystem(final UnitSystem unitSystem) { }
	}
}
