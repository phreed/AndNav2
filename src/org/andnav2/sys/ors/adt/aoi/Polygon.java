// Created by plusminus on 21:42:53 - 16.10.2008
package org.andnav2.sys.ors.adt.aoi;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import junit.framework.Assert;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;
import org.andnav2.ui.map.overlay.util.ManagedLinePath;

import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;


public class Polygon extends AreaOfInterest {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected List<GeoPoint> mExterior;
	protected List<List<GeoPoint>> mInterior;
	protected BoundingBoxE6 mBoundingBoxE6;

	// ===========================================================
	// Constructors
	// ===========================================================

	public Polygon() {

	}

	public Polygon(final ArrayList<GeoPoint> pExterior) {
		this(pExterior, null, null);
	}

	public Polygon(final ArrayList<GeoPoint> pExterior, final BoundingBoxE6 pBoundingBoxE6) {
		this(pExterior, pBoundingBoxE6, null);
	}

	public Polygon(final List<GeoPoint> pExterior, final BoundingBoxE6 pBoundingBoxE6, final List<List<GeoPoint>> pInterior) {
		Assert.assertNotNull(pExterior);
		if(pExterior.size() < 3) {
			throw new IllegalArgumentException("A Polygon is defined by at least 3 Points!");
		}

		this.mExterior = pExterior;
		this.mInterior = pInterior;
		this.mBoundingBoxE6 = pBoundingBoxE6;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public List<GeoPoint> getExterior() {
		return this.mExterior;
	}

	public List<List<GeoPoint>> getInterior() {
		return this.mInterior;
	}

	public BoundingBoxE6 getBoundingBoxE6() {
		if(this.mBoundingBoxE6 == null) {
			; // TODO Calculate BoundingBox
		}

		return this.mBoundingBoxE6;
	}

	public void setExterior(final List<GeoPoint> exterior) {
		this.mExterior = exterior;
	}

	public void setInterior(final List<List<GeoPoint>> interior) {
		this.mInterior = interior;
	}

	public void addInterior(final List<GeoPoint> interior) {
		if(this.mInterior == null) {
			this.mInterior = new ArrayList<List<GeoPoint>>();
		}

		this.mInterior.add(interior);
	}

	public void setBoundingBoxE6(final BoundingBoxE6 boundingBoxE6) {
		this.mBoundingBoxE6 = boundingBoxE6;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	/**
	 * <pre> &lt;xls:AOI&gt;
	 *   &lt;gml:Polygon&gt;
	 *     &lt;gml:exterior&gt;
	 *       &lt;gml:LinearRing&gt;
	 *         &lt;gml:pos&gt;45.256 -110.45&lt;/gml:pos&gt;
	 *         &lt;gml:pos&gt;46.46 -109.48&lt;/gml:pos&gt;
	 *         &lt;gml:pos&gt;43.84 -109.86&lt;/gml:pos&gt;
	 *         &lt;gml:pos&gt;45.256 -110.45&lt;/gml:pos&gt;
	 *       &lt;/gml:LinearRing&gt;
	 *     &lt;/gml:exterior&gt;
	 *   &lt;/gml:Polygon&gt;
	 * &lt;/xls:AOI&gt;</pre>
	 */
	@Override
	public void appendToStringBuilder(final StringBuilder sb, final Formatter f) {
		sb.append(XLS_AREAOFINTEREST_TAG_OPEN)
		.append(GML_POLYGON_TAG_OPEN)
		.append(GML_EXTERIOR_TAG_OPEN)
		.append(GML_LINEARRING_TAG_OPEN);

		for (final GeoPoint via : this.mExterior) {
			f.format(GML_POS_TAG, via.getLongitudeE6() / 1E6, via.getLatitudeE6() / 1E6);
		}

		sb.append(GML_LINEARRING_TAG_CLOSE)
		.append(GML_EXTERIOR_TAG_CLOSE)
		.append(GML_POLYGON_TAG_CLOSE)
		.append(XLS_AREAOFINTEREST_TAG_CLOSE);
	}

	@Override
	public void drawToCanvas(final Canvas c, final OSMMapViewProjection pj) {
		final Point reuse = new Point();
		final ManagedLinePath p = new ManagedLinePath();

		for (final GeoPoint gp : this.mExterior) {
			p.lineTo(pj.toPixels(gp, reuse));
		}

		c.drawPath(p, this.mPaint);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// ===========================================================
	// Parcelable
	// ===========================================================

	public static final Parcelable.Creator<Polygon> CREATOR = new Parcelable.Creator<Polygon>() {
		public Polygon createFromParcel(final Parcel in) {
			return readFromParcel(in);
		}

		public Polygon[] newArray(final int size) {
			return new Polygon[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(final Parcel out, final int arg1) {
		out.writeTypedList(this.mExterior);

		out.writeInt(this.mInterior.size());
		for(final List<GeoPoint> interior : this.mInterior) {
			out.writeTypedList(interior);
		}

		out.writeParcelable(this.mBoundingBoxE6, 0);
	}

	private static Polygon readFromParcel(final Parcel in){
		final ArrayList<GeoPoint> exterior = new ArrayList<GeoPoint>();
		in.readTypedList(exterior, GeoPoint.CREATOR);

		final int interiorCount = in.readInt();
		final ArrayList<List<GeoPoint>> interiors = new ArrayList<List<GeoPoint>>();
		for(int i = 0; i < interiorCount; i++){
			final ArrayList<GeoPoint> interior = new ArrayList<GeoPoint>();
			in.readTypedList(interior, GeoPoint.CREATOR);
			interiors.add(interior);
		}
		final BoundingBoxE6 bbE6 = in.readParcelable(null);
		return new Polygon(exterior, bbE6, interiors);
	}
}
