//Created by plusminus on 14:00:27 - 30.01.2008
package org.andnav2.ui.map.overlay;

import java.util.ArrayList;
import java.util.List;

import org.andnav2.R;
import org.andnav2.loc.AbstractAndNavLocationProvider;
import org.andnav2.nav.Navigator;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.util.constants.MathConstants;
import org.andnav2.osm.views.OSMMapView;
import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;
import org.andnav2.osm.views.overlay.OSMMapViewOverlay;
import org.andnav2.osm.views.overlay.util.DirectionArrowDescriptor;
import org.andnav2.preferences.PreferenceConstants;
import org.andnav2.sys.ors.adt.rs.Route;
import org.andnav2.sys.ors.adt.rs.RouteInstruction;
import org.andnav2.ui.map.OpenStreetDDMap;
import org.andnav2.ui.map.overlay.util.ArrowPathCreator;
import org.andnav2.ui.map.overlay.util.ManagedLinePath;
import org.andnav2.util.constants.Constants;
import org.andnav2.util.constants.GeoConstants;
import org.andnav2.util.constants.MathematicalConstants;
import org.andnav2.util.constants.TimeConstants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.util.FloatMath;
import android.util.Log;

public class MapDrivingDirectionsOverlay extends OSMMapViewOverlay implements Constants, TimeConstants, MathematicalConstants, PreferenceConstants, GeoConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	private static final int MIN_ZOOMLEVEL_FOR_ARROWS = 11;

	private static final int MARKER_START_HOTSPOT_X = 18;
	private static final int MARKER_DESTINATION_HOTSPOT_X = MARKER_START_HOTSPOT_X;
	private static final int MARKER_VIA_HOTSPOT_X = 12;

	private static final int MARKER_START_HOTSPOT_Y = 47;
	private static final int MARKER_DESTINATION_HOTSPOT_Y = MARKER_START_HOTSPOT_Y;
	private static final int MARKER_VIA_HOTSPOT_Y = 31;

	private static final float RADIUS_NO_ACCURACY = 20;

	private final Bitmap MARKER_START;
	private final Bitmap MARKER_VIA;
	private final Bitmap MARKER_END;
	private final Bitmap DIRECTION_ARROW;

	private final float DIRECTION_ARROW_HOTSPOT_X;
	private final float DIRECTION_ARROW_HOTSPOT_Y;
	private final float DIRECTION_ARROW_CENTER_X;
	private final float DIRECTION_ARROW_CENTER_Y;
	private final int DIRECTION_ARROW_WIDTH;
	private final int DIRECTION_ARROW_HEIGHT;

	// ===========================================================
	// Fields
	// ===========================================================

	//	private int debugDrawSum = 0;
	//	private int debugDrawCount= 0;

	private float mapRotationDegree = 0;

	private Route mRoute;

	private OpenStreetDDMap myDDMapActivity;
	private final Paint mPathDonePaint;
	private final Paint mPathUpcomingPaint;
	private final Paint mPathCurrentSegmentPaint;
	private final Paint mPathTurnSegmentPeakPaint;
	private final Paint mPathTurnSegmentPeakOutlinePaint;
	private final Paint mPathTurnSegmentPaint;
	private final Paint mPathTurnSegmentOutlinePaint;
	private final Paint mDirectionRotatorPaint;
	private final Paint mMarkerPaint;
	private final Paint mAccuracyPaint = new Paint();

	private final Matrix mDirectionRotater = new Matrix();

	private boolean mRealtimeNav;

	private int mStaticNavCurrentTurnPointIndex = 0;

	private boolean mShowAccuracy = true;

	// ===========================================================
	// Constructors
	// ===========================================================

	public MapDrivingDirectionsOverlay(final OpenStreetDDMap aMapAct, final int aDisplayQuality, final boolean aRealtimeNav, final DirectionArrowDescriptor pDirectionArrowDescriptor) {
		this.myDDMapActivity = aMapAct;
		this.mRealtimeNav = aRealtimeNav;

		{ /* Setup the paints. Needs to be inside of the constructor, as all paints are declared final. */
			this.mAccuracyPaint.setStrokeWidth(2);
			this.mAccuracyPaint.setColor(Color.BLUE);
			this.mAccuracyPaint.setAntiAlias(true);

			this.mPathDonePaint = new Paint();
			this.mPathDonePaint.setStyle(Paint.Style.STROKE);
			this.mPathDonePaint.setStrokeWidth(12);
			this.mPathDonePaint.setARGB(255, 252, 113, 105); // red
			this.mPathDonePaint.setStrokeCap(Cap.ROUND);

			this.mPathUpcomingPaint = new Paint(this.mPathDonePaint);
			this.mPathUpcomingPaint.setARGB(255, 113, 105, 252); // blue

			this.mPathCurrentSegmentPaint = new Paint(this.mPathDonePaint);
			this.mPathCurrentSegmentPaint.setARGB(255, 113, 252, 105); // green

			this.mPathTurnSegmentPaint = new Paint(this.mPathDonePaint);
			this.mPathTurnSegmentPaint.setARGB(255, 255, 255, 255); // white
			this.mPathTurnSegmentPaint.setStrokeWidth(12);

			this.mPathTurnSegmentOutlinePaint = new Paint(this.mPathDonePaint);
			this.mPathTurnSegmentOutlinePaint.setARGB(255, 0, 0, 0); // black
			this.mPathTurnSegmentOutlinePaint.setStrokeWidth(this.mPathTurnSegmentPaint.getStrokeWidth()+4);

			this.mPathTurnSegmentPeakPaint = new Paint(this.mPathTurnSegmentPaint);
			this.mPathTurnSegmentPeakPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			this.mPathTurnSegmentPeakPaint.setStrokeWidth(1);

			this.mPathTurnSegmentPeakOutlinePaint = new Paint(this.mPathTurnSegmentOutlinePaint);
			this.mPathTurnSegmentPeakOutlinePaint.setStyle(Paint.Style.STROKE);
			this.mPathTurnSegmentPeakOutlinePaint.setStrokeWidth(this.mPathTurnSegmentPeakPaint.getStrokeWidth()+4);

			this.mDirectionRotatorPaint = new Paint();

			this.mMarkerPaint = new Paint();

			switch(aDisplayQuality){
				case PREF_DISPLAYQUALITY_BEST:
					this.mDirectionRotatorPaint.setAntiAlias(true);
					this.mPathDonePaint.setAntiAlias(true);
					this.mPathUpcomingPaint.setAntiAlias(true);
					this.mPathCurrentSegmentPaint.setAntiAlias(true);
					this.mPathTurnSegmentPaint.setAntiAlias(true);
					this.mPathTurnSegmentOutlinePaint.setAntiAlias(true);
					this.mPathTurnSegmentPeakPaint.setAntiAlias(true);
					this.mPathTurnSegmentPeakOutlinePaint.setAntiAlias(true);
				case PREF_DISPLAYQUALITY_HIGH:
					this.mPathDonePaint.setPathEffect(new CornerPathEffect(this.mPathDonePaint.getStrokeWidth() / 2));
					this.mPathUpcomingPaint.setPathEffect(new CornerPathEffect(this.mPathUpcomingPaint.getStrokeWidth() / 2));
					this.mPathCurrentSegmentPaint.setPathEffect(new CornerPathEffect(this.mPathCurrentSegmentPaint.getStrokeWidth() / 2));
					this.mPathTurnSegmentPaint.setPathEffect(new CornerPathEffect(this.mPathTurnSegmentPaint.getStrokeWidth() / 2));
					this.mPathTurnSegmentOutlinePaint.setPathEffect(new CornerPathEffect(this.mPathTurnSegmentOutlinePaint.getStrokeWidth() / 2));
					this.mPathTurnSegmentPeakPaint.setPathEffect(new CornerPathEffect(this.mPathTurnSegmentPeakPaint.getStrokeWidth() / 2));
					this.mPathTurnSegmentPeakOutlinePaint.setPathEffect(new CornerPathEffect(this.mPathTurnSegmentPeakOutlinePaint.getStrokeWidth() / 2));
				case PREF_DISPLAYQUALITY_STANDARD:
					this.mMarkerPaint.setAlpha(180);
					this.mPathCurrentSegmentPaint.setAlpha(210);
					this.mPathUpcomingPaint.setAlpha(210);
					this.mPathDonePaint.setAlpha(210);
					this.mPathTurnSegmentOutlinePaint.setAlpha(225); // A bit less transparent than the others
					this.mPathTurnSegmentPeakOutlinePaint.setAlpha(225);
				case PREF_DISPLAYQUALITY_LOW:
					break;
			}
		}

		this.MARKER_START = BitmapFactory.decodeResource(this.myDDMapActivity.getResources(), R.drawable.flag_start);
		this.MARKER_END = BitmapFactory.decodeResource(this.myDDMapActivity.getResources(), R.drawable.flag_destination);
		this.MARKER_VIA = BitmapFactory.decodeResource(this.myDDMapActivity.getResources(), R.drawable.flag_via);

		this.DIRECTION_ARROW = BitmapFactory.decodeResource(this.myDDMapActivity.getResources(), pDirectionArrowDescriptor.getDrawableID());

		this.DIRECTION_ARROW_HEIGHT = this.DIRECTION_ARROW.getHeight();
		this.DIRECTION_ARROW_WIDTH = this.DIRECTION_ARROW.getWidth();

		this.DIRECTION_ARROW_CENTER_X = this.DIRECTION_ARROW_HEIGHT / 2;
		this.DIRECTION_ARROW_CENTER_Y = this.DIRECTION_ARROW_WIDTH / 2;

		final Point center = pDirectionArrowDescriptor.getCenter();
		this.DIRECTION_ARROW_HOTSPOT_X = (center != null) ? center.x : this.DIRECTION_ARROW_CENTER_X;
		this.DIRECTION_ARROW_HOTSPOT_Y = (center != null) ? center.y : this.DIRECTION_ARROW_CENTER_Y;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setShowAccuracy(final boolean pShowIt){
		this.mShowAccuracy = pShowIt;
	}

	public void setMapRotationDegree(final float rotationDegree) {
		this.mapRotationDegree = rotationDegree;
	}

	public void setStaticNavCurrentTurnPointIndex(final int aTurnPointIndex){
		this.mStaticNavCurrentTurnPointIndex = aTurnPointIndex;
	}

	public void setRealtimeNav(final boolean pRealtimenav) {
		this.mRealtimeNav = pRealtimenav;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void release() {
		this.mRoute = null;
		this.MARKER_START.recycle();
		this.MARKER_VIA.recycle();
		this.MARKER_END.recycle();
		this.DIRECTION_ARROW.recycle();
		this.myDDMapActivity = null;
		this.myDDMapActivity = null;
	}

	@Override
	protected void onDrawFinished(final Canvas c, final OSMMapView osmv) {
		// Nothing
	}

	/** This function does some fancy drawing, could be shortened a lot.*/
	@Override
	public void onDraw(final Canvas canvas, final OSMMapView mapView) {
		try{
			/* DEBUG Output */
			//		final long startMs = System.currentTimeMillis();
			//		long routedrawStartMs = 0, routedrawEndMs = 0;
			/* END DEBUG Output */

			/* Get the width/height of the underlying MapView.*/
			final int mapViewWidth = this.myDDMapActivity.getMapViewWidth();
			final int mapViewHeight = this.myDDMapActivity.getMapViewHeight();
			//		final GeoPoint curMapCenter = mapView.getMapCenter();

			/* Will hold various screen-coordinates. */
			final Point screenCoords = new Point();

			final Navigator nav = this.myDDMapActivity.getNavigator();

			/* Method in our custom map view
			 * to return the DrivingDirection object. */
			this.mRoute = this.myDDMapActivity.getRoute();
			if(this.mRoute != null && this.mStaticNavCurrentTurnPointIndex != Constants.NOT_SET){
				final int currentZoomLevel = this.myDDMapActivity.getZoomLevel();

				final int nextRouteIndex;
				final int nextTurnPointIndex;
				final int nextTurnIndexInRoute;
				final int turnAngle;

				final GeoPoint myProjectedLocationGeoPoint;

				final List<RouteInstruction> turnPointsRaw = this.mRoute.getRouteInstructions();
				if (!this.mRealtimeNav){
					final RouteInstruction currentRouteInstruction = turnPointsRaw.get(this.mStaticNavCurrentTurnPointIndex);
					final GeoPoint liteVersionCurrentTurnPoint = currentRouteInstruction.getTurnPoint();
					nextRouteIndex = currentRouteInstruction.getFirstMotherPolylineIndex();

					nextTurnPointIndex = Math.min(this.mStaticNavCurrentTurnPointIndex + 1, turnPointsRaw.size() - 1);
					final RouteInstruction nextTurnPoint = turnPointsRaw.get(nextTurnPointIndex);
					nextTurnIndexInRoute = nextTurnPoint.getFirstMotherPolylineIndex();

					myProjectedLocationGeoPoint = liteVersionCurrentTurnPoint;

					turnAngle = (int)nextTurnPoint.getAngle();
				}else{
					nextRouteIndex = nav.getNextRoutePointIndex();
					nextTurnPointIndex = nav.getNextTurnPointIndex();
					nextTurnIndexInRoute = Math.max(0, nav.getNextTurnPointIndexInRoute());

					turnAngle = (int)nav.getTurnAngle();

					myProjectedLocationGeoPoint = nav.getLastKnownLocationProjectedGeoPoint();
				}

				final GeoPoint myCurrentLocationGeoPoint = this.myDDMapActivity.getLastKnownLocationAsGeoPoint(true);

				/* First get Start end End Point of the route. */
				final GeoPoint startPoint = this.mRoute.getStart();
				final GeoPoint endPoint = this.mRoute.getDestination();

				final OSMMapViewProjection pj = mapView.getProjection();

				final ManagedLinePath pathDone = new ManagedLinePath();
				final ManagedLinePath pathCurrentSegment = new ManagedLinePath();
				final ArrayList<Path> pathTurnSegments = new ArrayList<Path>();
				final ArrayList<Path> pathTurnSegmentsPeaks = new ArrayList<Path>();
				final ManagedLinePath pathUpcoming = new ManagedLinePath();

				/* DEBUG Output */
				{
					//				routedrawStartMs = routedrawEndMs = System.currentTimeMillis();
				}
				/* END DEBUG Output */

				/* Check to see if the route is too long. */
				if (nav.isReady()) {
					/* Retrieve all (Map)Points of the route Found. */
					final List<GeoPoint> polyLine = this.mRoute.getPolyLine();

					//				final long startTransform = System.currentTimeMillis();

					//				canvas.drawText("nri: " + nextRouteIndex, 2, 40, this.pathDonePaint);
					//				canvas.drawText("nti: " + nextTurnPointIndex, 2, 50, this.pathDonePaint);
					//				canvas.drawText("ntiir: " + nextTurnIndexInRoute, 2, 60, this.pathDonePaint);

					if(nextRouteIndex != Constants.NOT_SET && polyLine != null){
						/* Loop through all MapPoints returned. */

						final int increment = (int)(Math.max(1,Math.pow(2, 14-currentZoomLevel)));

						final int lastIndexPathDone = Math.max(0, (myProjectedLocationGeoPoint != null) ? nextRouteIndex - 1 : nextRouteIndex); // -1 when there is the projection in between
						final int firstIndexPathDone = Math.max(0, lastIndexPathDone - 100 * increment);

						final int firstIndexPathCurrent = nextRouteIndex;
						final int lastIndexPathCurrent = nextTurnIndexInRoute;

						final int firstIndexPathUpcoming = lastIndexPathCurrent;
						final int lastIndexPathUPcoming = Math.min(firstIndexPathUpcoming + 100 * increment, polyLine.size() - 1);

						if(firstIndexPathDone != lastIndexPathDone) {
							for(int i = firstIndexPathDone; i <= lastIndexPathDone; i += increment) {
								pathDone.lineTo(pj.toPixels(polyLine.get(i), screenCoords));
							}
						}
						pathDone.lineTo(pj.toPixels(polyLine.get(lastIndexPathDone), screenCoords)); // Ensures, that the this path and the next are connected.

						if(myProjectedLocationGeoPoint != null){
							pj.toPixels(myProjectedLocationGeoPoint, screenCoords);
							pathDone.lineTo(screenCoords);
							pathCurrentSegment.lineTo(screenCoords);
						}

						if(firstIndexPathCurrent != lastIndexPathCurrent) {
							for(int i = firstIndexPathCurrent; i <= lastIndexPathCurrent; i += increment) {
								pathCurrentSegment.lineTo(pj.toPixels(polyLine.get(i), screenCoords));
							}
						}
						pathCurrentSegment.lineTo(pj.toPixels(polyLine.get(lastIndexPathCurrent), screenCoords)); // Ensures, that the this path and the next are connected.

						if(firstIndexPathUpcoming != lastIndexPathUPcoming) {
							for(int i = firstIndexPathUpcoming; i <= lastIndexPathUPcoming; i += increment) {
								pathUpcoming.lineTo(pj.toPixels(polyLine.get(i), screenCoords));
							}
						}



						//				final long endTransform = System.currentTimeMillis();
						//
						//				Log.d(Constants.DEBUGTAG, "Transform: " + (endTransform - startTransform) + " ms");

						/* Used for transforming all paths. */
						final float scaleFactor = (this.mapRotationDegree == Constants.NOT_SET)
						? 1.0f
								: FloatMath.sqrt(mapViewHeight * mapViewHeight + mapViewWidth
										* mapViewWidth) / Math.min(mapViewHeight, mapViewWidth);

						/* Calculate the turn-segment-arrow. */
						if(currentZoomLevel >= MIN_ZOOMLEVEL_FOR_ARROWS){
							{ /* next Arrow */
								final Path arrowPath = new Path();
								final Path arrowPeakPath = new Path();
								try{
									ArrowPathCreator.createArrowOverIndex(pj, nextTurnIndexInRoute, polyLine, arrowPath, arrowPeakPath, scaleFactor, currentZoomLevel, turnAngle);
									pathTurnSegments.add(arrowPath);
									pathTurnSegmentsPeaks.add(arrowPeakPath);
								}catch(final IndexOutOfBoundsException ioobe){
									//							Log.e(DEBUGTAG, "Error drawing arrow. index=" + nextTurnIndexInRoute + " polyline length = " + polyLine.size());
								}

								{ // TODO Remove on release
									//							final int ARROW_RENDER_ZOOMLEVEL = 15;
									//
									//							OpenStreetMapViewProjection pj2 = mapView.new OpenStreetMapViewProjection(ARROW_RENDER_ZOOMLEVEL, 0, 0);
									//
									//							final Path arrowPathDummy = new Path();
									//							final Path arrowPeakPathDummy = new Path();
									//							ArrowPathCreator.createArrowOverIndex(pj2, nextTurnIndexInRoute, polyLine, arrowPathDummy, arrowPeakPathDummy, 1, ARROW_RENDER_ZOOMLEVEL, turnAngle);
									//
									//							final Bitmap b = ArrowPathCreator.drawToBitmap(arrowPathDummy, arrowPeakPathDummy);
									//							canvas.drawBitmap(b, 250,250, new Paint());
								}
							}

							final int between = nav.getDistanceBetweenNextAndUpperNextTurnPoint();
							if(between < 1500 && nextTurnPointIndex != Constants.NOT_SET){ /* upperNext Arrow */
								final int upperNextTurnPointIndex = nextTurnPointIndex + 1;
								if(upperNextTurnPointIndex > 0 && upperNextTurnPointIndex < this.mRoute.getRouteInstructions().size()){
									final Path arrowPath = new Path();
									final Path arrowPeakPath = new Path();

									final RouteInstruction upperNextTurnPoint = turnPointsRaw.get(upperNextTurnPointIndex);
									final float upperNextTurnAngle = upperNextTurnPoint.getAngle();
									final int upperNextTurnIndexInRoute = upperNextTurnPoint.getFirstMotherPolylineIndex();

									try{
										ArrowPathCreator.createArrowOverIndex(pj, upperNextTurnIndexInRoute, polyLine, arrowPath, arrowPeakPath, scaleFactor, currentZoomLevel, upperNextTurnAngle);
										pathTurnSegments.add(arrowPath);
										pathTurnSegmentsPeaks.add(arrowPeakPath);
									}catch(final IndexOutOfBoundsException ioobe){
										//							Log.e(DEBUGTAG, "Error drawing arrow. index=" + upperNextTurnIndexInRoute + " polyline length = " + polyLine.size());
									}
								}
							}
						}
					}


					/* Draw the already driven route to the canvas. */
					//				if(!canvas.quickReject(pathDone, EdgeType.BW))
					canvas.drawPath(pathDone, this.mPathDonePaint);

					/* Draw the rest Route to the canvas. */
					//				if(!canvas.quickReject(pathUpcoming, EdgeType.BW))
					canvas.drawPath(pathUpcoming, this.mPathUpcomingPaint);

					/* Draw the current Route Segment to the canvas. */
					//				if(!canvas.quickReject(pathCurrentSegment, EdgeType.AA))
					canvas.drawPath(pathCurrentSegment, this.mPathCurrentSegmentPaint);

					/* Draw the Turn Segment to the canvas. */
					for (int j = pathTurnSegments.size() - 1; j >= 0 ; j--) {
						canvas.drawPath(pathTurnSegments.get(j), this.mPathTurnSegmentOutlinePaint);
						canvas.drawPath(pathTurnSegments.get(j), this.mPathTurnSegmentPaint);

						canvas.drawPath(pathTurnSegmentsPeaks.get(j), this.mPathTurnSegmentPeakOutlinePaint);
						canvas.drawPath(pathTurnSegmentsPeaks.get(j), this.mPathTurnSegmentPeakPaint);
					}

					// DEBUG Output
					{
						//					int minLatitude = this.mRoute.getLatitudeMinSpans()[nav.getNextRoutePointIndex()];
						//					int maxLatitude = this.mRoute.getLatitudeMaxSpans()[nav.getNextRoutePointIndex()];
						//					int minLongitude = this.mRoute.getLongitudeMinSpans()[nav.getNextRoutePointIndex()];
						//					int maxLongitude = this.mRoute.getLongitudeMaxSpans()[nav.getNextRoutePointIndex()];
						//
						////					Log.d(DEBUGTAG, "nextRoutePointIndex=" + nav.getNextRoutePointIndex());
						//
						//					int myLat = myCurrentLocationMapPoint.getLatitude();
						//					int myLon = myCurrentLocationMapPoint.getLongitude();
						//
						//					maxLatitude = Math.max(myLat, maxLatitude);
						//					minLatitude = Math.min(myLat, minLatitude);
						//					maxLongitude = Math.max(myLon, maxLongitude);
						//					minLongitude = Math.min(myLon, minLongitude);
						//
						//					int x1, x2, y1, y2;
						//					pj.toPixels(new GeoPoint(minLatitude, minLongitude), screenCoords);
						//					x1 = screenCoords.x;
						//					y1 = screenCoords.y;
						//
						//					pj.toPixels(new GeoPoint(maxLatitude, maxLongitude), screenCoords);
						//					x2 = screenCoords.x;
						//					y2 = screenCoords.y;
						////					Log.d(DEBUGTAG, "x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2="+ y2);
						//					Paint p = new Paint();
						//					p.setStrokeWidth(3);
						//					p.setARGB(255,255,0,0);
						//					p.setStyle(Style.STROKE);
						//					canvas.drawRect(new Rect(x1,y1,x2,y2), p);
					}
					// END DEBUG Output

					{ /* Print Pin-MArkers. */
						/* Finally draw a fancy PIN to mark the end... */
						pj.toPixels(endPoint, screenCoords);

						canvas.drawBitmap(this.MARKER_END,
								screenCoords.x - MARKER_DESTINATION_HOTSPOT_X,
								screenCoords.y - MARKER_DESTINATION_HOTSPOT_Y,
								this.mMarkerPaint);

						/* ...for all via-points. */
						final List<GeoPoint> vias = this.mRoute.getVias();
						for(final GeoPoint mpVia : vias){
							pj.toPixels(mpVia, screenCoords);

							canvas.drawBitmap(this.MARKER_VIA,
									screenCoords.x - MARKER_VIA_HOTSPOT_X,
									screenCoords.y - MARKER_VIA_HOTSPOT_Y,
									this.mMarkerPaint);
						}


						/* ...and the start of the route.*/
						pj.toPixels(startPoint, screenCoords);

						canvas.drawBitmap(this.MARKER_START,
								screenCoords.x - MARKER_START_HOTSPOT_X,
								screenCoords.y - MARKER_START_HOTSPOT_Y,
								this.mMarkerPaint);
					}


					{ /* DEBUG Output */
						//					routedrawEndMs = System.currentTimeMillis();
					} /* END DEBUG Output */
				}

				final AbstractAndNavLocationProvider andNavLocationProvider = this.myDDMapActivity.getAndNavLocationProvider();


				if(myCurrentLocationGeoPoint != null){
					/* Draw ourself to our real location. */
					pj.toPixels(myCurrentLocationGeoPoint, screenCoords);


					/* Draw the HorizontalPositioningError if we have a location. */
					if(this.mShowAccuracy){

						final float accuracyRadius = (andNavLocationProvider.hasHorizontalPositioningError())
						? pj.meterDistanceToScreenPixelDistance(andNavLocationProvider.getHorizontalPositioningError())
								: RADIUS_NO_ACCURACY;

						/* Only draw if the DirectionArrow doesn't cover it. */
						if(accuracyRadius > 8){
							/* Draw the inner shadow. */
							this.mAccuracyPaint.setAntiAlias(false);
							this.mAccuracyPaint.setAlpha(30);
							this.mAccuracyPaint.setStyle(Style.FILL);
							canvas.drawCircle(screenCoords.x, screenCoords.y, accuracyRadius, this.mAccuracyPaint);

							/* Draw the edge. */
							this.mAccuracyPaint.setAntiAlias(true);
							this.mAccuracyPaint.setAlpha(150);
							this.mAccuracyPaint.setStyle(Style.STROKE);
							canvas.drawCircle(screenCoords.x, screenCoords.y, accuracyRadius, this.mAccuracyPaint);
						}
					}

					/* Get the bearing if available. */
					final boolean hasBearing = andNavLocationProvider.hasBearing();
					final float directionBearing = (hasBearing) ? andNavLocationProvider.getBearing() : 0;

					/* Rotate the direction-Arrow according to the bearing we are driving. And draw it to the canvas. */
					this.mDirectionRotater.setRotate(directionBearing, this.DIRECTION_ARROW_CENTER_X , this.DIRECTION_ARROW_CENTER_Y);
					final Bitmap rotatedDirection = Bitmap.createBitmap(this.DIRECTION_ARROW, 0, 0, this.DIRECTION_ARROW_WIDTH, this.DIRECTION_ARROW_HEIGHT, this.mDirectionRotater, true);

					/* Calculate the deltas needed after the rotation, to paint the hotspot of the directionarrow on the actual location. */
					final float py = this.DIRECTION_ARROW_HOTSPOT_Y - this.DIRECTION_ARROW_CENTER_Y;

					final float dx;
					final float dy;

					if(py < 0.001 || py > 0.001){
						final float alpha = MathConstants.DEG2RAD * (-directionBearing + 90f);
						dx = FloatMath.cos(alpha) * py;
						dy = FloatMath.sin(alpha) * py;
					}else{
						dx = 0;
						dy = 0;
					}
					canvas.drawBitmap(rotatedDirection, screenCoords.x - rotatedDirection.getWidth() / 2 + dx, screenCoords.y - rotatedDirection.getHeight() / 2 - dy, this.mDirectionRotatorPaint);
				}

				{ /* DEBUG Output */
					//				long endMs = System.currentTimeMillis();
					//
					//				this.debugDrawSum +=  endMs - startMs;
					//				this.debugDrawCount++;
					//				Log.d(DEBUGTAG, "GUI: " + (endMs - startMs) + " ms  [Avg: " + (this.debugDrawSum / this.debugDrawCount) + " ms]"
					//						+ "    [Route: " + (routedrawEndMs - routedrawStartMs) + " ms]");
				} /* END DEBUG Output */
			}
		}catch(final Exception e){
			Log.e(Constants.DEBUGTAG, "Error in directionsOverlay", e);
			//			Exceptor.e("Error in directionsOverlay", e, this.myDDMapActivity);
		}
	}

	//	===========================================================
	//	Methods
	//	===========================================================



	//	===========================================================
	//	Inner and Anonymous Classes
	//	===========================================================
}
