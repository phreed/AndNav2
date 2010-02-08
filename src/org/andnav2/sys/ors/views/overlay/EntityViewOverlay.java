// Created by phreed on 02.02.2010
package org.andnav2.sys.ors.views.overlay;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.andnav2.osm.views.OSMMapView;
import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;
import org.andnav2.osm.views.overlay.OSMMapViewOverlay;
import org.andnav2.sys.ors.adt.me.MobileEntity;
import org.andnav2.ui.map.overlay.util.VisibilityFilter;

import android.graphics.Canvas;


public class EntityViewOverlay extends OSMMapViewOverlay {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final List<MobileEntity> mEntitySet;
	protected int mDrawnAreasLimit = Integer.MAX_VALUE;
	protected final VisibilityFilter mVisibityFilter = new VisibilityFilter();
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public EntityViewOverlay(){
		this(new ArrayList<MobileEntity>());
	}

	public EntityViewOverlay(final MobileEntity entity){
		this.mEntitySet = new ArrayList<MobileEntity>();
		this.mEntitySet.add(entity);
	}

	public EntityViewOverlay(final ArrayList<MobileEntity> entity_set){
		Assert.assertNotNull(entity_set);
		this.mEntitySet = entity_set;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public List<MobileEntity> getAreasOfInterest(){
		return this.mEntitySet;
	}

	public int getDrawnAreasLimit() {
		return this.mDrawnAreasLimit;
	}

	public void setDrawnAreasLimit(final int aLimit) {
		this.mDrawnAreasLimit = aLimit;
	}

	public void resetVisibilityFilter() {
		this.mVisibityFilter.clear();
	}
	
	public void setVisiblityFilter(final int index, final Integer pVisible){
		this.mVisibityFilter.set(index, null);
	}

	public boolean getVisiblityFilter(final int index){
		return this.mVisibityFilter.get(index);
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void release() {
		this.mEntitySet.clear();
	}

	@Override
	protected void onDraw(final Canvas canvas, final OSMMapView osmv) {
		final OSMMapViewProjection projection = osmv.getProjection();

		final int limit = Math.min(this.mDrawnAreasLimit, this.mEntitySet.size());
		for(int ix = 0; ix < limit; ++ix){
			final MobileEntity entity = this.mEntitySet.get(ix);
			entity.drawToCanvas(canvas, projection);
		}
	}

	@Override
	protected void onDrawFocused(final Canvas c, final OSMMapView osmv) {
		// Nothing
	}


	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
