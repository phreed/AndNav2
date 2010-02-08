package org.andnav2.sound.tts.speechimprovement.countries;

import org.andnav2.sound.tts.speechimprovement.AbstractSpeechImprover;


/**
 * @author Nicolas Gramlich
 * @since 09:13:48 - 24.06.2009
 */
public class SpeechImproverDE extends AbstractSpeechImprover {
	// ===========================================================
	// Constants
	// ===========================================================

	//	private static final String[][] SPEECH_IMPROVEMENTS = {{"traﬂe", "trasse"}};

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

	public static String improve(final String aTurnDescription) {
		return aTurnDescription.replace("ﬂ", "hs");
		//		return improve(aTurnDescription, SPEECH_IMPROVEMENTS);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

