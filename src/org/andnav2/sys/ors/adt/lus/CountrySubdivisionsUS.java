package org.andnav2.sys.ors.adt.lus;

import org.andnav2.R;
import org.andnav2.util.Capitalizer;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * @author Nicolas Gramlich
 * @since 17:46:20 - 23.06.2009
 */
public enum CountrySubdivisionsUS implements ICountrySubdivision {
	// ===========================================================
	// Elements
	// ===========================================================
	ALABAMA("Alabama", "AL", R.drawable.flag_usa_state_of_alabama),
	ALASKA("Alaska", "AK", R.drawable.flag_usa_state_of_alaska),
	//	AMERICAN_SAMOA("American Samoa", "AS", R.drawable.flag_iconsoon),
	ARIZONA("Arizona", "AZ", R.drawable.flag_usa_state_of_arizona),
	ARKANSAS("Arkansas", "AR", R.drawable.flag_usa_state_of_arkansas),
	CALIFORNIA("California", "CA", R.drawable.flag_usa_state_of_california),
	COLORADO("Colorado", "CO", R.drawable.flag_usa_state_of_colorado),
	CONNECTICUT("Conneticut", "CT", R.drawable.flag_usa_state_of_connecticut),
	DELAWARE("Delaware", "DE", R.drawable.flag_usa_state_of_delaware),
	//	FEDERATED_STATES_OF_MICRONESIA("Federated States of Micronesia", "FM", R.drawable.flag_iconsoon),
	FLORIDA("Florida", "FL", R.drawable.flag_usa_state_of_florida),
	GEORGIA("Georgia", "GA", R.drawable.flag_usa_state_of_georgia),
	//	GUAM("Guam", "GU", R.drawable.flag_iconsoon),
	HAWAII("Hawaii", "HI", R.drawable.flag_usa_state_of_hawaii),
	IDAHO("Idaho", "ID", R.drawable.flag_usa_state_of_idaho),
	ILLINOIS("Illinois", "IL", R.drawable.flag_usa_state_of_illinois),
	INDIANA("Indiana", "IN", R.drawable.flag_usa_state_of_indiana),
	IOWA("Iowa", "IA", R.drawable.flag_usa_state_of_iowa),
	KANSAS("Kansas", "KS", R.drawable.flag_usa_state_of_kansas),
	KENTUCKY("Kentucky", "KY", R.drawable.flag_usa_state_of_kentucky),
	LOUISIANA("Louisiana", "LA", R.drawable.flag_usa_state_of_louisiana),
	MAINE("Maine", "ME", R.drawable.flag_usa_state_of_maine),
	//	MARSHALL_ISLANDS("Marshall Islands", "MH", R.drawable.flag_iconsoon),
	MARYLAND("Maryland", "MD", R.drawable.flag_usa_state_of_maryland),
	MASSACHUSETTS("Massachusetts", "MA", R.drawable.flag_usa_state_of_massachusetts),
	MICHIGAN("Michigan", "MI", R.drawable.flag_usa_state_of_michigan),
	MINNESOTA("Minnesota", "MN", R.drawable.flag_usa_state_of_minnesota),
	MISSISSIPPI("Mississippi", "MS", R.drawable.flag_usa_state_of_mississippi),
	MISSOURI("Missouri", "MO", R.drawable.flag_usa_state_of_missouri),
	MONTANA("Montana", "MT", R.drawable.flag_usa_state_of_montana),
	NEBRASKA("Nebraska", "NE", R.drawable.flag_usa_state_of_nebraska),
	NEVADA("Nevada", "NV", R.drawable.flag_usa_state_of_nevada),
	NEW_HAMPSHIRE("New Hampshire", "NH", R.drawable.flag_usa_state_of_new_hampshire),
	NEW_JERSEY("New Jersey", "NJ", R.drawable.flag_usa_state_of_new_jersey),
	NEW_MEXICO("New Mexico", "NM", R.drawable.flag_usa_state_of_new_mexico),
	NEW_YORK("New York", "NY", R.drawable.flag_usa_state_of_new_york),
	NORTH_CAROLINA("North Carolina", "NC", R.drawable.flag_usa_state_of_north_carolina),
	NORTH_DAKOTA("North Dakota", "ND", R.drawable.flag_usa_state_of_north_dakota),
	//	NORTHERN_MARIANA_ISLANDS("Northern Mariana Islands", "MP", R.drawable.flag_iconsoon),
	OHIO("Ohio", "OH", R.drawable.flag_usa_state_of_ohio),
	OKLAHOMA("Oklahoma", "OK", R.drawable.flag_usa_state_of_oklahoma),
	OREGON("Oregon", "OR", R.drawable.flag_usa_state_of_oregon),
	//	PALAU("Palau", "PW", R.drawable.flag_iconsoon),
	PENNSYLVANIA("Pennsylvania", "PA", R.drawable.flag_usa_state_of_pennsylvania),
	//	PUERTO_RICO("Puerto Rico", "PR", R.drawable.flag_iconsoon),
	RHODE_ISLAND("Rhode Island", "RI", R.drawable.flag_usa_state_of_rhode_island),
	SOUTH_CAROLINA("South Carolina", "SC", R.drawable.flag_usa_state_of_south_carolina),
	SOUTH_DAKOTA("South Dakota", "SD", R.drawable.flag_usa_state_of_south_dakota),
	TENNESSEE("Tennessee", "TN", R.drawable.flag_usa_state_of_tennessee),
	TEXAS("Texas", "TX", R.drawable.flag_usa_state_of_texas),
	UTAH("Utah", "UT", R.drawable.flag_usa_state_of_utah),
	VERMONT("Vermont", "VT", R.drawable.flag_usa_state_of_vermont),
	//	VIRGIN_ISLANDS("Virgin Islands", "VI", R.drawable.flag_iconsoon),
	VIRGINIA("Virginia", "VA", R.drawable.flag_usa_state_of_virginia),
	WASHINGTON("Washington", "WA", R.drawable.flag_usa_state_of_washington),
	WASHINGTON_DC("Washington D.C.", "DC", R.drawable.flag_usa_state_of_washington_dc),
	WEST_VIRGINIA("West Virginia", "WV", R.drawable.flag_usa_state_of_west_virginia),
	WISCONSIN("Wisconsin", "WI", R.drawable.flag_usa_state_of_wisconsin),
	WYOMING("Wyoming", "WY", R.drawable.flag_usa_state_of_wyoming);

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final String mName;
	private final String mAbbreviation;
	private final int mFlagResID;

	// ===========================================================
	// Constructors
	// ===========================================================

	private CountrySubdivisionsUS(final String pName, final String pAbbreviation, final int pFlagResID) {
		this.mName = Capitalizer.capitalizeAllWords(pName); // TODO Maybe use 'name()' instead of pName. (Saves some bytes)
		this.mAbbreviation = pAbbreviation;
		this.mFlagResID = pFlagResID;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getAbbreviation() {
		return this.mAbbreviation;
	}

	public String getName() {
		return this.mName;
	}

	public int getFlagResID() {
		return this.mFlagResID;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public String uid() {
		return this.name();
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

	public static final Parcelable.Creator<CountrySubdivisionsUS> CREATOR = new Parcelable.Creator<CountrySubdivisionsUS>() {
		public CountrySubdivisionsUS createFromParcel(final Parcel in) {
			return readFromParcel(in);
		}

		public CountrySubdivisionsUS[] newArray(final int size) {
			return new CountrySubdivisionsUS[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(final Parcel out, final int flags) {
		out.writeInt(this.ordinal());
	}

	private static CountrySubdivisionsUS readFromParcel(final Parcel in){
		return values()[in.readInt()];
	}
}

