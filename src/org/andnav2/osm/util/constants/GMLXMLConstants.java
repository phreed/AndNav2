// Created by plusminus on 17:15:37 - 25.01.2009
package org.andnav2.osm.util.constants;


public interface GMLXMLConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static final String GML_POINT_TAG_OPEN = "<gml:Point srsName=\"EPSG:4326\">";
	public static final String GML_POINT_TAG_CLOSE = "</gml:Point>";

	public static final String GML_POS_TAG = "<gml:pos>%.8f %.8f</gml:pos>";

	public static final String GML_POLYGON_TAG_OPEN = "<gml:Polygon>";
	public static final String GML_POLYGON_TAG_CLOSE = "</gml:Polygon>";
	public static final String GML_EXTERIOR_TAG_OPEN = "<gml:exterior>";
	public static final String GML_EXTERIOR_TAG_CLOSE = "</gml:exterior>";
	public static final String GML_LINEARRING_TAG_OPEN = "<gml:LinearRing>";
	public static final String GML_LINEARRING_TAG_CLOSE = "</gml:LinearRing>";
	public static final String GML_LINESTRING_TAG_OPEN = "<gml:LineString srsName=\"EPSG:4326\">";
	public static final String GML_LINESTRING_TAG_CLOSE = "</gml:LineString>";

	public static final String GML_CIRCLEBYCENTERPOINT_TAG_OPEN = "<gml:CircleByCenterPoint numArc=\"1\">";
	public static final String GML_CIRCLEBYCENTERPOINT_TAG_CLOSE = "</gml:CircleByCenterPoint>";

	/** Needs to be formatted with a integer-decimal. */
	public static final String GML_RADIUS_TAG = "<gml:radius uom=\"m\">%d</gml:radius>";

	public static final String GML_ENVELOPE_TAG_OPEN = "<gml:Envelope srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\">";
	public static final String GML_ENVELOPE_TAG_CLOSE = "</gml:Envelope>";
	public static final String GML_BOX_TAG_OPEN = "<gml:Box srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\">";
	public static final String GML_BOX_TAG_CLOSE = "</gml:Box>";
	/** Needs to be formatted with four Floating-Point numbers. */
	public static final String GML_COORDINATES_TAG = "<gml:coordinates>%.8f,%.8f %.8f,%.8f</gml:coordinates>";
	/** Needs to be formatted with two Floating-Point numbers.
	 * * Probably the LowerLeft-Corner. */
	public static final String GML_LOWERCORNER_TAG = "<gml:lowerCorner>%.8f %.8f</gml:lowerCorner>";
	/** Needs to be formatted with two Floating-Point numbers.
	 * Probably the UpperRight-Corner. */
	public static final String GML_UPPERCORNER_TAG = "<gml:upperCorner>%.8f %.8f</gml:upperCorner>";
}
