// Created by plusminus on 23:18:23 - 02.10.2008
package org.andnav2.osm.views.overlay;

import java.util.List;

import org.andnav2.osm.views.OSMMapView;
import org.andnav2.sys.ors.adt.ts.ISpatialDataOrganizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

/**
 * @author Nicolas Gramlich
 *
 * @param <T>
 */
public class OSMMapViewSpacialIndexItemizedOverlayWithFocus<T extends OSMMapViewOverlayItem> extends AbstractOSMMapViewItemizedOverlayWithFocus<T> {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int ITEM_LIMIT = 20;

	// ===========================================================
	// Fields
	// ===========================================================

	private ISpatialDataOrganizer<T> mSpatialDataOrganizer;
	private List<T> mClosestItems;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewSpacialIndexItemizedOverlayWithFocus(final Context ctx, final ISpatialDataOrganizer<T> aManager, final Drawable pMarker, final Point pMarkerHotspot, final Drawable pMarkerFocusedBase, final Point pMarkerFocusedHotSpot, final int pFocusedBackgroundColor, final OnItemTapListener<T> pOnItemTapListener) {
		super(ctx, pMarker, pMarkerHotspot, pMarkerFocusedBase, pMarkerFocusedHotSpot, pFocusedBackgroundColor, pOnItemTapListener);
		this.mSpatialDataOrganizer = aManager;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * @return may return null !
	 */
	@Override
	public List<T> getOverlayItems(){
		return this.mClosestItems;
	}

	@Override
	public void setOverlayItems(final List<T> pItems){
		this.mSpatialDataOrganizer.clearIndex();
		this.mSpatialDataOrganizer.addAll(pItems);
		this.mSpatialDataOrganizer.buildIndex();
	}

	public void setSpacialIndexManager(final ISpatialDataOrganizer<T> pManager) {
		this.mSpatialDataOrganizer = pManager;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onDraw(final Canvas c, final OSMMapView mapView) {
		if(this.mSpatialDataOrganizer.isIndexBuilt()){
			switch(this.mSpatialDataOrganizer.getGetMode()){
				case BOUNDINGBOX:
					this.mClosestItems = this.mSpatialDataOrganizer.getWithinBoundingBox(mapView.getVisibleBoundingBoxE6(), ITEM_LIMIT);
					break;
				case CLOSEST:
					this.mClosestItems = this.mSpatialDataOrganizer.getClosest(mapView.getMapCenter(), ITEM_LIMIT);
					break;
			}

			super.onDraw(c, mapView);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
