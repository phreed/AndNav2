// Created by plusminus on 21:08:55 - 25.01.2009
package org.andnav2.sys.ors.aas;

import java.io.UnsupportedEncodingException;

import org.andnav2.osm.adt.GeoPoint;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;


public class AASRequestComposer {
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

	public static MultipartEntity create(final GeoPoint gp, final int pMinutes) throws UnsupportedEncodingException {
		final MultipartEntity requestEntity = new MultipartEntity();
		requestEntity.addPart("Position", new StringBody(gp.toInvertedDoubleString())); // Longitude first
		requestEntity.addPart("Minutes", new StringBody(String.valueOf(pMinutes)));
		return requestEntity;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
