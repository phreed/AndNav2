// Created by plusminus on 17:10:58 - 17.12.2008
package org.andnav2.osm.views.overlay;

import org.andnav2.osm.views.OSMMapView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;


public class OSMMapViewCrosshairOverlay extends OSMMapViewOverlay {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final Paint mPaint;
	private final int mCenterCircleRadius;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewCrosshairOverlay(){
		this(Color.BLACK, 1, 0);
	}

	public OSMMapViewCrosshairOverlay(final int aCrosshairColor, final int aCrosshairWidth, final int aCenterCircleRadius) {
		this(aCrosshairColor, aCrosshairWidth, aCenterCircleRadius, Style.STROKE, true);
	}

	public OSMMapViewCrosshairOverlay(final int aCrosshairColor, final int aCrosshairWidth, final int aCenterCircleRadius, final Style aCenterCircleStyle, final boolean aAntialiasing) {
		this.mPaint = new Paint();
		this.mPaint.setStyle(Style.STROKE);
		this.mPaint.setAntiAlias(aAntialiasing);
		this.mPaint.setColor(aCrosshairColor);
		this.mPaint.setStrokeWidth(aCrosshairWidth);
		this.mCenterCircleRadius = aCenterCircleRadius;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setCrosshairColor(final int aColor){
		this.mPaint.setColor(aColor);
	}

	public void setCrosshairWidth(final int aWidth){
		this.mPaint.setColor(aWidth);
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void release() {
		// Nothing to release
	}

	@Override
	protected void onDraw(final Canvas c, final OSMMapView osmv) {
		final int height = osmv.getHeight();
		final int width = osmv.getWidth();

		final int height_2 = height >> 1;
		final int width_2 = width >> 1;

		c.drawCircle(width_2, height_2, this.mCenterCircleRadius, this.mPaint);

		/* Draw line from left to the centercircle. */
		c.drawLine(0, height_2, width_2 - this.mCenterCircleRadius, height_2, this.mPaint);

		/* Draw line from right to the centercircle. */
		c.drawLine(width_2 + this.mCenterCircleRadius, height_2, width, height_2, this.mPaint);

		/* Draw line from top to the centercircle. */
		c.drawLine(width_2, 0, width_2, height_2 - this.mCenterCircleRadius, this.mPaint);

		/* Draw line from bottom to the centercircle. */
		c.drawLine(width_2, height_2 + this.mCenterCircleRadius, width_2, height, this.mPaint);
	}

	@Override
	protected void onDrawFinished(final Canvas c, final OSMMapView osmv) {
		// Nothing to draw.
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
