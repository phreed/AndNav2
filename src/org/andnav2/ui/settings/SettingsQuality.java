// Created by plusminus on 18:41:23 - 10.04.2008
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class SettingsQuality extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected TextView qualityQuickinfo;
	protected ImageView ivQualityRate;
	protected ImageView ivBatteryRate;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_quality);

		this.qualityQuickinfo = (TextView)this.findViewById(R.id.tv_settings_quality_quickinfo);
		this.ivBatteryRate = (ImageView)this.findViewById(R.id.iv_settings_quality_battery_rate);
		this.ivQualityRate = (ImageView)this.findViewById(R.id.iv_settings_quality_quality_rate);

		this.applyButtonListeners();

		updateViews(Preferences.getDisplayQuality(this));
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

	private void updateViews(final int aQuality) {
		switch(aQuality){
			case PREF_DISPLAYQUALITY_LOW:
				this.updateQuickInfo(R.string.tv_settings_quality_quickinfo_low_description);
				this.findViewById(R.id.btn_settings_quality_low).requestFocus();
				this.updateQualityRateBar(1);
				this.updateBatteryRateBar(5);
				break;
			case PREF_DISPLAYQUALITY_STANDARD:
				this.updateQuickInfo(R.string.tv_settings_quality_quickinfo_standard_description);
				this.findViewById(R.id.btn_settings_quality_standard).requestFocus();
				this.updateQualityRateBar(3);
				this.updateBatteryRateBar(3);
				break;
			case PREF_DISPLAYQUALITY_HIGH:
				this.updateQuickInfo(R.string.tv_settings_quality_quickinfo_high_description);
				this.findViewById(R.id.btn_settings_quality_high).requestFocus();
				this.updateQualityRateBar(4);
				this.updateBatteryRateBar(2);
				break;
			case PREF_DISPLAYQUALITY_BEST:
				this.updateQuickInfo(R.string.tv_settings_quality_quickinfo_best_description);
				this.findViewById(R.id.btn_settings_quality_best).requestFocus();
				this.updateQualityRateBar(5);
				this.updateBatteryRateBar(1);
				break;
		}
	}

	private void updateQuickInfo(final int resid){
		this.qualityQuickinfo.setText(resid);
	}

	private void updateQualityRateBar(final int rate){
		int resid = 0;
		switch(rate){
			case 5:
				resid = R.drawable.rate_5of5_full;
				break;
			case 4:
				resid = R.drawable.rate_4of5_full;
				break;
			case 3:
				resid = R.drawable.rate_3of5_full;
				break;
			case 2:
				resid = R.drawable.rate_2of5_full;
				break;
			case 1:
				resid = R.drawable.rate_1of5_full;
				break;
		}
		this.ivQualityRate.setImageResource(resid);
	}

	private void updateBatteryRateBar(final int rate){
		int resid = 0;
		switch(rate){
			case 5:
				resid = R.drawable.rate_5of5_full;
				break;
			case 4:
				resid = R.drawable.rate_4of5_full;
				break;
			case 3:
				resid = R.drawable.rate_3of5_full;
				break;
			case 2:
				resid = R.drawable.rate_2of5_full;
				break;
			case 1:
				resid = R.drawable.rate_1of5_full;
				break;
		}
		this.ivBatteryRate.setImageResource(resid);
	}

	protected void applyButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.btn_settings_quality_low)){
			@Override
			public void onFocusChanged(final View me, final boolean focused) {
				if(focused) {
					updateViews(PREF_DISPLAYQUALITY_LOW);
				}
			}

			@Override
			public void onClicked(final View me) {
				if(SettingsQuality.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsQuality.this, R.raw.save).start();
				}

				Preferences.saveDisplayQuality(SettingsQuality.this, PREF_DISPLAYQUALITY_LOW);
				updateViews(PREF_DISPLAYQUALITY_LOW);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.btn_settings_quality_standard)){
			@Override
			public void onFocusChanged(final View me, final boolean focused) {
				if(focused) {
					updateViews(PREF_DISPLAYQUALITY_STANDARD);
				}
			}

			@Override
			public void onClicked(final View me) {
				if(SettingsQuality.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsQuality.this, R.raw.save).start();
				}

				Preferences.saveDisplayQuality(SettingsQuality.this, PREF_DISPLAYQUALITY_STANDARD);
				updateViews(PREF_DISPLAYQUALITY_STANDARD);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.btn_settings_quality_high)){
			@Override
			public void onFocusChanged(final View me, final boolean focused) {
				if(focused) {
					updateViews(PREF_DISPLAYQUALITY_HIGH);
				}
			}

			@Override
			public void onClicked(final View me) {
				if(SettingsQuality.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsQuality.this, R.raw.save).start();
				}

				Preferences.saveDisplayQuality(SettingsQuality.this, PREF_DISPLAYQUALITY_HIGH);
				updateViews(PREF_DISPLAYQUALITY_HIGH);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.btn_settings_quality_best)){
			@Override
			public void onFocusChanged(final View me, final boolean focused) {
				if(focused) {
					updateViews(PREF_DISPLAYQUALITY_BEST);
				}
			}

			@Override
			public void onClicked(final View me) {
				if(SettingsQuality.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsQuality.this, R.raw.save).start();
				}

				Preferences.saveDisplayQuality(SettingsQuality.this, PREF_DISPLAYQUALITY_BEST);
				updateViews(PREF_DISPLAYQUALITY_BEST);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.btn_settings_quality_close)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SettingsQuality.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsQuality.this, R.raw.close).start();
				}
			}

			@Override
			public void onFocusChanged(final View me, final boolean focused) {
				if(focused) {
					SettingsQuality.this.qualityQuickinfo.setText(R.string.tv_settings_quickinfo_close_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				SettingsQuality.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
