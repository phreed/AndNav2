// Created by plusminus on 21:32:04 - 15.05.2008
package org.andnav2.adt.keyboardlayouts;


public abstract class FixedWidthKeyBoardLayout extends AbstractKeyBoardLayout{
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	protected final String[] keyRows;
	// ===========================================================
	// Constructors
	// ===========================================================
	public FixedWidthKeyBoardLayout(final String[] someKeyRows, final int aColumnCount){
		this.gridFieldSum = aColumnCount * someKeyRows.length;
		this.keyRows = someKeyRows;
		this.columnsPortrait = aColumnCount;
		this.columnsLandscape = aColumnCount;
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Character getKey(final int position) {
		final int row = position / this.columnsPortrait;
		final int index = position % this.columnsPortrait;
		if(index > this.keyRows[row].length() - 1) {
			return ' ';
		} else {
			return this.keyRows[row].charAt(index);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}