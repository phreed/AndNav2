package org.andnav2.sys.ors.lus;
import java.util.Formatter;
import java.util.Locale;

import junit.framework.Assert;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.ors.adt.lus.ICountrySubdivision;
import org.andnav2.sys.ors.adt.lus.ReverseGeocodePreferenceType;
import org.andnav2.sys.ors.util.Util;
import org.andnav2.sys.ors.util.constants.ORSXMLConstants;

import android.content.Context;


public class LUSRequestComposer implements ORSXMLConstants {

	/**
	 * <pre>&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
	 * &lt;xls:XLS xmlns:xls=&quot;http://www.opengis.net/xls&quot; xmlns:sch=&quot;http://www.ascc.net/xml/schematron&quot; xmlns:gml=&quot;http://www.opengis.net/gml&quot; xmlns:xlink=&quot;http://www.w3.org/1999/xlink&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xsi:schemaLocation=&quot;http://www.opengis.net/xls http://schemas.opengis.net/ols/1.1.0/LocationUtilityService.xsd&quot; version=&quot;1.1&quot;&gt;
	 *  &lt;xls:RequestHeader/&gt;
	 * 	&lt;xls:Request methodName=&quot;ReverseGeocodeRequest&quot; requestID=&quot;123456789&quot; version=&quot;1.1&quot;&gt;
	 * 		&lt;xls:ReverseGeocodeRequest&gt;
	 * 			&lt;xls:Position&gt;
	 * 				&lt;gml:Point srsName=&quot;4326&quot;&gt;
	 * 					&lt;gml:pos&gt;8.7520131 49.4515134&lt;/gml:pos&gt;
	 * 				&lt;/gml:Point&gt;
	 * 			&lt;/xls:Position&gt;
	 * 			&lt;xls:ReverseGeocodePreference&gt;StreetAddress&lt;/xls:ReverseGeocodePreference&gt;
	 * 		&lt;/xls:ReverseGeocodeRequest&gt;
	 * 	&lt;/xls:Request&gt;
	 * &lt;/xls:XLS&gt;</pre>
	 */
	public static String reverseGeocode(final Context ctx, final GeoPoint aGeoPoint, final ReverseGeocodePreferenceType aPreferenceType){
		Assert.assertNotNull(aGeoPoint);
		Assert.assertNotNull(aPreferenceType);

		final StringBuilder sb = new StringBuilder();
		final Formatter f = new Formatter(sb, Locale.ENGLISH);

		sb.append(XML_BASE_TAG_UTF8)
		.append(XLS_OPENGIS_LOCATIONUTILITYSERVICE_TAG_OPEN);
		f.format(XLS_REQUESTHEADER_TAG, Util.getORSClientName(ctx));
		sb.append(XLS_REQUESTMETHOD_REVERSEGEOCODE_TAG_OPEN)
		.append(XLS_REVERSEGEOCODEREQUEST_TAG_OPEN);


		sb.append(XLS_POSITION_TAG_OPEN)
		.append(GML_POINT_TAG_OPEN);
		f.format(GML_POS_TAG, aGeoPoint.getLongitudeE6() / 1E6, aGeoPoint.getLatitudeE6() / 1E6);
		sb.append(GML_POINT_TAG_CLOSE)
		.append(XLS_POSITION_TAG_CLOSE);

		f.format(XLS_REVERSEGEOCODEPREFERENCE_TAG, aPreferenceType.mDefinedName);

		sb.append(XLS_REVERSEGEOCODEREQUEST_TAG_CLOSE)
		.append(XLS_REQUESTMETHOD_REVERSEGEOCODE_TAG_CLOSE)
		.append(XLS_OPENGIS_LOCATIONUTILITYSERVICE_TAG_CLOSE);

		return sb.toString();
	}

