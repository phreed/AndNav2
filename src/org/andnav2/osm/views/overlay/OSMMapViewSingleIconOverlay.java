// Created by plusminus on 21:46:01 - 16.12.2008
package org.andnav2.osm.views.overlay;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.OSMMapView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;


public class OSMMapViewSingleIconOverlay extends OSMMapViewOverlay {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final Bitmap ICON;
	private final Paint myPaint = new Paint();
	private GeoPoint mLocation;
	private final Point mHotSpot;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewSingleIconOverlay(final Context ctx, final int mResID, final Point pHotSpot) {
		this.ICON = BitmapFactory.decodeResource(ctx.getResources(), mResID);
		this.mHotSpot = pHotSpot;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setLocation(final GeoPoint mp){
		this.mLocation = mp;
	}

	public GeoPoint getLocation(){
		return this.mLocation;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void release() {
		this.ICON.recycle();
	}


	/** This function does some fancy drawing, could be shortened a lot. */
	@Override
	public void onDraw(final Canvas canvas, final OSMMapView mapView) {
		if(this.mLocation != null){
			final Point p = new Point();
			mapView.getProjection().toPixels(this.mLocation, p);

			canvas.drawBitmap(this.ICON, p.x - this.mHotSpot.x, p.y - this.mHotSpot.y, this.myPaint);
		}
	}

	@Override
	protected void onDrawFinished(final Canvas c, final OSMMapView osmv) {
		// Nothing
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
