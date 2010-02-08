// Created by plusminus on 20:10:36 - 14.11.2008
package org.andnav2.osm.views.tiles.renderer;

import org.andnav2.osm.views.tiles.adt.OSMTileInfo;

import android.graphics.Bitmap;

/**
 * Interface used by all implemented Renderers.
 * @author Nicolas Gramlich
 */
public interface IOSMRenderer {

	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public Bitmap renderTile(final OSMTileInfo pTileInfo);

	public void release();
}
