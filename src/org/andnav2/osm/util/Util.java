// Created by plusminus on 13:24:05 - 21.09.2008
package org.andnav2.osm.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.andnav2.osm.util.constants.OSMConstants;

public class Util implements OSMConstants {
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

	/**
	 * May crash when External-Media is not mounted.
	 * @return path, like <code>"/sdcard/andnav2/"</code> always ending with a <code>"/"</code>
	 */
	public static final String getAndNavExternalStoragePath(){
		final String absoluteExternalPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
		if(absoluteExternalPath.endsWith("/")) {
			return absoluteExternalPath + BASEPATH_ON_EXTERNAL_MEDIA;
		} else {
			return absoluteExternalPath + "/" + BASEPATH_ON_EXTERNAL_MEDIA;
		}
	}


	/**
	 * Tests if the line segment from (X1,&nbsp;Y1) to (X2,&nbsp;Y2) intersects
	 * the line segment from (X3,&nbsp;Y3) to (X4,&nbsp;Y4).
	 * 
	 * @param X1
	 *            ,&nbsp;Y1 the coordinates of the beginning of the first
	 *            specified line segment
	 * @param X2
	 *            ,&nbsp;Y2 the coordinates of the end of the first specified
	 *            line segment
	 * @param X3
	 *            ,&nbsp;Y3 the coordinates of the beginning of the second
	 *            specified line segment
	 * @param X4
	 *            ,&nbsp;Y4 the coordinates of the end of the second specified
	 *            line segment
	 * @return <code>true</code> if the first specified line segment and the
	 *         second specified line segment intersect each other;
	 *         <code>false</code> otherwise.
	 */
	public static boolean linesIntersect(final int X1, final int Y1, final int X2, final int Y2,
			final int X3, final int Y3, final int X4, final int Y4) {
		return ((relativeCCW(X1, Y1, X2, Y2, X3, Y3)
				* relativeCCW(X1, Y1, X2, Y2, X4, Y4) <= 0) && (relativeCCW(X3,
						Y3, X4, Y4, X1, Y1)
						* relativeCCW(X3, Y3, X4, Y4, X2, Y2) <= 0));
	}

	/**
	 * Returns an indicator of where the specified point (PX,&nbsp;PY) lies with
	 * respect to the line segment from (X1,&nbsp;Y1) to (X2,&nbsp;Y2). The
	 * return value can be either 1, -1, or 0 and indicates in which direction
	 * the specified line must pivot around its first endpoint, (X1,&nbsp;Y1),
	 * in order to point at the specified point (PX,&nbsp;PY).
	 * <p>
	 * A return value of 1 indicates that the line segment must turn in the
	 * direction that takes the positive X axis towards the negative Y axis. In
	 * the default coordinate system used by Java 2D, this direction is
	 * counterclockwise.
	 * <p>
	 * A return value of -1 indicates that the line segment must turn in the
	 * direction that takes the positive X axis towards the positive Y axis. In
	 * the default coordinate system, this direction is clockwise.
	 * <p>
	 * A return value of 0 indicates that the point lies exactly on the line
	 * segment. Note that an indicator value of 0 is rare and not useful for
	 * determining colinearity because of floating point rounding issues.
	 * <p>
	 * If the point is colinear with the line segment, but not between the
	 * endpoints, then the value will be -1 if the point lies
	 * "beyond (X1,&nbsp;Y1)" or 1 if the point lies "beyond (X2,&nbsp;Y2)".
	 * 
	 * @param X1
	 *            ,&nbsp;Y1 the coordinates of the beginning of the specified
	 *            line segment
	 * @param X2
	 *            ,&nbsp;Y2 the coordinates of the end of the specified line
	 *            segment
	 * @param PX
	 *            ,&nbsp;PY the coordinates of the specified point to be
	 *            compared with the specified line segment
	 * @return an integer that indicates the position of the third specified
	 *         coordinates with respect to the line segment formed by the first
	 *         two specified coordinates.
	 */
	private static int relativeCCW(final int X1, final int Y1, int X2, int Y2, int PX,
			int PY) {
		X2 -= X1;
		Y2 -= Y1;
		PX -= X1;
		PY -= Y1;
		int ccw = PX * Y2 - PY * X2;
		if (ccw == 0) {
			// The point is colinear, classify based on which side of
			// the segment the point falls on. We can calculate a
			// relative value using the projection of PX,PY onto the
			// segment - a negative value indicates the point projects
			// outside of the segment in the direction of the particular
			// endpoint used as the origin for the projection.
			ccw = PX * X2 + PY * Y2;
			if (ccw > 0) {
				// Reverse the projection to be relative to the original X2,Y2
				// X2 and Y2 are simply negated.
				// PX and PY need to have (X2 - X1) or (Y2 - Y1) subtracted
				// from them (based on the original values)
				// Since we really want to get a positive answer when the
				// point is "beyond (X2,Y2)", then we want to calculate
				// the inverse anyway - thus we leave X2 & Y2 negated.
				PX -= X2;
				PY -= Y2;
				ccw = PX * X2 + PY * Y2;
				if (ccw < 0) {
					ccw = 0;
				}
			}
		}
		return (ccw < 0) ? -1 : ((ccw > 0) ? 1 : 0);
	}

