// Created by plusminus on 18:49:50 - 15.11.2008
package org.andnav2.sys.ors.adt.ds;

import org.andnav2.R;

public enum POIGroup {
	UNKNOWN("_unknown_", R.string.unknown),

	MAINGROUP("_maingroup_", R.string.sd_poi_category_maingroup),

	MOSTUSED("mostused", R.string.sd_poi_category_mostused),
	EMERGENCY("emergency", R.string.sd_poi_category_emergency),
	FOOD("food", R.string.sd_poi_category_food),
	LEISURE("leisure", R.string.sd_poi_category_leisure),
	LOCALITY("locality", R.string.sd_poi_category_locality),
	TRANSPORT("transport", R.string.sd_poi_category_transport),
	PUBLIC_TRANSPORT("public_transport", R.string.sd_poi_category_public_transport),
	SHOP("shop", R.string.sd_poi_category_shop),
	TOURISM("tourism", R.string.sd_poi_category_tourism);

	public final String RAWNAME;
	public final int READABLENAMERESID;

	private POIGroup(final String pRawName, final int pReadableNameResID){
		this.RAWNAME = pRawName;
		this.READABLENAMERESID = pReadableNameResID;
	}
}
