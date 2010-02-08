// Created by plusminus on 19:32:00 - 10.04.2008
package org.andnav2.ui.common.views;

import org.andnav2.sys.ors.adt.lus.ICountrySubdivision;

import android.content.Context;
import android.widget.LinearLayout;

/** Wrapper class around ImageView transporting
 * the Country-Information to the
 * OnItemClickListener and the
 * OnItemSelectedListener of the GridView.*/
public class CountrySubdivisionFlagView extends LinearLayout {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected ICountrySubdivision mSubdivision;

	// ===========================================================
	// Constructors
	// ===========================================================

	public CountrySubdivisionFlagView(final Context context, final ICountrySubdivision pSubdivision) {
		super(context);
		this.mSubdivision = pSubdivision;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ICountrySubdivision getSubdivision() {
		return this.mSubdivision;
	}

	public void setSubdivison(final ICountrySubdivision pSubdivison) {
		this.mSubdivision = pSubdivison;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
