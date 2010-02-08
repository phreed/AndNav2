// Created by plusminus on 23:29:45 - 27.01.2009
package org.andnav2.adt;

import org.andnav2.sys.ors.adt.lus.Country;



public class TrafficFeed {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final String mURL;
	private final String mName;
	private final Country mNationality;

	// ===========================================================
	// Constructors
	// ===========================================================

	public TrafficFeed(final String aURL, final String aName, final Country aNationality) {
		this.mURL = aURL;
		this.mName = aName;
		this.mNationality = aNationality;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getURL() {
		return this.mURL;
	}

	public String getName() {
		return this.mName;
	}

	public Country getNationality() {
		return this.mNationality;
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
