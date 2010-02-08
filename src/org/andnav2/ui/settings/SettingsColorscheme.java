// Created by plusminus on 19:38:55 - 07.08.2008
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.content.ContentResolver;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SettingsColorscheme extends AndNavBaseActivity{
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int BRIGHTNESS_DEFAULT = 200;

	// ===========================================================
	// Fields
	// ===========================================================

	private  RadioButton mRadDayScheme;
	private  RadioButton mRadDefaultScheme;
	private  RadioButton mRadNightScheme;
	private  SeekBar mSeekOverallBrightness;
	private ContentResolver mContentResolver;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);

		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_colorscheme);

		this.mContentResolver = this.getContentResolver();

		this.mSeekOverallBrightness = (SeekBar) this.findViewById(R.id.seek_settings_colorscheme_overallbrightness);

		this.mRadDayScheme = (RadioButton) this.findViewById(R.id.rad_settings_colorscheme_daymode);
		this.mRadDefaultScheme = (RadioButton) this.findViewById(R.id.rad_settings_colorscheme_default);
		this.mRadNightScheme = (RadioButton) this.findViewById(R.id.rad_settings_colorscheme_nightmode);

		this.applyTopButtonListeners();
		this.applyButtonListeners();

		this.loadSavedFlagsToViews();
	}

	private void applyTopButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_colorscheme_close)){
			@Override
			public void onBoth(final View arg0, final boolean focused) {
				if(SettingsColorscheme.super.mMenuVoiceEnabled && focused) {
					MediaPlayer.create(SettingsColorscheme.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				SettingsColorscheme.this.finish();
			}
		};
	}

	private void applyButtonListeners() {
		{
			this.mSeekOverallBrightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

				public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromTouch) {
					setBrightness(progress);
				}

				public void onStartTrackingTouch(final SeekBar seekBar) { }

				public void onStopTrackingTouch(final SeekBar seekBar) { }
			});
		}

		{ // Workaround for false treating of RadioButtons when they are not direct children of RadioGroup
			this.mRadDayScheme.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				public void onCheckedChanged(final CompoundButton b, final boolean isChecked) {
					if(isChecked){
						setDayScheme();
					}
				}
			});
			this.findViewById(R.id.iv_settings_colorscheme_daymode).setOnClickListener(new OnClickListener(){
				public void onClick(final View v) {
					setDayScheme();
					SettingsColorscheme.this.mRadDayScheme.setChecked(true);
				}
			});

			this.mRadDefaultScheme.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				public void onCheckedChanged(final CompoundButton b, final boolean isChecked) {
					if(isChecked){
						setDefaultScheme();
					}
				}
			});
			this.findViewById(R.id.iv_settings_colorscheme_default).setOnClickListener(new OnClickListener(){
				public void onClick(final View v) {
					setDefaultScheme();
					SettingsColorscheme.this.mRadDefaultScheme.setChecked(true);
				}
			});

			this.mRadNightScheme.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				public void onCheckedChanged(final CompoundButton b, final boolean isChecked) {
					if(isChecked){
						setNightScheme();
					}
				}
			});
			this.findViewById(R.id.iv_settings_colorscheme_nightmode).setOnClickListener(new OnClickListener(){
				public void onClick(final View v) {
					setNightScheme();
					SettingsColorscheme.this.mRadNightScheme.setChecked(true);
				}
			});
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	private void setDayScheme() {
		Preferences.saveSharedThemeID(SettingsColorscheme.this, PREF_THEME_DAY_RESID);
		SettingsColorscheme.this.mRadNightScheme.setChecked(false);
		SettingsColorscheme.this.mRadDefaultScheme.setChecked(false);
	}

	private void setNightScheme() {
		Preferences.saveSharedThemeID(SettingsColorscheme.this, PREF_THEME_NIGHT_RESID);
		SettingsColorscheme.this.mRadDefaultScheme.setChecked(false);
		SettingsColorscheme.this.mRadDayScheme.setChecked(false);
	}

	private void setDefaultScheme() {
		Preferences.saveSharedThemeID(SettingsColorscheme.this, PREF_THEME_DEFAULT_RESID);
		SettingsColorscheme.this.mRadDayScheme.setChecked(false);
		SettingsColorscheme.this.mRadNightScheme.setChecked(false);
	}

	private void setBrightness(final int pBrightness) {
		android.provider.Settings.System.putInt(this.mContentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS, pBrightness);
	}

	private int getBrightness(){
		return android.provider.Settings.System.getInt(this.mContentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS, BRIGHTNESS_DEFAULT);
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private void loadSavedFlagsToViews() {
		final int themeID = Preferences.getSharedThemeID(SettingsColorscheme.this);

		this.mSeekOverallBrightness.setProgress(getBrightness());

		switch(themeID){
			case PREF_THEME_NIGHT_RESID:
				this.mRadNightScheme.setChecked(true);
				break;
			case PREF_THEME_DAY_RESID:
				this.mRadDayScheme.setChecked(true);
				break;
			case PREF_THEME_DEFAULT_RESID:
			default:
				this.mRadDefaultScheme.setChecked(true);
				break;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
