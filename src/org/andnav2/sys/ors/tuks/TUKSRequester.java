// Created by plusminus on 00:12:51 - 20.01.2009
package org.andnav2.sys.ors.tuks;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.sys.ors.adt.Error;
import org.andnav2.sys.ors.adt.ts.TrafficItem;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class TUKSRequester {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final String BBC_TRAFFICFEED_URL = "http://www.bbc.co.uk/travelnews/tpeg/en/local/rtm/rtm_tpeg.xml";

	public static final BoundingBoxE6 BBOX_TUKS = new BoundingBoxE6(57.9311,3.5425,49.955269,-8.164723);

	private static String REQUESTURL_UNI = "http://openls.giub.uni-bonn.de/geoserver-osm/wfs";
	private static String REQUESTURL_HOME = "http://88.153.123.28/geoserver/wfs";

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

	public static List<TrafficItem> request(final BoundingBoxE6 pBoundingBoxE6) throws MalformedURLException, IOException, SAXException, ORSException{
		final URL requestURL = new URL(REQUESTURL_UNI);

		final HttpURLConnection acon = (HttpURLConnection) requestURL.openConnection();
		acon.setAllowUserInteraction(false);
		acon.setRequestMethod("POST");
		acon.setRequestProperty("Content-Type", "application/xml");
		acon.setDoOutput(true);
		acon.setDoInput(true);
		acon.setUseCaches(false);

		final BufferedWriter xmlOut;
		try{
			xmlOut = new BufferedWriter(new OutputStreamWriter(acon.getOutputStream()));
		}catch(final SocketException se){
			throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN, Error.SEVERITY_ERROR, "org.andnav2.ors.rs.RSRequester.request(...)", "Host unreachable."));
		}catch(final UnknownHostException uhe){
			throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN, Error.SEVERITY_ERROR, "org.andnav2.ors.rs.RSRequester.request(...)", "Host unresolved."));
		}

		//		final String trafficRequest = TUKSRequestComposer.createGML1(pBoundingBoxE6);
		final String trafficRequest = TUKSRequestComposer.createGML2(pBoundingBoxE6);
		xmlOut.write(trafficRequest);
		xmlOut.flush();
		xmlOut.close();


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
		final TUKSParser tuksParser = new TUKSParser();
		xr.setContentHandler(tuksParser);

		/* Parse the xml-data from our URL. */
		xr.parse(new InputSource(new BufferedInputStream(acon.getInputStream())));

		/* The Handler now provides the parsed data to us. */
		return tuksParser.getTrafficFeatures();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
