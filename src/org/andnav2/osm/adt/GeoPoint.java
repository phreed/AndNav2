// Created by plusminus on 21:28:12 - 25.09.2008
package org.andnav2.osm.adt;

import java.util.Formatter;
import java.util.Locale;

import org.andnav2.R;
import org.andnav2.osm.api.util.constants.OSMTraceAPIConstants;
import org.andnav2.osm.util.constants.GMLXMLConstants;
import org.andnav2.osm.util.constants.MathConstants;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.util.constants.GeoConstants;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class GeoPoint implements IGeoPoint, Parcelable, MathConstants, GeoConstants, OSMConstants, GMLXMLConstants, OSMTraceAPIConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final int mLongitudeE6;
	protected final int mLatitudeE6;

	// ===========================================================
	// Constructors
	// ===========================================================

	public GeoPoint(final int aLatitudeE6, final int aLongitudeE6) {
		this.mLatitudeE6 = aLatitudeE6;
		this.mLongitudeE6 = aLongitudeE6;
	}

	public GeoPoint(final GeoPoint pGeoPoint) {
		this(pGeoPoint.mLatitudeE6, pGeoPoint.mLongitudeE6);
	}

	public static GeoPoint fromCenterBetween(final GeoPoint geoPointA, final GeoPoint geoPointB) {
		return new GeoPoint((geoPointA.getLatitudeE6() + geoPointB.getLatitudeE6()) / 2,
				(geoPointA.getLongitudeE6() + geoPointB.getLongitudeE6()) / 2);
	}

	public GeoPoint(final Parcel in) {
		this.mLatitudeE6 = in.readInt();
		this.mLongitudeE6 = in.readInt();
	}

	/**
	 * First token is the Latitude, second the Longitude.
	 * @param s
	 * @param spacer
	 * @return
	 */
	public static GeoPoint fromDoubleString(final String s, final char spacer) {
		final int spacerPos = s.indexOf(spacer);
		return new GeoPoint((int) (Double.parseDouble(s.substring(0,
				spacerPos)) * 1E6), (int) (Double.parseDouble(s.substring(
						spacerPos + 1, s.length())) * 1E6));
	}

	/**
	 * First token is the Longitude, second the Latitude.
	 * @param s
	 * @param spacer
	 * @return
	 */
	public static GeoPoint fromInvertedDoubleString(final String s, final char spacer) {
		final int spacerPos = s.indexOf(spacer);
		return new GeoPoint((int) (Double.parseDouble(s.substring(
				spacerPos + 1, s.length())) * 1E6),
				(int) (Double.parseDouble(s.substring(0,
						spacerPos)) * 1E6));
	}

	/**
	 * Uses ',' as its separator.
	 * @param s
	 * @return
	 */
	public static GeoPoint fromIntString(final String s){
		final int commaPos = s.indexOf(',');
		return new GeoPoint(Integer.parseInt(s.substring(0,commaPos)),
				Integer.parseInt(s.substring(commaPos+1)));
	}

	public GeoPoint cloneDeep() {
		return new GeoPoint(this.mLatitudeE6, this.mLongitudeE6);
	}

	public static GeoPoint getGeoPointBetween(final GeoPoint a, final GeoPoint b) {
		return new GeoPoint((a.mLatitudeE6 + b.mLatitudeE6) / 2, (a.mLongitudeE6 + b.mLongitudeE6) / 2);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getLongitudeE6() {
		return this.mLongitudeE6;
	}

	public int getLatitudeE6() {
		return this.mLatitudeE6;
	}

	public double getLongitudeAsDouble() {
		return this.mLongitudeE6 / 1E6;
	}

	public double getLatitudeAsDouble() {
		return this.mLatitudeE6 / 1E6;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public String toString(){
		return new StringBuilder().append(this.mLatitudeE6).append(",").append(this.mLongitudeE6).toString();
	}

	public String toMultiLineUserString(final Context ctx){
		return new StringBuilder()
		.append(ctx.getString(R.string.latitude))
		.append(": ")
		.append(this.getLatitudeAsDouble())
		.append('\n')
		.append(ctx.getString(R.string.longitude))
		.append(": ")
		.append(this.getLongitudeAsDouble()).toString();
	}

	/**
	 * Latitude first, then Longitude.
	 * @return
	 */
	public String toDoubleString() {
		return new StringBuilder().append(this.mLatitudeE6 / 1E6).append(",").append(this.mLongitudeE6  / 1E6).toString();
	}

	/**
	 * Longitude first, then Latitude.
	 * @return
	 */
	public String toInvertedDoubleString() {
		return new StringBuilder().append(this.mLongitudeE6 / 1E6).append(",").append(this.mLatitudeE6 / 1E6).toString();
	}

	/**
	 * <pre>&lt;gml:pos&gt;8.69311 49.41473&lt;/gml:pos&gt;</pre>
	 * @return
	 */
	public String toGMLPos(){
		return String.format(Locale.ENGLISH, GML_POS_TAG, this.mLongitudeE6 / 1E6, this.mLatitudeE6 / 1E6);
	}

	public void formatTo(final Formatter f) {
		f.format(GML_POS_TAG, this.mLongitudeE6 / 1E6, this.mLatitudeE6 / 1E6);
	}

	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof GeoPoint)) {
			return false;
		}
		final GeoPoint g = (GeoPoint)obj;
		return g.mLatitudeE6 == this.mLatitudeE6 && g.mLongitudeE6 == this.mLongitudeE6;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void appendToGpxString(final StringBuilder sb, final Formatter f){
		f.format(GPX_TAG_TRACK_SEGMENT_POINT, getLatitudeAsDouble(), getLongitudeAsDouble());
		sb.append(GPX_TAG_TRACK_SEGMENT_POINT_CLOSE);
	}

	/**
	 * @see Source@ http://www.geocities.com/DrChengalva/GPSDistance.html
	 * @param gpA
	 * @param gpB
	 * @return distance in meters
	 */
	public int distanceTo(final GeoPoint other) {
		if(other == null) {
			return Integer.MAX_VALUE;
		}

		final double a1 = DEG2RAD * (this.mLatitudeE6 / 1E6);
		final double a2 = DEG2RAD * (this.mLongitudeE6 / 1E6);
		final double b1 = DEG2RAD * (other.mLatitudeE6 / 1E6);
		final double b2 = DEG2RAD * (other.mLongitudeE6 / 1E6);

		final double cosa1 = Math.cos(a1);
		final double cosb1 = Math.cos(b1);

		final double t1 = cosa1*Math.cos(a2)*cosb1*Math.cos(b2);

		final double t2 = cosa1*Math.sin(a2)*cosb1*Math.sin(b2);

		final double t3 = Math.sin(a1)*Math.sin(b1);

		final double tt = Math.acos( t1 + t2 + t3 );

		return (int)(RADIUS_EARTH_METERS*tt);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// ===========================================================
	// Parcelable
	// ===========================================================

	public static final Parcelable.Creator<GeoPoint> CREATOR = new Parcelable.Creator<GeoPoint>() {
		public GeoPoint createFromParcel(final Parcel in) {
			return readFromParcel(in);
		}

		public GeoPoint[] newArray(final int size) {
			return new GeoPoint[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(final Parcel out, final int arg1) {
		out.writeInt(this.mLatitudeE6);
		out.writeInt(this.mLongitudeE6);
	}

	private static GeoPoint readFromParcel(final Parcel in){
		return new GeoPoint(in);
	}
}