	/**
	 * 
	 * @param pOrderedPoints Ordered means that the points are placed as if they are a line.
	 */
	public static void smoothLine(final Set<ValuePair> pPointsInLineorder){
		ValuePair prev = null;
		ValuePair cur;

		/* We want only uniqe new values. */
		final Set<ValuePair> newPoints = new TreeSet<ValuePair>(new Comparator<ValuePair>(){
			public int compare(final ValuePair a, final ValuePair b) {
				return a.compareTo(b);
			}
		});

		for(final Iterator<ValuePair> lineIterator = pPointsInLineorder.iterator(); lineIterator.hasNext(); ) {
			cur = lineIterator.next();

			if(prev != null){
				final int difA = cur.a - prev.a;
				final int difB = cur.b - prev.b;
				/* Check if we have a diagonal jump, like: (See X's)
				 * 
				 *      X----
				 * ----X
				 * 
				 * */
				if(Math.abs(difA) == 1 && Math.abs(difB) == 1){
					/* Add two new points (See O's)
					 * 
					 *     0X----
					 * ----X0
					 * 
					 */
					newPoints.add(new ValuePair(cur.a, prev.b));
					newPoints.add(new ValuePair(prev.a, cur.b));
				}
			}

			prev = cur;
		}

		pPointsInLineorder.addAll(newPoints);
	}

