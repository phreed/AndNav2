// Created by plusminus on 11:39:39 - 04.02.2009
package org.andnav2.sys.maptilepacks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.andnav2.osm.views.util.StreamUtils;
import org.andnav2.sys.maptilepacks.adt.MapTilePack;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class MapTilePackRequester {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String MAPTILEPACK_URL = "http://www.andnav.org/sys/maptilepacks/andnav2/maptilepacks.xml";

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

	public static List<MapTilePack> request() throws SAXException, IOException {
		final BufferedInputStream in = new BufferedInputStream(new URL(MAPTILEPACK_URL).openStream(), StreamUtils.IO_BUFFER_SIZE);

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
		final MapTilePackParser mtParser = new MapTilePackParser();
		xr.setContentHandler(mtParser);

		/* Parse the xml-data from our URL. */
		xr.parse(new InputSource(in));

		/* The Handler now provides the parsed data to us. */
		return mtParser.getMapTilePacks();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
