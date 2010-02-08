// Created by plusminus on 17:36:07 - 15.12.2008
package org.andnav2.sys.osb.api;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.osb.adt.OpenStreetBug;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;


public class OSBRequestComposer {
	// ===========================================================
	// Constants
	// ===========================================================

	/**
	 * Formatter-Parameters: b,t,l,r (floats)
	 */
	private static final String BUG_BOUNDINGBOX_FULLGPX_BASEURL = "http://openstreetbugs.appspot.com/getGPX?b=%f&t=%f&l=%f&r=%f";
	private static final String BUG_BOUNDINGBOX_FULLJS_BASEURL = "http://openstreetbugs.appspot.com/getBugs?b=%f&t=%f&l=%f&r=%f";
	private static final String BUG_BY_ID_BASEURL = "http://openstreetbugs.appspot.com/getGPXitem?id=%d";

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
	 * 	-- To add a bug --
	 * <pre>
	 * 	Make a POST request on :
	 * 	   http://openstreetbugs.appspot.com/addPOIexec
	 * 	with :
	 * 	   lat (float)
	 * 	   lon (float)
	 * 	   text (string/utf-8)
	 * 
	 * 	You should get a response with :
	 * 	   ok\n
	 * 	   idOfTheNewBug
	 * </pre>
	 * @param pBugGeoPoint
	 * @param pBugDescription
	 * @return
	 */
	public static MultipartEntity createSubmitBugEntitiy(final GeoPoint pBugGeoPoint, final String pBugDescription){
		final MultipartEntity out = new MultipartEntity();
		try {
			out.addPart("lat", new StringBody(String.valueOf(pBugGeoPoint.getLatitudeAsDouble())));
			out.addPart("lon", new StringBody(String.valueOf(pBugGeoPoint.getLongitudeAsDouble())));
			out.addPart("text", new StringBody(pBugDescription, Charset.forName("UTF-8")));
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return out;
	}

	/**
	 * -- To get some bugs from one area --<br/>
	 *	Also, there is a limit of returned bugs. It's not that simple to
	 *	explain, but :
	 * <ul>
	 *	<li>with high zoom, you can have up to 80 non-closed bugs returned</li>
	 *	<li>the higher you zoom, the less bugs will be missing (of course)</li>
	 *	<li>the oldest bugs are missing first</li>
	 *</ul>
	 * <pre>
	 * FullGPX:
	 * http://openstreetbugs.appspot.com/getGPX?b=48.11939207282073&t=48.12552985106833&l=-1.661234664998126&r=-1.6495080471854842
	 * IDs only:
	 * http://openstreetbugs.appspot.com/getBugs?b=48.11939207282073&t=48.12552985106833&l=-1.661234664998126&r=-1.6495080471854842
	 *
	 * with :
	 *	- b as bottom limit
	 *	- t as top limit
	 *	- l as left limit
	 *	- r as right limit
	 * Some of the results are NOT in these limits but not far away (it is a trick to consume less resources).
	 * </pre>
	 * @param pBoundingBoxE6
	 * @param pAsGPX
	 * @return
	 */
	public static String createBugsFromBoundingBoxE6Entitiy(final BoundingBoxE6 pBoundingBoxE6, final boolean pAsGPX){
		final double b = pBoundingBoxE6.getLatSouthE6() / 1E6;
		final double t = pBoundingBoxE6.getLatNorthE6() / 1E6;
		final double l = pBoundingBoxE6.getLonWestE6() / 1E6;
		final double r = pBoundingBoxE6.getLonEastE6() / 1E6;
		return String.format(Locale.ENGLISH, (pAsGPX) ? BUG_BOUNDINGBOX_FULLGPX_BASEURL : BUG_BOUNDINGBOX_FULLJS_BASEURL,
				Math.max(-89.5, b),
				Math.min(89.5, t),
				Math.max(-180, l),
				Math.min(180, r));
	}

	/**
	 *	-- To close a bug --
	 *
	 * <pre>
	 *	Make a POST request on :
	 *	   http://openstreetbugs.appspot.com/closePOIexec
	 *	with :
	 *	   id (int)
	 *
	 *	You should get a response with :
	 *	   ok
	 * </pre>
	 */
	public static MultipartEntity createCloseBugEntitiy(final OpenStreetBug pBug){
		final MultipartEntity out = new MultipartEntity();
		try {
			out.addPart("id", new StringBody(String.valueOf(pBug.getID())));
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return out;
	}

	/**
	 *	-- To close a bug --
	 *
	 * <pre>
	 *	-- To append a text message to an existing bug --
	 *
	 *	Make a POST request on :
	 *	   http://openstreetbugs.appspot.com/editPOIexec
	 *	with :
	 *	   id (int)
	 *	   text (string/utf-8)
	 *
	 *	You should get a response with :
	 *	   ok
	 * </pre>
	 * @param pBug a Bug to append the description to.
	 * @param pDescriptionToAppend the description to append.
	 */
	public static MultipartEntity createAppendToBugEntitiy(final OpenStreetBug pBug, final String pDescriptionToAppend){
		final MultipartEntity out = new MultipartEntity();
		try {
			out.addPart("id", new StringBody(String.valueOf(pBug.getID())));
			out.addPart("text", new StringBody(pDescriptionToAppend, Charset.forName("UTF-8")));
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return out;
	}

	/**
	 * 
	 * @param pBugID
	 * @return <code>http://openstreetbugs.appspot.com/getGPXitem?id=1337</code>
	 */
	public String createBugFromIdEntity(final int pBugID){
		return String.format(Locale.ENGLISH, BUG_BY_ID_BASEURL, pBugID);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
