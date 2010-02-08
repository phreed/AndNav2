// Created by plusminus on 21:30:10 - 15.05.2008
package org.andnav2.ui.sd;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.andnav2.R;
import org.andnav2.adt.keyboardlayouts.AbstractKeyBoardLayout;
import org.andnav2.adt.keyboardlayouts.KeyBoardLayoutImpls;
import org.andnav2.db.DBManager;
import org.andnav2.db.DataBaseException;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.GeocodedAddress;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.postcode.uk_bs_7666.PostcodeUK_BS7776Matcher;
import org.andnav2.sys.postcode.uk_bs_7666.Requester;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.InlineAutoCompleterCombined;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.common.adapters.KeyLayoutAdapter;
import org.andnav2.ui.map.OpenStreetDDMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SDPostcodeUK_BS7666 extends AndNavBaseActivity {

	// ===========================================================
	// Final Fields
	// ===========================================================

	/* REQUEST-CODES for SubActivities. */
	protected static final int REQUESTCODE_FETCHROUTE = 0x1537;

	// ===========================================================
	// Fields
	// ===========================================================

	protected GridView keyBoardGrid;
	protected EditText postcodeEditText_1;
	protected EditText postcodeEditText_2;
	protected Bundle bundleCreatedWith;
	protected String acItem;

	protected EditText lastFocusedPostcodeEditText;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle, true); // We need DataState-Info
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.sd_postcode_uk_bs_7666);

		/*
		 * Save the Extras Bundle of the Intent this Activity was created with,
		 * because it contains the Information, that will finally be used for a
		 * GeoCode API.
		 */
		this.bundleCreatedWith = this.getIntent().getExtras();

		final AbstractKeyBoardLayout aKeyBoardLayout = KeyBoardLayoutImpls.getNumberedVersion(Preferences
				.getKeyboardLayout(this));
		this.keyBoardGrid = (GridView) findViewById(R.id.grid_sd_postcode_uk_bs_7666_keyboard);
		this.keyBoardGrid.setNumColumns(aKeyBoardLayout.getColumnsByDisplay(getWindowManager()
				.getDefaultDisplay()));

		this.postcodeEditText_1 = (EditText) findViewById(R.id.et_sd_postcode_uk_bs_7666_codeentered_1);
		this.postcodeEditText_2 = (EditText) findViewById(R.id.et_sd_postcode_uk_bs_7666_codeentered_2);
		this.lastFocusedPostcodeEditText = this.postcodeEditText_1;
		final OnFocusChangeListener lis = new OnFocusChangeListener() {
			public void onFocusChange(final View v, final boolean hasFocus) {
				SDPostcodeUK_BS7666.this.lastFocusedPostcodeEditText = (EditText) v;
			}
		};
		this.postcodeEditText_1.setOnFocusChangeListener(lis);
		this.postcodeEditText_2.setOnFocusChangeListener(lis);

		/* Make the Country-Grid be filled with all Countries available. */
		this.keyBoardGrid.setAdapter(new KeyLayoutAdapter(this, aKeyBoardLayout,
				this.mGridButtonListener));

		this.applyTopMenuButtonListeners();
		this.applyOkButtonListener();
		this.applyAutoCompleteListeners();
		this.applyKeyPadGridOnItemClickListener();

		if (super.mMenuVoiceEnabled) {
			MediaPlayer.create(this, R.raw.enter_a_zipcode).start();
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
		switch (resultCode) {
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

	private void checkFormat(final boolean force) {
		String postCode = SDPostcodeUK_BS7666.this.postcodeEditText_1.getText().toString()
		.toUpperCase()
		+ " "
		+ SDPostcodeUK_BS7666.this.postcodeEditText_2.getText().toString().toUpperCase().trim();

		if (postCode.length() == 0) {
			Toast.makeText(this, R.string.toast_sd_postcode_bs_7666_empty, Toast.LENGTH_SHORT)
			.show();
			return;
		}

		if (!force) {
			if (!PostcodeUK_BS7776Matcher.doesMatchUKPostcode_BS_7666(postCode)) {
				// Toast.makeText(this,
				// R.string.toast_sd_postcode_bs_7666_no_match_found,
				// Toast.LENGTH_SHORT).show();
				// return;
			} else {
				final String matchedPostcode = PostcodeUK_BS7776Matcher
				.getMatchUKPostcode_BS_7666(postCode);
				if (postCode.compareToIgnoreCase(matchedPostcode) == 0) {
					postCode = matchedPostcode;
				} else {
					new AlertDialog.Builder(this).setTitle(
							R.string.sd_postcode_bs_7666_bad_format_title).setMessage(
									String.format(
											getString(R.string.sd_postcode_bs_7666_bad_format_message),
											postCode, matchedPostcode)).setPositiveButton(
													R.string.sd_postcode_bs_7666_correct,
													new DialogInterface.OnClickListener() {
														public void onClick(final DialogInterface d, final int which) {
															d.dismiss();
															final String[] parts = matchedPostcode.split(" ");

															SDPostcodeUK_BS7666.this.postcodeEditText_1.setText(parts[0]);
															SDPostcodeUK_BS7666.this.postcodeEditText_1
															.setText(parts.length > 1 ? parts[1] : "");
															checkFormat(true);
														}
													}).setNeutralButton(R.string.proceed,
															new DialogInterface.OnClickListener() {

														public void onClick(final DialogInterface d, final int which) {
															d.dismiss();
															checkFormat(true);
														}
													}).setNegativeButton(R.string.cancel,
															new DialogInterface.OnClickListener() {

														public void onClick(final DialogInterface d, final int which) {
															d.dismiss();
														}
													}).show();
					return;
				}
			}
		}

		final String useItPostCode = postCode.toUpperCase();

		final ProgressDialog pd = ProgressDialog.show(this, "Resolving postcode",
				getString(R.string.please_wait_a_moment)); // TODO i18n
		new Thread(new Runnable() {

			public void run() {
				try {
					final GeocodedAddress g = new Requester().request(useItPostCode);

					runOnUiThread(new Runnable() {

						public void run() {
							if (g == null) {
								Toast.makeText(SDPostcodeUK_BS7666.this,
										"Postcode could not be resolved!", Toast.LENGTH_LONG)
										.show();
							} else if (useItPostCode.compareToIgnoreCase(g.getPostalCode()) != 0) {
								new AlertDialog.Builder(SDPostcodeUK_BS7666.this)
								.setTitle(
										R.string.sd_postcode_bs_7666_closest_databasematch_title)
										.setMessage(
												String
												.format(
														getString(R.string.sd_postcode_bs_7666_closest_databasematch_message),
														useItPostCode, g.getPostalCode()))
														.setPositiveButton(
																R.string.sd_postcode_bs_7666_use_closes_match,
																new DialogInterface.OnClickListener() {

																	public void onClick(final DialogInterface d, final int which) {
																		d.dismiss();
																		advanceToNextScreen(g);
																	}
																}).setNegativeButton(R.string.cancel,
																		new DialogInterface.OnClickListener() {

																	public void onClick(final DialogInterface d, final int which) {
																		d.dismiss();
																	}
																}).show();
								return;

							} else {
								/* Full Match! */
								advanceToNextScreen(g);
							}
						}
					});

				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(SDPostcodeUK_BS7666.this,
									"Postcode not in database :( ", Toast.LENGTH_LONG).show();
						}
					});
				} finally {
					pd.dismiss();
				}
			}
		}).start();
	}

	protected void advanceToNextScreen(final GeocodedAddress g) {
		Assert.assertNotNull(g);

		try {
			final Country nat = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
			DBManager.addZipCode(SDPostcodeUK_BS7666.this, g.getPostalCode(), nat.COUNTRYCODE);
		} catch (final DataBaseException e) {
			// Log.e(DEBUGTAG, "Error on inserting Postcode", e);
		}

		final int mode = SDPostcodeUK_BS7666.this.bundleCreatedWith.getInt(MODE_SD);
		switch (mode) {
			case MODE_SD_DESTINATION:
				/* Add the StreetNumber to the Bundle to be passed further. */
				SDPostcodeUK_BS7666.this.bundleCreatedWith.putInt(EXTRAS_MODE,
						EXTRAS_MODE_DIRECT_LATLNG);

				SDPostcodeUK_BS7666.this.bundleCreatedWith.putInt(EXTRAS_DESTINATION_LATITUDE_ID, g
						.getLatitudeE6());
				SDPostcodeUK_BS7666.this.bundleCreatedWith.putInt(EXTRAS_DESTINATION_LONGITUDE_ID,
						g.getLongitudeE6());

				/* Create an Intent to open the Map as a SubActivity. */
				final Intent fetchRouteIntent = new Intent(SDPostcodeUK_BS7666.this,
						OpenStreetDDMap.class);

				/* Pass the Bundle this Activity was created with further. */
				fetchRouteIntent.putExtras(SDPostcodeUK_BS7666.this.bundleCreatedWith);

				startActivityForResult(fetchRouteIntent, REQUESTCODE_FETCHROUTE);
				break;
			case MODE_SD_WAYPOINT:
			case MODE_SD_SETHOME:
			case MODE_SD_RESOLVE:

				final Intent resultData = new Intent();
				/* Add the StreetNumber to the Bundle to be passed further. */
				SDPostcodeUK_BS7666.this.bundleCreatedWith.putInt(EXTRAS_MODE,
						EXTRAS_MODE_DIRECT_LATLNG);

				SDPostcodeUK_BS7666.this.bundleCreatedWith.putInt(EXTRAS_DESTINATION_LATITUDE_ID, g
						.getLatitudeE6());
				SDPostcodeUK_BS7666.this.bundleCreatedWith.putInt(EXTRAS_DESTINATION_LONGITUDE_ID,
						g.getLongitudeE6());

				resultData.putExtras(SDPostcodeUK_BS7666.this.bundleCreatedWith);

				SDPostcodeUK_BS7666.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS,
						resultData);
				SDPostcodeUK_BS7666.this.finish();
				break;
		}
	}

	protected void applyAutoCompleteListeners() {
		try {
			final Country nat = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
			final List<String> usedCityNames = DBManager.getCityNames(this, nat.COUNTRYCODE);

			new InlineAutoCompleterCombined(this.postcodeEditText_1, usedCityNames, false) {

				@Override
				public boolean onEnter() {
					SDPostcodeUK_BS7666.this.checkFormat(false);
					return true;
				}

				@Override
				public ArrayList<String> onGetDynamic() {
					return doAutocomplete(0);
				}
			};

			new InlineAutoCompleterCombined(this.postcodeEditText_2, usedCityNames, false) {

				@Override
				public boolean onEnter() {
					SDPostcodeUK_BS7666.this.checkFormat(false);
					return true;
				}

				@Override
				public ArrayList<String> onGetDynamic() {
					return doAutocomplete(1);
				}
			};
		} catch (final DataBaseException e) {
			// Log.e(DEBUGTAG, "Error on loading CityNames", e);
		}
	}

	private ArrayList<String> doAutocomplete(final int pos) {
		final String postcode = SDPostcodeUK_BS7666.this.postcodeEditText_1.getText().toString() + " " + SDPostcodeUK_BS7666.this.postcodeEditText_2.getText().toString();

		try {
			final GeocodedAddress g = new Requester().request(postcode);

			final ArrayList<String> out = new ArrayList<String>();

			final String[] parts = g.getPostalCode().split(" ");
			if(parts.length > pos && parts[pos] != null){
				out.add(parts[pos]);
			}

			return out;
		} catch (final Exception e) {
			// Log.e(DEBUGTAG, "Geocoding-Error", e);
			return null;
		}
	}

	protected void handleButtonClickByCaption(final String buttonCaption) {
		final EditText editText = this.lastFocusedPostcodeEditText;
		if (buttonCaption.equals("" + AbstractKeyBoardLayout.BUTTONGRID_BACKCAPTION)) {
			final Editable et = editText.getText();
			final int len = et.length();
			if (len > 0) {
				editText.getText().delete(len - 1, len);
			}
		} else {
			/* Append the Buttons caption it to the streetNameEditText. */
			final int selStart = editText.getSelectionStart();
			final int selEnd = editText.getSelectionEnd();
			if (selStart < selEnd) {
				editText.getText().replace(selStart, selEnd, "");
			}
			editText.getText().append(buttonCaption);
			editText.invalidate();
		}
	}

	/**
	 * Applies a OnItemClickListener to the numberPadGrid which calls
	 * handleButtonClick(String caption).
	 */
	protected void applyKeyPadGridOnItemClickListener() {
		this.keyBoardGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> arg0, final View v, final int arg2, final long arg3) {
				/* Extract the Caption of the Button. */
				final String theCaption = ((Button) v).getText().toString();
				SDPostcodeUK_BS7666.this.handleButtonClickByCaption(theCaption);
			}
		});
	}

	protected void applyOkButtonListener() {
		/* Set OnClickListener for OK-Button. */
		findViewById(R.id.btn_sd_postcode_uk_bs_7666_ok).setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				SDPostcodeUK_BS7666.this.checkFormat(false);
			}
		});
	}

	protected void applyTopMenuButtonListeners() {
		/* Set Listener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this
				.findViewById(R.id.ibtn_sd_postcode_uk_bs_7666_back)) {

			@Override
			public void onBoth(final View me, final boolean focused) {
				if (focused && SDPostcodeUK_BS7666.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDPostcodeUK_BS7666.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View v) {
				/* Back one level. */
				SDPostcodeUK_BS7666.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDPostcodeUK_BS7666.this.finish();
			}
		};

		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this
				.findViewById(R.id.ibtn_sd_postcode_uk_bs_7666_close)) {

			@Override
			public void onBoth(final View me, final boolean focused) {
				if (focused && SDPostcodeUK_BS7666.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDPostcodeUK_BS7666.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/*
				 * Set RsultCode that the calling activity knows that we want to
				 * go back to the Base-Menu
				 */
				SDPostcodeUK_BS7666.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDPostcodeUK_BS7666.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	/**
	 * Apply OnItemClickListener to add the letter pressed to the EditText. This
	 * Listener will get called on actual CLICKS to the BUTTONS!
	 */
	protected OnClickListener mGridButtonListener = new OnClickListener() {
		public void onClick(final View v) {
			/* Extract the Caption of the Button. */
			final String theCaption = ((Button) v).getText().toString();
			SDPostcodeUK_BS7666.this.handleButtonClickByCaption(theCaption);
		};
	};
}