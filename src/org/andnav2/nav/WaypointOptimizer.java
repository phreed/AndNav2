//Created by plusminus on 11:34:13 - 25.05.2008
package org.andnav2.nav;

import java.util.ArrayList;
import java.util.List;

import org.andnav2.nav.util.Util;
import org.andnav2.osm.adt.GeoPoint;



public class WaypointOptimizer {
	// ===========================================================
	// Final Fields
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
	/**
	 * 
	 */
	public static boolean optimize(final GeoPoint start, final List<GeoPoint> waypoints, final GeoPoint end){
		if(waypoints.size() < 2) {
			return false;
		}

		int[] best = null;
		long bestLen = Long.MAX_VALUE;

		long curLen = 0;

		final int[] dummy = new int[waypoints.size()];
		// Initiate dummy with simple, ascending indizes
		for(int i = 0; i < dummy.length; i++) {
			dummy[i] = i;
		}

		final int[][] permutations = permutate(dummy);
		for(final int[] p : permutations){
			if((curLen = calcLen(start, waypoints, end, p)) < bestLen){
				bestLen = curLen;
				best = p;
			}
		}

		if(isOriginal(best)) {
			return false;
		}

		final ArrayList<GeoPoint> tmp = new ArrayList<GeoPoint>(waypoints);

		// Bring waypoints into the optimized order.
		for(int i = 0; i < best.length; i++) {
			waypoints.set(i, tmp.get(best[i]));
		}

		return true;
	}

	protected static boolean isOriginal(final int[] arr) {
		for(int i = 0; i < arr.length; i++) {
			if(i != arr[i]) {
				return false;
			}
		}
		return true;
	}

	public static long calcLen(final GeoPoint start, final ArrayList<GeoPoint> waypoints, final GeoPoint end) {
		final int[] p = new int[waypoints.size()];

		// Initiate dummy with simple, ascending indizes
		for(int i = 0; i < p.length; i++) {
			p[i] = i;
		}
		return calcLen(start, waypoints, end, p);
	}

	public static long calcLen(final GeoPoint start, final List<GeoPoint> waypoints, final GeoPoint end, final int[] p) {
		if(waypoints.size() != p.length) {
			throw new IllegalArgumentException();
		}

		long out = start.distanceTo(waypoints.get(p[0]));
		out += end.distanceTo(waypoints.get(p[waypoints.size() - 1]));
		for(int i = 0; i < p.length - 1; i++) {
			out += waypoints.get(p[i]).distanceTo(waypoints.get(p[i+1]));
		}
		
		return out;
	}

	protected static final int[][] permutate(final int[] bucket){
		if(bucket.length == 1) {
			return new int[][]{{bucket[0]}};
		}

		// The number of permutations is:
		// <code>faculty(amount)</code>
		final int facLessOne = faculty(bucket.length - 1);
		final int fac = facLessOne * bucket.length;
		final int[][] out = new int[fac][];

		for(int i = 0; i < bucket.length; i++){ // All except the last one
			// We will leave out one element
			final int except = bucket[i];

			// so we create a sub-array with one size less
			final int[] sub = new int[bucket.length - 1];

			// Copy all values except the one above
			System.arraycopy(bucket, 0, sub, 0, i);
			System.arraycopy(bucket, i + 1, sub, i, bucket.length - i - 1);

			// And permutate the sub-bucket
			final int[][] ret = permutate(sub);

			// Each permutated sub-bucket then needs to be merged with the element we excluded before
			for(int j = 0; j < ret.length; j++){
				final int[] cur = ret[j];
				final int[] merged = new int[bucket.length];
				// Copy all values from the current sub-bucket
				for(int k = 0; k < cur.length; k++) {
					merged[k] = cur[k];
				}

				merged[merged.length - 1] = except;

				// Copy back the merged bucket
				out[i * facLessOne + j] = merged;
			}
		}
		return out;
	}

	public static final int faculty(final int i){
		if(i > 1) {
			return i * faculty(i - 1);
		} else {
			return i;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
