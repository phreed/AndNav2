/**
 * 
 */
package org.andnav2.osm.views.overlay;

import org.andnav2.osm.adt.GeoPoint;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.Point;

/**
 * A drawable OSMMarker class to hold a drawable and its hotspot. 
 * The drawable object knows how to draw himself. 
 * The default this base class is a bitmap centered at its upper left corner.
 * 
 * @author phreed
 *
 */
public class OSMMapViewMarkerSimple 
implements OSMMapViewMarker
{
	final protected Drawable mMarker;
	final protected Point mHotSpot;
	final protected int mWidth, mHeight;
	
	protected Point mLocation;
	
	/**
	 * 
	 */
	public OSMMapViewMarkerSimple(
			final Drawable marker, 
			final Point hotspot)
	{
		super();
		this.mMarker = marker;
		this.mHotSpot = hotspot;
		this.mWidth = this.mMarker.getIntrinsicWidth();
		this.mHeight = this.mMarker.getIntrinsicHeight();
	}

	/**
	 * 
	 */
	public void relocate(final Point location) {
		this.mLocation = location;
	}
	/**
	 * If marker cannot be drawn it returns false.
	 * 
	 */
	@Override
	public boolean onDraw(final Canvas canvas, final MapPoint location) {
		
		if (location == null && this.mLocation == null) return false;
		Point locate = (location == null) ? this.mLocation : location.asPoint();
		
		final int left = locate.x - this.mHotSpot.x;
		final int top = locate.y - this.mHotSpot.y;
		
		final int right = left + this.mWidth;
		final int bottom = top + this.mHeight;

		if(right < 0) return false;
		if(bottom < 0) return false;
		
		final int height = canvas.getHeight() * 2;
		final int width = canvas.getWidth() * 2;
		
		if(left > width) return false;
		if(top > height) return false;
			
		this.mMarker.setBounds(left, top, right, bottom);
		this.mMarker.draw(canvas);
		return true;
	}

	@Override
	public boolean onDrawFocused(Canvas canvas, final MapPoint mp) {
		return onDraw(canvas, mp);
	}
	
	/**
	 * wrapper methods - they pass through to the wrapped object.
	 */
	public int getOpacity() { return this.mMarker.getOpacity(); }
	public void setAlpha(int alpha) { this.mMarker.setAlpha(alpha); }
	
	public int getIntrinsicWidth() { 
		return this.mMarker.getIntrinsicWidth();
	}
	public int getIntrinsicHeight() { 
		return this.mMarker.getIntrinsicHeight(); 
	}
	
	public void setColorFilter(ColorFilter cf) { this.mMarker.setColorFilter(cf); }
	
	public Point getHotSpot() { return this.mHotSpot; }

	public void setBounds(int left, int top, int right, int bottom) {
		this.mMarker.setBounds(left, top, right, bottom);
	}
	
	public void draw(Canvas c, MapPoint mp) {
		// TODO Auto-generated method stub
		
	}


}