	/**
	 * <pre>&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
	 * &lt;xls:XLS xmlns:xls=&quot;http://www.opengis.net/xls&quot; xmlns:sch=&quot;http://www.ascc.net/xml/schematron&quot;
	 * xmlns:gml=&quot;http://www.opengis.net/gml&quot; xmlns:xlink=&quot;http://www.w3.org/1999/xlink&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xsi:schemaLocation=&quot;http://www.opengis.net/xls http://schemas.opengis.net/ols/1.1.0/LocationUtilityService.xsd&quot; version=&quot;1.1&quot;&gt;
	 * 	&lt;xls:RequestHeader/&gt;
	 * 	&lt;xls:Request methodName=&quot;GeocodeRequest&quot; requestID=&quot;123456789&quot; version=&quot;1.1&quot;&gt;
	 * 		&lt;xls:GeocodeRequest&gt;
	 * 			&lt;xls:Address countryCode=&quot;DE&quot;&gt;
	 * 				&lt;xls:StreetAddress&gt;
	 * 				  &lt;xls:Street&gt;Mannheimer Straﬂe&lt;/xls:Street&gt;
	 * 				&lt;/xls:StreetAddress&gt;
	 * 				&lt;xls:Place type=&quot;Municipality&quot;&gt;Schriesheim&lt;/xls:Place&gt;
	 * 			&lt;/xls:Address&gt;
	 * 		&lt;/xls:GeocodeRequest&gt;
	 * 	&lt;/xls:Request&gt;
	 * &lt;/xls:XLS&gt;</pre>
	 * @param nat
	 * @param pCity
	 * @param pStreetName
	 * @param pStreetNumber
	 * @return
	 */
	public static String createStreetaddressCityRequest(final Context ctx, final Country nat, final ICountrySubdivision pCountrySubdivision, final String pCity, String pStreetName, final String pStreetNumber){
		if(pStreetName == null) {
			pStreetName = ""; // empty streetname is like searching for the town only.
		}

		Assert.assertNotNull(nat);
		Assert.assertTrue(pStreetNumber == null || (pStreetNumber != null && pStreetNumber.length() > 0)); // null or at least 1 char

		final StringBuilder sb = new StringBuilder();
		final Formatter f = new Formatter(sb, Locale.ENGLISH);

		appendBaseRequestStart(ctx, nat, sb, f);

		sb.append(XLS_STREETADDRESS_TAG_OPEN);
		if(pStreetNumber != null){
			f.format(XLS_BUILDING_TAG, pStreetNumber);
		}
		f.format(XLS_STREET_TAG, pStreetName);
		sb.append(XLS_STREETADDRESS_TAG_CLOSE);

		if(pCountrySubdivision != null){
			f.format(XLS_PLACE_COUNTRYSUBDIVISION_TAG, pCountrySubdivision.getAbbreviation());
		}

		f.format(XLS_PLACE_MUNICIPALITY_TAG, pCity);

		appendBaseRequestEnd(sb, f);

		return sb.toString();
	}

	/**
	 * <pre>&lt;xls:XLS xmlns:xls=&quot;http://www.opengis.net/xls&quot; xmlns:sch=&quot;http://www.ascc.net/xml/schematron&quot;
	 * xmlns:gml=&quot;http://www.opengis.net/gml&quot; xmlns:xlink=&quot;http://www.w3.org/1999/xlink&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xsi:schemaLocation=&quot;http://www.opengis.net/xls http://schemas.opengis.net/ols/1.1.0/LocationUtilityService.xsd&quot; version=&quot;1.1&quot;&gt;
	 * 	&lt;xls:RequestHeader/&gt;
	 * 	&lt;xls:Request methodName=&quot;GeocodeRequest&quot; requestID=&quot;123456789&quot; version=&quot;1.1&quot;&gt;
	 * 		&lt;xls:GeocodeRequest&gt;
	 * 			&lt;xls:Address countryCode=&quot;DE&quot;&gt;
	 * 				&lt;xls:StreetAddress&gt;
	 * 					&lt;xls:Street&gt;Mannheimer strasse&lt;/xls:Street&gt;
	 * 				&lt;/xls:StreetAddress&gt;
	 * 				&lt;xls:PostalCode&gt;69198&lt;/xls:PostalCode&gt;
	 * 			&lt;/xls:Address&gt;
	 * 		&lt;/xls:GeocodeRequest&gt;
	 * 	&lt;/xls:Request&gt;
	 * &lt;/xls:XLS&gt;</pre>
	 * @param nat
	 * @param pCountrySubdivision
	 * @param pCity
	 * @param pStreetName
	 * @param pStreetNumber
	 * @return
	 */
	public static String createStreetaddressPostalcodeRequest(final Context ctx, final Country nat, final ICountrySubdivision pCountrySubdivision, final String pPostalCode, String pStreetName, final String pStreetNumber){
		if(pStreetName == null) {
			pStreetName = ""; // empty streetname is like searching for the town only.
		}

		Assert.assertNotNull(nat);
		Assert.assertTrue(pStreetNumber == null || (pStreetNumber != null && pStreetNumber.length() > 0)); // null or at least 1 char

		final StringBuilder sb = new StringBuilder();
		final Formatter f = new Formatter(sb, Locale.ENGLISH);

		appendBaseRequestStart(ctx, nat, sb, f);

		sb.append(XLS_STREETADDRESS_TAG_OPEN);
		if(pStreetNumber != null){
			f.format(XLS_BUILDING_TAG, pStreetNumber);
		}
		f.format(XLS_STREET_TAG, pStreetName);
		sb.append(XLS_STREETADDRESS_TAG_CLOSE);

		if(pCountrySubdivision != null){
			f.format(XLS_PLACE_COUNTRYSUBDIVISION_TAG, pCountrySubdivision);
		}

		f.format(XLS_POSTALCODE_TAG, pPostalCode);

		appendBaseRequestEnd(sb, f);

		return sb.toString();
	}


