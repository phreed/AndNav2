// Created by plusminus on 20:48:35 - 17.05.2008
package org.andnav2.ui.common;

import org.andnav2.util.constants.Constants;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;


public abstract class OnClickOnFocusChangedListener implements OnClickListener, OnFocusChangeListener{
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	protected long mFocusLostOn = Constants.NOT_SET;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OnClickOnFocusChangedListener(final Activity aActivity, final int ... resIDs){
		for(final int aResID : resIDs) {
			applyTo(aActivity.findViewById(aResID));
		}
	}

	public OnClickOnFocusChangedListener(final View ... views){
		for(final View aView : views) {
			applyTo(aView);
		}
	}

	public void applyTo(final View aView){
		aView.setOnClickListener(this);

		aView.setOnFocusChangeListener(this);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public abstract void onClicked(View me);

	public void onClick(final View me) {
		final long dif = System.currentTimeMillis() - this.mFocusLostOn;
		this.onBoth(me, dif > 250); // TODO This sucks, maybe: loosing focus onClick is a bug ?
		this.onClicked(me);
	}

	public abstract void onFocusChanged(View me, boolean focused);

	public void onFocusChange(final View me, final boolean focused) {
		if(!focused) {
			this.mFocusLostOn = System.currentTimeMillis();
		}

		this.onBoth(me, focused);
		this.onFocusChanged(me, focused);
	}

	/**
	 * 
	 * @param me
	 * @param justGotFocus <code>true</code>, when View just received the focus via onFocusChanged OR view was clicked, having had NO focus before.<br />
	 * 						<code>false</code> when View just lost the focus via onFocusChanged OR view was clicked, having had focus before.
	 */
	public abstract void onBoth(View me, boolean justGotFocus);

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
