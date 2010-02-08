// Created by plusminus on 11:26:05 - 25.10.2008
package org.andnav2.ui.sd;

import org.andnav2.R;
import org.andnav2.db.DBManager;
import org.andnav2.db.DataBaseException;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.ors.adt.lus.CountrySubdivisionRegistry;
import org.andnav2.sys.ors.adt.lus.ICountrySubdivision;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.CommonCallback;
import org.andnav2.ui.common.CommonCallbackAdapter;
import org.andnav2.ui.common.CommonDialogFactory;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.map.OpenStreetDDMap;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;


public class SDMainChoose extends AndNavBaseActivity {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int MENU_DIRECT_LAT_LON = Menu.FIRST;

	private static final int REQUESTCODE_SD_SEARCH_ADDRESS = 0;
	private static final int REQUESTCODE_SD_FAVOURITES = REQUESTCODE_SD_SEARCH_ADDRESS + 1;
	private static final int REQUESTCODE_SD_SEARCH_POI = REQUESTCODE_SD_FAVOURITES + 1;
	private static final int REQUESTCODE_SD_CONTACT = REQUESTCODE_SD_SEARCH_POI + 1;
	private static final int REQUESTCODE_SD_SAVEDROUTES = REQUESTCODE_SD_CONTACT + 1;
	private static final int REQUESTCODE_TRACES = REQUESTCODE_SD_SAVEDROUTES + 1;
	private static final int REQUESTCODE_DDMAP = REQUESTCODE_TRACES + 1;

	private static final int DIALOG_SHOW_LAT_LON = 0;
	private static final int DIALOG_SELECT_FREEFORM_OR_CATEGPRIZED_POISEARCH = DIALOG_SHOW_LAT_LON + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	private Bundle bundleCreatedWith;

	private TabHost myTabHost;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preferences.applySharedSettings(this);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.setContentView(R.layout.sd_mainchoose);

		this.myTabHost = (TabHost)this.findViewById(R.id.th_settings_menu_tabhost);
		this.setupTabs();

		this.bundleCreatedWith = this.getIntent().getExtras();

