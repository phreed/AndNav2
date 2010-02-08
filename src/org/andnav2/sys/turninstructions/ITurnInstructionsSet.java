// Created by plusminus on 10:45:01 PM - Apr 4, 2009
package org.andnav2.sys.turninstructions;


public interface ITurnInstructionsSet {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public String getVehicle();
	public String getPedestrian();
	public String getAppoximation();
	public String getDay();
	public String getDays();
	public String getHour();
	public String getHours();
	public String getMinute();
	public String getMinutes();
	public String getSecond();
	public String getSeconds();
	public String getCurve();
	public String getSharpLeft();
	public String getLeft();
	public String getHalfLeft();
	public String getStraightForward();
	public String getHalfRight();
	public String getRight();
	public String getSharpRight();
	public String getUTurn();
	public String getStartTag();
	public String getEndTag();
	public String getOn();
	public String getFor();
	public String getBefore();
	public String getAfter();
	public String getActionNumber();
	public String getThenCommandWithDistance();
	public String getThenCommandWithoutDistance();
	public String getKilometers();
	public String getMeters();
	public String getYards();
	public String getMiles();
}
