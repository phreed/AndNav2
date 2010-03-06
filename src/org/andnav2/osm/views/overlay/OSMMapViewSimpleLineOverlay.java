// Created by plusminus on 5:29:23 PM - Mar 4, 2009
package org.andnav2.osm.views.overlay;

import org.andnav2.osm.adt.GeoLine;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.OSMMapView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;


public class OSMMapViewSimpleLineOverlay extends OSMMapViewOverlay {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private GeoPoint mGeoPointFrom;
	private GeoPoint mGeoPointTo;
	private Paint mPaint = new Paint();

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewSimpleLineOverlay() {
		this(null, null);
	}


	public OSMMapViewSimpleLineOverlay(final GeoLine pGeoLine) {
		this(pGeoLine.getGeoPointA(), pGeoLine.getGeoPointB());
	}

	public OSMMapViewSimpleLineOverlay(final GeoPoint pGeoPointFrom, final GeoPoint pGeoPointTo) {
		this.mGeoPointFrom = pGeoPointFrom;
		this.mGeoPointTo = pGeoPointTo;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Paint getPaint() {
		return this.mPaint;
	}

	public void setPaint(final Paint pPaint) {
		this.mPaint = pPaint;
	}

	/**
	 * Dashed, Red, Width=5;
	 */
	public void setPaintDashed() {
		this.mPaint.setPathEffect(new DashPathEffect(new float[]{10,5}, 0));
		this.mPaint.setStrokeWidth(5);
		this.mPaint.setColor(Color.RED);
		this.mPaint.setAntiAlias(false);
	}

	/**
	 * Dashed, Red, Width=5;
	 */
	public void setPaintNormal() {
		this.mPaint.setStrokeWidth(5);
		this.mPaint.setColor(Color.RED);
		this.mPaint.setAntiAlias(true);
	}

	public GeoPoint getFrom() {
		return this.mGeoPointFrom;
	}

	public void setFrom(final GeoPoint pGeoPointFrom) {
		this.mGeoPointFrom = pGeoPointFrom;
	}

	public GeoPoint getTo() {
		return this.mGeoPointTo;
	}

	public void setTo(final GeoPoint pGeoPointTo) {
		this.mGeoPointTo = pGeoPointTo;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void release() {
		/* Nothing. */
	}

	@Override
	protected void onDraw(final Canvas c, final OSMMapView osmv) {
		if(this.mGeoPointFrom != null && this.mGeoPointTo != null){
			final Point from = osmv.getProjection().toPixels(this.mGeoPointFrom, null);
			final Point to = osmv.getProjection().toPixels(this.mGeoPointTo, null);

			c.drawLine(from.x, from.y, to.x, to.y, this.mPaint);
		}
	}

	@Override
	protected void onDrawFinished(final Canvas c, final OSMMapView osmv) {
		/* Nothing. */
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
