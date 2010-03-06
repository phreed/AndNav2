// Created by plusminus on 12:47:27 - 26.10.2008
package org.andnav2.ui.sd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.andnav2.R;
import org.andnav2.adt.DBPOI;
import org.andnav2.adt.UnitSystem;
import org.andnav2.db.DBManager;
import org.andnav2.db.DataBaseException;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.adt.util.TypeConverter;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.GeocodedAddress;
import org.andnav2.sys.ors.adt.ds.ORSPOI;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.sys.ors.ds.DSRequester;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.sys.ors.lus.LUSRequester;
import org.andnav2.ui.AndNavGPSActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.map.OpenStreetDDMap;
import org.andnav2.ui.sd.util.StreetInfoExtractor;
import org.andnav2.util.UserTask;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.admob.android.ads.AdView;



public class SDPOISearchList extends AndNavGPSActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final String EXTRAS_POISEARCH_MODE = "extras_poisearch_mode";
	public static final int EXTRAS_POISEARCH_MODE_ORS_CATEGORY_SEARCH = 0;
	public static final int EXTRAS_POISEARCH_MODE_GOOGLE_FREEFORM_SEARCH = EXTRAS_POISEARCH_MODE_ORS_CATEGORY_SEARCH + 1;

	public static final String EXTRAS_POISEARCH_CATEGORY = "EXTRAS_POISEARCH_CATEGORY";

	public static final String EXTRAS_POISEARCH_RADIUS = "EXTRAS_POISEARCH_RADIUS_ID";
	public static final String EXTRAS_POISEARCH_QUERY = "EXTRAS_POISEARCH_QUERY";
	public static final int POISEARCH_RADIUS_GLOBAL = -1;

	protected static final int CONTEXTMENU_NAVTO_ITEM = 0;
	protected static final int CONTEXTMENU_CANCEL_ITEM = CONTEXTMENU_NAVTO_ITEM + 1;

	protected static final int REQUESTCODE_DDMAP = 0x1337;
	private static final String STATE_RESOLVEITEMS_ID = "state_resolveitems_id";

	// ===========================================================
	// Fields
	// ===========================================================

	protected Bundle bundleCreatedWith;
	protected ListView mFoundPOIList;
	private UnitSystem mUnitSystem;

	private ArrayList<POIItem> mResolvedItems = new ArrayList<POIItem>();

	private boolean mResolvingFinished = false;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.sd_poi_searchlist);

		this.mUnitSystem = Preferences.getUnitSystem(this);

		if(Preferences.getAdFreeState(this) == false)
			((AdView)findViewById(R.id.ad)).setGoneWithoutAd(true);
		else
			((AdView)findViewById(R.id.ad)).setVisibility(View.GONE);

		/*
		 * Save the Extras Bundle of the Intent this Activity was created with,
		 * because it contains the Information, that will finally be used for a
		 * GeoCode API.
		 */
		this.bundleCreatedWith = this.getIntent().getExtras();
		this.mFoundPOIList = (ListView) this.findViewById(R.id.list_poi_searchlist);

		final TextView empty = new TextView(this);
		empty.setText(R.string.list_empty);
		this.mFoundPOIList.setEmptyView(empty);

		this.applyTopMenuButtonListeners();

		if(savedInstanceState == null) {
			resolvePOIItemsAsync();
		}
	}

	private void resolvePOIItemsAsync() {
		final int aSearchRadius = this.bundleCreatedWith.getInt(EXTRAS_POISEARCH_RADIUS);

		final int mode = this.bundleCreatedWith.getInt(EXTRAS_POISEARCH_MODE);
		switch(mode){
			case EXTRAS_POISEARCH_MODE_GOOGLE_FREEFORM_SEARCH:
				searchFreeFormPOI(this.bundleCreatedWith.getString(EXTRAS_POISEARCH_QUERY), aSearchRadius);
				initListViewListener();
				break;
			case EXTRAS_POISEARCH_MODE_ORS_CATEGORY_SEARCH:
				searchCategoryPOI(POIType.fromRawName(this.bundleCreatedWith.getString(EXTRAS_POISEARCH_CATEGORY)), aSearchRadius);
				initListViewListener();
				break;
		}
	}

	protected void initListViewListener() {
		this.mFoundPOIList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
				final POIItem p = (POIItem)parent.getAdapter().getItem(position);

				try {
					final int latE6 = p.getLatitudeE6();
					final int lonE6 = p.getLongitudeE6();
					final String query = SDPOISearchList.this.bundleCreatedWith.getString(EXTRAS_POISEARCH_QUERY);
					DBManager.addPOIToHistory(SDPOISearchList.this, query, latE6, lonE6);
					DBManager.addPOIToHistory(SDPOISearchList.this, p.getName(), latE6, lonE6);
					DBManager.addFavorite(SDPOISearchList.this, query, latE6, lonE6);
				} catch (final DataBaseException e) {
					Log.e(DEBUGTAG, "DBError", e);
				}

				final int mode = SDPOISearchList.this.bundleCreatedWith.getInt(MODE_SD);
				switch(mode){
					case MODE_SD_DESTINATION:
						final Intent directIntent = new Intent(SDPOISearchList.this, OpenStreetDDMap.class);
						final Bundle b = new Bundle();
						b.putInt(EXTRAS_MODE, EXTRAS_MODE_DIRECT_LATLNG);

						b.putInt(EXTRAS_DESTINATION_LATITUDE_ID, p.getLatitudeE6());
						b.putInt(EXTRAS_DESTINATION_LONGITUDE_ID, p.getLongitudeE6());

						directIntent.putExtras(b);
						SDPOISearchList.this.startActivityForResult(directIntent, REQUESTCODE_DDMAP);
						break;
					case MODE_SD_SETHOME:
					case MODE_SD_WAYPOINT:
					case MODE_SD_RESOLVE:
						final Intent resultData = new Intent();

						SDPOISearchList.this.bundleCreatedWith.putInt(EXTRAS_MODE, EXTRAS_MODE_DIRECT_LATLNG);
						SDPOISearchList.this.bundleCreatedWith.putInt(EXTRAS_DESTINATION_LATITUDE_ID, p.getLatitudeE6());
						SDPOISearchList.this.bundleCreatedWith.putInt(EXTRAS_DESTINATION_LONGITUDE_ID, p.getLongitudeE6());

						resultData.putExtras(SDPOISearchList.this.bundleCreatedWith);
						SDPOISearchList.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS, resultData);
						SDPOISearchList.this.finish();

						break;
				}
			}
		});
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
			out.putParcelableArrayList(STATE_RESOLVEITEMS_ID, this.mResolvedItems);
		}
	}

	@Override
	protected void onRestoreInstanceState(final Bundle in) {
		final ArrayList<POIItem> restoredItems = in.getParcelableArrayList(STATE_RESOLVEITEMS_ID);
		if(restoredItems == null){
			resolvePOIItemsAsync();
		}else{
			this.mResolvedItems = restoredItems;
			final POIListAdapter gla = new POIListAdapter(this);
			gla.setListItems(this.mResolvedItems);
			this.mFoundPOIList.setAdapter(gla);
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

	private void searchCategoryPOI(final POIType type, final int aSearchRadius) {
		final POIListAdapter pla = new POIListAdapter(SDPOISearchList.this);

		final UserTask<Void, Integer, Void> ut;

		final ProgressDialog pd = ProgressDialog.show(this, getString(R.string.please_wait_a_moment), getString(R.string.toast_sd_poi_getting_gps_and_poi_search), true, true);

		ut = new UserTask<Void, Integer, Void>(){
			@Override
			public Void doInBackground(final Void... params) {
				try{
					if(!ensureLocationExists()){
						Toast.makeText(SDPOISearchList.this, R.string.location_could_not_be_resolved , Toast.LENGTH_LONG).show();
						runOnUiThread(new Runnable(){
							public void run() {
								pd.dismiss();
							}
						});
						return null;
					}
					final GeoPoint center = TypeConverter.locationToGeoPoint(SDPOISearchList.super.mMyLocation);


					final ArrayList<ORSPOI> orspois = DSRequester.request(SDPOISearchList.this, center, type, aSearchRadius);
					for (final ORSPOI orspoi : orspois){
						String name = orspoi.getName();
						if(name.length() == 0){
							name = getString(R.string.unknown);
						}
						SDPOISearchList.this.mResolvedItems.add(new POIItem(orspoi.getDistance(), name, orspoi.getGeoPoint(), SDPOISearchList.this.mUnitSystem));
					}

					runOnUiThread(new Runnable(){
						public void run() {
							pd.dismiss();
							if(SDPOISearchList.this.mResolvedItems.size() == 0){
								final String[] distStringParts = SDPOISearchList.this.mUnitSystem.getDistanceString(aSearchRadius, null);
								final String distString = distStringParts[UnitSystem.DISTSTRINGS_DIST_ID] + distStringParts[UnitSystem.DISTSTRINGS_UNIT_ID];

								Toast.makeText(SDPOISearchList.this, getString(R.string.toast_sd_poi_could_not_find, getString(type.READABLENAMERESID), distString), Toast.LENGTH_LONG).show();
							}else{
								Toast.makeText(SDPOISearchList.this, getString(R.string.whereami_search_places_found) + " " + SDPOISearchList.this.mResolvedItems.size(), Toast.LENGTH_SHORT).show();
							}
						}
					});

					/* Adapt the list to the Adapter. */
					pla.setListItems(SDPOISearchList.this.mResolvedItems);/* Orders by name, ascending. */
					SDPOISearchList.this.mResolvingFinished = true;
				} catch (final Exception e) {
					runOnUiThread(new Runnable(){
						public void run() {
							pd.dismiss();
							// TODO GLobal beachten!
							final String[] distStringParts = SDPOISearchList.this.mUnitSystem.getDistanceString(aSearchRadius, null);
							final String distString = distStringParts[UnitSystem.DISTSTRINGS_DIST_ID] + distStringParts[UnitSystem.DISTSTRINGS_UNIT_ID];

							Toast.makeText(SDPOISearchList.this, getString(R.string.toast_sd_poi_could_not_find, getString(type.READABLENAMERESID), distString), Toast.LENGTH_LONG).show();
						}
					});
					//					Log.e(DEBUGTAG, "GeocodeError", e);
				}
				return null;
			}
			@Override
			public void onPostExecute(final Void result) {
				/* Adapt the Adapter to the ListView. */
				SDPOISearchList.this.mFoundPOIList.setAdapter(pla);

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

	/**
	 * Searches in a Thread --> Unblocking.
	 * @param query
	 * @param which 0 for GLOBAL , 1 for VISIBLE RECTANGLE!
	 */
	private void searchFreeFormPOI(final String query, final int aSearchRadius) {
		final POIListAdapter pla = new POIListAdapter(SDPOISearchList.this);

		final UserTask<Void, Integer, Void> ut;

		final ProgressDialog pd = ProgressDialog.show(this, getString(R.string.please_wait_a_moment), getString(R.string.toast_sd_poi_getting_gps_and_poi_search));

		ut = new UserTask<Void, Integer, Void>(){
			@Override
			public Void doInBackground(final Void... params) {
				try {
					if(!ensureLocationExists()){
						runOnUiThread(new Runnable(){
							public void run() {
								Toast.makeText(SDPOISearchList.this, R.string.location_could_not_be_resolved , Toast.LENGTH_LONG).show();
							}
						});
						return null;
					}

					final GeoPoint center = TypeConverter.locationToGeoPoint(SDPOISearchList.super.mMyLocation);
					final List<GeocodedAddress> ret;

					final int searchdist = aSearchRadius;

					ret = LUSRequester.requestFreeformAddress(SDPOISearchList.this, null, query);

					runOnUiThread(new Runnable(){
						public void run() {
							pd.dismiss();
							if(ret == null || ret.size() == 0){
								Toast.makeText(SDPOISearchList.this, R.string.whereami_search_no_places_found, Toast.LENGTH_SHORT).show();
							}else{
								for (final GeocodedAddress a : ret){
									try{
										if(a.getLatitudeE6() != 0  && a.getLongitudeE6() != 0){
											final int distanceToCenter = center.distanceTo(a);
											if(searchdist == SDPOISearchList.POISEARCH_RADIUS_GLOBAL || distanceToCenter < searchdist){
												final String description;

												final String cityInfo = StreetInfoExtractor.constructCityLineInfo(SDPOISearchList.this, a.getMunicipality(), a.getPostalCode(), a.getNationality());
												if(cityInfo.length() > 0) {
													description = cityInfo;
												} else {
													description = StreetInfoExtractor.constructStreetLineInfo(SDPOISearchList.this, a.getStreetNameOfficial(), a.getStreetNumber(), a.getNationality());
												}


												SDPOISearchList.this.mResolvedItems.add(new POIItem(distanceToCenter, description, a, SDPOISearchList.this.mUnitSystem));
											}
										}
									}catch(final Exception e){ }
								}

								if(SDPOISearchList.this.mResolvedItems.size() == 0){
									Toast.makeText(SDPOISearchList.this, R.string.whereami_search_no_places_found, Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(SDPOISearchList.this, getString(R.string.whereami_search_places_found) + " " + SDPOISearchList.this.mResolvedItems.size(), Toast.LENGTH_SHORT).show();
								}
							}
						}
					});

					/* Adapt the list to the Adapter. */
					pla.setListItems(SDPOISearchList.this.mResolvedItems);/* Orders by name, ascending. */
					SDPOISearchList.this.mResolvingFinished = true;

				} catch (final ORSException e) {
					runOnUiThread(new Runnable(){
						public void run() {
							pd.dismiss();
							Toast.makeText(SDPOISearchList.this, e.getErrors().get(0).toUserString(), Toast.LENGTH_SHORT).show();
						}
					});
					//					Log.e(DEBUGTAG, "GeocodeError", e);
				} catch (final Exception e) {
					runOnUiThread(new Runnable(){
						public void run() {
							pd.dismiss();
							Toast.makeText(SDPOISearchList.this, R.string.whereami_search_no_places_found, Toast.LENGTH_SHORT).show(); // TODO Wrong String?
						}
					});
					//					Log.e(DEBUGTAG, "GeocodeError", e);
				}
				return null;
			}
			@Override
			public void onPostExecute(final Void result) {
				/* Adapt the Adapter to the ListView. */
				SDPOISearchList.this.mFoundPOIList.setAdapter(pla);

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

	protected void applyTopMenuButtonListeners() {
		/* Set Listener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_poi_searchlist_back)) {
			@Override
			public void onClicked(final View me) {
				if (SDPOISearchList.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDPOISearchList.this, R.raw.close).start();
				}

				/* Back one level. */
				SDPOISearchList.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDPOISearchList.this.finish();
			}
		};

		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_poi_searchlist_close)) {

			@Override
			public void onClicked(final View me) {
				if (SDPOISearchList.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDPOISearchList.this, R.raw.close).start();
				}

				/*
				 * Set ResultCode that the calling activity knows that we want
				 * to go back to the Base-Menu
				 */
				SDPOISearchList.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDPOISearchList.this.finish();
			}
		};
	}

	private boolean ensureLocationExists() {
		int tryCount = 30;
		do{
			tryCount--;
			/* Refresh own GPS position. */
			if((SDPOISearchList.super.mMyLocation = SDPOISearchList.this.mLocationManager.getLastKnownLocation(PROVIDER_NAME)) == null){
				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e) { }
			}
		}while(SDPOISearchList.super.mMyLocation == null && tryCount >= 0);

		return SDPOISearchList.super.mMyLocation != null;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static class POIItem extends DBPOI implements Comparable<POIItem>, Parcelable {
		protected final int mDistanceToUserLocation;
		protected final String mDistanceToUserLocationTextual;

		protected static UnitSystem mUnitSystem; // WORKAROUND, because we need a reference for unparceling, as this is a static class

		private POIItem(final int distanceToUserLocation, final String description, final GeoPoint geoPoint, final UnitSystem us) {
			super(description, geoPoint);
			mUnitSystem = us;
			this.mDistanceToUserLocation = distanceToUserLocation;

			final String[] distStringParts = us.getDistanceString(distanceToUserLocation, null);
			final String distString = distStringParts[UnitSystem.DISTSTRINGS_DIST_ID] + distStringParts[UnitSystem.DISTSTRINGS_UNIT_ID];

			this.mDistanceToUserLocationTextual = distString;
		}

		public int compareTo(final POIItem another) {
			return this.mDistanceToUserLocation - another.mDistanceToUserLocation;
		}

		// ===========================================================
		// Parcelable
		// ===========================================================

		public static final Parcelable.Creator<POIItem> CREATOR = new Parcelable.Creator<POIItem>() {
			public POIItem createFromParcel(final Parcel in) {
				return readFromParcel(in);
			}

			public POIItem[] newArray(final int size) {
				return new POIItem[size];
			}
		};

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(final Parcel out, final int flags) {
			out.writeInt(this.mLatitudeE6);
			out.writeInt(this.mLongitudeE6);
			out.writeInt(this.mDistanceToUserLocation);
			out.writeString(this.mName);
			out.writeInt(mUnitSystem.ordinal());
		}

		private static POIItem readFromParcel(final Parcel in){
			final GeoPoint geoPoint = new GeoPoint(in.readInt(), in.readInt());
			final int distanceToUserLocation = in.readInt();
			final String description = in.readString();
			final int unitSystemOrdinal = in.readInt();

			return new POIItem(distanceToUserLocation, description, geoPoint, UnitSystem.values()[unitSystemOrdinal]);
		}
	}

	private class POIListItemView extends LinearLayout{

		private final TextView mTVDescription;
		private final TextView mTVDistance;
		//	     private ImageView mIcon;

		public POIListItemView(final Context context, final POIItem aPOIItem) {
			super(context);

			this.setOrientation(HORIZONTAL);

			this.mTVDistance = new TextView(context);
			this.mTVDistance.setText(aPOIItem.mDistanceToUserLocationTextual);
			this.mTVDistance.setTextSize(TypedValue.COMPLEX_UNIT_PX, 16);
			this.mTVDistance.setPadding(10,0,20,0);

			addView(this.mTVDistance, new LinearLayout.LayoutParams(90, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));


			this.mTVDescription = new TextView(context);
			this.mTVDescription.setText(aPOIItem.getName());
			this.mTVDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);

			addView(this.mTVDescription, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		}

	}

	private class POIListAdapter extends BaseAdapter{

		/** Remember our context so we can use it when constructing views. */
		private final Context mContext;

		private List<POIItem> mItems = new ArrayList<POIItem>();

		public POIListAdapter(final Context context) {
			this.mContext = context;
		}

		public void addItem(final POIItem it) {
			this.mItems.add(it);
			Collections.sort(this.mItems);
		}

		@Override
		public boolean isEmpty() {
			return this.mItems == null || this.mItems.size() == 0;
		}

		public void setListItems(final List<POIItem> lit) {
			this.mItems = lit;
			Collections.sort(this.mItems);
		}

		/** @return The number of items in the */
		public int getCount() { return this.mItems.size(); }

		public Object getItem(final int position) { return this.mItems.get(position); }

		public long getItemId(final int position) { return position; }

		public View getView(final int position, final View convertView, final ViewGroup parent) {
			POIListItemView btv;
			if (convertView == null) {
				btv = new POIListItemView(this.mContext, this.mItems.get(position));
			} else { // Reuse/Overwrite the View passed
				// We are assuming(!) that it is castable!
				btv = (POIListItemView) convertView;
				btv.mTVDescription.setText(this.mItems.get(position).getName());
				btv.mTVDistance.setText( this.mItems.get(position).mDistanceToUserLocationTextual);
			}
			return btv;
		}

	}

	@Override
	protected void onLocationChanged() {
		// Nothing
	}

	@Override
	protected void onLocationLost() {

	}
}
