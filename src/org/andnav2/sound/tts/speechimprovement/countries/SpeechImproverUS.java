package org.andnav2.sound.tts.speechimprovement.countries;

import org.andnav2.sound.tts.speechimprovement.AbstractSpeechImprover;


/**
 * @author Nicolas Gramlich
 * @since 09:13:48 - 24.06.2009
 */
public class SpeechImproverUS extends AbstractSpeechImprover {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String[][] SPEECH_IMPROVEMENTS = {{"Pl", "Place"},
		{"St", "Street"},
		{"Rd", "Road"},
		{"Dr", "Drive"},
		{"Ave", "Avenue"},
		{"Av", "Avenue"},
		{"Cir", "Circle"},
		{"Hwy", "Highway"},
		{"Pky", "Parkway"},
		{"Pkwy", "Parkway"},
		{"Blvd", "Boulevard"},
		{"Sq", "Square"},
		{"Tr", "Trail"}};

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
		return improve(aTurnDescription, SPEECH_IMPROVEMENTS);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

