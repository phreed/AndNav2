// Created by plusminus on 10:48:38 - 04.02.2009
package org.andnav2.sys.maptilepacks;

import java.util.ArrayList;
import java.util.List;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.sys.maptilepacks.adt.MapTilePack;
import org.andnav2.util.constants.Constants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;


public class MapTilePackParser extends DefaultHandler implements Constants {
	// ====================================
	// Constants
	// ====================================

	// ====================================
	// Fields
	// ====================================

	private final StringBuilder sb = new StringBuilder();

	private List<MapTilePack> mMapTileList;

	private boolean inMapTilePacks = false;
	private boolean inMapTilePack = false;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public List<MapTilePack> getMapTilePacks() {
		return this.mMapTileList;
	}

	// ====================================
	// Methods from Superclasses
	// ====================================

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		this.mMapTileList = new ArrayList<MapTilePack>();
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
		this.sb.setLength(0);

		final String tagName = (localName.length() == 0) ? qName : localName;

		if(tagName.equals("maptilepacks")){
			this.inMapTilePacks = true;
		} else if(tagName.equals("maptilepack")){
			this.inMapTilePack = true;
			final String name = attributes.getValue("", "name");

			final String timeStampString = attributes.getValue("", "timestamp");

			final String maxZoomString = attributes.getValue("", "maxzoom");
			final int maxZoom = Integer.parseInt(maxZoomString);

			final String downloadURL = attributes.getValue("", "downloadurl");
			final String screenshotURL = attributes.getValue("", "screenshoturl");

			final String downloadsizembString = attributes.getValue("", "downloadsizemb");
			final int downloadsizemb = Integer.parseInt(downloadsizembString);

			/* BoundingBox. */
			final String bbNorthString = attributes.getValue("", "bbnorth");
			final double bbNorth = Double.parseDouble(bbNorthString);
			final String bbSouthString = attributes.getValue("", "bbsouth");
			final double bbSouth = Double.parseDouble(bbSouthString);
			final String bbEastString = attributes.getValue("", "bbeast");
			final double bbEast = Double.parseDouble(bbEastString);
			final String bbWestString = attributes.getValue("", "bbwest");
			final double bbWest = Double.parseDouble(bbWestString);


			final BoundingBoxE6 bbE6 = new BoundingBoxE6(bbNorth, bbEast, bbSouth, bbWest);

			this.mMapTileList.add(new MapTilePack(name, timeStampString, maxZoom, downloadURL, screenshotURL, downloadsizemb, bbE6));
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

		if(tagName.equals("maptilepacks")){
			this.inMapTilePacks = false;
		} else if(tagName.equals("maptilepack")){
			this.inMapTilePack = false;
		} else {
			Log.w(DEBUGTAG, "Unexpected end-tag: '" + tagName + "'");
		}

		// Reset the stringbuffer
		this.sb.setLength(0);

		super.endElement(uri, localName, qName);
	}
}
