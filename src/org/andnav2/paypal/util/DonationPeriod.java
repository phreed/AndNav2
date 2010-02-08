// Created by plusminus on 16:55:27 - 16.02.2009
package org.andnav2.paypal.util;

import org.andnav2.R;

import android.content.Context;


public enum DonationPeriod {
	// ===========================================================
	// Elements
	// ===========================================================

	DAILY(R.string.donate_period_daily, 'D'),
	WEEKLY(R.string.donate_period_weekly, 'W'),
	MONTHLY(R.string.donate_period_monthly, 'M'),
	YEARLY(R.string.donate_period_yearly, 'Y');

	// ===========================================================
	// Constants
	// ===========================================================


	// ===========================================================
	// Fields
	// ===========================================================

	public final int NAMERESID;
	public final char IDENTIFIER;

	// ===========================================================
	// Constructors
	// ===========================================================

	private DonationPeriod(final int pNameResID, final char pIdentifier){
		this.NAMERESID = pNameResID;
		this.IDENTIFIER = pIdentifier;
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
