//Created by plusminus on 19:05:55 - 12.02.2008
package org.andnav2.ui.map;

import java.util.ArrayList;
import java.util.List;

import org.andnav2.R;
import org.andnav2.adt.AndNavLocation;
import org.andnav2.adt.DBPOI;
import org.andnav2.adt.Direction;
import org.andnav2.adt.TrafficFeed;
import org.andnav2.adt.UnitSystem;
import org.andnav2.app.APIIntentReceiver;
import org.andnav2.db.DBManager;
import org.andnav2.db.DataBaseException;
import org.andnav2.exc.Exceptor;
import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.util.CoordinatesExtractor;
import org.andnav2.osm.views.OSMMapView;
import org.andnav2.osm.views.OSMMapViewScaleIndicatorView;
import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;
import org.andnav2.osm.views.OSMMapView.OnChangeListener;
import org.andnav2.osm.views.controller.OSMMapViewController.AnimationType;
import org.andnav2.osm.views.overlay.AbstractOSMMapViewItemizedOverlay;
import org.andnav2.osm.views.overlay.AbstractOSMMapViewItemizedOverlayWithFocus;
import org.andnav2.osm.views.overlay.BaseOSMMapViewListItemizedOverlayWithFocus;
import org.andnav2.osm.views.overlay.OSMMapViewCrosshairOverlay;
import org.andnav2.osm.views.overlay.OSMMapViewDirectedLocationOverlay;
import org.andnav2.osm.views.overlay.OSMMapViewItemizedOverlayControlView;
import org.andnav2.osm.views.overlay.OSMMapViewOverlay;
import org.andnav2.osm.views.overlay.OSMMapViewOverlayItem;
import org.andnav2.osm.views.overlay.OSMMapViewSimpleLineOverlay;
import org.andnav2.osm.views.overlay.OSMMapViewSingleIconOverlay;
import org.andnav2.osm.views.overlay.AbstractOSMMapViewItemizedOverlay.OnItemTapListener;
import org.andnav2.osm.views.tiles.OSMMapTileManager;
import org.andnav2.osm.views.tiles.OSMMapTileProviderInfo;
import org.andnav2.osm.views.tiles.adt.OSMTileInfo;
import org.andnav2.osm.views.tiles.caching.OSMMapTileFilesystemCache;
import org.andnav2.osm.views.tiles.util.OSMMapTilePreloader;
import org.andnav2.osm.views.tiles.util.OSMMapTilePreloader.OnProgressChangeListener;
import org.andnav2.osm.views.util.Util;
import org.andnav2.preferences.PreferenceConstants;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.aas.AASRequester;
import org.andnav2.sys.ors.aas.AASResponse;
import org.andnav2.sys.ors.adt.GeocodedAddress;
import org.andnav2.sys.ors.adt.aoi.AreaOfInterest;
import org.andnav2.sys.ors.adt.aoi.Polygon;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.ors.adt.lus.ReverseGeocodePreferenceType;
import org.andnav2.sys.ors.adt.ts.ISpatialDataOrganizer;
import org.andnav2.sys.ors.adt.ts.TrafficItem;
import org.andnav2.sys.ors.adt.ts.TrafficOverlayManager;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.sys.ors.lus.LUSRequester;
import org.andnav2.sys.ors.tuks.TUKSRequester;
import org.andnav2.sys.ors.util.RouteHandleIDExtractor;
import org.andnav2.sys.ors.views.overlay.AreaOfInterestOverlay;
import org.andnav2.sys.ors.views.overlay.TrafficOverlay;
import org.andnav2.sys.ors.views.overlay.TrafficOverlayItem;
import org.andnav2.sys.vehicleregistrationplates.VRPRegistry;
import org.andnav2.sys.vehicleregistrationplates.tables.IVRPElement;
import org.andnav2.ui.common.CommonCallback;
import org.andnav2.ui.common.CommonCallbackAdapter;
import org.andnav2.ui.common.CommonDialogFactory;
import org.andnav2.ui.common.InlineAutoCompleterConstant;
import org.andnav2.ui.common.views.CompassImageView;
import org.andnav2.ui.common.views.CompassRotateView;
import org.andnav2.ui.sd.SDMainChoose;
import org.andnav2.ui.weather.WeatherForecast;
import org.andnav2.util.FileSizeFormatter;
import org.andnav2.util.UserTask;
import org.andnav2.util.constants.Constants;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.admob.android.ads.AdView;


public class WhereAmIMap extends OpenStreetMapAndNavBaseActivity implements PreferenceConstants, Constants, AbstractOSMMapViewItemizedOverlay.OnItemTapListener<OSMMapViewOverlayItem>{

	// ===========================================================
	// Final Fields
	// ===========================================================

	/** Time in milliseconds the Autocentering is disabled, after the user panned the map. */
	private static final int AUTOCENTER_BLOCKTIME = 5000;

	private static final int REQUESTCODE_WEATHER = 0;
	private static final int REQUESTCODE_OSBMAP = REQUESTCODE_WEATHER + 1;
	private static final int REQUESTCODE_STRUCTURED_SEARCH_SD_MAINCHOOSE = REQUESTCODE_OSBMAP + 1;
	private static final int REQUESTCODE_DDMAP = REQUESTCODE_STRUCTURED_SEARCH_SD_MAINCHOOSE + 1;

	private final int LAT_INDEX = 0;
	private final int LON_INDEX = 1;

	private static final int MENU_QUIT_ID = Menu.FIRST;
	private static final int MENU_SATELLITE_ID = MENU_QUIT_ID + 1;
	private static final int MENU_SUBMENU_TRAFFIC_ID = MENU_SATELLITE_ID + 1;
	private static final int MENU_WEATHER_ID = MENU_SUBMENU_TRAFFIC_ID + 1;
	private static final int MENU_OSB_ID = MENU_WEATHER_ID + 1;
	private static final int MENU_COMPASS_ID = MENU_OSB_ID + 1;
	private static final int MENU_PRELOAD_ID = MENU_COMPASS_ID + 1;
	private static final int MENU_ACCESSIBILITYANALYSIS_ID = MENU_PRELOAD_ID + 1;
	private static final int MENU_SUBMENU_RENDERERS_ID = MENU_ACCESSIBILITYANALYSIS_ID + 1;
	private static final int MENU_LOAD_TRACE_ID = MENU_SUBMENU_RENDERERS_ID + 1;
	private static final int MENU_SHOWLATLON_ID = MENU_LOAD_TRACE_ID + 1;
	private static final int MENU_INPUTLATLON_ID = MENU_SHOWLATLON_ID + 1;
	private static final int MENU_VEHICLEREGISTRATIONPLATE_LOOKUP_ID = MENU_INPUTLATLON_ID + 1;
	private static final int MENU_SUBMENU_TRAFFIC_CUSTOM = MENU_VEHICLEREGISTRATIONPLATE_LOOKUP_ID + 1;
	private static final int MENU_SUBMENU_TRAFFIC_CLEAR = MENU_SUBMENU_TRAFFIC_CUSTOM + 1;
	private static final int MENU_GPSSTATUS_ID = MENU_SUBMENU_TRAFFIC_CLEAR + 1;

	private static final int MENU_SUBMENU_LAYERS_OFFSET = 1000;

	private static final int DIALOG_SELECT_CUSTOM_TRAFFIC_FEED = 0;
	private static final int DIALOG_ADD_CUSTOM_TRAFFIC_FEED = DIALOG_SELECT_CUSTOM_TRAFFIC_FEED + 1;
	private static final int DIALOG_INPUT_LAT_LON = DIALOG_ADD_CUSTOM_TRAFFIC_FEED + 1;
	private static final int DIALOG_SELECT_FREEFORM_OR_STRUCTURED_SEARCH = DIALOG_INPUT_LAT_LON + 1;
	private static final int DIALOG_INPUT_FAVORITE_NAME = DIALOG_SELECT_FREEFORM_OR_STRUCTURED_SEARCH + 1;
	private static final int DIALOG_SELECT_TRAFFICFEED_FILTER_QUARTER = DIALOG_INPUT_FAVORITE_NAME + 1;
	private static final int DIALOG_NOTINLITEVERSION = DIALOG_SELECT_TRAFFICFEED_FILTER_QUARTER + 1;
	private static final int DIALOG_SELECT_VEHICLEREGISTRATIONPLATE_LOOKUP_COUNTRIES = DIALOG_NOTINLITEVERSION + 1;
	private static final int DIALOG_INPUT_VEHICLEREGISTRATIONPLATE_LOOKUP = DIALOG_SELECT_VEHICLEREGISTRATIONPLATE_LOOKUP_COUNTRIES + 1;


	private static final int CENTERMODE_NONE = 0;
	private static final int CENTERMODE_ONCE = CENTERMODE_NONE + 1;
	private static final int CENTERMODE_AUTO = CENTERMODE_ONCE + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	/** Holds the timestamp until the AutoCentering is blocked, because the user has panned the map. */
	private long mAutoCenterBlockedUntil = 0;

	private SensorManager mSensorManager;

	private ImageButton mIbtnCenter;
	private ImageButton mIbtnWhereAmI;
	private ImageButton mIbtnSearch;
	private ImageButton mIbtnChooseRenderer;
	private EditText mEtSearch;
	private ImageButton mIbtnNavPointsDoStart;
	private ImageButton mIbtnNavPointsDoCancel;
	private ImageButton mIbtnNavPointsSetStart;
	private ImageButton mIbtnNavPointsSetDestination;
	private GeoPoint mGPLastMapClick;

	private OSMMapViewItemizedOverlayControlView mMapItemControlView;
	private OSMMapViewScaleIndicatorView mScaleIndicatorView;

	private OSMMapViewDirectedLocationOverlay mMyLocationOverlay;
	private OSMMapViewCrosshairOverlay mCrosshairOverlay;

	private TrafficFeed mCurrentTrafficFeed;

	private int mDoCenter = this.CENTERMODE_AUTO;

	private CompassRotateView mCompassRotateView;

	private ArrayList<OSMMapViewOverlayItem> mSearchPinList;
	/** Currently selected index in mSearchPinList. */
	private int mSearchPinListIndex;

