// Created by plusminus on 22:01:11 - 29.09.2008
package org.andnav2.osm.views.overlay;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.util.constants.MathConstants;
import org.andnav2.osm.views.OSMMapView;
import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;
import org.andnav2.osm.views.overlay.util.DirectionArrowDescriptor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.util.FloatMath;
import android.util.Log;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class OSMMapViewDirectedLocationOverlay extends OSMMapViewOverlay {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final Paint mDirectionRotaterPaint = new Paint();
	private final Paint mAccuracyPaint = new Paint();

	private final Bitmap DIRECTION_ARROW;

	private GeoPoint mLocation;
	private float mBearing;

	private final Matrix directionRotater = new Matrix();

	private final float DIRECTION_ARROW_HOTSPOT_X;
	private final float DIRECTION_ARROW_HOTSPOT_Y;

	private final float DIRECTION_ARROW_CENTER_X;
	private final float DIRECTION_ARROW_CENTER_Y;
	private final int DIRECTION_ARROW_WIDTH;
	private final int DIRECTION_ARROW_HEIGHT;

	private int mAccuracy = 0;
	private boolean mShowAccuracy = true;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewDirectedLocationOverlay(final Context ctx, final DirectionArrowDescriptor pDirectionArrowDescriptor){
		this.DIRECTION_ARROW = BitmapFactory.decodeResource(ctx.getResources(), pDirectionArrowDescriptor.getDrawableID());

		this.DIRECTION_ARROW_HEIGHT = this.DIRECTION_ARROW.getHeight();
		this.DIRECTION_ARROW_WIDTH = this.DIRECTION_ARROW.getWidth();

		this.DIRECTION_ARROW_CENTER_X = this.DIRECTION_ARROW_HEIGHT / 2;
		this.DIRECTION_ARROW_CENTER_Y = this.DIRECTION_ARROW_WIDTH / 2;

		final Point center = pDirectionArrowDescriptor.getCenter();
		this.DIRECTION_ARROW_HOTSPOT_X = (center != null) ? center.x : this.DIRECTION_ARROW_CENTER_X;
		this.DIRECTION_ARROW_HOTSPOT_Y = (center != null) ? center.y : this.DIRECTION_ARROW_CENTER_Y;


		this.mAccuracyPaint.setStrokeWidth(2);
		this.mAccuracyPaint.setColor(Color.BLUE);
		this.mAccuracyPaint.setAntiAlias(true);

		this.mDirectionRotaterPaint.setAntiAlias(true);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setShowAccuracy(final boolean pShowIt){
		this.mShowAccuracy = pShowIt;
	}

	public void setLocation(final GeoPoint mp){
		this.mLocation = mp;
	}

	/**
	 * 
	 * @param pAccuracy in Meters
	 */
	public void setAccuracy(final int pAccuracy){
		this.mAccuracy  = pAccuracy;
	}

	public void setBearing(final float aHeading){
		this.mBearing = aHeading;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void release() {
		this.DIRECTION_ARROW.recycle();
	}

	@Override
	protected void onDrawFinished(final Canvas c, final OSMMapView osmv) {
		return;
	}

	@Override
	public void onDraw(final Canvas canvas, final OSMMapView osmv) {
		try{
			if(this.mLocation != null){
				final OSMMapViewProjection pj = osmv.getProjection();
				final Point screenCoordsMe = new Point();
				pj.toPixels(this.mLocation, screenCoordsMe);

				if(this.mShowAccuracy && this.mAccuracy > 10){
					final float accuracyRadius = pj.meterDistanceToScreenPixelDistance(this.mAccuracy);
					/* Only draw if the DirectionArrow doesn't cover it. */
					if(accuracyRadius > 8){
						/* Draw the inner shadow. */
						this.mAccuracyPaint.setAntiAlias(false);
						this.mAccuracyPaint.setAlpha(30);
						this.mAccuracyPaint.setStyle(Style.FILL);
						canvas.drawCircle(screenCoordsMe.x, screenCoordsMe.y, accuracyRadius, this.mAccuracyPaint);

						/* Draw the edge. */
						this.mAccuracyPaint.setAntiAlias(true);
						this.mAccuracyPaint.setAlpha(150);
						this.mAccuracyPaint.setStyle(Style.STROKE);
						canvas.drawCircle(screenCoordsMe.x, screenCoordsMe.y, accuracyRadius, this.mAccuracyPaint);
					}
				}

				/* Rotate the direction-Arrow according to the bearing we are driving. And draw it to the canvas. */
				this.directionRotater.setRotate(this.mBearing, this.DIRECTION_ARROW_CENTER_X , this.DIRECTION_ARROW_CENTER_Y);
				final Bitmap rotatedDirection = Bitmap.createBitmap(this.DIRECTION_ARROW, 0, 0, this.DIRECTION_ARROW_WIDTH, this.DIRECTION_ARROW_HEIGHT, this.directionRotater, true);

				/* Calculate the deltas needed after the rotation, to paint the hotspot of the directionarrow on the actual location. */
				final float py = this.DIRECTION_ARROW_HOTSPOT_Y - this.DIRECTION_ARROW_CENTER_Y;

				final float dx;
				final float dy;

				if(py < 0.001 || py > 0.001){
					final float alpha = MathConstants.DEG2RAD * (-this.mBearing + 90f);
					dx = FloatMath.cos(alpha) * py;
					dy = FloatMath.sin(alpha) * py;
				}else{
					dx = 0;
					dy = 0;
				}

				canvas.drawBitmap(rotatedDirection, screenCoordsMe.x - rotatedDirection.getWidth() / 2 + dx, screenCoordsMe.y - rotatedDirection.getHeight() / 2 - dy, this.mDirectionRotaterPaint);
			}
		}catch(final OutOfMemoryError e){ // OutOfMemoryError
			Log.e(DEBUGTAG, "Error in: " + this.getClass().getSimpleName(), e);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
