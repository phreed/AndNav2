// Created by plusminus on 10:47:26 PM - Feb 18, 2009
package org.andnav2.ui.map.hud;

import org.andnav2.ui.map.hud.impl.basic.BasicHUDImpl;
import org.andnav2.ui.map.hud.impl.bigarrow.BigArrowHUDImpl;
import org.andnav2.ui.map.hud.impl.mavoric.MavoricHUDImpl;


public class HUDRegistry {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final IHUDImpl[] ALLHUDIMPLEMENTATIONS = new IHUDImpl[]{new BasicHUDImpl(), new MavoricHUDImpl(), new BigArrowHUDImpl()};

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

	public static IHUDImpl resolve(final int pHudID){
		switch(pHudID){
			case BasicHUDImpl.ID:
				return new BasicHUDImpl();
			case MavoricHUDImpl.ID:
				return new MavoricHUDImpl();
			case BigArrowHUDImpl.ID:
				return new BigArrowHUDImpl();
		}

		return getDefaultHUDImpl();
	}

	private static IHUDImpl getDefaultHUDImpl() {
		return new BasicHUDImpl();
	}

	public static IHUDImpl[] getAll(){
		return ALLHUDIMPLEMENTATIONS;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
