package org.andnav2.sys.osb.api;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;

import junit.framework.Assert;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.util.StreamUtils;
import org.andnav2.sys.osb.adt.OpenStreetBug;
import org.andnav2.sys.osb.exc.OSBException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *	The Google App Engine data-storage is protected against resources
 *	over-consumption. If you plan to use OSB API for a regular use (each
 *	person can execute some requests as on the OSB web site), it's okay.
 *	But if you plan to make an automatic dump of all the database or
 *	something like this, do put at least 0.5 seconds of sleep between each
 *	getGPX or addPOIexec request or you'll face an access-denial. There is
 *	also a risk that all OpenStreetBugs will be closed for a few minutes.
 *
 * @author Nicolas Gramlich
 *
 * TODO Throwing behavior of the different methods is now the same. Should be made equal.
 */
public class OSBRequester {
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

	public static ArrayList<OpenStreetBug> getBugsFromBoundingBoxE6(final BoundingBoxE6 pBBoxE6) throws OSBException {
		InputStreamReader isr = null;

		try {
			/* Prepare URL. */
			final String urlString = OSBRequestComposer.createBugsFromBoundingBoxE6Entitiy(pBBoxE6, false);

			/* Read repsonse to StringBuilder. */
			isr = new InputStreamReader(new BufferedInputStream(new URL(urlString).openStream(), StreamUtils.IO_BUFFER_SIZE));

			final StringBuilder sb = new StringBuilder();

			int read = 0;
			final char[] buf = new char[StreamUtils.IO_BUFFER_SIZE];

			while((read = isr.read(buf)) != -1) {
				sb.append(buf, 0, read);
			}

			/* Parse Response. */
			return OSBAjaxResponseParser.parseResponse(sb.toString());
		} catch (final Exception e) {
			throw new OSBException(e);
		} finally {
			StreamUtils.closeStream(isr);
		}
	}

	/**
	 * Submits a
	 * @param pBugGeoPoint
	 * @param pBugDescription
	 * @return
	 * @throws IOException
	 */
	public static int submitBug(final GeoPoint pBugGeoPoint, final String pBugDescription) throws IOException {
		Assert.assertNotNull(pBugGeoPoint);

		final String url = "http://openstreetbugs.appspot.com/addPOIexec";
		final HttpClient httpClient = new DefaultHttpClient();

		final HttpPost request = new HttpPost(url);

		/* Create the MultiPart Request-Entity and add the parts to it. */
		final MultipartEntity requestEntity = OSBRequestComposer.createSubmitBugEntitiy(pBugGeoPoint, pBugDescription);

		request.setEntity(requestEntity);

		final HttpResponse response = httpClient.execute(request);
		final int status = response.getStatusLine().getStatusCode();

		if (status != HttpStatus.SC_OK) {
			throw new IOException();
		} else {
			/* Retrieve the response-string. */

			final Reader r = new InputStreamReader(new BufferedInputStream(response.getEntity().getContent()));
			// see above
			final char[] buf = new char[128];
			int read;
			final StringBuilder sb = new StringBuilder();
			while((read = r.read(buf)) != -1) {
				sb.append(buf, 0, read);
			}

			final String responseString = sb.toString();

			final String[] lines = responseString.split("\n");


			/* 'Parse' the simple response. */
			if(lines[0].equals("ok")) {
				return Integer.parseInt(lines[1]);
			} else {
				return -1;
			}
		}
	}

	public static boolean appendToBug(final OpenStreetBug pOpenStreetBug, final String pCommentToAppend) throws IOException {
		Assert.assertNotNull(pOpenStreetBug);

		final String url = "http://openstreetbugs.appspot.com/editPOIexec";
		final HttpClient httpClient = new DefaultHttpClient();

		final HttpPost request = new HttpPost(url);

		/* Create the MultiPart Request-Entity and add the parts to it. */
		final MultipartEntity requestEntity = OSBRequestComposer.createAppendToBugEntitiy(pOpenStreetBug, pCommentToAppend);

		request.setEntity(requestEntity);

		final HttpResponse response = httpClient.execute(request);
		final int status = response.getStatusLine().getStatusCode();

		if (status != HttpStatus.SC_OK) {
			throw new IOException();
		} else {
			/* Retrieve the response-string. */

			final Reader r = new InputStreamReader(new BufferedInputStream(response.getEntity().getContent()));
			// see above
			final char[] buf = new char[128];
			int read;
			final StringBuilder sb = new StringBuilder();
			while((read = r.read(buf)) != -1) {
				sb.append(buf, 0, read);
			}

			final String responseString = sb.toString();

			/* 'Parse' the simple response. */
			if(responseString.equals("ok")) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean closeBug(final OpenStreetBug pOpenStreetBug) throws IOException {
		Assert.assertNotNull(pOpenStreetBug);

		final String url = "http://openstreetbugs.appspot.com/closePOIexec";
		final HttpClient httpClient = new DefaultHttpClient();

		final HttpPost request = new HttpPost(url);

		/* Create the MultiPart Request-Entity and add the parts to it. */
		final MultipartEntity requestEntity = OSBRequestComposer.createCloseBugEntitiy(pOpenStreetBug);

		request.setEntity(requestEntity);

		final HttpResponse response = httpClient.execute(request);
		final int status = response.getStatusLine().getStatusCode();

		if (status != HttpStatus.SC_OK) {
			throw new IOException();
		} else {
			/* Retrieve the response-string. */

			final Reader r = new InputStreamReader(new BufferedInputStream(response.getEntity().getContent()));
			// see above
			final char[] buf = new char[128];
			int read;
			final StringBuilder sb = new StringBuilder();
			while((read = r.read(buf)) != -1) {
				sb.append(buf, 0, read);
			}

			final String responseString = sb.toString();

			/* 'Parse' the simple response. */
			if(responseString.equals("ok")) {
				return true;
			} else {
				return false;
			}
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
