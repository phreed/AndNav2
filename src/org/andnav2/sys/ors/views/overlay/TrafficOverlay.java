// Created by plusminus on 23:36:12 - 18.01.2009
package org.andnav2.sys.ors.views.overlay;

import java.util.List;

import org.andnav2.R;
import org.andnav2.osm.views.overlay.OSMMapViewSpacialIndexItemizedOverlayWithFocus;
import org.andnav2.sys.ors.adt.ts.TrafficOverlayManager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;


public class TrafficOverlay extends OSMMapViewSpacialIndexItemizedOverlayWithFocus<TrafficOverlayItem> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public TrafficOverlay(final Context ctx, final List<TrafficOverlayItem> pList, final OnItemTapListener<TrafficOverlayItem> onItemTapListener) {
		super(ctx,
				new TrafficOverlayManager(pList),
				ctx.getResources().getDrawable(R.drawable.warning_severe_overlayitem),
				new Point(16, 16),
				ctx.getResources().getDrawable(R.drawable.warning_overlayitem_focused),
				new Point(16, 16),
				Color.argb(255, 130, 172, 223),
				onItemTapListener);

		/* Force to draw the actual icon below the focusing one. */
		this.mDrawBaseIntemUnderFocusedItem = true;

		/* No padding is needed, because we will draw nothing to there. */
		super.mPaddingTitleLeft = 0;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
