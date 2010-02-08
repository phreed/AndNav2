// Created by plusminus on 18:06:19 - 07.11.2008
package org.andnav2.sys.ors.aas;

import java.util.ArrayList;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.Error;
import org.andnav2.sys.ors.adt.aoi.Polygon;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.util.constants.Constants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;


/**
 * Parses XML-Data like:
 * <pre>&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 * &lt;aas:AAS xmlns:aas=&quot;http://www.geoinform.fh-mainz.de/aas&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:gml=&quot;http://www.opengis.net/gml&quot; version=&quot;1.0&quot; xsi:schemaLocation=&quot;http://www.geoinform.fh-mainz.de/aas D:/Schemata/AAS1.0/AccessibilityService.xsd&quot;&gt;
 *   &lt;aas:ResponseHeader xsi:type=&quot;aas:ResponseHeaderType&quot;/&gt;
 *   &lt;aas:Response xsi:type=&quot;aas:ResponseType&quot; requestID=&quot;123456789&quot; version=&quot;1.0&quot;&gt;
 *     &lt;aas:AccessibilityResponse xsi:type=&quot;aas:AccessibilityResponseType&quot;&gt;
 *       &lt;aas:AccessibilitySummary&gt;
 *         &lt;aas:NumberOfLocations&gt;0&lt;/aas:NumberOfLocations&gt;
 *         &lt;aas:BoundingBox srsName=&quot;EPSG:4326&quot;&gt;
 *           &lt;gml:pos&gt;8.6450233 49.4681933&lt;/gml:pos&gt;
 *           &lt;gml:pos&gt;8.6630108 49.4786068&lt;/gml:pos&gt;
 *         &lt;/aas:BoundingBox&gt;
 *       &lt;/aas:AccessibilitySummary&gt;
 *       &lt;aas:AccessibilityGeometry&gt;
 *         &lt;gml:Polygon srsName=&quot;EPSG:EPSG:4326&quot;&gt;
 *           &lt;gml:exterior&gt;
 *             &lt;gml:LinearRing xsi:type=&quot;gml:LinearRingType&quot;&gt;
 *               &lt;gml:pos&gt;8.6506844 49.4682097&lt;/gml:pos&gt;
 *               &lt;gml:pos&gt;8.6506481 49.4681933&lt;/gml:pos&gt;
 * 			  ...
 *               &lt;gml:pos&gt;8.6506844 49.4682097&lt;/gml:pos&gt;
 *             &lt;/gml:LinearRing&gt;
 *           &lt;/gml:exterior&gt;
 *         &lt;/gml:Polygon&gt;
 *         &lt;gml:Polygon srsName=&quot;EPSG:EPSG:4326&quot;&gt;
 *           &lt;gml:exterior&gt;
 *             &lt;gml:LinearRing xsi:type=&quot;gml:LinearRingType&quot;&gt;
 *               &lt;gml:pos&gt;8.6570897 49.4783262&lt;/gml:pos&gt;
 *               &lt;gml:pos&gt;8.6574036 49.4777484&lt;/gml:pos&gt;
 * 			  ...
 *               &lt;gml:pos&gt;8.6570897 49.4783262&lt;/gml:pos&gt;
 *             &lt;/gml:LinearRing&gt;
 *           &lt;/gml:exterior&gt;
 *         &lt;/gml:Polygon&gt;
 *         ....</pre>
 * 
 * @author Nicolas Gramlich
 *
 */
public class AASParser extends DefaultHandler implements Constants {
	// ====================================
	// Constants
	// ====================================

	// ====================================
	// Fields
	// ====================================

	private final StringBuilder sb = new StringBuilder();


	private final ArrayList<Error> mErrors = new ArrayList<Error>();

	private ArrayList<Polygon> mPolygons;
	private Polygon mTmpPolygon;
	private ArrayList<GeoPoint> mTmpLinearRing;

	private AASResponse mAASResponse;


	private GeoPoint mTmpGeoPoint;

	private boolean inAAS = false;
	private boolean inRepsonseHeader = false;
	private boolean inRepsonse = false;
	private boolean inAccessibilityResponse = false;
	private boolean inAccessibilitySummary = false;
	private boolean inNumberOfLocations = false;
	private boolean inBoundingBox = false;
	private boolean inPos = false;
	private boolean inAccessibilityGeometry = false;
	private boolean inPolygon = false;
	private boolean inExterior = false;
	private boolean inLinearRing = false;
	private boolean inInterior = false;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<Error> getErrors(){
		return this.mErrors;
	}

	public AASResponse getASSResponse() throws ORSException{
		if(this.mErrors != null && this.mErrors.size() > 0) {
			throw new ORSException(this.mErrors);
		}

		return this.mAASResponse;
	}

