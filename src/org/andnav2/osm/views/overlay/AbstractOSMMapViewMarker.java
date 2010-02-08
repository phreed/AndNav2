/**
 * 
 */
package org.andnav2.osm.views.overlay;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * I'm creating a drawable AbstractOSMMarker.java class to hold the drawable and its hotspot. 
 * I'm thinking that the drawable object should know how to draw himself focused and not. 
 * The default is a bitmap centered at its upper left corner.
 * 
 * Mostly this is a wrapper for Drawable objects.
 * 
 * @author phreed
 *
 */
public class AbstractOSMMapViewMarker {

	final private Drawable mMarker;
	final private Point mHotspot;
	final private int mWidth;
	final private int mHeight;
	
	private Point mLocation;
	/**
	 * 
	 */
	public AbstractOSMMapViewMarker(final Drawable marker, final Point hotspot) {
		this.mMarker = marker;
		this.mHotspot = hotspot;
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
	public boolean onDraw(final Canvas canvas, final Point location) {
		if (location == null && this.mLocation == null) return false;
		Point locate = (location == null) ? this.mLocation : location;
		
		final int left = locate.x - this.mHotspot.x;
		final int top = locate.y - this.mHotspot.y;
		
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

	public boolean onDrawFocused(Canvas canvas, final Point location) {
		return onDraw(canvas, location);
	}

	/**
	 * wrapper methods - they pass through to the wrapped object.
	 */
	public int getOpacity() { return this.mMarker.getOpacity(); }
	public void setAlpha(int alpha) { this.mMarker.setAlpha(alpha); }
	public void setColorFilter(ColorFilter cf) { this.mMarker.setColorFilter(cf); }

}
