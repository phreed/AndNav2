// Created by plusminus on 21:30:10 - 15.05.2008
package org.andnav2.ui.sd;

import java.util.ArrayList;
import java.util.List;

import org.andnav2.R;
import org.andnav2.adt.keyboardlayouts.AbstractKeyBoardLayout;
import org.andnav2.db.DBManager;
import org.andnav2.db.DataBaseException;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.GeocodedAddress;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.sys.ors.lus.LUSRequester;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.InlineAutoCompleterCombined;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.common.adapters.KeyLayoutAdapter;
import org.andnav2.util.constants.Constants;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.TextKeyListener;
import android.text.method.TextKeyListener.Capitalize;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SDCity extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	/* REQUEST-CODES for SubActivities. */
	protected static final int REQUESTCODE_SD_STREET = 0x1537;

	// ===========================================================
	// Fields
	// ===========================================================

	protected GridView keyBoardGrid;
	protected EditText cityNameEditText;
	protected Bundle bundleCreatedWith;
	protected String acItem;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle, true); // We need DataState-Info
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.sd_city);

		/* Save the Extras Bundle of the Intent this Activity
		 * was created with, because it contains the Information,
		 * that will finally be used for a GeoCode API. */
		this.bundleCreatedWith = this.getIntent().getExtras();

		final AbstractKeyBoardLayout aKeyBoardLayout = Preferences.getKeyboardLayout(this);
		this.keyBoardGrid = (GridView)findViewById(R.id.grid_sd_city_keyboard);

		this.keyBoardGrid.setNumColumns(aKeyBoardLayout.getColumnsByDisplay(getWindowManager().getDefaultDisplay()));

		this.cityNameEditText = (EditText)findViewById(R.id.et_sd_city_cityentered);
		this.cityNameEditText.setKeyListener(new TextKeyListener(Capitalize.WORDS, false)); // TODO Possible in XML !?!?!

		/* Make the Country-Grid be filled with all Countries available. */
		this.keyBoardGrid.setAdapter(new KeyLayoutAdapter(this, aKeyBoardLayout, this.mGridButtonListener));

		this.applyTopMenuButtonListeners();
		this.applyOkButtonListener();
		this.applyAutoCompleteListeners();
		this.applyKeyPadGridOnItemClickListener();

		if(super.mMenuVoiceEnabled) {
			MediaPlayer.create(this, R.raw.enter_a_cityname).start();
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch(resultCode){
			case SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS:
				this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS, data);
				this.finish();
				break;
			case SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED:
				this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED, data);
				this.finish();
				break;
		}
		/* Finally call the super()-method. */
		super.onActivityResult(requestCode, resultCode, data);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected void advanceToNextScreen() {
		if(this.cityNameEditText.getText().length() == 0){
			Toast.makeText(this, R.string.toast_sd_streetname_empty, Toast.LENGTH_SHORT).show();
		}else{
			final String cityName = SDCity.this.cityNameEditText.getText().toString();
			try {
				final Country nat = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
				DBManager.addCityName(this, cityName, nat.COUNTRYCODE);
			} catch (final DataBaseException e) {
				Log.e(DEBUGTAG, "Error on inserting CityName", e);
			}

			/* Then create an Intent to open the SubActivity. */
			final Intent sdStreetSearchIntent = new Intent(SDCity.this, SDStreet.class);
			/* Add the ZipCode to the Bundle to be passed further. */
			SDCity.this.bundleCreatedWith.putString(EXTRAS_CITYNAME_ID, SDCity.this.cityNameEditText.getText().toString());
			/* Pass the Bundle this Activity was created with further. */
			sdStreetSearchIntent.putExtras(SDCity.this.bundleCreatedWith);

			SDCity.this.startActivityForResult(sdStreetSearchIntent, REQUESTCODE_SD_STREET);
		}
	}

	protected void applyAutoCompleteListeners() {
		try {
			final Country nat = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
			final String countrycode = nat.COUNTRYCODE;
			final List<String> usedCityNames = DBManager.getCityNames(this, countrycode);

			new InlineAutoCompleterCombined(this.cityNameEditText, usedCityNames, false){
				@Override
				public boolean onEnter() {
					SDCity.this.advanceToNextScreen();
					return true;
				}

				@Override
				public ArrayList<String> onGetDynamic() {
					//					if(SDCity.super.getDataConnectionStrengh() == 0)
					//						return null;
					final String cityname = SDCity.this.cityNameEditText.getText().toString();

					if(cityname.length() < 7) {
						return null;
					}

					List<GeocodedAddress> addresses;
					try {
						addresses = LUSRequester.requestFreeformAddress(SDCity.this, Country.fromAbbreviation(countrycode), cityname);

						if(addresses == null) {
							return null;
						}

						final ArrayList<String> out = new ArrayList<String>();
						for (final GeocodedAddress a : addresses) {
							final String locality = a.getMunicipality();
							Log.d(Constants.DEBUGTAG, "Found locality: " + locality);
							if(locality != null) {
								out.add(locality);
							}
						}

						return out;
					} catch (final ORSException e) {
						runOnUiThread(new Runnable(){
							public void run() {
								Toast.makeText(SDCity.this, e.getErrors().get(0).toUserString(), Toast.LENGTH_SHORT).show();
							}
						});
						Log.e(DEBUGTAG, "Geocoding-Error", e);
						return null;
					} catch (final Exception e) {
						Log.e(DEBUGTAG, "Geocoding-Error", e);
						return null;
					}
				}
			};
		} catch (final DataBaseException e) {
			Log.e(DEBUGTAG, "Error on loading CityNames", e);
		}
	}

	protected void handleButtonClickByCaption(final String buttonCaption){
		final Editable curText = this.cityNameEditText.getText();
		if(buttonCaption.equals("" + AbstractKeyBoardLayout.BUTTONGRID_BACKCAPTION)){
			final Editable et = curText;
			final int len = et.length();
			if (len > 0) {
				curText.delete(len - 1, len);
			}
		}else{
			/* Append the Buttons caption it to the streetNameEditText. */
			final int selStart = this.cityNameEditText.getSelectionStart();
			final int selEnd = this.cityNameEditText.getSelectionEnd();
			if(selStart < selEnd) {
				curText.replace(selStart, selEnd, "");
			}

			/* If previous character is a letter, the next one should be lowercase. */
			if(curText.length() > 0 && Character.isLetter(curText.charAt(curText.length() - 1))) {
				curText.append(buttonCaption.toLowerCase());
			} else {
				curText.append(buttonCaption.toUpperCase());
			}
			this.cityNameEditText.invalidate();
		}
	}

	/** Applies a OnItemClickListener to the numberPadGrid
	 * which calls handleButtonClick(String caption).*/
	protected void applyKeyPadGridOnItemClickListener() {
		this.keyBoardGrid.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(final AdapterView<?> arg0, final View v, final int arg2, final long arg3) {
				/* Extract the Caption of the Button. */
				final String theCaption = ((Button)v).getText().toString();
				SDCity.this.handleButtonClickByCaption(theCaption);
			}
		});
	}

	protected void applyOkButtonListener() {
		/* Set OnClickListener for OK-Button. */
		findViewById(R.id.btn_sd_city_ok).setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				SDCity.this.advanceToNextScreen();
			}
		});
	}

	protected void applyTopMenuButtonListeners() {
		/* Set Listener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_city_back)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDCity.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDCity.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View v) {
				/* Back one level. */
				SDCity.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDCity.this.finish();
			}
		};

		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_city_close)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDCity.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDCity.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Set RsultCode that the calling
				 * activity knows that we want
				 * to go back to the Base-Menu */
				SDCity.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDCity.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	/** Apply OnItemClickListener to add the letter pressed to the EditText.
	 * This Listener will get called on actual CLICKS to the BUTTONS! */
	protected OnClickListener mGridButtonListener = new OnClickListener() {
		public void onClick(final View v) {
			/* Extract the Caption of the Button. */
			final String theCaption = ((Button)v).getText().toString();
			SDCity.this.handleButtonClickByCaption(theCaption);
		};
	};
}