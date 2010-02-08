// Created by plusminus on 11:39:39 - 04.02.2009
package org.andnav2.sys.serverdowntime;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.andnav2.osm.views.util.StreamUtils;
import org.andnav2.sys.serverdowntime.adt.DowntimeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class DowntimeRequester {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String DOWNTIME_URL = "http://andnav.org/sys/downtimes/andnav2/downtimes.xml";

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

	public static DowntimeList request() throws SAXException, IOException {
		final BufferedInputStream in = new BufferedInputStream(new URL(DOWNTIME_URL).openStream(), StreamUtils.IO_BUFFER_SIZE);

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
		final DowntimeParser dtParser = new DowntimeParser();
		xr.setContentHandler(dtParser);

		/* Parse the xml-data from our URL. */
		xr.parse(new InputSource(in));

		/* The Handler now provides the parsed data to us. */
		return dtParser.getDowntimes();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
