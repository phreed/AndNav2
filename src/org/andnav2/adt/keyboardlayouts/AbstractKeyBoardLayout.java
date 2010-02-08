// Created by plusminus on 21:32:28 - 15.05.2008
package org.andnav2.adt.keyboardlayouts;

import org.andnav2.util.constants.Constants;

import android.view.Display;


public abstract class AbstractKeyBoardLayout{
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final Character BUTTONGRID_BACKCAPTION = '<';

	// ===========================================================
	// Fields
	// ===========================================================

	protected int gridFieldSum;
	protected int columnsPortrait, columnsLandscape;

	protected int id = Constants.NOT_SET; // unique

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

	public abstract Character getKey(int position);

	public int getGridFieldSum() {
		return this.gridFieldSum;
	}

	public int getColumnsPortrait() {
		return this.columnsPortrait;
	}

	public int getColumnsLandscape() {
		return this.columnsLandscape;
	}

	public int getID() {
		if(this.id == Constants.NOT_SET) {
			throw new UnsupportedOperationException();
		}

		return this.id;
	}

	/**
	 * Returns the number of columns to use, depending on the Display.
	 * Usually when the display is in landscape mode, the number of columns is bigger than in Portrait-Mode.
	 * @param pDisplay
	 * @return
	 */
	public int getColumnsByDisplay(final Display pDisplay) {
		final float ratio = pDisplay.getHeight() / pDisplay.getWidth();
		if(ratio == 0) {
			return getColumnsLandscape();
		} else {
			return getColumnsPortrait();
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}