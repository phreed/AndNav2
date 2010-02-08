//Created by plusminus on 22:47:16 - 24.02.2008
package org.andnav2.nav;

import java.util.List;

import org.andnav2.adt.AndNavLocation;
import org.andnav2.adt.UnitSystem;
import org.andnav2.adt.voice.AudibleTurnCommand;
import org.andnav2.adt.voice.AudibleTurnCommandManager;
import org.andnav2.adt.voice.DirectionVoiceCommandListener;
import org.andnav2.nav.util.NavAlgorithm;
import org.andnav2.nav.util.Util;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.rs.Route;
import org.andnav2.sys.ors.adt.rs.RouteInstruction;
import org.andnav2.util.constants.Constants;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

public class Navigator implements Constants{

	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final byte OFF_ROUTE = 0;
	public static final byte ON_ROUTE = OFF_ROUTE + 1;
	public static final byte ROUTESTATUS_UNKNOWN = ON_ROUTE + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	private int mCurrentSearchindexCount = NavAlgorithm.BASE_SEARCHINDEX_COUNT;

	//	private int mDebugTickSum = 0;
	//	private int mDebugTickCount = 0;

	private final AudibleTurnCommandManager mAudibleTurnCommandManager = new AudibleTurnCommandManager();

	private boolean mReady = false;
	private boolean mTicking = false;

	/** Setting this to ROUTESTATUS_UNKNOWN
	 * on default will cause
	 * a RouteMissed/Route */
	private byte mOnRouteStatus = ROUTESTATUS_UNKNOWN;

	private AndNavLocation mMyLocation;

	private Route mRoute;

	private GeoPoint mMyProjectedLocationMapPoint;

	private WayPointListener mWayPointListener;
	private OffRouteListener mOffRouteListener;
	private DirectionVoiceCommandListener mDistanceVoiceCommandListener;

	private int mNextRoutePointIndex = NOT_SET;
	private int mNextTurnPointIndex = NOT_SET;
	private int mNextTurnPointIndexInRoute = NOT_SET;
	private int mDistanceToNextTurnPoint = NOT_SET;
	private int mDistanceToDestination = NOT_SET;
	private final long mTickDelay = 0;

	private float mPercentageDone = 0;

	/**
	 * Supposed to hold the Angle of the next turn. <code>0�;</code> straight<br/>
	 * <code>+x�;</code> left turn (<code>0 < x <= 180</code>)<br/>
	 * <code>-x�;</code> right turn (<code>0 > x >= -180</code>)
	 */
	private float mTurnAngle = NOT_SET;

	/** Holds the current navRunner-Thread if one is running. */
	private Thread mNavRunnerThread;

	private UnitSystem mUnitSystem;

	private boolean[] mWaypointsPassed;

	//	private final Context mCtx;

	// ===========================================================
	// Constructors
	// ===========================================================

	public Navigator(final Context ctx, final UnitSystem aUS){
		this.mUnitSystem = aUS;
		//		this.mCtx = ctx;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setRoute(final Route aRoute) {
		this.mRoute = aRoute;
		final List<GeoPoint> vias = aRoute.getVias();
		if(vias != null) {
			this.mWaypointsPassed = new boolean[vias.size()];
		} else {
			this.mWaypointsPassed = new boolean[0];
		}

		this.mNextRoutePointIndex = NOT_SET;
		this.mNextTurnPointIndex = NOT_SET;
		this.mNextTurnPointIndexInRoute = NOT_SET;
		this.mDistanceToNextTurnPoint = NOT_SET;
		this.mDistanceToDestination = NOT_SET;
	}

	public boolean isReady() {
		return this.mReady;
	}

	public void setReady(final boolean b) {
		this.mReady = b;
		this.mOnRouteStatus = ROUTESTATUS_UNKNOWN;
		if (!b) {
			try {
				if (this.mNavRunnerThread != null) {
					this.mNavRunnerThread.interrupt();
				}
			} catch (final Exception e) {
			}
		}
	}

	public boolean isTicking() {
		return this.mTicking;
	}

	public long getTickDelay() {
		return this.mTickDelay;
	}

	public void setUnitSystem(final UnitSystem aUS){
		this.mUnitSystem = aUS;
	}

	public int getDistanceBetweenNextAndUpperNextTurnPoint() {
		if(this.mNextTurnPointIndex == NOT_SET) {
			return Integer.MAX_VALUE;
		}

		final List<RouteInstruction> tps = this.mRoute.getRouteInstructions();

		if(this.mNextTurnPointIndex >= tps.size() - 1) {
			return Integer.MAX_VALUE;
		}

		return tps.get(this.mNextTurnPointIndex).getLengthMeters();
	}

