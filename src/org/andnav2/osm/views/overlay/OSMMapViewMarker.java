/**
 * 
 */
package org.andnav2.osm.views.overlay;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Point;


/**
 * An OSMMarker class for drawn markers.
 * 
 * @author phreed
 *
 */

public  interface OSMMapViewMarker 
{
	
	/**
	 * 
	 */
	public void relocate(final Point location) ;
	/**
	 * If marker cannot be drawn it returns false.
	 * 
	 */
	public boolean onDraw(final Canvas canvas, final MapPoint location) ;

	public boolean onDrawFocused(Canvas canvas, final MapPoint location) ;

	public void draw(Canvas canvas, MapPoint mp) ;
	
	/**
	 * wrapper methods - they pass through to the wrapped object.
	 */
	public int getOpacity() ;
	public void setAlpha(int alpha) ;
	
	public int getIntrinsicWidth() ;
	public int getIntrinsicHeight() ;
	
	public void setColorFilter(ColorFilter cf) ;
	
	public Point getHotSpot() ;

	public void setBounds(int left, int top, int right, int bottom);

}
