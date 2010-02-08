// Created by plusminus on 20:45:48 - 25.04.2008
package org.andnav2.adt.voice;

import org.andnav2.R;
import org.andnav2.preferences.PreferenceConstants;
import org.andnav2.sys.ors.adt.rs.DirectionsLanguage;

import android.content.Context;


public enum DistanceVoiceElement implements PreferenceConstants {
	M_50(R.raw.m_50, PREF_TURNVOICE_ELEMENT_50),
	M_100(R.raw.m_100, PREF_TURNVOICE_ELEMENT_100),
	M_200(R.raw.m_200, PREF_TURNVOICE_ELEMENT_200),
	M_500(R.raw.m_500, PREF_TURNVOICE_ELEMENT_500),
	KM_ONE(R.raw.km_1, 1, PREF_TURNVOICE_ELEMENT_1000),
	KM_TWO(R.raw.km_2, 2, PREF_TURNVOICE_ELEMENT_2000),
	KM_FIVE(R.raw.km_5, 5, PREF_TURNVOICE_ELEMENT_5000),
	KM_TEN(R.raw.km_10, 10, PREF_TURNVOICE_ELEMENT_10000),
	KM_TWENTY_FIVE(R.raw.km_25, 25, PREF_TURNVOICE_ELEMENT_25000),

	Y_50(R.raw.y_50, PREF_TURNVOICE_ELEMENT_50),
	Y_100(R.raw.y_100, PREF_TURNVOICE_ELEMENT_100),
	Y_200(R.raw.y_200, PREF_TURNVOICE_ELEMENT_200),
	Y_500(R.raw.y_500, PREF_TURNVOICE_ELEMENT_500),
	MI_ONE(R.raw.mi_1, 1, PREF_TURNVOICE_ELEMENT_1000),
	MI_TWO(R.raw.mi_2, 2, PREF_TURNVOICE_ELEMENT_2000),
	MI_FIVE(R.raw.mi_5, 5, PREF_TURNVOICE_ELEMENT_5000),
	MI_TEN(R.raw.mi_10, 10, PREF_TURNVOICE_ELEMENT_10000),
	MI_TWENTY_FIVE(R.raw.mi_25, 25, PREF_TURNVOICE_ELEMENT_25000);

	public final int RESID;
	public final int LENGTH_UNITWISE;
	public final int LENGTH_METERS;

	private DistanceVoiceElement(final int aResID, final int aLengthUnitwise) {
		this(aResID, aLengthUnitwise, aLengthUnitwise);
	}

	private DistanceVoiceElement(final int aResID, final int aLengthUnitwise, final int aLengthMeters) {
		this.RESID = aResID;
		this.LENGTH_UNITWISE = aLengthUnitwise;
		this.LENGTH_METERS = aLengthMeters;
	}

	public String getUnitTextual(final Context ctx, final DirectionsLanguage pLanguage){
		switch(this){
			case M_50: case M_100: case M_200: case M_500:
				return pLanguage.getTurnInstructionsSet(ctx).getMeters();
			case KM_ONE: case KM_TWO: case KM_FIVE: case KM_TEN: case KM_TWENTY_FIVE:
				return pLanguage.getTurnInstructionsSet(ctx).getKilometers();
			case Y_50: case Y_100: case Y_200: case Y_500:
				return pLanguage.getTurnInstructionsSet(ctx).getYards();
			case MI_ONE: case MI_TWO: case MI_FIVE: case MI_TEN: case MI_TWENTY_FIVE:
				return pLanguage.getTurnInstructionsSet(ctx).getMiles();
			default:
				throw new IllegalStateException("Default-Branch in org.andnav2.nav.voice.DistanceVoiceElement.getUnitTextual()");
		}
	}
}
