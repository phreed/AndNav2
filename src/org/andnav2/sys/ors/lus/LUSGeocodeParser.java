package org.andnav2.sys.ors.lus;

import java.util.ArrayList;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.Error;
import org.andnav2.sys.ors.adt.GeocodedAddress;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.util.constants.Constants;
import org.andnav2.util.constants.TimeConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/**
 * @author Nicolas Gramlich
 * Parses XML-Data like:
 * <pre>&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 * &lt;xls:XLS xmlns:xls=&quot;http://www.opengis.net/xls&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:gml=&quot;http://www.opengis.net/gml&quot; version=&quot;1.1&quot; xsi:schemaLocation=&quot;http://www.opengis.net/xls http://schemas.opengis.net/ols/1.1.0/LocationUtilityService.xsd&quot;&gt;
 *   &lt;xls:ResponseHeader xsi:type=&quot;xls:ResponseHeaderType&quot;/&gt;
 *   &lt;xls:Response xsi:type=&quot;xls:ResponseType&quot; requestID=&quot;123456789&quot; version=&quot;1.1&quot; numberOfResponses=&quot;1&quot;&gt;
 *     &lt;xls:GeocodeResponse xsi:type=&quot;xls:GeocodeResponseType&quot;&gt;
 *       &lt;xls:GeocodeResponseList numberOfGeocodedAddresses=&quot;1&quot;&gt;
 *         &lt;xls:GeocodedAddress&gt;
 *           &lt;gml:Point&gt;
 *             &lt;gml:pos srsName=&quot;EPSG:4326&quot;&gt;7.0986258 50.7323634&lt;/gml:pos&gt;
 *           &lt;/gml:Point&gt;
 *           &lt;xls:Address countryCode=&quot;de&quot;&gt;
 *             &lt;xls:StreetAddress&gt;
 *               &lt;xls:Street officialName=&quot;Maximilianstraﬂe&quot;/&gt;
 *             &lt;/xls:StreetAddress&gt;
 *             &lt;xls:Place type=&quot;CountrySubdivision&quot;&gt;Nordrhein-Westfalen&lt;/xls:Place&gt;
 *             &lt;xls:Place type=&quot;Municipality&quot;&gt;Bonn&lt;/xls:Place&gt;
 *             &lt;xls:PostalCode&gt;53115&lt;/xls:PostalCode&gt;
 *           &lt;/xls:Address&gt;
 *           &lt;xls:GeocodeMatchCode accuracy=&quot;0.9&quot;/&gt;
 *         &lt;/xls:GeocodedAddress&gt;
 *       &lt;/xls:GeocodeResponseList&gt;
 *     &lt;/xls:GeocodeResponse&gt;
 *   &lt;/xls:Response&gt;
 * &lt;/xls:XLS&gt;</pre>
 *
 */
public class LUSGeocodeParser extends DefaultHandler implements TimeConstants, Constants{
	// ====================================
	// Constants
	// ====================================

	protected static final int LATITUDE_OVERMAX = (int)(81 * 1E6);
	protected static final int LONGITUDE_OVERMAX = (int)(181 * 1E6);

	// ====================================
	// Fields
	// ====================================

	private final ArrayList<Error> mErrors = new ArrayList<Error>();

	private ArrayList<GeocodedAddress> mAddresses;

	private boolean inXLS = false;
	private boolean inResponseHeader = false;
	private boolean inResponse = false;
	private boolean inGeocodeResponse = false;
	private boolean inGeocodeResponseList = false;
	private boolean inGeocodedAddress = false;
	private boolean inAddress = false;
	private boolean inStreetAddress = false;
	private boolean inStreet = false;
	private boolean inPlaceCountrySubdivision = false;
	private boolean inPlaceMunicipality = false;
	private boolean inPostalCode = false;
	private boolean inGeocodeMatchCode = false;
	private boolean inPoint = false;
	private boolean inPos = false;

	private GeocodedAddress mTmpGeocodedAddress;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<Error> getErrors(){
		return this.mErrors;
	}

	public ArrayList<GeocodedAddress> getAddresses() throws ORSException{
		if(this.mErrors != null && this.mErrors.size() > 0) {
			throw new ORSException(this.mErrors);
		}

		return this.mAddresses;
	}

	// ====================================
	// Methods from Superclasses
	// ====================================

	@Override
	public void startDocument() throws SAXException {
		this.mAddresses = new ArrayList<GeocodedAddress>();
		super.startDocument();
	}

