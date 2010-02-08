package org.andnav2.sys.ors.exceptions;

import java.util.ArrayList;

import org.andnav2.sys.ors.adt.Error;

/**
 * @since 2008-04-06 22:14:13
 * @author Nicolas 'plusminus' Gramlich
 * License:
 * @see Creative Commons Attribution-Noncommercial-Share Alike 2.0 Germany License .
 * Permissions beyond the scope of this license may be requested at plusminus {at} anddev {dot} org
 */
public class ORSException extends Exception {
	// ===========================================================
	// Final Fields
	// ===========================================================

	/** Used for serialization. */
	private static final long serialVersionUID = -3776930343923046927L;

	// ===========================================================
	// Fields
	// ===========================================================

	private final ArrayList<Error> mErrors;

	// ===========================================================
	// Constructors
	// ===========================================================

	public ORSException(final Error error) {
		super();
		this.mErrors = new ArrayList<Error>();
		this.mErrors.add(error);
	}

	public ORSException(final ArrayList<Error> pErrors) {
		super();
		this.mErrors = pErrors;
	}

	public ORSException( final String detailMessage, final Throwable throwable, final ArrayList<Error> pErrors) {
		super(detailMessage, throwable);
		this.mErrors = pErrors;
	}

	public ORSException(final String detailMessage, final ArrayList<Error> pErrors) {
		super(detailMessage);
		this.mErrors = pErrors;
	}

	public ORSException(final Throwable throwable, final ArrayList<Error> pErrors) {
		super(throwable);
		this.mErrors = pErrors;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<Error> getErrors(){
		return this.mErrors;
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