	private AbstractOSMMapViewItemizedOverlayWithFocus<OSMMapViewOverlayItem> mItemOverlay;
	private AreaOfInterestOverlay mAASOverlay;
	private TrafficOverlay mTrafficOverlay;
	private OSMMapViewSingleIconOverlay mStartFlagOverlay;
	private OSMMapViewSingleIconOverlay mDestinationFlagOverlay;
	private OSMMapViewSimpleLineOverlay mNavPointsConnectionLineOverlay;


	private CompassImageView mIvCompass;

	/** Keeps the screen alive when it would lock otherwise. */
	private PowerManager.WakeLock mWakeLock;

	private Animation mFadeOutDelayedAnimation;
	private Animation mFadeToLeftAnimation;
	private Animation mFadeToRightAnimation;
	private Animation mFadeOutQuickAnimation;

	private AreaOfInterestOverlay mAreaOfAvoidingsOverlay;

	private final ArrayList<AreaOfInterest> mAvoidAreas = new ArrayList<AreaOfInterest>();

	private boolean mNavPointsCrosshairMode;


	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onSetupContentView() {
		this.setContentView(R.layout.whereami_map);
		super.mOSMapView = (OSMMapView)findViewById(R.id.map_whereami);
		super.mOSMapView.setProviderInfo(Preferences.getMapViewProviderInfoWhereAmI(this));

		final List<OSMMapViewOverlay> overlays = this.mOSMapView.getOverlays();

		/* Add a new instance of our fancy Overlay-Class to the MapView. */

		this.mMyLocationOverlay = new OSMMapViewDirectedLocationOverlay(this, Preferences.getHUDImplVariationDirectionArrowDescriptor(this));
		this.mMyLocationOverlay.setLocation(getLastKnownLocation(true));

		this.mTrafficOverlay = new TrafficOverlay(this, new ArrayList<TrafficOverlayItem>(), new OnItemTapListener<TrafficOverlayItem>(){
			public boolean onItemTap(final int index, final TrafficOverlayItem item) {
				if(index >= WhereAmIMap.this.mTrafficOverlay.getOverlayItems().size()) {
					throw new IllegalArgumentException();
				}

				final TrafficOverlayItem focusedItem = WhereAmIMap.this.mTrafficOverlay.getFocusedItem();
				if(!item.equals(focusedItem)){
					WhereAmIMap.this.mTrafficOverlay.setFocusedItem(item);
				}else{
					WhereAmIMap.this.mTrafficOverlay.unSetFocusedItem();
				}

				WhereAmIMap.this.mOSMapView.getController().animateTo(item, AnimationType.MIDDLEPEAKSPEED);

				return true;
			}
		});
		this.mTrafficOverlay.setDrawnItemsLimit(50);
		this.mTrafficOverlay.setAutoFocusItemsOnTap(false);

		this.mAASOverlay = new AreaOfInterestOverlay();
		this.mAASOverlay.setDrawnAreasLimit(10);
		this.mAreaOfAvoidingsOverlay = new AreaOfInterestOverlay(this.mAvoidAreas);

		/* SetNavPoints-Overlay. */
		this.mCrosshairOverlay = new OSMMapViewCrosshairOverlay(Color.BLACK, 2, 17);
		this.mCrosshairOverlay.setVisible(false);
		this.mStartFlagOverlay = new OSMMapViewSingleIconOverlay(this, R.drawable.flag_start, new Point(18,47));
		this.mDestinationFlagOverlay = new OSMMapViewSingleIconOverlay(this, R.drawable.flag_destination, new Point(18,47));
		this.mNavPointsConnectionLineOverlay = new OSMMapViewSimpleLineOverlay();
		this.mNavPointsConnectionLineOverlay.setPaintNormal();
		this.mNavPointsConnectionLineOverlay.setVisible(false);

		overlays.add(this.mAASOverlay);
		overlays.add(this.mAreaOfAvoidingsOverlay);
		overlays.add(this.mTrafficOverlay);
		overlays.add(this.mNavPointsConnectionLineOverlay);
		overlays.add(this.mStartFlagOverlay);
		overlays.add(this.mDestinationFlagOverlay);
		overlays.add(this.mMyLocationOverlay);
		overlays.add(this.mCrosshairOverlay);

		//		final BoundingBoxE6 bbox = BoundingBoxE6.fromParams("-n 43.77677 -s 36.05546 -e 4.34829 -w -9.31870".split(" "));
		//		final OSMMapViewSimpleRectangleOverlay overlay = new OSMMapViewSimpleRectangleOverlay(bbox);
		//		overlay.initDefaultPaint();
		//		overlays.add(overlay);
	}

	private void refreshPinOverlay(final GeoPoint pGeoPoint) {
		final ArrayList<OSMMapViewOverlayItem> items = new ArrayList<OSMMapViewOverlayItem>();
		items.add(new OSMMapViewOverlayItem(WhereAmIMap.this, pGeoPoint));
		refreshPinOverlay(items);
		WhereAmIMap.this.updateUIForAutoCenterChange(this.CENTERMODE_NONE);
		WhereAmIMap.super.mOSMapView.getController().animateTo(pGeoPoint, AnimationType.MIDDLEPEAKSPEED);
	}

	private void refreshPinOverlay(final ArrayList<OSMMapViewOverlayItem> items){
		this.mSearchPinListIndex = 0;

		clearPinOverlay();

		this.mMapItemControlView.setVisibility(View.VISIBLE);

		final boolean nextPreviousEnabled = items.size() > 1;
		this.mMapItemControlView.setNextEnabled(nextPreviousEnabled);
		this.mMapItemControlView.setPreviousEnabled(nextPreviousEnabled);

		this.mSearchPinList = items;

		this.mOSMapView.getOverlays().add(this.mItemOverlay = new BaseOSMMapViewListItemizedOverlayWithFocus<OSMMapViewOverlayItem>(this, this.mSearchPinList, this));
		this.mItemOverlay.setAutoFocusItemsOnTap(false);
	}


	private void clearPinOverlay() {
		this.mMapItemControlView.setVisibility(View.GONE);

		if(this.mSearchPinList != null) {
			this.mSearchPinList.clear();
		}

		final List<OSMMapViewOverlay> overlays = this.mOSMapView.getOverlays();
		if(this.mItemOverlay != null) {
			overlays.remove(this.mItemOverlay);
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);

		/* Load all the Views. */
		if(Preferences.getAdFreeState(this) == false)
			((AdView)findViewById(R.id.ad)).setGoneWithoutAd(true);
		else
			((AdView)findViewById(R.id.ad)).setVisibility(View.GONE);

		this.mIbtnCenter = (ImageButton)this.findViewById(R.id.ibtn_whereami_center);
		this.mIbtnWhereAmI = (ImageButton)this.findViewById(R.id.ibtn_whereami_whereami);
		this.mIbtnSearch = (ImageButton)this.findViewById(R.id.ibtn_whereami_search);
		this.mEtSearch = (EditText)this.findViewById(R.id.et_whereami_search);
		this.mIbtnChooseRenderer = (ImageButton)this.findViewById(R.id.ibtn_whereami_choose_renderer);
		this.mIbtnNavPointsSetStart = (ImageButton)this.findViewById(R.id.ibtn_whereami_setstartpoint);
		this.mIbtnNavPointsSetDestination = (ImageButton)this.findViewById(R.id.ibtn_whereami_setendpoint);
		this.mIbtnNavPointsDoStart = (ImageButton)this.findViewById(R.id.ibtn_whereami_setnavpoints_start);
		this.mIbtnNavPointsDoCancel = (ImageButton)this.findViewById(R.id.ibtn_whereami_setnavpoints_cancel);
		this.mCompassRotateView = (CompassRotateView)this.findViewById(R.id.rotator_wheramimap);
		this.mMapItemControlView = (OSMMapViewItemizedOverlayControlView)this.findViewById(R.id.itemizedoverlaycontrol_whereami);
		this.mScaleIndicatorView = (OSMMapViewScaleIndicatorView)this.findViewById(R.id.scaleindicatorview_whereami);
		this.mScaleIndicatorView.setUnitSystem(Preferences.getUnitSystem(this));

		/* Load the animation from XML (XML file is res/anim/***.xml). */
		this.mFadeOutDelayedAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out_delayed);
		this.mFadeOutDelayedAnimation.setFillAfter(true);

		this.mFadeOutQuickAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
		this.mFadeOutQuickAnimation.setFillAfter(true);

