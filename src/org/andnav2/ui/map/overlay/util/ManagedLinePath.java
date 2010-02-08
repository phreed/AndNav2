// Created by plusminus on 10:07:46 - 26.10.2008
package org.andnav2.ui.map.overlay.util;

import android.graphics.Path;
import android.graphics.Point;

/**
 * ManagedPath ensures that <code>moveTo</code> gets called before <code>lineTo</code> gets called for the first time.<br />
 * Its only 'managed' for lines!
 * <b>Info:</b> Where you had to decide whether to call moveTo or lineTo before, now just call lineTo.
 * @author Nicolas Gramlich
 */
public class ManagedLinePath extends Path {
	// ===========================================================
	// Fields
	// ===========================================================

	protected boolean mDidMoveTo = false;

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void lineTo(final Point p) {
		this.lineTo(p.x, p.y);
	}

	@Override
	public void lineTo(final float x, final float y) {
		if(!this.mDidMoveTo) {
			this.moveTo(x, y);
		} else {
			super.lineTo(x, y);
		}
	}

	@Deprecated
	public void moveTo(final Point p) {
		this.moveTo(p.x, p.y);
	}

	@Deprecated
	@Override
	public void moveTo(final float x, final float y) {
		this.mDidMoveTo = true;
		super.moveTo(x, y);
	}
}
