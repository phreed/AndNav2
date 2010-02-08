// Created by plusminus on 21:37:42 - 09.09.2008
package org.andnav2.ui.map.hud.impl.basic.views;

import org.andnav2.adt.UnitSystem;
import org.andnav2.preferences.PreferenceConstants;
import org.andnav2.ui.map.hud.IHUDView;
import org.andnav2.util.constants.Constants;
import org.andnav2.util.constants.TimeConstants;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;


public abstract class HUDBaseView extends ImageView implements IHUDView, Constants, TimeConstants, PreferenceConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final Paint mFullOpaquePaint, mHudTextPaint;

	protected int mDistance = NOT_SET;
	protected UnitSystem mUnitSystem = UnitSystem.IMPERIAL;

	protected final String[] mDistanceStrings = new String[2];

	// ===========================================================
	// Constructors
	// ===========================================================

	public HUDBaseView(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		this.mFullOpaquePaint = new Paint();
		this.mHudTextPaint = new Paint();
		this.mHudTextPaint.setAntiAlias(false);
		this.mHudTextPaint.setStrokeWidth(1);
		this.mHudTextPaint.setFakeBoldText(true);
		this.mHudTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void onClick(){
		// Nothing
	}

	public void reset() {
		this.mDistance = NOT_SET;
	}

	/**
	 * @param aMeterDistance in meters!
	 */
	public void setDistance(final int aMeterDistance){
		this.mDistance = aMeterDistance;
	}

	public void setUnitSystem(final UnitSystem aUnitSystem){
		this.mUnitSystem = aUnitSystem;
	}

	public void setDisplayQuality(final int aDisplayQuality){
		switch(aDisplayQuality){
			case PREF_DISPLAYQUALITY_BEST:
			case PREF_DISPLAYQUALITY_HIGH:
			case PREF_DISPLAYQUALITY_STANDARD:
				this.mHudTextPaint.setAntiAlias(true);
			case PREF_DISPLAYQUALITY_LOW:
				break;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected void refreshDistanceStrings(){
		refreshDistanceStrings(this.mDistance);
	}

	/**
	 * Writes data to the parameter <code>mDistanceStrings</code> in the specific UnitSystem.
	 */
	protected void refreshDistanceStrings(final int aDistance){
		this.mUnitSystem.getDistanceString(aDistance, this.mDistanceStrings);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
