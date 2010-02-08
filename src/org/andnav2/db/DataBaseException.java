// Created by plusminus on 14:21:28 - 15.02.2008
/**
 * 
 */
package org.andnav2.db;

public class DataBaseException extends Exception {

	// ===========================================================
	// Final Fields
	// ===========================================================

	private static final long serialVersionUID = 4795542397431337822L;

	// ===========================================================
	// Constructors
	// ===========================================================

	public DataBaseException() {

	}

	public DataBaseException(final String detailMessage) {
		super(detailMessage);
	}

	public DataBaseException(final Throwable throwable) {
		super(throwable);

	}

	public DataBaseException(final String detailMessage, final Throwable throwable) {
		super(detailMessage, throwable);
	}
}
