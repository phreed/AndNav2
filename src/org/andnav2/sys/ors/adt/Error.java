// Created by plusminus on 19:17:17 - 06.11.2008
package org.andnav2.sys.ors.adt;


public class Error {
	// ===========================================================
	// Constants
	// ===========================================================

	// TODO Check values
	public static final String ERRORCODE_UNKNOWN = "Unknown";
	public static final String SEVERITY_ERROR = "Error";
	public static final String SEVERITY_PROBLEM = "Problem";

	// ===========================================================
	// Fields
	// ===========================================================

	private final String mErrorCode;
	private final String mSeverity;
	private final String mLocationPath;
	private final String mMessage;

	// ===========================================================
	// Constructors
	// ===========================================================

	public Error(final String errorCode, final String severity, final String locationPath, final String message) {
		this.mErrorCode = errorCode;
		this.mSeverity = severity;
		this.mLocationPath = locationPath;
		this.mMessage = message;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getErrorCode() {
		return this.mErrorCode;
	}

	public String getSeverity() {
		return this.mSeverity;
	}

	public String getLocationPath() {
		return this.mLocationPath;
	}

	public String getMessage() {
		return this.mMessage;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public String toString(){
		return new StringBuilder()
		.append("Errorcode: ").append(this.mErrorCode).append('\n')
		.append("Severity: ").append(this.mSeverity).append('\n')
		.append("LocationPath: ").append(this.mLocationPath).append('\n')
		.append('\n')
		.append("Message: ").append(this.mMessage)
		.toString();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public String toUserString(){
		return new StringBuilder()
		.append("Errorcode: ").append(this.mErrorCode).append('\n')
		.append("Severity: ").append(this.mSeverity).append('\n')
		.toString();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
