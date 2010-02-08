// Created by plusminus on 21:42:38 - 16.10.2008
package org.andnav2.sys.ors.adt.aoi;

import java.util.Formatter;

import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;
import org.andnav2.sys.ors.util.constants.ORSXMLConstants;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcelable;


public abstract class AreaOfInterest implements ORSXMLConstants, Parcelable{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final Paint mPaint = new Paint();

	// ===========================================================
	// Constructors
	// ===========================================================

	public AreaOfInterest(){
		this.mPaint.setARGB(120,255,0,0); // LookThrough-RED
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Paint getPaint(){
		return this.mPaint;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Abstract Methods
	// ===========================================================

	/**
	 * @param sb
	 * @param f a Formatter bound to <code>sb</code>
	 */
	public abstract void appendToStringBuilder(final StringBuilder sb, final Formatter f);

	public abstract void drawToCanvas(final Canvas c, final OSMMapViewProjection pj);

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
