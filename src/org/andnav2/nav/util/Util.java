// Created by plusminus on 23:51:49 - 24.02.2008
package org.andnav2.nav.util;

import org.andnav2.adt.other.GraphicsPoint;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.util.constants.GeoConstants;
import org.andnav2.util.constants.MathematicalConstants;

import android.graphics.Point;
import android.location.Location;
import android.util.FloatMath;



public class Util implements MathematicalConstants, GeoConstants{
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static boolean inBounds(final int lower, final int val, final int upper) {
		return lower <= val && upper >= val;
	}

	/**
	 * Calculates the distance from a line spanned between two MapPoints to another MapPoint.
	 * This is only the geometric distance,
	 * when the search-point is located like:
	 * P or Q, but not like X,Y or Z:
	 * <pre>
	 * ^
	 * |   Y |   P |
	 * |     |     |
	 * |     A-----B
	 * |     |     |  Z
	 * |  X  |  Q  |
	 * |
	 * 0-----------------></pre>
	 * In cases of X and Y, the distance to A would be returned.
	 * In the case of Z, the distance to B would be returned.
	 * @param linePointA First (Map)Point on the line
	 * @param linePointB Second (Map)Point on the line
	 * @param p Point to determine the distance to the line
	 * @return distance from a Point to a line
	 */
	public static float getDistanceToLine(final GeoPoint linePointA, final GeoPoint linePointB, final Point p){
		/* a: Point A on the line. */
		final Point a = new Point(linePointA.getLongitudeE6(), linePointA.getLatitudeE6());
		/* b: Point B on the line. */
		final Point b = new Point(linePointB.getLongitudeE6(), linePointB.getLatitudeE6());
		return getDistanceToLine(a, b, p);
	}

	public static float getDistanceToLine(final Point a, final Point b, final Point p){

		/* If a is the same point as b then return the distance from p to a. */
		if(a.x == b.x && a.y ==b.y){
			final int dx = p.x - a.x;
			final int dy = p.y - a.y;
			return FloatMath.sqrt(dx*dx + dy*dy);
		}

		/* s is the vector from a to b. */
		final Point s = org.andnav2.adt.other.GraphicsPoint.difference(b, a);
		final float lenght_s = FloatMath.sqrt(((long)s.x) * s.x + ((long)s.y) * s.y);

		/* r is the vector from a to p. */
		final Point r = org.andnav2.adt.other.GraphicsPoint.difference(p, a);

		/* The case when the angle at a is 'overstretched' */
		/* Determine the angle between s and r. */
		final double angleAtA = Math.acos(org.andnav2.adt.other.GraphicsPoint.dotProduct(r, s) / (lenght_s * FloatMath.sqrt(((long)r.x) * r.x + ((long)r.y) * r.y)));
		/* If it is bigger than |90°| return distance from p to a*/
		if(Math.abs(angleAtA) > PI_HALF){
			final int dx = p.x - a.x;
			final int dy = p.y - a.y;
			return FloatMath.sqrt(dx*dx + dy*dy);
		}


		/* Attention: s now points to the other direction! */
		s.negate();

		/* t is the vector from b to p. */
		final Point t = GraphicsPoint.difference(p, b);

		/* The case when the angle at b is 'overstretched' */
		/* Determine the angle between s and r. */
		final double angleAtB = Math.acos(GraphicsPoint.dotProduct(s, t) / (lenght_s * FloatMath.sqrt(((long)t.x * t.x) + ((long)t.y) * t.y)));
		/* If it is bigger than |90°| return distance from p to a*/
		if(Math.abs(angleAtB) > PI_HALF){
			final int dx = p.x - b.x;
			final int dy = p.y - b.y;
			return FloatMath.sqrt(dx*dx + dy*dy);
		}

		/* Check if Point is exactly on the line. */
		if(Double.isNaN(angleAtA)){
			// || Double.isNaN(angleAtB) // NOTE: Not needed because angleAtA would also be NaN !
			return 0.0f;
		}


		/* Calculate the geometric distance.
		 * |(p-a) x b| / |b| */
		return Math.abs(GraphicsPoint.crossProduct(GraphicsPoint.difference(p, a), s)) / FloatMath.sqrt(((long)s.x * s.x) + ((long)s.y) * s.y);
	}

