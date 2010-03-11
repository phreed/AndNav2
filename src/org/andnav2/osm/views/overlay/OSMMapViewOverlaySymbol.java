/**
 * 
 */
package org.andnav2.osm.views.overlay;

import org.andnav2.osm.adt.GeoPoint;

/**
 * @author phreed
 *
 */

public class OSMMapViewOverlaySymbol 
extends OSMMapViewOverlayItem
{
	public enum STD_IDENTITY { FRIEND, HOSTILE, UNKNOWN, NEUTRAL };
	
	/**
	 * Factory methods.
	 * 
	 * @param title
	 * @param description
	 * @param geo
	 * @return
	 */
	static public OSMMapViewOverlaySymbol get_instance(
			final String title, 
			final String description, 
			final GeoPoint geo) 
	{
		return new OSMMapViewOverlaySymbol(title, description, geo.getLatitudeE6(), geo.getLongitudeE6());
	}
	
	static public OSMMapViewOverlaySymbol get_instance(
			final String title, 
			final String description, 
			final int longitudeE6, 
			final int latitudeE6) 
	{
		return new OSMMapViewOverlaySymbol(title, description, longitudeE6, latitudeE6 );
	}
	
	/**
	 * @param aTitle this should be <b>singleLine</b> (no <code>'\n'</code> )
	 * @param aDescription a <b>multiLine</b> description ( <code>'\n'</code> possible)
	 * @param aGeoPoint
	 */
	protected OSMMapViewOverlaySymbol(
			final String title, 
			final String description, 
			final int latitudeE6, 
			final int longitudeE6) 
	{
		super(title, description, latitudeE6, longitudeE6);
	}
	
//	public OSMMapViewOverlaySymbol(int dx, int dy) {
//		super(dx, dy);
//	}

	private STD_IDENTITY mSi;
	public void set_std_identity(STD_IDENTITY si) {  mSi = si; }
	public STD_IDENTITY get_std_identity() { return mSi; }
	
}
