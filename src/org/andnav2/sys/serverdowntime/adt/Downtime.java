// Created by plusminus on 09:58:34 - 04.02.2009
package org.andnav2.sys.serverdowntime.adt;

import java.text.ParseException;

import junit.framework.Assert;

import org.andnav2.sys.serverdowntime.exc.DowntimeHasAlreadyPassedException;
import org.andnav2.util.TimeUtils;
import org.andnav2.util.constants.TimeConstants;


public class Downtime implements Comparable<Downtime>, TimeConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final Period mPeriod;
	private final int mDurationSeconds;
	private final long mStartTimestamp;
	private final String mDescription;

	// ===========================================================
	// Constructors
	// ===========================================================

	public Downtime(final String pPeriodString, final String pDurationString, final String pStartTimeString, final String pDescription) throws ParseException {
		this(Period.fromName(pPeriodString), TimeUtils.durationTimeString(pDurationString), TimeUtils.convertUTCStringToTimestamp(pStartTimeString), pDescription);
	}

	public Downtime(final Period pPeriod, final int pDurationSeconds, final long pStartTimestamp, final String pDescription) {
		this.mPeriod = pPeriod;
		this.mDurationSeconds = pDurationSeconds;
		this.mStartTimestamp = pStartTimestamp;
		this.mDescription = pDescription;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Period getPeriod() {
		return this.mPeriod;
	}

	public int getDurationSeconds() {
		return this.mDurationSeconds;
	}

	public long getStartTimestamp() {
		return this.mStartTimestamp;
	}

	public String getDescription() {
		return this.mDescription;
	}

	public long getNextOccurenceStartingFrom(final long pStartSearchFrom) throws DowntimeHasAlreadyPassedException {
		/* Once occurring downtimes that have passed. */
		if(this.mPeriod == Period.ONCE && pStartSearchFrom > this.mStartTimestamp + this.mDurationSeconds) {
			throw new DowntimeHasAlreadyPassedException();
		}

		/* Future starting Downtimes. */
		if(this.mStartTimestamp > pStartSearchFrom) {
			return this.mStartTimestamp;
		}

		final long periodLengthMilliseconds;
		/* Convert the Period from seconds to milliseconds. */
		switch(this.mPeriod){
			case ANUAL:
				periodLengthMilliseconds = SECONDSPERYEAR * 1000;
				break;
			case MONTHLY:
				periodLengthMilliseconds = SECONDSPERMONTH * 1000;
				break;
			case WEEKLY:
				periodLengthMilliseconds = SECONDSPERWEEK * 1000;
				break;
			case DAILY:
				periodLengthMilliseconds = SECONDSPERDAY * 1000;
				break;
			default:
				throw new DowntimeHasAlreadyPassedException();
		}

		/* Calculate the startime of the period directly before pStartSearchFrom. */
		final long diffMilliSeconds = pStartSearchFrom - this.mStartTimestamp;

		final int periodsPassed = (int)(diffMilliSeconds / periodLengthMilliseconds);
		final long startTimeOfPeriodBeforeSearchFrom = this.mStartTimestamp + periodLengthMilliseconds * periodsPassed;

		Assert.assertTrue(pStartSearchFrom > startTimeOfPeriodBeforeSearchFrom);

		/* Check if pStartSearchFrom is inside that period. */
		if(pStartSearchFrom < startTimeOfPeriodBeforeSearchFrom + this.mDurationSeconds) {
			return startTimeOfPeriodBeforeSearchFrom;
		}

		/* Otherwise return the next period start. */
		return startTimeOfPeriodBeforeSearchFrom + periodLengthMilliseconds;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public int compareTo(final Downtime another) {
		return (int)(this.mStartTimestamp - another.mStartTimestamp);
	}

	@Override
	public String toString() {
		return new StringBuilder()
		.append("Period: ")
		.append(this.mPeriod.name())
		.append("   Timestamp: ")
		.append(TimeUtils.convertTimestampToUTCString(this.mStartTimestamp))
		.append("   Duration: ")
		.append(this.mDurationSeconds)
		.append("   Description: ")
		.append(this.mDescription).toString();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
