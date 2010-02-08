// Created by plusminus on 17:17:32 - 31.10.2008
package org.andnav2.sys.ors.util.constants;

import org.andnav2.osm.util.constants.GMLXMLConstants;
import org.andnav2.sys.ors.adt.ds.DirectoryType;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.sys.ors.adt.lus.ReverseGeocodePreferenceType;
import org.andnav2.sys.ors.adt.rs.RoutePreferenceType;


public interface ORSXMLConstants extends GMLXMLConstants {

	public static final String CLIENTNAME_ANDNAV_PREFIX = "andnav";
	public static final String CLIENTNAME_SPACER = "|";

	// ===========================================================
	// RouteService
	// ===========================================================

	public static final String XML_BASE_TAG_UTF8 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	public static final String XLS_OPENGIS_ROUTESERVICE_TAG_OPEN = "<xls:XLS xmlns:xls=\"http://www.opengis.net/xls\" xmlns:sch=\"http://www.ascc.net/xml/schematron\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/xls http://schemas.opengis.net/ols/1.1.0/RouteService.xsd\" version=\"1.1\" xls:lang=\"%s\">";
	public static final String XLS_OPENGIS_ROUTESERVICE_TAG_CLOSE = "</xls:XLS>";
	/** Needs to be formatted with the clientname, i.e.: "andnav2_0.9.3.2.1_beta" without parentheses. */
	public static final String XLS_REQUESTHEADER_TAG = "<xls:RequestHeader clientName=\"%s\"/>";
	public static final String XLS_REQUESTMETHOD_ROUTE_TAG_OPEN = "<xls:Request methodName=\"RouteRequest\" requestID=\"123456789\" version=\"1.1\">"; // ID = ?
	public static final String XLS_REQUESTMETHOD_ROUTE_TAG_CLOSE = "</xls:Request>";
	/** Needs to be formatted with a boolean for provideRouteHandle. */
	public static final String XLS_DETERMINEROUTEREQUEST_TAG_OPEN = "<xls:DetermineRouteRequest distanceUnit=\"M\" provideRouteHandle=\"%b\">";
	public static final String XLS_DETERMINEROUTEREQUEST_TAG_CLOSE = "</xls:DetermineRouteRequest>";

	/** Needs to be formatted with an integer for routeID. */
	public static final String XLS_ROUTEHANDLE_TAG = "<xls:RouteHandle routeID=\"%d\"/>" ;

	public static final String XLS_ROUTEPLAN_TAG_OPEN = "<xls:RoutePlan>";
	public static final String XLS_ROUTEPLAN_TAG_CLOSE = "</xls:RoutePlan>";
	/** Needs to be formatted with a {@link RoutePreferenceType}. */
	public static final String XLS_ROUTEPREFERENCE_TAG = "<xls:RoutePreference>%s</xls:RoutePreference>";
	public static final String XLS_WAYPOINTLIST_TAG_OPEN = "<xls:WayPointList>";
	public static final String XLS_WAYPOINTLIST_TAG_CLOSE = "</xls:WayPointList>";
	public static final String XLS_STARTPOINT_TAG_OPEN = "<xls:StartPoint>";
	public static final String XLS_STARTPOINT_TAG_CLOSE = "</xls:StartPoint>";
	public static final String XLS_VIAPOINT_TAG_OPEN = "<xls:ViaPoint>";
	public static final String XLS_VIAPOINT_TAG_CLOSE = "</xls:ViaPoint>";
	public static final String XLS_ENDPOINT_TAG_OPEN = "<xls:EndPoint>";
	public static final String XLS_ENDPOINT_TAG_CLOSE = "</xls:EndPoint>";
	public static final String XLS_POSITION_TAG_OPEN = "<xls:Position>";
	public static final String XLS_POSITION_TAG_CLOSE = "</xls:Position>";
	/** Needs to be formatted with two Floating-Point numbers. */
	public static final String XLS_AVOIDLIST_TAG_OPEN = "<xls:AvoidList>";
	public static final String XLS_AVOIDLIST_TAG_CLOSE = "</xls:AvoidList>";
	public static final String XLS_AVOIDFEATURE_TAG_OPEN = "<xls:AvoidFeature>";
	public static final String XLS_AVOIDFEATURE_TAG_CLOSE = "</xls:AvoidFeature>";
	public static final String XLS_AREAOFINTEREST_TAG_OPEN = "<xls:AOI>";
	public static final String XLS_AREAOFINTEREST_TAG_CLOSE = "</xls:AOI>";
	public static final String XLS_LOCATION_TAG_OPEN = "<xls:_Location>";
	public static final String XLS_LOCATION_TAG_CLOSE = "</xls:_Location>";


