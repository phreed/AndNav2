// Created by plusminus on 14:12:34 - 16.11.2008
package org.andnav2.ui.common.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.ds.OSMRepresentation;
import org.andnav2.sys.ors.adt.ds.POIGroup;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.ExpandableListView.OnChildClickListener;

public abstract class BasePOICategorySelectionActivity extends AndNavBaseActivity {

	// ===========================================================
	// Constants
	// ===========================================================

	/* REQUEST-CODES for SubActivities. */
	protected static final int REQUESTCODE_SD_POISEARCHLIST = 0;

	protected static final String KEY_GROUPREADABLENAME = "groupreadablename";
	protected static final String KEY_SUBTYPEEADABLENAME = "subtypereadablename";
	protected static final String KEY_SUBTYPERAWNAME = "subtyperawname";

	// ===========================================================
	// Fields
	// ===========================================================

	protected ExpandableListView mListView;
	protected SimpleExpandableListAdapter mExpListAdapter;
	protected Bundle bundleCreatedWith;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.sd_poi_categories);
		Preferences.applySharedSettings(this);

		/* Save the Extras Bundle of the Intent this Activity
		 * was created with, because it contains the Information,
		 * that will finally be used for a GeoCode API. */
		this.bundleCreatedWith = this.getIntent().getExtras();

		this.mExpListAdapter = new SimpleExpandableListAdapter(this,
				createGroupList(),
				R.layout.sd_poi_categories_group_row,
				new String[] { KEY_GROUPREADABLENAME },
				new int[] { R.id.groupname },
				createChildList(),
				R.layout.sd_poi_categories_child_row,
				new String[] { KEY_SUBTYPEEADABLENAME },
				new int[] { R.id.childname }
		);

		this.mListView = (ExpandableListView)this.findViewById(R.id.elv_sd_poi_categories_items);
		this.mListView.setAdapter(this.mExpListAdapter);

		this.mListView.setOnChildClickListener(onCreateOnChildClickListener());

		applyTopMenuButtonListeners();
	}

	/**
	 * The resulting List contains Maps.
	 * Each Map contains one entry with key-value pair.
	 */
	private List<HashMap<String, String>> createGroupList() {
		final List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

		final POIGroup[] poiGroups = POIGroup.values();
		for (final POIGroup g : poiGroups) {
			switch (g) {
				case MAINGROUP:
				case UNKNOWN:
					continue;
			}

			final HashMap<String, String> m = new HashMap<String, String>();
			m.put(KEY_GROUPREADABLENAME, getString(g.READABLENAMERESID));
			result.add(m);
		}
		return result;
	}

	/**
	 * The resulting List contains one list for each group.
	 * Each such second-level group contains Maps.
	 * Each such Map contains a key-value pair.
	 */
	private List<List<HashMap<String, String>>> createChildList() {
		final POIGroup[] poiGroups = POIGroup.values();

		final List<List<HashMap<String, String>>> result = new ArrayList<List<HashMap<String, String>>>();
		for (final POIGroup g : poiGroups) {
			switch (g) {
				case MAINGROUP:
				case UNKNOWN:
					continue;
			}

			final ArrayList<POIType> subTypes = POIType.getAllOfGroup(g, onGetMustMatchOSMRepresentationFilter());
			// Second-level lists
			final List<HashMap<String, String>> secList = new ArrayList<HashMap<String, String>>();
			for (final POIType t : subTypes) {
				final HashMap<String, String> child = new HashMap<String, String>();
				child.put(KEY_SUBTYPEEADABLENAME, getString(t.READABLENAMERESID));
				child.put(KEY_SUBTYPERAWNAME, t.RAWNAME);
				secList.add(child);
			}
			result.add(secList);
		}
		return result;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from/for SuperClass/Interfaces
	// ===========================================================


	protected abstract OnChildClickListener onCreateOnChildClickListener();
	protected abstract OSMRepresentation onGetMustMatchOSMRepresentationFilter();

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

	protected void applyTopMenuButtonListeners() {
		/* Set Listener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_poi_categories_back)) {
			@Override
			public void onClicked(final View me) {
				if (BasePOICategorySelectionActivity.super.mMenuVoiceEnabled) {
					MediaPlayer.create(BasePOICategorySelectionActivity.this, R.raw.close).start();
				}

				/* Back one level. */
				BasePOICategorySelectionActivity.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				BasePOICategorySelectionActivity.this.finish();
			}
		};

		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_poi_categories_close)) {
			@Override
			public void onClicked(final View me) {
				if (BasePOICategorySelectionActivity.super.mMenuVoiceEnabled) {
					MediaPlayer.create(BasePOICategorySelectionActivity.this, R.raw.close).start();
				}
				/*
				 * Set ResultCode that the calling activity knows that we want
				 * to go back to the Base-Menu
				 */
				BasePOICategorySelectionActivity.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				BasePOICategorySelectionActivity.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
