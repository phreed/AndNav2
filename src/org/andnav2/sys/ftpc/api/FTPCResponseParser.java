// Created by plusminus on 19:37:45 - 12.01.2009
package org.andnav2.sys.ftpc.api;


public class FTPCResponseParser {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * Check if the parameter s contains the string <code>"You should have an email"</code> which indicates a successful submission.
	 * @param s
	 * @return <code>true</code> on a successful response, false otherwise.
	 */
	public static boolean parseResponse(final String s) {
		return s.contains("You should have an email");
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
