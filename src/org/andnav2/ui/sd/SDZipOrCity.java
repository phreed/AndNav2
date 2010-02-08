// Created by plusminus on 23:47:09 - 02.02.2008
package org.andnav2.ui.sd;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.ors.adt.lus.ICountrySubdivision;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class SDZipOrCity extends AndNavBaseActivity {

	// ===========================================================
	// Final Fields
	// ===========================================================

	protected static final int REQUESTCODE_SD_ZIP = 0;
	protected static final int REQUESTCODE_SD_CITY = REQUESTCODE_SD_ZIP + 1;
	protected static final int REQUESTCODE_SD_UKBS7776_POSTCODE = REQUESTCODE_SD_CITY + 1;
	protected static final int REQUESTCODE_SD_STREET = REQUESTCODE_SD_UKBS7776_POSTCODE + 1;
	protected static final int REQUESTCODE_SD_COUNTRY = REQUESTCODE_SD_STREET + 1;
	// ===========================================================
	// Fields
	// ===========================================================

	protected Bundle bundleCreatedWith;
	private Country mostRecentCountry;
	private ICountrySubdivision mMostRecentCountrySubdivision;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		setContentView(R.layout.sd_ziporcity);

		/* Save the Extras Bundle of the Intent this Activity
		 * was created with, because it contains the Information,
		 * that will finally be used for the Yahoo GeoCode API. */
		this.bundleCreatedWith = this.getIntent().getExtras();

		final Country nat = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
		switch(nat){
			case UNITEDKINGDOM:
			case IRELAND:
				this.findViewById(R.id.ibtn_sd_ziporcity_select_uk_postcode_bs7776_search).setVisibility(View.VISIBLE);
				this.findViewById(R.id.tv_sd_ziporcity_select_uk_postcode_bs7776_search).setVisibility(View.VISIBLE);
				this.findViewById(R.id.tv_sd_ziporcity_select_zipsearch).setVisibility(View.GONE);
				break;
			default:
				break;
		}

		this.applyTopMenuButtonListeners();
		this.applyAdvanceButtonListeners();
		this.initMostRecentFlagView();

		if(super.mMenuVoiceEnabled){
			final MediaPlayer mp = MediaPlayer.create(this, R.raw.choose_zipcode_or_cityname);
			mp.setVolume(1, 1);
			mp.start();
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
		if(requestCode == REQUESTCODE_SD_COUNTRY) {

		}
		initMostRecentFlagView();

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

	private void initMostRecentFlagView() {
		this.mostRecentCountry = Preferences.getSDCountryMostRecentUsed(this);
		final int flagresid;
		this.mMostRecentCountrySubdivision = Preferences.getSDCountrySubdivisionMostRecentUsed(this, this.mostRecentCountry);
		flagresid = (this.mMostRecentCountrySubdivision != null) ? this.mMostRecentCountrySubdivision.getFlagResID() : this.mostRecentCountry.FLAGRESID;

		final ImageButton ibtn_mostrecent = (ImageButton) findViewById(R.id.iv_sd_ziporcity_mostrecentcountry);
		if (this.mostRecentCountry == null) {
			ibtn_mostrecent.setFocusable(false);
			ibtn_mostrecent.setVisibility(View.GONE);
		} else {
			ibtn_mostrecent.setImageResource(flagresid);
		}

		ibtn_mostrecent.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				SDZipOrCity.this.bundleCreatedWith.putParcelable(EXTRAS_COUNTRY_ID, SDZipOrCity.this.mostRecentCountry);
				final Intent countryIntent = new Intent(SDZipOrCity.this, SDCountry.class);

				SDZipOrCity.this.bundleCreatedWith.putInt(SDCountry.SDCOUNTRY_MODE_ID, SDCountry.SDCOUNTRY_MODE_FINISH);

				/* Add the Bundle to the Intent. */
				countryIntent.putExtras(SDZipOrCity.this.bundleCreatedWith);

				startActivityForResult(countryIntent, REQUESTCODE_SD_COUNTRY);
			}
		});
	}

	private void applyTopMenuButtonListeners() {
		/* Set Listener for Skip-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_ziporcity_skip)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDZipOrCity.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDZipOrCity.this, R.raw.skip).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Load SDZipcode-Activity. */
				final Intent sdStreetIntent = new Intent(SDZipOrCity.this, SDStreet.class);

				SDZipOrCity.this.bundleCreatedWith.putInt(EXTRAS_MODE, EXTRAS_MODE_STREETNAMESEARCH);

				/* Pass the Bundle this Activity was created with further. */
				sdStreetIntent.putExtras(SDZipOrCity.this.bundleCreatedWith);

				SDZipOrCity.this.startActivityForResult(sdStreetIntent, REQUESTCODE_SD_STREET);
			}
		};

		/* Set OnClickListener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_ziporcity_back)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDZipOrCity.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDZipOrCity.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Back one level. */
				SDZipOrCity.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDZipOrCity.this.finish();
			}
		};

		/* Set OnClickListener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_ziporcity_close)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDZipOrCity.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDZipOrCity.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Set RsultCode that the calling
				 * activity knows that we want
				 * to go back to the Base-Menu */
				SDZipOrCity.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDZipOrCity.this.finish();
			}
		};
	}

	private void applyAdvanceButtonListeners() {
		/* Set OnClickListener for ZipCodeSearch-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_ziporcity_select_zipsearch)){
			@Override
			public void onClicked(final View me) {
				/* Load SDZipcode-Activity. */
				final Intent sdZipSearchIntent = new Intent(SDZipOrCity.this, SDZip.class);

				SDZipOrCity.this.bundleCreatedWith.putInt(EXTRAS_MODE, EXTRAS_MODE_ZIPSEARCH);

				/* Pass the Bundle this Activity was created with further. */
				sdZipSearchIntent.putExtras(SDZipOrCity.this.bundleCreatedWith);

				SDZipOrCity.this.startActivityForResult(sdZipSearchIntent, REQUESTCODE_SD_ZIP);
			}
		};

		/* Set OnClickListener for CitySearch-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_ziporcity_select_citysearch)){
			@Override
			public void onClicked(final View me) {
				/* Load SDCity-Activity. */
				final Intent sdCitySearchIntent = new Intent(SDZipOrCity.this, SDCity.class);

				SDZipOrCity.this.bundleCreatedWith.putInt(EXTRAS_MODE, EXTRAS_MODE_CITYNAMESEARCH);

				/* Pass the Bundle this Activity was created with further. */
				sdCitySearchIntent.putExtras(SDZipOrCity.this.bundleCreatedWith);

				SDZipOrCity.this.startActivityForResult(sdCitySearchIntent, REQUESTCODE_SD_CITY);
			}
		};

		/* Set OnClickListener for UK BS 7776 PostcodeSearch-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_ziporcity_select_uk_postcode_bs7776_search)){
			@Override
			public void onClicked(final View me) {
				/* Load SDZipcode-Activity. */
				final Intent sdUKBS7776PostcodeSearchIntent = new Intent(SDZipOrCity.this, SDPostcodeUK_BS7666.class);

				SDZipOrCity.this.bundleCreatedWith.putInt(EXTRAS_MODE, EXTRAS_MODE_ZIPSEARCH);

				/* Pass the Bundle this Activity was created with further. */
				sdUKBS7776PostcodeSearchIntent.putExtras(SDZipOrCity.this.bundleCreatedWith);

				SDZipOrCity.this.startActivityForResult(sdUKBS7776PostcodeSearchIntent, REQUESTCODE_SD_UKBS7776_POSTCODE);
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
