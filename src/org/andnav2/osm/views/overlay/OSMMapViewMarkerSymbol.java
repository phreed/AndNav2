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
 * @author phreed
 *
 */
enum STD_IDENTITY { FRIEND, HOSTILE, UNKNOWN, NEUTRAL };

public class OSMMapViewMarkerSymbol
implements OSMMapViewMarker 
{

	/**
	 * @param marker
	 * @param hotspot
	 */
	private Context mCtx;
	private Drawable mMarker;
	private Point mHotSpot;
	private int mWidth, mHeight;
	
	public OSMMapViewMarkerSymbol(Context ctx) {
		super();
		mCtx = ctx;
		
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
	public boolean onDraw(Canvas canvas, MapPoint location) {
		mCtx.getResources().getDrawable(R.drawable.marker_default);
		return false;
	}

	@Override
	public boolean onDrawFocused(Canvas canvas, MapPoint location) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void draw(Canvas canvas, MapPoint mp) {
		
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
	
	private STD_IDENTITY mSi;
	
	public void set_std_identity(STD_IDENTITY si) { 
		mSi = si; 
		_determine_marker();
	}

	private void _determine_marker() {
		switch (mSi) {
		case FRIEND:
			this.mMarker = mCtx.getResources().getDrawable(R.drawable.mil_sfgpuci_20);
			this.mWidth = this.mMarker.getIntrinsicWidth();
			this.mHeight = this.mMarker.getIntrinsicHeight();
			this.mHotSpot = new Point(3,5);
			break;
		case HOSTILE:
			this.mMarker = mCtx.getResources().getDrawable(R.drawable.mil_shgpuci_20);
			this.mWidth = this.mMarker.getIntrinsicWidth();
			this.mHeight = this.mMarker.getIntrinsicHeight();
			this.mHotSpot = new Point(3,5);
			break;
		case UNKNOWN:
			this.mMarker = mCtx.getResources().getDrawable(R.drawable.mil_sugpuci_20);
			this.mWidth = this.mMarker.getIntrinsicWidth();
			this.mHeight = this.mMarker.getIntrinsicHeight();
			this.mHotSpot = new Point(3,5);
			break;
		case NEUTRAL:
			this.mMarker = mCtx.getResources().getDrawable(R.drawable.mil_sngpuci_20);
			this.mWidth = this.mMarker.getIntrinsicWidth();
			this.mHeight = this.mMarker.getIntrinsicHeight();
			this.mHotSpot = new Point(3,5);
			break;
		}
	}

}
