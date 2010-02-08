// Created by plusminus on 18:17:25 - 12.12.2008
package org.andnav2.ui.sd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.andnav2.R;
import org.andnav2.db.DBManager;
import org.andnav2.db.DataBaseException;
import org.andnav2.exc.Exceptor;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.GeocodedAddress;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.ors.adt.lus.ICountrySubdivision;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.sys.ors.lus.LUSRequester;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.map.OpenStreetDDMap;
import org.andnav2.ui.sd.util.StreetInfoExtractor;
import org.andnav2.util.UserTask;
import org.andnav2.util.constants.Constants;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.admob.android.ads.AdView;


public class SDResolver extends AndNavBaseActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int REQUESTCODE_DDMAP = 0x1337;

	private static final String STATE_RESOLVEDADDRESSES_ITEMS_ID = "state_resolvedaddresses_items_id";

	// ===========================================================
	// Fields
	// ===========================================================

	private Bundle mBundleCreatedWith;
	private ListView mResolvedAddressesList;

	private ArrayList<GeocodedAddressItem> mResolvedItems = new ArrayList<GeocodedAddressItem>();

	private boolean mResolvingFinished = false;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, true); // We need DataState-Info ... do we ?
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.sd_resolver);

		this.mBundleCreatedWith = this.getIntent().getExtras();

		if(Preferences.getAdFreeState(this) == false)
			((AdView)findViewById(R.id.ad)).setGoneWithoutAd(true);
		else
			((AdView)findViewById(R.id.ad)).setVisibility(View.GONE);

		this.mResolvedAddressesList = (ListView)this.findViewById(R.id.list_resolvedaddresses);

		/* Set empty view for the list. */
		final TextView empty = new TextView(this);
		empty.setText(R.string.list_empty);
		this.mResolvedAddressesList.setEmptyView(empty);

		applyTopMenuButtonListeners();

		initListView();

		if(savedInstanceState == null) {
			resolveAddressToListItemsAsync();
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onSaveInstanceState(final Bundle out) {
		if(this.mResolvingFinished) {
			out.putParcelableArrayList(STATE_RESOLVEDADDRESSES_ITEMS_ID, this.mResolvedItems);
		}
	}

	@Override
	protected void onRestoreInstanceState(final Bundle in) {
		final ArrayList<GeocodedAddressItem> restoredItems = in.getParcelableArrayList(STATE_RESOLVEDADDRESSES_ITEMS_ID);
		if(restoredItems == null){
			resolveAddressToListItemsAsync();
		}else{
			this.mResolvedItems = restoredItems;
			final GeocodedAddressesListAdapter gla = new GeocodedAddressesListAdapter(this);
			gla.setListItems(this.mResolvedItems);
			this.mResolvedAddressesList.setAdapter(gla);
			this.mResolvingFinished = true;
		}
	}

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

	protected void applyTopMenuButtonListeners() {
		/* Set Listener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_resolver_back)) {
			@Override
			public void onClicked(final View me) {
				if (SDResolver.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDResolver.this, R.raw.close).start();
				}

				/* Back one level. */
				SDResolver.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDResolver.this.finish();
			}
		};

		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_resolver_close)) {
			@Override
			public void onClicked(final View me) {
				if (SDResolver.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDResolver.this, R.raw.close).start();
				}
				/*
				 * Set ResultCode that the calling activity knows that we want
				 * to go back to the Base-Menu
				 */
				SDResolver.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDResolver.this.finish();
			}
		};
	}

	private void resolveAddressToListItemsAsync() {
		final GeocodedAddressesListAdapter gala = new GeocodedAddressesListAdapter(SDResolver.this);

		final UserTask<Void, Integer, Void> ut;

		final ProgressDialog pd = ProgressDialog.show(this, getString(R.string.dlg_sd_resolver_loading_title), getString(R.string.please_wait_a_moment), true, true);

		ut = new UserTask<Void, Integer, Void>(){
			@Override
			public Void doInBackground(final Void... params) {
				try{
					ArrayList<GeocodedAddress> resolved = null;

					final Bundle extras = SDResolver.this.mBundleCreatedWith; // Drag to local field

					final Country nat = extras.getParcelable(EXTRAS_COUNTRY_ID);
					final ICountrySubdivision subdivision = extras.getParcelable(EXTRAS_COUNTRYSUBDIVISIONCODE_ID);
					final String streetName = extras.getString(EXTRAS_STREET_ID);
					final String streetNumber = extras.getString(EXTRAS_STREETNUMBER_ID);

					/* Switch on the Mode. */
					final int searchMode = extras.getInt(EXTRAS_MODE);
					switch (searchMode) {
						case EXTRAS_MODE_STREETNAMESEARCH:
							final String dummycityname = "";
							resolved = LUSRequester.requestStreetaddressCity(SDResolver.this, nat, subdivision, dummycityname, streetName, streetNumber);
							break;
						case EXTRAS_MODE_FREEFORMSEARCH:
							final String freeformAddress = extras.getString(EXTRAS_FREEFORM_ID);
							resolved = LUSRequester.requestFreeformAddress(SDResolver.this, nat, freeformAddress);
							break;
						case EXTRAS_MODE_ZIPSEARCH:
							final String zipCode = extras.getString(EXTRAS_ZIPCODE_ID);
							resolved = LUSRequester.requestStreetaddressPostalcode(SDResolver.this, nat, subdivision, zipCode, streetName, streetNumber);
							break;
						case EXTRAS_MODE_CITYNAMESEARCH:
							final String cityname = extras.getString(EXTRAS_CITYNAME_ID);
							resolved = LUSRequester.requestStreetaddressCity(SDResolver.this, nat, subdivision, cityname, streetName, streetNumber);
							break;
						default:
							throw new IllegalArgumentException("Unawaited MODE in SDResolver.");
					}

					if(resolved == null || resolved.size() == 0){
						runOnUiThread(new Runnable(){
							public void run() {
								Toast.makeText(SDResolver.this, R.string.toast_could_not_resolve_address, Toast.LENGTH_LONG).show();
							}
						});
					}else{
						for(final GeocodedAddress ga : resolved) {
							SDResolver.this.mResolvedItems.add(new GeocodedAddressItem(ga));
						}
					}


					/* Adapt the list to the Adapter. */
					gala.setListItems(SDResolver.this.mResolvedItems);/* Orders by name, ascending. */
					SDResolver.this.mResolvingFinished = true;
				}catch(final ORSException e){
					runOnUiThread(new Runnable(){
						public void run() {
							Toast.makeText(SDResolver.this, R.string.toast_could_not_resolve_address, Toast.LENGTH_LONG).show();
						}
					});
				}catch(final Exception e){
					Exceptor.e("SDResolver-Exception", e, SDResolver.this);
				}
				return null;
			}

			@Override
			public void onPostExecute(final Void result) {
				/* Adapt the Adapter to the ListView. */
				SDResolver.this.mResolvedAddressesList.setAdapter(gala);

				try{
					pd.dismiss();
				}catch(final IllegalArgumentException ia){
					// Nothing
				}
			}
		};

		pd.setOnCancelListener(new OnCancelListener(){
			public void onCancel(final DialogInterface d) {
				d.dismiss();
				if(ut != null) {
					ut.cancel(true);
				}
			}
		});

		ut.execute();
	}

	protected void initListView() {
		this.mResolvedAddressesList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
				final GeocodedAddressItem r = (GeocodedAddressItem)parent.getAdapter().getItem(position);

				final int searchMode = SDResolver.this.mBundleCreatedWith.getInt(EXTRAS_MODE);

				if(searchMode == EXTRAS_MODE_ZIPSEARCH || searchMode == EXTRAS_MODE_CITYNAMESEARCH){
					final GeoPoint dest = r.mGeocodedAddress;
					try {
						DBManager.addFavorite(SDResolver.this, getFavoriteEntry(), dest.getLatitudeE6(), dest.getLongitudeE6());
					} catch (final DataBaseException e) {
						Log.e(Constants.DEBUGTAG, "Error adding Favorite", e);
					}
				}


				/* Proceed to next screen. */
				final int mode = SDResolver.this.mBundleCreatedWith.getInt(MODE_SD);
				final Bundle b = new Bundle();

				b.putInt(EXTRAS_MODE, EXTRAS_MODE_DIRECT_LATLNG);
				b.putString(EXTRAS_COUNTRY_ID, SDResolver.this.mBundleCreatedWith.getString(EXTRAS_COUNTRY_ID)); // Adds the country if available
				b.putInt(EXTRAS_DESTINATION_LATITUDE_ID, r.mGeocodedAddress.getLatitudeE6());
				b.putInt(EXTRAS_DESTINATION_LONGITUDE_ID, r.mGeocodedAddress.getLongitudeE6());

				switch(mode){
					case MODE_SD_DESTINATION:
						final Intent directIntent = new Intent(SDResolver.this, OpenStreetDDMap.class);
						directIntent.putExtras(b);
						SDResolver.this.startActivityForResult(directIntent, REQUESTCODE_DDMAP);
						break;
					case MODE_SD_RESOLVE:
					case MODE_SD_SETHOME:
					case MODE_SD_WAYPOINT:
						final Intent resultData = new Intent();
						resultData.putExtras(b);
						SDResolver.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS, resultData);
						SDResolver.this.finish();
				}

			}
		});
	}

	private String getFavoriteEntry() {
		final StringBuilder sb = new StringBuilder();

		/* Switch on the Mode. */
		final int searchMode = this.mBundleCreatedWith.getInt(EXTRAS_MODE);
		switch (searchMode) {
			case EXTRAS_MODE_ZIPSEARCH:
			case EXTRAS_MODE_CITYNAMESEARCH:

				final Country nat = this.mBundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
				sb.append(getString(nat.NAMERESID));
				sb.append(' ');

				final String zipCodeOrCityName = org.andnav2.ui.sd.Util.getZipCodeOrCityName(this.mBundleCreatedWith);
				sb.append(zipCodeOrCityName);
				sb.append(' ');

				final String streetName = this.mBundleCreatedWith.getString(EXTRAS_STREET_ID);
				final String streetNumber = this.mBundleCreatedWith.getString(EXTRAS_STREETNUMBER_ID);
				sb.append(StreetInfoExtractor.constructStreetLineInfo(this, streetName, streetNumber, nat));
				break;
		}
		return sb.toString();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static class GeocodedAddressItem implements Comparable<GeocodedAddressItem>, Parcelable {
		protected final GeocodedAddress mGeocodedAddress;

		private GeocodedAddressItem(final GeocodedAddress pGeocodedAddress) {
			this.mGeocodedAddress = pGeocodedAddress;
		}

		public int compareTo(final GeocodedAddressItem another) {
			final float diff = -(this.mGeocodedAddress.getAccuracy() - another.mGeocodedAddress.getAccuracy());
			if(Math.abs(diff) < 0.001f){
				final int cityDiff = this.mGeocodedAddress.getMunicipality().compareToIgnoreCase(another.mGeocodedAddress.getMunicipality());
				if(cityDiff != 0 || this.mGeocodedAddress.getStreetNameOfficial() == null){
					return cityDiff;
				}else{
					return this.mGeocodedAddress.getStreetNameOfficial().compareToIgnoreCase(another.mGeocodedAddress.getStreetNameOfficial());
				}
			}else if(diff < 0){
				return -1; // this before another
			}else{
				return 1;
			}
		}

		// ===========================================================
		// Parcelable
		// ===========================================================

		public static final Parcelable.Creator<GeocodedAddressItem> CREATOR = new Parcelable.Creator<GeocodedAddressItem>() {
			public GeocodedAddressItem createFromParcel(final Parcel in) {
				return readFromParcel(in);
			}

			public GeocodedAddressItem[] newArray(final int size) {
				return new GeocodedAddressItem[size];
			}
		};

		public int describeContents() {
			return 0;
		}

		public void writeToParcel(final Parcel out, final int flags) {
			out.writeParcelable(this.mGeocodedAddress, 0);
		}

		private static GeocodedAddressItem readFromParcel(final Parcel in){
			final GeocodedAddress addr = in.readParcelable(GeocodedAddress.class.getClassLoader());

			return new GeocodedAddressItem(addr);
		}
	}

	private class GeoCodedAddressListItemView extends LinearLayout{
		private final ImageView mIVNationality;
		private final TextView mTVMunicipality;
		private final TextView mTVStreet;

		public GeoCodedAddressListItemView(final Context context, final GeocodedAddressItem aItem) {
			super(context);
			this.setOrientation(HORIZONTAL);

			final GeocodedAddress ga = aItem.mGeocodedAddress;
			final Country nat = ga.getNationality();

			this.mIVNationality = new ImageView(context);
			this.mIVNationality.setPadding(10, 0, 10, 0);
			this.mIVNationality.setImageResource(nat.FLAGRESID);

			addView(this.mIVNationality);

			final LinearLayout ll = new LinearLayout(context);
			ll.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			ll.setOrientation(VERTICAL);
			addView(ll);

			/* CityLine */
			final String cityLine = StreetInfoExtractor.constructCityLineInfo(context, ga.getMunicipality(),  ga.getPostalCode(), nat);

			this.mTVMunicipality = new TextView(context);
			this.mTVMunicipality.setText(cityLine);
			this.mTVMunicipality.setTextSize(TypedValue.COMPLEX_UNIT_PX, 26);
			this.mTVMunicipality.setPadding(10,0,10,0);

			ll.addView(this.mTVMunicipality, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));


			/* StreetLine */
			this.mTVStreet = new TextView(context);

			final String streetName = ga.getStreetNameOfficial();
			final String streetNumber = ga.getStreetNumber();

			final String streetLine = StreetInfoExtractor.constructStreetLineInfo(context, streetName, streetNumber, nat);

			if(streetLine != null && streetLine.length() > 0) {

				this.mTVStreet.setText(streetLine);
				this.mTVStreet.setTextSize(TypedValue.COMPLEX_UNIT_PX, 18);

				ll.addView(this.mTVStreet, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			}
		}
	}

	private class GeocodedAddressesListAdapter extends BaseAdapter {

		/** Remember our context so we can use it when constructing views. */
		private final Context mContext;

		private List<GeocodedAddressItem> mItems = new ArrayList<GeocodedAddressItem>();

		public GeocodedAddressesListAdapter(final Context context) {
			this.mContext = context;
		}

		public void addItem(final GeocodedAddressItem it) {
			this.mItems.add(it);
			Collections.sort(this.mItems);
		}

		public void setListItems(final List<GeocodedAddressItem> lit) {
			this.mItems = lit;
			Collections.sort(this.mItems);
		}

		@Override
		public boolean isEmpty() {
			return this.mItems == null || this.mItems.size() == 0;
		}

		/** @return The number of items in the */
		public int getCount() { return this.mItems.size(); }

		public Object getItem(final int position) { return this.mItems.get(position); }

		public long getItemId(final int position) { return position; }

		public View getView(final int position, final View convertView, final ViewGroup parent) {
			GeoCodedAddressListItemView btv;
			if (convertView == null) {
				btv = new GeoCodedAddressListItemView(this.mContext, this.mItems.get(position));
			} else { // Reuse/Overwrite the View passed
				// We are assuming(!) that it is castable!
				btv = (GeoCodedAddressListItemView) convertView;

				final GeocodedAddress ga = this.mItems.get(position).mGeocodedAddress;
				final Country nat = ga.getNationality();

				btv.mIVNationality.setImageResource(nat.FLAGRESID);

				btv.mTVMunicipality.setText(StreetInfoExtractor.constructCityLineInfo(this.mContext, ga.getMunicipality(),  ga.getPostalCode(), nat));

				final String streetLine = StreetInfoExtractor.constructStreetLineInfo(this.mContext, ga.getStreetNameOfficial(), ga.getStreetNumber(), nat);
				if(streetLine != null && streetLine.length() > 0) {
					btv.mTVStreet.setText(streetLine);
				}
			}
			return btv;
		}
	}
}
