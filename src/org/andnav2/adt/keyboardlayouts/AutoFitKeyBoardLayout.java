// Created by plusminus on 21:32:16 - 15.05.2008
package org.andnav2.adt.keyboardlayouts;

public abstract class AutoFitKeyBoardLayout extends AbstractKeyBoardLayout {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final String keys;

	// ===========================================================
	// Constructors
	// ===========================================================

	AutoFitKeyBoardLayout(final String someKeys){
		this.keys = someKeys;
		this.gridFieldSum = this.keys.length();
		this.columnsPortrait = 7;
		this.columnsLandscape = 10;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Character getKey(final int position) {
		if(position > this.keys.length() - 1) {
			return ' ';
		} else {
			return this.keys.charAt(position);
		}
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}