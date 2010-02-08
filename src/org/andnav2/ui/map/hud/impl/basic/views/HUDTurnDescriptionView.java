// Created by plusminus on 9:25:55 PM - Feb 18, 2009
package org.andnav2.ui.map.hud.impl.basic.views;

import org.andnav2.ui.map.hud.IHUDTurnDescriptionView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


public class HUDTurnDescriptionView extends TextView implements IHUDTurnDescriptionView {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public HUDTurnDescriptionView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void reset() {
		setTurnDescription("");
	}

	public void setTurnDescription(final String pTurnDescription) {
		this.setText(pTurnDescription);
	}

	public String getTurnDescription() {
		return this.getText().toString();
	}

	public void setTurnDescriptionOnClickListener(final OnClickListener pOnClickListener) {
		this.setOnClickListener(pOnClickListener);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
