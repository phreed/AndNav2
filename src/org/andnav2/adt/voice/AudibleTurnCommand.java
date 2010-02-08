// Created by plusminus on 20:43:25 - 25.04.2008
package org.andnav2.adt.voice;


public class AudibleTurnCommand extends
SimpleAudibleTurnCommand {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	/** Full textual description of the next turn. */
	private final String mFullTurnText;
	/** The angle of the next turn. */
	private final int mTurnAngle;
	/** The distance up to the next turn in METERS. */
	private final int mTurnDistanceMeters;

	private SimpleAudibleTurnCommand mThenCommand;

	// ===========================================================
	// Constructors
	// ===========================================================

	public AudibleTurnCommand(final DistanceVoiceElement voiceDistance, final TurnVoiceElement voiceTurn, final String aFullTurnText, final int aTurnAngle, final int aTurnDistance) {
		super(voiceDistance, voiceTurn);
		this.mFullTurnText = aFullTurnText;
		this.mTurnAngle = aTurnAngle;
		this.mTurnDistanceMeters = aTurnDistance;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getTurnDistanceMeters() {
		return this.mTurnDistanceMeters;
	}

	public String getFullTurnText() {
		return this.mFullTurnText;
	}

	public int getTurnAngle() {
		return this.mTurnAngle;
	}

	public boolean hasThenCommand() {
		return this.mThenCommand != null;
	}

	public SimpleAudibleTurnCommand getThenCommand() {
		return this.mThenCommand;
	}

	public void setThenCommand(final SimpleAudibleTurnCommand pThenCommand) {
		this.mThenCommand = pThenCommand;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean equals(final Object o) {
		if(o == null || !(o instanceof AudibleTurnCommand)) {
			return false;
		}

		final AudibleTurnCommand atc = (AudibleTurnCommand)o;
		return atc.mDistanceVoiceElement.RESID == this.mDistanceVoiceElement.RESID && atc.mFullTurnText.equals(this.mFullTurnText);
	}

	// ===========================================================
	// Methods
	// ===========================================================
}
