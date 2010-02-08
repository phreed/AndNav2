// Created by plusminus on 00:02:58 - 03.10.2008
package org.andnav2.osm.views.overlay;

import org.andnav2.R;
import org.andnav2.osm.adt.GeoPoint;

import android.content.Context;


/**
 * Immutable class describing a GeoPoint with a Title and a Description.
 * @author Nicolas Gramlich
 *
 */
public class OSMMapViewOverlayItem extends GeoPoint{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public final String mTitle;
	public final String mDescription;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewOverlayItem(final Context ctx, final GeoPoint pGeoPoint) {
		this(ctx.getString(R.string.coordinates), pGeoPoint.toMultiLineUserString(ctx), pGeoPoint.getLatitudeE6(), pGeoPoint.getLongitudeE6());
	}

	public OSMMapViewOverlayItem(final String aTitle, final String aDescription, final GeoPoint pGeoPoint) {
		this(aTitle, aDescription, pGeoPoint.getLatitudeE6(), pGeoPoint.getLongitudeE6());
	}


	/**
	 * @param aTitle this should be <b>singleLine</b> (no <code>'\n'</code> )
	 * @param aDescription a <b>multiLine</b> description ( <code>'\n'</code> possible)
	 * @param aGeoPoint
	 */
	public OSMMapViewOverlayItem(final String aTitle, final String aDescription, final int pLatitudeE6, final int pLongitudeE6) {
		super(pLatitudeE6, pLongitudeE6);
		this.mTitle = aTitle;
		this.mDescription = aDescription;
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
