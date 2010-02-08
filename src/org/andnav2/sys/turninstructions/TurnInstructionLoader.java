// Created by plusminus on 10:51:47 PM - Apr 4, 2009
package org.andnav2.sys.turninstructions;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;


public class TurnInstructionLoader {
	// ===========================================================
	// Constants
	// ===========================================================

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

	public static ITurnInstructionsSet load(final XmlResourceParser pXpp) throws XmlPullParserException, IOException {
		final TurnInstructionSet out = new TurnInstructionSet();

		String currentText = "";
		int eventType = pXpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if(eventType == XmlPullParser.END_TAG) {
				final String tagName = pXpp.getName();
				if(tagName.equals("Vehicle")){
					out.setVehicle(currentText);
				} else if(tagName.equals("Pedestrian")){
					out.setPedestrian(currentText);
				} else if(tagName.equals("Vehicle")){
					out.setVehicle(currentText);
				} else if(tagName.equals("Appoximation")){
					out.setApproximation(currentText);
				} else if(tagName.equals("Day")){
					out.setDay(currentText);
				} else if(tagName.equals("Hour")){
					out.setHour(currentText);
				} else if(tagName.equals("Minute")){
					out.setMinute(currentText);
				} else if(tagName.equals("Second")){
					out.setSecond(currentText);
				} else if(tagName.equals("Curve")){
					out.setCurve(currentText);
				} else if(tagName.equals("SharpLeft")){
					out.setSharpLeft(currentText);
				} else if(tagName.equals("Left")){
					out.setLeft(currentText);
				} else if(tagName.equals("HalfLeft")){
					out.setHalfLeft(currentText);
				} else if(tagName.equals("StraightForward")){
					out.setStraightForward(currentText);
				} else if(tagName.equals("HalfRight")){
					out.setHalfRight(currentText);
				} else if(tagName.equals("Right")){
					out.setRight(currentText);
				} else if(tagName.equals("SharpRight")){
					out.setSharpRight(currentText);
				} else if(tagName.equals("ActionNumber")){
					out.setActionNumber(currentText);
				} else if(tagName.equals("StartTag")){
					out.setStartTag(currentText);
				} else if(tagName.equals("EndTag")){
					out.setEndTag(currentText);
				} else if(tagName.equals("On")){
					out.setOn(currentText);
				} else if(tagName.equals("For")){
					out.setFor(currentText);
				} else if(tagName.equals("Before")){
					out.setBefore(currentText);
				} else if(tagName.equals("After")){
					out.setAfter(currentText);
				}   else if(tagName.equals("ThenCommandWithDistance")){
					out.setThenCommandWithDistance(currentText);
				} else if(tagName.equals("ThenCommandWithoutDistance")){
					out.setThenCommandWithoutDistance(currentText);
				} else if(tagName.equals("Meters")){
					out.setMeters(currentText);
				} else if(tagName.equals("Kilometers")){
					out.setKilometers(currentText);
				} else if(tagName.equals("Miles")){
					out.setMiles(currentText);
				} else if(tagName.equals("Yards")){
					out.setYards(currentText);
				}
			} else if(eventType == XmlPullParser.TEXT) {
				currentText = pXpp.getText();
			}
			eventType = pXpp.next();
		}

		return out;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
