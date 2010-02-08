// Created by plusminus on 18:26:58 - 05.04.2008
package org.andnav2.exc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.andnav2.ui.util.Util;
import org.andnav2.util.constants.Constants;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Exceptor implements Constants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final int LEVEL_VERBOSE = 0;
	public static final int LEVEL_DEBUG = LEVEL_VERBOSE + 1;
	public static final int LEVEL_ERROR = LEVEL_DEBUG + 1;
	public static final int LEVEL_NONE = LEVEL_ERROR + 1;

	public static final int LEVEL = LEVEL_VERBOSE;

	public static final int MODE_RELEASE = 0;
	public static final int MODE_DEBUG = MODE_RELEASE + 1;
	public static final int MODE_TOAST = MODE_DEBUG + 1;
	public static final int MODE_MAIL = MODE_TOAST + 1;

	public static final int MODE = MODE_MAIL;

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

	public static void v(final String msg, final Throwable t){
		v(msg, t, null);
	}

	public static void d(final String msg, final Throwable t){
		d(msg, t, null);
	}

	public static void e(final String msg, final Throwable t){
		e(msg, t, null);
	}

	public static void v(final String msg, final Throwable t, final Context ctx){
		if(LEVEL <= LEVEL_VERBOSE) {
			log(msg,t, ctx, LEVEL_VERBOSE);
		}
	}

	public static void d(final String msg, final Throwable t, final Context ctx){
		if(LEVEL <= LEVEL_DEBUG) {
			log(msg,t, ctx, LEVEL_DEBUG);
		}
	}

	public static void e(final String msg, final Throwable t, final Context ctx){
		if(LEVEL <= LEVEL_ERROR) {
			log(msg,t, ctx, LEVEL_ERROR);
		}
	}

	private static void log(final String msg, final Throwable t, final Context ctx, final int pLevel){
		try{
			switch(MODE){
				case MODE_RELEASE:
					// Do nothing
					break;
				case MODE_TOAST:
					if(ctx != null){
						Toast.makeText(ctx, getStacktraceString(t), Toast.LENGTH_LONG).show();
					}else{
						rawLog(pLevel, DEBUGTAG, msg, t);
					}
					break;
				case MODE_MAIL:
					if(ctx != null){
						Util.sendExceptionEmail(ctx, "Your Description: \n\n\nError-Cause:\n\n" + getStacktraceString(t));
					}else{
						rawLog(pLevel, DEBUGTAG, msg, t);
					}
					break;
				case MODE_DEBUG:
					rawLog(pLevel, DEBUGTAG, msg, t);
					break;
				default:
					throw new IllegalArgumentException("Unknown Exceptor MODE");
			}
		}catch(final Throwable t2){
			rawLog(pLevel, DEBUGTAG, msg, t);
		}
	}

	private static void rawLog(final int pLevel, final String pTag, final String msg, final Throwable t) {
		switch(pLevel){
			case LEVEL_VERBOSE:
				Log.v(pTag, msg);
				break;
			case LEVEL_DEBUG:
				Log.d(pTag, msg, t);
				break;
			case LEVEL_ERROR:
				Log.e(pTag, msg, t);
				break;
		}
	}

	private static String getStacktraceString(final Throwable t){
		if(t == null) {
			return "No Stacktrace available";
		}

		final ByteArrayOutputStream bo = new ByteArrayOutputStream();
		t.printStackTrace(new PrintStream(bo));
		return new String(bo.toByteArray());
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