	/** Needs to be formatted with a boolean. */
	public static final String XLS_ROUTEINSTRUCTIONSREQUEST_TAG = "<xls:RouteInstructionsRequest provideGeometry=\"%b\"/>";
	public static final String XLS_ROUTEGEOMETRYREQUEST_TAG = "<xls:RouteGeometryRequest/>";

	// ===========================================================
	// Location Utility Service
	// ===========================================================

	public static final String XLS_OPENGIS_LOCATIONUTILITYSERVICE_TAG_OPEN = "<xls:XLS xmlns:xls=\"http://www.opengis.net/xls\" xmlns:sch=\"http://www.ascc.net/xml/schematron\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/xls http://schemas.opengis.net/ols/1.1.0/LocationUtilityService.xsd\" version=\"1.1\">";
	public static final String XLS_OPENGIS_LOCATIONUTILITYSERVICE_TAG_CLOSE = "</xls:XLS>";
	public static final String XLS_REQUESTMETHOD_GEOCODE_TAG_OPEN = "<xls:Request methodName=\"GeocodeRequest\" requestID=\"123456789\" version=\"1.1\">";
	public static final String XLS_REQUESTMETHOD_GEOCODE_TAG_CLOSE = "</xls:Request>";
	public static final String XLS_GEOCODEREQUEST_TAG_OPEN = "<xls:GeocodeRequest>";
	public static final String XLS_GEOCODEREQUEST_TAG_CLOSE = "</xls:GeocodeRequest>";
	/** Needs to be formatted with a Countrycode-String. */
	public static final String XLS_ADDRESS_TAG_OPEN = "<xls:Address countryCode=\"%s\">";
	public static final String XLS_ADDRESS_TAG_CLOSE = "</xls:Address>";
	/** Needs to be formatted with a Freeform Address-String. */
	public static final String XLS_FREEFORMADDRESS_TAG = "<xls:freeFormAddress>%s</xls:freeFormAddress>";

	public static final String XLS_STREETADDRESS_TAG_OPEN = "<xls:StreetAddress>";
	public static final String XLS_STREETADDRESS_TAG_CLOSE = "</xls:StreetAddress>";

	/** Needs to be formatted with a Street-String. */
	public static final String XLS_STREET_TAG = "<xls:Street>%s</xls:Street>";

	/** Needs to be formatted with a PostalCode-String. */
	public static final String XLS_POSTALCODE_TAG = "<xls:PostalCode>%s</xls:PostalCode>";

	/** Needs to be formatted with a String. */
	public static final String XLS_PLACE_MUNICIPALITY_TAG = "<xls:Place type=\"Municipality\">%s</xls:Place>";
	/** Needs to be formatted with a String. */
	public static final String XLS_PLACE_COUNTRYSUBDIVISION_TAG = "<xls:Place type=\"CountrySubdivision\">%s</xls:Place>";

	/** Needs to be formatted with a PostalCode-String. */
	public static final String XLS_BUILDING_TAG = "<xls:Building number=\"%s\"/>";

	public static final String XLS_REQUESTMETHOD_REVERSEGEOCODE_TAG_OPEN = "<xls:Request methodName=\"ReverseGeocodeRequest\" requestID=\"123456789\" version=\"1.1\">";
	public static final String XLS_REQUESTMETHOD_REVERSEGEOCODE_TAG_CLOSE = "</xls:Request>";
	public static final String XLS_REVERSEGEOCODEREQUEST_TAG_OPEN = "<xls:ReverseGeocodeRequest>";
	public static final String XLS_REVERSEGEOCODEREQUEST_TAG_CLOSE = "</xls:ReverseGeocodeRequest>";

	/** Needs to be formatted with a {@link ReverseGeocodePreferenceType}*/
	public static final String XLS_REVERSEGEOCODEPREFERENCE_TAG = "<xls:ReverseGeocodePreference>%s</xls:ReverseGeocodePreference>";

	// ===========================================================
	// Directory Service
	// ===========================================================
	public static final String XLS_OPENGIS_DIRECTORYSERVICE_TAG_OPEN = "<xls:XLS xmlns:xls=\"http://www.opengis.net/xls\" xmlns:sch=\"http://www.ascc.net/xml/schematron\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/xls http://schemas.opengis.net/ols/1.1.0/DirectoryService.xsd\" version=\"1.1\">";
	public static final String XLS_OPENGIS_DIRECTORYSERVICE_TAG_CLOSE = "</xls:XLS>";
	public static final String XLS_REQUESTMETHOD_DIRECTORY_TAG_OPEN = "<xls:Request methodName=\"DirectoryRequest\" requestID=\"123456789\" version=\"1.1\">";
	public static final String XLS_REQUESTMETHOD_DIRECTORY_TAG_CLOSE = "</xls:Request>";
	public static final String XLS_DIRECTORYREQUEST_TAG_OPEN = "<xls:DirectoryRequest distanceUnit=\"M\" sortCriteria=\"Distance\">";
	public static final String XLS_DIRECTORYREQUEST_TAG_CLOSE = "</xls:DirectoryRequest>";

