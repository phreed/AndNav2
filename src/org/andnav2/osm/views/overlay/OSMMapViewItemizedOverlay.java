// Created by plusminus on 23:18:23 - 02.10.2008
package org.andnav2.osm.views.overlay;

import java.util.List;

import org.andnav2.R;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.OSMMapView;
import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

/**
 * Draws a list of {@link OSMMapViewOverlayItem} as markers to a map.
 * The item with the lowest index is drawn as last and therefore the 'topmost' marker. 
 * It also gets checked for onTap first.
 * This class is generic, because you then you get your custom item-class passed back in onTap().
 * @author Nicolas Gramlich
 *
 * @param <T>
 */
public abstract class OSMMapViewItemizedOverlay<T extends OSMMapViewOverlayItem> 
extends OSMMapViewOverlay
{
	// ===========================================================
	// Constants
	// ===========================================================

	private static final Point DEFAULTMARKER_HOTSPOT = new Point(13, 47);

	// ===========================================================
	// Fields
	// ===========================================================

	private OnItemTapListener<T> mOnItemTapListener;
	protected OSMMapViewMarker mMarker;
	private int mMarkerWidth, mMarkerHeight;

	private int mDrawnItemsLimit = Integer.MAX_VALUE;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewItemizedOverlay(
			final Context ctx, 
			final OnItemTapListener<T> aOnItemTapListener) 
	{
		this(ctx, null, aOnItemTapListener);
	}

	public OSMMapViewItemizedOverlay(
			final Context ctx, 
			final OSMMapViewMarker pMarker, 
			final OnItemTapListener<T> aOnItemTapListener) 
	{
		assert(ctx != null);
		this.mMarker = (pMarker != null) ? pMarker
				: new OSMMapViewMarkerSimple(
						ctx.getResources().getDrawable(R.drawable.marker_default),
				        DEFAULTMARKER_HOTSPOT);

		this.mOnItemTapListener = aOnItemTapListener;

		this.mMarkerWidth = this.mMarker.getIntrinsicWidth();
		this.mMarkerHeight = this.mMarker.getIntrinsicHeight();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * @return may return null !
	 */
	public abstract List<T> getOverlayItems();

	public abstract void setOverlayItems(final List<T> pItems);

	public int getDrawnItemsLimit() {
		return this.mDrawnItemsLimit;
	}

	public void setDrawnItemsLimit(final int aLimit) {
		this.mDrawnItemsLimit = aLimit;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void release() {
		this.mMarker = null;
	}

	@Override
	protected void onDrawFinished(final Canvas c, final OSMMapView osmv) {
		return;
	}

	@Override
	public void onDraw(final Canvas c, final OSMMapView mapView) {
		final List<T> overlayItems = this.getOverlayItems();
		if(overlayItems == null) return;
		if(overlayItems.size() < 1) return;
		
		final OSMMapViewProjection pj = mapView.getProjection();

		/* Drag to local field. */
		final int drawnItemsLimit = this.mDrawnItemsLimit;
		int itemsDrawn = 0;

		/* Draw in backward cycle, so the items with the least index are in the front. */
		for(int i = overlayItems.size() - 1; i >= 0; i--) {
			final T item = overlayItems.get(i);
			MapPoint mp = new MapPoint(item, pj);

			final boolean itemWasDrawn = onDrawItem(c, i, mp);

			if(itemWasDrawn) itemsDrawn++;

			if(itemsDrawn >= drawnItemsLimit) break;
		}
	}

	/**
	 * 
	 * @param c
	 * @param index
	 * @param pMarkerHotSpot
	 * @return <code>true</code> if the item was actually drawn. <code>false</code> it was not drawn because it was out of the visible area.
	 */
	protected boolean onDrawItem(final Canvas canvas, final int index, final MapPoint location) 
	{
		if(this.mMarker == null) return false;
		
		final int left = location.asPoint().x - this.mMarker.getHotSpot().x;
		final int right = left + this.mMarkerWidth;
		final int top = location.asPoint().y - this.mMarker.getHotSpot().y;
		final int bottom = top + this.mMarkerHeight;

		if(right < 0) return false;
		if(bottom < 0) return false;
		
		final int height = canvas.getHeight() * 2;
		final int width = canvas.getWidth() * 2;
		if(left > width) return false;
		if(top > height) return false;
		
		/* Draw item. */
		this.mMarker.setBounds(left, top, right, bottom);
		this.mMarker.onDraw(canvas, location);
		return true; /* Item was drawn. */
	}

	@Override
	public boolean onSingleTapUp(final MotionEvent event, final OSMMapView mapView) {
		final List<T> overlayItems = this.getOverlayItems();
		if(this.mMarker == null) return super.onSingleTapUp(event, mapView);
		if(overlayItems == null) return super.onSingleTapUp(event, mapView);
		if(overlayItems.size() < 1) return super.onSingleTapUp(event, mapView);
		
		final OSMMapViewProjection pj = mapView.getProjection();
		final int eventX = (int)event.getX();
		final int eventY = (int)event.getY();

		final int markerWidth = this.mMarker.getIntrinsicWidth();
		final int markerHeight = this.mMarker.getIntrinsicHeight();

		/* These objects are created to avoid construct new ones every cycle. */
		final Rect curMarkerBounds = new Rect();
		final Point mCurScreenCoords = new Point();

		for(int i = 0; i < overlayItems.size(); i++){
			final T mItem = overlayItems.get(i);
			pj.toPixels(mItem, mCurScreenCoords);

			final int left = mCurScreenCoords.x - this.mMarker.getHotSpot().x;
			final int right = left + markerWidth;
			final int top = mCurScreenCoords.y - this.mMarker.getHotSpot().y;
			final int bottom = top + markerHeight;

			curMarkerBounds.set(left, top, right, bottom);
			if(! curMarkerBounds.contains(eventX, eventY)) continue;
			if(!onTap(i)) continue;
			
			return true;
		}
		return super.onSingleTapUp(event, mapView);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected boolean onTap(final int pIndex) {
		final List<T> overlayItems = this.getOverlayItems();
		if(this.mOnItemTapListener == null) return false;
		if(overlayItems == null) return false;
		
		return this.mOnItemTapListener.onItemTap(pIndex, overlayItems.get(pIndex));
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	public static interface OnItemTapListener<T>{
		/**
		 * 
		 * @param aIndex
		 * @param aItem
		 * @return <code>true</code> if the OnTapListener handled the tap. <code>false</code> otherwise.
		 */
		public boolean onItemTap(final int aIndex, final T aItem);
	}
}
