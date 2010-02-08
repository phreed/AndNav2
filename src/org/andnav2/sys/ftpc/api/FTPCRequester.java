// Created by plusminus on 01:57:35 - 12.01.2009
package org.andnav2.sys.ftpc.api;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import junit.framework.Assert;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.util.StreamUtils;
import org.andnav2.sys.ftpc.exc.FTPCException;


public class FTPCRequester {
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
	 * Submits a Postcode to the FreeThePostcode-Project.
	 * @param pBugGeoPoint
	 * @param pBugDescription
	 * @param pGeoPoint
	 * @param pPostCode1String
	 * @param pPostCode2String
	 * @param pEMailAddress
	 * @return
	 * @throws IOException
	 * @throws FTPCException
	 */
	public static boolean submitPostcode(final GeoPoint pGeoPoint, final String pPostCode1String, final String pPostCode2String, final String pEMailAddress) throws IOException, FTPCException {
		Assert.assertNotNull(pGeoPoint); // TODO More Asserts

		InputStreamReader isr = null;

		try {
			/* Prepare URL. */
			final String urlString = FTPCRequestComposer.createSubmitPostCodeEntitiy(pGeoPoint, pPostCode1String, pPostCode2String, pEMailAddress);

			/* Read repsonse to StringBuilder. */
			isr = new InputStreamReader(new BufferedInputStream(new URL(urlString).openStream(), StreamUtils.IO_BUFFER_SIZE));

			final StringBuilder sb = new StringBuilder();

			int read = 0;
			final char[] buf = new char[StreamUtils.IO_BUFFER_SIZE];

			while((read = isr.read(buf)) != -1) {
				sb.append(buf, 0, read);
			}

			/* Parse Response. */
			return FTPCResponseParser.parseResponse(sb.toString());
		} catch (final Exception e) {
			throw new FTPCException(e);
		} finally {
			StreamUtils.closeStream(isr);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
