// Created by plusminus on 18:14:36 - 15.12.2008
package org.andnav2.sys.osb.api;

import java.util.ArrayList;

import org.andnav2.sys.osb.adt.OpenStreetBug;
import org.andnav2.sys.osb.exc.OSBException;
import org.andnav2.util.constants.Constants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;


/**
 * <pre> &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot; ?&gt;
 * &lt;gpx xmlns=&quot;http://www.topografix.com/GPX/1/1&quot; creator=&quot;OpenStreetBugs&quot; version=&quot;1.1&quot;
 *     xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
 *     xsi:schemaLocation=&quot;http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd&quot;&gt;
 * 	&lt;wpt lat=&quot;48.113433&quot; lon=&quot;-1.662450&quot;&gt;&lt;desc&gt;FirstDescription [PersonA] | SecondDescription [PersonB]&lt;/desc&gt;&lt;/wpt&gt;
 * 	&lt;wpt lat=&quot;48.120044&quot; lon=&quot;-1.658083&quot;&gt;&lt;desc&gt;Some text [someone]&lt;/desc&gt;&lt;/wpt&gt;
 * &lt;/gpx&gt;</pre>
 * @author Nicolas Gramlich
 *
 */
public class OSBGPXParser extends DefaultHandler implements Constants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected StringBuilder sb = new StringBuilder();


	protected boolean inGPX = false;
	protected boolean inWPT = false;
	protected boolean inDescription = false;

	protected ArrayList<OpenStreetBug> mBugs;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<OpenStreetBug> getResponse() throws OSBException {
		// TODO OSB-Errors
		return this.mBugs;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		this.mBugs = new ArrayList<OpenStreetBug>();
		throw new UnsupportedOperationException("CLass not yet implemented !!!");
	}

	@Override
	public void startElement(final String uri, final String localName, final String name, final Attributes attributes) throws SAXException {
		// Reset the stringbuffer
		this.sb.setLength(0);

		if(localName.equals("gpx")){
			this.inGPX = true;
		} else if(localName.equals("wpt")){
			this.inWPT = true;
		} else if(localName.equals("desc")){
			this.inDescription = true;
		} else {
			Log.w(DEBUGTAG, "Unexpected tag: '" + name + "'");
		}

		super.startElement(uri, localName, name, attributes);
	}

	@Override
	public void characters(final char[] chars, final int start, final int length) throws SAXException {
		this.sb.append(chars, start, length);
		super.characters(chars, start, length);
	}

	@Override
	public void endElement(final String uri, final String localName, final String name) throws SAXException {

		if(localName.equals("gpx")){
			this.inGPX = false;
		} else if(localName.equals("wpt")){
			this.inWPT = false;
		} else if(localName.equals("desc")){
			this.inDescription = false;
		} else {
			Log.w(DEBUGTAG, "Unexpected end-tag: '" + name + "'");
		}

		// Reset the stringbuffer
		this.sb.setLength(0);

		super.endElement(uri, localName, name);
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
