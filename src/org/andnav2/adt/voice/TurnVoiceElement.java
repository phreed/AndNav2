package org.andnav2.adt.voice;

import org.andnav2.R;
import org.andnav2.sys.ors.adt.rs.DirectionsLanguage;

import android.content.Context;
import android.widget.Toast;


public enum TurnVoiceElement {
	// ===========================================================
	// Elements
	// ===========================================================

	//	NONE(Constants.NOT_SET),

	//	LEFT_SHARPER(R.raw.take_a_sharp_left,),
	LEFT_SHARP(R.raw.turn_left_sharply),
	LEFT_BEAR(R.raw.bear_left),
	LEFT(R.raw.turn_left),

	//	RIGHT_SHARPER(R.raw.take_a_sharp_right),
	RIGHT_SHARP(R.raw.turn_right_sharply),
	RIGHT_BEAR(R.raw.bear_right),
	RIGHT(R.raw.turn_right),

	TURN_AROUND(R.raw.turn_around),
	STRAIGHT_ON(R.raw.go_straight_on);
	//	CONTINUE_AHEAD(R.raw.continue_ahead);

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public final int VOICERESID;

	// ===========================================================
	// Constructors
	// ===========================================================

	private TurnVoiceElement(final int pVoiceResID) {
		this.VOICERESID = pVoiceResID;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getText(final Context ctx, final DirectionsLanguage pLanguage) {
		Toast.makeText(ctx, this.name(), Toast.LENGTH_SHORT).show();
		switch(this){
			case LEFT:
				return pLanguage.getTurnInstructionsSet(ctx).getLeft();
			case LEFT_BEAR:
				return pLanguage.getTurnInstructionsSet(ctx).getHalfLeft();
			case LEFT_SHARP:
				return pLanguage.getTurnInstructionsSet(ctx).getSharpLeft();
			case RIGHT:
				return pLanguage.getTurnInstructionsSet(ctx).getRight();
			case RIGHT_BEAR:
				return pLanguage.getTurnInstructionsSet(ctx).getHalfRight();
			case RIGHT_SHARP:
				return pLanguage.getTurnInstructionsSet(ctx).getSharpRight();
			case STRAIGHT_ON:
				return pLanguage.getTurnInstructionsSet(ctx).getStraightForward();
			case TURN_AROUND:
				return pLanguage.getTurnInstructionsSet(ctx).getUTurn();
		}

		throw new IllegalArgumentException();
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
