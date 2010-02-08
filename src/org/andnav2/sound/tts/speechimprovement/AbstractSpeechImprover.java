package org.andnav2.sound.tts.speechimprovement;


/**
 * @author Nicolas Gramlich
 * @since 09:14:07 - 24.06.2009
 */
public abstract class AbstractSpeechImprover {
	// ===========================================================
	// Final Fields
	// ===========================================================

	private static final int KEY_ID = 0;
	private static final int VALUE_ID = 1;

	// ===========================================================
	// Methods
	// ===========================================================

	protected static String improve(final String aTurnDescription, final String[][] improvements) {
		/* Do lowercase comparison for better matching. */
		final String lowerCaseTurnDescription = aTurnDescription.toLowerCase();

		for(final String[] pair : improvements) {
			if(lowerCaseTurnDescription.endsWith(pair[KEY_ID].toLowerCase())) {
				return aTurnDescription.subSequence(0, aTurnDescription.length() - pair[KEY_ID].length()) + pair[VALUE_ID];
			}
		}

		return aTurnDescription;
	}
}

