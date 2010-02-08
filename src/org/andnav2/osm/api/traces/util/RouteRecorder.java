// Created by plusminus on 12:28:16 - 21.09.2008
package org.andnav2.osm.api.traces.util;

import java.util.ArrayList;

import org.andnav2.adt.AndNavLocation;
import org.andnav2.osm.adt.GPSGeoLocation;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.adt.util.TypeConverter;
import org.andnav2.osm.util.constants.OSMConstants;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class RouteRecorder implements OSMConstants, Parcelable {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected ArrayList<GeoPoint> mRecords = new ArrayList<GeoPoint>();

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<GeoPoint> getRecordedGeoPoints() {
		return this.mRecords;
	}

	public void newRecordedGeoPoints() {
		this.mRecords = new ArrayList<GeoPoint>();
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void add(final Location aLocation){
		this.mRecords.add(TypeConverter.locationToGPSGeoLocation(aLocation));
	}

	public void add(final GeoPoint aGeoPoint){
		this.mRecords.add(new GPSGeoLocation(
				aGeoPoint.getLatitudeE6(),
				aGeoPoint.getLongitudeE6(),
				System.currentTimeMillis()));
	}

	public void add(final AndNavLocation aLocation) {
		/* TODO Hier sind mehr infos m�glich! */
		this.mRecords.add(new GPSGeoLocation(
				aLocation.getLatitudeE6(),
				aLocation.getLongitudeE6(),
				System.currentTimeMillis(),
				((aLocation.hasNumberOfLandmarks()) ? aLocation.getNumberOfLandmarks() : NOT_SET)));
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// ===========================================================
	// Parcelable
	// ===========================================================

	public static final Parcelable.Creator<RouteRecorder> CREATOR = new Parcelable.Creator<RouteRecorder>() {
		public RouteRecorder createFromParcel(final Parcel in) {
			return readFromParcel(in);
		}

		public RouteRecorder[] newArray(final int size) {
			return new RouteRecorder[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(final Parcel out, final int arg1) {
		out.writeTypedList(this.mRecords);
	}

	private static RouteRecorder readFromParcel(final Parcel in){
		final RouteRecorder out = new RouteRecorder();

		out.mRecords = new ArrayList<GeoPoint>();
		in.readTypedList(out.mRecords, GeoPoint.CREATOR);

		return out;
	}
}
