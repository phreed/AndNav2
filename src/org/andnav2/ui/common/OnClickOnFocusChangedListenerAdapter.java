// Created by plusminus on 20:59:46 - 17.05.2008
package org.andnav2.ui.common;

import android.app.Activity;
import android.view.View;


public class OnClickOnFocusChangedListenerAdapter extends OnClickOnFocusChangedListener {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public OnClickOnFocusChangedListenerAdapter(final Activity activity, final int ... resIDs) {
		super(activity, resIDs);
	}

	public OnClickOnFocusChangedListenerAdapter(final View ... views) {
		super(views);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onBoth(final View me, final boolean focus){
		// Nothing to do, subclasses will do the rest
	}

	@Override
	public void onClicked(final View me){
		// Nothing to do, subclasses will do the rest
	}

	@Override
	public void onFocusChanged(final View me, final boolean focused) {
		// Nothing to do, subclasses will do the rest
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
