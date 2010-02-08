package org.andnav2.sys.ors.rs;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.AvoidFeature;
import org.andnav2.sys.ors.adt.aoi.AreaOfInterest;
import org.andnav2.sys.ors.adt.rs.DirectionsLanguage;
import org.andnav2.sys.ors.adt.rs.RoutePreferenceType;
import org.andnav2.sys.ors.util.Util;
import org.andnav2.sys.ors.util.constants.ORSXMLConstants;

import android.content.Context;



public class RSRequestComposer implements ORSXMLConstants {

	/**
	 * 
	 * @param ctx
	 * @param nat
	 * @param start
	 * @param vias
	 * @param end
	 * @param pRoutePreference
	 * @param pProvideGeometry
	 * @param pAvoidTolls
	 * @param pAvoidHighways
	 * @param pRequestHandle <code>true</code> will make the Route available on the server, accessible through the handle-ID.
	 * @param pAvoidAreas
	 * @return
	 */
	public static String create(final Context ctx, final DirectionsLanguage nat, final GeoPoint start, final List<GeoPoint> vias, final GeoPoint end, final RoutePreferenceType pRoutePreference, final boolean pProvideGeometry, final boolean pAvoidTolls, final boolean pAvoidHighways, final boolean pRequestHandle, final ArrayList<AreaOfInterest> pAvoidAreas){
		Assert.assertNotNull(start);
		Assert.assertNotNull(end);
		Assert.assertNotNull(pRoutePreference);

		final StringBuilder sb = new StringBuilder();
		final Formatter f = new Formatter(sb, Locale.ENGLISH);

		sb.append(XML_BASE_TAG_UTF8);
		f.format(XLS_OPENGIS_ROUTESERVICE_TAG_OPEN, nat.ID);

		f.format(XLS_REQUESTHEADER_TAG, Util.getORSClientName(ctx));
		sb.append(XLS_REQUESTMETHOD_ROUTE_TAG_OPEN);
		f.format(XLS_DETERMINEROUTEREQUEST_TAG_OPEN, pRequestHandle);
		sb.append(XLS_ROUTEPLAN_TAG_OPEN);

		f.format(XLS_ROUTEPREFERENCE_TAG, pRoutePreference.mDefinedName);

		sb.append(XLS_WAYPOINTLIST_TAG_OPEN)
		.append(XLS_STARTPOINT_TAG_OPEN)
		.append(XLS_POSITION_TAG_OPEN)
		.append(GML_POINT_TAG_OPEN);

		f.format(GML_POS_TAG, start.getLongitudeE6() / 1E6, start.getLatitudeE6() / 1E6);
		sb.append(GML_POINT_TAG_CLOSE)
		.append(XLS_POSITION_TAG_CLOSE)
		.append(XLS_STARTPOINT_TAG_CLOSE);

		if(vias != null){
			for (final GeoPoint via : vias) {
				sb.append(XLS_VIAPOINT_TAG_OPEN)
				.append(XLS_POSITION_TAG_OPEN)
				.append(GML_POINT_TAG_OPEN);

				f.format(GML_POS_TAG, via.getLongitudeE6() / 1E6, via.getLatitudeE6() / 1E6);
				sb.append(GML_POINT_TAG_CLOSE)
				.append(XLS_POSITION_TAG_CLOSE)
				.append(XLS_VIAPOINT_TAG_CLOSE);
			}
		}

		sb.append(XLS_ENDPOINT_TAG_OPEN)
		.append(XLS_POSITION_TAG_OPEN)
		.append(GML_POINT_TAG_OPEN);

		f.format(GML_POS_TAG, end.getLongitudeE6() / 1E6, end.getLatitudeE6() / 1E6);
		sb.append(GML_POINT_TAG_CLOSE)
		.append(XLS_POSITION_TAG_CLOSE)
		.append(XLS_ENDPOINT_TAG_CLOSE)
		.append(XLS_WAYPOINTLIST_TAG_CLOSE);

		if(pAvoidHighways || pAvoidTolls || (pAvoidAreas != null && pAvoidAreas.size() > 0)){
			sb.append(XLS_AVOIDLIST_TAG_OPEN);
			if((pAvoidAreas != null && pAvoidAreas.size() > 0)){
				for(final AreaOfInterest a : pAvoidAreas) {
					a.appendToStringBuilder(sb, f);
				}
			}

			if(pAvoidHighways){
				sb.append(XLS_AVOIDFEATURE_TAG_OPEN)
				.append(AvoidFeature.HIGHWAY.mDefiniton)
				.append(XLS_AVOIDFEATURE_TAG_CLOSE);
			}
			if(pAvoidTolls){
				sb.append(XLS_AVOIDFEATURE_TAG_OPEN)
				.append(AvoidFeature.TOLLWAY.mDefiniton)
				.append(XLS_AVOIDFEATURE_TAG_CLOSE);
			}

			sb.append(XLS_AVOIDLIST_TAG_CLOSE);
		}

		sb.append(XLS_ROUTEPLAN_TAG_CLOSE);

		f.format(XLS_ROUTEINSTRUCTIONSREQUEST_TAG, pProvideGeometry);

		sb.append(XLS_ROUTEGEOMETRYREQUEST_TAG)
		.append(XLS_DETERMINEROUTEREQUEST_TAG_CLOSE)
		.append(XLS_REQUESTMETHOD_ROUTE_TAG_CLOSE)
		.append(XLS_OPENGIS_ROUTESERVICE_TAG_CLOSE);

		return sb.toString();
	}

