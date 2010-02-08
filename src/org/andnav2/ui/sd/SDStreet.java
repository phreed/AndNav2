//Created by plusminus on 20:51:30 - 10.02.2008
package org.andnav2.ui.sd;

import java.util.ArrayList;
import java.util.List;

import org.andnav2.R;
import org.andnav2.adt.keyboardlayouts.AbstractKeyBoardLayout;
import org.andnav2.adt.keyboardlayouts.KeyBoardLayoutImpls;
import org.andnav2.db.DBManager;
import org.andnav2.db.DataBaseException;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.GeocodedAddress;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.ors.adt.lus.ICountrySubdivision;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.sys.ors.lus.LUSRequester;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.InlineAutoCompleterCombined;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.common.adapters.KeyLayoutAdapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.TextKeyListener;
import android.text.method.TextKeyListener.Capitalize;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SDStreet extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	/* REQUEST-CODES for SubActivities. */
	protected static final int REQUESTCODE_SD_STREETNUMBER = 0x1637;
	protected static final int REQUESTCODE_RESOLVER = REQUESTCODE_SD_STREETNUMBER + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	protected GridView keyBoardGrid;
	protected EditText streetNameEditText;
	protected Bundle bundleCreatedWith;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle, true); // We need DataState-Info
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.sd_street);

		/* Save the Extras Bundle of the Intent this Activity
		 * was created with, because it contains the Information,
		 * that will finally be used for a GeoCode API. */
		this.bundleCreatedWith = this.getIntent().getExtras();

		final AbstractKeyBoardLayout aKeyBoardLayout = KeyBoardLayoutImpls.getNumberedVersion(Preferences.getKeyboardLayout(this));
		this.keyBoardGrid = (GridView)findViewById(R.id.grid_searchstreet_keyboard);
		this.keyBoardGrid.setNumColumns(aKeyBoardLayout.getColumnsByDisplay(getWindowManager().getDefaultDisplay()));

		this.streetNameEditText = (EditText)findViewById(R.id.et_sd_street_streetentered);
		this.streetNameEditText.setKeyListener(new TextKeyListener(Capitalize.WORDS,false)); // TODO Possible in XML !?!?!

		/* Make the Country-Grid be filled with all Countries available. */
		this.keyBoardGrid.setAdapter(new KeyLayoutAdapter(this, aKeyBoardLayout, this.mGridButtonListener));

		if(super.mMenuVoiceEnabled) {
			MediaPlayer.create(this, R.raw.enter_a_streetname).start();
		}

		this.applyTopMenuButtonListeners();
		this.applyOkButtonListener();
		this.applyKeyPadGridOnItemClickListener();

		if(this.bundleCreatedWith.getInt(EXTRAS_MODE) == EXTRAS_MODE_STREETNAMESEARCH){
			this.findViewById(R.id.ibtn_sd_street_skip).setVisibility(View.INVISIBLE);
		}else{
			this.applyAutoCompleteListeners();
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

	protected void advanceToNextScreen(final boolean skipThis) {
		if(skipThis){
			/* Remove Info from the bundle that may be included in it. */
			this.bundleCreatedWith.remove(EXTRAS_STREETNUMBER_ID);
			this.bundleCreatedWith.remove(EXTRAS_STREET_ID);



			/* Then create an Intent to open the SubActivity. */
			final Intent resolveIntent = new Intent(this, SDResolver.class);

			/* Pass the Bundle this Activity was created with further. */
			resolveIntent.putExtras(this.bundleCreatedWith);

			startActivityForResult(resolveIntent, REQUESTCODE_RESOLVER);
		}else{
			if(this.streetNameEditText.getText().length() == 0){
				Toast.makeText(this, R.string.toast_sd_streetname_empty, Toast.LENGTH_SHORT).show();
				return;
			}

			final String streetName = SDStreet.this.streetNameEditText.getText().toString();
			/* Add the StreetName to the Bundle to be passed further. */
			this.bundleCreatedWith.putString(EXTRAS_STREET_ID, streetName);

			if(this.bundleCreatedWith.getInt(EXTRAS_MODE) != EXTRAS_MODE_STREETNAMESEARCH){
				try {
					final String zipCodeOrCityName = org.andnav2.ui.sd.Util.getZipCodeOrCityName(this.bundleCreatedWith);
					final Country nat = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
					DBManager.addStreet(this, streetName, zipCodeOrCityName, nat.COUNTRYCODE);
				} catch (final DataBaseException e) {
					Log.e(DEBUGTAG, "Error on inserting ZipCode", e);
				}
			}


			/* Then create an Intent to open the SubActivity. */
			final Intent streetNumberIntent = new Intent(this, SDStreetNumber.class);

			/* Pass the Bundle this Activity was created with further. */
			streetNumberIntent.putExtras(this.bundleCreatedWith);

			startActivityForResult(streetNumberIntent, REQUESTCODE_SD_STREETNUMBER);
		}
	}

	protected void applyAutoCompleteListeners() {
		try{
			final Country nat = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
			final ICountrySubdivision subdivision = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRYSUBDIVISIONCODE_ID);
			final String zipCodeOrCityName = org.andnav2.ui.sd.Util.getZipCodeOrCityName(this.bundleCreatedWith);

			final List<String> usedStreetNames = DBManager.getStreetNames(this, zipCodeOrCityName, nat.COUNTRYCODE);

			new InlineAutoCompleterCombined(this.streetNameEditText, usedStreetNames, false){
				@Override
				public boolean onEnter() {
					SDStreet.this.advanceToNextScreen(false);
					return true;
				}

				@Override
				public ArrayList<String> onGetDynamic() {
					//					if(SDStreet.super.getDataConnectionStrengh() == 0)
					//						return null;

					final String streetName =  SDStreet.this.streetNameEditText.getText().toString();
					if(streetName.length() < 100) {
						return null;
					}

					List<GeocodedAddress> addresses;
					try {
						switch (SDStreet.this.bundleCreatedWith.getInt(EXTRAS_MODE)) {
							case EXTRAS_MODE_ZIPSEARCH:
								final String zip = SDStreet.this.bundleCreatedWith.getString(EXTRAS_ZIPCODE_ID);
								addresses = LUSRequester.requestStreetaddressPostalcode(SDStreet.this, nat, subdivision, zip, streetName, null);
								break;
							case EXTRAS_MODE_CITYNAMESEARCH:
								final String city = SDStreet.this.bundleCreatedWith.getString(EXTRAS_CITYNAME_ID);
								addresses = LUSRequester.requestStreetaddressCity(SDStreet.this, nat, subdivision, city, streetName, null);
								break;
							default:
								throw new IllegalArgumentException();
						}

						if(addresses == null || addresses.size() == 0) {
							return null;
						}

						final ArrayList<String> out = new ArrayList<String>();

						for (final GeocodedAddress a : addresses) {
							final String street = a.getStreetNameOfficial();
							Log.d(DEBUGTAG, "Street found: " + street);
							if(street != null && street.length() > 0) {
								out.add(street);
							}
						}
						return out;
					} catch (final ORSException e) {
						runOnUiThread(new Runnable(){
							public void run() {
								Toast.makeText(SDStreet.this, e.getErrors().get(0).toUserString(), Toast.LENGTH_SHORT).show();
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
			Log.e(DEBUGTAG, "Error on loading ZipCodes", e);
		}
	}

	protected void handleButtonClickByCaption(final String buttonCaption){
		final Editable curText = this.streetNameEditText.getText();
		if(buttonCaption.equals("" + AbstractKeyBoardLayout.BUTTONGRID_BACKCAPTION)){
			final int len = curText.length();
			if (len > 0) {
				curText.delete(len - 1, len);
			}
		}else{
			/* Append the Buttons caption it to the streetNameEditText. */
			final int selStart = this.streetNameEditText.getSelectionStart();
			final int selEnd = this.streetNameEditText.getSelectionEnd();
			if(selStart < selEnd) {
				curText.replace(selStart, selEnd, "");
			}

			/* If previous character is a letter, the next one should be lowercase. */
			if(curText.length() > 0 && Character.isLetter(curText.charAt(curText.length() - 1))) {
				curText.append(buttonCaption.toLowerCase());
			} else {
				curText.append(buttonCaption.toUpperCase());
			}

			this.streetNameEditText.invalidate();
		}
	}

	/** Applies a OnItemClickListener to the numberPadGrid
	 * which calls handleButtonClick(String caption).*/
	protected void applyKeyPadGridOnItemClickListener() {
		this.keyBoardGrid.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(final AdapterView<?> arg0, final View v, final int arg2, final long arg3) {
				/* Extract the Caption of the Button. */
				final String theCaption = ((Button)v).getText().toString();
				SDStreet.this.handleButtonClickByCaption(theCaption);
			}
		});
	}

	protected void applyOkButtonListener() {
		/* Set Listener for OK-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.btn_sd_street_ok)){

			@Override
			public void onClicked(final View me) {
				SDStreet.this.advanceToNextScreen(false);
			}
		};
	}

	protected void applyTopMenuButtonListeners() {
		this.streetNameEditText.setOnKeyListener(new OnKeyListener(){
			public boolean onKey(final View arg0, final int arg1, final KeyEvent ke) {
				if(ke.getAction() == KeyEvent.ACTION_DOWN){
					if(ke.getKeyCode() == KeyEvent.KEYCODE_ENTER || ke.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER){
						if(SDStreet.super.mMenuVoiceEnabled) {
							MediaPlayer.create(SDStreet.this, R.raw.ok).start();
						}

						advanceToNextScreen(false);
						return true;
					}
				}
				return false;
			}
		});

		/* Set Listener for Skip-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_street_skip)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDStreet.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDStreet.this, R.raw.skip).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				SDStreet.this.advanceToNextScreen(true);
			}
		};

		/* Set Listener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_street_back)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDStreet.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDStreet.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Back one level. */
				SDStreet.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDStreet.this.finish();
			}
		};

		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_street_close)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDStreet.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDStreet.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Set RsultCode that the calling
				 * activity knows that we want
				 * to go back to the Base-Menu */
				SDStreet.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDStreet.this.finish();
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
			SDStreet.this.handleButtonClickByCaption(theCaption);
		};
	};
}
