// Created by plusminus on 16:49:55 - 22.05.2008
package org.andnav2.adt;

import java.text.DecimalFormat;

import org.andnav2.R;
import org.andnav2.adt.voice.DistanceVoiceElement;
import org.andnav2.sys.ors.adt.rs.DirectionsLanguage;
import org.andnav2.util.constants.UnitConstants;

import android.content.Context;


public enum UnitSystem {
	METRIC(1, "m", R.string.units_meters, 1, "km", R.string.units_kilometers, UnitConstants.METERSPERSECONDTOKILOMETERSPERHOUR, "km/h", new FloatToFloatMethod(){
		public float calculate(final float f) {
			return f;
		}
	}, "�C", "metric"),
	IMPERIAL(UnitConstants.METERTOYARD, "y", R.string.units_yards, UnitConstants.KILOMETERTOMILE, "mi", R.string.units_miles , UnitConstants.METERSPERSECONDTOMILESPERHOUR, "mph", new FloatToFloatMethod(){
		public float calculate(final float f) {
			return 9.0f / 5.0f * f + 32;
		}
	}, "�F", "us");

	public static final int DISTSTRINGS_DIST_ID = 0;
	public static final int DISTSTRINGS_UNIT_ID = 1;

	protected static final DecimalFormat lessThanTenXHighDF = new DecimalFormat("0.0");

	/** Factor to multiply the meter/kilometer/mps amount with to get the correct value in the destination unit-system. */
	public final float mScaleToMeters, mScaleToKilometers, mScaleToMetersPerSecond;
	public final int mResIDMeterScale, mResIDKilometersScale;
	public final String mAbbrMeterScale, mAbbrKilometersScale, mAbbrKilometersPerHourScale, mAbbrTemperature;
	public final String mAbbreviation; // a kind of ID
	private final FloatToFloatMethod mTempFFMethod;

	private UnitSystem(final float aScaleToMeters, final String aAbbrScaleToMetersString, final int aResIDScaleToMetersString, final float aScaleToKilometers, final String aAbbrScaleToKilometersString, final int aResIDScaleToKilometersString, final float aScaleToKilometerPerHour, final String aAbbrScaleToKilometersPerHourString, final FloatToFloatMethod aTempFFMethod, final String aAbbrTemperature, final String aAbbreviation) {
		this.mAbbreviation = aAbbreviation;
		this.mScaleToMeters = aScaleToMeters;
		this.mScaleToKilometers = aScaleToKilometers;
		this.mScaleToMetersPerSecond = aScaleToKilometerPerHour;
		this.mAbbrMeterScale = aAbbrScaleToMetersString;
		this.mResIDMeterScale = aResIDScaleToMetersString;
		this.mAbbrKilometersScale = aAbbrScaleToKilometersString;
		this.mResIDKilometersScale = aResIDScaleToKilometersString;
		this.mAbbrKilometersPerHourScale = aAbbrScaleToKilometersPerHourString;
		this.mTempFFMethod = aTempFFMethod;
		this.mAbbrTemperature = aAbbrTemperature;
	}

	public float convertTemperatureFromCelsius(final float aCelsius){
		return this.mTempFFMethod.calculate(aCelsius);
	}

	public static UnitSystem fromAbbreviation(final String aAbbr){
		for(final UnitSystem ds : UnitSystem.values()) {
			if(ds.mAbbreviation.compareTo(aAbbr) == 0) {
				return ds;
			}
		}
		throw new IllegalArgumentException();
	}

	public DistanceVoiceElement convertFromMetricDistanceVoice(final DistanceVoiceElement in){
		switch(this){
			case IMPERIAL:
				switch(in){
					case KM_ONE:
						return DistanceVoiceElement.MI_ONE;
					case KM_TWO:
						return DistanceVoiceElement.MI_TWO;
					case KM_FIVE:
						return DistanceVoiceElement.MI_FIVE;
					case KM_TEN:
						return DistanceVoiceElement.MI_TEN;
					case KM_TWENTY_FIVE:
						return DistanceVoiceElement.MI_TWENTY_FIVE;
					case M_50:
						return DistanceVoiceElement.Y_50;
					case M_100:
						return DistanceVoiceElement.Y_100;
					case M_200:
						return DistanceVoiceElement.Y_200;
					case M_500:
						return DistanceVoiceElement.Y_500;
				};
			case METRIC:
				return in;
		}
		throw new IllegalArgumentException();
	}