	/**
	 * Output similar to:
	 * <pre>&lt;xls:XLS xmlns:xls=&quot;http://www.opengis.net/xls&quot; xmlns:gml=&quot;http://www.opengis.net/gml&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;  xsi:schemaLocation=&quot;http://www.opengis.net/xls http://schemas.opengis.net/ols/1.1.0/RouteService.xsd
	 * version=&quot;1.1&quot; xls:lang=&quot;en&quot;&gt;
	 * 	&lt;xls:RequestHeader sessionID=&quot;987654321&quot;/&gt;
	 * 	&lt;xls:Request methodName=&quot;RouteRequest&quot; requestID=&quot;123456789&quot; version=&quot;1.1&quot;&gt;
	 * 		&lt;xls:DetermineRouteRequest distanceUnit=&quot;YD&quot;&gt;
	 * 			&lt;xls:RouteHandle routeID=&quot;1154174405531&quot;&gt;&lt;/xls:RouteHandle&gt;
	 * 			&lt;xls:RouteInstructionsRequest format=&quot;text/plain&quot;/&gt;
	 * 		&lt;/xls:DetermineRouteRequest&gt;
	 * 	&lt;/xls:Request&gt;
	 * &lt;/xls:XLS&gt;</pre>
	 * @param ctx
	 * @param routeHandle
	 * @return
	 */
	public static String create(final Context ctx, final DirectionsLanguage nat, final long pRouteHandle) {
		final StringBuilder sb = new StringBuilder();
		final Formatter f = new Formatter(sb, Locale.ENGLISH);

		f.format(XLS_OPENGIS_ROUTESERVICE_TAG_OPEN, nat.ID);
		f.format(XLS_REQUESTHEADER_TAG, Util.getORSClientName(ctx));
		sb.append(XLS_REQUESTMETHOD_ROUTE_TAG_OPEN);
		f.format(XLS_DETERMINEROUTEREQUEST_TAG_OPEN, true);
		f.format(XLS_ROUTEHANDLE_TAG, pRouteHandle);
		f.format(XLS_ROUTEINSTRUCTIONSREQUEST_TAG, true);
		sb.append(XLS_DETERMINEROUTEREQUEST_TAG_CLOSE)
		.append(XLS_REQUESTMETHOD_ROUTE_TAG_CLOSE)
		.append(XLS_OPENGIS_ROUTESERVICE_TAG_CLOSE);

		return sb.toString();
	}
}
