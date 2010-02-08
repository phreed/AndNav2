// Created by plusminus on 17:45:56 - 25.09.2008
package org.andnav2.osm.views;

import java.util.ArrayList;
import java.util.List;

import org.andnav2.exc.Exceptor;
import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.adt.IGeoPoint;
import org.andnav2.osm.adt.util.TypeConverter;
import org.andnav2.osm.exceptions.ExternalStorageNotMountedException;
import org.andnav2.osm.util.MyMath;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.osm.views.controller.OSMMapViewController;
import org.andnav2.osm.views.overlay.OSMMapViewOverlay;
import org.andnav2.osm.views.tiles.OSMAbstractMapTileProvider;
import org.andnav2.osm.views.tiles.OSMMapTileManager;
import org.andnav2.osm.views.tiles.OSMMapTileProviderInfo;
import org.andnav2.osm.views.tiles.adt.OSMTileInfo;
import org.andnav2.osm.views.tiles.caching.OSMMapTileFilesystemCache;
import org.andnav2.osm.views.util.Util;
import org.andnav2.osm.views.util.constants.OSMMapViewConstants;
import org.andnav2.ui.common.views.RotateView;
import org.andnav2.ui.map.overlay.util.ManagedLinePath;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.GestureDetector.OnGestureListener;

