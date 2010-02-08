// Created by plusminus on 19:15:38 - 23.11.2008
package org.andnav2.sys.postcode.uk_bs_7666;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.andnav2.sys.ors.adt.Error;
import org.andnav2.sys.ors.adt.GeocodedAddress;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.sys.postcode.IPostCodeRequester;
import org.apache.http.util.ByteArrayBuffer;


public class Requester implements IPostCodeRequester{
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String BASEURL = "http://www.andnav.org/sys/postcodes/uk_bs_7666/query.php?q=";

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

	public GeocodedAddress request(final String aPostcode) throws IOException, ORSException{
		final URL requestURL = new URL(BASEURL.concat(aPostcode.trim().replace(" ", "%20")));
		//		Log.d(Constants.DEBUGTAG, "Requesting: " + requestURL.toExternalForm());
		final URLConnection conn = requestURL.openConnection();
		conn.connect();
		final InputStream is = conn.getInputStream();
		/* Buffered is always good for a performance plus. */
		final ByteArrayBuffer bab = new ByteArrayBuffer(1000);
		final BufferedInputStream bis = new BufferedInputStream(is, 8000); // SDK says to use 8k buffer.
		final byte[] buf = new byte[256];
		int len = 0;
		while((len = bis.read(buf)) > 0) {
			bab.append(buf, 0, len);
		}

		return createFromResult(new String(bab.toByteArray()).trim());
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private static GeocodedAddress createFromResult(final String s) throws ORSException {
		if(s.compareToIgnoreCase("error") == 0){
			throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN,
					Error.SEVERITY_ERROR,
					"org.andnav2.sys.postcode.PostCodeRequester.createFromResult(final String postCode)",
			"Postcode could not be resolved."));
		}

		/* Example: "AB21 0GU 57204940 -2211980" */
		final String[] parts = s.split(" ", 3);

		final int lat = Integer.parseInt(parts[0]);
		final int lon = Integer.parseInt(parts[1]);

		final GeocodedAddress out = new GeocodedAddress(lat, lon);
		out.setPostalCode(parts[2]);

		return out;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
