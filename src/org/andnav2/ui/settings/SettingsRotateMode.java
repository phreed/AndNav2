// Created by plusminus on 18:11:58 - 07.03.2008
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class SettingsRotateMode extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected ImageView rotateModePreview;
	protected TextView rotateModeQuickinfo;

	// ===========================================================
	// Constructors
	// ===========================================================
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_rotatemode);

		/* Get the centermodePreview ImageView from xml. */
		this.rotateModePreview = (ImageView)this.findViewById(R.id.iv_settings_rotatemode_sample);
		this.rotateModeQuickinfo = (TextView)this.findViewById(R.id.tv_settings_rotatemode_quickinfo);

		final int activeRotateMode = Preferences.getRotateMode(this);

		this.refreshRotatemodeImageAndTextByMode(activeRotateMode, false);

		if(activeRotateMode == PREF_ROTATEMODE_DRIVINGDIRECTION_UP) {
			this.findViewById(R.id.ibtn_settings_rotatemode_drivingdirectionup).requestFocus();
		} else if(activeRotateMode == PREF_ROTATEMODE_NORTH_UP) {
			this.findViewById(R.id.ibtn_settings_rotatemode_northup).requestFocus();
		}

		this.applyButtonListeners();
	}

	private void refreshRotatemodeImageAndTextByMode(final int rotateMode, final boolean saved){
		final int activeRotateMode = Preferences.getRotateMode(this);
		switch (rotateMode){
			case PREF_ROTATEMODE_NORTH_UP:
				this.rotateModePreview.setImageResource(R.drawable.rotatemode_northup_full);
				if(saved) {
					this.rotateModeQuickinfo.setText(R.string.tv_settings_rotatemode_quickinfo_northup_saved);
				} else{
					if(activeRotateMode == rotateMode) {
						this.rotateModeQuickinfo.setText(getString(R.string.tv_settings_rotatemode_quickinfo_northup) + " "+ getString(R.string.tv_settings_center_and_rotate_mode_quickinfo_current));
					} else {
						this.rotateModeQuickinfo.setText(R.string.tv_settings_rotatemode_quickinfo_northup);
					}
				}
				break;
			case PREF_ROTATEMODE_DRIVINGDIRECTION_UP:
				this.rotateModePreview.setImageResource(R.drawable.rotatemode_drivingdirectionup_full);
				if(saved) {
					this.rotateModeQuickinfo.setText(R.string.tv_settings_rotatemode_quickinfo_drivingdirectionsup_saved);
				} else{
					if(activeRotateMode == rotateMode) {
						this.rotateModeQuickinfo.setText(getString(R.string.tv_settings_rotatemode_quickinfo_drivingdirectionsup) + " "+ getString(R.string.tv_settings_center_and_rotate_mode_quickinfo_current));
					} else {
						this.rotateModeQuickinfo.setText(R.string.tv_settings_rotatemode_quickinfo_drivingdirectionsup);
					}
				}
				break;
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	protected void applyButtonListeners() {

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_rotatemode_northup)){
			@Override
			public void onFocusChanged(final View arg0, final boolean focus) {
				if(focus) {
					SettingsRotateMode.this.refreshRotatemodeImageAndTextByMode(PREF_ROTATEMODE_NORTH_UP, false);
				}
			}

			@Override
			public void onClicked(final View me) {
				Preferences.saveRotateMode(SettingsRotateMode.this, PREF_ROTATEMODE_NORTH_UP);
				SettingsRotateMode.this.refreshRotatemodeImageAndTextByMode(PREF_ROTATEMODE_NORTH_UP, true);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_rotatemode_drivingdirectionup)){
			@Override
			public void onFocusChanged(final View arg0, final boolean focus) {
				if(focus) {
					SettingsRotateMode.this.refreshRotatemodeImageAndTextByMode(PREF_ROTATEMODE_DRIVINGDIRECTION_UP, false);
				}
			}

			@Override
			public void onClicked(final View me) {
				/* ROTATEMODE_DRIVINGDIRECTION_UP and centermode_UPTO_NEXTTURN are 'mutex' */
				if(Preferences.getCenterMode(SettingsRotateMode.this) == PREF_CENTERMODE_UPTO_NEXTTURN){
					final AlertDialog.Builder aBuilder = new AlertDialog.Builder(SettingsRotateMode.this);
					aBuilder.setIcon(R.drawable.questionmark_small);
					aBuilder.setTitle(R.string.centermode_dd_up_rotatemode_upto_nextturn_mutex_title);
					aBuilder.setMessage(R.string.centermode_dd_up_rotatemode_upto_nextturn_mutex_message);
					aBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
						public void onClick(final DialogInterface arg0, final int arg1) {
							Preferences.saveCenterMode(SettingsRotateMode.this, PREF_CENTERMODE_CENTERUSER);
							Preferences.saveRotateMode(SettingsRotateMode.this, PREF_ROTATEMODE_DRIVINGDIRECTION_UP);
							SettingsRotateMode.this.refreshRotatemodeImageAndTextByMode(PREF_ROTATEMODE_DRIVINGDIRECTION_UP, true);
						}
					});
					aBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
						public void onClick(final DialogInterface arg0, final int arg1) {
							// Nothing to react on here!
						}
					});
					aBuilder.create().show();
				}else{
					Preferences.saveRotateMode(SettingsRotateMode.this, PREF_ROTATEMODE_DRIVINGDIRECTION_UP);
					SettingsRotateMode.this.refreshRotatemodeImageAndTextByMode(PREF_ROTATEMODE_DRIVINGDIRECTION_UP, true);
				}
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_rotatemode_close)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SettingsRotateMode.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsRotateMode.this, R.raw.close).start();
				}
			}

			@Override
			public void onFocusChanged(final View arg0, final boolean focus) {
				if(focus) {
					SettingsRotateMode.this.rotateModeQuickinfo.setText(R.string.tv_settings_quickinfo_close_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				SettingsRotateMode.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