public class OSMMapView extends View implements OSMConstants, OSMMapViewConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	final OSMMapTileProviderInfo DEFAULTPROVIDER = OSMMapTileProviderInfo.MAPNIK;

	// ===========================================================
	// Fields
	// ===========================================================

	private final List<OnChangeListener> mChangeListeners = new ArrayList<OnChangeListener>();

	private int mLatitudeE6 = 0, mLongitudeE6 = 0;
	private int mZoomLevel = 0;

	protected OSMMapTileProviderInfo mProviderInfo;
	protected OSMMapTileManager mTileManager;

	protected final GestureDetector mGestureDetector = new GestureDetector(new OpenStreetMapViewGestureDetectorListener());

	protected final List<OSMMapViewOverlay> mOverlays = new ArrayList<OSMMapViewOverlay>();

	protected final Paint mPaint = new Paint();
	private int mTouchDownX;
	private int mTouchDownY;
	private int mTouchMapOffsetX;
	private int mTouchMapOffsetY;

	private OSMMapView mMiniMap, mMaxiMap;

	private OSMMapViewController mController;
	private int mMiniMapOverriddenVisibility = NOT_SET;
	private int mMiniMapZoomDiff = NOT_SET;

	/** Gets reset everytime the mapcenter or the zoomlevel changes. */
	private OSMMapViewProjection mCurrentProjection;

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * XML Constructor (uses default Renderer)
	 * @throws ExternalStorageNotMountedException
	 */
	public OSMMapView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.mProviderInfo = this.DEFAULTPROVIDER;
		this.mTileManager = new OSMMapTileManager(context, this.mProviderInfo, new SimpleInvalidationHandler());
	}

	/**
	 * Standard Constructor for {@link OSMMapView}.
	 * @param context
	 * @param aRendererInfo pass a {@link OSMMapTileProviderInfo} you like.
	 * @throws ExternalStorageNotMountedException
	 */
	public OSMMapView(final Context context, final OSMMapTileProviderInfo aRendererInfo) {
		this(context, aRendererInfo, null);
	}

	/**
	 * 
	 * @param context
	 * @param aProviderInfo  pass a {@link OSMMapTileProviderInfo} you like.
	 * @param osmv another {@link OSMMapView}, to share the TileProvider with.<br/>
	 * May significantly improve the render speed, when using the same {@link OSMMapTileProviderInfo}.
	 * @throws ExternalStorageNotMountedException
	 */
	public OSMMapView(final Context context, final OSMMapTileProviderInfo aProviderInfo, final OSMMapView aMapToShareTheTileProviderWith) {
		super(context);
		this.mProviderInfo = aProviderInfo;
		if(aMapToShareTheTileProviderWith == null){
			this.mTileManager =  new OSMMapTileManager(context, aProviderInfo, new SimpleInvalidationHandler());
		}else{
			this.mTileManager = aMapToShareTheTileProviderWith.mTileManager;
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void addChangeListener(final OnChangeListener aChangeListener){
		this.mChangeListeners.add(aChangeListener);
	}

	private void fireOnChangeListeners() {
		for(final OnChangeListener cl : this.mChangeListeners) {
			cl.onChange();
		}
	}

	public void forceFireOnChangeListeners() {
		this.fireOnChangeListeners();
	}

	public int getTouchMapOffsetX() {
		return this.mTouchMapOffsetX;
	}

	public int getTouchMapOffsetY() {
		return this.mTouchMapOffsetY;
	}

	/**
	 * This MapView takes control of the {@link OSMMapView} passed as parameter.<br />
	 * I.e. it zoomes it to x levels less than itself and centers it the same coords.<br />
	 * Its pretty usefull when the MiniMap uses the same TileProvider.
	 * @see OpenStreetMapView.OpenStreetMapView(
	 * @param aOsmvMinimap
	 * @param aZoomDiff 3 is a good Value. Pass {@link OSMMapViewConstants}.NOT_SET to disable autozooming of the minimap.
	 */
	public void setMiniMap(final OSMMapView aOsmvMinimap, final int aZoomDiff) {
		this.mMiniMapZoomDiff  = aZoomDiff;
		this.mMiniMap = aOsmvMinimap;
		aOsmvMinimap.setMaxiMap(this);

		// Synchronize the Views.
		this.setMapCenter(this.mLatitudeE6, this.mLongitudeE6);
		this.setZoomLevel(this.getZoomLevel());
	}

	public boolean hasMiniMap(){
		return this.mMiniMap != null;
	}

	/**
	 * @return {@link View}.GONE or {@link View}.VISIBLE or {@link View}.INVISIBLE or {@link OSMMapViewConstants}.NOT_SET
	 * */
	public int getOverrideMiniMapVisiblity() {
		return this.mMiniMapOverriddenVisibility;
	}

	/**
	 * Use this method if you want to make the MiniMap visible i.e.: always or never.
	 * Use {@link View}.GONE , {@link View}.VISIBLE, {@link View}.INVISIBLE.
	 * Use {@link OSMMapViewConstants}.NOT_SET to reset this feature.
	 * @param aVisiblity
	 */
	public void setOverrideMiniMapVisiblity(final int aVisiblity) {
		switch(aVisiblity){
			case View.GONE:
			case View.VISIBLE:
			case View.INVISIBLE:
				if(this.mMiniMap != null) {
					this.mMiniMap.setVisibility(aVisiblity);
				}
			case NOT_SET:
				this.setZoomLevel(this.mZoomLevel);
				break;
			default:
				throw new IllegalArgumentException("See javadoc of this method !!!");
		}
		this.mMiniMapOverriddenVisibility = aVisiblity;
	}

	protected void setMaxiMap(final OSMMapView aOsmvMaxiMap){
		this.mMaxiMap = aOsmvMaxiMap;
	}

	public OSMMapViewController getController() {
		if(this.mController != null) {
			return this.mController;
		} else {
			return this.mController = new OSMMapViewController(this);
		}
	}

	/**
	 * You can add/remove/reorder your Overlays using the List of {@link OSMMapViewOverlay}.
	 * The first (index 0) Overlay gets drawn first, the one with the highest as the last one.
	 */
	public List<OSMMapViewOverlay> getOverlays() {
		return this.mOverlays;
	}

	public double getLatitudeSpan() {
		return this.getDrawnBoundingBoxE6().getLongitudeSpanE6() / 1E6;
	}

	public int getLatitudeSpanE6() {
		return this.getDrawnBoundingBoxE6().getLatitudeSpanE6();
	}

	public double getLongitudeSpan() {
		return this.getDrawnBoundingBoxE6().getLatitudeSpanE6() / 1E6;
	}

	public int getLongitudeSpanE6() {
		return this.getDrawnBoundingBoxE6().getLatitudeSpanE6();
	}

	public BoundingBoxE6 getDrawnBoundingBoxE6() {
		return getBoundingBox(this.getWidth(), this.getHeight());
	}

	public BoundingBoxE6 getVisibleBoundingBoxE6() {
		final ViewParent parent = this.getParent();
		if(parent instanceof RotateView){
			final RotateView par = (RotateView)parent;
			return getBoundingBox(par.getMeasuredWidth(), par.getMeasuredHeight());
		}else{
			return getBoundingBox(this.getWidth(), this.getHeight());
		}
	}

	private BoundingBoxE6 getBoundingBox(final int pViewWidth, final int pViewHeight){
		/* Get the center MapTile which is above this.mLatitudeE6 and this.mLongitudeE6 .*/
		final OSMTileInfo centerMapTileCoords = Util.getMapTileFromCoordinates(this.mLatitudeE6, this.mLongitudeE6, this.mZoomLevel);

		final BoundingBoxE6 tmp = Util.getBoundingBoxFromMapTile(centerMapTileCoords);

		final int mLatitudeSpan_2 = (int)(1.0f * tmp.getLatitudeSpanE6() * pViewHeight / this.mProviderInfo.MAPTILE_SIZEPX) / 2;
		final int mLongitudeSpan_2 = (int)(1.0f * tmp.getLongitudeSpanE6() * pViewWidth / this.mProviderInfo.MAPTILE_SIZEPX) / 2;

		final int north = this.mLatitudeE6 + mLatitudeSpan_2;
		final int south = this.mLatitudeE6 - mLatitudeSpan_2;
		final int west = this.mLongitudeE6 - mLongitudeSpan_2;
		final int east = this.mLongitudeE6 + mLongitudeSpan_2;

		return new BoundingBoxE6(north, east, south, west);
	}

	public OSMMapViewProjection getProjection(){
		if(this.mCurrentProjection == null) {
			this.mCurrentProjection = new OSMMapViewProjection();
		}

		return this.mCurrentProjection;
	}

	public OSMMapViewProjection getProjection(final int aZoom, final GeoPoint aCenter){
		return new OSMMapViewProjection(aZoom, aCenter.getLatitudeE6(), aCenter.getLongitudeE6());
	}

	public void setMapCenter(final GeoPoint aCenter){
		if(aCenter != null) {
			this.setMapCenter(aCenter.getLatitudeE6(), aCenter.getLongitudeE6());
		}
	}

	public void setMapCenter(final Location aLocation) {
		if(aLocation != null) {
			this.setMapCenter(TypeConverter.locationToGeoPoint(aLocation));
		}
	}

	public void setMapCenter(final double aLatitude, final double aLongitude){
		this.setMapCenter((int)(aLatitude * 1E6), (int)(aLongitude * 1E6));
	}

	public void setMapCenter(final int aLatitudeE6, final int aLongitudeE6){
		this.setMapCenter(aLatitudeE6, aLongitudeE6, true);
	}

	protected void setMapCenter(final int aLatitudeE6, final int aLongitudeE6, final boolean doPassFurther){
		/* The current Projection will get invalid with this call. */
		this.mCurrentProjection = null;

		/* Workaround that happens in about no case accidentally. */
		if(aLatitudeE6 == 0 && aLongitudeE6 == 0) {
			return;
		}

		/* Check if the ProviderInfo has a BoundingBox-Fix. */
		if(this.mProviderInfo.hasBoundingBox()){
			final GeoPoint gp = this.mProviderInfo.bringToBoundingBox(aLatitudeE6, aLongitudeE6);
			this.mLatitudeE6 = gp.getLatitudeE6();
			this.mLongitudeE6 = gp.getLongitudeE6();
		}else{
			this.mLatitudeE6 = aLatitudeE6;
			this.mLongitudeE6 = aLongitudeE6;
		}

		if(doPassFurther && this.mMiniMap != null) {
			this.mMiniMap.setMapCenter(aLatitudeE6, aLongitudeE6, false);
		} else if(this.mMaxiMap != null) {
			this.mMaxiMap.setMapCenter(aLatitudeE6, aLongitudeE6, false);
		}

		this.fireOnChangeListeners();
		this.postInvalidate();
	}

	public void setProviderInfo(final OSMMapTileProviderInfo aProvider){
		try{
			this.mProviderInfo = aProvider;
			this.mTileManager.setProviderInfo(aProvider);
		}catch(final IllegalArgumentException iae){
			setProviderInfo(OSMMapTileProviderInfo.getDefault());
			return;
		}

		this.setZoomLevel(this.mZoomLevel); // Invalidates the map and zooms to the maximum level of the renderer.
		this.setMapCenter(this.getMapCenter()); // Renderer might have a BoundingBox
	}

	/**
	 * @param aZoomLevel between 0 (equator) and 18/19(closest), depending on the Renderer chosen.
	 */
	public void setZoomLevel(final int aZoomLevel){
		final int zoomLevelBefore = this.mZoomLevel;
		this.mZoomLevel = Math.max(this.mProviderInfo.ZOOM_MINLEVEL, Math.min(this.mProviderInfo.ZOOM_MAXLEVEL, aZoomLevel));
		if(this.mZoomLevel == zoomLevelBefore) {
			return;
		}

		if(this.mMiniMap != null){
			if(this.mZoomLevel < this.mMiniMapZoomDiff){
				if(this.mMiniMapOverriddenVisibility == NOT_SET) {
					this.mMiniMap.setVisibility(View.INVISIBLE);
				}
			}else{
				if(this.mMiniMapOverriddenVisibility == NOT_SET && this.mMiniMap.getVisibility() != View.VISIBLE){
					this.mMiniMap.setVisibility(View.VISIBLE);
				}
				if(this.mMiniMapZoomDiff != NOT_SET) {
					this.mMiniMap.setZoomLevel(this.mZoomLevel - this.mMiniMapZoomDiff);
				}
			}
		}
		/* The current Projection will get invalid with this call. */
		this.mCurrentProjection = null;

		this.fireOnChangeListeners();
		this.postInvalidate();
	}

	/**
	 * Zooms in if possible.
	 */
	public void zoomIn(){
		this.setZoomLevel(this.mZoomLevel + 1);
	}

	/**
	 * Zooms out if possible.
	 */
	public void zoomOut(){
		this.setZoomLevel(this.mZoomLevel - 1);
	}

	/**
	 * @return the current ZoomLevel between 0 (equator) and 18/19(closest), depending on the Renderer chosen.
	 */
	public int getZoomLevel(){
		return this.mZoomLevel;
	}

	public GeoPoint getMapCenter() {
		return new GeoPoint(this.mLatitudeE6, this.mLongitudeE6);
	}

	public int getMapCenterLatitudeE6() {
		return this.mLatitudeE6;
	}

	public int getMapCenterLongitudeE6() {
		return this.mLongitudeE6;
	}

	public OSMMapTileManager getMapTileManager(){
		return this.mTileManager;
	}

	public OSMMapTileProviderInfo getProviderInfo(){
		return this.mProviderInfo;
	}

	public void release() {
		for(final OSMMapViewOverlay o : this.mOverlays) {
			o.release();
		}
		this.mTileManager.release();
		this.mTileManager = null;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void invalidate() {
		if(this.mZoomLevel < 4){
			super.invalidate();
		}else{
			final OSMMapViewProjection pj = getProjection();
			final BoundingBoxE6 bbE6Visible = getVisibleBoundingBoxE6();

			final Point reuse = new Point();
			pj.toPixels(bbE6Visible.getLatNorthE6(), bbE6Visible.getLonWestE6(), reuse);
			final int l = reuse.x;
			final int t = reuse.y;
			pj.toPixels(bbE6Visible.getLatSouthE6(), bbE6Visible.getLonEastE6(), reuse);
			final int r = reuse.x;
			final int b = reuse.y;
			invalidate(l - this.mTouchMapOffsetX, t - this.mTouchMapOffsetY, r - this.mTouchMapOffsetX, b - this.mTouchMapOffsetY);
		}
	}

	public void onLongPress(final MotionEvent e) {
		for(final OSMMapViewOverlay osmvo : this.mOverlays) {
			if(osmvo.onLongPress(e, this)) {
				return;
			}
		}
	}

	public boolean onSingleTapUp(final MotionEvent e) {
		for(final OSMMapViewOverlay osmvo : this.mOverlays) {
			if(osmvo.onSingleTapUp(e, this)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		for(final OSMMapViewOverlay osmvo : this.mOverlays) {
			if(osmvo.onKeyDown(keyCode, event, this)) {
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(final int keyCode, final KeyEvent event) {
		for(final OSMMapViewOverlay osmvo : this.mOverlays) {
			if(osmvo.onKeyUp(keyCode, event, this)) {
				return true;
			}
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onTrackballEvent(final MotionEvent event) {
		for(final OSMMapViewOverlay osmvo : this.mOverlays) {
			if(osmvo.onTrackballEvent(event, this)) {
				return true;
			}
		}

		return super.onTrackballEvent(event);
	}



	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		for(final OSMMapViewOverlay osmvo : this.mOverlays) {
			if(osmvo.onTouchEvent(event, this)) {
				return true;
			}
		}

		/* TouchEvents should stop all animations. */
		this.getController().stopAnimation(false);

		this.mGestureDetector.onTouchEvent(event);

		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				this.mTouchDownX = (int)event.getX();
				this.mTouchDownY = (int)event.getY();
				invalidate();
				return true;
			case MotionEvent.ACTION_MOVE:
				this.mTouchMapOffsetX = (int)event.getX() - this.mTouchDownX;
				this.mTouchMapOffsetY = (int)event.getY() - this.mTouchDownY;
				invalidate();
				return true;
			case MotionEvent.ACTION_UP:
				final int viewWidth_2 = this.getWidth() / 2;
				final int viewHeight_2 = this.getHeight() / 2;
				final GeoPoint newCenter = this.getProjection().fromPixels(viewWidth_2, viewHeight_2);
				this.mTouchMapOffsetX = 0;
				this.mTouchMapOffsetY = 0;
				this.setMapCenter(newCenter); // Calls invalidate
		}

		return super.onTouchEvent(event);
	}

	@Override
	public void onDraw(final Canvas c){
		try{
			//			final long startMs = System.currentTimeMillis();

			/* Do some calculations and drag attributes to local variables to save some performance. */
			final int zoomLevel = this.mZoomLevel;
			final int viewWidth = this.getWidth();
			final int viewHeight = this.getHeight();
			final int tileSizePx = this.mProviderInfo.MAPTILE_SIZEPX;

			/* Get the center MapTile which is above this.mLatitudeE6 and this.mLongitudeE6 .*/
			final OSMTileInfo centerMapTileCoords = Util.getMapTileFromCoordinates(this.mLatitudeE6, this.mLongitudeE6, zoomLevel);

			/* Calculate the Latitude/Longitude on the left-upper ScreenCoords of the center MapTile.
			 * So in the end we can determine which MapTiles we additionally need next to the centerMapTile. */
			final Point upperLeftCornerOfCenterMapTile = getUpperLeftCornerOfCenterMapTileInScreen(centerMapTileCoords, tileSizePx, null);

			final int centerMapTileScreenLeft = upperLeftCornerOfCenterMapTile.x;
			final int centerMapTileScreenTop = upperLeftCornerOfCenterMapTile.y;

			final int centerMapTileScreenRight = centerMapTileScreenLeft + tileSizePx;
			final int centerMapTileScreenBottom = centerMapTileScreenTop + tileSizePx;

			/* Calculate the amount of tiles needed for each side around the center one. */
			final int additionalTilesNeededToLeftOfCenter = (int)Math.ceil((float)centerMapTileScreenLeft / tileSizePx); // i.e. "30 / 256" = 1;
			final int additionalTilesNeededToRightOfCenter = (int)Math.ceil((float)(viewWidth - centerMapTileScreenRight) / tileSizePx);
			final int additionalTilesNeededToTopOfCenter = (int)Math.ceil((float)centerMapTileScreenTop / tileSizePx); // i.e. "30 / 256" = 1;
			final int additionalTilesNeededToBottomOfCenter = (int)Math.ceil((float)(viewHeight - centerMapTileScreenBottom) / tileSizePx);


			final int mapTileUpperBound = (int)Math.pow(2, zoomLevel);

			/* Draw all the MapTiles (from the upper left to the lower right). */
			for(int y = -additionalTilesNeededToTopOfCenter; y <= additionalTilesNeededToBottomOfCenter; y++){
				for(int x = -additionalTilesNeededToLeftOfCenter; x <= additionalTilesNeededToRightOfCenter; x++){
					/* Add/substract the difference of the tile-position to the one of the center. */
					final int yLat = MyMath.mod(centerMapTileCoords.y + y, mapTileUpperBound);
					final int xLon = MyMath.mod(centerMapTileCoords.x + x, mapTileUpperBound);

					/* Draw the MapTile 'i * tileSizePx' above of the centerMapTile */
					final Bitmap currentMapTile = this.mTileManager.getMapTile(new OSMTileInfo(xLon, yLat, zoomLevel));
					final int tileLeft = this.mTouchMapOffsetX + centerMapTileScreenLeft + (x * tileSizePx);
					final int tileTop = this.mTouchMapOffsetY + centerMapTileScreenTop + (y * tileSizePx);

					if(!currentMapTile.isRecycled()) {
						c.drawBitmap(currentMapTile, tileLeft, tileTop, this.mPaint);
					}

					if(DEBUGMODE){
						c.drawText("x=" + tileLeft + "  y=" + tileTop, tileLeft + 2, tileTop + 10, this.mPaint);
						c.drawLine(tileLeft, tileTop, tileLeft + tileSizePx, tileTop, this.mPaint);
						c.drawLine(tileLeft, tileTop, tileLeft, tileTop + tileSizePx, this.mPaint);
					}
				}
			}

			if(this.mProviderInfo.hasBoundingBox()){
				final OSMMapViewProjection pj = getProjection();
				final RectF rect = pj.toPixels(this.mProviderInfo.BOUNDINGBOXE6);
				c.drawRect(rect, this.mPaint);
			}

			/* Draw all Overlays. */
			for(final OSMMapViewOverlay osmvo : this.mOverlays) {
				osmvo.onManagedDraw(c, this);
			}

			this.mPaint.setStyle(Style.STROKE);
			if (this.mMaxiMap != null) {
				c.drawRect(0, 0, viewWidth - 1, viewHeight - 1, this.mPaint);
			}

			//			final long endMs = System.currentTimeMillis();
			//			Log.i(DEBUGTAG, "Rendering overall: " + (endMs - startMs) + "ms");
		}catch(final Exception e){
			Exceptor.e("Exception while Drawing mapview", e, this.getContext());
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * @param centerMapTileCoords
	 * @param tileSizePx
	 * @param reuse just pass null if you do not have a Point to be 'recycled'.
	 */
	private Point getUpperLeftCornerOfCenterMapTileInScreen(final OSMTileInfo pTileInfo, final int tileSizePx, final Point reuse){
		final Point out = (reuse != null) ? reuse : new Point();

		final int viewWidth = this.getWidth();
		final int viewWidth_2 = viewWidth / 2;
		final int viewHeight = this.getHeight();
		final int viewHeight_2 = viewHeight / 2;

		/* Calculate the Latitude/Longitude on the left-upper ScreenCoords of the center MapTile.
		 * So in the end we can determine which MapTiles we additionally need next to the centerMapTile. */
		final BoundingBoxE6 bb = Util.getBoundingBoxFromMapTile(pTileInfo);
		final float[] relativePositionInCenterMapTile = bb.getRelativePositionOfGeoPointInBoundingBoxWithLinearInterpolation(this.mLatitudeE6, this.mLongitudeE6, null);

		final int centerMapTileScreenLeft = viewWidth_2 - (int)(0.5f + (relativePositionInCenterMapTile[X] * tileSizePx));
		final int centerMapTileScreenTop = viewHeight_2 - (int)(0.5f + (relativePositionInCenterMapTile[Y] * tileSizePx));

		out.set(centerMapTileScreenLeft, centerMapTileScreenTop);
		return out;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface OnChangeListener{
		public void onChange();
	}

	/**
	 * This class may return valid results until the underlying {@link OSMMapView} gets modified in any way (i.e. new center zoomLevel).
	 * @author Nicolas Gramlich
	 */
	public class OSMMapViewProjection {
		final int viewWidth;
		final int viewHeight;
		final BoundingBoxE6 bb;
		final int tileSizePx;
		final OSMTileInfo centerMapTileCoords;
		final Point upperLeftCornerOfCenterMapTile;

		public OSMMapViewProjection(){
			this(OSMMapView.this.mZoomLevel, OSMMapView.this.mLatitudeE6, OSMMapView.this.mLongitudeE6);
		}

		public OSMMapViewProjection(final int aZoom, final int aLatitudeE6, final int aLongitudeE6){
			this.viewWidth = OSMMapView.this.getWidth();
			this.viewHeight = OSMMapView.this.getHeight();

			/* Do some calculations and drag attributes to local variables to save some performance. */
			this.tileSizePx = OSMMapView.this.mProviderInfo.MAPTILE_SIZEPX;

			/* Get the center MapTile which is above this.mLatitudeE6 and this.mLongitudeE6 .*/
			this.centerMapTileCoords = Util.getMapTileFromCoordinates(aLatitudeE6, aLongitudeE6, aZoom);
			this.upperLeftCornerOfCenterMapTile = getUpperLeftCornerOfCenterMapTileInScreen(this.centerMapTileCoords, this.tileSizePx, null);

			this.bb = OSMMapView.this.getDrawnBoundingBoxE6();
		}


		/**
		 * Converts x/y ScreenCoordinates to the underlying GeoPoint.
		 * @param x
		 * @param y
		 * @return GeoPoint under x/y.
		 */
		public GeoPoint fromPixels(float x, float y) {
			/* Subtract the offset caused by touch. */
			x -= OSMMapView.this.mTouchMapOffsetX;
			y -= OSMMapView.this.mTouchMapOffsetY;

			return this.bb.getGeoPointOfRelativePositionWithLinearInterpolation(x / this.viewWidth, y / this.viewHeight);
		}

		private static final int EQUATORCIRCUMFENCE = 40075004;

		public float metersToEquatorPixels(final float aMeters) {
			return aMeters / EQUATORCIRCUMFENCE * OSMMapView.this.mProviderInfo.MAPTILE_SIZEPX;
		}

		public float meterDistanceToScreenPixelDistance(final float aMeters) {
			final BoundingBoxE6 drawnBoundingBoxE6 = OSMMapView.this.getDrawnBoundingBoxE6();
			final int lonEastE6 = drawnBoundingBoxE6.getLonEastE6();
			final int lonWestE6 = drawnBoundingBoxE6.getLonWestE6();
			final GeoPoint centerPoint = drawnBoundingBoxE6.getCenter();

			final GeoPoint eastCenter = new GeoPoint(centerPoint.getLatitudeE6(), lonEastE6);
			final GeoPoint westCenter = new GeoPoint(centerPoint.getLatitudeE6(), lonWestE6);

			final int totalScreenMeters = eastCenter.distanceTo(westCenter);

			final int viewWidthPx = OSMMapView.this.getWidth();

			return (aMeters / totalScreenMeters) * viewWidthPx;
		}

		public RectF toPixels(final BoundingBoxE6 pBoundingBoxE6) {
			final RectF rect = new RectF();

			final Point reuse = new Point();

			toPixels(new GeoPoint(pBoundingBoxE6.getLatNorthE6(), pBoundingBoxE6.getLonWestE6()), reuse);
			rect.left = reuse.x;
			rect.top = reuse.y;

			toPixels(new GeoPoint(pBoundingBoxE6.getLatSouthE6(), pBoundingBoxE6.getLonEastE6()), reuse);
			rect.right = reuse.x;
			rect.bottom = reuse.y;

			return rect;
		}

		/**
		 * Converts a GeoPoint to its ScreenCoordinates. <br/>
		 * <br/>
		 * <b>CAUTION</b> ! Conversion may have a large error on <code>zoomLevels <= 7</code>.<br/>
		 * The Error on ZoomLevels higher than 7, the error is below <code>1px</code>.<br/>
		 * <PRE>Zoom 	Error(m) 	Error(px)
		 * 11 	6m 	1/12px
		 * 10 	24m 	1/6px
		 * 8 	384m 	1/2px
		 * 6 	6144m 	3px
		 * 4 	98304m 	10px </PRE>
		 * @param in the GeoPoint you want the onScreenCoordinates of.
		 * @param reuse just pass null if you do not have a Point to be 'recycled'.
		 * @return the Point containing the approximated ScreenCoordinates of the GeoPoint passed.
		 */
		public Point toPixels(final IGeoPoint in, final Point reuse) {
			return toPixels(in, reuse, true);
		}

		public Point toPixels(final int latE6, final int lonE6, final Point reuse) {
			return toPixels(latE6, lonE6, reuse, true);
		}

		protected Point toPixels(final IGeoPoint in, final Point reuse, final boolean doGudermann){
			return toPixels(in.getLatitudeE6(), in.getLongitudeE6(), reuse, doGudermann);
		}

		protected Point toPixels(final int latE6, final int lonE6, final Point reuse, final boolean doGudermann){

			final Point out = (reuse != null) ? reuse : new Point();

			final OSMTileInfo underGeopointTileCoords = Util.getMapTileFromCoordinates(latE6, lonE6, this.centerMapTileCoords.zoom);

			/* Calculate the Latitude/Longitude on the left-upper ScreenCoords of the MapTile. */
			final BoundingBoxE6 bb = Util.getBoundingBoxFromMapTile(underGeopointTileCoords);


			final float[] relativePositionInCenterMapTile;
			if(doGudermann && this.centerMapTileCoords.zoom < 7) {
				relativePositionInCenterMapTile = bb.getRelativePositionOfGeoPointInBoundingBoxWithExactGudermannInterpolation(latE6, lonE6, null);
			} else {
				relativePositionInCenterMapTile = bb.getRelativePositionOfGeoPointInBoundingBoxWithLinearInterpolation(latE6, lonE6, null);
			}

			final int tileDiffX = this.centerMapTileCoords.x - underGeopointTileCoords.x;
			final int tileDiffY = this.centerMapTileCoords.y - underGeopointTileCoords.y;
			final int underGeopointTileScreenLeft = this.upperLeftCornerOfCenterMapTile.x - (this.tileSizePx * tileDiffX);
			final int underGeopointTileScreenTop = this.upperLeftCornerOfCenterMapTile.y - (this.tileSizePx * tileDiffY);

			final int x = underGeopointTileScreenLeft + (int)(relativePositionInCenterMapTile[X] * this.tileSizePx);
			final int y = underGeopointTileScreenTop + (int)(relativePositionInCenterMapTile[Y] *this.tileSizePx);

			/* Add up the offset caused by touch. */
			out.set(x + OSMMapView.this.mTouchMapOffsetX, y + OSMMapView.this.mTouchMapOffsetY);
			return out;
		}

		public Path toPixels(final List<IGeoPoint> in, final Path reuse) {
			return toPixels(in, reuse, true);
		}

		protected Path toPixels(final List<IGeoPoint> in, final Path reuse, final boolean doGudermann) throws IllegalArgumentException {
			if(in.size() < 2) {
				throw new IllegalArgumentException("List of GeoPoints needs to be at least 2.");
			}

			final Path out = (reuse != null) ? reuse : new ManagedLinePath();

			int i = 0;
			for (final IGeoPoint gp : in) {
				i++;
				final OSMTileInfo underGeopointTileCoords = Util.getMapTileFromCoordinates(gp.getLatitudeE6(), gp.getLongitudeE6(), this.centerMapTileCoords.zoom);

				/* Calculate the Latitude/Longitude on the left-upper ScreenCoords of the MapTile. */
				final BoundingBoxE6 bb = Util.getBoundingBoxFromMapTile(underGeopointTileCoords);


				final float[] relativePositionInCenterMapTile;
				if(doGudermann && this.centerMapTileCoords.zoom < 7) {
					relativePositionInCenterMapTile = bb.getRelativePositionOfGeoPointInBoundingBoxWithExactGudermannInterpolation(gp.getLatitudeE6(), gp.getLongitudeE6(), null);
				} else {
					relativePositionInCenterMapTile = bb.getRelativePositionOfGeoPointInBoundingBoxWithLinearInterpolation(gp.getLatitudeE6(), gp.getLongitudeE6(), null);
				}

				final int tileDiffX = this.centerMapTileCoords.x - underGeopointTileCoords.x;
				final int tileDiffY = this.centerMapTileCoords.y - underGeopointTileCoords.y;
				final int underGeopointTileScreenLeft = this.upperLeftCornerOfCenterMapTile.x - (this.tileSizePx * tileDiffX);
				final int underGeopointTileScreenTop = this.upperLeftCornerOfCenterMapTile.y - (this.tileSizePx * tileDiffY);

				final int x = underGeopointTileScreenLeft + (int)(relativePositionInCenterMapTile[X] * this.tileSizePx);
				final int y = underGeopointTileScreenTop + (int)(relativePositionInCenterMapTile[Y] *this.tileSizePx);

				/* Add up the offset caused by touch. */
				out.lineTo(x + OSMMapView.this.mTouchMapOffsetX, y + OSMMapView.this.mTouchMapOffsetY);
			}

			return out;
		}
	}

	private class SimpleInvalidationHandler extends Handler{
		@Override
		public void handleMessage(final Message msg) {
			switch(msg.what){
				case OSMAbstractMapTileProvider.MAPTILEPROVIDER_SUCCESS_ID:
				case OSMMapTileFilesystemCache.MAPTILEFSCACHE_SUCCESS_ID:
					if(msg.arg1 != 0 && msg.arg2 != 0){
						final OSMTileInfo coords = new OSMTileInfo(msg.arg1, msg.arg2, OSMMapView.this.mZoomLevel);
						final BoundingBoxE6 bb = Util.getBoundingBoxFromMapTile(coords);
						final OSMMapViewProjection pj = getProjection();

						final Point reuse = new Point();
						pj.toPixels(bb.getLatNorthE6(), bb.getLonWestE6(), reuse);
						final int l = reuse.x;
						final int t = reuse.y;

						final int r = l + OSMMapView.this.mProviderInfo.MAPTILE_SIZEPX;
						final int b = t + OSMMapView.this.mProviderInfo.MAPTILE_SIZEPX;

						OSMMapView.this.invalidate(l, t, r, b);
					}else{
						OSMMapView.this.invalidate();
					}
					break;
			}
		}
	}

	private class OpenStreetMapViewGestureDetectorListener implements OnGestureListener{
		public boolean onDown(final MotionEvent e) {
			return false;
		}

		public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
			// TODO Could be used for smoothly 'fling-out' the map on a fast motion.
			return false;
		}

		public void onLongPress(final MotionEvent e) {
			OSMMapView.this.onLongPress(e);
		}

		public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
			return false;
		}

		public void onShowPress(final MotionEvent e) { }

		public boolean onSingleTapUp(final MotionEvent e) {
			return OSMMapView.this.onSingleTapUp(e);
		}
	}
}
