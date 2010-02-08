// Created by plusminus on 01:22:18 - 26.01.2009
package org.andnav2.osm.views.util;

/**
 * Adapted Knuth-Morris-Pratt Algorithm to find byte[]-patterns in byte[].
 * @author Nicolas Gramlich
 */
public class KMPMatcher {
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
	 * @param data
	 * @param pattern
	 * @return
	 */
	public static int indexOf(final byte[] data, final byte[] pattern) {
		final int[] failure = new int[pattern.length];

		int j = 0;
		if (data.length == 0) {
			return -1;
		}

		for (int i = 0; i < data.length; i++) {
			while (j > 0 && pattern[j] != data[i]) {
				j = failure[j - 1];
			}
			if (pattern[j] == data[i]) {
				j++;
			}
			if (j == pattern.length) {
				return i - pattern.length + 1;
			}
		}
		return -1;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