	// ====================================
	// Methods from Superclasses
	// ====================================

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		this.mAASResponse = new AASResponse();
	}

	@Override
	public void startElement(final String uri, final String localName, final String name, final Attributes attributes) throws SAXException {
		if(localName.equals("Error") || name.equals("Error")){
			final String errorCode = attributes.getValue("", "errorCode");
			final String severity = attributes.getValue("", "severity");
			final String locationPath = attributes.getValue("", "locationPath");
			final String message = attributes.getValue("", "message");
			this.mErrors.add(new Error(errorCode, severity, locationPath, message));
		}

		this.sb.setLength(0);

		if(localName.equals("AAS")){
			this.inAAS = true;
		} else if(localName.equals("ResponseHeader")){
			this.inRepsonseHeader = true;
		} else if(localName.equals("Response")){
			this.inRepsonse = true;
		} else if(localName.equals("AccessibilityResponse")){
			this.inAccessibilityResponse = true;
		} else if(localName.equals("AccessibilitySummary")){
			this.inAccessibilitySummary = true;
		} else if(localName.equals("NumberOfLocations")){
			this.inNumberOfLocations = true;
		} else if(localName.equals("BoundingBox")){
			this.inBoundingBox = true;
		} else if(localName.equals("AccessibilityGeometry")){
			this.inAccessibilityGeometry = true;
			this.mPolygons = new ArrayList<Polygon>();
			this.mAASResponse.setPolygons(this.mPolygons);
		} else if(localName.equals("pos")){
			this.inPos = true;
		} else if(localName.equals("Polygon")){
			this.inPolygon = true;
			this.mTmpPolygon = new Polygon();
			this.mPolygons.add(this.mTmpPolygon);
		} else if(localName.equals("exterior")){
			this.inExterior = true;
		} else if(localName.equals("LinearRing")){
			this.inLinearRing = true;
			this.mTmpLinearRing = new ArrayList<GeoPoint>();
			if(this.inExterior){
				this.mTmpPolygon.setExterior(this.mTmpLinearRing);
			} else {
				this.mTmpPolygon.addInterior(this.mTmpLinearRing);
			}
		} else if(localName.equals("interior")){
			this.inInterior = true;
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
		if(localName.equals("AAS")){
			this.inAAS = false;
		} else if(localName.equals("ResponseHeader")){
			this.inRepsonseHeader = false;
		} else if(localName.equals("Response")){
			this.inRepsonse = false;
		} else if(localName.equals("AccessibilityResponse")){
			this.inAccessibilityResponse = false;
		} else if(localName.equals("AccessibilitySummary")){
			this.inAccessibilitySummary = false;
		} else if(localName.equals("NumberOfLocations")){
			this.inNumberOfLocations = false;
		} else if(localName.equals("BoundingBox")){
			this.inBoundingBox = false;
		} else if(localName.equals("AccessibilityGeometry")){
			this.inAccessibilityGeometry = false;
		} else if(localName.equals("pos")){
			this.inPos = false;
			final GeoPoint gp = GeoPoint.fromInvertedDoubleString(this.sb.toString(), ' ');
			if(this.inAccessibilityGeometry){
				this.mTmpLinearRing.add(gp);
			} else if(this.inBoundingBox){
				if(this.mTmpGeoPoint == null){ // First GeoPoint
					this.mTmpGeoPoint = gp;
				}else{ // Second one
					final int mFirstLatE6 = this.mTmpGeoPoint.getLatitudeE6();
					final int mFirstLonE6 = this.mTmpGeoPoint.getLongitudeE6();
					this.mTmpGeoPoint = gp;
					final int mSecondLatE6 = this.mTmpGeoPoint.getLatitudeE6();
					final int mSecondLonE6 = this.mTmpGeoPoint.getLongitudeE6();
					this.mAASResponse.setBoundingBoxE6(new BoundingBoxE6(Math.max(mFirstLatE6, mSecondLatE6),
							Math.max(mFirstLonE6, mSecondLonE6),
							Math.min(mFirstLatE6, mSecondLatE6),
							Math.min(mFirstLonE6, mSecondLonE6)));
				}
			}
		} else if(localName.equals("Polygon")){
			this.inPolygon = false;
		} else if(localName.equals("exterior")){
			this.inExterior = false;
		} else if(localName.equals("LinearRing")){
			this.inLinearRing = false;
		} else if(localName.equals("interior")){
			this.inInterior = false;
		} else {
			Log.w(DEBUGTAG, "Unexpected end-tag: '" + name + "'");
		}

		// Reset the stringbuffer
		this.sb.setLength(0);

		super.endElement(uri, localName, name);
	}

	@Override
	public void endDocument() throws SAXException {
		if(this.mErrors == null || this.mErrors.size() == 0){
			// Maybe do some finalization or similar...
		}
		super.endDocument();
	}
}