		this.applyButtonListeners();
	}

	private void setupTabs() {
		this.myTabHost.setup();

		final TabSpec tsOnline = this.myTabHost.newTabSpec("ONLINE");
		tsOnline.setIndicator("", getResources().getDrawable(R.drawable.online));
		tsOnline.setContent(R.id.grid_sd_mainchoose_page_online);
		this.myTabHost.addTab(tsOnline);

		final TabSpec tsDevice = this.myTabHost.newTabSpec("OFFLINE");
		tsDevice.setIndicator("", getResources().getDrawable(R.drawable.offline));
		tsDevice.setContent(R.id.grid_sd_mainchoose_page_offline);
		this.myTabHost.addTab(tsDevice);

		this.myTabHost.setCurrentTab(0);
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
			case DIALOG_SHOW_LAT_LON:
				return CommonDialogFactory.createInputLatLonDialog(this, new CommonCallback<GeoPoint>(){
					public void onSuccess(final GeoPoint result) {
						navToLatLon(result.getLatitudeE6(), result.getLongitudeE6());
					}

					public void onFailure(final Throwable t) {
						Toast.makeText(SDMainChoose.this, R.string.dlg_input_direct_lat_lon_malformed, Toast.LENGTH_SHORT).show();
					}
				});
			case DIALOG_SELECT_FREEFORM_OR_CATEGPRIZED_POISEARCH:
				return CommonDialogFactory.createFreeformOrCategorizedPOISelectorDialog(SDMainChoose.this, new CommonCallbackAdapter<Intent>(){
					@Override
					public void onSuccess(final Intent result) {
						result.putExtras(SDMainChoose.this.bundleCreatedWith);
						SDMainChoose.this.startActivityForResult(result, REQUESTCODE_SD_SEARCH_POI);
					}
				});
			default:
				return null;
		}
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch(item.getItemId()){
			case MENU_DIRECT_LAT_LON:
				showDialog(DIALOG_SHOW_LAT_LON);
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}


	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, MENU_DIRECT_LAT_LON, Menu.NONE, R.string.menu_sd_mainchoose_lat_lon).setIcon(R.drawable.world);

		return super.onCreateOptionsMenu(menu);
	}


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

	protected void navToLatLon(final int latE6, final int lonE6) {
		final int mode = SDMainChoose.this.bundleCreatedWith.getInt(MODE_SD);
		switch(mode){
			case MODE_SD_DESTINATION:
				final Intent directIntent = new Intent(SDMainChoose.this, OpenStreetDDMap.class);
				final Bundle b = new Bundle();
				b.putInt(EXTRAS_MODE, EXTRAS_MODE_DIRECT_LATLNG);
				b.putInt(EXTRAS_DESTINATION_LATITUDE_ID,latE6);
				b.putInt(EXTRAS_DESTINATION_LONGITUDE_ID, lonE6);
				directIntent.putExtras(b);
				SDMainChoose.this.startActivityForResult(directIntent, REQUESTCODE_DDMAP);
				break;
			case MODE_SD_SETHOME:
			case MODE_SD_WAYPOINT:
			case MODE_SD_RESOLVE:
				final Intent resultData = new Intent();

				SDMainChoose.this.bundleCreatedWith.putInt(EXTRAS_MODE, EXTRAS_MODE_DIRECT_LATLNG);
				SDMainChoose.this.bundleCreatedWith.putInt(EXTRAS_DESTINATION_LATITUDE_ID, latE6);
				SDMainChoose.this.bundleCreatedWith.putInt(EXTRAS_DESTINATION_LONGITUDE_ID, lonE6);

				resultData.putExtras(SDMainChoose.this.bundleCreatedWith);
				SDMainChoose.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS, resultData);
				SDMainChoose.this.finish();
				break;
		}
	}

	protected void applyButtonListeners() {
		int favCount;
		try {
			favCount = DBManager.getFavoritesCount(this);
		} catch (final DataBaseException e) {
			favCount = 0;
			//			Log.e(DEBUGTAG, "Error getting Favorite-Count", e);
		}
		/* Only display and set listeners to Fav-Button, when there is at least one favorite. */
		if (favCount <= 0) {
			this.findViewById(R.id.ibtn_sd_mainchoose_favorites).setEnabled(false);
		}else{
			/* Set OnClickListener for Favorites-Button. */
			new OnClickOnFocusChangedListenerAdapter(this
					.findViewById(R.id.ibtn_sd_mainchoose_favorites)) {

				@Override
				public void onBoth(final View me, final boolean focused) {
					if (focused && SDMainChoose.super.mMenuVoiceEnabled) {
						MediaPlayer.create(SDMainChoose.this, R.raw.select_a_favourite).start();
					}
				}

				@Override
				public void onClicked(final View me) {
					final Intent favIntent = new Intent(SDMainChoose.this, SDFavorites.class);
					SDMainChoose.this.bundleCreatedWith.putBoolean(SDFavorites.EXTRAS_FAVORITES_REFER, true);
					favIntent.putExtras(SDMainChoose.this.bundleCreatedWith);
					SDMainChoose.this.startActivityForResult(favIntent, REQUESTCODE_SD_FAVOURITES);
				}
			};
		}

		{ /* Offline-Page. */
			final boolean externalMediaMounted = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

			final View loadSavedButton = this.findViewById(R.id.ibtn_sd_mainchoose_load_saved);
			/* Loading only for Destinatio-Search. */
			loadSavedButton.setEnabled(externalMediaMounted && this.bundleCreatedWith.getInt(MODE_SD) == MODE_SD_DESTINATION);

			/* Set OnClickListener for Contacts-Button. */
			new OnClickOnFocusChangedListenerAdapter(loadSavedButton) {
				@Override
				public void onClicked(final View me) {
					final Intent iSavedRouteChooser = new Intent(SDMainChoose.this, SDSavedRouteChooser.class);
					/* Add the Bundle to the Intent. */
					iSavedRouteChooser.putExtras(SDMainChoose.this.bundleCreatedWith);
					startActivityForResult(iSavedRouteChooser, REQUESTCODE_SD_SAVEDROUTES);
				}
			};

			final View loadTraceButton = this.findViewById(R.id.ibtn_sd_mainchoose_load_trace);
			/* Loading only for Destinatio-Search. */
			loadTraceButton.setEnabled(false && externalMediaMounted && this.bundleCreatedWith.getInt(MODE_SD) == MODE_SD_DESTINATION); // TODO Remove false.

			/* Set OnClickListener for Contacts-Button. */
			new OnClickOnFocusChangedListenerAdapter(loadTraceButton) {
				@Override
				public void onClicked(final View me) {
					final Intent iSavedTraceChooser = new Intent(SDMainChoose.this, SDSavedTraceChooser.class);
					/* Add the Bundle to the Intent. */
					iSavedTraceChooser.putExtras(SDMainChoose.this.bundleCreatedWith);
					startActivityForResult(iSavedTraceChooser, REQUESTCODE_TRACES);
				}
			};
		}

		/* Set OnClickListener for Contacts-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_mainchoose_contacts)) {

			@Override
			public void onBoth(final View me, final boolean focused) {
				if (focused && SDMainChoose.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDMainChoose.this, R.raw.select_from_contacts).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent iContacts = new Intent(SDMainChoose.this, SDContacts.class);
				/* Add the Bundle to the Intent. */
				iContacts.putExtras(SDMainChoose.this.bundleCreatedWith);
				startActivityForResult(iContacts, REQUESTCODE_SD_CONTACT);
			}
		};


		/* Set OnClickListener for Address-Search-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_mainchoose_address)) {
			@Override
			public void onClicked(final View me) {
				final Country nat = Preferences.getSDCountryMostRecentUsed(SDMainChoose.this);

				final boolean hasSubDivision = CountrySubdivisionRegistry.get(nat) != null;
				final ICountrySubdivision subdiv = Preferences.getSDCountrySubdivisionMostRecentUsed(SDMainChoose.this, nat);

				if(nat == null || (hasSubDivision && subdiv == null)){
					final Intent addressSearchCountryIntent = new Intent(SDMainChoose.this, SDCountry.class);
					SDMainChoose.this.bundleCreatedWith.putInt(SDCountry.SDCOUNTRY_MODE_ID, SDCountry.SDCOUNTRY_MODE_PROCEED);
					addressSearchCountryIntent.putExtras(SDMainChoose.this.bundleCreatedWith);
					SDMainChoose.this.startActivityForResult(addressSearchCountryIntent, REQUESTCODE_SD_SEARCH_ADDRESS);
				}else{
					SDMainChoose.this.bundleCreatedWith.putParcelable(EXTRAS_COUNTRY_ID, nat);
					if(subdiv != null) {
						SDMainChoose.this.bundleCreatedWith.putParcelable(EXTRAS_COUNTRYSUBDIVISIONCODE_ID, subdiv);
					}
					final Intent addressSearchZipOrCityIntent = new Intent(SDMainChoose.this, SDZipOrCity.class);
					addressSearchZipOrCityIntent.putExtras(SDMainChoose.this.bundleCreatedWith);
					SDMainChoose.this.startActivityForResult(addressSearchZipOrCityIntent, REQUESTCODE_SD_SEARCH_ADDRESS);
				}
			}
		};

		/* Set OnClickListener for POI-Search-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_mainchoose_poi)) {

			@Override
			public void onBoth(final View me, final boolean focused) {
				//				if (SDMainChoose.super.mMenuVoiceEnabled)
				//					MediaPlayer.create(SDMainChoose.this, R.raw.search_destination).start();
			}

			@Override
			public void onClicked(final View me) {
				showDialog(DIALOG_SELECT_FREEFORM_OR_CATEGPRIZED_POISEARCH);
			}
		};


		/* Set OnClickListener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_mainchoose_page_online_close), this.findViewById(R.id.ibtn_sd_mainchoose_page_offline_close)) {

			@Override
			public void onBoth(final View me, final boolean focused) {
				if (focused && SDMainChoose.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDMainChoose.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/*
				 * Set RsultCode that the calling activity knows that we want to
				 * go back to the Base-Menu
				 */
				SDMainChoose.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDMainChoose.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
