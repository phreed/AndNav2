// Created by plusminus on 01:36:24 - 12.01.2009
package org.andnav2.sys.ftpc.api;

import java.util.Locale;

import org.andnav2.osm.adt.GeoPoint;


public class FTPCRequestComposer {
	// ===========================================================
	// Constants
	// ===========================================================

	/**
	 * email (String)
	 * lat (float)
	 * lon (float)
	 * postcode1 (String)
	 * postcode2 (String)
	 */
	private static final String SUBMIT_BASEURL = "http://www.freethepostcode.org/submit?email=%s&lat=%f&lon=%f&postcode1=%s&postcode2=%s";

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

	/**
	 * 
	 * 	-- To add a postcode --
	 * <pre>
	 * 	Make a REST request on :
	 * 	   http://www.freethepostcode.org/submit?email...
	 * 	with :
	 *     email (string/utf-8) i.e. "name@provider.com"
	 * 	   lat (float) i.e. 123.12345
	 * 	   lon (float) i.e. -123.4567
	 * 	   postcode1 (string/utf-8) i.e. "SW1A"
	 *     postcode2 (string/utf-8) i.e. "0AA"
	 * 
	 * Sample:
	 *    http://www.freethepostcode.org/submit?email=stoepsel5%40gmx.de&lat=51.52028&lon=-0.148659&postcode1=SW1A&postcode2=0AY
	 * 
	 * 	You should get a response with :
	 * 	   ???
	 * </pre>
	 * @param pGeoPoint
	 * @param pPostCode1String
	 * @param pPostCode2String
	 * @param pEMailAddress
	 * @return
	 */
	public static String createSubmitPostCodeEntitiy(final GeoPoint pGeoPoint, final String pPostCode1String, final String pPostCode2String, final String pEMailAddress){
		return String.format(Locale.ENGLISH, SUBMIT_BASEURL,
				pEMailAddress,
				pGeoPoint.getLatitudeAsDouble(),
				pGeoPoint.getLongitudeAsDouble(),
				pPostCode1String,
				pPostCode2String);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