		this.mFadeToLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_to_left);
		this.mFadeToLeftAnimation.setFillAfter(true);

		this.mFadeToRightAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_to_right);
		this.mFadeToRightAnimation.setFillAfter(true);

		/* Run the Hide-Icons animation on the start, because no touch is applied yet. */
		this.startDelayedHideControlsAnimation();


		this.mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		/* This code together with the one in onResume() will make the screen be always on during navigation. */
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyWakeLock");
		this.mWakeLock.acquire();

		this.mIvCompass = (CompassImageView)this.findViewById(R.id.iv_whereami_compass);

		this.mOSMapView.addChangeListener(new OnChangeListener(){
			public void onChange() {
				runOnUiThread(new Runnable(){
					public void run() {
						WhereAmIMap.this.mScaleIndicatorView.refresh(WhereAmIMap.this.mOSMapView);
					}
				});
			}
		});


		this.applyQuickButtonListeners();
		this.applyZoomButtonListeners();
		this.applyMapViewLongPressListener();
		this.applyAutoCompleteListeners();


		final boolean doDefault = !handlePossibleAction();

		if(doDefault){
			final GeoPoint location = super.getLastKnownLocation(true);
			if(location == null || Math.abs(location.getLatitudeE6()) <= 100 || Math.abs(location.getLongitudeE6()) <= 100) {
				this.mOSMapView.setZoomLevel(3);
			} else{
				this.mOSMapView.setZoomLevel(13);
				this.mOSMapView.setMapCenter(location);
			}

			/* Show the user why the map is auto-centering on the user. */
			Toast.makeText(WhereAmIMap.this, R.string.toast_autofollow_enabled, Toast.LENGTH_SHORT).show();
			this.mDoCenter = this.CENTERMODE_AUTO;
		}

		/* forces the ScaleIndicator-View to be refreshed in the beginning. */
		super.mOSMapView.forceFireOnChangeListeners();
	}

	/**
	 * 
	 * @return <code>true</code> whether an action was correctly recognized and handled.
	 */
	private boolean handlePossibleAction() {
		final Intent iStartedWith = this.getIntent();
		final String action = iStartedWith.getAction();
		if(action != null){
			if(action.equals(ANDNAV2_VIEW_ACTION)){
				final Bundle extras = iStartedWith.getExtras();
				/* Extract geopoint-Strings from the Bundle. */
				final ArrayList<String> geoPointStrings = extras.getStringArrayList(APIIntentReceiver.WHEREAMI_EXTRAS_LOCATIONS_ID);

				if(geoPointStrings.size() > 0){
					/* And convert them to actual GeoPoints */
					final ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>(geoPointStrings.size());
					for (final String locationString : geoPointStrings) {
						geoPoints.add(GeoPoint.fromIntString(locationString));
					}


					/* Extract descriptions and titles from the Bundle. */
					final ArrayList<String> descriptions = extras.getStringArrayList(APIIntentReceiver.WHEREAMI_EXTRAS_LOCATIONS_DESCRIPTIONS_ID);
					final ArrayList<String> titles = extras.getStringArrayList(APIIntentReceiver.WHEREAMI_EXTRAS_LOCATIONS_TITLES_ID);


					/* Create overlay-items from the data extracted. */
					final ArrayList<OSMMapViewOverlayItem> items = new ArrayList<OSMMapViewOverlayItem>(geoPointStrings.size());
					for(int i = 0; i < geoPointStrings.size(); i++) {
						items.add(new OSMMapViewOverlayItem(titles.get(i), descriptions.get(i), geoPoints.get(i)));
					}

					/* Calculate the BoundingBox around the items. */
					final BoundingBoxE6 itemBoundingBoxE6 = BoundingBoxE6.fromGeoPoints(geoPoints);

					refreshPinOverlay(items);

					WhereAmIMap.this.updateUIForAutoCenterChange(this.CENTERMODE_NONE);

					new Handler().postDelayed(new Runnable(){
						public void run() {
							if(items.size() == 1) {
								WhereAmIMap.super.mOSMapView.setZoomLevel(13);
							} else {
								WhereAmIMap.super.mOSMapView.getController().zoomToSpan(itemBoundingBoxE6);
							}

							WhereAmIMap.super.mOSMapView.getController().animateTo(itemBoundingBoxE6.getCenter(), AnimationType.MIDDLEPEAKSPEED);
						}
					}, 500);

					return true;
				}
			}else if(action.equals(android.content.Intent.ACTION_VIEW)){
				final Uri data = iStartedWith.getData();
				if(data != null && data.getScheme().equals("geo")){

					/* Extract lat/lon-String. */
					final String coordsString = iStartedWith.getData().getSchemeSpecificPart();
					if(coordsString.length() > 0){
						final String[] coordinates = coordsString.split(",");
						try{
							final double lat = Double.parseDouble(coordinates[this.LAT_INDEX]);
							final double lon = Double.parseDouble(coordinates[this.LON_INDEX]);

							this.mOSMapView.setZoomLevel(15);
							this.mOSMapView.setMapCenter(new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6)));

							this.mDoCenter = this.CENTERMODE_NONE;
							return true;
						}catch(final NumberFormatException nfe){
							final int qParamIndex = coordsString.indexOf("q=");
							if(qParamIndex != -1){
								final String textualQuery = coordsString.substring(qParamIndex + "q=".length());
								if(textualQuery.length() > 0){
									searchORSLocations(textualQuery);

									this.mEtSearch.setText(textualQuery);

									this.mDoCenter = this.CENTERMODE_NONE;
									return true;
								}
							}else{
								Log.d(Constants.DEBUGTAG, "Could not parse \"" + iStartedWith.getData().toString() + "\"-Uri");
							}
						}
					}
				}
			}
		}
		return false;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	private void startDelayedHideControlsAnimation(){
		/* Left icons */
		if(this.mEtSearch.getVisibility() != View.VISIBLE) {
			this.mIbtnSearch.startAnimation(this.mFadeToLeftAnimation);
		}

		if(this.mNavPointsCrosshairMode == false){
			this.mIbtnNavPointsDoStart.startAnimation(this.mFadeToLeftAnimation);
			this.mIbtnCenter.startAnimation(this.mFadeToRightAnimation);
		}

		/* Right icons */
		this.mIbtnWhereAmI.startAnimation(this.mFadeToRightAnimation);
		this.mIbtnChooseRenderer.startAnimation(this.mFadeToRightAnimation);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch(requestCode){
			case REQUESTCODE_STRUCTURED_SEARCH_SD_MAINCHOOSE:
				if(resultCode == SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS || resultCode == SUBACTIVITY_RESULTCODE_SUCCESS){
					final Bundle b = data.getExtras();
					final ArrayList<OSMMapViewOverlayItem> items = new ArrayList<OSMMapViewOverlayItem>();

					final int latE6 = b.getInt(EXTRAS_DESTINATION_LATITUDE_ID);
					final int lonE6 = b.getInt(EXTRAS_DESTINATION_LONGITUDE_ID);
					final GeoPoint gp = new GeoPoint(latE6, lonE6);

					items.add(new OSMMapViewOverlayItem(this, gp));
					refreshPinOverlay(items);
					WhereAmIMap.this.updateUIForAutoCenterChange(this.CENTERMODE_NONE);
					WhereAmIMap.super.mOSMapView.getController().animateTo(gp, AnimationType.MIDDLEPEAKSPEED);
				}
				break;
		}
	}

	@Override
	public void release(){
		// Nothing...
	}

	@Override
	public void onDestroy() {
		Log.d(Constants.DEBUGTAG, "OnDESTROY");

		this.mWakeLock.release();

		super.onDestroy();
	}

	private final String STATE_COMPASSMODE_ID = "state_compassmode_id";
	private final String STATE_AUTOCENTER_ID = "state_autocenter_id";
	private final String STATE_ETSEARCHVISIBLE_ID = "state_etsearchvisible_id";
	private final String STATE_ZOOM_ID = "state_zoom_id";
	private final String STATE_MAPCENTER_ID = "state_mapcenter_id";
	private final String STATE_VEHICLEREGISTRATIONPLATE_NATIONALITY_ID = "state_crp_nationality_id";

	protected Country mVehicleRegistrationPlateLOokupNationality;

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		if(savedInstanceState == null) {
			return;
		}

		super.onRestoreInstanceState(savedInstanceState);

		if(savedInstanceState.getBoolean(this.STATE_COMPASSMODE_ID, false)) {
			toggleCompass();
		}

		updateUIForAutoCenterChange(savedInstanceState.getInt(this.STATE_AUTOCENTER_ID, this.CENTERMODE_AUTO));

		if(savedInstanceState.getBoolean(this.STATE_ETSEARCHVISIBLE_ID)) {
			handleSearchOpen();
		}

		this.mOSMapView.setZoomLevel(savedInstanceState.getInt(this.STATE_ZOOM_ID, 13));

		final GeoPoint mapCenter = savedInstanceState.getParcelable(this.STATE_MAPCENTER_ID);
		this.mOSMapView.setMapCenter(mapCenter);

		final String vrpLookupNationalityString = savedInstanceState.getString(this.STATE_VEHICLEREGISTRATIONPLATE_NATIONALITY_ID);
		if(vrpLookupNationalityString != null){
			this.mVehicleRegistrationPlateLOokupNationality = Country.fromAbbreviation(vrpLookupNationalityString);
		}
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean(this.STATE_COMPASSMODE_ID, this.mCompassRotateView.isActive());
		outState.putInt(this.STATE_AUTOCENTER_ID, this.mDoCenter);

		outState.putBoolean(this.STATE_ETSEARCHVISIBLE_ID, this.mEtSearch.getVisibility() == View.VISIBLE);

		outState.putInt(this.STATE_ZOOM_ID, this.mOSMapView.getZoomLevel());

		outState.putParcelable(this.STATE_MAPCENTER_ID, this.mOSMapView.getMapCenter());

		if(this.mVehicleRegistrationPlateLOokupNationality != null) {
			outState.putString(this.STATE_VEHICLEREGISTRATIONPLATE_NATIONALITY_ID, this.mVehicleRegistrationPlateLOokupNationality.COUNTRYCODE);
		}
	}

	/**
	 * Gets called when an item of the PinOverlay gets tapped.
	 */
	public boolean onItemTap(final int index, final OSMMapViewOverlayItem item) {
		if(index >= this.mSearchPinList.size()) {
			throw new IllegalArgumentException();
		}

		this.mSearchPinListIndex = index;
		final TrafficOverlayItem focusedItem = WhereAmIMap.this.mTrafficOverlay.getFocusedItem();
		if(!item.equals(focusedItem)){
			this.mItemOverlay.setFocusedItem(item);
		}else{
			this.mItemOverlay.unSetFocusedItem();
		}

		this.mOSMapView.getController().animateTo(item, AnimationType.MIDDLEPEAKSPEED);

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if((this.mSensorManager.getSensors() & SensorManager.SENSOR_ORIENTATION) != 0){
			this.mSensorManager.registerListener(this.mCompassRotateView, SensorManager.SENSOR_ORIENTATION, SensorManager.SENSOR_DELAY_UI);
			this.mSensorManager.registerListener(this.mIvCompass, SensorManager.SENSOR_ORIENTATION, SensorManager.SENSOR_DELAY_UI);
		}
	}

	@Override
	protected void onPause() {
		if((this.mSensorManager.getSensors() & SensorManager.SENSOR_ORIENTATION) != 0){
			this.mSensorManager.unregisterListener(this.mCompassRotateView);
			this.mSensorManager.unregisterListener(this.mIvCompass);
		}
		super.onPause();
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		switch(keyCode){
			case KeyEvent.KEYCODE_C:
				if((this.mSensorManager.getSensors() & SensorManager.SENSOR_ORIENTATION) != 0) {
					toggleCompass();
				}
				return true;
			case KeyEvent.KEYCODE_SEARCH:
				if(this.mEtSearch.getVisibility() != View.VISIBLE) {
					handleSearchOpen();
				} else {
					handleSearchSubmit(this.mEtSearch.getText().toString());
				}
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		int menuPos = Menu.FIRST;
		menu.setQwertyMode(true);

		{ // Weather-Item
			menu.add(menuPos, MENU_WEATHER_ID, menuPos, getString(R.string.maps_menu_weather))
			.setIcon(R.drawable.weather_get)
			.setAlphabeticShortcut('w');
			menuPos++;
		}

		{ // Compass-Item
			if((this.mSensorManager.getSensors() & SensorManager.SENSOR_ORIENTATION) != 0){
				menu.add(menuPos, MENU_COMPASS_ID, menuPos, getString(R.string.maps_menu_compass))
				.setIcon(R.drawable.compass)
				.setAlphabeticShortcut('c');
				menuPos++;
			}
		}

		{ // Renderers-SubMenuItem
			final SubMenu subMenu = menu.addSubMenu(menuPos, MENU_SUBMENU_RENDERERS_ID, menuPos, getString(R.string.maps_menu_submenu_renderers)).setIcon(R.drawable.layers);
			menuPos++;
			{
				final OSMMapTileProviderInfo[] providers = OSMMapTileProviderInfo.values();
				for(int j = 0; j < providers.length; j ++){
					final SpannableString itemTitle = new SpannableString(providers[j].NAME + " (" + providers[j].DESCRIPTION + ")");
					itemTitle.setSpan(new StyleSpan(Typeface.ITALIC), providers[j].NAME.length(), itemTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					itemTitle.setSpan(new RelativeSizeSpan(0.5f), providers[j].NAME.length(), itemTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					subMenu.add(0, MENU_SUBMENU_LAYERS_OFFSET + j, Menu.NONE, itemTitle);
				}
			}
		}

		{ // Traffic-SubMenuItem
			final SubMenu subMenu = menu.addSubMenu(menuPos, MENU_SUBMENU_TRAFFIC_ID, menuPos, getString(R.string.maps_menu_submenu_traffic)).setIcon(R.drawable.warning_severe);
			menuPos++;
			{
				subMenu.add(0, MENU_SUBMENU_TRAFFIC_CLEAR, Menu.NONE, R.string.maps_menu_submenu_traffic_clear);
				subMenu.add(1, MENU_SUBMENU_TRAFFIC_CUSTOM, Menu.NONE, R.string.maps_menu_submenu_traffic_custom_feeds);
			}
		}

		{ // VRP-Lookup-Item
			menu.add(menuPos, MENU_VEHICLEREGISTRATIONPLATE_LOOKUP_ID, menuPos, getString(R.string.maps_menu_vehicleregistrationplate_lookup))
			.setIcon(R.drawable.vehicleregistrationplate)
			.setAlphabeticShortcut('v');
			menuPos++;
		}

		{ // AAS-Item
			menu.add(menuPos, MENU_ACCESSIBILITYANALYSIS_ID, menuPos, getString(R.string.maps_menu_accessibility_analysis))
			.setIcon(R.drawable.accessibility)
			.setAlphabeticShortcut('a');
			menuPos++;
		}

		{ // GPS-Status-Item
			menu.add(menuPos, MENU_GPSSTATUS_ID, menuPos, getString(R.string.maps_menu_gpsstatus))
			.setIcon(R.drawable.gps_status)
			.setAlphabeticShortcut('g');
			menuPos++;
		}

		{ // Preload-Item
			menu.add(menuPos, MENU_PRELOAD_ID, menuPos, getString(R.string.maps_menu_preload))
			.setIcon(R.drawable.preload)
			.setAlphabeticShortcut('p');
			menuPos++;
		}

		{ // OSB-Item
			menu.add(menuPos, MENU_OSB_ID, menuPos, getString(R.string.maps_menu_osb))
			.setIcon(R.drawable.osb_icon_bug_add)
			.setAlphabeticShortcut('b');
			menuPos++;
		}

		{ // Show Lat/Lng-Item
			menu.add(menuPos, MENU_SHOWLATLON_ID, menuPos, getString(R.string.maps_menu_getcentetcoordinates))
			.setIcon(R.drawable.world)
			.setAlphabeticShortcut('l');
			menuPos++;
		}

		{ // Input Lat/Lon-Item
			menu.add(menuPos, MENU_INPUTLATLON_ID, menuPos, getString(R.string.maps_menu_focus_coordinates))
			.setIcon(R.drawable.world)
			.setAlphabeticShortcut('i');
			menuPos++;
		}

		{ // Close-Item
			if(menu.size() <= 5){ // If there will be no 'more'-item
				menu.add(menuPos, MENU_QUIT_ID, menuPos, getString(R.string.maps_menu_quit))
				.setIcon(R.drawable.exit)
				.setAlphabeticShortcut('q');
			}else{
				// Place it as the fifth.
				menu.add(4, MENU_QUIT_ID, 4, getString(R.string.maps_menu_quit))
				.setIcon(R.drawable.exit)
				.setAlphabeticShortcut('q');
			}
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		/* Refresh Accessibility item. */
		final MenuItem item = menu.findItem(MENU_ACCESSIBILITYANALYSIS_ID);
		item.setTitle((this.mAASOverlay.getAreasOfInterest().size() == 0) ? R.string.maps_menu_accessibility_analysis : R.string.hide);

		menu.findItem(MENU_SUBMENU_TRAFFIC_CLEAR).setVisible(this.mTrafficOverlay.getOverlayItems() != null && this.mTrafficOverlay.getOverlayItems().size() > 0);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		final int itemId = item.getItemId();
		switch(itemId){
			case MENU_ACCESSIBILITYANALYSIS_ID:
				if(this.mAASOverlay.getAreasOfInterest().size() == 0) {
					showAccessibilityAnalysisDialog(item);
				} else {
					this.mAASOverlay.getAreasOfInterest().clear();
				}
				return true;
			case MENU_WEATHER_ID:
				/* LITEVERSION */
				if(PROVERSION) {
					openWeatherDialog(super.mOSMapView.getMapCenter());
				} else {
					showDialog(DIALOG_NOTINLITEVERSION);
				}
				return true;
			case MENU_GPSSTATUS_ID:
				org.andnav2.ui.util.Util.startUnknownActivity(this, "com.eclipsim.gpsstatus.VIEW", "com.eclipsim.gpsstatus");
				return true;
			case MENU_OSB_ID:
				openOSBMap();
				return true;
			case MENU_QUIT_ID:
				this.setResult(Constants.SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				this.finish();
				return true;
			case MENU_COMPASS_ID:
				this.toggleCompass();
				return true;
			case MENU_PRELOAD_ID:
				showPreloadDialog();
				return true;
			case MENU_INPUTLATLON_ID:
				showDialog(DIALOG_INPUT_LAT_LON);
				return true;
			case MENU_SHOWLATLON_ID:
				showCenterLatLonDialog();
				return true;
			case MENU_VEHICLEREGISTRATIONPLATE_LOOKUP_ID:
				showDialog(DIALOG_SELECT_VEHICLEREGISTRATIONPLATE_LOOKUP_COUNTRIES);
				return true;
			case MENU_SUBMENU_TRAFFIC_CLEAR:
				this.mTrafficOverlay.setOverlayItems(null);
				return true;
			case MENU_SUBMENU_TRAFFIC_CUSTOM:
				try {
					final int feedCount = DBManager.getCustomTrafficFeedCount(this);

					if(feedCount > 0) {
						this.showDialog(DIALOG_SELECT_CUSTOM_TRAFFIC_FEED);
					} else {
						this.showDialog(DIALOG_ADD_CUSTOM_TRAFFIC_FEED);
					}
				} catch (final DataBaseException e) {
					// TODO ERROR MESSAGE!
				}
				return true;
			default:
				if(itemId >= MENU_SUBMENU_LAYERS_OFFSET){
					changeProviderInfo(OSMMapTileProviderInfo.values()[item.getItemId() - MENU_SUBMENU_LAYERS_OFFSET]);
					return true;
				}
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onPrepareDialog(final int id, final Dialog d) {
		switch(id){
			case DIALOG_SELECT_CUSTOM_TRAFFIC_FEED:
		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		switch(id){
			case DIALOG_SELECT_VEHICLEREGISTRATIONPLATE_LOOKUP_COUNTRIES:
				return CommonDialogFactory.createNationalitySelectionDialog(this, Country.getAllWithVRPTable(), new CommonCallbackAdapter<Country>(){
					@Override
					public void onSuccess(final Country result) {
						WhereAmIMap.this.mVehicleRegistrationPlateLOokupNationality = result;
						showDialog(DIALOG_INPUT_VEHICLEREGISTRATIONPLATE_LOOKUP);
					}
				});
			case DIALOG_INPUT_VEHICLEREGISTRATIONPLATE_LOOKUP:
				/* TODO proper msg and title. */
				return CommonDialogFactory.createInputDialog(this, R.string.dlg_input_vehicleregistrationplate_lookup_hint, R.string.dlg_input_vehicleregistrationplate_lookup_title, new CommonCallbackAdapter<String>(){
					@Override
					public void onSuccess(final String result) {
						final IVRPElement vrp = VRPRegistry.resolve(WhereAmIMap.this.mVehicleRegistrationPlateLOokupNationality.getVRPTableID(), result);
						if(vrp == null){
							Toast.makeText(WhereAmIMap.this, "Sorry, not found", Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(WhereAmIMap.this, "Found: " + vrp.getAbbreviation() + " = " + vrp.getRepresentation(), Toast.LENGTH_LONG).show();
						}
					}
				});
			case DIALOG_NOTINLITEVERSION:
				return CommonDialogFactory.createNotInLiteVersionDialog(this, new CommonCallbackAdapter<Boolean>(){
					@Override
					public void onSuccess(final Boolean result) {
						if(result){
							// TODO (Wo wirds benutzt ??)
						}
					}
				});
			case DIALOG_SELECT_TRAFFICFEED_FILTER_QUARTER:
				return CommonDialogFactory.createDiagonalDirectionDialog(this, new CommonCallbackAdapter<Direction>(){
					@Override
					public void onSuccess(final Direction result) {
						Toast.makeText(WhereAmIMap.this, R.string.please_wait_a_moment, Toast.LENGTH_LONG).show();

						final BoundingBoxE6 boundingBoxE6 = WhereAmIMap.this.mCurrentTrafficFeed.getNationality().BOUNDINGBOXE6.getQuarter(result);

						receiveTPEGMLTraffic(boundingBoxE6);
					}
				});
			case DIALOG_SELECT_CUSTOM_TRAFFIC_FEED:
				List<TrafficFeed> customTrafficFeeds;
				try {
					customTrafficFeeds = DBManager.getCustomTrafficFeeds(this);
				} catch (final DataBaseException e) {
					customTrafficFeeds = new ArrayList<TrafficFeed>();
				}
				return CommonDialogFactory.createSelectCustomTrafficFeedDialog(this, customTrafficFeeds, new CommonCallbackAdapter<TrafficFeed>(){
					@Override
					public void onSuccess(final TrafficFeed result) {
						if(result != null){
							receiveTrafficFromFeed(result);
						}else{
							showDialog(DIALOG_ADD_CUSTOM_TRAFFIC_FEED);
						}
					}
				});
			case DIALOG_ADD_CUSTOM_TRAFFIC_FEED:
				return CommonDialogFactory.createAddCustomTrafficFeedDialog(WhereAmIMap.this, new CommonCallbackAdapter<TrafficFeed>(){
					@Override
					public void onSuccess(final TrafficFeed result) {
						if(result != null){
							receiveTrafficFromFeed(result);
						}
					}
				});
			case DIALOG_INPUT_LAT_LON:
				return CommonDialogFactory.createInputLatLonDialog(this, new CommonCallback<GeoPoint>(){
					public void onFailure(final Throwable t) {
						Toast.makeText(WhereAmIMap.this, R.string.dlg_input_direct_lat_lon_malformed, Toast.LENGTH_SHORT).show();
					}

					public void onSuccess(final GeoPoint result) {
						refreshPinOverlay(result);
					}
				});
			case DIALOG_SELECT_FREEFORM_OR_STRUCTURED_SEARCH:
				return CommonDialogFactory.createFreeformOrStructuredAddressSelectorDialog(this, new CommonCallbackAdapter<Integer>(){
					@Override
					public void onSuccess(final Integer result) {
						switch(result){
							case 0:
								handleSearchOpen();
								break;
							case 1:
								/* Load SDMainChoose-Activity. */
								final Intent sdIntent = new Intent(WhereAmIMap.this, SDMainChoose.class);

								final Bundle b = new Bundle();
								b.putInt(MODE_SD, MODE_SD_RESOLVE);

								sdIntent.putExtras(b);
								WhereAmIMap.this.startActivityForResult(sdIntent, REQUESTCODE_STRUCTURED_SEARCH_SD_MAINCHOOSE);
								break;
						}
					}
				});
			case DIALOG_INPUT_FAVORITE_NAME:
				return CommonDialogFactory.createInputFavoriteNameDialog(WhereAmIMap.this, new CommonCallback<String>(){
					public void onFailure(final Throwable t) {
						Toast.makeText(WhereAmIMap.this, R.string.toast_error_adding_favorite, Toast.LENGTH_LONG).show();
					}

					public void onSuccess(final String result) {
						try {
							DBManager.addFavorite(WhereAmIMap.this, result, WhereAmIMap.this.mGPLastMapClick.getLatitudeE6(), WhereAmIMap.this.mGPLastMapClick.getLongitudeE6());
						} catch (final DataBaseException e) {
							Toast.makeText(WhereAmIMap.this, R.string.toast_error_adding_favorite, Toast.LENGTH_LONG).show();
						}
					}
				});
			default:
				return null;
		}
	}

	private void receiveTrafficFromFeed(final TrafficFeed pTrafficFeed){
		/* Check if we have the special case of the BBC TrafficFeed. */
		if(pTrafficFeed.getURL().compareToIgnoreCase(TUKSRequester.BBC_TRAFFICFEED_URL) == 0){
			this.mCurrentTrafficFeed = new TrafficFeed(TUKSRequester.BBC_TRAFFICFEED_URL, pTrafficFeed.getName(), Country.UNITEDKINGDOM);
		}else{
			this.mCurrentTrafficFeed = pTrafficFeed;
		}


		/* Check if there is a Nationality set and a Boundingbox available. */
		if(this.mCurrentTrafficFeed.getNationality() != null && this.mCurrentTrafficFeed.getNationality().BOUNDINGBOXE6 != null){
			showDialog(DIALOG_SELECT_TRAFFICFEED_FILTER_QUARTER);
		}else{
			/* Get traffic for whole feed. (not BBox-filtering). */
			receiveTPEGMLTraffic(null);
		}
	}

	private void receiveTPEGMLTraffic(final BoundingBoxE6 bBox){
		/* Check if we have the special case of the BBC TrafficFeed. */
		if(this.mCurrentTrafficFeed.getURL().equals(TUKSRequester.BBC_TRAFFICFEED_URL)){
			receiveTrafficUKBBC(bBox);
		}else{

		}
	}

	private void receiveTrafficUKBBC(final BoundingBoxE6 bBox){
		new UserTask<Void, Void, ISpatialDataOrganizer<TrafficOverlayItem>>(){
			@Override
			public ISpatialDataOrganizer<TrafficOverlayItem> doInBackground(final Void... params) {
				try {
					Log.d(Constants.DEBUGTAG, "Before TUKS.");
					final List<TrafficItem> trafficItems = TUKSRequester.request(bBox);
					Log.d(Constants.DEBUGTAG, "Received TUKS.");

					Log.d(Constants.DEBUGTAG, "Filtering items. (Before: " + trafficItems.size() + ")");
					final List<TrafficOverlayItem> overlayItems = filterTrafficItemsToTrafficOverlayItems(trafficItems); // TODO vllt nur converten...

					Log.d(Constants.DEBUGTAG, "Building index. (Count:" + trafficItems.size() + ")");
					final ISpatialDataOrganizer<TrafficOverlayItem> trafficResult = new TrafficOverlayManager(overlayItems);
					trafficResult.buildIndex();
					Log.d(Constants.DEBUGTAG, "Built index.");

					return trafficResult;
				} catch (final Exception e) {
					Log.d(Constants.DEBUGTAG, "Builign index failed.");
					Exceptor.e("Error getting UK-Traffic.", e, WhereAmIMap.this);
					return null;
				}
			}

			@Override
			public void onPostExecute(final ISpatialDataOrganizer<TrafficOverlayItem> result) {
				if(result != null){
					WhereAmIMap.this.mTrafficOverlay.setSpacialIndexManager(result);
				}else{
					Toast.makeText(WhereAmIMap.this, "Sorry there was a problem receiving the Traffic-Feed.", Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
	}

	/**
	 * Filters by Severity (Excludes: VERY_SLIGHT && SLIGHT)
	 * @return
	 */
	private List<TrafficOverlayItem> filterTrafficItemsToTrafficOverlayItems(final List<TrafficItem> trafficItems) {
		final List<TrafficOverlayItem> overlayItems = new ArrayList<TrafficOverlayItem>(trafficItems.size());
		for (final TrafficItem trafficItem : trafficItems){
			switch(trafficItem.getSeverity()){
				case VERY_SLIGHT:
				case SLIGHT:
					break;
				case UNKNOWN:
				case UNSPECIFIED:
				case MEDIUM:
				case SEVERE:
				case VERY_SEVERE:
					overlayItems.add(new TrafficOverlayItem(WhereAmIMap.this, trafficItem));
					break;
			}
		}

		return overlayItems;
	}

	@Override
	public void onLocationLost(final AndNavLocation pLocation) {
		// TODO anzeigen...
	}

	@Override
	public void onLocationChanged(final AndNavLocation pLocation) {
		if(super.mOSMapView == null || this.mMyLocationOverlay == null) {
			return;
		}

		if(pLocation != null){
			if(this.mMyLocationOverlay != null){
				this.mMyLocationOverlay.setLocation(pLocation);
				if(pLocation.hasBearing()) {
					this.mMyLocationOverlay.setBearing(pLocation.getBearing());
				}
				if(pLocation.hasHorizontalPositioningError()) {
					this.mMyLocationOverlay.setAccuracy(pLocation.getHorizontalPositioningError());
				}
			}
			if(this.mDoCenter == this.CENTERMODE_AUTO && System.currentTimeMillis() > this.mAutoCenterBlockedUntil){
				this.mOSMapView.setMapCenter(pLocation);
			}
		}
		this.mOSMapView.invalidate();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void showCenterLatLonDialog() {
		final GeoPoint mapCenter = WhereAmIMap.this.mOSMapView.getMapCenter();
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.world)
		.setTitle(R.string.coordinates)
		.setMessage(getString(R.string.maps_menu_getcentetcoordinates_message, mapCenter.getLatitudeAsDouble(), mapCenter.getLongitudeAsDouble()))
		.setNeutralButton(R.string.clipboard, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				final ClipboardManager cb = (ClipboardManager) WhereAmIMap.this.getSystemService(Context.CLIPBOARD_SERVICE);

				final String clipboardText = String.format("%.6f %.6f",mapCenter.getLatitudeAsDouble(), mapCenter.getLongitudeAsDouble());
				cb.setText(clipboardText);
				d.dismiss();
			}
		})
		.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
			}
		})
		.create().show();
	}

	private void showPreloadDialog() {
		final OSMMapTileProviderInfo providerInfo = this.mOSMapView.getProviderInfo();

		final String[] zoomLevelsRaw = getResources().getStringArray(R.array.preloader_rectangle_zoomlevels);
		final String[] zoomLevelsForThisRenderer = new String[Math.min(providerInfo.ZOOM_MAXLEVEL + 1, zoomLevelsRaw.length)];
		for(int i = 0; i < zoomLevelsForThisRenderer.length; i++) {
			zoomLevelsForThisRenderer[i] = (zoomLevelsRaw[i] != null) ? zoomLevelsRaw[i] : "" + i;
		}

		new AlertDialog.Builder(this).setSingleChoiceItems(zoomLevelsForThisRenderer, 12, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
				// which is the zoomLevel
				preloadMapTilesUpToZoomLevel(which);
			}
		}).setTitle(R.string.dlg_preloader_rect_maxzoom_title)
		.create().show();
	}

	private void preloadMapTilesUpToZoomLevel(final int pUptoZoomLevel) {
		final OSMMapTileManager pTileManager = WhereAmIMap.this.mOSMapView.getMapTileManager();
		final OSMMapTileProviderInfo providerInfo = this.mOSMapView.getProviderInfo();

		/* For each zoomLevel, get the tiles, that hit the visible Rectangle. */
		final BoundingBoxE6 bbE6Visible = this.mOSMapView.getVisibleBoundingBoxE6();
		final OSMTileInfo[][] tilesNeeded = new OSMTileInfo[pUptoZoomLevel+1][];
		for(int i = 0; i <= pUptoZoomLevel; i++) {
			tilesNeeded[i] = Util.calculateNeededTilesForZoomLevelInBoundingBox(i, bbE6Visible);
		}


		/* Calculate the number of tiles to download. */
		long tmpCount = 0;
		for(final OSMTileInfo[] part : tilesNeeded) {
			tmpCount += part.length;
		}

		final long tileCount = tmpCount;

		/* Calculate the needed size. */
		final long bytesEpectedNeeded = tileCount * providerInfo.EXPECTED_AVERAGE_MAPTILE_BYTESIZE;
		final String formattedFileSize = FileSizeFormatter.formatFileSize(bytesEpectedNeeded);

		final AlertDialog.Builder ab = new AlertDialog.Builder(this)
		.setTitle(R.string.dlg_preloader_rect_title)
		.setMessage(String.format(getString(R.string.dlg_preloader_rect_message), tileCount , formattedFileSize));

		if(pUptoZoomLevel > 0){
			ab.setNeutralButton(R.string.dlg_preloader_rect_reducezoom, new DialogInterface.OnClickListener(){
				public void onClick(final DialogInterface d, final int which) {
					d.dismiss();
					preloadMapTilesUpToZoomLevel(pUptoZoomLevel - 1);
				}
			});
		}

		ab.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				final String progressMessage = getString(R.string.pdg_preloader_message);
				final ProgressDialog pd = ProgressDialog.show(WhereAmIMap.this, getString(R.string.pdg_preloader_title), String.format(progressMessage, 0, tileCount), true, true);
				new Thread(new Runnable(){
					public void run() {
						final OSMMapTileFilesystemCache fsProvider = pTileManager.getFileSystemCache();
						if(fsProvider.getMaxFSCacheByteSize() < bytesEpectedNeeded){
							final int newCacheByteSize = (int)(bytesEpectedNeeded * 1.1f);

							runOnUiThread(new Runnable(){
								public void run() {
									pd.setMessage(getString(R.string.pdg_settings_cache_clear_cache_title));
									Toast.makeText(WhereAmIMap.this, String.format(getString(R.string.toast_preloader_cache_increased_message), FileSizeFormatter.formatFileSize(newCacheByteSize)), Toast.LENGTH_LONG).show();
								}
							});

							final int cacheSizeForPreferences = Math.min(OSMMapTileFilesystemCache.MAXIMUM_CACHESIZE, 1 + newCacheByteSize / (1024 * 1024));
							Preferences.saveMaxCacheSize(WhereAmIMap.this, cacheSizeForPreferences);
							/* Cache size needs to be increased. */
							fsProvider.setMaxFSCacheByteSize(cacheSizeForPreferences * 1024 * 1024); // 10% margin
							fsProvider.clearCurrentFSCache();
						} else {
							final int fsCacheBytesFree = fsProvider.getMaxFSCacheByteSize() - fsProvider.getCurrentFSCacheByteSize();
							if(bytesEpectedNeeded > fsCacheBytesFree * 0.9f){ // 10% margin
								runOnUiThread(new Runnable(){
									public void run() {
										pd.setMessage(getString(R.string.pdg_settings_cache_clear_cache_title));
									}
								});
								fsProvider.cutCurrentFSCacheBy((int)(bytesEpectedNeeded - fsCacheBytesFree * 0.9f)); // 10% margin
							}
						}

						runOnUiThread(new Runnable(){
							public void run() {
								new OSMMapTilePreloader().loadAllToCacheAsync(tilesNeeded,
										pUptoZoomLevel,
										providerInfo,
										pTileManager,
										new OnProgressChangeListener(){
									public void onProgressChange(final int progress, final int max) {
										if(progress != max) {
											pd.setMessage(String.format(progressMessage, progress, max));
										} else {
											pd.dismiss();
										}
									}
								});
							}
						});
					}
				}, "Preloader-Thread").start();

				d.dismiss();
			}
		});
		ab.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
			}
		}).show();
	}

	private void openOSBMap(){
		final Intent osbIntent = new Intent(this, OSBMap.class);
		startActivityForResult(osbIntent, REQUESTCODE_OSBMAP);
	}

	private void openWeatherDialog(final GeoPoint pGeoPoint) {
		final Intent getWeatherIntent = new Intent(this, WeatherForecast.class);

		getWeatherIntent.putExtra(WeatherForecast.WEATHERQUERY_GEOPOINTSTRING_ID, pGeoPoint.toString());

		startActivityForResult(getWeatherIntent, REQUESTCODE_WEATHER);
	}

	private void showAccessibilityAnalysisDialog(final MenuItem item) {
		final String minute = " " + getString(R.string.minute);
		final String minutes = " " + getString(R.string.minutes);

		final int[] minuteValues = getResources().getIntArray(R.array.accessibility_minutes);
		final String[] minuteStrings = new String[minuteValues.length];

		minuteStrings[0] = minuteValues[0] + " " + minute;
		for (int i = 1; i < minuteValues.length; i++) {
			minuteStrings[i] = minuteValues[i] + " " + minutes;
		}

		new AlertDialog.Builder(this).setSingleChoiceItems(minuteStrings, 4, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
				item.setEnabled(false); // Disable AAS

				final int minuteToResolve = minuteValues[which];
				Toast.makeText(WhereAmIMap.this, R.string.please_wait_a_moment, Toast.LENGTH_LONG).show();
				new Thread(new Runnable(){
					public void run() {
						try {
							final AASResponse aasr = AASRequester.request(WhereAmIMap.this.mOSMapView.getMapCenter(), minuteToResolve);
							final List<AreaOfInterest> areas = WhereAmIMap.this.mAASOverlay.getAreasOfInterest();
							areas.clear();
							for(final Polygon p : aasr.getPolygons()) {
								areas.add(p);
							}

							runOnUiThread(new Runnable(){
								public void run() {
									Toast.makeText(WhereAmIMap.this, R.string.toast_map_accessibilityanalysis_finished, Toast.LENGTH_LONG).show();
								}
							});

						} catch(final ORSException orse){
							runOnUiThread(new Runnable(){
								public void run() {
									Toast.makeText(WhereAmIMap.this, orse.getErrors().get(0).toUserString(), Toast.LENGTH_LONG).show();
								}
							});
						} catch (final Exception e) {
							Log.e(Constants.DEBUGTAG, "AASRequester-Error", e);
						} finally {
							item.setEnabled(true); // Enable AAS
						}
					}
				}, "Accessibility-Runner").start();
			}
		}).create().show();
	}

	private void toggleCompass() {
		this.mCompassRotateView.toggleActive();
		if(this.mCompassRotateView.isActive()){
			this.mIvCompass.setVisibility(View.VISIBLE);
			//			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // TODO Strange issue with SkyHook
		}else{
			this.mIvCompass.setVisibility(View.GONE);
			//			this.setRequestedOrientation(Preferences.getRequestedScreenOrientation(this)); // TODO Strange issue with SkyHook
		}
	}

	private void applyAutoCompleteListeners() {
		try{
			final List<DBPOI> usedPOIs = DBManager.getPOIHistory(this);

			final ArrayList<String> usedPOIStrings = new ArrayList<String>(usedPOIs.size());

			for(final DBPOI poi : usedPOIs) {
				usedPOIStrings.add(poi.getName());
			}

			new InlineAutoCompleterConstant(this.mEtSearch, usedPOIStrings, false){
				@Override
				public boolean onEnter() {
					handleSearchSubmit(WhereAmIMap.this.mEtSearch.getText().toString());
					return true;
				}
			};
		} catch (final DataBaseException e) {
			//			Log.e(DEBUGTAG, "Error on loading POIs", e);
		}
	}

	private void applyMapViewLongPressListener() {
		final GestureDetector gd = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
			@Override
			public void onLongPress(final MotionEvent e) {
				final OSMMapViewProjection pj = WhereAmIMap.super.mOSMapView.getProjection();
				WhereAmIMap.this.mGPLastMapClick = pj.fromPixels((int)e.getX(), (int)e.getY());

				final String[] items = new String[]{
						getString(R.string.tv_whereami_contextmenu_nav_here),
						getString(R.string.tv_whereami_contextmenu_add_as_favorite),
						getString(R.string.tv_whereami_contextmenu_show_radar),
						getString(R.string.tv_whereami_contextmenu_weather_get),
						getString(R.string.tv_whereami_contextmenu_close)};
				new AlertDialog.Builder(WhereAmIMap.this)
				.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener(){

					public void onClick(final DialogInterface d, final int which) {
						d.dismiss();
						switch(which){
							case 0:
								doNavToGeoPoint(WhereAmIMap.this.mGPLastMapClick);
								break;
							case 1:
								showDialog(DIALOG_INPUT_FAVORITE_NAME);
								break;
							case 2:
								final Intent i = new Intent("com.google.android.radar.SHOW_RADAR");
								i.putExtra("latitude", (float)WhereAmIMap.this.mGPLastMapClick.getLatitudeAsDouble());
								i.putExtra("longitude", (float)WhereAmIMap.this.mGPLastMapClick.getLongitudeAsDouble());
								org.andnav2.ui.util.Util.startUnknownActivity(WhereAmIMap.this, i, "com.google.android.radar");
								break;
							case 3:
								openWeatherDialog(WhereAmIMap.this.mGPLastMapClick);
								break;
							case 4:
								return;
						}
					}
				})
				.create().show();
			}
		});
		this.mOSMapView.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(final View v, final MotionEvent ev) {
				if(WhereAmIMap.this.mNavPointsCrosshairMode){
					return false;
				}else{
					WhereAmIMap.this.mAutoCenterBlockedUntil = System.currentTimeMillis() + AUTOCENTER_BLOCKTIME;

					if(ev.getAction() == MotionEvent.ACTION_DOWN) {
						startDelayedHideControlsAnimation();
					}

					return gd.onTouchEvent(ev);
				}
			}
		});
	}

	private void applyZoomButtonListeners(){
		this.findViewById(R.id.iv_whereami_zoomin).setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				WhereAmIMap.this.mOSMapView.zoomIn();
				WhereAmIMap.this.mOSMapView.invalidate();
			}
		});
		this.findViewById(R.id.iv_whereami_zoomout).setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				WhereAmIMap.this.mOSMapView.zoomOut();
				WhereAmIMap.this.mOSMapView.invalidate();
			}
		});
	}

	private void applyQuickButtonListeners() {
		this.mMapItemControlView.setItemizedOverlayControlViewListener(new OSMMapViewItemizedOverlayControlView.ItemizedOverlayControlViewListener(){
			public void onCenter() {
				final OSMMapViewOverlayItem oi = WhereAmIMap.this.mSearchPinList.get(WhereAmIMap.this.mSearchPinListIndex);
				WhereAmIMap.this.mOSMapView.getController().animateTo(oi, AnimationType.MIDDLEPEAKSPEED);
			}

			public void onNavTo() {
				final GeoPoint gp = WhereAmIMap.this.mSearchPinList.get(WhereAmIMap.this.mSearchPinListIndex);

				final String aPOIName = WhereAmIMap.this.mEtSearch.getText().toString();
				try {
					DBManager.addPOIToHistory(WhereAmIMap.this, aPOIName, gp.getLatitudeE6(), gp.getLongitudeE6());
				} catch (final DataBaseException e) {
					//					Log.e(DEBUGTAG, "Error adding POI", e);
				}

				doNavToGeoPoint(gp);
			}

			public void onNext() {
				WhereAmIMap.this.mSearchPinListIndex++;
				WhereAmIMap.this.mSearchPinListIndex = WhereAmIMap.this.mSearchPinListIndex % WhereAmIMap.this.mSearchPinList.size();
				final OSMMapViewOverlayItem oi = WhereAmIMap.this.mSearchPinList.get(WhereAmIMap.this.mSearchPinListIndex);
				WhereAmIMap.this.mOSMapView.getController().animateTo(oi, AnimationType.MIDDLEPEAKSPEED);
			}

			public void onPrevious() {
				if(WhereAmIMap.this.mSearchPinListIndex == 0) {
					WhereAmIMap.this.mSearchPinListIndex = WhereAmIMap.this.mSearchPinList.size() - 1;
				} else {
					WhereAmIMap.this.mSearchPinListIndex--;
				}

				final OSMMapViewOverlayItem oi = WhereAmIMap.this.mSearchPinList.get(WhereAmIMap.this.mSearchPinListIndex);
				WhereAmIMap.this.mOSMapView.getController().animateTo(oi, AnimationType.MIDDLEPEAKSPEED);
			}
		});

		/* Left side. */
		this.mIbtnNavPointsSetStart.setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				/* Set destination-flag and start crosshair-mode. */
				WhereAmIMap.this.mStartFlagOverlay.setLocation(WhereAmIMap.this.mOSMapView.getMapCenter());
				WhereAmIMap.this.mStartFlagOverlay.setVisible(true);
				updateUIForNavPointsCrosshairMode(true);
			}
		});


		/* Left side. */
		this.mIbtnNavPointsSetDestination.setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				/* Set destination-flag and start crosshair-mode. */
				WhereAmIMap.this.mDestinationFlagOverlay.setLocation(WhereAmIMap.this.mOSMapView.getMapCenter());
				WhereAmIMap.this.mDestinationFlagOverlay.setVisible(true);
				updateUIForNavPointsCrosshairMode(true);
			}
		});

		/* Left side. */
		this.mIbtnNavPointsDoCancel.setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				/* End crosshair-mode. */
				updateUIForNavPointsCrosshairMode(false);
			}
		});

		this.mIbtnNavPointsDoStart.setOnClickListener(new OnClickListener(){
			public void onClick(final View arg0) {
				if(WhereAmIMap.this.mNavPointsCrosshairMode){
					/* User chose a good start+destination. */
					doNavBetweenGeoPoints(WhereAmIMap.this.mStartFlagOverlay.getLocation(), WhereAmIMap.this.mDestinationFlagOverlay.getLocation());
					/* End crosshair-mode. */
					updateUIForNavPointsCrosshairMode(false);
				}else{
					/* Disable Auto-Follow. */
					updateUIForAutoCenterChange(WhereAmIMap.this.CENTERMODE_NONE);

					WhereAmIMap.this.mDestinationFlagOverlay.setVisible(false);
					WhereAmIMap.this.mStartFlagOverlay.setVisible(false);
					updateUIForNavPointsCrosshairMode(true);
				}
			}
		});

		this.mEtSearch.setOnKeyListener(new OnKeyListener(){
			public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP){
					switch(event.getKeyCode()){
						case KeyEvent.KEYCODE_DPAD_CENTER:
						case KeyEvent.KEYCODE_ENTER:
						case KeyEvent.KEYCODE_CALL:
							handleSearchSubmit(WhereAmIMap.this.mEtSearch.getText().toString());
							return true;
					}
				}
				return false;
			}
		});

		this.mIbtnSearch.setOnClickListener(new OnClickListener(){
			public void onClick(final View arg0) {
				if(WhereAmIMap.this.mEtSearch.getVisibility() == View.GONE){
					showDialog(DIALOG_SELECT_FREEFORM_OR_STRUCTURED_SEARCH);
					//					handleSearchOpen(); <-- Would directly open the edittext
				}else{
					handleSearchSubmit(WhereAmIMap.this.mEtSearch.getText().toString());
				}
			}
		});

		/* Right side. */
		this.mIbtnChooseRenderer.setOnClickListener(new OnClickListener(){
			public void onClick(final View arg0) {
				startDelayedHideControlsAnimation();
				final OSMMapTileProviderInfo[] providers = OSMMapTileProviderInfo.values();

				final SpannableString[] renderersNames = new SpannableString[providers.length];

				for(int j = 0; j < providers.length; j ++){
					final SpannableString itemTitle = new SpannableString(providers[j].NAME + " (" + providers[j].DESCRIPTION + ")");
					itemTitle.setSpan(new StyleSpan(Typeface.ITALIC), providers[j].NAME.length(), itemTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					itemTitle.setSpan(new RelativeSizeSpan(0.5f), providers[j].NAME.length(), itemTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

					renderersNames[j] = itemTitle;
				}

				final int curRendererIndex = WhereAmIMap.this.mOSMapView.getProviderInfo().ordinal();

				new AlertDialog.Builder(WhereAmIMap.this)
				.setTitle(R.string.maps_menu_submenu_renderers)
				.setSingleChoiceItems(renderersNames, curRendererIndex , new DialogInterface.OnClickListener(){
					public void onClick(final DialogInterface d, final int which) {
						changeProviderInfo(providers[which]);
						d.dismiss();
					}
				}).create().show();
			}
		});

		this.mIbtnWhereAmI.setOnClickListener(new OnClickListener(){
			public void onClick(final View arg0) {
				startDelayedHideControlsAnimation();
				Toast.makeText(WhereAmIMap.this, R.string.please_wait_a_moment, Toast.LENGTH_SHORT).show();

				final GeoPoint mapCenter = WhereAmIMap.this.mOSMapView.getMapCenter();
				final UnitSystem us = Preferences.getUnitSystem(WhereAmIMap.this);

				new Thread(new Runnable(){
					public void run() {
						try {
							final ArrayList<GeocodedAddress> addr = LUSRequester.requestReverseGeocode(WhereAmIMap.this, mapCenter, ReverseGeocodePreferenceType.STREETADDRESS);
							runOnUiThread(new Runnable(){
								public void run() {
									if(addr == null || addr.size() == 0){
										Toast.makeText(WhereAmIMap.this, R.string.whereami_location_not_resolvable, Toast.LENGTH_SHORT).show();
									}else{
										final GeocodedAddress closestGeocodedAddress = addr.get(0);
										final String msg = closestGeocodedAddress.toString(WhereAmIMap.this, us, true);

										Drawable icon;
										try{
											icon = getResources().getDrawable(closestGeocodedAddress.getNationality().FLAGRESID);
										}catch(final Exception e){
											icon = getResources().getDrawable(R.drawable.questionmark);
										}

										new AlertDialog.Builder(WhereAmIMap.this)
										.setTitle(closestGeocodedAddress.getNationality().NAMERESID)
										.setMessage(msg + '\n')
										.setIcon(icon)
										.setPositiveButton(R.string.ok, null)
										.create().show();
									}
								}
							});
						} catch (final ORSException e) {
							runOnUiThread(new Runnable(){
								public void run() {
									Toast.makeText(WhereAmIMap.this, e.getErrors().get(0).toUserString(), Toast.LENGTH_LONG).show();
								}
							});
						} catch (final Exception e) {
							Exceptor.e("LUSRequester", e);
						}
					}
				}).start();
				/* Invalidate map. */
				WhereAmIMap.this.mOSMapView.invalidate();
			}
		});

		this.mIbtnCenter.setOnClickListener(new OnClickListener(){
			public void onClick(final View arg0) {
				startDelayedHideControlsAnimation();
				final int newMode = (WhereAmIMap.this.mDoCenter + 1) % 3;

				final GeoPoint lastKnownLocationAsGeoPoint = WhereAmIMap.super.getLastKnownLocation(true);
				if(lastKnownLocationAsGeoPoint != null){
					switch(newMode){
						case CENTERMODE_AUTO:
						case CENTERMODE_ONCE:
							WhereAmIMap.this.mOSMapView.getController().animateTo(lastKnownLocationAsGeoPoint, AnimationType.MIDDLEPEAKSPEED);
							break;
					}
				}

				updateUIForAutoCenterChange(newMode);
				/* Invalidate map. */
				WhereAmIMap.this.mOSMapView.invalidate();
			}
		});
	}

	private void updateUIForNavPointsCrosshairMode(final boolean pNewState) {
		this.mCrosshairOverlay.setVisible(pNewState);
		this.mNavPointsCrosshairMode = pNewState;

		if(pNewState){
			this.mIbtnNavPointsDoCancel.setVisibility(View.VISIBLE);
			this.mIbtnNavPointsSetDestination.setVisibility(View.VISIBLE);
			this.mIbtnNavPointsSetStart.setVisibility(View.VISIBLE);

			this.mIbtnNavPointsDoCancel.clearAnimation();
			this.mIbtnNavPointsDoStart.clearAnimation();
			this.mIbtnNavPointsSetStart.clearAnimation();
			this.mIbtnNavPointsSetDestination.clearAnimation();
			this.mIbtnCenter.clearAnimation();

			final boolean startAndDestinationSet = this.mStartFlagOverlay.isVisible() && this.mDestinationFlagOverlay.isVisible();
			this.mIbtnNavPointsDoStart.setEnabled(startAndDestinationSet);
			this.mNavPointsConnectionLineOverlay.setFrom(this.mStartFlagOverlay.getLocation());
			this.mNavPointsConnectionLineOverlay.setTo(this.mDestinationFlagOverlay.getLocation());
			this.mNavPointsConnectionLineOverlay.setVisible(startAndDestinationSet);
		}else{
			startDelayedHideControlsAnimation();

			this.mIbtnNavPointsDoCancel.setVisibility(View.GONE);
			this.mIbtnNavPointsSetDestination.setVisibility(View.GONE);
			this.mIbtnNavPointsSetStart.setVisibility(View.GONE);
			this.mIbtnNavPointsDoStart.startAnimation(this.mFadeToLeftAnimation);
			this.mIbtnCenter.startAnimation(this.mFadeToRightAnimation);

			this.mIbtnNavPointsDoStart.setEnabled(true);
			this.mStartFlagOverlay.setVisible(false);
			this.mDestinationFlagOverlay.setVisible(false);
			this.mNavPointsConnectionLineOverlay.setVisible(false);
		}

		super.mOSMapView.invalidate();
	}

	private void doNavToGeoPoint(final GeoPoint gp) {
		final Intent navTo = new Intent(Constants.ANDNAV2_NAV_ACTION);

		final Bundle b = new Bundle();
		b.putString("to", gp.toDoubleString()); // i.e.: "to" --> "37.465259,-122.126456"
		navTo.putExtras(b);

		sendBroadcast(navTo);
	}

	private void doNavBetweenGeoPoints(final GeoPoint pGPStart, final GeoPoint pGPDestination) {
		final Intent navTo = new Intent(Constants.ANDNAV2_NAV_ACTION);

		final Bundle b = new Bundle();
		b.putString("from", pGPStart.toDoubleString()); // i.e.: "to" --> "37.465259,-122.126456"
		b.putString("to", pGPDestination.toDoubleString()); // i.e.: "to" --> "37.465259,-122.126456"
		navTo.putExtras(b);

		sendBroadcast(navTo);
	}

	private void changeProviderInfo(final OSMMapTileProviderInfo aProviderInfo) {
		/* Remember changes to the provider to start the next time with the same provider. */
		Preferences.saveMapViewProviderInfoWhereAmI(this, aProviderInfo);

		/* Check if Auto-Follow has to be disabled. */
		if(!aProviderInfo.hasBoundingBox() || aProviderInfo.BOUNDINGBOXE6.contains(super.mOSMapView.getMapCenter())){
			super.mOSMapView.setProviderInfo(aProviderInfo);
		}else{
			updateUIForAutoCenterChange(this.CENTERMODE_NONE);

			super.mOSMapView.setProviderInfo(aProviderInfo);
			/* Finally center and zoom on the center of the BoundingBox. */
			super.mOSMapView.getController().zoomToSpan(aProviderInfo.BOUNDINGBOXE6);
			super.mOSMapView.getController().animateTo(aProviderInfo.BOUNDINGBOXE6.getCenter(), AnimationType.MIDDLEPEAKSPEED);
		}
	}

	private void updateUIForAutoCenterChange(final int pNewMode) {
		if(WhereAmIMap.this.mDoCenter == pNewMode) {
			return;
		}

		WhereAmIMap.this.mDoCenter = pNewMode;

		if(WhereAmIMap.this.mDoCenter == this.CENTERMODE_AUTO){
			WhereAmIMap.this.mIbtnCenter.setImageResource(R.drawable.person_focused_small);
			Toast.makeText(WhereAmIMap.this, R.string.toast_autofollow_enabled, Toast.LENGTH_SHORT).show();
		}else if(WhereAmIMap.this.mDoCenter == this.CENTERMODE_ONCE){
			WhereAmIMap.this.mIbtnCenter.setImageResource(R.drawable.person_focused_once_small);
			Toast.makeText(WhereAmIMap.this, R.string.toast_autofollow_once, Toast.LENGTH_SHORT).show();
		}else if(WhereAmIMap.this.mDoCenter == this.CENTERMODE_NONE){
			WhereAmIMap.this.mIbtnCenter.setImageResource(R.drawable.person_small);
			Toast.makeText(WhereAmIMap.this, R.string.toast_autofollow_disabled, Toast.LENGTH_SHORT).show();
		}
	}

	private void handleSearchOpen() {
		this.mIbtnSearch.setImageResource(R.drawable.search_submit);
		this.mIbtnSearch.clearAnimation();
		this.mEtSearch.setVisibility(View.VISIBLE);
		/* And Focus on the */
		this.mEtSearch.requestFocus();
		this.mEtSearch.selectAll();
		this.mOSMapView.invalidate();
		WhereAmIMap.this.clearPinOverlay();
	}

	private void handleSearchSubmit(final String pQuery) {
		final String query = pQuery.trim();

		this.mIbtnSearch.setImageResource(R.drawable.search);
		this.mEtSearch.setVisibility(View.GONE);

		this.mIbtnSearch.startAnimation(this.mFadeOutQuickAnimation);

		if(query.length() > 0){

			/* Check if coordinates were entered. */
			final GeoPoint coordsIfEntered = CoordinatesExtractor.match(query);
			if(coordsIfEntered != null){
				refreshPinOverlay(coordsIfEntered);
			}else if(RouteHandleIDExtractor.match(query) != -1){
				startDDMapWithRouteHandleID(RouteHandleIDExtractor.match(query));
			}else{
				/* No coords --> textual/freeform search. */
				//			final String[] choices = new String[]{getString(R.string.whereami_search_scope_global), getString(R.string.whereami_search_scope_local)};
				//			new AlertDialog.Builder(this)
				//			.setTitle(R.string.whereami_search_scope_title)
				//			.setCancelable(true)
				//			.setSingleChoiceItems(choices, 0, new DialogInterface.OnClickListener(){
				//				@Override
				//				public void onClick(final DialogInterface dialog, final int which) {
				//					dialog.dismiss();
				Toast.makeText(WhereAmIMap.this, R.string.please_wait_a_moment, Toast.LENGTH_SHORT).show();
				searchORSLocations(query);
				//					searchLocations(query, 0); // which
				//				}
				//			}).create().show();
			}
		}
	}

	private void startDDMapWithRouteHandleID(final long pRouteHandleID) {
		final Intent directIntent = new Intent(this, OpenStreetDDMap.class);
		final Bundle b = new Bundle();
		b.putInt(EXTRAS_MODE, EXTRAS_MODE_LOAD_ROUTE_BY_ROUTEHANDLEID);

		b.putLong(EXTRAS_ROUTEHANDLEID_ID, pRouteHandleID);

		directIntent.putExtras(b);
		this.startActivityForResult(directIntent, REQUESTCODE_DDMAP);
	}

	private void searchORSLocations(final String query) {
		new Thread(new Runnable(){
			public void run() {
				try {
					final ArrayList<GeocodedAddress> ret = LUSRequester.requestFreeformAddress(WhereAmIMap.this, null, query);

					runOnUiThread(new Runnable(){
						public void run() {
							if(ret == null || ret.size() == 0){
								Toast.makeText(WhereAmIMap.this, R.string.whereami_search_no_places_found, Toast.LENGTH_SHORT).show();
							}else{

								final BoundingBoxE6 bBox = BoundingBoxE6.fromGeoPoints(ret);

								/* Disable Auto-Follow. */
								if(WhereAmIMap.this.mDoCenter == WhereAmIMap.this.CENTERMODE_AUTO) {
									updateUIForAutoCenterChange(WhereAmIMap.this.CENTERMODE_NONE);
								}


								final ArrayList<OSMMapViewOverlayItem> itemsFound = new ArrayList<OSMMapViewOverlayItem>();
								for (final GeocodedAddress ga : ret) {
									itemsFound.add(new OSMMapViewOverlayItem(ga.getMunicipality(), ga.toString(WhereAmIMap.this), ga));
								}

								final int foundItemsSize = ret.size();
								if(foundItemsSize == 1){
									WhereAmIMap.this.mOSMapView.setZoomLevel(15);
									WhereAmIMap.this.mOSMapView.getController().animateTo(bBox.getCenter(), AnimationType.MIDDLEPEAKSPEED);
								}else{
									WhereAmIMap.this.mOSMapView.getController().zoomToSpan(bBox);
									WhereAmIMap.this.mOSMapView.getController().animateTo(bBox.getCenter(), AnimationType.MIDDLEPEAKSPEED);
								}

								WhereAmIMap.this.refreshPinOverlay(itemsFound);
								Log.d(Constants.DEBUGTAG, "Items remained: " + foundItemsSize);
								Toast.makeText(WhereAmIMap.this, getString(R.string.whereami_search_places_found) + " " + foundItemsSize, Toast.LENGTH_SHORT).show();
							}
						}
					});

				} catch (final ORSException e) {
					runOnUiThread(new Runnable(){
						public void run() {
							WhereAmIMap.this.clearPinOverlay();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable(){
						public void run() {
							WhereAmIMap.this.clearPinOverlay();
						}
					});
					Exceptor.e("GeocodeError", e, WhereAmIMap.this);
				}

			}
		}).start();
	}

	@Override
	public void onDataStateChanged(final int strength) {
		// TODO
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
