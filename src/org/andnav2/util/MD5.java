// Created by plusminus on 10:35:21 - 01.02.2009
package org.andnav2.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5 {
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

	public static String hash(final String in) {
		if(in == null) {
			throw new IllegalArgumentException("'in' has to be != null");
		}
		try {
			final MessageDigest md5 = MessageDigest.getInstance("MD5");
			if(md5 == null) {
				return in;
			}

			try {
				md5.update(in.getBytes("UTF-8"));
			} catch (final UnsupportedEncodingException e) {
				return in;
			}

			return new BigInteger(1,md5.digest()).toString(16);
		} catch (final NoSuchAlgorithmException ex) {
			return in;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
