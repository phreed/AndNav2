// Created by plusminus on 00:52:21 - 12.11.2008
package org.andnav2.sys.ors.views.overlay;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.andnav2.osm.views.OSMMapView;
import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;
import org.andnav2.osm.views.overlay.OSMMapViewOverlay;
import org.andnav2.sys.ors.adt.aoi.AreaOfInterest;

import android.graphics.Canvas;


public class AreaOfInterestOverlay extends OSMMapViewOverlay{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final List<AreaOfInterest> mAOIs;
	protected int mDrawnAreasLimit = Integer.MAX_VALUE;

	// ===========================================================
	// Constructors
	// ===========================================================

	public AreaOfInterestOverlay(){
		this(new ArrayList<AreaOfInterest>());
	}

	public AreaOfInterestOverlay(final AreaOfInterest pAOI){
		this.mAOIs = new ArrayList<AreaOfInterest>();
		this.mAOIs.add(pAOI);
	}

	public AreaOfInterestOverlay(final ArrayList<AreaOfInterest> pAOIs){
		Assert.assertNotNull(pAOIs);
		this.mAOIs = pAOIs;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public List<AreaOfInterest> getAreasOfInterest(){
		return this.mAOIs;
	}

	public int getDrawnAreasLimit() {
		return this.mDrawnAreasLimit;
	}

	public void setDrawnAreasLimit(final int aLimit) {
		this.mDrawnAreasLimit = aLimit;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void release() {
		this.mAOIs.clear();
	}

	@Override
	protected void onDraw(final Canvas c, final OSMMapView osmv) {
		final OSMMapViewProjection pj = osmv.getProjection();

		final int limit = Math.min(this.mDrawnAreasLimit, this.mAOIs.size());
		for(int i = 0; i < limit; i++){
			final AreaOfInterest a = this.mAOIs.get(i);
			a.drawToCanvas(c, pj);
		}
	}

	@Override
	protected void onDrawFinished(final Canvas c, final OSMMapView osmv) {
		// Nothing
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
