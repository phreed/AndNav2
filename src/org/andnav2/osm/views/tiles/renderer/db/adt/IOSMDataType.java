// Created by plusminus on 22:35:21 - 11.02.2009
package org.andnav2.osm.views.tiles.renderer.db.adt;


public interface IOSMDataType {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public boolean hasOSMID();
	public long getOSMID();

	public boolean hasName();
	public String getName();
}
