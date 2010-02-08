// Created by plusminus on 17:09:24 - 25.01.2009
package org.andnav2.sys.ors.aps;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.aps.util.constants.APSConstants;
import org.andnav2.sys.ors.util.constants.ORSXMLConstants;


public class APSRequestComposer implements ORSXMLConstants, APSConstants {
	// ===========================================================
	// Constants
	// ===========================================================

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
	 * <pre>&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
	 * &lt;xls:XLS xmlns:xls=&quot;http://www.opengis.net/xls&quot;
	 * xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
	 * xmlns:gml=&quot;http://www.opengis.net/gml&quot; version=&quot;1.1&quot;
	 * xsi:schemaLocation=&quot;http://www.opengis.net/xls
	 * http://schemas.opengis.net/ols/1.1.0/RouteService.xsd&quot;&gt;
	 *  &lt;xls:RouteGeometry&gt;
	 *    &lt;gml:LineString srsName=&quot;EPSG:4326&quot;&gt;
	 *      &lt;gml:pos&gt;8.68108 49.40781&lt;/gml:pos&gt;
	 *      ...
	 *      &lt;gml:pos&gt;8.69311 49.41473&lt;/gml:pos&gt;
	 *    &lt;/gml:LineString&gt;
	 *  &lt;/xls:RouteGeometry&gt;
	 * &lt;/xls:XLS&gt;</pre>
	 * @param startIndex
	 */
	public static String createXMLPostRequest(final List<GeoPoint> pGeoPoints, final int pStartIndex){
		final StringBuilder sb = new StringBuilder();
		final Formatter f = new Formatter(sb, Locale.ENGLISH);

		sb.append(XML_BASE_TAG_UTF8)
		.append(XLS_OPENGIS_ALTITUDESERVICE_TAG_OPEN)
		.append(XLS_ROUTEGEOMETRY_TAG_OPEN)
		.append(GML_LINESTRING_TAG_OPEN);

		/* Will hold the summed up length. */
		int lengthSummed = 0;

		final int geoPointCount = pGeoPoints.size();

		/* Ensure the startIndex. */
		final int min = Math.min(pStartIndex, geoPointCount - 1);

		GeoPoint previous = pGeoPoints.get(min);

		for(int i = min; i < geoPointCount; i++) {
			final GeoPoint gp = pGeoPoints.get(i);
			gp.formatTo(f);

			lengthSummed += previous.distanceTo(gp);
			previous = gp;
			if(lengthSummed > MAXIMUM_LENGTH) {
				break;
			}
		}

		sb.append(GML_LINESTRING_TAG_CLOSE)
		.append(XLS_ROUTEGEOMETRY_TAG_CLOSE)
		.append(XLS_OPENGIS_ALTITUDESERVICE_TAG_CLOSE);

		return sb.toString();
	}

	public static String createRestFulRequestURL(final String pBaseURL, final List<GeoPoint> pGeoPoints, final int pStartIndex) {
		final StringBuilder latBuilder = new StringBuilder();
		final StringBuilder lonBuilder = new StringBuilder();

		int lengthSummed = 0;
		GeoPoint previous = pGeoPoints.get(0);
		for(final GeoPoint gp : pGeoPoints){
			latBuilder.append(gp.getLatitudeAsDouble());
			latBuilder.append(',');
			lonBuilder.append(gp.getLongitudeAsDouble());
			lonBuilder.append(',');

			lengthSummed += previous.distanceTo(gp);
			previous = gp;
			if(lengthSummed > MAXIMUM_LENGTH) {
				break;
			}
		}

		/* Cur last comma. */
		if(latBuilder.length() > 0 && lonBuilder.length() > 0){
			latBuilder.setLength(latBuilder.length() - 1);
			lonBuilder.setLength(lonBuilder.length() - 1);
		}

		return String.format(Locale.ENGLISH, pBaseURL, latBuilder.toString(), lonBuilder.toString());
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
