//Created by plusminus on 00:26:29 - 18.05.2008
package org.andnav2.adt.voice;

import org.andnav2.R;
import org.andnav2.util.constants.Constants;

import android.view.KeyEvent;


public class Voice {
	// ===========================================================
	// Final Fields
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

	public static int getNumberVoice(final int i){
		switch(i){
			case 0:
				return R.raw.number_0;
			case 1:
				return R.raw.number_1;
			case 2:
				return R.raw.number_2;
			case 3:
				return R.raw.number_3;
			case 4:
				return R.raw.number_4;
			case 5:
				return R.raw.number_5;
			case 6:
				return R.raw.number_6;
			case 7:
				return R.raw.number_7;
			case 8:
				return R.raw.number_8;
			case 9:
				return R.raw.number_9;
			default:
				return Constants.NOT_SET;
		}
	}

	public static int getNumberVoiceFromKeyCode(final int keyCode) {
		switch(keyCode){
			case KeyEvent.KEYCODE_0:
				return R.raw.number_0;
			case KeyEvent.KEYCODE_1:
				return R.raw.number_1;
			case KeyEvent.KEYCODE_2:
				return R.raw.number_2;
			case KeyEvent.KEYCODE_3:
				return R.raw.number_3;
			case KeyEvent.KEYCODE_4:
				return R.raw.number_4;
			case KeyEvent.KEYCODE_5:
				return R.raw.number_5;
			case KeyEvent.KEYCODE_6:
				return R.raw.number_6;
			case KeyEvent.KEYCODE_7:
				return R.raw.number_7;
			case KeyEvent.KEYCODE_8:
				return R.raw.number_8;
			case KeyEvent.KEYCODE_9:
				return R.raw.number_9;
			default:
				return Constants.NOT_SET;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
