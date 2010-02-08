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
import org.andnav2.ui.common.CommonDialogFactory.CreateSavedTraceSelectionOptions;
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


public class SDSavedTraceChooser extends AndNavBaseActivity implements OSMConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final int REQUESTCODE_DDMAP = 0x1337;
	protected static String EXTERNAL_STORAGE_BASEDIRECTORY;

	protected static final int DIALOG_SHOWSELECTEDTRACEOPTIONS_ID = 0;
	protected static final int DIALOG_RENAMESELECTEDTRACE_ID = DIALOG_SHOWSELECTEDTRACEOPTIONS_ID + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Bundle bundleCreatedWith;
	protected ListView mSavedTracesList;
	protected SavedTraceItem mSelectedSavedTrace;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle, true); // We need DataState-Info
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.sd_savedtracechooser);

		this.bundleCreatedWith = this.getIntent().getExtras();

		this.mSavedTracesList = (ListView)this.findViewById(R.id.list_savedtraces);

		/* Set empty view for the list. */
		final TextView empty = new TextView(this);
		empty.setText(R.string.list_empty);
		this.mSavedTracesList.setEmptyView(empty);

		if(!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			/* Should not happen as the activity is not selectable in SDMainChoose, when no external media is inserted. */
		}else{
			EXTERNAL_STORAGE_BASEDIRECTORY = Util.getAndNavExternalStoragePath();

			// Ensure the traces-directory exists.
			new File(EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_SAVEDTRACES_PATH).mkdirs();

			initListView();

			updateTraceListItems();
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
			case DIALOG_RENAMESELECTEDTRACE_ID:
				return CommonDialogFactory.createInputDialog(this, R.string.dlg_sd_savedtracechooser_contextmenu_rename_hint, new CommonCallbackAdapter<String>(){
					@Override
					public void onSuccess(final String result) {
						final String oldFilename = SDSavedTraceChooser.this.mSelectedSavedTrace.mFileName;

						if(result != null){
							try{
								final boolean success = new File(EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_SAVEDTRACES_PATH + oldFilename).renameTo(new File(EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_SAVEDTRACES_PATH + result));

								if(success){
									updateTraceListItems();
									Toast.makeText(SDSavedTraceChooser.this, R.string.dlg_sd_savedtracechooser_contextmenu_rename_success, Toast.LENGTH_LONG).show();
								}else{
									Toast.makeText(SDSavedTraceChooser.this, R.string.dlg_sd_savedtracechooser_contextmenu_rename_fail, Toast.LENGTH_LONG).show();
								}
							}catch(final Throwable t){
								Toast.makeText(SDSavedTraceChooser.this, R.string.dlg_sd_savedtracechooser_contextmenu_rename_fail, Toast.LENGTH_LONG).show();
							}
						}else{
							Toast.makeText(SDSavedTraceChooser.this, R.string.dlg_sd_savedtracechooser_contextmenu_rename_fail, Toast.LENGTH_LONG).show();
						}
					}
				});
			case DIALOG_SHOWSELECTEDTRACEOPTIONS_ID:
				return CommonDialogFactory.createSavedTraceSelectionOptions(this, new CommonCallbackAdapter<CreateSavedTraceSelectionOptions>(){
					@Override
					public void onSuccess(final CreateSavedTraceSelectionOptions result) {
						final String filename = SDSavedTraceChooser.this.mSelectedSavedTrace.mFileName;

						switch(result){
							case SHARE_TRAILMAPPING:

								/* TODO Trace-Sharing */
								break;
							case INFORMATION:
								/* TODO Extract Information from Trace, like number of points, etc... */
								break;
							case RENAME:
								showDialog(DIALOG_RENAMESELECTEDTRACE_ID);
								break;
							case USE:
								/* Proceed to next screen. */
								// TODO advance to WhereAmI
								final int mode = SDSavedTraceChooser.this.bundleCreatedWith.getInt(MODE_SD);
								switch(mode){
									case MODE_SD_DESTINATION:
										final Intent directIntent = new Intent(SDSavedTraceChooser.this, OpenStreetDDMap.class);
										final Bundle b = new Bundle();
										b.putInt(EXTRAS_MODE, EXTRAS_MODE_LOAD_SAVED_ROUTE);

										b.putString(EXTRAS_SAVED_ROUTE_FILENAME_ID, filename);

										directIntent.putExtras(b);
										SDSavedTraceChooser.this.startActivityForResult(directIntent, REQUESTCODE_DDMAP);
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

	private static final String STATE_SELECTEDSAVEDTRACE_ID = "state_selectedsavedtrace_id";

	@Override
	protected void onSaveInstanceState(final Bundle icicle) {
		super.onSaveInstanceState(icicle);
		icicle.putParcelable(STATE_SELECTEDSAVEDTRACE_ID, this.mSelectedSavedTrace);
	}

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		this.mSelectedSavedTrace = savedInstanceState.getParcelable(STATE_SELECTEDSAVEDTRACE_ID);
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
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_savedtracechooser_back)) {
			@Override
			public void onClicked(final View me) {
				if (SDSavedTraceChooser.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDSavedTraceChooser.this, R.raw.close).start();
				}

				/* Back one level. */
				SDSavedTraceChooser.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDSavedTraceChooser.this.finish();
			}
		};

		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_savedtracechooser_close)) {
			@Override
			public void onClicked(final View me) {
				if (SDSavedTraceChooser.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDSavedTraceChooser.this, R.raw.close).start();
				}
				/*
				 * Set ResultCode that the calling activity knows that we want
				 * to go back to the Base-Menu
				 */
				SDSavedTraceChooser.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDSavedTraceChooser.this.finish();
			}
		};
	}

	private void updateTraceListItems() {
		final ArrayList<SavedTraceItem> savedTraceItems = new ArrayList<SavedTraceItem>();

		final SavedTraceListAdapter sla = new SavedTraceListAdapter(SDSavedTraceChooser.this);

		final ProgressDialog pd = ProgressDialog.show(this, getString(R.string.dlg_sd_savedtracechooser_loading_title), getString(R.string.please_wait_a_moment), false); // TODO Make determinate, when SDK supports this.

		final String progressBaseString = getString(R.string.dlg_sd_savedtracechooser_loading_progress);

		new UserTask<Void, Integer, Void>(){
			@Override
			public Void doInBackground(final Void... params) {
				try{
					final File sdRoot = new File(EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_SAVEDTRACES_PATH);
					final String[] traceFiles = sdRoot.list();
					if (traceFiles != null) {
						final int routeCount = traceFiles.length;
						for (int i = 0; i < routeCount; i++) {
							savedTraceItems.add(new SavedTraceItem(traceFiles[i]));
							publishProgress(i, routeCount);
						}
					}

					/* Adapt the list to the Adapter. */
					sla.setListItems(savedTraceItems);/* Orders by name, ascending. */
				}catch(final Exception e){
					Exceptor.e("SavedTraceChooser-Exception", e, SDSavedTraceChooser.this);
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
				SDSavedTraceChooser.this.mSavedTracesList.setAdapter(sla);
				pd.dismiss();
			}
		}.execute();
	}

	protected void initListView() {
		this.mSavedTracesList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
				SDSavedTraceChooser.this.mSelectedSavedTrace = (SavedTraceItem)parent.getAdapter().getItem(position);
				showDialog(DIALOG_SHOWSELECTEDTRACEOPTIONS_ID);
			}
		});
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static class SavedTraceItem implements Comparable<SavedTraceItem>, Parcelable {
		protected final String mFileName;

		private SavedTraceItem(final String pFileName) {
			this.mFileName = pFileName;
		}

		public int compareTo(final SavedTraceItem another) {
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

		public static final Parcelable.Creator<SavedTraceItem> CREATOR = new Parcelable.Creator<SavedTraceItem>() {
			public SavedTraceItem createFromParcel(final Parcel in) {
				return readFromParcel(in);
			}

			public SavedTraceItem[] newArray(final int size) {
				return new SavedTraceItem[size];
			}
		};

		public int describeContents() {
			return 0;
		}

		public void writeToParcel(final Parcel out, final int arg1) {
			out.writeString(this.mFileName);
		}

		private static SavedTraceItem readFromParcel(final Parcel in){
			return new SavedTraceItem(in.readString());
		}
	}

	private class SavedTraceListItemView extends LinearLayout{
		private final TextView mTVName;

		public SavedTraceListItemView(final Context context, final SavedTraceItem aPOIItem) {
			super(context);

			this.setOrientation(VERTICAL);

			this.mTVName = new TextView(context);
			this.mTVName.setText(aPOIItem.mFileName);
			this.mTVName.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
			this.mTVName.setPadding(10,0,20,0);

			addView(this.mTVName, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		}
	}

	private class SavedTraceListAdapter extends BaseAdapter implements FastScrollView.SectionIndexer {

		/** Remember our context so we can use it when constructing views. */
		private final Context mContext;

		private List<SavedTraceItem> mItems = new ArrayList<SavedTraceItem>();

		private String[] mAlphabet;

		public SavedTraceListAdapter(final Context context) {
			this.mContext = context;
			initAlphabet(context);
		}

		public void addItem(final SavedTraceItem it) {
			this.mItems.add(it);
			Collections.sort(this.mItems);
		}

		public void setListItems(final List<SavedTraceItem> lit) {
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
			SavedTraceListItemView btv;
			if (convertView == null) {
				btv = new SavedTraceListItemView(this.mContext, this.mItems.get(position));
			} else { // Reuse/Overwrite the View passed
				// We are assuming(!) that it is castable!
				btv = (SavedTraceListItemView) convertView;
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
			int position = Collections.binarySearch(this.mItems, new SavedTraceItem(firstChar));

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
