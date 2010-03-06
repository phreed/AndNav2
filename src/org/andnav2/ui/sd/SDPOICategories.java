// Created by plusminus on 14:12:34 - 16.11.2008
package org.andnav2.ui.sd;

import java.util.HashMap;

import org.andnav2.R;
import org.andnav2.adt.UnitSystem;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.ds.OSMRepresentation;
import org.andnav2.sys.ors.adt.ds.POIGroup;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.ui.common.CommonDialogFactory;
import org.andnav2.ui.common.activities.BasePOICategorySelectionActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class SDPOICategories extends BasePOICategorySelectionActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int DIALOG_NOTINLITEVERSION = 0;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected Dialog onCreateDialog(final int id) {
		switch(id){
			case DIALOG_NOTINLITEVERSION:
				return CommonDialogFactory.createNotInLiteVersionDialog(this);
			default:
				return null;
		}
	}

	@Override
	protected OSMRepresentation onGetMustMatchOSMRepresentationFilter() {
		return null;
	}

	@Override
	protected OnChildClickListener onCreateOnChildClickListener() {
		return new OnChildClickListener(){
			@SuppressWarnings("unchecked")
			public boolean onChildClick(final ExpandableListView parent, final View v, final int groupPosition, final int childPosition, final long id) {
				final HashMap<String, String> map = (HashMap<String, String>)SDPOICategories.this.mExpListAdapter.getChild(groupPosition, childPosition);
				final String poiTypeRawName = map.get(KEY_SUBTYPERAWNAME);


				/* LITEVERSION */
				final POIType p = POIType.fromRawName(poiTypeRawName);
				if(LITEVERSION && !p.isInGroup(POIGroup.MOSTUSED)){
					showDialog(DIALOG_NOTINLITEVERSION);
					return true;
				}


				final UnitSystem us = Preferences.getUnitSystem(SDPOICategories.this);

				final int[] valDist;
				switch(us){
					case IMPERIAL:
						valDist = getResources().getIntArray(R.array.poi_search_radius_ors_imperial);
						break;
					case METRIC:
					default:
						valDist = getResources().getIntArray(R.array.poi_search_radius_ors_metric);
				}

				final String[] valStr = new String[valDist.length];

				for (int i = 0; i < valDist.length; i++){
					final int cur = valDist[i];
					if(cur == SDPOISearchList.POISEARCH_RADIUS_GLOBAL){
						valStr[i] = getString(R.string.poi_search_radius_global);
					}else{
						final String[] distStringParts = us.getDistanceString(cur, null);
						valStr[i] = distStringParts[UnitSystem.DISTSTRINGS_DIST_ID] + distStringParts[UnitSystem.DISTSTRINGS_UNIT_ID];
					}
				}

				new AlertDialog.Builder(SDPOICategories.this)
				.setTitle(R.string.choose_search_radius)
				.setSingleChoiceItems(valStr, 3, new DialogInterface.OnClickListener(){
					public void onClick(final DialogInterface d, final int which) {
						d.dismiss();

						final Intent favIntent = new Intent(SDPOICategories.this, SDPOISearchList.class);
						SDPOICategories.this.bundleCreatedWith.putInt(SDPOISearchList.EXTRAS_POISEARCH_MODE, SDPOISearchList.EXTRAS_POISEARCH_MODE_ORS_CATEGORY_SEARCH);
						SDPOICategories.this.bundleCreatedWith.putInt(SDPOISearchList.EXTRAS_POISEARCH_RADIUS, valDist[which]);
						SDPOICategories.this.bundleCreatedWith.putString(SDPOISearchList.EXTRAS_POISEARCH_CATEGORY, poiTypeRawName);
						favIntent.putExtras(SDPOICategories.this.bundleCreatedWith);
						SDPOICategories.this.startActivityForResult(favIntent, REQUESTCODE_SD_POISEARCHLIST);
					}
				}).create().show();

				return true;
			}
		};
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
