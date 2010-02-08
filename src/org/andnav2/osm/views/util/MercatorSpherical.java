// Created by plusminus on 18:58:15 - 25.09.2008
package org.andnav2.osm.views.util;

import org.andnav2.osm.util.constants.MathConstants;

/**
 * http://wiki.openstreetmap.org/index.php/Mercator
 * @author Nicolas Gramlich
 * This class provides a way to convert from latitude and longitude to a simple Mercator projection.
 */
public class MercatorSpherical implements MathConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	//	For the spherical project we assume a radius of exactly 6378137m which
	//	gives a circumference of 40075016.6856m. This maps +-180 degrees of
	//	longitude to +-20037508.3428.

	public static final int RADIUS = 6378137;
	public static final double CIRCUMFERENCE = Math.PI * (RADIUS * 2);

	public static final double SCALE_FACTOR = (CIRCUMFERENCE / 2) / 180;

	// ===========================================================
	// Static Methods
	// ===========================================================

	/**
	 * Converts a Mercator-projected y-coordinate (projected latitude) to the real Latitude. This is only a approximation.
	 */
	public static double y2lat(final double a) { return (180/Math.PI * (2 * Math.atan(Math.exp(a / SCALE_FACTOR * Math.PI/180)) - Math.PI/2)); }

	/**
	 * Converts a real Latitude to the Mercator-projected y-coordinate (projected latitude) . This is only a approximation.
	 */
	public static double lat2y(final double a) { return (180/Math.PI * Math.log(Math.tan(Math.PI/4+a*(Math.PI/180)/2))) * SCALE_FACTOR; }

	/**
	 * Converts a Mercator-projected x-coordinate (projected longitude) to the real longitude. This is only a approximation.
	 */
	public static double x2lon(final double x){ return x / SCALE_FACTOR; }

	/**
	 * Converts a real longitude to the Mercator-projected x-coordinate (projected longitude). This is only a approximation.
	 */
	public static double lon2x(final double lon) { return SCALE_FACTOR * lon; }

}
