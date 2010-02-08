// Created by plusminus on 20:55:02 - 12.01.2009
/**
 * 
 */
package org.andnav2.sys.postcode.uk_bs_7666;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostcodeUK_BS7776Matcher {
	/** Note: this pattern is <b>CaSe-SeNsItIvE</b>! */
	protected static final String PATTERNSTRING_UK_POSTCODE_BS_7666 = "(GIR 0AA|[A-PR-UWYZ]([0-9]{1,2}|([A-HK-Y][0-9]|[A-HK-Y][0-9]([0-9]|[ABEHMNPRV-Y]))|[0-9][A-HJKS-UW]) [0-9][ABD-HJLNP-UW-Z]{2})";
	/** Note: this pattern is <b>CaSe-SeNsItIvE</b>! */
	protected static final Pattern PATTERN_UKPOSTCODE_BS_7666 = Pattern.compile(PATTERNSTRING_UK_POSTCODE_BS_7666);

	/**
	 * Checks whether the <code>aPostcode</code> passed is a valid <b>BS 7666</b>-UK-Postcode.
	 * @param aPostcode <b>CaSe-SeNsItIvE</b>!
	 * @return
	 * @throws NoMatchFoundException
	 */
	public static boolean doesMatchUKPostcode_BS_7666(final String aPostcode){
		final Matcher matcher = PATTERN_UKPOSTCODE_BS_7666.matcher(aPostcode);
		return matcher.find();
	}

	/**
	 * Extracts a matched <b>BS 7666</b>-UK-Postcode out of a String.
	 * @param aPostcode <b>CaSe-SeNsItIvE</b>!
	 * @return
	 * @throws NoMatchFoundException whether the <code>aPostcode</code> passed is <b>not</b> a valid <b>BS 7666</b>-UK-Postcode.
	 */
	public static String getMatchUKPostcode_BS_7666(final String aPostcode) throws IllegalArgumentException{
		final Matcher matcher = PATTERN_UKPOSTCODE_BS_7666.matcher(aPostcode);
		final boolean matchFound = matcher.find();

		if(matchFound) {
			return matcher.group();
		} else {
			throw new IllegalArgumentException();
		}
	}
}