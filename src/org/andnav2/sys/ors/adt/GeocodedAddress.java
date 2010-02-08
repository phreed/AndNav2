// Created by plusminus on 01:20:48 - 02.11.2008
package org.andnav2.sys.ors.adt;

import org.andnav2.R;
import org.andnav2.adt.UnitSystem;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.lus.Country;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;


public class GeocodedAddress extends GeoPoint implements Parcelable {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected Country mNationality;
	protected String mStreetNameOfficial;
	protected String mCountrySubdivision;
	protected String mMunicipality;
	protected String mStreetNumber;
	protected String mPostalCode;
	protected float mAccuracy;

	// ===========================================================
	// Constructors
	// ===========================================================

	public GeocodedAddress(final int latitudeE6, final int longitudeE6) {
		super(latitudeE6, longitudeE6);
	}

	public GeocodedAddress(final GeoPoint gp) {
		super(gp.getLatitudeE6(), gp.getLongitudeE6());
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getMunicipality() {
		return this.mMunicipality;
	}

	public void setMunicipality(final String municipality) {
		this.mMunicipality = municipality;
	}

	public Country getNationality() {
		return this.mNationality;
	}

	public void setNationality(final Country nationality) {
		this.mNationality = nationality;
	}

	public String getStreetNameOfficial() {
		return this.mStreetNameOfficial;
	}

	public void setStreetNameOfficial(final String streetNameOfficial) {
		this.mStreetNameOfficial = streetNameOfficial;
	}

	public String getCountrySubdivision() {
		return this.mCountrySubdivision;
	}

	public void setCountrySubdivision(final String countrySubdivision) {
		this.mCountrySubdivision = countrySubdivision;
	}

	public String getStreetNumber() {
		return this.mStreetNumber;
	}

	public void setStreetNumber(final String streetNumber) {
		this.mStreetNumber = streetNumber;
	}

	public String getPostalCode() {
		return this.mPostalCode;
	}

	public void setPostalCode(final String postalCode) {
		this.mPostalCode = postalCode;
	}

	public float getAccuracy() {
		return this.mAccuracy;
	}

	public void setAccuracy(final float accuracy) {
		this.mAccuracy = accuracy;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================
	public String toString(final Context ctx) {
		return toString(ctx, null, false);
	}

	public String toString(final Context ctx, final UnitSystem aUs) {
		return toString(ctx, aUs, true);
	}

	/**
	 * @param ctx Needed to get i18n Strings like "Streetnumber".
	 */
	public String toString(final Context ctx, final UnitSystem aUs, final boolean addDistanceOrAccuracy) {
		final StringBuilder sb = new StringBuilder();

		if(this.mStreetNumber != null && this.mStreetNumber.length() > 0) {
			sb.append(ctx.getString(R.string.wherami_dialog_streetnumber)).append(' ').append(this.mStreetNumber).append('\n');
		}

		if(this.mStreetNameOfficial != null && this.mStreetNameOfficial.length() > 0) {
			sb.append(ctx.getString(R.string.wherami_dialog_streetname)).append(' ').append(this.mStreetNameOfficial).append('\n');
		}

		if(this.mMunicipality != null && this.mMunicipality.length() > 0) {
			sb.append(ctx.getString(R.string.wherami_dialog_cityname)).append(' ').append(this.mMunicipality).append('\n');
		}

		if(this.mPostalCode != null && this.mPostalCode.length() > 0) {
			sb.append(ctx.getString(R.string.wherami_dialog_zipcode)).append(' ').append(this.mPostalCode).append('\n');
		}

		if(this.mCountrySubdivision != null && this.mCountrySubdivision.length() > 0) {
			sb.append(ctx.getString(R.string.wherami_dialog_adminarea)).append(' ').append(this.mCountrySubdivision).append('\n');
		}

		if(this.mNationality != null) {
			sb.append(ctx.getString(R.string.wherami_dialog_country)).append(' ').append(ctx.getString(this.mNationality.NAMERESID)).append('\n');
		}

		if(aUs != null && addDistanceOrAccuracy){
			final String[] distStringParts = aUs.getDistanceString((int)this.mAccuracy, null);
			final String formattedDistance = distStringParts[UnitSystem.DISTSTRINGS_DIST_ID] + distStringParts[UnitSystem.DISTSTRINGS_UNIT_ID];

			sb.append(ctx.getString(R.string.wherami_dialog_distance)).append(' ').append(formattedDistance).append('\n');
		}

		try{
			final double lat = this.mLatitudeE6 / 1E6;
			final double lon = this.mLongitudeE6 / 1E6;
			sb.append('\n');
			sb.append(ctx.getString(R.string.latitude)).append(": ").append(lat).append('\n');
			sb.append(ctx.getString(R.string.longitude)).append(": ").append(lon);
		}catch(final IllegalStateException ise){ }

		if(sb.charAt(sb.length() - 1) == '\n') {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// ===========================================================
	// Parcelable
	// ===========================================================

	public static final Parcelable.Creator<GeocodedAddress> CREATOR = new Parcelable.Creator<GeocodedAddress>() {
		public GeocodedAddress createFromParcel(final Parcel in) {
			return readFromParcel(in);
		}

		public GeocodedAddress[] newArray(final int size) {
			return new GeocodedAddress[size];
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

		if(this.mNationality == null) {
			out.writeInt(NOT_SET);
		} else {
			out.writeInt(this.mNationality.ordinal());
		}

		out.writeString(this.mStreetNameOfficial);
		out.writeString(this.mCountrySubdivision);
		out.writeString(this.mMunicipality);
		out.writeString(this.mStreetNumber);
		out.writeString(this.mPostalCode);
		out.writeFloat(this.mAccuracy);
	}

	private static GeocodedAddress readFromParcel(final Parcel in){
		final int latE6 = in.readInt();
		final int lonE6 = in.readInt();

		final GeocodedAddress out = new GeocodedAddress(latE6, lonE6);

		final int natOrdinal = in.readInt();
		final Country nat = (natOrdinal == NOT_SET) ? null : Country.values()[natOrdinal];
		out.setNationality(nat);

		out.setStreetNameOfficial(in.readString());
		out.setCountrySubdivision(in.readString());
		out.setMunicipality(in.readString());
		out.setStreetNumber(in.readString());
		out.setPostalCode(in.readString());
		out.setAccuracy(in.readFloat());

		return out;
	}
}