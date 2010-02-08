package org.andnav2.sys.osb.adt;

import org.andnav2.osm.adt.GeoPoint;

import android.os.Parcel;
import android.os.Parcelable;

public class OpenStreetBug extends GeoPoint implements Parcelable {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final String mDesription;
	protected final int mID;
	protected final boolean mIsOpen;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OpenStreetBug(final GeoPoint aGeoPoint, final int aID, final String aDesription, final boolean pIsOpen) {
		this(aGeoPoint.getLatitudeE6(), aGeoPoint.getLongitudeE6(), aID, aDesription, pIsOpen);
	}

	public OpenStreetBug(final int aLatitudeE6, final int aLongitudeE6, final int aID, final String aDesription, final boolean pIsOpen) {
		super(aLatitudeE6, aLongitudeE6);
		this.mDesription = aDesription;
		this.mID = aID;
		this.mIsOpen = pIsOpen;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getID() {
		return this.mID;
	}

	public boolean isOpen(){
		return this.mIsOpen;
	}

	/**
	 * Multiple Comments within the description can be split by "\n".
	 * @return the Description of this bug.
	 */
	public String getDesription() {
		return this.mDesription;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean equals(final Object o) {
		if(!(o instanceof OpenStreetBug)) {
			return false;
		}

		final OpenStreetBug other = (OpenStreetBug)o;
		return this.mID == other.mID;
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

	public static final Parcelable.Creator<OpenStreetBug> CREATOR = new Parcelable.Creator<OpenStreetBug>() {
		public OpenStreetBug createFromParcel(final Parcel in) {
			return readFromParcel(in);
		}

		public OpenStreetBug[] newArray(final int size) {
			return new OpenStreetBug[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel out, final int arg1) {
		out.writeInt(this.mLatitudeE6);
		out.writeInt(this.mLongitudeE6);
		out.writeInt(this.mID);
		out.writeString(this.mDesription);
		out.writeInt((this.mIsOpen) ? 1 : 0);
	}

	private static OpenStreetBug readFromParcel(final Parcel in){
		final int latE6 = in.readInt();
		final int lonE6 = in.readInt();
		final int id = in.readInt();
		final String description = in.readString();
		final boolean open = in.readInt() == 1;
		return new OpenStreetBug(latE6, lonE6, id, description, open);
	}
}
