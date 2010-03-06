// Created by plusminus on 20:07:58 - 15.12.2008
package org.andnav2.ui.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.andnav2.R;
import org.andnav2.adt.AndNavLocation;
import org.andnav2.exc.Exceptor;
import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.OSMMapView;
import org.andnav2.osm.views.OSMMapViewScaleIndicatorView;
import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;
import org.andnav2.osm.views.OSMMapView.OnChangeListener;
import org.andnav2.osm.views.controller.OSMMapViewController.AnimationType;
import org.andnav2.osm.views.overlay.BaseOSMMapViewListItemizedOverlayWithFocus;
import org.andnav2.osm.views.overlay.OSMMapViewCrosshairOverlay;
import org.andnav2.osm.views.overlay.OSMMapViewOverlay;
import org.andnav2.osm.views.overlay.OSMMapViewSimpleLocationOverlay;
import org.andnav2.osm.views.overlay.AbstractOSMMapViewItemizedOverlay.OnItemTapListener;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ftpc.api.FTPCRequester;
import org.andnav2.sys.ors.adt.ds.POIGroup;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.sys.osb.adt.OpenStreetBug;
import org.andnav2.sys.osb.api.OSBRequester;
import org.andnav2.sys.osb.exc.OSBException;
import org.andnav2.sys.osb.views.overlay.OSMMapViewOSBOverlay;
import org.andnav2.sys.osb.views.overlay.OSMMapViewOSBOverlayItem;
import org.andnav2.sys.postcode.uk_bs_7666.PostcodeUK_BS7776Matcher;
import org.andnav2.ui.common.CommonCallback;
import org.andnav2.ui.common.CommonCallbackAdapter;
import org.andnav2.ui.common.CommonDialogFactory;
import org.andnav2.ui.common.CommonDialogFactory.CreateEditOSBBugDialogResult;
import org.andnav2.ui.common.CommonDialogFactory.OSBMapLongAddSelectorResult;
import org.andnav2.ui.osm.api.nodes.POICategorySelector;
import org.andnav2.util.TimeUtils;
import org.andnav2.util.constants.Constants;
import org.openstreetmap.api.exceptions.OSMAPIException;
import org.openstreetmap.api.node.NodeCreationRequester;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;


public class OSBMap extends OpenStreetMapAndNavBaseActivity implements OnItemTapListener<OSMMapViewOSBOverlayItem> {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int REQUESTCODE_POICATEGORYSELECTOR = 0;

	private static final int MENU_QUIT_ID = Menu.FIRST;
	private static final int MENU_REFRESH_ID = MENU_QUIT_ID + 1;
	private static final int MENU_CLEAR_ID = MENU_REFRESH_ID + 1;
	private static final int MENU_INSTRUCTIONS_ID = MENU_CLEAR_ID + 1;

	private static final int MIN_ZOOM_FOR_ADDING = 15;

	private static final int DIALOG_SELECT_POI_OR_OSB_OR_FTPC = 0;
	private static final int DIALOG_SHOW_INSTRUCTIONS = DIALOG_SELECT_POI_OR_OSB_OR_FTPC + 1;
	private static final int DIALOG_INPUT_OSM_ACCOUNT_INFO = DIALOG_SHOW_INSTRUCTIONS + 1;
	private static final int DIALOG_SHOW_OSB_BUG_REFRESH_AFTER_ADD = DIALOG_INPUT_OSM_ACCOUNT_INFO + 1;
	private static final int DIALOG_SHOW_OSB_BUG_REFRESH_AFTER_EDIT = DIALOG_SHOW_OSB_BUG_REFRESH_AFTER_ADD + 1;
	private static final int DIALOG_INPUT_OSB_BUG = DIALOG_SHOW_OSB_BUG_REFRESH_AFTER_EDIT + 1;
	private static final int DIALOG_INPUT_EDIT_BUG = DIALOG_INPUT_OSB_BUG + 1;
	private static final int DIALOG_INPUT_OSM_POI = DIALOG_INPUT_EDIT_BUG + 1;
	protected static final int DIALOG_SHOW_OSM_POI_SUCCESS = DIALOG_INPUT_OSM_POI + 1;
	protected static final int DIALOG_SHOW_OSM_POI_FAILED = DIALOG_SHOW_OSM_POI_SUCCESS + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean mAddBugCrosshairMode = false;