	/**
	 * @return xml-request String. i.e.:
	 * <pre>&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
	 * &lt;xls:XLS xmlns:xls=&quot;http://www.opengis.net/xls&quot; xmlns:sch=&quot;http://www.ascc.net/xml/schematron&quot;
	 * xmlns:gml=&quot;http://www.opengis.net/gml&quot; xmlns:xlink=&quot;http://www.w3.org/1999/xlink&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xsi:schemaLocation=&quot;http://www.opengis.net/xls http://schemas.opengis.net/ols/1.1.0/LocationUtilityService.xsd&quot; version=&quot;1.1&quot;&gt;
	 * 	&lt;xls:RequestHeader/&gt;
	 * 	&lt;xls:Request methodName=&quot;GeocodeRequest&quot; requestID=&quot;123456789&quot; version=&quot;1.1&quot;&gt;
	 * 		&lt;xls:GeocodeRequest&gt;
	 * 			&lt;xls:Address countryCode=&quot;DE&quot;&gt;
	 * 				&lt;xls:freeFormAddress&gt;bonn maximilianstraﬂe&lt;/xls:freeFormAddress&gt;
	 * 			&lt;/xls:Address&gt;
	 * 		&lt;/xls:GeocodeRequest&gt;
	 * 	&lt;/xls:Request&gt;
	 * &lt;/xls:XLS&gt;</pre>
	 */
	public static String createFreeformAddressRequest(final Context ctx, final Country nat, final String freeFormAddress){
		Assert.assertNotNull(freeFormAddress);
		Assert.assertTrue(freeFormAddress.length() > 0);

		final StringBuilder sb = new StringBuilder();
		final Formatter f = new Formatter(sb, Locale.ENGLISH);

		appendBaseRequestStart(ctx, nat, sb, f);

		f.format(XLS_FREEFORMADDRESS_TAG, freeFormAddress);

		appendBaseRequestEnd(sb, f);

		return sb.toString();
	}

	private static void appendBaseRequestStart(final Context ctx, final Country nat, final StringBuilder sb, final Formatter f){
		sb.append(XML_BASE_TAG_UTF8)
		.append(XLS_OPENGIS_LOCATIONUTILITYSERVICE_TAG_OPEN);
		f.format(XLS_REQUESTHEADER_TAG, Util.getORSClientName(ctx));
		sb.append(XLS_REQUESTMETHOD_GEOCODE_TAG_OPEN)
		.append(XLS_GEOCODEREQUEST_TAG_OPEN);
		if(nat == null) {
			f.format(XLS_ADDRESS_TAG_OPEN, Country.UNKNOWN);
		} else {
			f.format(XLS_ADDRESS_TAG_OPEN, nat.COUNTRYCODE);
		}
	}

	private static void appendBaseRequestEnd(final StringBuilder sb, final Formatter f){
		sb.append(XLS_ADDRESS_TAG_CLOSE)
		.append(XLS_GEOCODEREQUEST_TAG_CLOSE)
		.append(XLS_REQUESTMETHOD_GEOCODE_TAG_CLOSE)
		.append(XLS_OPENGIS_LOCATIONUTILITYSERVICE_TAG_CLOSE);
	}
}
