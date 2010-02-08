// Created by plusminus on 13:23:07 - 07.02.2009
package org.andnav2.ui.osm.api.nodes;

import java.util.HashMap;

import org.andnav2.sys.ors.adt.ds.OSMRepresentation;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.ui.common.activities.BasePOICategorySelectionActivity;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;


public class POICategorySelector extends BasePOICategorySelectionActivity {
	// ===========================================================
	// Constants
	// ===========================================================

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
	protected OSMRepresentation onGetMustMatchOSMRepresentationFilter() {
		return OSMRepresentation.NODE;
	}

	@Override
	protected OnChildClickListener onCreateOnChildClickListener() {
		return new OnChildClickListener(){
			@SuppressWarnings("unchecked")
			public boolean onChildClick(final ExpandableListView parent, final View v, final int groupPosition, final int childPosition, final long id) {
				final HashMap<String, String> map = (HashMap<String, String>)POICategorySelector.this.mExpListAdapter.getChild(groupPosition, childPosition);
				final String poiTypeRawName = map.get(KEY_SUBTYPERAWNAME);
				final POIType pType = POIType.fromRawName(poiTypeRawName);

				POICategorySelector.this.setResult(pType.ordinal());
				POICategorySelector.this.finish();
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
