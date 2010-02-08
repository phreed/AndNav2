//Created by plusminus on 19:01:29 - 20.05.2008
package org.andnav2.ui.settings;

import java.util.HashMap;

import org.andnav2.R;
import org.andnav2.adt.UnitSystem;
import org.andnav2.adt.voice.DistanceVoiceElement;
import org.andnav2.preferences.PreferenceConstants;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.media.MediaPlayer;
import android.os.Bundle;
//FIXME import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class SettingsDirectionVoice extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	private static final String TEST_MESSAGE = "In 500 meters, turn right into King Street.";

	// ===========================================================
	// Fields
	// ===========================================================

	protected UnitSystem mUnitSystem;
	private Spinner mSPin50;
	private Spinner mSPin100;
	private Spinner mSPin200;
	private Spinner mSPin500;
	private Spinner mSPin1000;
	private Spinner mSPin2000;
	private Spinner mSPin5000;
	private Spinner mSPin10000;
	private Spinner mSPin25000;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_directionvoice);
		this.mUnitSystem = Preferences.getUnitSystem(this);

		this.mSPin50 = (Spinner)findViewById(R.id.spin_settings_directionvoice_50_dist);
		this.mSPin100 = (Spinner)findViewById(R.id.spin_settings_directionvoice_100_dist);
		this.mSPin200 = (Spinner)findViewById(R.id.spin_settings_directionvoice_200_dist);
		this.mSPin500 = (Spinner)findViewById(R.id.spin_settings_directionvoice_500_dist);
		this.mSPin1000 = (Spinner)findViewById(R.id.spin_settings_directionvoice_1k_dist);
		this.mSPin2000 = (Spinner)findViewById(R.id.spin_settings_directionvoice_2k_dist);
		this.mSPin5000 = (Spinner)findViewById(R.id.spin_settings_directionvoice_5k_dist);
		this.mSPin10000 = (Spinner)findViewById(R.id.spin_settings_directionvoice_10k_dist);
		this.mSPin25000 = (Spinner)findViewById(R.id.spin_settings_directionvoice_25k_dist);

		this.refreshUnitTexts();
		this.refreshSpinnerTextAndStates();

		this.applyTopButtonListeners();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onPause() {
		Preferences.saveTurnVoiceSayList(SettingsDirectionVoice.this, getTurnVoiceSayList());
		super.onPause();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void applyTopButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_directionvoice_testtts)){
// FIXME			private TextToSpeech mTTS;
			private boolean mTTSAvailable = false;

			@Override
			public void onClicked(final View me) {
				/* FIXME
				if(this.mTTS == null){
					this.mTTS = new TextToSpeech(SettingsDirectionVoice.this, new TextToSpeech.OnInitListener(){
						public void onInit(final int version) {
							mTTSAvailable = true;
							//							mTTS.setLanguage(Preferences.getDrivingDirectionsLanguage(SettingsDirectionVoice.this).getIETFLAnguageTag());
							mTTS.speak(TEST_MESSAGE, 0, null);
						}
					});
				}else{
					if(this.mTTSAvailable) {
						this.mTTS.speak(TEST_MESSAGE, 0, null);
					}
				}*/
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_directionvoice_close)){
			@Override
			public void onClicked(final View me) {
				if(SettingsDirectionVoice.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsDirectionVoice.this, R.raw.close).start();
				}

				SettingsDirectionVoice.this.finish();
			}
		};
	}

	private void refreshSpinnerTextAndStates() {
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spin_settings_directionvoice_saywhat_items, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		this.mSPin50.setAdapter(adapter);
		this.mSPin100.setAdapter(adapter);
		this.mSPin200.setAdapter(adapter);
		this.mSPin500.setAdapter(adapter);
		this.mSPin1000.setAdapter(adapter);
		this.mSPin2000.setAdapter(adapter);
		this.mSPin5000.setAdapter(adapter);
		this.mSPin10000.setAdapter(adapter);
		this.mSPin25000.setAdapter(adapter);

		final HashMap<Integer, Integer> turnVoiceList = Preferences.getTurnVoiceSayList(this);
		this.mSPin50.setSelection(turnVoiceList.get(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[0]));
		this.mSPin100.setSelection(turnVoiceList.get(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[1]));
		this.mSPin200.setSelection(turnVoiceList.get(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[2]));
		this.mSPin500.setSelection(turnVoiceList.get(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[3]));
		this.mSPin1000.setSelection(turnVoiceList.get(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[4]));
		this.mSPin2000.setSelection(turnVoiceList.get(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[5]));
		this.mSPin5000.setSelection(turnVoiceList.get(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[6]));
		this.mSPin10000.setSelection(turnVoiceList.get(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[7]));
		this.mSPin25000.setSelection(turnVoiceList.get(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[8]));
	}

	private void refreshUnitTexts() {
		((TextView)findViewById(R.id.tv_settings_directionvoice_50)).setText(this.mUnitSystem.convertFromMetricDistanceVoice(DistanceVoiceElement.M_50).LENGTH_UNITWISE + this.mUnitSystem.mAbbrMeterScale);
		((TextView)findViewById(R.id.tv_settings_directionvoice_100)).setText(this.mUnitSystem.convertFromMetricDistanceVoice(DistanceVoiceElement.M_100).LENGTH_UNITWISE + this.mUnitSystem.mAbbrMeterScale);
		((TextView)findViewById(R.id.tv_settings_directionvoice_200)).setText(this.mUnitSystem.convertFromMetricDistanceVoice(DistanceVoiceElement.M_200).LENGTH_UNITWISE + this.mUnitSystem.mAbbrMeterScale);
		((TextView)findViewById(R.id.tv_settings_directionvoice_500)).setText(this.mUnitSystem.convertFromMetricDistanceVoice(DistanceVoiceElement.M_500).LENGTH_UNITWISE + this.mUnitSystem.mAbbrMeterScale);

		((TextView)findViewById(R.id.tv_settings_directionvoice_1k)).setText(this.mUnitSystem.convertFromMetricDistanceVoice(DistanceVoiceElement.KM_ONE).LENGTH_UNITWISE + this.mUnitSystem.mAbbrKilometersScale);
		((TextView)findViewById(R.id.tv_settings_directionvoice_2k)).setText(this.mUnitSystem.convertFromMetricDistanceVoice(DistanceVoiceElement.KM_TWO).LENGTH_UNITWISE + this.mUnitSystem.mAbbrKilometersScale);
		((TextView)findViewById(R.id.tv_settings_directionvoice_5k)).setText(this.mUnitSystem.convertFromMetricDistanceVoice(DistanceVoiceElement.KM_FIVE).LENGTH_UNITWISE + this.mUnitSystem.mAbbrKilometersScale);
		((TextView)findViewById(R.id.tv_settings_directionvoice_10k)).setText(this.mUnitSystem.convertFromMetricDistanceVoice(DistanceVoiceElement.KM_TEN).LENGTH_UNITWISE + this.mUnitSystem.mAbbrKilometersScale);
		((TextView)findViewById(R.id.tv_settings_directionvoice_25k)).setText(this.mUnitSystem.convertFromMetricDistanceVoice(DistanceVoiceElement.KM_TWENTY_FIVE).LENGTH_UNITWISE + this.mUnitSystem.mAbbrKilometersScale);
	}

	private HashMap<Integer, Integer> getTurnVoiceSayList(){
		final HashMap<Integer, Integer> out = new HashMap<Integer, Integer>();
		out.put(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[0], this.mSPin50.getSelectedItemPosition());
		out.put(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[1], this.mSPin100.getSelectedItemPosition());
		out.put(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[2], this.mSPin200.getSelectedItemPosition());
		out.put(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[3], this.mSPin500.getSelectedItemPosition());
		out.put(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[4], this.mSPin1000.getSelectedItemPosition());
		out.put(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[5], this.mSPin2000.getSelectedItemPosition());
		out.put(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[6], this.mSPin5000.getSelectedItemPosition());
		out.put(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[7], this.mSPin10000.getSelectedItemPosition());
		out.put(PreferenceConstants.PREF_TURNVOICE_ELEMENTS[8], this.mSPin25000.getSelectedItemPosition());
		return out;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
