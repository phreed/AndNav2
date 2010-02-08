// Created by plusminus on 18:33:37 - 15.12.2008
package org.andnav2.sys.osb.api;

import java.util.ArrayList;
import java.util.Scanner;

import org.andnav2.sys.osb.adt.OpenStreetBug;
import org.andnav2.sys.osb.exc.OSBException;
import org.andnav2.util.HTMLUtil;


public class OSBAjaxResponseParser {
	// ===========================================================
	// Constants
	// ===========================================================

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
	 * <pre>putAJAXMarker(31824,-1.654381,48.116183,"manque une ruelle ici [Xav]",0);
	 * putAJAXMarker(31823,-1.654124,48.114908,"manque deux ruelles dans ce coin [Xav]",0);
	 * putAJAXMarker(15396,-1.664939,48.112645,"Dans le sens E-&gt;O, portion de la rue de Paris réservée au bus (de Rue Armand Barbes à de Rue de Châteaudun) [Xav]<hr />et autorisée aux vélos [Xav]",0);
	 * putAJAXMarker(8525,-1.662450,48.113433,"Lycée ??? [gwenn]<hr />&quot;Ensemble scolaire Saint-Vincent Providence&quot; [NoName]",0);
	 * putAJAXMarker(8008,-1.658083,48.120044,"Terrains de tennis [gwenn]",0);</pre>
	 */
	public static final ArrayList<OpenStreetBug> parseResponse(final String pResponse) throws OSBException {
		final ArrayList<OpenStreetBug> out = new ArrayList<OpenStreetBug>();
		final Scanner sc = new Scanner(pResponse);

		while(sc.hasNextLine()){
			final String line = sc.nextLine();
			if(!line.startsWith("putAJAXMarker(") || !line.endsWith(");")) {
				continue;
				//				throw new OSBException("Each line should start with \"putAJAXMarker(\" and end with \");\"");
			}

			/* Trim "putAJAXMarker(" and ");" */
			final String trimmedLine = line.substring("putAJAXMarker(".length(), line.length() - 2);

			/* String is now:
			 * "8525,-1.662450,48.113433,"Lycée ??? [gwenn]<hr />&quot;Ensemble scolaire Saint-Vincent Providence&quot; [NoName]""*/

			final String[] segments = trimmedLine.split(",", 4);
			/* Parts are:
			 * [0] = "8525"
			 * [1] = "-1.662450"
			 * [2] = "48.113433"
			 * [3] = ""Lycée ??? [gwenn]<hr />&quot;Ensemble scolaire Saint-Vincent Providence&quot; [NoName]",1"*/

			final int id = Integer.parseInt(segments[0]);
			final double lon = Double.parseDouble(segments[1]);
			final int lonE6 = (int)(lon * 1E6);
			final double lat =  Double.parseDouble(segments[2]);
			final int latE6 = (int)(lat * 1E6);


			/* Cut Open-Status from the end.*/
			final char openStatusChar = segments[3].charAt(segments[3].length() - 1);
			final boolean isOpen = openStatusChar == '0';

			/* Prepare comment: ""Lycée ??? [gwenn]<hr />&quot;Ensemble scolaire Saint-Vincent Providence&quot; [NoName]",1"*/
			String comment = segments[3].substring(1, segments[3].length() - 3);

			/* Comment is now: "Lycée ??? [gwenn]<hr />&quot;Ensemble scolaire Saint-Vincent Providence&quot; [NoName]"*/
			comment = comment.replace("<hr />", "\n");
			comment = HTMLUtil.htmlDecode(comment);

			out.add(new OpenStreetBug(latE6, lonE6, id, comment, isOpen));
		}

		return out;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
