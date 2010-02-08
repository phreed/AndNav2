// Created by plusminus on 14:30:07 - 08.02.2009
package org.andnav2.osm.views.tiles.renderer;

import org.andnav2.osm.views.tiles.OSMMapTileProviderInfo;

import android.content.Context;

/**
 * Class resolving a OSMMapTileProviderInfo to its renderer.
 * @author Nicolas Gramlich
 */
public class RendererRegistry {
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
	public static IOSMRenderer resolve(final Context ctx, final OSMMapTileProviderInfo pTileProviderInfo) throws IllegalArgumentException {
		//		switch(pTileProviderInfo){
		//			case MAPNIK_RENDERER:
		//				throw new IllegalArgumentException(); // TODO
		//			case TRIVIAL_RENDERER:
		//				return new TrivialRenderer(ctx);
		//		}

		throw new IllegalArgumentException();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