	@Override
	public void startElement(final String uri, final String localName, final String name, final Attributes attributes) throws SAXException {
		if(localName.equals("Error") || name.equals("Error")){
			final String errorCode = attributes.getValue("", "errorCode");
			final String severity = attributes.getValue("", "severity");
			final String locationPath = attributes.getValue("", "locationPath");
			final String message = attributes.getValue("", "message");
			this.mErrors.add(new org.andnav2.sys.ors.adt.Error(errorCode, severity, locationPath, message));
		}

		this.sb.setLength(0);

		if(localName.equals("XLS")){
			this.inXLS = true;
		} else if(localName.equals("ResponseHeader")){
			this.inResponseHeader = true;
		} else if(localName.equals("Response")){
			this.inResponse = true;
		} else if(localName.equals("GeocodeResponse")){
			this.inGeocodeResponse = true;
		} else if(localName.equals("GeocodeResponseList")){
			this.inGeocodeResponseList = true;
		} else if(localName.equals("GeocodedAddress")){
			this.inGeocodedAddress = true;
		} else if(localName.equals("Point")){
			this.inPoint = true;
		} else if(localName.equals("pos")){
			this.inPos = true;
		} else if(localName.equals("Address")){
			this.inAddress = true;
			final String countryCode = attributes.getValue("", "countryCode");
			if(countryCode != null) {
				this.mTmpGeocodedAddress.setNationality(Country.fromAbbreviation(countryCode));
			}
		} else if(localName.equals("StreetAddress")){
			this.inStreetAddress = true;
		} else if(localName.equals("Street")){
			this.inStreet = true;
			final String officialName = attributes.getValue("", "officialName");
			if(officialName != null) {
				this.mTmpGeocodedAddress.setStreetNameOfficial(officialName);
			}
		} else if(localName.equals("Place")){
			final String type = attributes.getValue("", "type");
			if(type.compareTo("CountrySubdivision") == 0){
				this.inPlaceCountrySubdivision = true;
			}else if(type.compareTo("Municipality") == 0){
				this.inPlaceMunicipality = true;
			}
		} else if(localName.equals("PostalCode")){
			this.inPostalCode = true;
		} else if(localName.equals("GeocodeMatchCode")){
			this.inGeocodeMatchCode = true;
			final String accuracy = attributes.getValue("", "accuracy");
			if(accuracy != null) {
				this.mTmpGeocodedAddress.setAccuracy(Float.parseFloat(accuracy));
			}
		} else {
			Log.w(DEBUGTAG, "Unexpected tag: '" + name + "'");
		}
		super.startElement(uri, localName, name, attributes);
	}

	protected StringBuilder sb = new StringBuilder();

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
			this.inResponseHeader = false;
		} else if(localName.equals("Response")){
			this.inResponse = false;
		} else if(localName.equals("GeocodeResponse")){
			this.inGeocodeResponse = false;
		} else if(localName.equals("GeocodeResponseList")){
			this.inGeocodeResponseList = false;
		} else if(localName.equals("GeocodedAddress")){
			this.inGeocodedAddress = false;
		} else if(localName.equals("Point")){
			this.inPoint = false;
		} else if(localName.equals("pos")){
			this.inPos = false;
			final GeoPoint gp = GeoPoint.fromInvertedDoubleString(this.sb.toString(), ' ');

			this.mTmpGeocodedAddress = new GeocodedAddress(gp);
			this.mAddresses.add(this.mTmpGeocodedAddress);
		} else if(localName.equals("Address")){
			this.inAddress = false; // CC
		} else if(localName.equals("StreetAddress")){
			this.inStreetAddress = false;
		} else if(localName.equals("Street")){
			this.inStreet = false;
		} else if(localName.equals("Place")){
			if(this.inPlaceCountrySubdivision){
				this.inPlaceCountrySubdivision = false;
				this.mTmpGeocodedAddress.setCountrySubdivision(this.sb.toString());
			}else if(this.inPlaceMunicipality){
				this.inPlaceMunicipality = false;
				this.mTmpGeocodedAddress.setMunicipality(this.sb.toString());
			}
		} else if(localName.equals("PostalCode")){
			this.inPostalCode = false;
			this.mTmpGeocodedAddress.setPostalCode(this.sb.toString());
		} else if(localName.equals("GeocodeMatchCode")){
			this.inGeocodeMatchCode = false;
		} else {
			Log.w(DEBUGTAG, "Unexpected end-tag: '" + name + "'");
		}

		// Reset the stringbuffer
		this.sb.setLength(0);

		super.endElement(uri, localName, name);
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
}