	public static final String XLS_POILOCATION_TAG_OPEN = "<xls:POILocation>";
	public static final String XLS_POILOCATION_TAG_CLOSE = "</xls:POILocation>";
	/** Needs to be formatted with a {@link DirectoryType} */
	public static final String XLS_POIPROPERTIES_TAG_OPEN = "<xls:POIProperties directoryType=\"%s\">";
	public static final String XLS_POIPROPERTIES_TAG_CLOSE = "</xls:POIProperties>";

	public static final String XLS_WITHINDISTANCE_TAG_OPEN = "<xls:WithinDistance>";
	public static final String XLS_WITHINDISTANCE_TAG_CLOSE = "</xls:WithinDistance>";

	/** Needs to be formatted with a meter-amount. */
	public static final String XLS_MAXIMUMDISTANCE_TAG = "<xls:MaximumDistance value=\"%d\" uom=\"M\"/>";
	/** Needs to be formatted with a meter-amount. */
	public static final String XLS_MINIMUMDISTANCE_TAG = "<xls:MinimumDistance value=\"%d\" uom=\"M\"/>";

	/** Needs to be formatted with (XLS_POIPROPERTY_MAINGROUP_NAME && {@link POIType}) || (XLS_POIPROPERTY_SUBGROUP_NAME && {@link POIType}) */
	public static final String XLS_POIPROPERTY_TAG = "<xls:POIProperty name=\"%s\" value=\"%s\"/>";
	public static final String XLS_POIPROPERTY_MAINGROUP_NAME = "Keyword";
	public static final String XLS_POIPROPERTY_SUBGROUP_NAME = "NAICS_type";

	// ===========================================================
	// Traffic Service
	// ===========================================================

	public static final String WFS_GETFEATURE_TAG_OPEN = "<wfs:GetFeature service=\"WFS\" version=\"1.1.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.1.0/wfs.xsd\">;";
	public static final String WFS_GETFEATURE_TAG_GML2_OPEN = "<wfs:GetFeature service=\"WFS\" version=\"1.0.0\" outputFormat=\"GML2\" xmlns:topp=\"http://www.openplans.org/topp\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd\">";
	public static final String WFS_GETFEATURE_TAG_CLOSE = "</wfs:GetFeature>";
	/** Needs to be formatted with a {@link String}. */
	public static final String WFS_QUERY_TAG_OPEN = "<wfs:Query typeName=\"%s\">";
	public static final String WFS_QUERY_TAG_CLOSE = "</wfs:Query>";
	/** Needs to be formatted with a {@link String}. */
	public static final String WFS_PROPERTY_TAG = "<wfs:PropertyName>%s</wfs:PropertyName>";
	public static final String OGC_FILTER_TAG_OPEN = "<ogc:Filter>";
	public static final String OGC_FILTER_TAG_CLOSE = "</ogc:Filter>";
	public static final String OGC_BBOX_TAG_OPEN = "<ogc:BBOX>";
	public static final String OGC_BBOX_TAG_CLOSE = "</ogc:BBOX>";
	/** Needs to be formatted with a {@link String}. */
	public static final String OGC_PROPERTY_TAG = "<ogc:PropertyName>%s</ogc:PropertyName>";

	// ===========================================================
	// Altitude Profile Service
	// ===========================================================

	public static final String XLS_OPENGIS_ALTITUDESERVICE_TAG_OPEN = "<xls:XLS xmlns:xls=\"http://www.opengis.net/xls\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:gml=\"http://www.opengis.net/gml\" version=\"1.1\" xsi:schemaLocation=\"http://www.opengis.net/xls http://schemas.opengis.net/ols/1.1.0/RouteService.xsd\">";
	public static final String XLS_OPENGIS_ALTITUDESERVICE_TAG_CLOSE = "</xls:XLS>";


	public static final String XLS_ROUTEGEOMETRY_TAG_OPEN = "<xls:RouteGeometry>";
	public static final String XLS_ROUTEGEOMETRY_TAG_CLOSE = "</xls:RouteGeometry>";
}
