package org.andnav2.sys.ors.ds;
import java.util.Formatter;
import java.util.Locale;

import junit.framework.Assert;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.ds.DirectoryType;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.sys.ors.util.Util;
import org.andnav2.sys.ors.util.constants.ORSXMLConstants;

import android.content.Context;


public class DSRequestComposer implements ORSXMLConstants {

	/**
	 * <pre>&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
	 * &lt;xls:XLS xmlns:xls=&quot;http://www.opengis.net/xls&quot; xmlns:sch=&quot;http://www.ascc.net/xml/schematron&quot;
	 * xmlns:gml=&quot;http://www.opengis.net/gml&quot; xmlns:xlink=&quot;http://www.w3.org/1999/xlink&quot;
	 * xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
	 * xsi:schemaLocation=&quot;http://www.opengis.net/xls http://schemas.opengis.net/ols/1.1.0/DirectoryService.xsd&quot; version=&quot;1.1&quot;&gt;
	 * 	&lt;xls:RequestHeader/&gt;
	 * 	&lt;xls:Request methodName=&quot;DirectoryRequest&quot; requestID=&quot;123456789&quot; version=&quot;1.1&quot;&gt;
	 * 		&lt;xls:DirectoryRequest distanceUnit=&quot;M&quot; sortCriteria=&quot;Distance&quot;&gt;
	 * 			&lt;xls:POILocation&gt;
	 * 				&lt;xls:WithinDistance&gt;
	 * 					&lt;xls:Position&gt;
	 * 						&lt;gml:Point&gt;
	 * 							&lt;gml:pos&gt;7.092298 50.733306&lt;/gml:pos&gt;
	 * 						&lt;/gml:Point&gt;
	 * 					&lt;/xls:Position&gt;
	 * 					&lt;xls:MinimumDistance value=&quot;500&quot; uom=&quot;M&quot;/&gt;
	 * 				&lt;/xls:WithinDistance&gt;
	 * 			&lt;/xls:POILocation&gt;
	 * 			&lt;xls:POIProperties directoryType=&quot;OSM&quot;&gt;
	 * 				&lt;xls:POIProperty name=&quot;Keyword&quot; value=&quot;public_tran&quot;/&gt;
	 * 			&lt;/xls:POIProperties&gt;
	 * 		&lt;/xls:DirectoryRequest&gt;
	 * 	&lt;/xls:Request&gt;
	 * &lt;/xls:XLS&gt;</pre>
	 */
	public static String create(final Context ctx, final GeoPoint aGeoPoint, final POIType aPOIType, final int pRadiusMeters){
		Assert.assertNotNull(aGeoPoint);
		Assert.assertNotNull(aPOIType);

		final StringBuilder sb = new StringBuilder();
		final Formatter f = new Formatter(sb, Locale.ENGLISH);

		sb.append(XML_BASE_TAG_UTF8)
		.append(XLS_OPENGIS_DIRECTORYSERVICE_TAG_OPEN);
		f.format(XLS_REQUESTHEADER_TAG, Util.getORSClientName(ctx));
		sb.append(XLS_REQUESTMETHOD_DIRECTORY_TAG_OPEN)
		.append(XLS_DIRECTORYREQUEST_TAG_OPEN)
		.append(XLS_POILOCATION_TAG_OPEN)
		.append(XLS_WITHINDISTANCE_TAG_OPEN)
		.append(XLS_POSITION_TAG_OPEN)
		.append(GML_POINT_TAG_OPEN);

		f.format(GML_POS_TAG, aGeoPoint.getLongitudeE6() / 1E6, aGeoPoint.getLatitudeE6() / 1E6);

		sb.append(GML_POINT_TAG_CLOSE)
		.append(XLS_POSITION_TAG_CLOSE);

		f.format(XLS_MAXIMUMDISTANCE_TAG, pRadiusMeters);

		sb.append(XLS_WITHINDISTANCE_TAG_CLOSE)
		.append(XLS_POILOCATION_TAG_CLOSE);
		f.format(XLS_POIPROPERTIES_TAG_OPEN, DirectoryType.OSM.mName);

		switch(aPOIType.POIGROUPS[0]){
			case MAINGROUP:
				f.format(XLS_POIPROPERTY_TAG, XLS_POIPROPERTY_MAINGROUP_NAME, aPOIType.RAWNAME);
				break;
			default:
				f.format(XLS_POIPROPERTY_TAG, XLS_POIPROPERTY_SUBGROUP_NAME, aPOIType.RAWNAME);
				break;
		}

		sb.append(XLS_POIPROPERTIES_TAG_CLOSE)
		.append(XLS_DIRECTORYREQUEST_TAG_CLOSE)
		.append(XLS_REQUESTMETHOD_DIRECTORY_TAG_CLOSE)
		.append(XLS_OPENGIS_DIRECTORYSERVICE_TAG_CLOSE);

		return sb.toString();
	}
}