	public String[] getDistanceString(final int aMeterDistance, final String[] reuse){
		final int restDistanceUnitScaledHigh = (int)(aMeterDistance * this.mScaleToKilometers);
		final int restDistanceUnitScaledLow = (int)(aMeterDistance * this.mScaleToMeters);
		// Value are now in the UnitSystem

		final String[] ret = (reuse != null) ? reuse : new String[2];

		if(restDistanceUnitScaledHigh >= 1000 * 1000){ // x > 1000km
			ret[DISTSTRINGS_DIST_ID] = lessThanTenXHighDF.format(restDistanceUnitScaledHigh / (1000f * 1000)) + "k";
			ret[DISTSTRINGS_UNIT_ID] = this.mAbbrKilometersScale;
		}else if(restDistanceUnitScaledHigh > 10000){ // x > 10km
			ret[DISTSTRINGS_DIST_ID] = "" + restDistanceUnitScaledHigh / 1000;
			ret[DISTSTRINGS_UNIT_ID] = this.mAbbrKilometersScale;
		}else if(restDistanceUnitScaledHigh >= 1000){ // 10km > x > 1km
			ret[DISTSTRINGS_DIST_ID] = lessThanTenXHighDF.format(restDistanceUnitScaledHigh / 1000f);
			ret[DISTSTRINGS_UNIT_ID] = this.mAbbrKilometersScale;
		}else if(restDistanceUnitScaledLow >= 1000){ // 1km > x > 500m
			ret[DISTSTRINGS_DIST_ID] = lessThanTenXHighDF.format(restDistanceUnitScaledHigh / 1000f);
			ret[DISTSTRINGS_UNIT_ID] = this.mAbbrKilometersScale;
		}else if(restDistanceUnitScaledLow >= 500){ // 1km > x > 500m
			ret[DISTSTRINGS_DIST_ID] = "" + (restDistanceUnitScaledLow - restDistanceUnitScaledLow % 100);
			ret[DISTSTRINGS_UNIT_ID] = this.mAbbrMeterScale;
		}else if(restDistanceUnitScaledLow >= 100){ // 500m > x > 100m
			ret[DISTSTRINGS_DIST_ID] = "" + (restDistanceUnitScaledLow - restDistanceUnitScaledLow % 50);
			ret[DISTSTRINGS_UNIT_ID] = this.mAbbrMeterScale;
		}else{ // 100m > x
			ret[DISTSTRINGS_DIST_ID] = "" + (restDistanceUnitScaledLow - restDistanceUnitScaledLow % 10);
			ret[DISTSTRINGS_UNIT_ID] = this.mAbbrMeterScale;
		}

		return ret;
	}

	public String[] getDistanceStringFull(final Context ctx, final DirectionsLanguage pLanguage, final String[] reuse, final int aMeterDistance){
		final int restDistanceUnitScaledHigh = (int)(aMeterDistance * this.mScaleToKilometers);
		final int restDistanceUnitScaledLow = (int)(aMeterDistance * this.mScaleToMeters);
		// Value are now in the UnitSystem

		final String[] ret = (reuse != null) ? reuse : new String[2];

		if(restDistanceUnitScaledHigh >= 1000 * 1000){ // x > 1000km
			ret[DISTSTRINGS_DIST_ID] = lessThanTenXHighDF.format(restDistanceUnitScaledHigh / (1000f * 1000)) + "k";
			ret[DISTSTRINGS_UNIT_ID] = getKilometerScaleFromLanguage(ctx, pLanguage);
		}else if(restDistanceUnitScaledHigh > 10000){ // x > 10km
			ret[DISTSTRINGS_DIST_ID] = "" + restDistanceUnitScaledHigh / 1000;
			ret[DISTSTRINGS_UNIT_ID] = getKilometerScaleFromLanguage(ctx, pLanguage);
		}else if(restDistanceUnitScaledHigh >= 1000){ // 10km > x > 1km
			ret[DISTSTRINGS_DIST_ID] = lessThanTenXHighDF.format(restDistanceUnitScaledHigh / 1000f);
			ret[DISTSTRINGS_UNIT_ID] = getKilometerScaleFromLanguage(ctx, pLanguage);
		}else if(restDistanceUnitScaledLow >= 1000){ // 1km > x > 500m
			ret[DISTSTRINGS_DIST_ID] = lessThanTenXHighDF.format(restDistanceUnitScaledHigh / 1000f);
			ret[DISTSTRINGS_UNIT_ID] = getKilometerScaleFromLanguage(ctx, pLanguage);
		}else if(restDistanceUnitScaledLow >= 500){ // 1km > x > 500m
			ret[DISTSTRINGS_DIST_ID] = "" + (restDistanceUnitScaledLow - restDistanceUnitScaledLow % 100);
			ret[DISTSTRINGS_UNIT_ID] = getMeterScaleFromLanguage(ctx, pLanguage);
		}else if(restDistanceUnitScaledLow >= 100){ // 500m > x > 100m
			ret[DISTSTRINGS_DIST_ID] = "" + (restDistanceUnitScaledLow - restDistanceUnitScaledLow % 50);
			ret[DISTSTRINGS_UNIT_ID] = getMeterScaleFromLanguage(ctx, pLanguage);
		}else{ // 100m > x
			ret[DISTSTRINGS_DIST_ID] = "" + (restDistanceUnitScaledLow - restDistanceUnitScaledLow % 10);
			ret[DISTSTRINGS_UNIT_ID] = getMeterScaleFromLanguage(ctx, pLanguage);
		}

		return ret;
	}

