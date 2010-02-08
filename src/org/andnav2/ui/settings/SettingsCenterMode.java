//Created by plusminus on 18:11:58 - 07.03.2008
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


public class SettingsCenterMode extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	protected static final int REQUESTCODE_NAVSETTINGS = 0;

	// ===========================================================
	// Fields
	// ===========================================================

	protected ImageView mTvCenterModePreview;
	protected TextView mTvCenterModeQuickinfo;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_centermode);

		/* Get the centermodePreview ImageView from xml. */
		this.mTvCenterModePreview = (ImageView)this.findViewById(R.id.iv_settings_centermode_sample);
		this.mTvCenterModeQuickinfo = (TextView)this.findViewById(R.id.tv_settings_centermode_quickinfo);

		final int activecentermode = Preferences.getCenterMode(this);

		this.refreshCenterModeImageAndTextByMode(activecentermode, false);

		if(activecentermode == PREF_CENTERMODE_CENTERUSER) {
			this.findViewById(R.id.ibtn_settings_centermode_centeruser).requestFocus();
		} else if(activecentermode == PREF_CENTERMODE_UPTO_NEXTTURN) {
			this.findViewById(R.id.ibtn_settings_centermode_upto_nextturn).requestFocus();
		}

		this.applyViewListeners();
	}

	private void refreshCenterModeImageAndTextByMode(final int centermode, final boolean saved){
		final int activecentermode = Preferences.getCenterMode(this);
		switch (centermode){
			case PREF_CENTERMODE_CENTERUSER:
				this.mTvCenterModePreview.setImageResource(R.drawable.centermode_user_full);
				if(saved) {
					this.mTvCenterModeQuickinfo.setText(R.string.tv_settings_centermode_quickinfo_centeruser_saved);
				} else{
					if(activecentermode == centermode) {
						this.mTvCenterModeQuickinfo.setText(getString(R.string.tv_settings_centermode_quickinfo_centeruser) + " "+ getString(R.string.tv_settings_center_and_rotate_mode_quickinfo_current));
					} else {
						this.mTvCenterModeQuickinfo.setText(R.string.tv_settings_centermode_quickinfo_centeruser);
					}
				}
				break;
			case PREF_CENTERMODE_UPTO_NEXTTURN:
				this.mTvCenterModePreview.setImageResource(R.drawable.centermode_uptonextturn_full);
				if(saved) {
					this.mTvCenterModeQuickinfo.setText(R.string.tv_settings_centermode_quickinfo_upto_nextturn_saved);
				} else{
					if(activecentermode == centermode) {
						this.mTvCenterModeQuickinfo.setText(getString(R.string.tv_settings_centermode_quickinfo_upto_nextturn) + " "+ getString(R.string.tv_settings_center_and_rotate_mode_quickinfo_current));
					} else {
						this.mTvCenterModeQuickinfo.setText(R.string.tv_settings_centermode_quickinfo_upto_nextturn);
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

	protected void applyViewListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_centermode_centeruser)){
			@Override
			public void onFocusChanged(final View me, final boolean focus) {
				if(focus) {
					SettingsCenterMode.this.refreshCenterModeImageAndTextByMode(PREF_CENTERMODE_CENTERUSER, false);
				}
			}

			@Override
			public void onClicked(final View me) {
				Preferences.saveCenterMode(SettingsCenterMode.this, PREF_CENTERMODE_CENTERUSER);
				SettingsCenterMode.this.refreshCenterModeImageAndTextByMode(PREF_CENTERMODE_CENTERUSER, true);

				if(SettingsCenterMode.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsCenterMode.this, R.raw.save).start();
				}
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_centermode_upto_nextturn)){
			@Override
			public void onFocusChanged(final View me, final boolean focus) {
				if(focus) {
					SettingsCenterMode.this.refreshCenterModeImageAndTextByMode(PREF_CENTERMODE_UPTO_NEXTTURN, false);
				}
			}

			@Override
			public void onClicked(final View me) {
				/* ROTATEMODE_DRIVINGDIRECTION_UP and centermode_UPTO_NEXTTURN are 'mutex' */
				if(Preferences.getRotateMode(SettingsCenterMode.this) == PREF_ROTATEMODE_DRIVINGDIRECTION_UP){
					final AlertDialog.Builder aBuilder = new AlertDialog.Builder(SettingsCenterMode.this);
					aBuilder.setIcon(R.drawable.questionmark_small);
					aBuilder.setTitle(R.string.rotatemode_dd_up_centermode_upto_nextturn_mutex_title);
					aBuilder.setMessage(R.string.rotatemode_dd_up_centermode_upto_nextturn_mutex_message);
					aBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
						public void onClick(final DialogInterface arg0, final int arg1) {
							Preferences.saveRotateMode(SettingsCenterMode.this, PREF_ROTATEMODE_NORTH_UP);
							Preferences.saveCenterMode(SettingsCenterMode.this, PREF_CENTERMODE_UPTO_NEXTTURN);
							SettingsCenterMode.this.refreshCenterModeImageAndTextByMode(PREF_CENTERMODE_UPTO_NEXTTURN, true);

							if(SettingsCenterMode.super.mMenuVoiceEnabled) {
								MediaPlayer.create(SettingsCenterMode.this, R.raw.save).start();
							}
						}
					});
					aBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
						public void onClick(final DialogInterface arg0, final int arg1) {
							// Nothing to react on here!
						}
					});
					aBuilder.create().show();
				}else{
					Preferences.saveCenterMode(SettingsCenterMode.this, PREF_CENTERMODE_UPTO_NEXTTURN);
					SettingsCenterMode.this.refreshCenterModeImageAndTextByMode(PREF_CENTERMODE_UPTO_NEXTTURN, true);

					if(SettingsCenterMode.super.mMenuVoiceEnabled) {
						MediaPlayer.create(SettingsCenterMode.this, R.raw.save).start();
					}
				}
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_centermode_close)){

			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SettingsCenterMode.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsCenterMode.this, R.raw.close).start();
				}
			}

			@Override
			public void onFocusChanged(final View me, final boolean focus) {
				if(focus) {
					SettingsCenterMode.this.mTvCenterModeQuickinfo.setText(R.string.tv_settings_quickinfo_close_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				SettingsCenterMode.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
