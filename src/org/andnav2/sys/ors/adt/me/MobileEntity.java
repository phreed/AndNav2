package org.andnav2.sys.ors.adt.me;

import java.util.Formatter;

import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;
import org.andnav2.sys.ors.util.constants.ORSXMLConstants;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

public abstract class MobileEntity implements ORSXMLConstants, Parcelable {

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
	}
	
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

	public MobileEntity(){
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