	private String getKilometerScaleFromLanguage(final Context ctx, final DirectionsLanguage pLanguage) {
		switch(this){
			case METRIC:
				return pLanguage.getTurnInstructionsSet(ctx).getKilometers();
			case IMPERIAL:
				return pLanguage.getTurnInstructionsSet(ctx).getMiles();
		}
		throw new IllegalArgumentException();
	}

	private String getMeterScaleFromLanguage(final Context ctx, final DirectionsLanguage pLanguage) {
		switch(this){
			case METRIC:
				return pLanguage.getTurnInstructionsSet(ctx).getMeters();
			case IMPERIAL:
				return pLanguage.getTurnInstructionsSet(ctx).getYards();
		}
		throw new IllegalArgumentException();
	}

	public DistanceStringReturnValue getDistanceString(final int aMeterDistance){
		final int restDistanceUnitScaledHigh = (int)(aMeterDistance * this.mScaleToKilometers);
		final int restDistanceUnitScaledLow = (int)(aMeterDistance * this.mScaleToMeters);
		// Value are now in the UnitSystem

		final String retText;
		final int unitResID;

		if(restDistanceUnitScaledHigh >= 1000 * 1000){ // x > 1000km
			retText = lessThanTenXHighDF.format(restDistanceUnitScaledHigh / (1000f * 1000)) + "k";
			unitResID = this.mResIDKilometersScale;
		}else if(restDistanceUnitScaledHigh > 10000){ // x > 10km
			retText = "" + restDistanceUnitScaledHigh / 1000;
			unitResID = this.mResIDKilometersScale;
		}else if(restDistanceUnitScaledHigh >= 1000){ // 10km > x > 1km
			retText = lessThanTenXHighDF.format(restDistanceUnitScaledHigh / 1000f);
			unitResID = this.mResIDKilometersScale;
		}else if(restDistanceUnitScaledLow >= 1000){ // 1km > x > 500m
			retText = lessThanTenXHighDF.format(restDistanceUnitScaledHigh / 1000f);
			unitResID = this.mResIDKilometersScale;
		}else if(restDistanceUnitScaledLow >= 500){ // 1km > x > 500m
			retText = "" + (restDistanceUnitScaledLow - restDistanceUnitScaledLow % 100);
			unitResID = this.mResIDMeterScale;
		}else if(restDistanceUnitScaledLow >= 100){ // 500m > x > 100m
			retText = "" + (restDistanceUnitScaledLow - restDistanceUnitScaledLow % 50);
			unitResID = this.mResIDMeterScale;
		}else{ // 100m > x
			retText = "" + (restDistanceUnitScaledLow - restDistanceUnitScaledLow % 10);
			unitResID = this.mResIDMeterScale;
		}

		return new DistanceStringReturnValue(unitResID, retText);
	}

	public static class DistanceStringReturnValue {
		public final int UNIT_RESID;
		public final String LENGTH_STRING;

		public DistanceStringReturnValue(final int pUnitResID, final String pLengthString) {
			this.UNIT_RESID = pUnitResID;
			this.LENGTH_STRING = pLengthString;
		}
	}

	private static interface FloatToFloatMethod {
		public float calculate(float f);
	}
}


