// Created by plusminus on 15:15:39 - 12.04.2008
package org.andnav2.app;

import java.util.ArrayList;

import org.andnav2.R;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.ui.map.OpenStreetDDMap;
import org.andnav2.ui.map.WhereAmIMap;
import org.andnav2.util.constants.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class APIIntentReceiver extends BroadcastReceiver implements Constants{
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final int WHEREAMI_EXTRAS_MODE_VIEW_LOCATIONS = 0;
	public static final String WHEREAMI_EXTRAS_LOCATIONS_ID = "org.andnav2.whereami_extras_locations_id";
	public static final String WHEREAMI_EXTRAS_LOCATIONS_TITLES_ID = "org.andnav2.whereami_extras_locations_titles_id";
	public static final String WHEREAMI_EXTRAS_LOCATIONS_DESCRIPTIONS_ID = "org.andnav2.whereami_extras_locations_descriptions_id";

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
	public void onReceive(final Context ctx, final Intent i) {
		Log.d(Constants.DEBUGTAG, "NavIntent recieved. Raw: " + i.toString());
		final String action = i.getAction();
		if(action.equals(Constants.ANDNAV_NAV_ACTION) || action.equals(Constants.ANDNAV2_NAV_ACTION)){
			handleNavToIntent(ctx, i);
		}else if(action.equals(Constants.ANDNAV2_VIEW_ACTION)){
			handleViewIntent(ctx, i);
		}else if(action.equals(android.content.Intent.ACTION_VIEW)){
			final String scheme = i.getScheme();
			final Uri data = i.getData();
			if(scheme.equals("geo")){
				i.getData().getHost();
			}else if(scheme.equals("http") && data.getHost().equals("maps.google.com")){
				final String path = data.getPath();
				if(path.equals("/maps") || path.equals("")){
					final String start = data.getQueryParameter("saddr");
					final String destination = data.getQueryParameter("daddr");
					Toast.makeText(ctx, "start: " + start + "\nDestination: " + destination, Toast.LENGTH_LONG).show();
				}
			}
			//			TODO Weitermachen!
			//			// TODO wie in AndroidManifest.xml
			//			Toast.makeText(ctx, "TODO =) Parsing of Uri.", Toast.LENGTH_LONG).show();
		}
	}

	private void handleViewIntent(final Context ctx, final Intent i) {

		final ArrayList<GeoPoint> locationGeoPoints = new ArrayList<GeoPoint>();
		final ArrayList<String> locationTitles = new ArrayList<String>();
		final ArrayList<String> locationDescription = new ArrayList<String>();
		try {
			final Bundle bIn = i.getExtras();

			final ArrayList<String> locationStrings = bIn.getStringArrayList("locations");
			/* This validates the parsing string. */
			if(locationStrings != null){
				for (final String loc : locationStrings){
					final String[] parts = loc.split(";");

					final GeoPoint gp = GeoPoint.fromDoubleString(parts[0], ',');
					locationGeoPoints.add(gp);

					locationTitles.add((parts.length > 1) ? parts[1] : ctx.getString(R.string.coordinates));

					locationDescription.add((parts.length > 2) ? parts[2] : gp.toMultiLineUserString(ctx));
				}
			}

		} catch (final Exception e) {
			Toast.makeText(ctx, R.string.nav_intent_parse_error_message, Toast.LENGTH_LONG).show(); // TODO own toast
			return;
		}

		/* Parsing was correct. */
		final Intent mapIntent = new Intent(ctx, WhereAmIMap.class);
		mapIntent.setAction(ANDNAV2_VIEW_ACTION);
		mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		final Bundle bOut = new Bundle();

		bOut.putInt(EXTRAS_MODE, WHEREAMI_EXTRAS_MODE_VIEW_LOCATIONS);

		if(locationGeoPoints.size() > 0){
			final ArrayList<String> viasStringsE6 = new ArrayList<String>(locationGeoPoints.size());

			for(final GeoPoint g : locationGeoPoints) {
				viasStringsE6.add(g.toString());
			}

			bOut.putStringArrayList(WHEREAMI_EXTRAS_LOCATIONS_ID, viasStringsE6);
		}

		bOut.putStringArrayList(WHEREAMI_EXTRAS_LOCATIONS_TITLES_ID, locationTitles);
		bOut.putStringArrayList(WHEREAMI_EXTRAS_LOCATIONS_DESCRIPTIONS_ID, locationDescription);

		mapIntent.putExtras(bOut);
		ctx.startActivity(mapIntent);
	}

	private void handleNavToIntent(final Context ctx, final Intent i) {
		final GeoPoint start;
		final GeoPoint dest;
		final ArrayList<GeoPoint> viasGeoPoints = new ArrayList<GeoPoint>();

		final Bundle bIn = i.getExtras();

		final String fromString = bIn.getString("from");

		try {
			start = (fromString == null) ? null : GeoPoint.fromDoubleString(fromString, ',');

			dest = GeoPoint.fromDoubleString(bIn.getString("to"), ',');

			final ArrayList<String> viasStrings = bIn.getStringArrayList("via");
			/* This validates the parsing string. */
			if(viasStrings != null) {
				for (final String v : viasStrings) {
					viasGeoPoints.add(GeoPoint.fromDoubleString(v, ','));
				}
			}

		} catch (final Exception e) {
			Toast.makeText(ctx, R.string.nav_intent_parse_error_message, Toast.LENGTH_LONG).show();
			return;
		}

		/* Parsing was correct. */
		final Intent mapIntent = new Intent(ctx, OpenStreetDDMap.class);
		mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		final Bundle bOut = new Bundle();

		bOut.putInt(EXTRAS_MODE, EXTRAS_MODE_DIRECT_LATLNG);

		if(start != null){
			bOut.putInt(EXTRAS_START_LATITUDE_ID, start.getLatitudeE6());
			bOut.putInt(EXTRAS_START_LONGITUDE_ID, start.getLongitudeE6());
		}

		bOut.putInt(EXTRAS_DESTINATION_LATITUDE_ID, dest.getLatitudeE6());
		bOut.putInt(EXTRAS_DESTINATION_LONGITUDE_ID, dest.getLongitudeE6());

		if(viasGeoPoints.size() > 0){
			final ArrayList<String> viasStringsE6 = new ArrayList<String>(viasGeoPoints.size());

			for(final GeoPoint g : viasGeoPoints) {
				viasStringsE6.add(g.toString());
			}

			bOut.putStringArrayList(EXTRAS_VIAS_ID, viasStringsE6);
		}

		mapIntent.putExtras(bOut);
		ctx.startActivity(mapIntent);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
