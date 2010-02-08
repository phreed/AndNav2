// Created by plusminus on 20:58:26 - 09.12.2008
package org.andnav2.ui.sd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.andnav2.R;
import org.andnav2.exc.Exceptor;
import org.andnav2.osm.util.Util;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.CommonCallbackAdapter;
import org.andnav2.ui.common.CommonDialogFactory;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.common.CommonDialogFactory.CreateSavedRouteSelectionOptions;
import org.andnav2.ui.common.views.FastScrollView;
import org.andnav2.ui.map.OpenStreetDDMap;
import org.andnav2.util.UserTask;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
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


public class SDSavedRouteChooser extends AndNavBaseActivity implements OSMConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final int REQUESTCODE_DDMAP = 0x1337;
	protected static String EXTERNAL_STORAGE_BASEDIRECTORY;

	protected static final int DIALOG_SHOWSELECTEDROUTEOPTIONS_ID = 0;
	protected static final int DIALOG_RENAMESELECTEDROUTE_ID = DIALOG_SHOWSELECTEDROUTEOPTIONS_ID + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Bundle bundleCreatedWith;
	protected ListView mSavedRoutesList;
	protected SavedRouteItem mSelectedSavedRoute;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle, true); // We need DataState-Info
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.sd_savedroutechooser);

		this.bundleCreatedWith = this.getIntent().getExtras();

		this.mSavedRoutesList = (ListView)this.findViewById(R.id.list_savedroutes);

		/* Set empty view for the list. */
		final TextView empty = new TextView(this);
		empty.setText(R.string.list_empty);
		this.mSavedRoutesList.setEmptyView(empty);

		if(!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			/* Should not happen as the activity is not selectable in SDMainChoose, when no external media is inserted. */
		}else{
			EXTERNAL_STORAGE_BASEDIRECTORY = Util.getAndNavExternalStoragePath();

			// Ensure the routes-directory exists.
			new File(EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_SAVEDROUTES_PATH).mkdirs();

			initListView();

			updateSavedRouteListItems();
		}

		applyTopMenuButtonListeners();
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
			case DIALOG_RENAMESELECTEDROUTE_ID:
				return CommonDialogFactory.createInputDialog(this, R.string.dlg_sd_savedroutechooser_contextmenu_rename_hint, new CommonCallbackAdapter<String>(){
					@Override
					public void onSuccess(final String result) {
						final String oldFilename = SDSavedRouteChooser.this.mSelectedSavedRoute.mFileName;

						if(result != null){
							try{
								final boolean success = new File(EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_SAVEDROUTES_PATH + oldFilename).renameTo(new File(EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_SAVEDROUTES_PATH + result));

								if(success){
									updateSavedRouteListItems();
									Toast.makeText(SDSavedRouteChooser.this, R.string.dlg_sd_savedroutechooser_contextmenu_rename_success, Toast.LENGTH_LONG).show();
								}else{
									Toast.makeText(SDSavedRouteChooser.this, R.string.dlg_sd_savedroutechooser_contextmenu_rename_fail, Toast.LENGTH_LONG).show();
								}
							}catch(final Throwable t){
								Toast.makeText(SDSavedRouteChooser.this, R.string.dlg_sd_savedroutechooser_contextmenu_rename_fail, Toast.LENGTH_LONG).show();
							}
						}else{
							Toast.makeText(SDSavedRouteChooser.this, R.string.dlg_sd_savedroutechooser_contextmenu_rename_fail, Toast.LENGTH_LONG).show();
						}
					}
				});
			case DIALOG_SHOWSELECTEDROUTEOPTIONS_ID:
				return CommonDialogFactory.createSavedRouteSelectionOptions(this, new CommonCallbackAdapter<CreateSavedRouteSelectionOptions>(){
					@Override
					public void onSuccess(final CreateSavedRouteSelectionOptions result) {
						final String filename = SDSavedRouteChooser.this.mSelectedSavedRoute.mFileName;

						switch(result){
							case SHARE:
								// TODO RouteFactory.getRouteHandleFromInputStream/File
								break;
							case INFORMATION:
								/* TODO Extract Information from Route. */
								break;
							case RENAME:
								showDialog(DIALOG_RENAMESELECTEDROUTE_ID);
								break;
							case USE:
								/* Proceed to next screen. */
								final int mode = SDSavedRouteChooser.this.bundleCreatedWith.getInt(MODE_SD);
								switch(mode){
									case MODE_SD_DESTINATION:
										final Intent directIntent = new Intent(SDSavedRouteChooser.this, OpenStreetDDMap.class);
										final Bundle b = new Bundle();
										b.putInt(EXTRAS_MODE, EXTRAS_MODE_LOAD_SAVED_ROUTE);

										b.putString(EXTRAS_SAVED_ROUTE_FILENAME_ID, filename);

										directIntent.putExtras(b);
										SDSavedRouteChooser.this.startActivityForResult(directIntent, REQUESTCODE_DDMAP);
										break;
									case MODE_SD_RESOLVE:
									case MODE_SD_SETHOME:
									case MODE_SD_WAYPOINT:
										throw new IllegalArgumentException("Only MODE_SD_DESTINATION is possible.");
								}

								break;
						}
					}
				});
			default:
				return null;
		}
	}

	private static final String STATE_SELECTEDSAVEDROUTE_ID = "state_selectedsavedroute_id";

	@Override
	protected void onSaveInstanceState(final Bundle icicle) {
		super.onSaveInstanceState(icicle);
		icicle.putParcelable(STATE_SELECTEDSAVEDROUTE_ID, this.mSelectedSavedRoute);
	}

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		this.mSelectedSavedRoute = savedInstanceState.getParcelable(STATE_SELECTEDSAVEDROUTE_ID);
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
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_savedroutechooser_back)) {
			@Override
			public void onClicked(final View me) {
				if (SDSavedRouteChooser.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDSavedRouteChooser.this, R.raw.close).start();
				}

				/* Back one level. */
				SDSavedRouteChooser.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDSavedRouteChooser.this.finish();
			}
		};

		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_savedroutechooser_close)) {
			@Override
			public void onClicked(final View me) {
				if (SDSavedRouteChooser.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDSavedRouteChooser.this, R.raw.close).start();
				}
				/*
				 * Set ResultCode that the calling activity knows that we want
				 * to go back to the Base-Menu
				 */
				SDSavedRouteChooser.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDSavedRouteChooser.this.finish();
			}
		};
	}

	private void updateSavedRouteListItems() {
		final ArrayList<SavedRouteItem> savedRouteItems = new ArrayList<SavedRouteItem>();

		final SavedRouteListAdapter sla = new SavedRouteListAdapter(SDSavedRouteChooser.this);

		final ProgressDialog pd = ProgressDialog.show(this, getString(R.string.dlg_sd_savedroutechooser_loading_title), getString(R.string.please_wait_a_moment), false); // TODO Make determinate, when SDK supports this.

		final String progressBaseString = getString(R.string.dlg_sd_savedroutechooser_loading_progress);

		new UserTask<Void, Integer, Void>(){
			@Override
			public Void doInBackground(final Void... params) {
				try{
					final File sdRoot = new File(EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_SAVEDROUTES_PATH);
					final String[] routeFiles = sdRoot.list();
					if (routeFiles != null) {
						final int routeCount = routeFiles.length;
						for (int i = 0; i < routeCount; i++) {
							savedRouteItems.add(new SavedRouteItem(routeFiles[i]));
							publishProgress(i, routeCount);
						}
					}

					Collections.sort(savedRouteItems);

					/* Adapt the list to the Adapter. */
					sla.setListItems(savedRouteItems);/* Orders by name, ascending. */
				}catch(final Exception e){
					Exceptor.e("SavedRouteChooser-Exception", e, SDSavedRouteChooser.this);
				}
				return null;
			}

			@Override
			public void onProgressUpdate(final Integer... progress) {
				pd.setMessage(String.format(progressBaseString, (int)(100*((float)progress[0] / progress[1])), progress[0], progress[1]));
			}

			@Override
			public void onPostExecute(final Void result) {
				/* Adapt the Adapter to the ListView. */
				SDSavedRouteChooser.this.mSavedRoutesList.setAdapter(sla);
				pd.dismiss();
			}
		}.execute();
	}

	protected void initListView() {
		this.mSavedRoutesList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
				SDSavedRouteChooser.this.mSelectedSavedRoute = (SavedRouteItem)parent.getAdapter().getItem(position);
				showDialog(DIALOG_SHOWSELECTEDROUTEOPTIONS_ID);
			}
		});
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static class SavedRouteItem implements Comparable<SavedRouteItem>, Parcelable {
		protected final String mFileName;

		private SavedRouteItem(final String pFileName) {
			this.mFileName = pFileName;
		}

		public int compareTo(final SavedRouteItem another) {
			final boolean thisFirstDigit = Character.isDigit(this.mFileName.charAt(0));
			final boolean otherFirstDigit = Character.isDigit(another.mFileName.charAt(0));

			/* Make Characters appear above of digits(timestamps). */
			if(thisFirstDigit && !otherFirstDigit) {
				return 1;
			} else if(!thisFirstDigit && otherFirstDigit) {
				return -1;
			} else if(thisFirstDigit && otherFirstDigit) {
				return -this.mFileName.compareToIgnoreCase(another.mFileName); /* Ascending. */
			} else {
				return this.mFileName.compareToIgnoreCase(another.mFileName); /* Descending. */
			}
		}

		// ===========================================================
		// Parcelable
		// ===========================================================

		public static final Parcelable.Creator<SavedRouteItem> CREATOR = new Parcelable.Creator<SavedRouteItem>() {
			public SavedRouteItem createFromParcel(final Parcel in) {
				return readFromParcel(in);
			}

			public SavedRouteItem[] newArray(final int size) {
				return new SavedRouteItem[size];
			}
		};

		public int describeContents() {
			return 0;
		}

		public void writeToParcel(final Parcel out, final int arg1) {
			out.writeString(this.mFileName);
		}

		private static SavedRouteItem readFromParcel(final Parcel in){
			return new SavedRouteItem(in.readString());
		}
	}

	private class SavedRouteListItemView extends LinearLayout{
		private final TextView mTVName;

		public SavedRouteListItemView(final Context context, final SavedRouteItem aPOIItem) {
			super(context);

			this.setOrientation(VERTICAL);

			this.mTVName = new TextView(context);
			this.mTVName.setText(aPOIItem.mFileName);
			this.mTVName.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
			this.mTVName.setPadding(10,0,20,0);

			addView(this.mTVName, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		}
	}

	private class SavedRouteListAdapter extends BaseAdapter implements FastScrollView.SectionIndexer {

		/** Remember our context so we can use it when constructing views. */
		private final Context mContext;

		private List<SavedRouteItem> mItems = new ArrayList<SavedRouteItem>();

		private String[] mAlphabet;

		public SavedRouteListAdapter(final Context context) {
			this.mContext = context;
			initAlphabet(context);
		}

		public void addItem(final SavedRouteItem it) {
			this.mItems.add(it);
			Collections.sort(this.mItems);
		}

		public void setListItems(final List<SavedRouteItem> lit) {
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
			SavedRouteListItemView btv;
			if (convertView == null) {
				btv = new SavedRouteListItemView(this.mContext, this.mItems.get(position));
			} else { // Reuse/Overwrite the View passed
				// We are assuming(!) that it is castable!
				btv = (SavedRouteListItemView) convertView;
				btv.mTVName.setText(this.mItems.get(position).mFileName);
			}
			return btv;
		}

		// ===========================================================
		// FastScrollView-Methods
		// ===========================================================

		public int getPositionForSection(final int section) {

			final String firstChar = this.mAlphabet[section];

			/* Find the index, of the firstchar within the Contact-Items */
			int position = Collections.binarySearch(this.mItems, new SavedRouteItem(firstChar));

			if(position < 0){
				/* Negative result means the insertion-point.
				 * See definition of Collections.binarySearch */
				position = -(position + 1);
			}

			return position;
		}

		public int getSectionForPosition(final int position) {
			return 0;
		}

		public Object[] getSections() {
			return this.mAlphabet;
		}

		private void initAlphabet(final Context context) {
			final String alphabetString = context.getResources().getString(R.string.alphabet); // TODO Use Systems Alphabet!
			this.mAlphabet = new String[alphabetString.length()];

			for (int i = 0; i < this.mAlphabet.length; i++) {
				this.mAlphabet[i] = String.valueOf(alphabetString.charAt(i));
			}
		}
	}
}
