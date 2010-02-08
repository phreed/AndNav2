// Created by plusminus on 18:22:30 - 05.11.2008
package org.andnav2.sys.ors.aas;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Assert;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.util.constants.Constants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class AASRequester implements Constants{
	// ===========================================================
	// Constants
	// ===========================================================

	public static final int AAS_MAXMINUTES = 60;

	private static final String URL_AAS_EUROPE = "http://data.giub.uni-bonn.de/openrouteservice/php/AAS_AccessibilityRequest.php";

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
	 * @param gp
	 * @param pMinutes
	 * @throws SAXException
	 * @throws ORSException
	 */
	public static AASResponse request(final GeoPoint gp, final int pMinutes) throws IOException, SAXException, ORSException {
		Assert.assertNotNull(gp);
		if(pMinutes < 1 || pMinutes > AAS_MAXMINUTES) {
			throw new IllegalArgumentException("pMinutes must be between: 1 and " + AAS_MAXMINUTES);
		}

		final String url = URL_AAS_EUROPE;
		final HttpClient httpClient = new DefaultHttpClient();
		//      httpClient.getParams().setParameter("http.socket.timeout", new Integer(1200000)); // 2min

		final HttpPost request = new HttpPost(url);

		// create the multipart request and add the parts to it
		final MultipartEntity requestEntity = AASRequestComposer.create(gp, pMinutes);

		request.setEntity(requestEntity);

		final HttpResponse response = httpClient.execute(request);
		final int status = response.getStatusLine().getStatusCode();

		if (status != HttpStatus.SC_OK) {
			throw new IOException();
		} else {
			{ // DEBUG
				//	            final Reader r = new InputStreamReader(new BufferedInputStream(response.getEntity().getContent()));
				//	            // see above
				//	            final char[] buf = new char[32 * 1024];
				//	            int read;
				//	            final StringBuilder sb = new StringBuilder();
				//	            while((read = r.read(buf)) != -1)
				//	                 sb.append(buf, 0, read);
				//
				//	            Log.d(DEBUGTAG, sb.toString().replace('\n', ' '));
				//        		return null;
			}

			/* Get a SAXParser from the SAXPArserFactory. */
			final SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp;
			try {
				sp = spf.newSAXParser();
			} catch (final ParserConfigurationException e) {
				throw new SAXException(e);
			}

			/* Get the XMLReader of the SAXParser we created. */
			final XMLReader xr = sp.getXMLReader();
			/* Create a new ContentHandler and apply it to the XML-Reader*/
			final AASParser aasParser = new AASParser();
			xr.setContentHandler(aasParser);

			/* Parse the xml-data from our URL. */
			xr.parse(new InputSource(new BufferedInputStream(response.getEntity().getContent())));

			/* The Handler now provides the parsed data to us. */
			return aasParser.getASSResponse();
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
