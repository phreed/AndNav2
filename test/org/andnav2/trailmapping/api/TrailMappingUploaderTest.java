// Created by plusminus on 11:32:03 PM - Apr 16, 2009
package org.andnav2.trailmapping.api;

import java.util.ArrayList;
import java.util.List;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.trailmapping.api.TrailMappingUploader;
import org.junit.Test;



public class TrailMappingUploaderTest {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	
	@Test
	public void testUpload() throws Exception {
		final List<GeoPoint> points = new ArrayList<GeoPoint>();
		points.add(new GeoPoint(50700730, 7137700));
		points.add(new GeoPoint(50710630, 7127140));
		
		TrailMappingUploader.upload(points);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
