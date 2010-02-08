// Created by plusminus on 14:11:09 - 21.09.2008
package org.andnav2.osm.api.util.constants;

import org.openstreetmap.api.constants.OSMBaseAPIConstants;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public interface OSMTraceAPIConstants extends OSMBaseAPIConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final int MINGEOPOINTS_FOR_OSM_CONTRIBUTION = 100;
	public static final int MINDIAGONALMETERS_FOR_OSM_CONTRIBUTION = 200;

	public static final String OSM_CREATOR_INFO = "AndNav - http://www.andnav.org - Android Navigation System";

	/* GPX-Constants */
	public static final String GPX_VERSION = "1.1";
	public static final String GPX_TAG = "<gpx version=\"" + GPX_VERSION + "\" creator=\"%s\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.topografix.com/GPX/1/1\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">";
	public static final String GPX_TAG_CLOSE = "</gpx>";
	public static final String GPX_TAG_TIME = "<time>%s</time>";
	public static final String GPX_TAG_TRACK = "<trk>";
	public static final String GPX_TAG_TRACK_CLOSE = "</trk>";
	public static final String GPX_TAG_TRACK_NAME = "<name>%s</name>";
	public static final String GPX_TAG_TRACK_SEGMENT = "<trkseg>";
	public static final String GPX_TAG_TRACK_SEGMENT_CLOSE = "</trkseg>";
	public static final String GPX_TAG_TRACK_SEGMENT_POINT = "<trkpt lat=\"%f\" lon=\"%f\">";
	public static final String GPX_TAG_TRACK_SEGMENT_POINT_CLOSE = "</trkpt>";
	public static final String GPX_TAG_TRACK_SEGMENT_POINT_TIME = "<time>%s</time>";
	public static final String GPX_TAG_TRACK_SEGMENT_POINT_SAT = "<sat>%d</sat>";
	public static final String GPX_TAG_TRACK_SEGMENT_POINT_ELE = "<ele>%d</ele>";
	public static final String GPX_TAG_TRACK_SEGMENT_POINT_COURSE = "<course>%d</course>";
	public static final String GPX_TAG_TRACK_SEGMENT_POINT_SPEED = "<speed>%d</speed>";

	// ===========================================================
	// Methods
	// ===========================================================
}
