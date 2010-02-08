// Created by plusminus on 20:12:07 - 11.09.2008
package org.andnav2.ui.map.overlay.util;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.adt.util.TypeConverter;
import org.andnav2.osm.views.overlay.OSMMapViewOverlayItem;

import android.location.Address;

public class AddressOverlayItem extends OSMMapViewOverlayItem {


	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final Address mAddress;

	// ===========================================================
	// Constructors
	// ===========================================================

	private AddressOverlayItem(final String title, final String snippet, final GeoPoint point, final Address pAddress) {
		super(title, snippet, point);
		this.mAddress = pAddress;
	}

	public static AddressOverlayItem create(final Address pAddress) throws IllegalArgumentException{
		if(pAddress.hasLatitude() && pAddress.hasLongitude()){
			return new AddressOverlayItem(pAddress.getLocality(), pAddress.getCountryName(), TypeConverter.addressToGeoPoint(pAddress), pAddress);
		}else{
			throw new IllegalArgumentException("Address has no Lat/Lng!");
		}
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
