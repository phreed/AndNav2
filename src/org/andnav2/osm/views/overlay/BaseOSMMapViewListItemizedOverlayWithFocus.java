// Created by plusminus on 7:28:43 PM - Mar 27, 2009
package org.andnav2.osm.views.overlay;

import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class BaseOSMMapViewListItemizedOverlayWithFocus<T extends OSMMapViewOverlayItem> extends AbstractOSMMapViewItemizedOverlayWithFocus<T> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private List<T> mItems;

	// ===========================================================
	// Constructors
	// ===========================================================

	public BaseOSMMapViewListItemizedOverlayWithFocus(final Context ctx, final List<T> pList, final OnItemTapListener<T> onItemTapListener) {
		super(ctx, onItemTapListener);
		this.mItems = pList;
	}

	public BaseOSMMapViewListItemizedOverlayWithFocus(final Context ctx, final List<T> pList, final Drawable pMarker, final Point pMarkerHotspot, final Drawable pMarkerFocusedBase, final Point pMarkerFocusedHotSpot, final int pFocusedBackgroundColor, final OnItemTapListener<T> pOnItemTapListener) {
		super(ctx, pMarker, pMarkerHotspot, pMarkerFocusedBase, pMarkerFocusedHotSpot, pFocusedBackgroundColor, pOnItemTapListener);
		this.mItems = pList;
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public List<T> getOverlayItems() {
		return this.mItems;
	}

	@Override
	public void setOverlayItems(final List<T> pItems) {
		this.mItems = pItems;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}