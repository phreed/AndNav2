// Created by plusminus on 00:16:36 - 20.01.2009
package org.andnav2.sys.ors.tuks;

import java.util.ArrayList;
import java.util.List;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.GeocodedAddress;
import org.andnav2.sys.ors.adt.ts.TrafficItem;
import org.andnav2.traffic.tpeg.adt.IIDFinder;
import org.andnav2.traffic.tpeg.adt.rtm.table.RTM31_general_magnitude;
import org.andnav2.util.constants.Constants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/** <pre>&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 * &lt;wfs:FeatureCollection numberOfFeatures=&quot;810&quot; timeStamp=&quot;2009-01-20T06:35:25.629+01:00&quot; xsi:schemaLocation=&quot;http://www.giub.uni-bonn.de/traffic http://openls.giub.uni-bonn.de:80/geoserver-osm/wfs?service=WFS&amp;amp;version=1.1.0&amp;amp;request=DescribeFeatureType&amp;amp;typeName=traffic:t_traffic_view http://www.opengis.net/wfs http://openls.giub.uni-bonn.de:80/geoserver-osm/schemas/wfs/1.1.0/wfs.xsd&quot; xmlns:osm=&quot;http://www.giub.uni-bonn.de/karto/osm&quot; xmlns:ogc=&quot;http://www.opengis.net/ogc&quot; xmlns:gml=&quot;http://www.opengis.net/gml&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:traffic=&quot;http://www.giub.uni-bonn.de/traffic&quot; xmlns:xlink=&quot;http://www.w3.org/1999/xlink&quot; xmlns:ows=&quot;http://www.opengis.net/ows&quot; xmlns:wfs=&quot;http://www.opengis.net/wfs&quot;&gt;
 *   &lt;gml:featureMembers&gt;
 *       &lt;traffic:t_traffic_view gml:id=&quot;t_traffic_view.nfm-dec2d53_11ef0f30304_-2398&quot;&gt;
 *           &lt;gml:description&gt;
 *               M25 Hertfordshire - Earlier accident clockwise at J21A, St Albans.
 *           &lt;/gml:description&gt;
 *           &lt;gml:boundedBy&gt;
 *               &lt;gml:Envelope srsName=&quot;urn:x-ogc:def:crs:EPSG:6.11.2:4326&quot;&gt;
 *                   &lt;gml:lowerCorner&gt;
 *                       51.714921 -0.370989
 *                   &lt;/gml:lowerCorner&gt;
 *                   &lt;gml:upperCorner&gt;
 *                       51.714921 -0.370989
 *                   &lt;/gml:upperCorner&gt;
 *               &lt;/gml:Envelope&gt;
 *           &lt;/gml:boundedBy&gt;
 *           &lt;traffic:severity&gt;&amp;rtm31_5;&lt;/traffic:severity&gt;
 *           &lt;traffic:the_geom&gt;
 *               &lt;gml:Point srsName=&quot;urn:x-ogc:def:crs:EPSG:6.11.2:4326&quot;&gt;
 *                   &lt;gml:pos&gt;
 *                       51.714921 -0.370989
 *                   &lt;/gml:pos&gt;
 *               &lt;/gml:Point&gt;
 *           &lt;/traffic:the_geom&gt;
 *       &lt;/traffic:t_traffic_view&gt;
 *   &lt;/gml:featureMembers&gt;
 * &lt;/wfs:FeatureCollection&gt;</pre>
 * */
public class TUKSParser extends DefaultHandler implements Constants {
	// ====================================
	// Constants
	// ====================================

	// ====================================
	// Fields
	// ====================================

	private List<TrafficItem> mTrafficFeatures;

	private boolean inFeatureColection = false;
	private boolean inFeatureMembers = false;
	private boolean inFeatureMember = false;
	private boolean inTrafficView = false;
	private boolean inDescription = false;
	private boolean inSeverity = false;
	private boolean inGeometry = false;
	private boolean inCoordinates = false;
	private boolean inPoint = false;
	private boolean inPos = false;
	private boolean inBoundedBy;
	private boolean inEnvelope;
	private boolean inLowerCorner;
	private boolean inUpperCorner;

	protected GeocodedAddress mTmpGeocodedAddress;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public List<TrafficItem> getTrafficFeatures() {
		return this.mTrafficFeatures;
	}

