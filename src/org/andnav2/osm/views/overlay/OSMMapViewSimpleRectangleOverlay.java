// Created by plusminus on 5:29:23 PM - Mar 4, 2009
package org.andnav2.osm.views.overlay;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.views.OSMMapView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;


public class OSMMapViewSimpleRectangleOverlay extends OSMMapViewOverlay {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private BoundingBoxE6 mBoundingBox;
	private Paint mPaint = new Paint();

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewSimpleRectangleOverlay() {
		this(null);
	}

	public OSMMapViewSimpleRectangleOverlay(final BoundingBoxE6 pBoundingBoxE6) {
		this.mBoundingBox = pBoundingBoxE6;
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
	 * Dashed, Red, Width=2;
	 */
	public void initDefaultPaint() {
		this.mPaint.setPathEffect(new DashPathEffect(new float[]{10,5}, 0));
		this.mPaint.setStyle(Style.STROKE);
		this.mPaint.setStrokeWidth(2);
		this.mPaint.setColor(Color.RED);
		this.mPaint.setAntiAlias(false);
	}

	public BoundingBoxE6 getBoundingBoxE6() {
		return this.mBoundingBox;
	}

	public void setBoundingBoxE6(final BoundingBoxE6 pBoundingBoxE6) {
		this.mBoundingBox = pBoundingBoxE6;
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
		if(this.mBoundingBox != null){
			final RectF bbox = osmv.getProjection().toPixels(this.mBoundingBox);

			c.drawRect(bbox, this.mPaint);
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
