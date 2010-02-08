// Created by plusminus on 10:27:21 - 07.04.2008
package org.andnav2.sys.ors.adt.rs;

import java.util.ArrayList;
import java.util.List;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.util.Util;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * @since 2008-04-06 10:27:21
 * @author Nicolas 'plusminus' Gramlich
 * License:
 * @see Creative Commons Attribution-Noncommercial-Share Alike 2.0 Germany License .
 * Permissions beyond the scope of this license may be requested at plusminus {at} anddev {dot} org
 */
public class RouteInstruction implements Parcelable {

	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected boolean mIsWaypoint = false;

	protected int mDurationSeconds;
	protected int mLengthMeters;
	protected String mDescriptionHtml;

	protected List<GeoPoint> mPartialPolyLine;

	protected int mFirstMotherPolylineIndex;

	protected float mAngle;

	protected BoundingBoxE6 mBoundingBoxE6;

	// ===========================================================
	// Constructors
	// ===========================================================

	public RouteInstruction(){
		this.mPartialPolyLine = new ArrayList<GeoPoint>();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public List<GeoPoint> getPartialPolyLine() {
		return this.mPartialPolyLine;
	}

	/**
	 * @return the BoundingBoxE6 of this RouteInstruction.
	 */
	public BoundingBoxE6 getBoundingBoxE6() {
		if(this.mBoundingBoxE6 == null) {
			this.mBoundingBoxE6 = BoundingBoxE6.fromGeoPoints(this.mPartialPolyLine);
		}
		return this.mBoundingBoxE6;
	}

	/**
	 * @return the BoundingBoxE6 of this RouteInstruction.
	 */
	public GeoPoint getTurnPoint() {
		return this.mPartialPolyLine.get(0);
	}

	/**
	 * @return the mDurationSeconds
	 */
	public int getDurationSeconds() {
		return this.mDurationSeconds;
	}

	/**
	 * @return the mLengthMeters
	 */
	public int getLengthMeters() {
		return this.mLengthMeters;
	}

	/**
	 * @return the mDescriptionHtml
	 */
	public String getDescriptionHtml() {
		return this.mDescriptionHtml;
	}

	/**
	 * @return the mDescriptionHtml
	 */
	public String getDescription() {
		return Util.removeHtmlTags(this.mDescriptionHtml);
	}

	/**
	 * @return the mPolylineIndex
	 */
	public int getFirstMotherPolylineIndex() {
		return this.mFirstMotherPolylineIndex;
	}

	public int getLastMotherPolylineIndex() {
		return this.mFirstMotherPolylineIndex + this.mPartialPolyLine.size();
	}

	/**
	 * @return the mAngle
	 */
	public float getAngle() {
		return this.mAngle;
	}

	public int getEstimatedRestSeconds(final int indexInRoute, final int distanceToNextTurnPoint) {
		return (int)(this.mDurationSeconds * (distanceToNextTurnPoint / ((float)this.mLengthMeters)));
	}

	public boolean contains(final int testIndex) {
		return (testIndex >= this.mFirstMotherPolylineIndex && testIndex <= (this.mFirstMotherPolylineIndex + this.mPartialPolyLine.size() - 1));
	}

	public void setDurationSeconds(final int durationSeconds) {
		this.mDurationSeconds = durationSeconds;
	}

	public void setLengthMeters(final int lengthMeters) {
		this.mLengthMeters = lengthMeters;
	}

	public void setDescriptionHtml(final String descriptionHtml) {
		this.mDescriptionHtml = descriptionHtml;
	}


	public void setFirstMotherPolylineIndex(final int firstMotherPolylineIndex) {
		this.mFirstMotherPolylineIndex = firstMotherPolylineIndex;
	}


	public void setAngle(final float angle) {
		this.mAngle = angle;
	}


	public void setBoundingBoxE6(final BoundingBoxE6 boundingBoxE6) {
		this.mBoundingBoxE6 = boundingBoxE6;
	}

	public void setIsWaypoint(final boolean b) {
		this.mIsWaypoint = b;
	}

	public boolean isWaypoint(){
		return this.mIsWaypoint;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean equals(final Object o) {
		if(o != null && o instanceof RouteInstruction){
			final RouteInstruction r = (RouteInstruction)o;
			return r.mLengthMeters == this.mLengthMeters && r.mFirstMotherPolylineIndex == this.mFirstMotherPolylineIndex;
		}else{
			return false;
		}
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

	public static final Parcelable.Creator<RouteInstruction> CREATOR = new Parcelable.Creator<RouteInstruction>() {
		public RouteInstruction createFromParcel(final Parcel in) {
			return readFromParcel(in);
		}

		public RouteInstruction[] newArray(final int size) {
			return new RouteInstruction[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(final Parcel out, final int flags) {

		out.writeInt((this.mIsWaypoint) ? 1 : 0);
		out.writeInt(this.mDurationSeconds);
		out.writeInt(this.mLengthMeters);
		out.writeString(this.mDescriptionHtml);

		out.writeTypedList(this.mPartialPolyLine);

		out.writeInt(this.mFirstMotherPolylineIndex);
		out.writeFloat(this.mAngle);
		out.writeParcelable(this.mBoundingBoxE6, 0);
	}

	private static RouteInstruction readFromParcel(final Parcel in){
		final RouteInstruction out = new RouteInstruction();

		out.mIsWaypoint = (in.readInt() == 1);

		out.mDurationSeconds = in.readInt();
		out.mLengthMeters = in.readInt();
		out.mDescriptionHtml = in.readString();

		out.mPartialPolyLine = new ArrayList<GeoPoint>();
		in.readTypedList(out.mPartialPolyLine, GeoPoint.CREATOR);

		out.mFirstMotherPolylineIndex = in.readInt();

		out.mAngle = in.readFloat();

		out.mBoundingBoxE6 = in.readParcelable(null);
		return out;
	}
}
