// Created by plusminus on 11:57:59 PM - Mar 30, 2009
package org.andnav2.adt.voice;

import static org.andnav2.nav.util.Util.inBounds;

import org.andnav2.adt.UnitSystem;
import org.andnav2.sys.ors.adt.rs.RouteInstruction;
import org.andnav2.util.constants.Constants;


public class AudibleTurnCommandManager {
	// ===========================================================
	// Constants
	// ===========================================================

	/** Influence of the speed in seconds. 1 means that the sound will happen 1 second before the position has been reached. */
	private static final float SPEED_INFLUENCE_SECONDS = 2;

	private static final int MARK_25000 = 25600;
	private static final int MARK_10000 = 10400;
	private static final int MARK_5000 = 5090;
	private static final int MARK_2000 = 2090;
	private static final int MARK_1000 = 1060;
	private static final int MARK_500 = 550;
	private static final int MARK_200 = 250;
	private static final int MARK_100 = 125;
	private static final int MARK_50 = 65;

	private static final int MIN_ANGLE_BEAR_LEFT = 20;
	private static final int MIN_ANGLE_LEFT = 40;
	private static final int MIN_ANGLE_SHARP_LEFT = 65;

	private static final int MIN_ANGLE_BEAR_RIGHT = MIN_ANGLE_BEAR_LEFT;
	private static final int MIN_ANGLE_RIGHT = MIN_ANGLE_LEFT;
	private static final int MIN_ANGLE_SHARP_RIGHT = MIN_ANGLE_SHARP_LEFT;

	// ===========================================================
	// Fields
	// ===========================================================

	private SimpleAudibleTurnCommand LASTAUDIBLETURNCOMMAND;
	private RouteInstruction LASTROUTEINSTRUCTION;

	// ===========================================================
	// Constructors
	// ===========================================================