	/**
	 * @see http://www.cs.unc.edu/~mcmillan/comp136/Lecture6/Lines.html
	 */
	public static void rasterLine(int x0, int y0, int x1, int y1, final PixelSetter raster) {
		int dy = y1 - y0;
		int dx = x1 - x0;
		int stepx, stepy;

		if (dy < 0) {
			dy = -dy;
			stepy = -1;
		} else {
			stepy = 1;
		}
		if (dx < 0) {
			dx = -dx;
			stepx = -1;
		} else {
			stepx = 1;
		}

		raster.setPixel(x0, y0);
		raster.setPixel(x1, y1);
		if (dx > dy) {
			final int length = (dx - 1) >> 2;
		final int extras = (dx - 1) & 3;
		final int incr2 = (dy << 2) - (dx << 1);
		if (incr2 < 0) {
			final int c = dy << 1;
			final int incr1 = c << 1;
			int d = incr1 - dx;
			for (int i = 0; i < length; i++) {
				x0 += stepx;
				x1 -= stepx;
				if (d < 0) { 									// Pattern:
					raster.setPixel(x0, y0); 					//
					raster.setPixel(x0 += stepx, y0); 			// x o o
					raster.setPixel(x1, y1); 					//
					raster.setPixel(x1 -= stepx, y1);
					d += incr1;
				} else {
					if (d < c) { 									// Pattern:
						raster.setPixel(x0, y0); 					// o
						raster.setPixel(x0 += stepx, y0 += stepy); 	// x o
						raster.setPixel(x1, y1); 					//
						raster.setPixel(x1 -= stepx, y1 -= stepy);
					} else {
						raster.setPixel(x0, y0 += stepy); 			// Pattern:
						raster.setPixel(x0 += stepx, y0); 			// o o
						raster.setPixel(x1, y1 -= stepy); 			// x
						raster.setPixel(x1 -= stepx, y1); 			//
					}
					d += incr2;
				}
			}
			if (extras > 0) {
				if (d < 0) {
					raster.setPixel(x0 += stepx, y0);
					if (extras > 1) {
						raster.setPixel(x0 += stepx, y0);
					}
					if (extras > 2) {
						raster.setPixel(x1 -= stepx, y1);
					}
				} else if (d < c) {
					raster.setPixel(x0 += stepx, y0);
					if (extras > 1) {
						raster.setPixel(x0 += stepx, y0 += stepy);
					}
					if (extras > 2) {
						raster.setPixel(x1 -= stepx, y1);
					}
				} else {
					raster.setPixel(x0 += stepx, y0 += stepy);
					if (extras > 1) {
						raster.setPixel(x0 += stepx, y0);
					}
					if (extras > 2) {
						raster.setPixel(x1 -= stepx, y1 -= stepy);
					}
				}
			}
		} else {
			final int c = (dy - dx) << 1;
			final int incr1 = c << 1;
			int d = incr1 + dx;
			for (int i = 0; i < length; i++) {
				x0 += stepx;
				x1 -= stepx;
				if (d > 0) {
					raster.setPixel(x0, y0 += stepy); 				// Pattern:
					raster.setPixel(x0 += stepx, y0 += stepy); 		// o
					raster.setPixel(x1, y1 -= stepy); 				// o
					raster.setPixel(x1 -= stepx, y1 -= stepy); 		// x
					d += incr1;
				} else {
					if (d < c) {
						raster.setPixel(x0, y0); 						// Pattern:
						raster.setPixel(x0 += stepx, y0 += stepy); 		// o
						raster.setPixel(x1, y1); 						// x o
						raster.setPixel(x1 -= stepx, y1 -= stepy);		//
					} else {
						raster.setPixel(x0, y0 += stepy); 		// Pattern:
						raster.setPixel(x0 += stepx, y0); 		// o o
						raster.setPixel(x1, y1 -= stepy); 		// x
						raster.setPixel(x1 -= stepx, y1); 		//
					}
					d += incr2;
				}
			}
			if (extras > 0) {
				if (d > 0) {
					raster.setPixel(x0 += stepx, y0 += stepy);
					if (extras > 1) {
						raster.setPixel(x0 += stepx, y0 += stepy);
					}
					if (extras > 2) {
						raster.setPixel(x1 -= stepx, y1 -= stepy);
					}
				} else if (d < c) {
					raster.setPixel(x0 += stepx, y0);
					if (extras > 1) {
						raster.setPixel(x0 += stepx, y0 += stepy);
					}
					if (extras > 2) {
						raster.setPixel(x1 -= stepx, y1);
					}
				} else {
					raster.setPixel(x0 += stepx, y0 += stepy);
					if (extras > 1) {
						raster.setPixel(x0 += stepx, y0);
					}
					if (extras > 2) {
						if (d > c) {
							raster.setPixel(x1 -= stepx, y1 -= stepy);
						} else {
							raster.setPixel(x1 -= stepx, y1);
						}
					}
				}
			}
		}
		} else {
			final int length = (dy - 1) >> 2;
		final int extras = (dy - 1) & 3;
		final int incr2 = (dx << 2) - (dy << 1);
		if (incr2 < 0) {
			final int c = dx << 1;
			final int incr1 = c << 1;
			int d = incr1 - dy;
			for (int i = 0; i < length; i++) {
				y0 += stepy;
				y1 -= stepy;
				if (d < 0) {
					raster.setPixel(x0, y0);
					raster.setPixel(x0, y0 += stepy);
					raster.setPixel(x1, y1);
					raster.setPixel(x1, y1 -= stepy);
					d += incr1;
				} else {
					if (d < c) {
						raster.setPixel(x0, y0);
						raster.setPixel(x0 += stepx, y0 += stepy);
						raster.setPixel(x1, y1);
						raster.setPixel(x1 -= stepx, y1 -= stepy);
					} else {
						raster.setPixel(x0 += stepx, y0);
						raster.setPixel(x0, y0 += stepy);
						raster.setPixel(x1 -= stepx, y1);
						raster.setPixel(x1, y1 -= stepy);
					}
					d += incr2;
				}
			}
			if (extras > 0) {
				if (d < 0) {
					raster.setPixel(x0, y0 += stepy);
					if (extras > 1) {
						raster.setPixel(x0, y0 += stepy);
					}
					if (extras > 2) {
						raster.setPixel(x1, y1 -= stepy);
					}
				} else if (d < c) {
					raster.setPixel(stepx, y0 += stepy);
					if (extras > 1) {
						raster.setPixel(x0 += stepx, y0 += stepy);
					}
					if (extras > 2) {
						raster.setPixel(x1, y1 -= stepy);
					}
				} else {
					raster.setPixel(x0 += stepx, y0 += stepy);
					if (extras > 1) {
						raster.setPixel(x0, y0 += stepy);
					}
					if (extras > 2) {
						raster.setPixel(x1 -= stepx, y1 -= stepy);
					}
				}
			}
		} else {
			final int c = (dx - dy) << 1;
			final int incr1 = c << 1;
			int d = incr1 + dy;
			for (int i = 0; i < length; i++) {
				y0 += stepy;
				y1 -= stepy;
				if (d > 0) {
					raster.setPixel(x0 += stepx, y0);
					raster.setPixel(x0 += stepx, y0 += stepy);
					raster.setPixel(x1 -= stepy, y1);
					raster.setPixel(x1 -= stepx, y1 -= stepy);
					d += incr1;
				} else {
					if (d < c) {
						raster.setPixel(x0, y0);
						raster.setPixel(x0 += stepx, y0 += stepy);
						raster.setPixel(x1, y1);
						raster.setPixel(x1 -= stepx, y1 -= stepy);
					} else {
						raster.setPixel(x0 += stepx, y0);
						raster.setPixel(x0, y0 += stepy);
						raster.setPixel(x1 -= stepx, y1);
						raster.setPixel(x1, y1 -= stepy);
					}
					d += incr2;
				}
			}
			if (extras > 0) {
				if (d > 0) {
					raster.setPixel(x0 += stepx, y0 += stepy);
					if (extras > 1) {
						raster.setPixel(x0 += stepx, y0 += stepy);
					}
					if (extras > 2) {
						raster.setPixel(x1 -= stepx, y1 -= stepy);
					}
				} else if (d < c) {
					raster.setPixel(x0, y0 += stepy);
					if (extras > 1) {
						raster.setPixel(x0 += stepx, y0 += stepy);
					}
					if (extras > 2) {
						raster.setPixel(x1, y1 -= stepy);
					}
				} else {
					raster.setPixel(x0 += stepx, y0 += stepy);
					if (extras > 1) {
						raster.setPixel(x0, y0 += stepy);
					}
					if (extras > 2) {
						if (d > c) {
							raster.setPixel(x1 -= stepx, y1 -= stepy);
						} else {
							raster.setPixel(x1, y1 -= stepy);
						}
					}
				}
			}
		}
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface PixelSetter {
		public void setPixel(final int x, final int y);
	}
}
