/**
 * 
 */
package org.andnav2.osm.views.overlay;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.overlay.OSMMapViewOverlayItem;

/**
 * @author phreed
 *
 */

public class OSMMapViewOverlayObstacle 
extends OSMMapViewOverlayItem
{
	public enum TYPE { M_OAR, M_OFD, M_OGL };
	
	/**
	 * Factory methods.
	 * 
	 * @param title
	 * @param description
	 * @param geo
	 * @return
	 */
	static public OSMMapViewOverlayObstacle get_instance(
			final String title, 
			final String description, 
			final GeoPoint geo) 
	{
		return new OSMMapViewOverlayObstacle(title, description, geo.getLatitudeE6(), geo.getLongitudeE6());
	}
	
	static public OSMMapViewOverlayObstacle get_instance(
			final String title, 
			final String description, 
			final int longitudeE6, 
			final int latitudeE6) 
	{
		return new OSMMapViewOverlayObstacle(title, description, longitudeE6, latitudeE6 );
	}
	
	/**
	 * @param aTitle this should be <b>singleLine</b> (no <code>'\n'</code> )
	 * @param aDescription a <b>multiLine</b> description ( <code>'\n'</code> possible)
	 * @param aGeoPoint
	 */
	protected OSMMapViewOverlayObstacle(
			final String title, 
			final String description, 
			final int latitudeE6, 
			final int longitudeE6) 
	{
		super(title, description, latitudeE6, longitudeE6);
	}
	
//	public OSMMapViewOverlayTactic(int dx, int dy) {
//		super(dx, dy);
//	}

	private TYPE mType;
	public void set_type(TYPE type) {  mType = type; }
	public TYPE get_type() { return mType; }
	
}
