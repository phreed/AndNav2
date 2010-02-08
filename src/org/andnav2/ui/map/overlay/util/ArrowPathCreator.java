// Created by plusminus on 15:00:17 - 23.11.2008
package org.andnav2.ui.map.overlay.util;

import java.util.List;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;
import org.andnav2.preferences.PreferenceConstants;
import org.andnav2.util.constants.Constants;
import org.andnav2.util.constants.GeoConstants;
import org.andnav2.util.constants.MathematicalConstants;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Cap;
import android.util.FloatMath;


public class ArrowPathCreator implements MathematicalConstants, PreferenceConstants, GeoConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int HUD_ARROW_MIDDLETOPEAK = 13;
	private static final int HUD_ARROW_MIDDLETOBOTTOM = 13;
	private static final int HUD_ARROW_MIDDLETOSIDE = 13;
	private static final int HUD_ARROW_SIDETOMIDDLE_OPEN = 5;

	private static final int TURNARROW_SEGMENT_LENGTH_PX = 30;
	private static final int TURNARROW_SEGMENT_HALF_LENGTH_PX = TURNARROW_SEGMENT_LENGTH_PX / 2;

	private static final Paint pathTurnSegmentPeakPaint;
	private static final Paint pathTurnSegmentPeakOutlinePaint;
	private static final Paint pathTurnSegmentPaint;
	private static final Paint pathTurnSegmentOutlinePaint;

	static{
		pathTurnSegmentPaint = new Paint();
		pathTurnSegmentPaint.setARGB(255, 255, 255, 255); // white
		pathTurnSegmentPaint.setStrokeWidth(8);
		pathTurnSegmentPaint.setStyle(Paint.Style.STROKE);
		pathTurnSegmentPaint.setStrokeCap(Cap.ROUND);

		pathTurnSegmentOutlinePaint = new Paint(pathTurnSegmentPaint);
		pathTurnSegmentOutlinePaint.setARGB(255, 0, 0, 0); // black
		pathTurnSegmentOutlinePaint.setStrokeWidth(pathTurnSegmentPaint.getStrokeWidth()+2);

		pathTurnSegmentPeakPaint = new Paint(pathTurnSegmentPaint);
		pathTurnSegmentPeakPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		pathTurnSegmentPeakPaint.setStrokeWidth(1);

		pathTurnSegmentPeakOutlinePaint = new Paint(pathTurnSegmentOutlinePaint);
		pathTurnSegmentPeakOutlinePaint.setStyle(Paint.Style.STROKE);
		pathTurnSegmentPeakOutlinePaint.setStrokeWidth(pathTurnSegmentPeakPaint.getStrokeWidth()+2);


		pathTurnSegmentPaint.setAntiAlias(true);
		pathTurnSegmentOutlinePaint.setAntiAlias(true);
		pathTurnSegmentPeakPaint.setAntiAlias(true);
		pathTurnSegmentPeakOutlinePaint.setAntiAlias(true);

		pathTurnSegmentPaint.setPathEffect(new CornerPathEffect(pathTurnSegmentPaint.getStrokeWidth() / 2));
		pathTurnSegmentOutlinePaint.setPathEffect(new CornerPathEffect(pathTurnSegmentOutlinePaint.getStrokeWidth() / 2));
		pathTurnSegmentPeakPaint.setPathEffect(new CornerPathEffect(pathTurnSegmentPeakPaint.getStrokeWidth() / 2));
		pathTurnSegmentPeakOutlinePaint.setPathEffect(new CornerPathEffect(pathTurnSegmentPeakOutlinePaint.getStrokeWidth() / 2));
	}

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public static Bitmap drawToBitmap(final Path arrowPath, final Path arrowPeakPath){
		final Bitmap b = Bitmap.createBitmap(48, 48, Config.ARGB_8888);
		final Canvas c = new Canvas(b);

		final RectF boundsPath = new RectF();
		arrowPath.computeBounds(boundsPath, true);
		final RectF boundsOverall = new RectF();
		arrowPath.computeBounds(boundsOverall, true);

		boundsOverall.bottom = Math.max(boundsOverall.bottom, boundsPath.bottom);
		boundsOverall.top = Math.min(boundsOverall.top, boundsPath.top);
		boundsOverall.left = Math.min(boundsOverall.left, boundsPath.left);
		boundsOverall.right = Math.max(boundsOverall.right, boundsPath.right);

		final float width = boundsOverall.right - boundsOverall.left;
		final float height = boundsOverall.top - boundsOverall.bottom;
		final float biggerSide = Math.max(width, height);

		final Matrix m = new Matrix();
		final int PADDING = 6;
		m.postTranslate(-boundsOverall.left + PADDING, -boundsOverall.top + PADDING); // 2px padding
		final float scaleFactor = (48 - (2*PADDING)) / biggerSide;
		m.postScale(scaleFactor, scaleFactor);


		arrowPath.transform(m);
		arrowPeakPath.transform(m);

		c.drawPath(arrowPath, pathTurnSegmentOutlinePaint);
		c.drawPath(arrowPath, pathTurnSegmentPaint);

		c.drawPath(arrowPeakPath, pathTurnSegmentPeakOutlinePaint);
		c.drawPath(arrowPeakPath, pathTurnSegmentPeakPaint);
		return b;
	}

	public static void createArrowOverIndex(final OSMMapViewProjection pj, final int indexOfArrow, final List<GeoPoint> polyLine, final Path pathTurnSegment, final Path pathTurnSegmentPeak, final float aScaleFactor, final int zoomLevel, final float turnAngle) throws IndexOutOfBoundsException{
		final Point screenCoords = new Point();
		final Point screenCoordsBefore = new Point();

		final int lengthDesiredPerSide = (int)(TURNARROW_SEGMENT_HALF_LENGTH_PX / aScaleFactor);
		final int polyLineLengthLessOne = polyLine.size() - 1;

		/* Compute the first index to be used for the arrow. */
		int startIndex = Math.max(0, indexOfArrow);
		final float baseRestLen = lengthDesiredPerSide * (Math.max(1, (zoomLevel - 8)/ 2.5f));  /* length of the arrow increases as we get closer.*/
		float restLen = baseRestLen;

		GeoPoint mpCur = polyLine.get(startIndex);
		pj.toPixels(mpCur, screenCoords);

		float lastLen = 0;
		/* While restLen not < 0 include the next element. */
		while(startIndex > 0 && restLen > 0){
			startIndex--;
			/* calculate length from 'startIndex' to 'startIndex + 1' */

			screenCoordsBefore.x = screenCoords.x;
			screenCoordsBefore.y = screenCoords.y;

			mpCur = polyLine.get(startIndex);
			pj.toPixels(mpCur, screenCoords);

			lastLen = FloatMath.sqrt(calculateScreenCoordDistanceSquared(screenCoords, screenCoordsBefore));

			restLen -= lastLen;
		}

		final int shortenFirstTo = (int)Math.max(1,lastLen+restLen);


		/* Compute the last index to be used for the arrow. */
		int endIndex = indexOfArrow;
		restLen = baseRestLen / (float)Math.max(1, Math.pow(2, 14 - zoomLevel));

		mpCur = polyLine.get(endIndex);
		pj.toPixels(mpCur, screenCoords);

		float lastAngle = Constants.NOT_SET;
		lastLen = 0;
		while(endIndex < polyLineLengthLessOne && restLen > 0){
			endIndex++;
			/* calculate length from 'startIndex' to 'startIndex + 1' */

			screenCoordsBefore.x = screenCoords.x;
			screenCoordsBefore.y = screenCoords.y;

			mpCur = polyLine.get(endIndex);
			pj.toPixels(mpCur, screenCoords);

			final float angle = org.andnav2.nav.util.Util.calculateBearing(screenCoordsBefore, screenCoords) - 90.0f;

			if(lastAngle != Constants.NOT_SET && Math.abs(angle - lastAngle) > 30.0f){
				restLen = 0;
				endIndex--;
				break;
			}else{
				lastLen = FloatMath.sqrt(calculateScreenCoordDistanceSquared(screenCoords, screenCoordsBefore));
				restLen -= lastLen;
			}

			lastAngle = angle;
		}

		final int shortenLastTo = (int)Math.max(1, lastLen+restLen);

		/* Create the path... */

		mpCur = polyLine.get(startIndex);
		pj.toPixels(mpCur, screenCoordsBefore);

		mpCur = polyLine.get(startIndex+1);
		pj.toPixels(mpCur, screenCoords);

		/* First segment needs to get shrinked. */
		shortenToDistance(screenCoords, screenCoordsBefore, shortenFirstTo, false);

		pathTurnSegment.moveTo(screenCoordsBefore.x, screenCoordsBefore.y);
		pathTurnSegment.lineTo(screenCoords.x, screenCoords.y);

		for(int i = startIndex + 1; i < endIndex; i++){
			mpCur = polyLine.get(i);
			pj.toPixels(mpCur, screenCoords);

			/* Add the onTurn-Point as second. */
			pathTurnSegment.lineTo(screenCoords.x, screenCoords.y);
		}

		screenCoordsBefore.x = screenCoords.x;
		screenCoordsBefore.y = screenCoords.y;

		mpCur = polyLine.get(endIndex);
		pj.toPixels(mpCur, screenCoords);

		/* Determine the Angle the peak of the turn-arrow will point to, BEFORE shrinking. */
		final float endAngle = org.andnav2.nav.util.Util.calculateBearing(screenCoordsBefore, screenCoords) - 90.0f;

		/* First segment needs to get shrinked. */
		shortenToDistance(screenCoordsBefore, screenCoords, shortenLastTo, false);

		/* Add the onTurn-Point as last. */
		pathTurnSegment.lineTo(screenCoords.x, screenCoords.y);

		pathTurnSegmentPeak.moveTo(screenCoords.x - HUD_ARROW_SIDETOMIDDLE_OPEN / aScaleFactor, screenCoords.y + HUD_ARROW_MIDDLETOBOTTOM / aScaleFactor); // Start of the line
		pathTurnSegmentPeak.lineTo(screenCoords.x - HUD_ARROW_MIDDLETOSIDE / aScaleFactor, screenCoords.y + HUD_ARROW_MIDDLETOBOTTOM / aScaleFactor); // Start at the lower left edge
		pathTurnSegmentPeak.lineTo(screenCoords.x, screenCoords.y - HUD_ARROW_MIDDLETOPEAK / aScaleFactor); // Peak of the triangle
		pathTurnSegmentPeak.lineTo(screenCoords.x + HUD_ARROW_MIDDLETOSIDE / aScaleFactor, screenCoords.y + HUD_ARROW_MIDDLETOBOTTOM / aScaleFactor); // Lower right edge
		pathTurnSegmentPeak.lineTo(screenCoords.x + HUD_ARROW_SIDETOMIDDLE_OPEN / aScaleFactor, screenCoords.y + HUD_ARROW_MIDDLETOBOTTOM / aScaleFactor); // Small inset

		/* Finally rotate the peak around the center-point, that it points to the correct direction of the turn. */
		final Matrix rotPeak = new Matrix();
		rotPeak.setRotate(endAngle, screenCoords.x, screenCoords.y);
		pathTurnSegmentPeak.transform(rotPeak);
		final float endAngleRad = (endAngle - 90) * DEGTORAD;
		rotPeak.setTranslate(FloatMath.cos(endAngleRad) * 6, FloatMath.sin(endAngleRad) * 6);
		pathTurnSegmentPeak.transform(rotPeak);
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================


	/**
	 * Tries to match the second parameter to the pxDistance.
	 * NOTE: distance will only be shorted, so it matches to "<code><= pxDistance</code>"
	 * @param fixedCoords will not be changed
	 * @param variableCoords will be changed, to match the pxDistance
	 */
	protected static final void shortenToDistance(final Point fixedCoords, final Point variableCoords, final int pxDesiredDistance, final boolean force){
		final float curMaxDistSquared = calculateScreenCoordDistanceSquared(fixedCoords, variableCoords);

		final float stretchFactor = pxDesiredDistance / (1.25f * FloatMath.sqrt(curMaxDistSquared));
		if(stretchFactor > 1.0f) {
			return; // stretching the distance over the actual points makes no sense.
		} else{
			final int dx = fixedCoords.x - variableCoords.x;
			final int dy = fixedCoords.y - variableCoords.y;

			variableCoords.x = fixedCoords.x - (int)(dx * stretchFactor);
			variableCoords.y = fixedCoords.y - (int)(dy * stretchFactor);
		}
	}


	protected static final int calculateScreenCoordDistanceSquared(final Point screenCoordsA, final Point screenCoordsB){
		final int dx = screenCoordsA.x - screenCoordsB.x;
		final int dy = screenCoordsA.y - screenCoordsB.y;
		return dx*dx + dy*dy;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
