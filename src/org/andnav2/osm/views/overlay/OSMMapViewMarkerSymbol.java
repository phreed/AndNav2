/**
 * 
 */
package org.andnav2.osm.views.overlay;

import org.andnav2.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

/**
 * @author phreed
 *
 */
public class OSMMapViewMarkerSymbol
implements OSMMapViewMarker 
{

	/**
	 * @param marker
	 * @param hotspot
	 */
	private Context mCtx;
	
	public OSMMapViewMarkerSymbol(Context ctx) {
		super();
		mCtx = ctx;
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Point getHotSpot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIntrinsicHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIntrinsicWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean onDraw(Canvas canvas, Point location) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDrawFocused(Canvas canvas, Point location) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void relocate(Point location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		
	}

}
