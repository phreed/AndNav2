// Created by plusminus on 13:17:57 - 18.05.2008
package org.andnav2.ui.common;


public interface DataStateChangedListener {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void onDataStateChanged(int strength);

	public void onPauseForDataStateChangedListener();

	public void onResumeForDataStateChangedListener();
}
