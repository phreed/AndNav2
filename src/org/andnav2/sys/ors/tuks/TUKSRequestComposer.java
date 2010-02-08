// Created by plusminus on 18:59:46 - 19.01.2009
package org.andnav2.sys.ors.tuks;

import java.util.Formatter;
import java.util.Locale;

import junit.framework.Assert;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.sys.ors.util.constants.ORSXMLConstants;


public class TUKSRequestComposer implements ORSXMLConstants {
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
	 * <pre>
	 * &lt;wfs:GetFeature service=&quot;WFS&quot; version=&quot;1.1.0&quot;
	 *  xmlns:wfs=&quot;http://www.opengis.net/wfs&quot;
	 *  xmlns:ogc=&quot;http://www.opengis.net/ogc&quot;
	 *  xmlns:gml=&quot;http://www.opengis.net/gml&quot;
	 *  xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
	 *  xsi:schemaLocation=&quot;http://www.opengis.net/wfs
	 *                      http://schemas.opengis.net/wfs/1.1.0/wfs.xsd&quot;&gt;;
	 *  &lt;wfs:Query typeName=&quot;traffic:messages&quot;&gt;
	 *    &lt;wfs:PropertyName&gt;traffic:description&lt;/wfs:PropertyName&gt;
	 *    &lt;wfs:PropertyName&gt;traffic:severity&lt;/wfs:PropertyName&gt;
	 *    &lt;wfs:PropertyName&gt;traffic:the_geom&lt;/wfs:PropertyName&gt;
	 *    &lt;ogc:Filter&gt;
	 *      &lt;ogc:BBOX&gt;
	 *        &lt;ogc:PropertyName&gt;the_geom&lt;/ogc:PropertyName&gt;
	 *        &lt;gml:Envelope srsName=&quot;http://www.opengis.net/gml/srs/epsg.xml#4326&quot;&gt;
	 *           &lt;gml:lowerCorner&gt;-10.44161 58.82350&lt;/gml:lowerCorner&gt;
	 *           &lt;gml:upperCorner&gt;2.26089 41.512517&lt;/gml:upperCorner&gt;
	 *        &lt;/gml:Envelope&gt;
	 *      &lt;/ogc:BBOX&gt;
	 *   &lt;/ogc:Filter&gt;
	 *  &lt;/wfs:Query&gt;
	 * &lt;/wfs:GetFeature&gt;</pre>
	 * @param pBoundingBoxE6
	 * */
	public static String createGML1(final BoundingBoxE6 pBoundingBoxE6){
		Assert.assertNotNull(pBoundingBoxE6);

		final StringBuilder sb = new StringBuilder();
		final Formatter f = new Formatter(sb, Locale.ENGLISH);

		sb.append(WFS_GETFEATURE_TAG_OPEN);
		f.format(WFS_QUERY_TAG_OPEN, "traffic:t_traffic_view");  // @HOME SERVER traffic:messages | @UNI traffic:t_traffic_view
		f.format(WFS_PROPERTY_TAG, "traffic:severity");
		f.format(WFS_PROPERTY_TAG, "traffic:description");
		f.format(WFS_PROPERTY_TAG, "traffic:the_geom");

		sb.append(OGC_FILTER_TAG_OPEN)
		.append(OGC_BBOX_TAG_OPEN);

		f.format(OGC_PROPERTY_TAG, "the_geom");

		/* The BoundingBox...  */
		sb.append(GML_ENVELOPE_TAG_OPEN);

		f.format(GML_LOWERCORNER_TAG, pBoundingBoxE6.getLonWest(), pBoundingBoxE6.getLatSouth());
		f.format(GML_UPPERCORNER_TAG, pBoundingBoxE6.getLonEast(), pBoundingBoxE6.getLatNorth());

		sb.append(GML_ENVELOPE_TAG_CLOSE)
		.append(OGC_BBOX_TAG_CLOSE)
		.append(OGC_FILTER_TAG_CLOSE)
		.append(WFS_QUERY_TAG_CLOSE)
		.append(WFS_GETFEATURE_TAG_CLOSE);

		return sb.toString();
	}

