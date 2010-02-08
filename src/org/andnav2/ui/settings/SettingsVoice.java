//Created by plusminus on 19:01:29 - 20.05.2008
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.sound.tts.Util;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.CommonCallbackAdapter;
import org.andnav2.ui.common.CommonDialogFactory;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
//FIXME import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class SettingsVoice extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	private static final String TEST_MESSAGE = "Text to speech is working properly.";

	protected static final int ADVANCED_REQUESTCODE = 0x1337;
	protected static final int TTS_CONFIGURATIONMANAGER_REQUESTCODE = ADVANCED_REQUESTCODE + 1;

	protected static final int DIALOG_SHOWTTS = 0;

	// ===========================================================
	// Fields
	// ===========================================================

	protected CheckBox chkMenuVoice;
	protected CheckBox chkDirectionVoice;
	protected TextView tvQuickInfo;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_voice);

		this.tvQuickInfo = (TextView)this.findViewById(R.id.tv_settings_voice_quickinfo);

		this.chkMenuVoice = (CheckBox)this.findViewById(R.id.chk_settings_voice_menuvoice);
		this.chkMenuVoice.setChecked(Preferences.getMenuVoiceEnabled(this));
		this.chkDirectionVoice = (CheckBox)this.findViewById(R.id.chk_settings_voice_directionvoice);
		this.chkDirectionVoice.setChecked(Preferences.getDirectionVoiceEnabled(this));

		this.applyTopButtonListeners();
		this.applyCheckBoxListeners();
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
			case DIALOG_SHOWTTS:
				return CommonDialogFactory.createTTSConfigOrTestDialog(SettingsVoice.this, new CommonCallbackAdapter<Integer>(){
// FIXME 					private TextToSpeech mTTS;
					private boolean mTTSAvailable = false;

					@Override
					public void onSuccess(final Integer result) {
						switch(result){
							case 0:
								final boolean managerShown = Util.show(SettingsVoice.this);
								if(!managerShown){
									Toast.makeText(SettingsVoice.this, R.string.tts_not_installed_message, Toast.LENGTH_LONG).show();
								}
								return;
							case 1:
// FIXME 
//								if(this.mTTS == null){
//									this.mTTS = new TextToSpeech(SettingsVoice.this, new TextToSpeech.OnInitListener(){
//										public void onInit(final int version) {
//											mTTSAvailable = true;
//											//										mTTS.setLanguage(Preferences.getDrivingDirectionsLanguage(SettingsVoice.this).getIETFLAnguageTag());
//											mTTS.speak(TEST_MESSAGE, 0, null);
//										}
//									});
//								}else{
//									if(this.mTTSAvailable) {
//										this.mTTS.speak(TEST_MESSAGE, 0, null);
//									}
//								}
								return;
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

	private void applyTopButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_voice_goadvanced)){
			@Override
			public void onClicked(final View me) {
				// TODO No voice-file found for "advanced" or "details"
				//				if(SettingsVoice.super.mMenuVoiceEnabled)
				//					MediaPlayer.create(SettingsVoice.this, R.raw.close).start();

				final Intent goAdvancedIntent = new Intent(SettingsVoice.this, SettingsDirectionVoice.class);

				SettingsVoice.this.startActivityForResult(goAdvancedIntent, ADVANCED_REQUESTCODE);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_voice_tts)){
			@Override
			public void onClicked(final View me) {
				showDialog(DIALOG_SHOWTTS);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_voice_close)){
			@Override
			public void onClicked(final View me) {
				if(SettingsVoice.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsVoice.this, R.raw.close).start();
				}

				SettingsVoice.this.finish();
			}
		};
	}

	private void applyCheckBoxListeners() {
		this.chkMenuVoice.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(final CompoundButton me, final boolean checked) {
				SettingsVoice.super.mMenuVoiceEnabled = SettingsVoice.this.chkMenuVoice.isChecked();

				if(SettingsVoice.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsVoice.this, R.raw.save).start();
				}
				Preferences.saveMenuVoiceEnabled(SettingsVoice.this, SettingsVoice.this.chkMenuVoice.isChecked());
			}
		});
		new OnClickOnFocusChangedListenerAdapter(this.chkMenuVoice){
			@Override
			public void onBoth(final View me, final boolean focused) {
				SettingsVoice.this.tvQuickInfo.setText(R.string.tv_settings_voice_quickinfo_menuvoice_description);
			}
		};

		this.chkDirectionVoice.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(final CompoundButton me, final boolean checked) {
				if(SettingsVoice.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsVoice.this, R.raw.save).start();
				}

				Preferences.saveDirectionVoiceEnabled(SettingsVoice.this, checked);
			}
		});
		new OnClickOnFocusChangedListenerAdapter(this.chkDirectionVoice){
			@Override
			public void onBoth(final View me, final boolean focused) {
				SettingsVoice.this.tvQuickInfo.setText(R.string.tv_settings_voice_quickinfo_directionvoice_description);
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