	private TrafficItem getLastTrafficFeature(){
		return this.mTrafficFeatures.get(this.mTrafficFeatures.size() - 1);
	}

	// ====================================
	// Methods from Superclasses
	// ====================================

	@Override
	public void startDocument() throws SAXException {
		this.mTrafficFeatures = new ArrayList<TrafficItem>();
		super.startDocument();
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
		this.sb.setLength(0);

		if(localName.equals("FeatureCollection")){
			this.inFeatureColection = true;
		} else if(localName.equals("featureMembers")){
			this.inFeatureMembers = true;
		} else if(localName.equals("featureMember")){
			this.inFeatureMember = true;
		} else if(localName.equals("messages") || localName.equals("t_traffic_view")){ // Different Service-Implementations
			this.inTrafficView = true;
			this.mTrafficFeatures.add(new TrafficItem());
		} else if(localName.equals("description")){
			this.inDescription = true;
		} else if(localName.equals("severity")){
			this.inSeverity = true;
		} else if(localName.equals("the_geom")){
			this.inGeometry = true;
		} else if(localName.equals("coordinates")){
			this.inCoordinates = true;
		} else if(localName.equals("Point")){
			this.inPoint = true;
		} else if(localName.equals("pos")){
			this.inPos = true;
		} else if(localName.equals("boundedBy")){
			this.inBoundedBy = true;
		} else if(localName.equals("Envelope")){
			this.inEnvelope = true;
		} else if(localName.equals("lowerCorner")){
			this.inLowerCorner = true;
		} else if(localName.equals("upperCorner")){
			this.inUpperCorner = true;
		} else {
			Log.w(DEBUGTAG, "Unexpected tag: '" + localName + "'");
		}
		super.startElement(uri, localName, qName, attributes);
	}

	protected StringBuilder sb = new StringBuilder();

	@Override
	public void characters(final char[] chars, final int start, final int length) throws SAXException {
		this.sb.append(chars, start, length);
		super.characters(chars, start, length);
	}

	@Override
	public void endElement(final String uri, final String localName, final String qName) throws SAXException {
		if(localName.equals("FeatureCollection")){
			this.inFeatureColection = false;
		} else if(localName.equals("featureMembers")){
			this.inFeatureMembers = false;
		} else if(localName.equals("featureMember")){
			this.inFeatureMember = false;
		} else if(localName.equals("messages") || localName.equals("t_traffic_view")){ // Different Service-Implementations
			this.inTrafficView = false;
		} else if(localName.equals("description")){
			this.inDescription = false;
			getLastTrafficFeature().setDescription(this.sb.toString());
		} else if(localName.equals("severity")){
			this.inSeverity = false;
			final String rtmString = this.sb.toString();
			final RTM31_general_magnitude rtmElement = (RTM31_general_magnitude)IIDFinder.resolve(RTM31_general_magnitude.getValuesStatic(), rtmString);
			getLastTrafficFeature().setSeverity(rtmElement);
		} else if(localName.equals("the_geom")){
			this.inGeometry = false;
		} else if(localName.equals("coordinates")){
			this.inCoordinates = false;
			if(this.inPoint){
				final GeoPoint gp = GeoPoint.fromInvertedDoubleString(this.sb.toString(), ',');
				getLastTrafficFeature().setGeoPoint(gp);
			}
		} else if(localName.equals("Point")){
			this.inPoint = false;
		} else if(localName.equals("pos")){
			this.inPos = false;
			final GeoPoint gp = GeoPoint.fromInvertedDoubleString(this.sb.toString(), ' ');
			getLastTrafficFeature().setGeoPoint(gp);
		} else if(localName.equals("boundedBy")){
			this.inBoundedBy = false;
		} else if(localName.equals("Envelope")){
			this.inEnvelope = false;
		} else if(localName.equals("lowerCorner")){
			this.inLowerCorner = false;
		} else if(localName.equals("upperCorner")){
			this.inUpperCorner = false;
		} else {
			Log.w(DEBUGTAG, "Unexpected end-tag: '" + localName + "'");
		}

		// Reset the stringbuffer
		this.sb.setLength(0);

		super.endElement(uri, localName, qName);
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
}
