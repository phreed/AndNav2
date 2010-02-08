// Created by plusminus on 20:34:07 - 17.05.2008
package org.andnav2.ui;

import org.andnav2.preferences.PreferenceConstants;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.common.DataStateChangedListener;
import org.andnav2.ui.common.MyDataStateChangedWatcher;
import org.andnav2.util.constants.Constants;

import android.app.Activity;
import android.os.Bundle;

public class AndNavBaseActivity extends Activity implements DataStateChangedListener, Constants, PreferenceConstants {

	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected boolean mMenuVoiceEnabled = true;

	protected MyDataStateChangedWatcher mDscw;

	private int mDataConnectionStrength = 5;
	protected boolean mNeedDataState = false;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	protected void onCreate(final Bundle icicle, final boolean aNeedDataState) {
		super.onCreate(icicle);
		this.mMenuVoiceEnabled = Preferences.getMenuVoiceEnabled(this);
		this.mNeedDataState = aNeedDataState;
	}

	@Override
	protected void onCreate(final Bundle icicle) {
		this.onCreate(icicle, false);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	protected int getDataConnectionStrength() {
		if(this.mNeedDataState) {
			return this.mDataConnectionStrength;
		} else {
			throw new IllegalStateException("The need for DataState needs to be passed in onCreate().");
		}
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	/** Restart the receiving, when we are back on line. */
	@Override
	protected void onResume() {
		super.onResume();

		this.setRequestedOrientation(Preferences.getRequestedScreenOrientation(this));

		if(this.mNeedDataState) {
			this.onResumeForDataStateChangedListener();
		}

		this.mMenuVoiceEnabled = Preferences.getMenuVoiceEnabled(this);
	}

	@Override
	protected void onPause() {
		this.onPauseForDataStateChangedListener();
		super.onPause();
	}

	public void onPauseForDataStateChangedListener() {
		if(this.mNeedDataState) {
			this.mDscw.unregister();
		}
	}

	public void onResumeForDataStateChangedListener() {
		/** Initiates the local field <code>dsir</code> a
		 * DataStateChangedWatcher to notify this class
		 * on changes to the Connection-State... */
		this.mDscw = new MyDataStateChangedWatcher(this, this);
	}

	public void onDataStateChanged(final int aStrength) {
		if(this.mNeedDataState) {
			this.mDataConnectionStrength = aStrength;
		} else {
			throw new IllegalStateException("Cannot get DataConnection Strength, as NeedDataState = false");
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
