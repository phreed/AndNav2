// Created by plusminus on 8:15:02 PM - Apr 4, 2009
package org.andnav2.adt.voice;


public class SimpleAudibleTurnCommand {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	/** The distance of the next turn transformed into a voice-sound. */
	protected final DistanceVoiceElement mDistanceVoiceElement;
	/** The angle of the next turn transformed into a voice-sound. */
	protected final TurnVoiceElement mTurnVoiceElement;

	// ===========================================================
	// Constructors
	// ===========================================================

	public SimpleAudibleTurnCommand(final DistanceVoiceElement voiceDistance, final TurnVoiceElement voiceTurn) {
		this.mDistanceVoiceElement = voiceDistance;
		this.mTurnVoiceElement = voiceTurn;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public DistanceVoiceElement getDistanceVoiceElement() {
		return this.mDistanceVoiceElement;
	}

	public TurnVoiceElement getTurnVoiceElement() {
		return this.mTurnVoiceElement;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