	/**
	 * @param linePointA
	 * @param linePointB
	 * @param p
	 * @return the projected MapPoint if possible. <br/>
	 * <ul>
	 * <li><code>null</code> if no projection is possible</li>
	 * <li>when project ON the line was not possible.</li>
	 * <li>when a == b</li>
	 * </ul>.
	 */
	public static GeoPoint getProjectedGeoPoint(final GeoPoint linePointA, final GeoPoint linePointB, final Point p){

		/* a: Point A on the line. */
		final Point a = new Point(linePointA.getLongitudeE6(), linePointA.getLatitudeE6()); /* Longitude is X, Latitude is Y. */
		/* b: Point B on the line. */
		final Point b = new Point(linePointB.getLongitudeE6(), linePointB.getLatitudeE6()); /* Longitude is X, Latitude is Y. */

		/* If a is the same point as b then return null. */
		if(a.x == b.x && a.y ==b.y) {
			return null;
		}

		/* s is the vector from a to b. */
		final Point s = GraphicsPoint.difference(b, a);
		final float lenght_s = FloatMath.sqrt(((long)s.x) * s.x + ((long)s.y) * s.y);

		/* r is the vector from a to p. */
		final Point r = GraphicsPoint.difference(p, a);

		/* The case when the angle at a is 'overstretched' */
		/* Determine the angle between s and r. */
		final double angleAtA = Math.acos(GraphicsPoint.dotProduct(r, s) / (lenght_s * FloatMath.sqrt(((long)r.x) * r.x + ((long)r.y) * r.y)));
		/* If it is bigger than |90°| return null. */
		if(Math.abs(angleAtA) > PI_HALF) {
			return null;
		} else if(Double.isNaN(angleAtA)) {
			return new GeoPoint(p.y,p.x); /* MapPoint is defined as Latitude(Y),Longitude(X). */
		}


		/* Attention: s now points to the other direction! */
		s.negate();

		/* t is the vector from b to p. */
		final Point t = GraphicsPoint.difference(p, b);

		/* The case when the angle at b is 'overstretched' */
		/* Determine the angle between s and r. */
		final float angleAtB = (float)Math.acos(GraphicsPoint.dotProduct(s, t) / (lenght_s * FloatMath.sqrt(((long)t.x) * t.x + ((long)t.y) * t.y)));
		/* If it is bigger than |90°| return b*/
		if(Math.abs(angleAtB) > PI_HALF) {
			return null;
			// NOTE: Not needed because angleAtA would also be NaN !
			//		else if(Double.isNaN(angleAtB))
			//			return new MapPoint(p.x,p.y);
		}

		/* Attention: s now points back to the original direction! */
		s.negate();

		/* Do the actual projection */
		/* First: Calculate the geometric distance.
		 * |(p-a) x b| / |b| */
		final float distance = GraphicsPoint.crossProduct(GraphicsPoint.difference(p, a), s) / FloatMath.sqrt(((long)s.x * s.x) + ((long)s.y) * s.y);

		/* Calculate the gradient of the line from a to be (what already is 's'). */
		final float angleOfOrthogonalRad = (float)Math.atan2(- s.x, s.y);

		/* NOTE: MapPoint is defined as Latitude(Y),Longitude(X). */
		return new GeoPoint(p.y - Math.round(distance * FloatMath.sin(angleOfOrthogonalRad)),
				p.x - Math.round(distance * (float)Math.cos(angleOfOrthogonalRad)));
	}

	public static Point geoPoint2Point(final GeoPoint aGP) {
		return new Point(aGP.getLongitudeE6(), aGP.getLatitudeE6());
	}

	/** Converts an {@link Location} to an {@link Point}. */
	public static Point location2Point(final Location aLocation){
		return new Point((int) (aLocation.getLongitude() * 1E6),
				(int) (aLocation.getLatitude() * 1E6));
	}

	/** Converts an {@link Location} to a {@link GeoPoint}. */
	public static GeoPoint location2GeoPoint(final Location aLocation){
		return new GeoPoint((int) (aLocation.getLatitude() * 1E6),
				(int) (aLocation.getLongitude() * 1E6));
	}

	/**
	 * Calculates the bearing of the two Locations supplied and returns the
	 * Angle in the following (GPS-likely) manner: <br />
	 * <code>N:0°, E:90°, S:180°, W:270°</code>
	 */
	public static float calculateBearing(final GeoPoint before, final GeoPoint after) {
		final Point pBefore = geoPoint2Point(before);
		final Point pAfter = geoPoint2Point(after);

		final float res = -(float) (Math.atan2(pAfter.y - pBefore.y, pAfter.x
				- pBefore.x) * 180 / PI) + 90.0f;

		if (res < 0) {
			return res + 360.0f;
		} else {
			return res;
		}
	}

	/**
	 * Calculates the bearing of the two Locations supplied and returns the
	 * Angle in the following (GPS-likely) manner: <br />
	 * <code>N:0°, E:90°, S:180°, W:270°</code>
	 */
	public static float calculateBearing(final Location before, final Location after) {
		final Point pBefore = location2Point(before);
		final Point pAfter = location2Point(after);

		final float res = -(float) (Math.atan2(pAfter.y - pBefore.y, pAfter.x
				- pBefore.x) * 180 / PI) + 90.0f;

		if (res < 0) {
			return res + 360.0f;
		} else {
			return res;
		}
	}

	/**
	 * Calculates the bearing of the two Locations supplied and returns the
	 * Angle in mathematical manner: <br />
	 * <code>Right: 0° ; Up: 90°; Left: 180°, Down: 270°</code>
	 */
	public static float calculateBearing(final Point coordsA, final Point coordsB) {
		// !!!!!! UNTESTED !!!!!!
		final float res = (float) (Math.atan2(coordsA.y - coordsB.y, coordsA.x - coordsB.x) * 180 / PI);

		if (res < 0) {
			return res + 360.0f;
		} else {
			return res;
		}
	}

	/**
	 * @deprecated Method is probably not correct !!!
	 * @param geoPointA
	 * @param geoPointB
	 * @return
	 */
	@Deprecated
	public static int distanceSquared(final GeoPoint geoPointA, final GeoPoint geoPointB) {
		final int dx = geoPointA.getLongitudeE6() - geoPointB.getLongitudeE6();
		final int dy = geoPointA.getLatitudeE6() - geoPointB.getLatitudeE6();
		return dx*dx + dy*dy;
	}
}
