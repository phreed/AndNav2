// Created by plusminus on 20:39:13 - 10.04.2008
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.ors.adt.rs.DirectionsLanguage;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.CommonCallbackAdapter;
import org.andnav2.ui.common.CommonDialogFactory;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.common.adapters.CountryAdapter;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;


public class SettingsDirectionLanguage extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	protected static final int DIALOG_SELECT_DIALECT = 0;
	protected static final int REQUESTCODE_VOICE = 0;

	// ===========================================================
	// Fields
	// ===========================================================

	protected TextView mTvQuickInfo;
	protected ListView mcountryListView;
	protected Country mNationalityToSelectDialectFrom;

	// ===========================================================
	// Constructors
	// ===========================================================
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_directionslanguage);

		this.mTvQuickInfo = (TextView)findViewById(R.id.tv_settings_directionslanguage_quickinfo);

		this.mcountryListView = (ListView)findViewById(R.id.list_settings_directionslanguage_flags);

		/* Make the Country-Grid be filled with all Countries available. */
		this.mcountryListView.setAdapter(new CountryAdapter(this, Country.getAllWithDrivingDirectionsLanguage()));

		final int dialectNameResID = Preferences.getDrivingDirectionsLanguage(this).NAMERESID;
		final String dialect = (dialectNameResID == R.string.dialect_none) ? "" : " - " + getString(dialectNameResID);
		this.mTvQuickInfo.setText(getString(Preferences.getDrivingDirectionsLanguage(this).getMotherCountry().NAMERESID) + dialect + " " + getString(R.string.tv_settings_directionslanguage_quickinfo_current_appendix));

		this.applyGridViewListeners();
		this.applyTopMenuButtonListeners();
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	private static final String STATE_NATIONALITYTOSELECTDIALECTFROM = "state_nationalitytoselectdialectfrom";

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		this.mNationalityToSelectDialectFrom = Country.values()[savedInstanceState.getInt(STATE_NATIONALITYTOSELECTDIALECTFROM)];
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(final Bundle savedInstanceState) {
		if(this.mNationalityToSelectDialectFrom != null) {
			savedInstanceState.putInt(STATE_NATIONALITYTOSELECTDIALECTFROM, this.mNationalityToSelectDialectFrom.ordinal());
		} else {
			savedInstanceState.putInt(STATE_NATIONALITYTOSELECTDIALECTFROM, Country.USA.ordinal());
		}

		super.onRestoreInstanceState(savedInstanceState);
	}


	@Override
	protected Dialog onCreateDialog(final int id) {
		switch(id){
			case DIALOG_SELECT_DIALECT:
				return CommonDialogFactory.createSelectDialectFromNationality(this, this.mNationalityToSelectDialectFrom, new CommonCallbackAdapter<DirectionsLanguage>(){
					@Override
					public void onSuccess(final DirectionsLanguage result) {
						runOnUiThread(new Runnable(){
							public void run(){
								final int dialectNameResID = result.NAMERESID;
								final boolean isDialect = dialectNameResID == R.string.dialect_none;
								final String dialect = isDialect ? "" : " - " + getString(dialectNameResID);
								SettingsDirectionLanguage.this.mTvQuickInfo.setText(getString(result.getMotherCountry().NAMERESID) + dialect + " " + getString(R.string.tv_settings_directionslanguage_quickinfo_current_appendix));

								Preferences.saveDrivingDirectionsLanguage(SettingsDirectionLanguage.this, result);
							}
						});
					}
				});
			default:
				return null;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void applyTopMenuButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_directionslanguage_voice)){
			@Override
			public void onClicked(final View me) {
				final Intent voiceIntent = new Intent(SettingsDirectionLanguage.this, SettingsVoice.class);
				SettingsDirectionLanguage.this.startActivityForResult(voiceIntent, REQUESTCODE_VOICE);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_directionslanguage_close)){
			@Override
			public void onBoth(final View arg0, final boolean focused) {
				if(focused && SettingsDirectionLanguage.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsDirectionLanguage.this, R.raw.close).start();
				}
			}

			@Override
			public void onFocusChanged(final View me, final boolean focus) {
				//				if(focus)
				//						SettingsDirectionLanguage.this.tvQuickInfo.setText(R.string.tv_settings_quickinfo_close_focused);
			}

			@Override
			public void onClicked(final View me) {
				SettingsDirectionLanguage.this.finish();
			}
		};
	}

	/** Applies an OnItemSelectedListener for displaying the country-name
	 * and an OnItemClickListener for calling the next Activity. */
	protected void applyGridViewListeners() {
		/* Apply OnItemClickListener to show the country + "saved" in the quickinfo. */
		this.mcountryListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(final AdapterView<?> arg0, final View v, final int position, final long arg3) {
				if(v != null){
					final Country nat = ((CountryAdapter)SettingsDirectionLanguage.this.mcountryListView.getAdapter()).getItem(position);
					/* First make the Country-Name appear in the quick-info. */
					SettingsDirectionLanguage.this.mTvQuickInfo.setText(getString(nat.NAMERESID) + " "
							+ SettingsDirectionLanguage.this.getText(R.string.tv_settings_directionslanguage_quickinfo_saved_appendix));

					final DirectionsLanguage[] dirLangs = nat.getDrivingDiectionsLanguages();
					if(dirLangs == null || dirLangs.length == 0) {
						throw new IllegalArgumentException("No languages for ");
					}

					if(dirLangs.length == 1){
						Preferences.saveDrivingDirectionsLanguage(SettingsDirectionLanguage.this, dirLangs[0]);
					}else{
						/* Show dialog. */
						SettingsDirectionLanguage.this.mNationalityToSelectDialectFrom = nat;
						showDialog(DIALOG_SELECT_DIALECT);
					}
				}
			}
		});

		/* Apply OnItemSelectedListener to show Country-Name in TextView. */
		this.mcountryListView.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(final AdapterView<?> parent, final View v, final int position, final long id) {
				if(v != null){
					final Country selNation = ((CountryAdapter)SettingsDirectionLanguage.this.mcountryListView.getAdapter()).getItem(position);
					if(selNation.equals(Preferences.getDrivingDirectionsLanguage(SettingsDirectionLanguage.this))) {
						SettingsDirectionLanguage.this.mTvQuickInfo.setText(getString(selNation.NAMERESID)  + " " + getString(R.string.tv_settings_directionslanguage_quickinfo_current_appendix));
					} else {
						SettingsDirectionLanguage.this.mTvQuickInfo.setText(getString(selNation.NAMERESID));
					}
				}
			}

			public void onNothingSelected(final AdapterView<?> arg0) {
				SettingsDirectionLanguage.this.mTvQuickInfo.setText(getString(Preferences.getDrivingDirectionsLanguage(SettingsDirectionLanguage.this).NAMERESID)
						+ " " + getString(R.string.tv_settings_directionslanguage_quickinfo_current_appendix));
			}
		});
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
