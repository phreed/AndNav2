// Created by plusminus on 13:52:12 - 04.02.2009
package org.andnav2.sys.serverdowntime.adt;

import java.util.ArrayList;

import org.andnav2.sys.serverdowntime.exc.DowntimeHasAlreadyPassedException;


public class DowntimeList extends ArrayList<Downtime> {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final long serialVersionUID = -8017661811395542697L;

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

	public void filterPassed(){
		final long now = System.currentTimeMillis();

		for(final Downtime d : this){
			switch(d.getPeriod()){
				case ONCE:
					if(now > d.getStartTimestamp() + d.getDurationSeconds()){
						this.remove(d);
					}
					break;
			}
		}
	}

	public Downtime getNextOccuringDowntimeStartingFrom(final long pStartSearchFrom){
		if(this.size() == 0) {
			return null;
		}

		/* The values holding information on the next downtime. */
		Downtime min = null;
		long minTime = Long.MAX_VALUE;

		for(final Downtime d : this){
			try {
				final long nextOccurence = d.getNextOccurenceStartingFrom(pStartSearchFrom);
				if(minTime > nextOccurence){
					minTime = nextOccurence;
					min = d;
				}
			} catch (final DowntimeHasAlreadyPassedException e) { }
		}

		return min;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
