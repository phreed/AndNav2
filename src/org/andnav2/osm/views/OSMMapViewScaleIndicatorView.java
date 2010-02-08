// Created by plusminus on 23:01:57 - 13.01.2009
package org.andnav2.osm.views;

import org.andnav2.R;
import org.andnav2.adt.UnitSystem;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class OSMMapViewScaleIndicatorView extends LinearLayout {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int SCALEBAR_WIDTH_2 = 50;

	// ===========================================================
	// Fields
	// ===========================================================

	private UnitSystem mUnitSystem = UnitSystem.METRIC;
	private final TextView mTVDistance;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewScaleIndicatorView(final Context ctx, final AttributeSet attrs) {
		super(ctx, attrs);

		this.setOrientation(LinearLayout.VERTICAL);

		this.mTVDistance = new TextView(ctx);
		this.mTVDistance.setGravity(Gravity.CENTER);
		this.mTVDistance.setText("500 m");
		this.mTVDistance.setTextColor(Color.BLACK);
		//		this.mTVDistance.setLayoutParams(params)

		final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		this.addView(this.mTVDistance, layoutParams);

		final ImageView ivScaleBar = new ImageView(ctx);
		ivScaleBar.setImageResource(R.drawable.scalebar);
		this.addView(ivScaleBar, layoutParams);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setUnitSystem(final UnitSystem aUnitSystem) {
		this.mUnitSystem = aUnitSystem;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void refresh(final OSMMapView osmMapView){
		final OSMMapViewProjection pj = osmMapView.getProjection();

		/* Determine two GeoPoints X pixels from ScreenCenter. */
		final Point mapCenterPoint = pj.toPixels(osmMapView.getMapCenter(), null);

		final GeoPoint gpA = pj.fromPixels(mapCenterPoint.x - SCALEBAR_WIDTH_2, mapCenterPoint.y);
		final GeoPoint gpB = pj.fromPixels(mapCenterPoint.x + SCALEBAR_WIDTH_2, mapCenterPoint.y);
		/* Calculate the distance between these points. */
		final int dist = gpA.distanceTo(gpB);

		final String[] parts = this.mUnitSystem.getDistanceString(dist, null);
		this.mTVDistance.setText(parts[UnitSystem.DISTSTRINGS_DIST_ID] + " " + parts[UnitSystem.DISTSTRINGS_UNIT_ID]);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
