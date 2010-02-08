// Created by plusminus on 00:59:43 - 28.01.2009
package org.andnav2.util;

import java.util.StringTokenizer;


public class Capitalizer {
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

	public static String capitalize(final String s) {
		if(s.length() == 0){
			return s;
		}else{
			final char[] chars = s.toCharArray();
			chars[0] = Character.toUpperCase(chars[0]);
			for (int i = 1; i < chars.length; i++) {
				chars[i] = Character.toLowerCase(chars[i]);
			}
			return new String(chars);
		}
	}


	public static String capitalizeAllWords(final String s) {
		if(s.length() == 0){
			return s;
		}else{
			final StringBuilder sb = new StringBuilder();

			final StringTokenizer st = new StringTokenizer(s);
			while(st.hasMoreTokens()){
				sb.append(capitalize(st.nextToken()));
				if(st.hasMoreTokens()) {
					sb.append(' ');
				}
			}

			return sb.toString();
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