	public AudibleTurnCommandManager() {

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

	/**
	 * @param pUS The {@Link UnitSystem} is used to scale up/down the distances (<code>oldDist</code> and <code>newDist</code>) between to determine
	 * @param pRouteInstruction the Route-Instruction to extract the information from.
	 * @param pOldDistMeters in METERS.
	 * @param pNewDistMeters in METERS.
	 * @param pCurrentSpeedMetersPerSecond
	 * @param pUpperNextRouteInstruction
	 * @return If neccessary a new {@link AudibleTurnCommand}, otherwise <code>null</code>.
	 */
	public AudibleTurnCommand createIfNeccessary(final UnitSystem pUS, final RouteInstruction pRouteInstruction, final int pOldDistMeters, final int pNewDistMeters, final float pCurrentSpeedMetersPerSecond, final RouteInstruction pUpperNextRouteInstruction) {
		final float pNewDistMetersSpeedAdjusted;
		if(pCurrentSpeedMetersPerSecond == Constants.NOT_SET){
			pNewDistMetersSpeedAdjusted = pNewDistMeters;
		}else{
			pNewDistMetersSpeedAdjusted = pNewDistMeters - SPEED_INFLUENCE_SECONDS * pCurrentSpeedMetersPerSecond;
		}

		final int oldDistKilometerScale = (int)(pOldDistMeters * pUS.mScaleToKilometers);
		final int oldDistMeterScale = (int)(pOldDistMeters * pUS.mScaleToMeters);

		final int newDistKilometerScale = (int)(pNewDistMetersSpeedAdjusted * pUS.mScaleToKilometers);
		final int newDistMeterScale = (int)(pNewDistMetersSpeedAdjusted * pUS.mScaleToMeters);

		final DistanceVoiceElement dve = getDistanceVoiceElementFromDistances(oldDistKilometerScale, oldDistMeterScale, newDistKilometerScale, newDistMeterScale);
		/* We didn't pass any of the 'gates' so nothing needs to be said. */
		if(dve == null) {
			return null;
		}


		final int turnAngle = (int)pRouteInstruction.getAngle();
		final TurnVoiceElement tve = getTurnVoiceElementFromAngle(turnAngle);


		final AudibleTurnCommand response = new AudibleTurnCommand(dve, tve, pRouteInstruction.getDescription(), turnAngle, pNewDistMeters);

		if(pUpperNextRouteInstruction != null && pRouteInstruction.getLengthMeters() * pUS.mScaleToMeters < 200 && newDistMeterScale < MARK_1000){
			final TurnVoiceElement turnDistance = getTurnVoiceElementFromAngle((int)pUpperNextRouteInstruction.getAngle());
			final DistanceVoiceElement voiceDistance = getCloserDistanceVoiceElement((int)(pRouteInstruction.getLengthMeters() * pUS.mScaleToMeters));
			response.setThenCommand(new SimpleAudibleTurnCommand(voiceDistance, turnDistance));
		}

		/* Check if the same thing has been said before. */
		if(this.LASTAUDIBLETURNCOMMAND != null && this.LASTAUDIBLETURNCOMMAND.equals(response) && this.LASTROUTEINSTRUCTION != null && this.LASTROUTEINSTRUCTION.equals(pRouteInstruction) ){
			this.LASTAUDIBLETURNCOMMAND = response;
			this.LASTROUTEINSTRUCTION = pRouteInstruction;
			return null;
		}else{
			this.LASTAUDIBLETURNCOMMAND = response;
			this.LASTROUTEINSTRUCTION = pRouteInstruction;
			return response;
		}
	}

	public static DistanceVoiceElement getDistanceVoiceElementFromDistances(final int oldDistKilometerScale, final int oldDistMeterScale, final int newDistKilometerScale, final int newDistMeterScale) {
		if (inBounds(newDistKilometerScale, MARK_25000, oldDistKilometerScale)) {
			return DistanceVoiceElement.KM_TWENTY_FIVE;
		} else if (inBounds(newDistKilometerScale, MARK_10000, oldDistKilometerScale)) {
			return DistanceVoiceElement.KM_TEN;
		} else if (inBounds(newDistKilometerScale, MARK_5000, oldDistKilometerScale)) {
			return DistanceVoiceElement.KM_FIVE;
		} else if (inBounds(newDistKilometerScale, MARK_2000, oldDistKilometerScale)) {
			return DistanceVoiceElement.KM_TWO;
		} else if (inBounds(newDistKilometerScale, MARK_1000, oldDistKilometerScale)) {
			return DistanceVoiceElement.KM_ONE;
		} else if (inBounds(newDistMeterScale, MARK_500, oldDistMeterScale)) {
			return DistanceVoiceElement.M_500;
		} else if (inBounds(newDistMeterScale, MARK_200, oldDistMeterScale)) {
			return DistanceVoiceElement.M_200;
		} else if (inBounds(newDistMeterScale, MARK_100, oldDistMeterScale)) {
			return DistanceVoiceElement.M_100;
		} else if (inBounds(newDistMeterScale, MARK_50, oldDistMeterScale)) {
			return DistanceVoiceElement.M_50;
		} else {
			/* We didn't pass any of the 'gates' so nothing needs to be said. */
			return null;
		}
	}

	public static DistanceVoiceElement getCloserDistanceVoiceElement(final int pDistance) {
		if(pDistance < MARK_50) {
			return null;
		} else if(pDistance < MARK_100) {
			return DistanceVoiceElement.M_50;
		} else if(pDistance < MARK_200) {
			return DistanceVoiceElement.M_100;
		} else if(pDistance < MARK_500) {
			return DistanceVoiceElement.M_200;
		} else {
			throw new IllegalArgumentException("getCloserDistanceVoiceElement() should not be called for pDistance > MARK_500 (was: " + pDistance + ")");
		}
	}

	public static TurnVoiceElement getTurnVoiceElementFromAngle(final int pTurnAngle) {
		final TurnVoiceElement tve;
		if (pTurnAngle > MIN_ANGLE_SHARP_LEFT) {
			tve = TurnVoiceElement.LEFT_SHARP;
		} else if (pTurnAngle > MIN_ANGLE_LEFT) {
			tve = TurnVoiceElement.LEFT;
		} else if (pTurnAngle > MIN_ANGLE_BEAR_LEFT) {
			tve = TurnVoiceElement.LEFT_BEAR;
		} else if (pTurnAngle <= MIN_ANGLE_BEAR_LEFT && pTurnAngle >= -MIN_ANGLE_BEAR_RIGHT) {
			tve = TurnVoiceElement.STRAIGHT_ON;
		} else if (pTurnAngle > -MIN_ANGLE_RIGHT) {
			tve = TurnVoiceElement.RIGHT_BEAR;
		} else if (pTurnAngle > -MIN_ANGLE_SHARP_RIGHT) {
			tve = TurnVoiceElement.RIGHT;
		} else {
			tve = TurnVoiceElement.RIGHT_SHARP;
		}
		return tve;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