	// ===========================================================
	// Constructors
	// ===========================================================

	private BaseOSMMapViewListItemizedOverlayWithFocus<OSMMapViewOSBOverlayItem> mOSBOverlay;
	private final ArrayList<OSMMapViewOSBOverlayItem> mBugOverlayItems = new ArrayList<OSMMapViewOSBOverlayItem>();
	private int mBugOverlayItemsIndex = NOT_SET;
	private ImageButton mIbtnCommentWrite;
	private ImageButton mIbtnRefresh;
	private ImageButton mIbtnAdd;
	private ImageButton mIbtnAddCancel;

	private OSMMapViewCrosshairOverlay mAddBugCrosshairOverlay;
	private OSMMapViewSimpleLocationOverlay mMyLocationOverlay;

	private OSMMapViewScaleIndicatorView mScaleIndicatorView;

	private POIType mAddOSMPOIType;
	private int mNewOSMPOINodeID = NOT_SET;

	@Override
	public void onSetupContentView() {
		this.setContentView(R.layout.osbmap);
		super.mOSMapView = (OSMMapView)findViewById(R.id.map_osbmap);

		this.mIbtnCommentWrite = (ImageButton)this.findViewById(R.id.ibtn_osbmap_comment_write);
		this.mIbtnRefresh = (ImageButton)this.findViewById(R.id.ibtn_osbmap_refresh);
		this.mIbtnAdd = (ImageButton)this.findViewById(R.id.ibtn_osbmap_add);
		this.mIbtnAddCancel = (ImageButton)this.findViewById(R.id.ibtn_osbmap_add_cancel);

		this.mScaleIndicatorView = (OSMMapViewScaleIndicatorView)this.findViewById(R.id.scaleindicatorview_osbmap);
		this.mScaleIndicatorView.setUnitSystem(Preferences.getUnitSystem(this));

		this.mIbtnCommentWrite.setEnabled(false);

		final List<OSMMapViewOverlay> overlays = super.mOSMapView.getOverlays();

		this.mOSBOverlay = new OSMMapViewOSBOverlay(this, this.mBugOverlayItems, this);
		this.mOSBOverlay.setAutoFocusItemsOnTap(false);

		overlays.add(this.mMyLocationOverlay = new OSMMapViewSimpleLocationOverlay(this));

		overlays.add(this.mOSBOverlay);
		/* Add AddBugOverlay after OSBOverlay to give it a higher zOrder. */
		overlays.add(this.mAddBugCrosshairOverlay = new OSMMapViewCrosshairOverlay(Color.BLACK, 2, 17));
		this.mAddBugCrosshairOverlay.setVisible(false);

		super.mOSMapView.invalidate();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		Log.d(Constants.DEBUGTAG, "OnCREATE");
		super.onCreate(savedInstanceState);

		applyZoomButtonListeners();
		applyQuickButtonListeners();
		applyMapViewLongPressListener();

		this.mOSMapView.addChangeListener(new OnChangeListener(){
			public void onChange() {
				runOnUiThread(new Runnable(){
					public void run() {
						OSBMap.this.mScaleIndicatorView.refresh(OSBMap.this.mOSMapView);
					}
				});
			}
		});

		/* Check if this is the first start, or a configuration-change. */
		if(savedInstanceState == null){
			final GeoPoint location = super.getLastKnownLocation(true);
			if(location == null || Math.abs(location.getLatitudeE6()) <= 100 || Math.abs(location.getLongitudeE6()) <= 100) {
				this.mOSMapView.setZoomLevel(3);
			} else{
				this.mOSMapView.setZoomLevel(13);
				this.mOSMapView.setMapCenter(location);
			}
			/* Init the First bug-query a bit delayed, until the mapview offers a correct bounding-box. */
			new Handler().postDelayed(new Runnable() {
				public void run() {
					refreshVisibleBugs();
				}
			}, 1000);

			if(Preferences.getShowOSBInstructions(this)){
				showDialog(DIALOG_SHOW_INSTRUCTIONS);
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		Log.d(DEBUGTAG, "Creating dialog: " + id);
		switch(id){
			case DIALOG_SELECT_POI_OR_OSB_OR_FTPC:
				return CommonDialogFactory.createAddOSBorFTPCDialog(OSBMap.this, new CommonCallbackAdapter<OSBMapLongAddSelectorResult>(){

					@Override
					public void onSuccess(final OSBMapLongAddSelectorResult result) {
						switch(result){
							case OSB:
								showDialog(DIALOG_INPUT_OSB_BUG);
								break;
							case FTPC:
								showAddFTPCDialog();
								break;
							case OSMPOI:
								showAddPOIDialog();
								break;
						}
					}
				});
			case DIALOG_SHOW_INSTRUCTIONS:
				return CommonDialogFactory.createOpenStreetBugInstructionsDialog(this, new CommonCallbackAdapter<Boolean>(){
					@Override
					public void onSuccess(final Boolean showAgain) {
						if(!showAgain) {
							Preferences.saveShowOSBInstructions(OSBMap.this, false);
						}
					}
				});
			case DIALOG_INPUT_OSM_ACCOUNT_INFO:
				return CommonDialogFactory.createOSMAccountInfoDialog(this, new CommonCallbackAdapter<Boolean>(){
					@Override
					public void onSuccess(final Boolean result) {
						if(result){
							showPOICategorySelectorActivity();
						}
					}
				});
			case DIALOG_SHOW_OSB_BUG_REFRESH_AFTER_ADD:
				return CommonDialogFactory.createOSBRefreshBugsDialog(this, R.string.dlg_osb_add_bug_success, new CommonCallbackAdapter<Boolean>(){
					@Override
					public void onSuccess(final Boolean result) {
						if(result){
							refreshVisibleBugs();
						}
					}
				});
			case DIALOG_SHOW_OSB_BUG_REFRESH_AFTER_EDIT:
				return CommonDialogFactory.createOSBRefreshBugsDialog(this, R.string.dlg_osb_edit_bug_success, new CommonCallbackAdapter<Boolean>(){
					@Override
					public void onSuccess(final Boolean result) {
						if(result){
							refreshVisibleBugs();
						}
					}
				});
			case DIALOG_INPUT_OSB_BUG:
				return CommonDialogFactory.createAddOSBBugDialog(this, new CommonCallbackAdapter<String>(){
					@Override
					public void onSuccess(final String result) {
						try{
							final int assignedID = OSBRequester.submitBug(OSBMap.this.mOSMapView.getMapCenter(), result);
							if(assignedID < 0){
								runOnUiThread(new Runnable(){
									public void run() {
										Toast.makeText(OSBMap.this, R.string.dlg_osb_add_bug_error, Toast.LENGTH_LONG).show();
									}
								});
								return;
							}
							runOnUiThread(new Runnable(){
								public void run() {
									showDialog(DIALOG_SHOW_OSB_BUG_REFRESH_AFTER_ADD);
								}
							});
						} catch (final IOException e) {
							onFailure(e);
						}
					}
				});
			case DIALOG_INPUT_EDIT_BUG:
				final OpenStreetBug pOSB = OSBMap.this.mBugOverlayItems.get(OSBMap.this.mBugOverlayItemsIndex).getBug();
				return CommonDialogFactory.createEditOSBBugDialog(this, pOSB, new CommonCallbackAdapter<CreateEditOSBBugDialogResult>(){
					@Override
					public void onSuccess(final CreateEditOSBBugDialogResult result) {
						Log.d(DEBUGTAG, "Callback received");
						try{
							/* Append the name in brackets and append the comment. */
							final OpenStreetBug pOSB = OSBMap.this.mBugOverlayItems.get(OSBMap.this.mBugOverlayItemsIndex).getBug();
							boolean success = OSBRequester.appendToBug(pOSB, result.mCommentWithName);
							if(!success){
								runOnUiThread(new Runnable(){
									public void run() {
										Toast.makeText(OSBMap.this, R.string.dlg_osb_edit_bug_error_during_append, Toast.LENGTH_LONG).show();
									}
								});
								return;
							}

							if(result.mCloseBug){
								success = OSBRequester.closeBug(pOSB);
								if(!success){
									runOnUiThread(new Runnable(){
										public void run() {
											Toast.makeText(OSBMap.this, R.string.dlg_osb_edit_bug_error_during_close, Toast.LENGTH_LONG).show();
										}
									});
									return;
								}
							}
							runOnUiThread(new Runnable(){
								public void run() {
									showDialog(DIALOG_SHOW_OSB_BUG_REFRESH_AFTER_EDIT);
								}
							});
						}catch(final Throwable t){
							onFailure(t);
						}
					}
				});
			case DIALOG_INPUT_OSM_POI:
				return CommonDialogFactory.createInputOSMPOIDialog(this, this.mAddOSMPOIType, new CommonCallback<String>(){
					public void onSuccess(final String pResultName) {
						// TODO Ensure mapcenter did not change
						if(pResultName == null || pResultName.length() == 0){
							onFailure(new OSMAPIException("Invalid name."));
						}else{
							try {
								final GeoPoint mapCenter = OSBMap.this.mOSMapView.getMapCenter();
								final POIType poi = OSBMap.this.mAddOSMPOIType;

								Assert.assertNotNull(poi);
								Assert.assertNotNull(mapCenter);
								Assert.assertFalse(poi.POIGROUPS[0] == POIGroup.MAINGROUP);

								final long now = System.currentTimeMillis();
								final String utcTimestamp = TimeUtils.convertTimestampToUTCString(now);
								final String username = Preferences.getOSMAccountUsername(OSBMap.this);
								final String password = Preferences.getOSMAccountPassword(OSBMap.this);

								OSBMap.this.mNewOSMPOINodeID = NodeCreationRequester
								.requestAddPOI(username, password,
										pResultName,
										mapCenter.getLatitudeAsDouble(),
										mapCenter.getLongitudeAsDouble(),
										utcTimestamp,
										poi.OSMKEYNAME, poi.RAWNAME);
								showDialog(DIALOG_SHOW_OSM_POI_SUCCESS);
							} catch (final Throwable t) {
								onFailure(t);
							}
						}
					}

					public void onFailure(final Throwable t) {
						showDialog(DIALOG_SHOW_OSM_POI_FAILED);
					}
				});
			case DIALOG_SHOW_OSM_POI_FAILED:
				return CommonDialogFactory.createAddOSMPOIFailedDialog(this, new CommonCallbackAdapter<Void>(){
					@Override
					public void onSuccess(final Void result) {
						// Nothing
					}
				});
			case DIALOG_SHOW_OSM_POI_SUCCESS:
				return CommonDialogFactory.createAddOSMPOISuccessDialog(this, new CommonCallbackAdapter<Void>(){
					@Override
					public void onSuccess(final Void result) {
						// Nothing
					}
				});
			default:
				return null;
		}
	}

	@Override
	protected void onPrepareDialog(final int id, final Dialog d) {
		Log.d(DEBUGTAG, "Preparing dialog: " + id);
		super.onPrepareDialog(id, d);
		switch(id){
			case DIALOG_INPUT_EDIT_BUG:
				CommonDialogFactory.prepareEditOSBBugDialog(this, d, OSBMap.this.mBugOverlayItems.get(OSBMap.this.mBugOverlayItemsIndex).getBug());
				break;
			case DIALOG_INPUT_OSM_POI:
				CommonDialogFactory.prepareInputOSMPOIDialog(this, d, this.mAddOSMPOIType);
				break;
		}
	}

	protected void showAddPOIDialog() {
		if(Preferences.getOSMAccountUsername(this).length() == 0){
			showDialog(DIALOG_INPUT_OSM_ACCOUNT_INFO);
		}else{
			showPOICategorySelectorActivity();
		}
	}

	protected void showPOICategorySelectorActivity() {
		final Intent i = new Intent(this, POICategorySelector.class);
		startActivityForResult(i, REQUESTCODE_POICATEGORYSELECTOR);
	}

	private void refreshVisibleBugs() {
		Toast.makeText(OSBMap.this, R.string.please_wait_a_moment, Toast.LENGTH_SHORT).show();
		new Thread(new Runnable(){
			public void run() {
				final BoundingBoxE6 drawnBoundingBoxE6 = OSBMap.super.mOSMapView.getDrawnBoundingBoxE6();
				try {
					final ArrayList<OpenStreetBug> bugs = OSBRequester.getBugsFromBoundingBoxE6(drawnBoundingBoxE6);
					for(final OpenStreetBug b : bugs){
						final OSMMapViewOSBOverlayItem osbOverlayItem = new OSMMapViewOSBOverlayItem(b);

						/* By removing and adding after each other, old markers get refreshed. */
						OSBMap.this.mBugOverlayItems.remove(osbOverlayItem);
						OSBMap.this.mBugOverlayItems.add(osbOverlayItem);
					}

					runOnUiThread(new Runnable(){
						public void run() {
							OSBMap.super.mOSMapView.invalidate();
						}
					});
				} catch (final OSBException e) {
					runOnUiThread(new Runnable(){
						public void run() {
							Toast.makeText(OSBMap.this, R.string.toast_osbmap_refresh_error, Toast.LENGTH_SHORT).show();
						}
					});

					//					Exceptor.e("Error while retrieving OpenStreetBugs for rectangle: " + drawnBoundingBoxE6.toString(), e, OSBMap.this);
				}
			}
		}).start();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	private static final String STATE_BUGOVERLAYITEMS_ID = "state_bugoverlayitems_id";
	private static final String STATE_BUGOVERLAYITEMS_SELECTED_ID = "state_bugoverlayitems_selected_id";
	private static final String STATE_ZOOMLEVEL_ID = "state_zoomlevel_id";
	private static final String STATE_MAPCENTER_ID = "state_mapcenter_id";
	private static final String STATE_ADDOSMPOITYPE_ID= "state_osmpoitype_ordinal_id";

	@Override
	protected void onRestoreInstanceState(final Bundle in) {
		final ArrayList<OSMMapViewOSBOverlayItem> items = in.getParcelableArrayList(STATE_BUGOVERLAYITEMS_ID);
		this.mBugOverlayItems.addAll(items);
		this.mBugOverlayItemsIndex = in.getInt(STATE_BUGOVERLAYITEMS_SELECTED_ID);
		super.mOSMapView.setZoomLevel(in.getInt(STATE_ZOOMLEVEL_ID));

		final GeoPoint mapCenter = in.getParcelable(STATE_MAPCENTER_ID);
		if(mapCenter != null) {
			super.mOSMapView.setMapCenter(mapCenter);
		}


		this.mAddOSMPOIType = in.getParcelable(STATE_ADDOSMPOITYPE_ID);

		super.mOSMapView.invalidate();
	}

	@Override
	protected void onSaveInstanceState(final Bundle out) {
		out.putParcelableArrayList(STATE_BUGOVERLAYITEMS_ID, this.mBugOverlayItems);
		out.putInt(STATE_BUGOVERLAYITEMS_SELECTED_ID, this.mBugOverlayItemsIndex);
		out.putInt(STATE_ZOOMLEVEL_ID, super.mOSMapView.getZoomLevel());
		out.putParcelable(STATE_ADDOSMPOITYPE_ID, this.mAddOSMPOIType);
		out.putParcelable(STATE_MAPCENTER_ID, super.mOSMapView.getMapCenter());
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch(requestCode){
			case REQUESTCODE_POICATEGORYSELECTOR:
				this.mAddOSMPOIType = POIType.values()[resultCode];
				showDialog(DIALOG_INPUT_OSM_POI);
				break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		int menuPos = Menu.FIRST;
		menu.setQwertyMode(true);

		{ // Instructions-Item
			menu.add(menuPos, MENU_INSTRUCTIONS_ID, menuPos, getString(R.string.instructions))
			.setIcon(R.drawable.information)
			.setAlphabeticShortcut('i');
			menuPos++;
		}

		{ // Refresh-Item
			menu.add(menuPos, MENU_REFRESH_ID, menuPos, getString(R.string.refresh))
			.setIcon(R.drawable.refresh)
			.setAlphabeticShortcut('r');
			menuPos++;
		}

		{ // Clear-Item
			menu.add(menuPos, MENU_CLEAR_ID, menuPos, getString(R.string.clear))
			.setIcon(R.drawable.wipe)
			.setAlphabeticShortcut('r');
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
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		final int itemId = item.getItemId();
		switch(itemId){
			case MENU_REFRESH_ID:
				this.refreshVisibleBugs();
				return true;
			case MENU_CLEAR_ID:
				this.mBugOverlayItems.clear();
				return true;
			case MENU_INSTRUCTIONS_ID:
				showDialog(DIALOG_SHOW_INSTRUCTIONS);
				return true;
			case MENU_QUIT_ID:
				this.setResult(Constants.SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				this.finish();
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public boolean onItemTap(final int index, final OSMMapViewOSBOverlayItem item) {
		if(index >= this.mBugOverlayItems.size()) {
			throw new IllegalArgumentException();
		}

		this.mBugOverlayItemsIndex = index;
		final OSMMapViewOSBOverlayItem focusedItem = this.mOSBOverlay.getFocusedItem();
		if(!item.equals(focusedItem)){
			this.mOSBOverlay.setFocusedItem(item);
			this.mIbtnCommentWrite.setEnabled(true);
		}else{
			this.mOSBOverlay.unSetFocusedItem();
			this.mIbtnCommentWrite.setEnabled(false);
		}

		this.mOSMapView.getController().animateTo(item, AnimationType.MIDDLEPEAKSPEED);

		return true;
	}

	@Override
	public void onLocationLost(final AndNavLocation pLocation) {

	}

	@Override
	public void onLocationChanged(final AndNavLocation pLocation) {
		if(this.mMyLocationOverlay != null) {
			this.mMyLocationOverlay.setLocation(getLastKnownLocation(true));
		}

		if(super.mOSMapView != null) {
			super.mOSMapView.invalidate();
		}
	}

	@Override
	protected void release() {
		// TODO
	}

	@Override
	public void onDataStateChanged(final int strength) {

	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected void applyMapViewLongPressListener() {
		final GestureDetector gd = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
			@Override
			public void onLongPress(final MotionEvent mv) {
				final OSMMapView mapView = OSBMap.super.mOSMapView; // Drag to local field
				final OSMMapViewProjection pj = mapView.getProjection();
				final GeoPoint gp = pj.fromPixels((int)mv.getX(), (int)mv.getY());

				OSBMap.this.mOSMapView.setMapCenter(gp);
				OSBMap.this.mOSMapView.invalidate();

				handleAddAction();
			}
		});
		this.mOSMapView.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(final View v, final MotionEvent ev) {
				if(!OSBMap.this.mAddBugCrosshairMode){
					return gd.onTouchEvent(ev);
				}else{
					return false;
				}
			}
		});
	}

	protected void applyQuickButtonListeners(){
		this.mIbtnCommentWrite.setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				showDialog(DIALOG_INPUT_EDIT_BUG);
			}
		});

		this.mIbtnRefresh.setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				refreshVisibleBugs();
			}
		});

