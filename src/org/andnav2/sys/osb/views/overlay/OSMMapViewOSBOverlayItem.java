// Created by plusminus on 00:02:58 - 03.10.2008
package org.andnav2.sys.osb.views.overlay;

import org.andnav2.osm.views.overlay.OSMMapViewOverlayItem;
import org.andnav2.sys.osb.adt.OpenStreetBug;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Immutable class describing a GeoPoint with a Title and a Description.
 * @author Nicolas Gramlich
 *
 */
public class OSMMapViewOSBOverlayItem extends OSMMapViewOverlayItem implements Parcelable{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	/* The Bug this OverlayItem is based on. */
	protected final OpenStreetBug mBaseBug;
	protected final boolean mIsOpen;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewOSBOverlayItem(final OpenStreetBug b) {
		super((b.isOpen()) ? "Open Bug" : "Closed Bug", b.getDesription(), b); // TODO i18n
		this.mIsOpen = b.isOpen();
		this.mBaseBug = b;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean isOpenBug() {
		return this.mIsOpen;
	}

	public OpenStreetBug getBug(){
		return this.mBaseBug;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean equals(final Object o) {
		if(!(o instanceof OSMMapViewOverlayItem)) {
			return false;
		}

		final OSMMapViewOSBOverlayItem other = (OSMMapViewOSBOverlayItem)o;
		return this.mBaseBug.equals(other.mBaseBug);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// ===========================================================
	// Parcelable
	// ===========================================================

	public static final Parcelable.Creator<OSMMapViewOSBOverlayItem> CREATOR = new Parcelable.Creator<OSMMapViewOSBOverlayItem>(){
		public OSMMapViewOSBOverlayItem createFromParcel(final Parcel in) {
			return readFromParcel(in);
		}

		public OSMMapViewOSBOverlayItem[] newArray(final int size) {
			return new OSMMapViewOSBOverlayItem[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel out, final int arg1) {
		out.writeParcelable(this.mBaseBug, 0);
	}

	private static OSMMapViewOSBOverlayItem readFromParcel(final Parcel in){
		final OpenStreetBug osb = in.readParcelable(null);
		return new OSMMapViewOSBOverlayItem(osb);
	}
}
