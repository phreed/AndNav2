// Created by plusminus on 16:55:27 - 16.02.2009
package org.andnav2.paypal.util;

import org.andnav2.R;

import android.content.Context;


public enum DonationFrequency {
	// ===========================================================
	// Elements
	// ===========================================================

	ONCE(R.string.donate_frequency_once, 1),
	TWICE(R.string.donate_frequency_twice, 2),
	SIX_TIMES(R.string.donate_frequency_six_times, 6),
	TWELVE_TIMES(R.string.donate_frequency_twelve_times, 12),
	THIRTY_TIMES(R.string.donate_frequency_thirty_times, 30),
	THREEHUNDREDSIXTYFIVE_TIMES(R.string.donate_frequency_threehundredsixtyfive_times, 365);

	// ===========================================================
	// Constants
	// ===========================================================


	// ===========================================================
	// Fields
	// ===========================================================

	public final int NAMERESID;
	public final int PERIODS;

	// ===========================================================
	// Constructors
	// ===========================================================

	private DonationFrequency(final int pNameResID, final int pPeriods){
		this.NAMERESID = pNameResID;
		this.PERIODS = pPeriods;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public String toString(final Context ctx){
		return ctx.getString(this.NAMERESID);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