	/**
	 * Using this method causes this Navigator to call the appropriate method of
	 * its OffRouteListener (if set) on the next tick.
	 */
	public void forceOffRouteListenerUpdateInNextTick() {
		this.mOnRouteStatus = ROUTESTATUS_UNKNOWN;
	}

	public void setNextRoutePointIndex(final int newIndex) {
		final int oldIndex = this.mNextRoutePointIndex;
		this.mNextRoutePointIndex = newIndex;

		if (oldIndex == newIndex || this.mWayPointListener == null) {
			return;
		}

		// TODO Vorher warens SubRoutes, jetzt sinds RouteInstructions!
		final List<RouteInstruction> routeInstructions = this.mRoute.getRouteInstructions();
		final int routeInstructionCount = routeInstructions.size();

		boolean changed = false;
		boolean targetReached = false;
		for(int i = 0; i < routeInstructionCount; i++){
			//			Log.d(DEBUGTAG, "inBounds( " + oldIndex + ", " + subroutes[i].getPolylineEndIndex() + ", " + newIndex);
			final RouteInstruction curInstruction = routeInstructions.get(i);
			if(curInstruction.isWaypoint() && waypointPassed(oldIndex, curInstruction.getFirstMotherPolylineIndex(), newIndex)){
				if(i == routeInstructionCount - 1){
					changed = true;
					targetReached = true;
					Log.d(DEBUGTAG, "Target reached.");
				}else if(!this.mWaypointsPassed[i]){
					changed = true;
					this.mWaypointsPassed[i] = true;
					Log.d(DEBUGTAG, "Waypoint " + i + " passed.");
				}
			}
		}
		if(changed){
			final List<GeoPoint> waypoints = this.mRoute.getVias();
			// TODO Target reach sollte nimmer kommen :( *DAMN*
			final int mWaypointsPassedLengthLessOne = this.mWaypointsPassed.length - 1;
			for(int i = mWaypointsPassedLengthLessOne; i >= 0; i--) {
				if(this.mWaypointsPassed[i]) {
					waypoints.remove(i);
				}
			}

			if(targetReached) {
				this.mWayPointListener.onWaypointPassed(waypoints);
			} else {
				this.mWayPointListener.onTargetReached();
			}
		}
	}

	protected void setDistanceToNextTurnPoint(final int newDist) {
		final int oldDist = this.mDistanceToNextTurnPoint;
		this.mDistanceToNextTurnPoint = newDist;

		if (this.mDistanceVoiceCommandListener == null) {
			return;
		}

		final List<RouteInstruction> routeInstructions = this.mRoute.getRouteInstructions();
		final RouteInstruction nextRouteInstruction = routeInstructions.get(this.mNextTurnPointIndex);
		final RouteInstruction upperNextRouteInstruction;
		if(this.mNextRoutePointIndex + 1 < routeInstructions.size()){
			upperNextRouteInstruction = routeInstructions.get(this.mNextTurnPointIndex + 1);
		}else{
			upperNextRouteInstruction = null;
		}
		final AudibleTurnCommand atc = this.mAudibleTurnCommandManager.createIfNeccessary(this.mUnitSystem, nextRouteInstruction, oldDist, newDist, this.mMyLocation.getSpeed(), upperNextRouteInstruction);
		/* If atc is null, nothing should be said. */
		if(atc != null){
			this.mDistanceVoiceCommandListener.onReceiveAudibleTurnCommand(atc);
		}
	}

	protected static boolean waypointPassed(final int iBefore, final int iWaypoint, final int iAfter) {
		return iBefore <= iWaypoint && iWaypoint < iAfter;
	}

	/** Provides the real on-route distance up to the end of the route. */
	public int getTotalRestDistance() {
		return this.mDistanceToDestination;
	}

	/** Provides the real on-route distance up to the next turnpoint. */
	public int getDistanceToNextTurnPoint() {
		return this.mDistanceToNextTurnPoint;
	}

	/**
	 * Get the current GPS-position projected to the closest route-segment .
	 * 
	 * @return current GPS-Position projected to the closest Route-Segment<br />
	 *         or null before the first tick();
	 */
	public GeoPoint getLastKnownLocationProjectedGeoPoint() {
		return this.mMyProjectedLocationMapPoint;
	}

	/**
	 * Provides the angle of the next turn. <code>0�;</code> straight<br/>
	 * <code>+x�;</code> left turn (<code>0 < x <= 180</code>)<br/>
	 * <code>-x�;</code> right turn (<code>0 > x >= -180</code>)
	 */
	public float getTurnAngle() {
		return this.mTurnAngle;
	}

