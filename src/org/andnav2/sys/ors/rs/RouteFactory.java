// Created by plusminus on 00:27:06 - 17.10.2008
package org.andnav2.sys.ors.rs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.aoi.AreaOfInterest;
import org.andnav2.sys.ors.adt.rs.DirectionsLanguage;
import org.andnav2.sys.ors.adt.rs.Route;
import org.andnav2.sys.ors.adt.rs.RoutePreferenceType;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.util.constants.Constants;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;


public class RouteFactory implements Constants{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static Route create(final Context ctx, final long pRouteHandle) throws ORSException, Exception{
		final DirectionsLanguage nat = Preferences.getDrivingDirectionsLanguage(ctx);
		try {
			final Route route = RSRequester.request(ctx, nat, pRouteHandle);

			return route;
		} catch(final ORSException e){
			throw e;
		} catch (final MalformedURLException e) {
			Log.e(DEBUGTAG, "Error", e);
			throw new Exception(e);
		} catch (final IOException e) {
			Log.e(DEBUGTAG, "Error", e);
			throw new Exception(e);
		} catch (final SAXException e) {
			Log.e(DEBUGTAG, "Error", e);
			throw new Exception(e);
		}
	}

	public static Route create(final Context ctx, final GeoPoint start, final GeoPoint end, final ArrayList<GeoPoint> vias, final ArrayList<AreaOfInterest> pAvoidAreas, final boolean pSaveRoute) throws ORSException, Exception{
		final DirectionsLanguage nat = Preferences.getDrivingDirectionsLanguage(ctx);
		final boolean pAvoidHighways = Preferences.getAvoidHighways(ctx);
		final boolean pAvoidTolls = Preferences.getAvoidTolls(ctx);
		final boolean requestHandle = true;
		final RoutePreferenceType pRoutePreference = Preferences.getRoutePreferenceType(ctx);
		try {
			final Route route = RSRequester.request(ctx, nat, start, vias, end, pRoutePreference, true, pAvoidTolls, pAvoidHighways, requestHandle, pAvoidAreas, pSaveRoute);

			route.getVias().addAll(vias);

			return route;
		} catch(final ORSException e){
			throw e;
		} catch (final MalformedURLException e) {
			Log.e(DEBUGTAG, "Error", e);
			throw new Exception(e);
		} catch (final IOException e) {
			Log.e(DEBUGTAG, "Error", e);
			throw new Exception(e);
		} catch (final SAXException e) {
			Log.e(DEBUGTAG, "Error", e);
			throw new Exception(e);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
