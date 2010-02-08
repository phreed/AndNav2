// Created by plusminus on 20:07:18 - 21.05.2008
package org.andnav2.ui.map;

import java.util.List;

import org.andnav2.R;
import org.andnav2.adt.AndNavLocation;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.OSMMapView;
import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;
import org.andnav2.osm.views.controller.OSMMapViewController.AnimationType;
import org.andnav2.osm.views.overlay.OSMMapViewOverlay;
import org.andnav2.osm.views.overlay.OSMMapViewSimpleLocationOverlay;
import org.andnav2.osm.views.overlay.OSMMapViewSingleIconOverlay;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.util.constants.Constants;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;


public class SetHomeMap extends OpenStreetMapAndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	protected ImageButton ibtnCenter;
	protected ImageButton ibtnToggleSatellite;
	protected ImageButton ibtnClose;
	protected ImageButton ibtnSetHome;
	protected OSMMapViewSingleIconOverlay mSetHomeOverlay;
	protected OSMMapViewSimpleLocationOverlay mMyLocationOverlay;

	protected GeoPoint mHomeLocation;

	protected boolean doAutoCenter = false;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onSetupContentView() {
		this.setContentView(R.layout.sethome_map);
		super.mOSMapView = (OSMMapView)findViewById(R.id.map_sethome);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);

		/* Add a new instance of our fancy Overlay-Class to the MapView. */
		final List<OSMMapViewOverlay> overlays = this.mOSMapView.getOverlays();
		overlays.add(this.mSetHomeOverlay = new OSMMapViewSingleIconOverlay(this, R.drawable.home_set, new Point(0,0)));
		overlays.add(this.mMyLocationOverlay = new OSMMapViewSimpleLocationOverlay(this));

		// Load the animation from XML (XML file is res/anim/move_animation.xml).
		final Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_beat);
		anim.setRepeatCount(0);

		this.ibtnCenter = (ImageButton)this.findViewById(R.id.ibtn_sethome_center);
		this.ibtnClose = (ImageButton)this.findViewById(R.id.ibtn_sethome_cancel);
		this.ibtnSetHome = (ImageButton)this.findViewById(R.id.ibtn_sethome_set);
		this.ibtnToggleSatellite = (ImageButton)this.findViewById(R.id.ibtn_sethome_toggle_sattelite);

		this.ibtnSetHome.startAnimation(anim);

		this.applyQuickButtonListeners();
		this.applyZoomButtonListeners();
		this.applyMapViewLongPressListener();

		this.mOSMapView.setZoomLevel(15);

		final GeoPoint mp = Preferences.getHomeGeoPoint(this);
		if(mp != null){
			this.mHomeLocation = mp;
			this.mSetHomeOverlay.setLocation(mp);
			this.mOSMapView.getController().animateTo(mp, AnimationType.MIDDLEPEAKSPEED);
			this.mOSMapView.invalidate();
		}else{
			this.ibtnCenter.startAnimation(anim);
			this.doAutoCenter = true;
			this.ibtnCenter.setImageResource(R.drawable.person_focused_small);
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void release(){
		// Nothing
	}

	@Override
	public void onResume() {
		// TODO onSaveRestoreInstanceState mit evtl gesetztem HOME
		this.setRequestedOrientation(Preferences.getRequestedScreenOrientation(this));
		super.onResume();
	}

	@Override
	public void onLocationLost(final AndNavLocation pLocation) {
		// TODO anzeigen...
	}

	@Override
	public void onLocationChanged(final AndNavLocation pLocation) {
		if(this.mMyLocationOverlay != null){
			this.mMyLocationOverlay.setLocation(pLocation);

			if(this.doAutoCenter){
				this.mOSMapView.getController().animateTo(pLocation, AnimationType.LINEAR);
			}
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================


	protected void applyZoomButtonListeners(){
		this.findViewById(R.id.iv_sethome_map_zoomin).setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				SetHomeMap.this.mOSMapView.zoomIn();
			}
		});
		this.findViewById(R.id.iv_sethome_map_zoomout).setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				SetHomeMap.this.mOSMapView.zoomOut();
			}
		});
	}

	protected void applyQuickButtonListeners() {
		/* Left side. */
		new OnClickOnFocusChangedListenerAdapter(this.ibtnClose){
			@Override
			public void onClicked(final View arg0) {
				if(SetHomeMap.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SetHomeMap.this, R.raw.close).start();
				}

				SetHomeMap.this.setResult(Constants.SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SetHomeMap.this.finish();
			}
		};

		/* Right side. */
		new OnClickOnFocusChangedListenerAdapter(this.ibtnToggleSatellite){
			@Override
			public void onClicked(final View arg0) {
				// TODO Show Layers-Menu
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.ibtnSetHome){
			@Override
			public void onClicked(final View arg0) {
				if(SetHomeMap.this.mHomeLocation == null){
					Toast.makeText(SetHomeMap.this, R.string.toast_settings_sethome_via_map_howto, Toast.LENGTH_LONG).show();
				}else{
					if(SetHomeMap.super.mMenuVoiceEnabled) {
						MediaPlayer.create(SetHomeMap.this, R.raw.save).start();
					}

					Preferences.saveHomeGeoPoint(SetHomeMap.this, SetHomeMap.this.mHomeLocation);
					SetHomeMap.this.setResult(Constants.SUBACTIVITY_RESULTCODE_SUCCESS);
					SetHomeMap.this.finish();
				}
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.ibtnCenter){
			@Override
			public void onClick(final View arg0) {
				SetHomeMap.this.doAutoCenter = !SetHomeMap.this.doAutoCenter;
				if(SetHomeMap.this.doAutoCenter){
					SetHomeMap.this.ibtnCenter.setImageResource(R.drawable.person_focused_small);
					Toast.makeText(SetHomeMap.this, R.string.toast_autofollow_enabled, Toast.LENGTH_SHORT).show();
				}else{
					SetHomeMap.this.ibtnCenter.setImageResource(R.drawable.person_small);
					Toast.makeText(SetHomeMap.this, R.string.toast_autofollow_disabled, Toast.LENGTH_SHORT).show();
				}
				/* Invalidate map. */
				SetHomeMap.this.mOSMapView.invalidate();
			}
		};
	}

	protected void applyMapViewLongPressListener() {

		final GestureDetector gd = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
			@Override
			public void onLongPress(final MotionEvent mv) {
				final OSMMapView mapView = SetHomeMap.super.mOSMapView; // Drag to local field
				final OSMMapViewProjection pj = mapView.getProjection();
				final GeoPoint mp = pj.fromPixels((int)mv.getX(), (int)mv.getY());

				SetHomeMap.this.mHomeLocation = mp;
				SetHomeMap.this.mSetHomeOverlay.setLocation(mp);
				SetHomeMap.this.mOSMapView.invalidate();
			}
		});
		this.mOSMapView.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(final View v, final MotionEvent ev) {
				return gd.onTouchEvent(ev);
			}
		});
	}

	@Override
	public void onDataStateChanged(final int strength) {
		// TODO ??
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