	/** <pre>&lt;wfs:GetFeature service=&quot;WFS&quot; version=&quot;1.0.0&quot;
	 *  outputFormat=&quot;GML2&quot;
	 *  xmlns:topp=&quot;http://www.openplans.org/topp&quot;
	 *  xmlns:wfs=&quot;http://www.opengis.net/wfs&quot;
	 *  xmlns:ogc=&quot;http://www.opengis.net/ogc&quot;
	 *  xmlns:gml=&quot;http://www.opengis.net/gml&quot;
	 *  xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
	 *  xsi:schemaLocation=&quot;http://www.opengis.net/wfs
	 *                      http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd&quot;&gt;
	 *  &lt;wfs:Query typeName=&quot;traffic:messages&quot;&gt;
	 *    &lt;wfs:PropertyName&gt;traffic:id&lt;/wfs:PropertyName&gt;
	 *    &lt;wfs:PropertyName&gt;traffic:description&lt;/wfs:PropertyName&gt;
	 *    &lt;wfs:PropertyName&gt;traffic:severity&lt;/wfs:PropertyName&gt;
	 *    &lt;wfs:PropertyName&gt;traffic:the_geom&lt;/wfs:PropertyName&gt;
	 *    &lt;ogc:Filter&gt;
	 *      &lt;ogc:BBOX&gt;
	 *        &lt;ogc:PropertyName&gt;the_geom&lt;/ogc:PropertyName&gt;
	 *        &lt;gml:Box srsName=&quot;http://www.opengis.net/gml/srs/epsg.xml#4326&quot;&gt;
	 *           &lt;gml:coordinates&gt;0.0,51.0 -1.0,52.0&lt;/gml:coordinates&gt;
	 *        &lt;/gml:Box&gt;
	 *      &lt;/ogc:BBOX&gt;
	 *   &lt;/ogc:Filter&gt;
	 *  &lt;/wfs:Query&gt;
	 * &lt;/wfs:GetFeature&gt;</pre>
	 * @param pBoundingBoxE6
	 * */
	public static String createGML2(final BoundingBoxE6 pBoundingBoxE6){
		Assert.assertNotNull(pBoundingBoxE6);

		final StringBuilder sb = new StringBuilder();
		final Formatter f = new Formatter(sb, Locale.ENGLISH);

		sb.append(WFS_GETFEATURE_TAG_GML2_OPEN);
		f.format(WFS_QUERY_TAG_OPEN, "traffic:t_traffic_view");  // @HOME SERVER traffic:messages | @UNI traffic:t_traffic_view
		f.format(WFS_PROPERTY_TAG, "traffic:severity");
		f.format(WFS_PROPERTY_TAG, "traffic:description");
		f.format(WFS_PROPERTY_TAG, "traffic:the_geom");

		sb.append(OGC_FILTER_TAG_OPEN)
		.append(OGC_BBOX_TAG_OPEN);

		f.format(OGC_PROPERTY_TAG, "the_geom");

		/* The BoundingBox...  */
		sb.append(GML_BOX_TAG_OPEN);

		f.format(GML_COORDINATES_TAG,
				pBoundingBoxE6.getLonEast(),
				pBoundingBoxE6.getLatSouth(),
				pBoundingBoxE6.getLonWest(),
				pBoundingBoxE6.getLatNorth());

		sb.append(GML_BOX_TAG_CLOSE)
		.append(OGC_BBOX_TAG_CLOSE)
		.append(OGC_FILTER_TAG_CLOSE)
		.append(WFS_QUERY_TAG_CLOSE)
		.append(WFS_GETFEATURE_TAG_CLOSE);

		return sb.toString();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
