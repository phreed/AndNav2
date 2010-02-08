// Created by plusminus on 19:27:39 - 23.11.2008
package org.andnav2.sys.postcode;

import java.io.IOException;

import org.andnav2.sys.ors.adt.GeocodedAddress;
import org.andnav2.sys.ors.exceptions.ORSException;


public interface IPostCodeRequester {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public GeocodedAddress request(final String aPostcode) throws IOException, ORSException;

	// ===========================================================
	// Methods
	// ===========================================================
}
