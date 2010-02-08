package org.andnav2.sys.ors.lus;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.Error;
import org.andnav2.sys.ors.adt.GeocodedAddress;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.ors.adt.lus.ICountrySubdivision;
import org.andnav2.sys.ors.adt.lus.ReverseGeocodePreferenceType;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.util.constants.Constants;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;


public class LUSRequester implements Constants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final boolean WORKAROUND_STRUCTURED_AS_FREEFORM = true;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public static ArrayList<GeocodedAddress> requestReverseGeocode(final Context ctx, final GeoPoint aGeoPoint, final ReverseGeocodePreferenceType aPreferenceType) throws MalformedURLException, IOException, SAXException, ORSException{
		if(Math.abs(aGeoPoint.getLatitudeE6()) < 10000 && Math.abs(aGeoPoint.getLongitudeE6()) < 10000) {
			return null;
		}

		return request(ctx, LUSRequestComposer.reverseGeocode(ctx, aGeoPoint, aPreferenceType), true);
	}

	public static ArrayList<GeocodedAddress> requestFreeformAddress(final Context ctx, final Country nat, final String freeFormAddress) throws MalformedURLException, IOException, SAXException, ORSException{
		return request(ctx, LUSRequestComposer.createFreeformAddressRequest(ctx, nat, freeFormAddress), false);
	}

	public static ArrayList<GeocodedAddress> requestStreetaddressCity(final Context ctx, final Country nat, final ICountrySubdivision pCountrySubdivision, final String pCity, final String pStreetName, final String pStreetNumber) throws MalformedURLException, IOException, SAXException, ORSException{
		if(shouldDoWorkaround(nat)) {
			return request(ctx, LUSRequestComposer.createFreeformAddressRequest(ctx, nat, structuredToFreeform(pCountrySubdivision, pCity, pStreetName, pStreetNumber)), false);
		} else {
			return request(ctx, LUSRequestComposer.createStreetaddressCityRequest(ctx, nat, pCountrySubdivision, pCity, pStreetName, pStreetNumber), false);
		}
	}

	public static ArrayList<GeocodedAddress> requestStreetaddressPostalcode(final Context ctx, final Country nat, final ICountrySubdivision pCountrySubdivision, final String pPostalCode, final String pStreetName, final String pStreetNumber) throws MalformedURLException, IOException, SAXException, ORSException{
		if(shouldDoWorkaround(nat)) {
			return request(ctx, LUSRequestComposer.createFreeformAddressRequest(ctx, nat, structuredToFreeform(pCountrySubdivision, pPostalCode, pStreetName, pStreetNumber)), false);
		} else {
			return request(ctx, LUSRequestComposer.createStreetaddressPostalcodeRequest(ctx, nat, pCountrySubdivision, pPostalCode, pStreetName, pStreetNumber), false);
		}
	}

	private static boolean shouldDoWorkaround(final Country nat) {
		final boolean doWorkaround;
		switch(nat){
			case USA:
			case CANADA:
				doWorkaround = false;
				break;
			default:
				doWorkaround = WORKAROUND_STRUCTURED_AS_FREEFORM; // true
		}
		return doWorkaround;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private static String structuredToFreeform(final ICountrySubdivision pCountrySubdivision, final String pCityOrZipCode, final String pStreetName, final String pStreetNumber) throws ORSException {
		/* Either one of these has to be set. */
		if((pCityOrZipCode == null || pCityOrZipCode.length() == 0) && (pStreetName == null || pStreetName.length() == 0)) {
			throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN, Error.SEVERITY_ERROR, "org.andnav2.ors.lus.structuredToFreeform.LUSRequester.structuredToFreeform()", "Either street/zip or streetname has to be set."));
		}

		final StringBuilder sb = new StringBuilder();

		if(pCityOrZipCode != null){
			sb.append(pCityOrZipCode);
			if(pStreetName != null){
				sb.append(',');
			}
		}
		if(pStreetName != null){
			sb.append(pStreetName);
			if(pStreetNumber != null){
				sb.append(' ')
				.append(pStreetNumber);
			}
		}

		return sb.toString();
	}

	private static ArrayList<GeocodedAddress> request(final Context ctx, final String locationRequest, final boolean pIsReverseGeocode) throws SAXException, ORSException, IOException{
		final URL requestURL = new URL(Preferences.getORSServer(ctx).URL_LOCATIONUTILITYSERVICE);

		final HttpURLConnection acon = (HttpURLConnection) requestURL.openConnection();
		acon.setAllowUserInteraction(false);
		acon.setRequestMethod("POST");
		acon.setRequestProperty("Content-Type", "application/xml");
		acon.setDoOutput(true);
		acon.setDoInput(true);
		acon.setUseCaches(false);

		final BufferedWriter xmlOut;
		try{
			xmlOut = new BufferedWriter(new OutputStreamWriter(acon.getOutputStream()));
		}catch(final SocketException se){
			throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN, Error.SEVERITY_ERROR, "org.andnav2.ors.lus.LUSRequester.request(...)", "Host unreachable."));
		}catch(final UnknownHostException uhe){
			throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN, Error.SEVERITY_ERROR, "org.andnav2.ors.lus.LUSRequester.request(...)", "Host unresolved."));
		}

		//		Log.d(DEBUGTAG, locationRequest);
		xmlOut.write(locationRequest);
		xmlOut.flush();
		xmlOut.close();


		/* Get a SAXParser from the SAXPArserFactory. */
		final SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp;
		try {
			sp = spf.newSAXParser();
		} catch (final ParserConfigurationException e) {
			throw new SAXException(e);
		}

		/* Get the XMLReader of the SAXParser we created. */
		final XMLReader xr = sp.getXMLReader();
		/* Create a new ContentHandler and apply it to the XML-Reader*/
		final DefaultHandler openLUSParser = (pIsReverseGeocode) ? new LUSReverseGeocodeParser() : new LUSGeocodeParser();
		xr.setContentHandler(openLUSParser);

		/* Parse the xml-data from our URL. */
		try{
			xr.parse(new InputSource(new BufferedInputStream(acon.getInputStream())));
		}catch(final Exception e){
			Log.e(DEBUGTAG, "Error", e);
		}

		/* The Handler now provides the parsed data to us. */
		if(pIsReverseGeocode) {
			return ((LUSReverseGeocodeParser)openLUSParser).getAddresses();
		} else {
			return ((LUSGeocodeParser)openLUSParser).getAddresses();
		}
	}


	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
