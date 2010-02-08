// Created by plusminus on 19:32:00 - 10.04.2008
package org.andnav2.ui.common.views;

import org.andnav2.sys.ors.adt.lus.Country;

import android.content.Context;
import android.widget.ImageView;

/** Wrapper class around ImageView transporting
 * the Country-Information to the
 * OnItemClickListener and the
 * OnItemSelectedListener of the GridView.*/
public class CountryFlagView extends ImageView {

	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected Country itsNation;

	// ===========================================================
	// Constructors
	// ===========================================================

	public CountryFlagView(final Context context, final Country c) {
		super(context);
		this.itsNation = c;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Country getNation() {
		return this.itsNation;
	}

	public void setNation(final Country aNation) {
		this.itsNation = aNation;
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
