// Created by plusminus on 17:02:14 - 11.08.2008
package org.andnav2.util.constants;


public interface UnitConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final float METERTOYARD = 1.0936133f;
	public static final float YARDTOMETER = 1 / METERTOYARD;
	public static final float KILOMETERTOMILE = 0.621371192f;
	public static final float MILETOKILOMETER = 1 / KILOMETERTOMILE;

	public static final float METERSPERSECONDTOKILOMETERSPERHOUR = (60 * 60) / 1000.0f;
	public static final float METERSPERSECONDTOMILESPERHOUR =  METERSPERSECONDTOKILOMETERSPERHOUR * KILOMETERTOMILE;

	// ===========================================================
	// Methods
	// ===========================================================
}
