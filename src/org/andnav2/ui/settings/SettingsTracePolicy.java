// Created by plusminus on 19:38:55 - 07.08.2008
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.CommonCallbackAdapter;
import org.andnav2.ui.common.CommonDialogFactory;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingsTracePolicy extends AndNavBaseActivity{
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final int DIALOG_INPUT_OSM_ACCOUNT_INFO = 0;

	// ===========================================================
	// Fields
	// ===========================================================

	protected boolean mDialogMode = false;

	protected ImageButton mIbtnToSDCard;
	protected ImageButton mIbtnToOSMAccountUploader;
	protected ImageButton mIbtnToAndNav;
	protected ImageView mIvToSDCardSelector;
	protected ImageView mIvToOSMAccountUploaderSelector;
	protected ImageView mIvToAndNavSelector;
	protected CheckBox mChkFilterMinimalTraces;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_tracepolicy);

		this.mIbtnToSDCard = (ImageButton)this.findViewById(R.id.ibtn_settings_tracepolicy_sdcard);
		this.mIbtnToOSMAccountUploader = (ImageButton)this.findViewById(R.id.ibtn_settings_tracepolicy_uploadtoosmaccount);
		this.mIbtnToAndNav = (ImageButton)this.findViewById(R.id.ibtn_settings_tracepolicy_uploadtoandnav);

		this.mIvToSDCardSelector = (ImageView)this.findViewById(R.id.iv_settings_tracepolicy_sdcard_selector);
		this.mIvToOSMAccountUploaderSelector = (ImageView)this.findViewById(R.id.iv_settings_tracepolicy_uploadtoosmaccount_selector);
		this.mIvToAndNavSelector = (ImageView)this.findViewById(R.id.iv_settings_tracepolicy_uploadtoandnav_selector);

		this.mChkFilterMinimalTraces = (CheckBox)this.findViewById(R.id.chk_settings_tracepolicy_filter_minimal_requirements);

		this.applyViewListeners();

		this.loadSettingsToViews();
	}

	private void applyViewListeners() {

		this.mChkFilterMinimalTraces.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(final CompoundButton me, final boolean isChecked) {
				Preferences.saveMinimalTraceFilteringEnabled(SettingsTracePolicy.this, isChecked);
			}
		});

		new OnClickOnFocusChangedListenerAdapter(this, R.id.ibtn_settings_tracepolicy_close){
			@Override
			public void onClicked(final View me) {
				if(SettingsTracePolicy.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsTracePolicy.this, R.raw.close).start();
				}

				SettingsTracePolicy.this.finish();
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this, R.id.ibtn_settings_tracepolicy_sdcard){
			@Override
			public void onClicked(final View me) {
				final boolean oldValue = Preferences.getTracePolicyExternal(SettingsTracePolicy.this);
				final boolean newValue = !oldValue;

				if(SettingsTracePolicy.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsTracePolicy.this, R.raw.save).start();
				}

				if(newValue) {
					SettingsTracePolicy.this.mIvToSDCardSelector.setImageResource(R.drawable.checked);
				} else {
					SettingsTracePolicy.this.mIvToSDCardSelector.setImageResource(R.drawable.crossed);
				}

				Preferences.saveTracePolicyExternal(SettingsTracePolicy.this, newValue);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this, R.id.ibtn_settings_tracepolicy_uploadtoandnav){
			@Override
			public void onClicked(final View me) {
				final boolean oldValue = Preferences.getTracePolicyAndnavOrg(SettingsTracePolicy.this);
				final boolean newValue = !oldValue;

				if(SettingsTracePolicy.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsTracePolicy.this, R.raw.save).start();
				}


				if(newValue) {
					SettingsTracePolicy.this.mIvToAndNavSelector.setImageResource(R.drawable.checked);
				} else {
					SettingsTracePolicy.this.mIvToAndNavSelector.setImageResource(R.drawable.crossed);
				}

				Preferences.saveTracePolicyAndnavOrg(SettingsTracePolicy.this, newValue);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this, R.id.ibtn_settings_tracepolicy_uploadtoosmaccount){
			@Override
			public void onClicked(final View me) {
				if(Preferences.getTracePolicyOSM(SettingsTracePolicy.this)){
					SettingsTracePolicy.this.mIvToOSMAccountUploaderSelector.setImageResource(R.drawable.crossed);
					Preferences.saveTracePolicyOSM(SettingsTracePolicy.this, false);
				}else{
					showDialog(DIALOG_INPUT_OSM_ACCOUNT_INFO);
				}
			}
		};
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected Dialog onCreateDialog(final int id) {
		switch(id){
			case DIALOG_INPUT_OSM_ACCOUNT_INFO:
				return CommonDialogFactory.createOSMAccountInfoDialog(this, new CommonCallbackAdapter<Boolean>(){
					@Override
					public void onSuccess(final Boolean result) {
						if(result){
							final boolean oldValue = Preferences.getTracePolicyOSM(SettingsTracePolicy.this);
							final boolean newValue = !oldValue;

							if(SettingsTracePolicy.super.mMenuVoiceEnabled) {
								MediaPlayer.create(SettingsTracePolicy.this, R.raw.save).start();
							}


							if(newValue) {
								SettingsTracePolicy.this.mIvToOSMAccountUploaderSelector.setImageResource(R.drawable.checked);
							} else {
								SettingsTracePolicy.this.mIvToOSMAccountUploaderSelector.setImageResource(R.drawable.crossed);
							}

							Preferences.saveTracePolicyOSM(SettingsTracePolicy.this, newValue);
						}
					}
				});
			default:
				return null;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void loadSettingsToViews() {
		this.mChkFilterMinimalTraces.setChecked(Preferences.getMinimalTraceFilteringEnabled(this));

		if(Preferences.getTracePolicyExternal(this)) {
			this.mIvToSDCardSelector.setImageResource(R.drawable.checked);
		} else {
			this.mIvToSDCardSelector.setImageResource(R.drawable.crossed);
		}

		if(Preferences.getTracePolicyOSM(this)) {
			this.mIvToOSMAccountUploaderSelector.setImageResource(R.drawable.checked);
		} else {
			this.mIvToOSMAccountUploaderSelector.setImageResource(R.drawable.crossed);
		}

		if(Preferences.getTracePolicyAndnavOrg(this)) {
			this.mIvToAndNavSelector.setImageResource(R.drawable.checked);
		} else {
			this.mIvToAndNavSelector.setImageResource(R.drawable.crossed);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
