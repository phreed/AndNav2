/**
 * 
 */
package org.andnav2.osm.views.overlay;

import org.andnav2.R;
import org.andnav2.osm.adt.GeoPoint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

/**
 * This is the object which knows how to draw items of a particular type namely symbols.
 * 
 * @author phreed
 *
 */

public class OSMMapViewMarkerObstacle
implements OSMMapViewMarker 
{
	private Context mCtx;
	
	public OSMMapViewMarkerObstacle(Context ctx) {
		super();
		mCtx = ctx;
	}
	
	@Override
	public Point getHotSpot() {
		return new Point(0,0);
	}

	@Override
	public int getIntrinsicHeight() {
		return 20;
	}

	@Override
	public int getIntrinsicWidth() {
		return 20;
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public boolean onDraw(Canvas canvas, MapPoint location) {
		if (location == null) return false;
		
		GeoPoint gp = location.asGeoPoint();
		if (!(gp instanceof OSMMapViewOverlayObstacle)) return false;
		OSMMapViewOverlayObstacle gpt = (OSMMapViewOverlayObstacle)gp;
		
		Drawable item;
		int iwidth;
		int iheight;
		Point hot_spot;
	
		switch (gpt.get_type()) {
		case M_OAR:
			item = mCtx.getResources().getDrawable(R.drawable.mil_g_m_oar);
			iwidth = item.getIntrinsicWidth();
			iheight = item.getIntrinsicHeight();
			hot_spot = new Point(100,100);
			break;
		case M_OFD:
			item = mCtx.getResources().getDrawable(R.drawable.mil_g_m_ofd);
			iwidth = item.getIntrinsicWidth();
			iheight = item.getIntrinsicHeight();
			hot_spot = new Point(200,100);
			break;
		case M_OGL:
			item = mCtx.getResources().getDrawable(R.drawable.mil_g_m_ogl);
			iwidth = item.getIntrinsicWidth();
			iheight = item.getIntrinsicHeight();
			hot_spot = new Point(100,100);
			break;
		default: return false;
		}
		
		Point locate = location.asPoint();
		
		final int left = locate.x - hot_spot.x;
		final int top = locate.y - hot_spot.y;
		
		final int right = left + iwidth;
		final int bottom = top + iheight;

		if(right < 0) return false;
		if(bottom < 0) return false;
		
		final int height = canvas.getHeight() * 2;
		final int width = canvas.getWidth() * 2;
		
		if(left > width) return false;
		if(top > height) return false;
			
		item.setBounds(left, top, right, bottom);
		item.draw(canvas);
		return false;
	}

	@Override
	public boolean onDrawFocused(Canvas canvas, MapPoint location) {
		return false;
	}

	@Override
	public void relocate(Point location) {
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
	}

}
