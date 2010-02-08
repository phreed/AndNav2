// Created by plusminus on 10:48:38 - 04.02.2009
package org.andnav2.sys.serverdowntime;

import java.text.ParseException;

import org.andnav2.sys.serverdowntime.adt.Downtime;
import org.andnav2.sys.serverdowntime.adt.DowntimeList;
import org.andnav2.util.constants.Constants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;


public class DowntimeParser extends DefaultHandler implements Constants {
	// ====================================
	// Constants
	// ====================================

	// ====================================
	// Fields
	// ====================================

	private final StringBuilder sb = new StringBuilder();

	private DowntimeList mDowntimeList;

	private boolean inDowntimes = false;
	private boolean inDowntime = false;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public DowntimeList getDowntimes() {
		return this.mDowntimeList;
	}

	// ====================================
	// Methods from Superclasses
	// ====================================

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		this.mDowntimeList = new DowntimeList();
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
		this.sb.setLength(0);

		final String tagName = (localName.length() == 0) ? qName : localName;

		if(tagName.equals("downtimes")){
			this.inDowntimes = true;
		} else if(tagName.equals("downtime")){
			this.inDowntime = true;
			final String periodString = attributes.getValue("", "period");
			final String durationString = attributes.getValue("", "duration");
			final String startString = attributes.getValue("", "start");
			final String descriptionString = attributes.getValue("", "description");
			try {
				this.mDowntimeList.add(new Downtime(periodString, durationString, startString, descriptionString));
			} catch (final ParseException e) {
				Log.d(DEBUGTAG, "Problem parsing downtime: ", e);
			}
		} else {
			Log.w(DEBUGTAG, "Unexpected tag: '" + tagName + "'");
		}
		super.startElement(uri, localName, qName, attributes);
	}

	@Override
	public void characters(final char[] chars, final int start, final int length) throws SAXException {
		this.sb.append(chars, start, length);
		super.characters(chars, start, length);
	}

	@Override
	public void endElement(final String uri, final String localName, final String qName) throws SAXException {

		final String tagName = (localName.length() == 0) ? qName : localName;

		if(tagName.equals("downtimes")){
			this.inDowntimes = false;
		} else if(tagName.equals("downtime")){
			this.inDowntime = false;
		} else {
			Log.w(DEBUGTAG, "Unexpected end-tag: '" + tagName + "'");
		}

		// Reset the stringbuffer
		this.sb.setLength(0);

		super.endElement(uri, localName, qName);
	}
}