		this.mIbtnAdd.setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				handleAddAction();
			}
		});

		this.mIbtnAddCancel.setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				/* Toggle status */
				OSBMap.this.mAddBugCrosshairMode = false;
				refreshAddButtonsStates();
				OSBMap.super.mOSMapView.invalidate();
			}
		});
	}

	private void showAddFTPCDialog() {
		final LayoutInflater inflater = LayoutInflater.from(this);
		final FrameLayout fl = (FrameLayout)inflater.inflate(R.layout.dlg_osb_add_ftpc, null);

		final EditText etMail = (EditText)fl.findViewById(R.id.et_dlg_osb_add_ftpc_mail);
		final EditText etPostcode1 = (EditText)fl.findViewById(R.id.et_dlg_osb_add_ftpc_postcode1);
		final EditText etPostcode2 = (EditText)fl.findViewById(R.id.et_dlg_osb_add_ftpc_postcode2);

		etMail.setText(Preferences.getFTPCConfirmationMail(this));
		etPostcode1.setSelectAllOnFocus(true);
		etPostcode2.setSelectAllOnFocus(true);

		new AlertDialog.Builder(this)
		.setView(fl)
		.setTitle(R.string.dlg_osb_add_ftpc_title)
		.setPositiveButton(R.string.save, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				final String mail = etMail.getText().toString();

				/* TODO Add simple Email-Matcher. */
				if(mail.length() == 0){
					Toast.makeText(OSBMap.this, R.string.dlg_osb_ftpc_mail_invalid_comment, Toast.LENGTH_LONG).show();
					return;
				}

				/* Store email for next use. */
				Preferences.saveFTPCConfirmationMail(OSBMap.this, mail);

				final String postcode1 = etPostcode1.getText().toString();
				final String postcode2 = etPostcode2.getText().toString();
				final String postcode = postcode1 + " " + postcode2;

				if(postcode.length() == 0 || !PostcodeUK_BS7776Matcher.doesMatchUKPostcode_BS_7666(postcode)){
					Toast.makeText(OSBMap.this, R.string.dlg_osb_ftpc_postcode_invalid_comment, Toast.LENGTH_LONG).show();
					return;
				}

				Toast.makeText(OSBMap.this, R.string.please_wait_a_moment, Toast.LENGTH_LONG).show();
				new Thread(new Runnable(){
					public void run() {
						try {
							final boolean success = FTPCRequester.submitPostcode(OSBMap.this.mOSMapView.getMapCenter(), postcode1, postcode2, mail);
							if(success){
								runOnUiThread(new Runnable(){
									public void run() {
										Toast.makeText(OSBMap.this, R.string.dlg_osb_add_ftpc_success, Toast.LENGTH_LONG).show();
									}
								});
							}else{
								runOnUiThread(new Runnable(){
									public void run() {
										Toast.makeText(OSBMap.this, R.string.dlg_osb_add_ftpc_failed, Toast.LENGTH_LONG).show();
									}
								});
							}
						} catch (final Exception e) {
							Exceptor.e("Error submitting FreeThePostcode-Postcode.", e, OSBMap.this);
						}
					}
				}).start();

				d.dismiss();
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
			}
		}).create().show();
	}

	protected void applyZoomButtonListeners(){
		this.findViewById(R.id.iv_osbmap_zoomin).setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				OSBMap.this.mOSMapView.zoomIn();
				OSBMap.this.mOSMapView.invalidate();
			}
		});
		this.findViewById(R.id.iv_osbmap_zoomout).setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				OSBMap.this.mOSMapView.zoomOut();
				OSBMap.this.mOSMapView.invalidate();
			}
		});
	}

	private void refreshAddButtonsStates() {
		if(this.mAddBugCrosshairMode){
			this.mIbtnAdd.setImageResource(R.drawable.checked);
			this.mIbtnAddCancel.setVisibility(View.VISIBLE);
			this.mAddBugCrosshairOverlay.setVisible(true);
		}else{
			this.mIbtnAdd.setImageResource(R.drawable.osb_icon_bug_add);
			this.mIbtnAddCancel.setVisibility(View.GONE);
			this.mAddBugCrosshairOverlay.setVisible(false);
		}
	}

	private void handleAddAction() {
		final int zoomLevel = OSBMap.this.mOSMapView.getZoomLevel();
		if(zoomLevel < MIN_ZOOM_FOR_ADDING){
			Toast.makeText(OSBMap.this, getString(R.string.toast_osbmap_add_bug_zoom_too_low, MIN_ZOOM_FOR_ADDING - zoomLevel), Toast.LENGTH_LONG).show();
			return;
		}

		/* Toggle status */
		OSBMap.this.mAddBugCrosshairMode = !OSBMap.this.mAddBugCrosshairMode;
		refreshAddButtonsStates();
		if(OSBMap.this.mAddBugCrosshairMode){
			Toast.makeText(OSBMap.this, R.string.toast_osbmap_position_buglocation_in_crosshair, Toast.LENGTH_LONG).show();
		}else{
			/* User chose a good destination. */
			showDialog(DIALOG_SELECT_POI_OR_OSB_OR_FTPC);
		}
		OSBMap.super.mOSMapView.invalidate();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
