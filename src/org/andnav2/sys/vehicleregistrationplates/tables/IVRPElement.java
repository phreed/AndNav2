// Created by plusminus on 2:28:52 PM - Mar 6, 2009
package org.andnav2.sys.vehicleregistrationplates.tables;

import org.andnav2.sys.ors.adt.lus.Country;


public interface IVRPElement {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public Country getNationality();
	public String getAbbreviation();
	public String getRepresentation();
}
