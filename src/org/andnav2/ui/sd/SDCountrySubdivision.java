// Created by plusminus on 17:35:04 - 02.02.2008
package org.andnav2.ui.sd;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.ors.adt.lus.CountrySubdivisionRegistry;
import org.andnav2.sys.ors.adt.lus.CountrySubdivisionsUS;
import org.andnav2.sys.ors.adt.lus.ICountrySubdivision;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.common.adapters.CountrySubdivisionAdapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SDCountrySubdivision extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	/* REQUEST-CODES for SubActivities. */
	protected static final int REQUESTCODE_SD_ZIPORCITY = 0x1437;
	protected static final int REQUESTCODE_SD_FAVOURITES = REQUESTCODE_SD_ZIPORCITY + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Bundle bundleCreatedWith;

	protected ICountrySubdivision mostRecentSubdivision;

	protected ListView countrySubdivisionList;
	private Country mNationality;
	private CountrySubdivisionAdapter mCountrySubdivisionAdapter;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.sd_countrysubdivision);

		/*
		 * Save the Extras Bundle of the Intent this Activity was created with,
		 * because it contains the Information, that will finally be needed later.
		 */
		this.bundleCreatedWith = this.getIntent().getExtras();

		this.countrySubdivisionList = (ListView) findViewById(R.id.list_sd_countrysubdivision);


		this.mNationality = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
		final CountrySubdivisionsUS[] subdivisions = CountrySubdivisionRegistry.get(this.mNationality);

		this.mCountrySubdivisionAdapter = new CountrySubdivisionAdapter(this, subdivisions);
		this.countrySubdivisionList.setAdapter(this.mCountrySubdivisionAdapter);

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
		this.mostRecentSubdivision = Preferences.getSDCountrySubdivisionMostRecentUsed(this, this.mNationality);

		final ImageButton ibtn_mostrecent = (ImageButton) findViewById(R.id.iv_sd_countrysubdivision_mostrecent);
		if (this.mostRecentSubdivision == null) {
			ibtn_mostrecent.setFocusable(false);
			ibtn_mostrecent.setVisibility(View.GONE);
		} else {
			ibtn_mostrecent.setImageResource(this.mostRecentSubdivision.getFlagResID());
		}

		ibtn_mostrecent.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				final ICountrySubdivision n = SDCountrySubdivision.this.mostRecentSubdivision;
				if (n != null) {
					SDCountrySubdivision.this.advanceToNextScreen(n);
				}
			}
		});
	}

	protected void applyTopMenuButtonListeners() {
		/* Set OnClickListener for BACK-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_countrysubdivision_back)) {

			@Override
			public void onBoth(final View me, final boolean focused) {
				if (focused && SDCountrySubdivision.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDCountrySubdivision.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/*
				 * Set RsultCode that the calling activity knows that we want to
				 * go back to the previous Screen
				 */
				SDCountrySubdivision.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDCountrySubdivision.this.finish();
			}
		};

		/* Set OnClickListener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_countrysubdivision_close)) {

			@Override
			public void onBoth(final View me, final boolean focused) {
				if (focused && SDCountrySubdivision.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDCountrySubdivision.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/*
				 * Set RsultCode that the calling activity knows that we want to
				 * go back to the Base-Menu
				 */
				SDCountrySubdivision.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDCountrySubdivision.this.finish();
			}
		};
	}

	/**
	 * Applies an OnItemSelectedListener for displaying the country-name and an
	 * OnItemClickListener for calling the next Activity.
	 */
	protected void applyGridViewListeners() {
		/* Apply OnItemClickListener to advance to the next screen(Zip-entry). */
		this.countrySubdivisionList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> arg0, final View v, final int position, final long id) {
				if (v != null) {
					/* Extract the Country out of the FlagView. */
					final ICountrySubdivision theCountry = SDCountrySubdivision.this.mCountrySubdivisionAdapter.getItem(position);
					SDCountrySubdivision.this.advanceToNextScreen(theCountry);
				}
			}
		});
	}

	private void advanceToNextScreen(final ICountrySubdivision pSubdivision) {
		if (pSubdivision != null) {
			Preferences.saveSDCountrySubdivisionMostRecentUsed(this, this.mNationality, pSubdivision);
		}

		this.bundleCreatedWith.putParcelable(EXTRAS_COUNTRYSUBDIVISIONCODE_ID, pSubdivision);

		switch(this.bundleCreatedWith.getInt(SDCountry.SDCOUNTRY_MODE_ID)){
			case SDCountry.SDCOUNTRY_MODE_FINISH:
				this.setResult(SUBACTIVITY_RESULTCODE_SUCCESS);
				this.finish();
				break;
			case SDCountry.SDCOUNTRY_MODE_PROCEED:
				/* Then create an Intent to open the SubActivity. */
				final Intent zipOrCityIntent = new Intent(SDCountrySubdivision.this, SDZipOrCity.class);

				/* Add the Bundle to the Intent. */
				zipOrCityIntent.putExtras(SDCountrySubdivision.this.bundleCreatedWith);
				startActivityForResult(zipOrCityIntent, REQUESTCODE_SD_ZIPORCITY);
				break;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
