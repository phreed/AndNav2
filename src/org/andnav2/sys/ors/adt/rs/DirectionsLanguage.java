// Created by plusminus on 4:15:07 PM - Feb 26, 2009
package org.andnav2.sys.ors.adt.rs;

import org.andnav2.R;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.turninstructions.ITurnInstructionsSet;
import org.andnav2.sys.turninstructions.TurnInstructionLoader;

import android.content.Context;


public enum DirectionsLanguage {
	// ===========================================================
	// Elements
	// ===========================================================

	ENGLISH("en", R.string.dialect_none, R.xml.turninstructions_en),
	CZECH("cs", R.string.dialect_none, R.xml.turninstructions_cs),
	GERMAN("de", R.string.dialect_none, R.xml.turninstructions_de),
	GERMAN_PLATT("de-opplat", R.string.dialect_germany_platt, R.xml.turninstructions_de),
	GERMAN_SCHWAEBISCH("de-swabia", R.string.dialect_germany_schwaebisch, R.xml.turninstructions_de),
	GERMAN_BERLINERISCH("de-berlin", R.string.dialect_germany_berlinerisch, R.xml.turninstructions_de),
	GERMAN_RHEINLAENDISCH("de-rheinl", R.string.dialect_germany_rheinlaendisch, R.xml.turninstructions_de),
	GERMAN_RUHRPOTT("de-ruhrpot", R.string.dialect_germany_ruhrpott, R.xml.turninstructions_de),
	FRENCH("fr", R.string.dialect_none),
	FINNISH("fi", R.string.dialect_none),
	ESPERANTO("eo", R.string.dialect_none),
	DUTCH("nl", R.string.dialect_none, R.xml.turninstructions_de),
	DUTCH_FLANDERN("nl_BE", R.string.dialect_none, R.xml.turninstructions_de),
	SPANISH("es", R.string.dialect_none),
	ITALIAN("it", R.string.dialect_none),
	TURKISH("tr", R.string.dialect_none),
	PORTUGUESE("pt_BR", R.string.dialect_none),
	SWEDISH("se", R.string.dialect_none);

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public final String ID;
	public final int NAMERESID;
	private Country MOTHERCOUNTRY;
	private final int TURNINSTRUCTIONSREDID;
	private ITurnInstructionsSet mTurnInstructionsSet;

	// ===========================================================
	// Constructors
	// ===========================================================

	private DirectionsLanguage(final String pIdentifier, final int pNameResID){
		this(pIdentifier, pNameResID, R.xml.turninstructions_en);
	}

	private DirectionsLanguage(final String pIdentifier, final int pNameResID, final int pTurnInstructionsResID){
		this.ID = pIdentifier;
		this.NAMERESID = pNameResID;
		this.TURNINSTRUCTIONSREDID = pTurnInstructionsResID;
	}

	public static DirectionsLanguage fromAbbreviation(final String pID) {
		for(final DirectionsLanguage d : values()) {
			if(d.ID.compareToIgnoreCase(pID) == 0) {
				return d;
			}
		}

		return DirectionsLanguage.ENGLISH;
	}

	public String getIETFLanguageTag() {
		if(this.MOTHERCOUNTRY == null || this.MOTHERCOUNTRY.IETFLANGUAGETAG == null) {
			return Country.UNITEDKINGDOM.IETFLANGUAGETAG;
		} else {
			return this.MOTHERCOUNTRY.IETFLANGUAGETAG;
		}
	}

	public Country getMotherCountry(){
		if(this.MOTHERCOUNTRY == null) {
			this.MOTHERCOUNTRY = Country.fromDialect(this);
		}

		return this.MOTHERCOUNTRY;
	}

	public ITurnInstructionsSet getTurnInstructionsSet(final Context ctx) {
		if (this.mTurnInstructionsSet == null) {
			try {
				this.mTurnInstructionsSet = TurnInstructionLoader.load(ctx.getResources().getXml(this.TURNINSTRUCTIONSREDID));
			} catch (final Throwable t) {
			}
		}
		return this.mTurnInstructionsSet;
	}

	public String getThenCommandWithoutDistance(final Context ctx, final RoutePreferenceType pPreferenceType){
		final String thenCommandString = this.mTurnInstructionsSet.getThenCommandWithoutDistance();

		if(thenCommandString == null){
			/* Default-Fallback. */
			return ENGLISH.getThenCommandWithoutDistance(ctx, pPreferenceType);
		}else{
			return thenCommandString.replace("@MobilityBasedMovementInstruction", (pPreferenceType == RoutePreferenceType.PEDESTRIAN) ? this.mTurnInstructionsSet.getPedestrian() : this.mTurnInstructionsSet.getVehicle());
		}
	}

	public String getThenCommandWithDistance(final Context ctx, final RoutePreferenceType pPreferenceType){
		final String thenCommandString = this.mTurnInstructionsSet.getThenCommandWithDistance();

		if(thenCommandString == null){
			/* Default-Fallback. */
			return ENGLISH.getThenCommandWithDistance(ctx, pPreferenceType);
		}else{
			return thenCommandString.replace("@MobilityBasedMovementInstruction", (pPreferenceType == RoutePreferenceType.PEDESTRIAN) ? this.mTurnInstructionsSet.getPedestrian() : this.mTurnInstructionsSet.getVehicle());
		}
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

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