	public int getEstimatedRestSeconds() {
		if (this.mRoute == null) {
			return NOT_SET;
		} else {
			return this.mRoute.getEstimatedRestSeconds(
					this.mNextRoutePointIndex, this.mDistanceToNextTurnPoint);
		}
	}

	/** Provides the index of the next point in the route. */
	public int getNextRoutePointIndex() {
		return this.mNextRoutePointIndex;
	}

	/** Provides the Index of the next RouteInstruction in the RouteInstruction-Array. */
	public int getNextTurnPointIndex() {
		return this.mNextTurnPointIndex;
	}

	/** Provides the Index of the next RouteInstruction within the whole route. */
	public int getNextTurnPointIndexInRoute() {
		return this.mNextTurnPointIndexInRoute;
	}

	/** Provides the percentage of the route, which has already been driven. */
	public float getPercentageDone() {
		return this.mPercentageDone;
	}

	public GeoPoint getNextTurnPointIndexAsGeoPoint() {
		final int nextTurnPointIndex = this.mNextTurnPointIndex;
		if(nextTurnPointIndex < this.mRoute.getRouteInstructions().size()) {
			return this.mRoute.getRouteInstructions().get(nextTurnPointIndex).getTurnPoint();
		} else {
			return null;
		}
	}

	public void setOffTrackListener(final OffRouteListener otl) {
		this.mOffRouteListener = otl;
	}

	public void removeOffTrackListener(final OffRouteListener otl) {
		if (otl != null && otl.equals(this.mOffRouteListener)) {
			this.mOffRouteListener = null;
		}
	}

	public void setWayPointListener(final WayPointListener wpl) {
		this.mWayPointListener = wpl;
	}

	public void removeWayPointListener(final WayPointListener wpl) {
		if (wpl != null && wpl.equals(this.mWayPointListener)) {
			this.mWayPointListener = null;
		}
	}

	public void setDistanceVoiceCommandListener(final DirectionVoiceCommandListener dvcl) {
		this.mDistanceVoiceCommandListener = dvcl;
	}

