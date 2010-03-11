/**
 * 
 */
package org.andnav2.osm.views.overlay;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.OSMMapView;
import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;

import android.graphics.Point;

/**
 * A wrapper for GeoPoint which computes the location of the point in screen pixels.
 * 
 * @author phreed
 *
 */
public class MapPoint 
{
	private Point screenCoords;
	private GeoPoint mGeo;

	public MapPoint(GeoPoint geo,  final OSMMapViewProjection proj) {
		screenCoords = new Point();
		proj.toPixels(geo, screenCoords);
		mGeo = geo;
	}
	
	public MapPoint(GeoPoint geo, final OSMMapView mv) {
		this(geo, mv.getProjection());
	}
	
	final public Point asPoint() { return screenCoords; }
	
	final public GeoPoint asGeoPoint() { return mGeo; }
}
