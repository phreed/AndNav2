// Created by plusminus on 17:35:04 - 02.02.2008
package org.andnav2.ui.sd;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.ors.adt.lus.CountrySubdivisionRegistry;
import org.andnav2.sys.ors.adt.lus.CountrySubdivisionsUS;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.common.adapters.CountryAdapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SDCountry extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final String SDCOUNTRY_MODE_ID = "sdcountry_mode_id";
	public static final int SDCOUNTRY_MODE_PROCEED = 0;
	public static final int SDCOUNTRY_MODE_FINISH = SDCOUNTRY_MODE_PROCEED + 1;

	/* REQUEST-CODES for SubActivities. */
	protected static final int REQUESTCODE_SD_ZIPORCITY = 0x1437;
	protected static final int REQUESTCODE_SD_FAVORITES = REQUESTCODE_SD_ZIPORCITY + 1;
	protected static final int REQUESTCODE_SD_COUNTRYSUBDIVISION = REQUESTCODE_SD_FAVORITES + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Bundle bundleCreatedWith;

	protected Country mostRecentNation;

	protected ListView mCountryList;
	private CountryAdapter mCountryAdapter;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.sd_country);

		/*
		 * Save the Extras Bundle of the Intent this Activity was created with,
		 * because it contains the Information, that will finally be needed later.
		 */
		this.bundleCreatedWith = this.getIntent().getExtras();

		this.mCountryList = (ListView) findViewById(R.id.list_sd_country);

		this.mCountryAdapter = new CountryAdapter(this, Country.getAllWithDrivingDirections());
		this.mCountryList.setAdapter(mCountryAdapter);

		this.initMostRecentFlagView();

		this.applyGridViewListeners();
		this.applyTopMenuButtonListeners();

		if (super.mMenuVoiceEnabled) {
			MediaPlayer.create(this, R.raw.choose_a_country).start();
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
			case SUBACTIVITY_RESULTCODE_SUCCESS:
				this.setResult(SUBACTIVITY_RESULTCODE_SUCCESS, data);
				this.finish();
				break;
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

	protected void initMostRecentFlagView() {
		this.mostRecentNation = Preferences.getSDCountryMostRecentUsed(this);

		final ImageButton ibtn_mostrecent = (ImageButton) findViewById(R.id.iv_sd_country_mostrecent);
		if (this.mostRecentNation == null) {
			ibtn_mostrecent.setFocusable(false);
			ibtn_mostrecent.setVisibility(View.GONE);
		} else {
			ibtn_mostrecent.setImageResource(this.mostRecentNation.FLAGRESID);
		}

		ibtn_mostrecent.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				final Country n = SDCountry.this.mostRecentNation;
				if (n != null) {
					SDCountry.this.advanceToNextScreen(n);
				}
			}
		});
	}

	protected void applyTopMenuButtonListeners() {
		/* Set OnClickListener for BACK-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_country_back)) {

			@Override
			public void onBoth(final View me, final boolean focused) {
				if (focused && SDCountry.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDCountry.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/*
				 * Set RsultCode that the calling activity knows that we want to
				 * go back to the previous Screen
				 */
				SDCountry.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDCountry.this.finish();
			}
		};

		/* Set OnClickListener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_country_close)) {

			@Override
			public void onBoth(final View me, final boolean focused) {
				if (focused && SDCountry.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDCountry.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/*
				 * Set RsultCode that the calling activity knows that we want to
				 * go back to the Base-Menu
				 */
				SDCountry.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDCountry.this.finish();
			}
		};
	}

	/**
	 * Applies an OnItemSelectedListener for displaying the country-name and an
	 * OnItemClickListener for calling the next Activity.
	 */
	protected void applyGridViewListeners() {
		/* Apply OnItemClickListener to advance to the next screen(Zip-entry). */
		this.mCountryList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(final AdapterView<?> arg0, final View v, final int position, final long id) {
				if (v != null) {
					/* Extract the Country out of the FlagView. */
					final Country theCountry = SDCountry.this.mCountryAdapter.getItem(position);
					SDCountry.this.advanceToNextScreen(theCountry);
				}
			}
		});
	}

	private void advanceToNextScreen(final Country pNationality) {
		if (pNationality != null) {
			Preferences.saveSDCountryMostRecentUsed(this, pNationality);
		}

		this.bundleCreatedWith.putParcelable(EXTRAS_COUNTRY_ID, pNationality);

		final CountrySubdivisionsUS[] subdivisions = CountrySubdivisionRegistry.get(pNationality);

		switch(this.bundleCreatedWith.getInt(SDCOUNTRY_MODE_ID)){
			case SDCOUNTRY_MODE_FINISH:
				if(subdivisions == null){
					this.setResult(SUBACTIVITY_RESULTCODE_SUCCESS);
					this.finish();
				}else{
					final Intent intent = new Intent(SDCountry.this, SDCountrySubdivision.class);

					/* Add the Bundle to the Intent. */
					intent.putExtras(SDCountry.this.bundleCreatedWith);

					startActivityForResult(intent, REQUESTCODE_SD_COUNTRYSUBDIVISION);
				}
				break;
			case SDCOUNTRY_MODE_PROCEED:
				/* Then create an Intent to open the SubActivity. */
				final Intent intent;
				if(subdivisions == null){
					this.bundleCreatedWith.remove(EXTRAS_COUNTRYSUBDIVISIONCODE_ID);
					intent = new Intent(SDCountry.this, SDZipOrCity.class);
				}else{
					intent = new Intent(SDCountry.this, SDCountrySubdivision.class);
				}
				/* Add the Bundle to the Intent. */
				intent.putExtras(SDCountry.this.bundleCreatedWith);

				if(subdivisions == null){
					startActivityForResult(intent, REQUESTCODE_SD_ZIPORCITY);
				}else{
					startActivityForResult(intent, REQUESTCODE_SD_COUNTRYSUBDIVISION);
				}
				break;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
