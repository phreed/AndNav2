// Created by plusminus on 20:42:56 - 30.06.2008
package org.andnav2.ui.settings;

import java.text.DecimalFormat;

import org.andnav2.R;
import org.andnav2.adt.UnitSystem;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SettingsStatistics extends AndNavBaseActivity {

	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_statistics);

		applyTopButtonListeners();
		fillStatisticsFields();
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

	private void applyTopButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_statistics_close)){
			@Override
			public void onBoth(final View arg0, final boolean focused) {
				if(SettingsStatistics.super.mMenuVoiceEnabled && focused) {
					MediaPlayer.create(SettingsStatistics.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				SettingsStatistics.this.finish();
			}
		};
	}

	private void fillStatisticsFields() {
		final UnitSystem us = Preferences.getUnitSystem(this);

		final DecimalFormat df = new DecimalFormat("#,###,##0.0");
		final long metersDrivenSession = Preferences.getStatisticsMetersDrivenSession(this);
		final long secondsDrivenSession = Preferences.getStatisticsSecondsDrivenSession(this);

		final int maxSpeedSession = Preferences.getStatisticsMaxSpeedSession(this);


		final long metersDrivenOverall = Preferences.getStatisticsMetersDrivenOverall(this);
		final long secondsDrivenOverall = Preferences.getStatisticsSecondsDrivenOverall(this);

		final int maxSpeedOverall = Math.max(maxSpeedSession, Preferences.getStatisticsMaxSpeedBeforeSession(this));

		// ############# OVERALL #############
		((TextView) findViewById(R.id.tv_settings_statistics_overall_maxspeed)).setText(""
				+ df.format(us.mScaleToMetersPerSecond
						* maxSpeedOverall)
						+ us.mAbbrKilometersPerHourScale);


		((TextView) findViewById(R.id.tv_settings_statistics_overall_average)).setText(""
				+ df.format(us.mScaleToMetersPerSecond
						* (metersDrivenOverall / secondsDrivenOverall)) + us.mAbbrKilometersPerHourScale);

		((TextView) findViewById(R.id.tv_settings_statistics_overall_distance)).setText(""
				+ df.format(us.mScaleToKilometers * (metersDrivenOverall / 1000.0f))
				+ us.mAbbrKilometersScale);

		((TextView) findViewById(R.id.tv_settings_statistics_overall_time)).setText(""
				+ df.format(secondsDrivenOverall / (60.0f * 60.0f)) + "h");


		// ############# SESSION #############
		if(Preferences.getStatisticsSessionStart(this) == NOT_SET){
			((TextView) findViewById(R.id.tv_settings_statistics_session_maxspeed)).setText(getString(R.string.na));
			((TextView) findViewById(R.id.tv_settings_statistics_session_average)).setText(getString(R.string.na));
			((TextView) findViewById(R.id.tv_settings_statistics_session_distance)).setText(getString(R.string.na));
			((TextView) findViewById(R.id.tv_settings_statistics_session_time)).setText(getString(R.string.na));
		}else{
			((TextView) findViewById(R.id.tv_settings_statistics_session_maxspeed)).setText(""
					+ df.format(us.mScaleToMetersPerSecond
							* maxSpeedSession)
							+ us.mAbbrKilometersPerHourScale);

			((TextView) findViewById(R.id.tv_settings_statistics_session_average)).setText(""
					+ df.format(us.mScaleToMetersPerSecond
							* (metersDrivenSession / secondsDrivenSession)) + us.mAbbrKilometersPerHourScale);

			((TextView) findViewById(R.id.tv_settings_statistics_session_distance)).setText(""
					+ df.format(us.mScaleToKilometers * metersDrivenSession / 1000.0f)
					+ us.mAbbrKilometersScale);

			((TextView) findViewById(R.id.tv_settings_statistics_session_time)).setText(""
					+ df.format(secondsDrivenSession / (60.0f * 60.0f)) + "h");
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
