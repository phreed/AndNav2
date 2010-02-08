// Created by plusminus on 12:57:42 - 19.01.2009
package org.andnav2.sound.tts;

import android.content.Context;


public class Util {
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

	public static boolean show(final Context ctx){
//		if(!TextToSpeech.isInstalled(ctx)){
//			return false;
//		}else{
//			try {
//				final Intent intent = new Intent(ctx.createPackageContext("com.google.tts", 0), com.google.tts.ConfigurationManager.class);
//				ctx.startActivity(intent);
//			} catch (final NameNotFoundException e) {
//				// TTS.installed would have returned false in this case
//			}
			return true;
//		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
