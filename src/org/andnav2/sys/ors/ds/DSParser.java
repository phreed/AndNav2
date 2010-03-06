// Created by plusminus on 18:06:19 - 07.11.2008
package org.andnav2.sys.ors.ds;

import java.util.ArrayList;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.Error;
import org.andnav2.sys.ors.adt.ds.ORSPOI;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.util.constants.Constants;
import org.andnav2.util.constants.TimeConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;


/**
 * Parses XML-Data like:
 * <pre>&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 * &lt;xls:XLS xmlns:xls=&quot;http://www.opengis.net/xls&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:gml=&quot;http://www.opengis.net/gml&quot; version=&quot;1.1&quot; xsi:schemaLocation=&quot;http://www.opengis.net/xls http://schemas.opengis.net/ols/1.1.0/DirectoryService.xsd&quot;&gt;
 * 	&lt;xls:ResponseHeader xsi:type=&quot;xls:ResponseHeaderType&quot;/&gt;
 * 	&lt;xls:Response xsi:type=&quot;xls:ResponseType&quot; requestID=&quot;123456789&quot; version=&quot;1.1&quot; numberOfResponses=&quot;13&quot;&gt;
 * 		&lt;xls:DirectoryResponse xsi:type=&quot;xls:DirectoryResponseType&quot;&gt;
 * 			&lt;xls:POIContext&gt;
 * 				&lt;xls:POI POIName=&quot;Kocaman&quot; description=&quot;bakery&quot; ID=&quot;293710580&quot;&gt;
 * 					&lt;gml:Point&gt;
 * 						&lt;gml:pos srsName=&quot;EPSG:4326&quot;&gt;7.0866882 50.7335413&lt;/gml:pos&gt;
 * 					&lt;/gml:Point&gt;
 * 				&lt;/xls:POI&gt;
 * 				&lt;xls:Distance uom=&quot;M&quot; value=&quot;625&quot;/&gt;
 * 			&lt;/xls:POIContext&gt;
 * 			...
 * 			&lt;xls:POIContext&gt;
 * 				&lt;xls:POI POIName=&quot;Domgörgen&quot; description=&quot;bakery&quot; ID=&quot;270495053&quot;&gt;
 * 					&lt;gml:Point&gt;
 * 						&lt;gml:pos srsName=&quot;EPSG:4326&quot;&gt;7.0922473 50.7741497&lt;/gml:pos&gt;
 * 					&lt;/gml:Point&gt;
 * 				&lt;/xls:POI&gt;
 * 				&lt;xls:Distance uom=&quot;KM&quot; value=&quot;5&quot;/&gt;
 * 			&lt;/xls:POIContext&gt;
 * 		&lt;/xls:DirectoryResponse&gt;
 * 	&lt;/xls:Response&gt;
 * &lt;/xls:XLS&gt;</pre>
 * 
 * @author Nicolas Gramlich
 *
 */
public class DSParser extends DefaultHandler implements TimeConstants, Constants{
	// ====================================
	// Constants
	// ====================================

	// ====================================
	// Fields
	// ====================================

	private final ArrayList<Error> mErrors = new ArrayList<Error>();

	private ArrayList<ORSPOI> mPOIs;
	private ORSPOI mCurPOI;

	private boolean inXLS = false;
	private boolean inRepsonseHeader = false;
	private boolean inRepsonse = false;
	private boolean inDirectoryResponse = false;
	private boolean inPOIContext = false;
	private boolean inPOI = false;
	private boolean inPoint = false;
	private boolean inPos = false;
	private boolean inDistance = false;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<Error> getErrors(){
		return this.mErrors;
	}

	public ArrayList<ORSPOI> getDSResponse() throws ORSException{
		if(this.mErrors != null && this.mErrors.size() > 0) {
			throw new ORSException(this.mErrors);
		}

		return this.mPOIs;
	}

	// ====================================
	// Methods from Superclasses
	// ====================================

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		this.mPOIs = new ArrayList<ORSPOI>();
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

		if(localName.equals("XLS")){
			this.inXLS = true;
		} else if(localName.equals("ResponseHeader")){
			this.inRepsonseHeader = true;
		} else if(localName.equals("Response")){
			this.inRepsonse = true;
		} else if(localName.equals("DirectoryResponse")){
			this.inDirectoryResponse = true;
		} else if(localName.equals("POIContext")){
			this.inPOIContext = true;
		} else if(localName.equals("POI")){
			this.inPOI = true;
			this.mCurPOI = new ORSPOI();
			final String poiname = attributes.getValue("", "POIName");
			final String poitype = attributes.getValue("", "description");
			this.mCurPOI.setName(poiname);
			this.mCurPOI.setPOIType(POIType.fromRawName(poitype));
			this.mPOIs.add(this.mCurPOI);
		} else if(localName.equals("Point")){
			this.inPoint = true;
		} else if(localName.equals("pos")){
			this.inPos = true;
		} else if(localName.equals("Distance")){
			this.inDistance = true;
			final String uom = attributes.getValue("", "uom");

			float factor = 0;
			if(uom != null){
				if(uom.equals("M")){
					factor = 1;
				}else if(uom.equals("KM")){
					factor = 1000;
				}
			}

			this.mCurPOI.setDistance((int)(factor * Float.parseFloat(attributes.getValue("", "value"))));
		} else {
			Log.w(DEBUGTAG, "Unexpected tag: '" + name + "'");
		}
		super.startElement(uri, localName, name, attributes);
	}

	private final StringBuilder sb = new StringBuilder();

	@Override
	public void characters(final char[] chars, final int start, final int length) throws SAXException {
		this.sb.append(chars, start, length);
		super.characters(chars, start, length);
	}

	@Override
	public void endElement(final String uri, final String localName, final String name) throws SAXException {
		if(localName.equals("XLS")){
			this.inXLS = false;
		} else if(localName.equals("ResponseHeader")){
			this.inRepsonseHeader = false;
		} else if(localName.equals("Response")){
			this.inRepsonse = false;
		} else if(localName.equals("DirectoryResponse")){
			this.inDirectoryResponse = false;
		} else if(localName.equals("POIContext")){
			this.inPOIContext = false;
		} else if(localName.equals("POI")){
			this.inPOI = false;
		} else if(localName.equals("Point")){
			this.inPoint = false;
		} else if(localName.equals("pos")){
			this.inPos = false;
			this.mCurPOI.setGeoPoint(GeoPoint.fromInvertedDoubleString(this.sb.toString(), ' '));
		} else if(localName.equals("Distance")){
			this.inDistance = false;
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

	// ====================================
	// Helper-Methods
	// ====================================
}
