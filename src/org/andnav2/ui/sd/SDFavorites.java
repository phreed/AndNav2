// Created by plusminus on 21:11:40 - 13.06.2008
package org.andnav2.ui.sd;

import org.andnav2.R;
import org.andnav2.adt.Favorite;
import org.andnav2.db.DBManager;
import org.andnav2.db.DataBaseException;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.CommonCallbackAdapter;
import org.andnav2.ui.common.CommonDialogFactory;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.map.OpenStreetDDMap;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class SDFavorites extends AndNavBaseActivity {

	// ===========================================================
	// Final Fields
	// ===========================================================

	protected static final int CONTEXTMENU_USEITEM = 0;
	protected static final int CONTEXTMENU_DELETEITEM = CONTEXTMENU_USEITEM + 1;

	protected static final int REQUESTCODE_DDMAP = 0x1337;

	public static final String EXTRAS_FAVORITES_REFER = "doStartNavToOnListItemClick";

	protected static final int MENU_WIPE_ID = Menu.FIRST;
	protected static final int DIALOG_SELECT_USE_OR_DELETE = 0;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Bundle bundleCreatedWith;
	protected ListView mFavList;
	protected int mSelectedFavoritePosition;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle, false); // We need DataState-Info
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.sd_favorites);

		/*
		 * Save the Extras Bundle of the Intent this Activity was created with,
		 * because it contains the Information, that will finally be used for a
		 * GeoCode API.
		 */
		this.bundleCreatedWith = this.getIntent().getExtras();
		this.mFavList = (ListView) this.findViewById(R.id.list_favorites);

		final TextView empty = new TextView(this);
		empty.setText(R.string.list_empty);
		this.mFavList.setEmptyView(empty);

		initListView(this.bundleCreatedWith.getBoolean(EXTRAS_FAVORITES_REFER));

		this.applyTopMenuButtonListeners();
	}

	private void updateFavListItems() throws DataBaseException {
		this.mFavList.setAdapter(new ArrayAdapter<Favorite>(this, android.R.layout.simple_list_item_1, DBManager.getFavorites(this)));
	}

	protected void initListView(final boolean doStartNavOnClick) {
		try {
			updateFavListItems();

			this.mFavList.setOnItemLongClickListener(new OnItemLongClickListener(){
				public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
					SDFavorites.this.mSelectedFavoritePosition = position;
					showDialog(DIALOG_SELECT_USE_OR_DELETE);
					return true;
				}
			});

			if(doStartNavOnClick){
				this.mFavList.setOnItemClickListener(new OnItemClickListener(){
					public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
						final Favorite f = (Favorite)parent.getAdapter().getItem(position);
						handleFavoriteClicked(f);
					}
				});
			}
		} catch (final DataBaseException e) {
			//			Log.e(DEBUGTAG, "Error getting favorites", e); // TODO Show Error to user
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, MENU_WIPE_ID, Menu.NONE, R.string.menu_sd_favorites_wipe).setIcon(R.drawable.wipe).setAlphabeticShortcut('w');
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		switch(id){
			case DIALOG_SELECT_USE_OR_DELETE:
				return CommonDialogFactory.createFavoriteUseOrDeleteDialog(this, new CommonCallbackAdapter<Integer>(){
					@Override
					public void onSuccess(final Integer result) {
						switch(result){
							case CONTEXTMENU_USEITEM:
								handleFavoriteClicked((Favorite) SDFavorites.this.mFavList.getAdapter().getItem(SDFavorites.this.mSelectedFavoritePosition));
								break;
							case CONTEXTMENU_DELETEITEM:
								final String favName = ((Favorite) SDFavorites.this.mFavList.getAdapter().getItem(SDFavorites.this.mSelectedFavoritePosition)).getName();
								try {
									DBManager.deleteFavoriteByName(SDFavorites.this, favName);
									updateFavListItems();
								} catch (final DataBaseException e) {
									//										Log.e(DEBUGTAG, "Error deleting Favorite!", e);
								}
								break;
						}
					}
				});
			default:
				return null;
		}
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch(item.getItemId()){
			case MENU_WIPE_ID:
				try {
					DBManager.clearFavorites(this);
					updateFavListItems();
				} catch (final DataBaseException e) {
					Log.e(DEBUGTAG, "DBError", e);
				}
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
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

	private void handleFavoriteClicked(final Favorite f) {
		try {
			DBManager.addFavorite(SDFavorites.this, f.getName(), f.getLatitudeE6(), f.getLongitudeE6());
		} catch (final DataBaseException e) {
			//			Log.e(DEBUGTAG, "Could not increment Favorite-Uses", e);
		}

		final int mode = SDFavorites.this.bundleCreatedWith.getInt(MODE_SD);
		switch(mode){
			case MODE_SD_DESTINATION:
				final Intent directIntent = new Intent(SDFavorites.this, OpenStreetDDMap.class);
				final Bundle b = new Bundle();
				b.putInt(EXTRAS_MODE, EXTRAS_MODE_DIRECT_LATLNG);
				b.putInt(EXTRAS_DESTINATION_LATITUDE_ID, f.getLatitudeE6());
				b.putInt(EXTRAS_DESTINATION_LONGITUDE_ID, f.getLongitudeE6());
				directIntent.putExtras(b);
				SDFavorites.this.startActivityForResult(directIntent, REQUESTCODE_DDMAP);
				break;
			case MODE_SD_SETHOME:
			case MODE_SD_RESOLVE:
			case MODE_SD_WAYPOINT:
				final Intent resultData = new Intent();

				SDFavorites.this.bundleCreatedWith.putInt(EXTRAS_MODE, EXTRAS_MODE_DIRECT_LATLNG);
				SDFavorites.this.bundleCreatedWith.putInt(EXTRAS_DESTINATION_LATITUDE_ID, f.getLatitudeE6());
				SDFavorites.this.bundleCreatedWith.putInt(EXTRAS_DESTINATION_LONGITUDE_ID, f.getLongitudeE6());

				resultData.putExtras(SDFavorites.this.bundleCreatedWith);
				SDFavorites.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS, resultData);
				SDFavorites.this.finish();

				break;
		}
	}

	protected void applyTopMenuButtonListeners() {
		/* Set Listener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_favorites_back)) {

			@Override
			public void onBoth(final View me, final boolean focused) {
				if (focused && SDFavorites.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDFavorites.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Back one level. */
				SDFavorites.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDFavorites.this.finish();
			}
		};

		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_favorites_close)) {

			@Override
			public void onBoth(final View me, final boolean focused) {
				if (focused && SDFavorites.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDFavorites.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/*
				 * Set ResultCode that the calling activity knows that we want
				 * to go back to the Base-Menu
				 */
				SDFavorites.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDFavorites.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
