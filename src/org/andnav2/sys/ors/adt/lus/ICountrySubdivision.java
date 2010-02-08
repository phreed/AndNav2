package org.andnav2.sys.ors.adt.lus;

import android.os.Parcelable;


/**
 * @author Nicolas Gramlich
 * @since 17:45:13 - 23.06.2009
 */
public interface ICountrySubdivision extends Parcelable {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public String getName();
	public String getAbbreviation();
	public int getFlagResID();
	public String uid();

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