	public void removeDistanceVoiceCommandListener(
			final DirectionVoiceCommandListener dvcl) {
		if (dvcl != null && dvcl.equals(this.mDistanceVoiceCommandListener)) {
			this.mDistanceVoiceCommandListener = null;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void tick(final AndNavLocation pNewLocation) {
		if (this.mReady && !this.mTicking) {
			if (pNewLocation == null || pNewLocation.equals(this.mMyLocation)) {
				Log.d(DEBUGTAG, "NavTick skipped, because was null or location didn't change.");
			} else if (pNewLocation != null) {
				this.mTicking = true;

				this.mMyLocation = pNewLocation;

				(this.mNavRunnerThread = new Thread(new NavRunner(), "NavRunner-Thread")).start();
			}
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	protected class NavRunner implements Runnable {
		public void run() {
			try {
				//				final long startMs = System.currentTimeMillis();

				/* Store to local field for performance-reasons. */
				final List<GeoPoint> polyLine = Navigator.this.mRoute.getPolyLine();
				int beforeNextIndexRoutePoint = Math.max(0, Navigator.this.mNextRoutePointIndex);
				beforeNextIndexRoutePoint = Math.min(polyLine.size() - 1, beforeNextIndexRoutePoint);

				/*
				 * Convert the current GPS-Location to an
				 * android.Graphics.Point, because its needed in that format
				 * within getDistanceToLine(..).
				 */
				final Point myGPSPositionPoint = Util.geoPoint2Point(Navigator.this.mMyLocation);

				/* All other distances will be smaller than this one. */

				final int indexOfClosest = Math.max(0, NavAlgorithm.getClosestIndex(polyLine, myGPSPositionPoint, beforeNextIndexRoutePoint, Navigator.this.mCurrentSearchindexCount));
				if(indexOfClosest == NOT_SET) {
					return;
				}

				/* Calculate our Location projected onto the route. */
				final int firstIndex = Math.max(0, indexOfClosest - 1);
				final int lastIndex = Math.min(firstIndex + 1, polyLine.size() - 1);
				Navigator.this.mMyProjectedLocationMapPoint = Util.getProjectedGeoPoint(polyLine.get(firstIndex), polyLine.get(lastIndex), myGPSPositionPoint);

				if(Navigator.this.mMyProjectedLocationMapPoint == null) {
					Navigator.this.mMyProjectedLocationMapPoint = polyLine.get(indexOfClosest);
				}

				if(Navigator.this.mMyProjectedLocationMapPoint != null){
					final int distanceToRouteInMeters = Navigator.this.mMyProjectedLocationMapPoint.distanceTo(Navigator.this.mMyLocation);
					/* Check if we are Off the Route. */
					final int horizontalPositioningError = (Navigator.this.mMyLocation.hasHorizontalPositioningError()) ? Navigator.this.mMyLocation.getHorizontalPositioningError() : 0;
					if (NavAlgorithm.DISTANCE_TO_TOGGLE_OFF_ROUTE < distanceToRouteInMeters - horizontalPositioningError) {
						/* Increase number of indexes to search in. */
						Navigator.this.mCurrentSearchindexCount = Math.min(Navigator.this.mCurrentSearchindexCount + 5, NavAlgorithm.MAX_SEARCHINDEX_COUNT);

						/* Check if we were(!) on the Route before (or unset). */
						if (Navigator.this.mOffRouteListener != null) {
							if (Navigator.this.mOnRouteStatus == ON_ROUTE
									|| Navigator.this.mOnRouteStatus == ROUTESTATUS_UNKNOWN) {
								Navigator.this.mOnRouteStatus = OFF_ROUTE;
								Navigator.this.mOffRouteListener.onRouteMissed();
							}
						}
					} else { /* We are on the Route. */
						/* Set the count of indexes to search in to a minimum. */
						Navigator.this.mCurrentSearchindexCount = Math.max(Navigator.this.mCurrentSearchindexCount - 5, NavAlgorithm.BASE_SEARCHINDEX_COUNT);
						/* Check if we were(!) off the Route before (or unset). */
						if (Navigator.this.mOffRouteListener != null) {
							if (Navigator.this.mOnRouteStatus == OFF_ROUTE
									|| Navigator.this.mOnRouteStatus == ROUTESTATUS_UNKNOWN) {
								Navigator.this.mOnRouteStatus = ON_ROUTE;
								Navigator.this.mOffRouteListener.onRouteResumed();
							}
						}
					}
				}
				Navigator.this.setNextRoutePointIndex(indexOfClosest);
				// Log.d(DEBUGTAG, "Distance to Route[" +
				// indexOfMinDistance + "]: " +
				// currentMinDistance);


				/* Determine the next RouteInstruction. */
				/* Make it a local parameter, for performance reasons. */
				final List<RouteInstruction> turnRouteIndizes = Navigator.this.mRoute.getRouteInstructions();

				final int turnPointCount = turnRouteIndizes.size();
				int nextTurnPointIndex = 0;
				for (int i = 0; i < turnPointCount; i++) {
					/* Check if we have already passed turnRouteIndizes[i] */
					if (turnRouteIndizes.get(i).getFirstMotherPolylineIndex() >= indexOfClosest) {
						nextTurnPointIndex = i;
						break;
					}
				}

				final int nextTurnPointIndexInRoute = turnRouteIndizes.get(nextTurnPointIndex).getFirstMotherPolylineIndex();
				Navigator.this.mNextTurnPointIndexInRoute = nextTurnPointIndexInRoute;

				Navigator.this.mNextTurnPointIndex = nextTurnPointIndex;

				/* Calculate the distance to the next Turn Point. */
				/*
				 * First calculate the distance from our current position to the next
				 */
				int distanceToNextTurnPoint = Navigator.this.mMyLocation.distanceTo(polyLine.get(indexOfClosest));
				final int[] segmentLenghts = Navigator.this.mRoute.getRouteSegmentLengths();

				for (int i = indexOfClosest; i < nextTurnPointIndexInRoute; i++) {
					distanceToNextTurnPoint += segmentLenghts[i];
				}

				Navigator.this.setDistanceToNextTurnPoint(distanceToNextTurnPoint);

				final int distanceFromNextTurnPointToDestination = Navigator.this.mRoute.getRouteSegmentLengthsUpToDestination()[nextTurnPointIndexInRoute];

				Navigator.this.mTurnAngle = turnRouteIndizes.get(nextTurnPointIndex).getAngle();

				Navigator.this.mDistanceToDestination = distanceToNextTurnPoint
				+ distanceFromNextTurnPointToDestination;

				Navigator.this.mPercentageDone = ((float) Navigator.this.mDistanceToDestination) / ((float) Navigator.this.mRoute.getDistanceMeters());

				/* DEBUG Output */
				//				final long end = System.currentTimeMillis();
				//				Navigator.this.mTickDelay = end - startMs;
				//
				//				Navigator.this.mDebugTickSum += end - startMs;
				//				Navigator.this.mDebugTickCount++;
				// Log.d(DEBUGTAG, "NavTick: " + (end - startMs) + "
				// ms [Avg: " +
				// (Navigator.this.debugTickSum / Navigator.this.debugTickCount)
				// + " ms]");

			} catch (final Exception e) {
				//				Exceptor.e("Error in NavRunner.run();", e, mCtx);
			}
			/* Finally re-enable upcoming ticks. */
			Navigator.this.mTicking = false;
		}
	}
}
