// Created by plusminus on 19:07:24 - 16.02.2008
package org.andnav2.preferences;

import java.util.HashMap;

import org.andnav2.R;
import org.andnav2.adt.UnitSystem;
import org.andnav2.adt.keyboardlayouts.AbstractKeyBoardLayout;
import org.andnav2.adt.keyboardlayouts.KeyBoardLayoutImpls;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.overlay.util.DirectionArrowDescriptor;
import org.andnav2.osm.views.tiles.OSMMapTileProviderInfo;
import org.andnav2.osm.views.tiles.caching.OSMMapTileFilesystemCache;
import org.andnav2.osm.views.tiles.caching.OSMMapTileFilesystemCache.StoragePolicy;
import org.andnav2.sys.ors.adt.ORSServer;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.ors.adt.lus.CountrySubdivisionRegistry;
import org.andnav2.sys.ors.adt.lus.ICountrySubdivision;
import org.andnav2.sys.ors.adt.rs.DirectionsLanguage;
import org.andnav2.sys.ors.adt.rs.RoutePreferenceType;
import org.andnav2.ui.map.hud.HUDRegistry;
import org.andnav2.ui.map.hud.IHUDImpl;
import org.andnav2.ui.map.hud.IHUDImplVariation;
import org.andnav2.ui.map.hud.impl.basic.BasicHUDImpl;
import org.andnav2.util.Base64;
import org.andnav2.util.constants.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import com.skyhookwireless.wps.WPSAuthentication;


public class Preferences implements Constants, PreferenceConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static SharedPreferences mINSTANCE;
	private static Editor mEDITORINSTANCE;

	// ===========================================================
	// Methods
	// ===========================================================

	private static SharedPreferences getInstance(final Context ctx){
		if(mINSTANCE == null) {
			mINSTANCE = ctx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		}
		return mINSTANCE;
	}

	private static Editor getEditorInstance(final Context ctx){
		if(mEDITORINSTANCE == null) {
			mEDITORINSTANCE = getInstance(ctx).edit();
		}
		return mEDITORINSTANCE;
	}

	// ===========================================================
	// Most Recently Used Country
	// ===========================================================

	/** Returns the Most Recent Used Country saved in Preferences to the Activity parameter.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static Country getSDCountryMostRecentUsed(final Context ctx){
		return Country.fromAbbreviation(getInstance(ctx).getString(PREF_MOSTRECENTUSED_COUNTRY_ID, null));
	}

	/** Save the Most Recent Used Country to be used in SD Country.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aNation Nationality to be saved and used by the whole application. */
	public static void saveSDCountryMostRecentUsed(final Context ctx, final Country aNation){
		getEditorInstance(ctx).putString(PREF_MOSTRECENTUSED_COUNTRY_ID, aNation.COUNTRYCODE).commit();
	}

	// ===========================================================
	// Most Recently Used CountrySubdivision
	// ===========================================================

	public static void saveSDCountrySubdivisionMostRecentUsed(final Context ctx, final Country pCountry, final ICountrySubdivision pCountrySubdivision) {
		if(pCountrySubdivision == null) {
			return;
		}

		getEditorInstance(ctx).putString(PREF_MOSTRECENTUSED_COUNTRYSUBDIVISION_ID + pCountry.name(), pCountrySubdivision.uid()).commit();
	}

	public static ICountrySubdivision getSDCountrySubdivisionMostRecentUsed(final Context ctx, final Country pCountry) {
		if(pCountry == null) {
			return null;
		}

		final ICountrySubdivision[] subdivisions = CountrySubdivisionRegistry.get(pCountry);
		final String subdivisionName = getInstance(ctx).getString(PREF_MOSTRECENTUSED_COUNTRYSUBDIVISION_ID + pCountry.name(), null);
		if(subdivisionName != null){
			for (int i = subdivisions.length - 1; i >= 0; i--) {
				final ICountrySubdivision countrySubdivision = subdivisions[i];
				if(subdivisionName.equals(countrySubdivision.uid())) {
					return countrySubdivision;
				}
			}
		}
		return null;
	}

	// ===========================================================
	// Theme shared throughout application
	// ===========================================================

	/** Applies the theme saved in Preferences to the Activity parameter.
	 * @param ctx Activity-Context to set the shared theme to. */
	public static void applySharedSettings(final Activity act){
		//		final int themeID = getPreferences(ctx).getInt(PREF_THEME_RESID_ID, THEME_DEFAULT_RESID);
		//		ctx.setTheme(themeID);
		// TODO Enable Themes back when white-on-white issues are solved in dialogs
	}

	/** Returns the theme saved in Preferences to the Activity parameter.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static int getSharedThemeID(final Context ctx){
		return getInstance(ctx).getInt(PREF_THEME_RESID_ID, PREF_THEME_DEFAULT_RESID);
	}

	/** Save a sharedThemeID to be used by the whole application.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aThemeID ThemeID to be saved and used by the whole application. */
	public static void saveSharedThemeID(final Context ctx, final int aThemeID){
		getEditorInstance(ctx).putInt(PREF_THEME_RESID_ID, aThemeID).commit();
	}

	// ===========================================================
	// ShowMap in Titlebar
	// ===========================================================

	/** Returns whether the TitleBar should be shown in Preferences to the Activity parameter.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static boolean getShowTitleBarInMap(final Context ctx){
		return getInstance(ctx).getBoolean(PREF_SHOWTITLEBARINMAP_ID, PREF_SHOWINTITLEBAR_DEFAULT);
	}

	/** Save the setting whether the TitleBar should be shown by the calling application.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aShowIt to be saved and used by the whole application. */
	public static void saveShowTitleBarInMap(final Context ctx, final boolean aShowIt){
		getEditorInstance(ctx).putBoolean(PREF_SHOWTITLEBARINMAP_ID, aShowIt).commit();
	}

	// ===========================================================
	// centermode
	// ===========================================================

	/** Returns the centermode saved in Preferences to the Activity parameter.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static int getCenterMode(final Context ctx){
		return getInstance(ctx).getInt(PREF_CENTERMODE_ID, PREF_CENTERMODE_DEFAULT);
	}

	/** Save a centermode to be used by the whole application.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aCenterMode centermode to be saved and used by the whole application. */
	public static void saveCenterMode(final Context ctx, final int aCenterMode){
		getEditorInstance(ctx).putInt(PREF_CENTERMODE_ID, aCenterMode).commit();
	}

	// ===========================================================
	// Displayquality
	// ===========================================================

	/** Returns the DisplayQuality saved in Preferences to the Activity parameter.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static int getDisplayQuality(final Context ctx){
		return getInstance(ctx).getInt(PREF_DISPLAYQUALITY_ID, PREF_DISPLAYQUALITY_DEFAULT);
	}

	/** Save a DisplayQuality to be used by the whole application.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aDisplayQuality DisplayQuality to be saved and used by the whole application. */
	public static void saveDisplayQuality(final Context ctx, final int aDisplayQuality){
		getEditorInstance(ctx).putInt(PREF_DISPLAYQUALITY_ID, aDisplayQuality).commit();
	}

	// ===========================================================
	// DrivingDirectionsLanguage
	// ===========================================================

	/** Returns the DrivingDirectionsLanguage saved in Preferences to the Activity parameter.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static DirectionsLanguage getDrivingDirectionsLanguage(final Context ctx){
		return DirectionsLanguage.fromAbbreviation(getInstance(ctx).getString(PREF_DRIVINGDIRECTIONSLANGUAGE_ID, PREF_DRIVINGDIRECTIONSLANGUAGE_DEFAULT));
	}

	/** Save a DrivingDirectionsLanguage to be used by the whole application.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param nat Nationality to be saved and used by the whole application. */
	public static void saveDrivingDirectionsLanguage(final Context ctx, final DirectionsLanguage nat){
		final boolean isDialect = nat.NAMERESID == R.string.dialect_none;
		if(isDialect && Constants.LITEVERSION){
			Toast.makeText(ctx, R.string.toast_get_pro_version, Toast.LENGTH_SHORT).show();
		}else{
			getEditorInstance(ctx).putString(PREF_DRIVINGDIRECTIONSLANGUAGE_ID, nat.ID).commit();
		}
	}

	// ===========================================================
	// UnitSystem
	// ===========================================================

	/** Returns the UnitSystem saved in Preferences to the Activity parameter.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static UnitSystem getUnitSystem(final Context ctx){
		return UnitSystem.fromAbbreviation(getInstance(ctx).getString(PREF_UNITSYSTEM_ID, PREF_UNITSYSTEM_DEFAULT));
	}

	/** Save a UnitSystem to be used by the whole application.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param ds UnitSystem to be saved and used by the whole application. */
	public static void saveUnitSystem(final Context ctx, final UnitSystem ds){
		getEditorInstance(ctx).putString(PREF_UNITSYSTEM_ID, ds.mAbbreviation).commit();
	}

	// ===========================================================
	// Rotatemode
	// ===========================================================

	/** Returns the RotateMode saved in Preferences to the Activity parameter.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static int getRotateMode(final Context ctx){
		return getInstance(ctx).getInt(PREF_ROTATEMODE_ID, PREF_ROTATEMODE_DEFAULT);
	}

	/** Save a RotateMode to be used by the whole application.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aRotateMode RotateMode to be saved and used by the whole application. */
	public static void saveRotateMode(final Context ctx, final int aRotateMode){
		getEditorInstance(ctx).putInt(PREF_ROTATEMODE_ID, aRotateMode).commit();
	}

	// ===========================================================
	// KeyboardLayout
	// ===========================================================

	/** Returns the KeyBoardLayout saved in Preferences to the Activity parameter.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static AbstractKeyBoardLayout getKeyboardLayout(final Context ctx){
		switch(getInstance(ctx).getInt(PREF_KEYBOARDLAYOUT_ID, PREF_KEYBOARDLAYOUT_DEFAULT)){
			case PREF_KEYBOARDLAYOUT_ABCDEF:
				return new KeyBoardLayoutImpls.ABCKeyboardLayout();
			case PREF_KEYBOARDLAYOUT_QWERTY:
				return new KeyBoardLayoutImpls.QWERTYKeyBoardLayout();
			case PREF_KEYBOARDLAYOUT_QWERTZ:
				return new KeyBoardLayoutImpls.QWERTZKeyBoardLayout();
			case PREF_KEYBOARDLAYOUT_CYRILLIC:
				return new KeyBoardLayoutImpls.CyrillicKeyBoardLayout();
			default:
				throw new UnsupportedOperationException();
		}
	}

	/** Save a KeyBoardLayout to be used by the whole application.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param akbl AbstractKeyBoardLayout to be saved and used by the whole application. */
	public static void saveKeyboardLayout(final Context ctx, final AbstractKeyBoardLayout akbl){
		getEditorInstance(ctx).putInt(PREF_KEYBOARDLAYOUT_ID, akbl.getID()).commit();
	}

	// ===========================================================
	// HomeLocation
	// ===========================================================

	/** Returns the HomeLocation as MapPoint saved in Preferences to the Activity parameter.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @return the Home-Location as a GeoPoint or <code>null</code> if non was set.*/
	public static GeoPoint getHomeGeoPoint(final Context ctx){
		final String s = getInstance(ctx).getString(PREF_HOMEGEOPOINT_ID, PREF_HOMEGEOPOINT_DEFAULT);
		if(s == null) {
			return null;
		}

		final String[] coordinates = s.split(",");
		final int lat = Integer.parseInt(coordinates[LAT_INDEX]);
		final int lon = Integer.parseInt(coordinates[LON_INDEX]);

		return new GeoPoint(lat, lon);
	}

	/** Save a MapPoint to be used as the home-location.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param gp MapPoint to be saved and to be used as the home-location. */
	public static void saveHomeGeoPoint(final Context ctx, final GeoPoint gp){
		getEditorInstance(ctx).putString(PREF_HOMEGEOPOINT_ID, gp.toString()).commit();
	}

	// ===========================================================
	// MenuVoice
	// ===========================================================

	/** Returns whether MenuVoice-Help in all menus is enabled in Preferences to the Activity parameter.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static boolean getMenuVoiceEnabled(final Context ctx){
		return getInstance(ctx).getBoolean(PREF_MENUVOICE_ID, PREF_MENUVOICE_DEFAULT);
	}

	/** Save the setting whether the MenuVoice-Help should be enabled by the calling application.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aEnabledIt to be saved and used by the whole application. */
	public static void saveMenuVoiceEnabled(final Context ctx, final boolean aEnabledIt){
		getEditorInstance(ctx).putBoolean(PREF_MENUVOICE_ID, aEnabledIt).commit();
	}

	// ===========================================================
	// Statistics
	// ===========================================================

	/** Returns whether DirectionVoice-Help in all menus is enabled in Preferences to the Activity parameter.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static boolean getStatisticsEnabled(final Context ctx){
		return getInstance(ctx).getBoolean(PREF_STATISTICS_ID, PREF_STATISTICS_DEFAULT);
	}

	/** Save the setting whether the DirectionVoice-Help should be enabled by the calling application.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aEnabledIt to be saved and used by the whole application. */
	public static void saveStatisticsEnabled(final Context ctx, final boolean aEnabledIt){
		getEditorInstance(ctx).putBoolean(PREF_STATISTICS_ID, aEnabledIt).commit();
	}

	/** Returns the sessionstart-Timestamp in milliseconds.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static long getStatisticsSessionStart(final Context ctx){
		return getInstance(ctx).getLong(PREF_STATISTICS_SESSIONSTART_ID, System.currentTimeMillis());
	}

	/** End session-Statistics. Should be called when destroying Nav-Activity / Statistics-Manager!
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static void startStatisticsSession(final Context ctx){
		final boolean isFreshRestart = getStatisticsSessionStart(ctx) == NOT_SET;
		final Editor e = getEditorInstance(ctx);
		if(isFreshRestart) {
			e.putLong(PREF_STATISTICS_SESSIONSTART_ID, System.currentTimeMillis());
		}
		e.putLong(PREF_STATISTICS_SESSIONEND_ID, NOT_SET);
		e.commit();
	}

	/** Cleanup session-Statistics. Should be called when starting AndNav at all!
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static void cleanStatisticsSession(final Context ctx){
		final Editor e = getEditorInstance(ctx);
		e.putLong(PREF_STATISTICS_SESSIONSTART_ID, NOT_SET);
		e.putLong(PREF_STATISTICS_SESSIONEND_ID, NOT_SET);
		e.putInt(PREF_STATISTICS_SESSIONMAXSPEED_ID, 0);
		e.putLong(PREF_STATISTICS_SESSIONMETERS_ID, 0);
		e.commit();
	}

	/** End session-Statistics. Should be called when destroying Nav-Activity / Statistics-Manager!
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static void endStatisticsSession(final Context ctx){
		updateStatisticsMaxSpeedBeforeSession(ctx, getStatisticsMaxSpeedSession(ctx));
		final long drivenOverall = getStatisticsMetersDrivenOverall(ctx);
		final long secondsOverall = getStatisticsSecondsDrivenOverall(ctx);

		Log.d(DEBUGTAG, "endStatisticsSession();");
		final Editor e = getEditorInstance(ctx);
		e.putLong(PREF_STATISTICS_SESSIONSTART_ID, NOT_SET);
		e.putLong(PREF_STATISTICS_SESSIONEND_ID, NOT_SET);
		e.putLong(PREF_STATISTICS_SESSIONMETERS_ID, 0);
		e.putInt(PREF_STATISTICS_SESSIONMAXSPEED_ID, 0);

		e.putLong(PREF_STATISTICS_BEFORESESSIONMETERS_ID, drivenOverall);
		e.putLong(PREF_STATISTICS_BEFORESESSIONSECONDS_ID, secondsOverall);
		e.commit();
	}

	/** Returns the Meters driven in the current session.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static long getStatisticsMetersDrivenSession(final Context ctx){
		return getInstance(ctx).getLong(PREF_STATISTICS_SESSIONMETERS_ID, 0);
	}

	/** Returns the seconds driven in the current session.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static long getStatisticsSecondsDrivenSession(final Context ctx){
		final SharedPreferences sPref = getInstance(ctx);
		final long start = sPref.getLong(PREF_STATISTICS_SESSIONSTART_ID, NOT_SET);
		final long end = sPref.getLong(PREF_STATISTICS_SESSIONEND_ID, NOT_SET);
		if(start == NOT_SET || end == NOT_SET) {
			return 1;
		} else {
			return (end - start) / 1000;
		}
	}

	/** Returns the seconds driven before the current session.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static long getStatisticsSecondsDrivenBeforeSession(final Context ctx){
		return getInstance(ctx).getLong(PREF_STATISTICS_BEFORESESSIONSECONDS_ID, 0);
	}

	/** Add the meters driven in the current session.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aRotateMode RotateMode to be saved and used by the whole application. */
	public static void addStatisticsMetersDrivenSession(final Context ctx, final long aMeters){
		final long beforeDriven = getStatisticsMetersDrivenSession(ctx);
		getEditorInstance(ctx).putLong(PREF_STATISTICS_SESSIONMETERS_ID, beforeDriven + aMeters).commit();
	}


	/** Returns the Meters driven in the before the current session.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static long getStatisticsMetersDrivenBeforeSession(final Context ctx){
		return getInstance(ctx).getLong(PREF_STATISTICS_BEFORESESSIONMETERS_ID, 0);
	}

	/** Returns the MaximumSpeed driven in the before the current session.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static int getStatisticsMaxSpeedBeforeSession(final Context ctx){
		return getInstance(ctx).getInt(PREF_STATISTICS_BEFORESESSIONMAXSPEED_ID, 0);
	}

	/** Returns the MaximumSpeed driven in the current session.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static int getStatisticsMaxSpeedSession(final Context ctx){
		return getInstance(ctx).getInt(PREF_STATISTICS_SESSIONMAXSPEED_ID, 0);
	}

	/** Save the max-speed (if it is bigger than the current max-speed).
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aMaxSpeed to be saved and used by the whole application. */
	public static void updateStatisticsMaxSpeedBeforeSession(final Context ctx, final int aNewMaxSpeed){
		final int maxSpeedBefore = getStatisticsMaxSpeedBeforeSession(ctx);
		getEditorInstance(ctx).putInt(PREF_STATISTICS_BEFORESESSIONMAXSPEED_ID, Math.max(maxSpeedBefore, aNewMaxSpeed)).commit();
	}

	/** Saves the sessions max-speed.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aMaxSpeed to be saved and used by the whole application. */
	public static void updateStatisticsMaxSpeedSession(final Context ctx, final int aNewMaxSpeed){
		final int maxSpeedBefore = getStatisticsMaxSpeedSession(ctx);
		getEditorInstance(ctx).putInt(PREF_STATISTICS_SESSIONMAXSPEED_ID, Math.max(maxSpeedBefore, aNewMaxSpeed)).commit();
	}

	/** Saves the sessions end-timestamp.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aMaxSpeed to be saved and used by the whole application. */
	public static void updateSessionEndTimeStamp(final Context ctx){
		getEditorInstance(ctx).putLong(PREF_STATISTICS_SESSIONEND_ID, System.currentTimeMillis()).commit();
	}


	/** Returns the Meters overall driven.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static long getStatisticsMetersDrivenOverall(final Context ctx){
		return getStatisticsMetersDrivenSession(ctx) + getStatisticsMetersDrivenBeforeSession(ctx);
	}

	/** Returns the Seconds overall driven.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static long getStatisticsSecondsDrivenOverall(final Context ctx){
		return getStatisticsSecondsDrivenSession(ctx) + getStatisticsSecondsDrivenBeforeSession(ctx);
	}

	// ===========================================================
	// EULA
	// ===========================================================

	public static void saveEulaAccepted(final Context ctx) {
		getEditorInstance(ctx).putBoolean(PREF_EULA_ACCEPTED_ID, true).commit();
	}

	public static boolean isEulaAccepted(final Context ctx) {
		return getInstance(ctx).getBoolean(PREF_EULA_ACCEPTED_ID, PREF_EULA_ACCEPTED_DEFAULT);
	}

	// ===========================================================
	// RequestedScreenRotation
	// ===========================================================

	/** Returns the screenrotation-mode.
	 * 	@param ctx Activity needed to retrieve the SharedPreferences. */
	public static int getRequestedScreenOrientation(final Context ctx){
		return getInstance(ctx).getInt(PREF_SCREENROTATION_ID, PREF_SCREENORIENTATION_DEFAULT);
	}

	/** Save the requestedScreenrotation setting to be used later on.
	 * @param ctx Activity needed to retrieve the SharedPreferences.
	 * @param aMode to be saved and used by the whole application. */
	public static void saveRequestedScreenRotation(final Context ctx, final int aMode){
		getEditorInstance(ctx).putInt(PREF_SCREENROTATION_ID, aMode).commit();
	}

	// ===========================================================
	// Direction Voice
	// ===========================================================

	/** Returns whether Driving-Help in all menus is enabled in Preferences to the Activity parameter.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static boolean getDirectionVoiceEnabled(final Context ctx){
		return getInstance(ctx).getBoolean(PREF_DIRECTIONVOICE_ID, PREF_DIRECTIONVOICE_DEFAULT);
	}

	/** Save the setting whether the DirectionVoice-Help should be enabled by the calling application.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aEnabledIt to be saved and used by the whole application. */
	public static void saveDirectionVoiceEnabled(final Context ctx, final boolean aEnabledIt){
		getEditorInstance(ctx).putBoolean(PREF_DIRECTIONVOICE_ID, aEnabledIt).commit();
	}

	/** Returns the VoiceSayList saved in Preferences to the Activity parameter.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static HashMap<Integer, Integer> getTurnVoiceSayList(final Context ctx){
		final SharedPreferences sPref = getInstance(ctx);
		final HashMap<Integer, Integer> out = new HashMap<Integer, Integer>();

		for(int i = 0; i < PREF_TURNVOICE_ELEMENTS.length; i++){
			final int saymode = sPref.getInt(PREF_TURNVOICESAYLIST_ID + "_" + PREF_TURNVOICE_ELEMENTS[i], PREF_TURNVOICESAYLIST_DEFAULT[i]);
			out.put(PREF_TURNVOICE_ELEMENTS[i], saymode);
		}

		return out;
	}

	/** Save a VoiceSayList to be used by the whole application.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aVoiceSayList VoiceSayList to be saved and used by the whole application. */
	public static void saveTurnVoiceSayList(final Context ctx, final HashMap<Integer, Integer> aVoiceSayList){
		final Editor editor = getEditorInstance(ctx);

		for(int i = 0; i < PREF_TURNVOICE_ELEMENTS.length; i++) {
			editor.putInt(PREF_TURNVOICESAYLIST_ID + "_" + PREF_TURNVOICE_ELEMENTS[i], aVoiceSayList.get(PREF_TURNVOICE_ELEMENTS[i])).commit();
		}

		editor.commit();
	}

	// ===========================================================
	// Nav Flags
	// ===========================================================

	/** Save the setting whether the NavFlags are to be remembered.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aRememberIt to be saved and used by the whole application. */
	public static void saveNavSettingsRemember(final Context ctx, final boolean aRememberIt){
		getEditorInstance(ctx).putBoolean(PREF_NAVFLAGS_REMEMBER_ID, aRememberIt).commit();
	}

	/** Returns whether NavFlags are remembered.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static boolean getNavSettingsRemember(final Context ctx) {
		return getInstance(ctx).getBoolean(PREF_NAVFLAGS_REMEMBER_ID, PREF_NAVFLAGS_REMEMBER_DEFAULT);
	}

	public static void saveRoutePreferenceType(final Context ctx, final RoutePreferenceType aPreferenceType){
		getEditorInstance(ctx).putInt(PREF_ROUTEPREFERENCETYPE_ID, aPreferenceType.ordinal()).commit();
	}

	public static RoutePreferenceType getRoutePreferenceType(final Context ctx){
		return RoutePreferenceType.values()[getInstance(ctx).getInt(PREF_ROUTEPREFERENCETYPE_ID, PREF_ROUTEPREFERENCETYPE_DEFAULT)];
	}

	public static void saveAvoidTolls(final Context ctx, final boolean aAvoidTolls){
		getEditorInstance(ctx).putBoolean(PREF_AVOIDTOLLS_ID, aAvoidTolls).commit();
	}

	public static boolean getAvoidTolls(final Context ctx){
		return getInstance(ctx).getBoolean(PREF_AVOIDTOLLS_ID, PREF_AVOIDTOLLS_DEFAULT);
	}

	public static void saveAvoidHighways(final Context ctx, final boolean aAvoidHighways){
		getEditorInstance(ctx).putBoolean(PREF_AVOIDHIGHWAYS_ID, aAvoidHighways).commit();
	}

	public static boolean getAvoidHighways(final Context ctx){
		return getInstance(ctx).getBoolean(PREF_AVOIDHIGHWAYS_ID, PREF_AVOIDHIGHWAYS_DEFAULT);
	}

	// ===========================================================
	// StartupWarning
	// ===========================================================

	public static void saveShowStartupWarningNeverAgain(final Context ctx) {
		getEditorInstance(ctx).putBoolean(PREF_STARTUPWARNING_SHOW_NEVERAGAIN_ID, true).commit();
	}

	public static boolean getShowStartupWarningNeverAgain(final Context ctx) {
		return getInstance(ctx).getBoolean(PREF_STARTUPWARNING_SHOW_NEVERAGAIN_ID, PREF_STARTUPWARNING_SHOW_NEVERAGAIN_DEFAULT);
	}

	// ===========================================================
	// Autozoom
	// ===========================================================

	public static void saveAutoZoomEnabled(final Context ctx, final boolean aEnabled) {
		getEditorInstance(ctx).putBoolean(PREF_AUTOZOOM_ID, aEnabled).commit();
	}

	public static boolean getAutoZoomEnabled(final Context ctx) {
		return getInstance(ctx).getBoolean(PREF_AUTOZOOM_ID, PREF_AUTOZOOM_DEFAULT);
	}

	public static void saveAutoZoomMaxLevel(final Context ctx, final int aMaxLevel) {
		getEditorInstance(ctx).putInt(PREF_AUTOZOOM_MAXLEVEL_ID, aMaxLevel).commit();
	}

	public static int getAutoZoomMaxLevel(final Context ctx) {
		return getInstance(ctx).getInt(PREF_AUTOZOOM_MAXLEVEL_ID, PREF_AUTOZOOM_MAXLEVEL_DEFAULT);
	}

	// ===========================================================
	// StartupWarning
	// ===========================================================

	public static void saveRealTimeNav(final Context ctx, final boolean aEnabled) {
		getEditorInstance(ctx).putBoolean(PREF_REALTIMENAV_ID, aEnabled).commit();
	}

	public static boolean getRealTimeNav(final Context ctx) {
		return getInstance(ctx).getBoolean(PREF_REALTIMENAV_ID, PREF_REALTIMENAV_DEFAULT);
	}

	// ===========================================================
	// Minimal Filtering of Traces
	// ===========================================================

	public static void saveMinimalTraceFilteringEnabled(final Context ctx, final boolean aEnabled) {
		getEditorInstance(ctx).putBoolean(PREF_MINIMALTRACEFILTERING_ID, aEnabled).commit();
	}

	public static boolean getMinimalTraceFilteringEnabled(final Context ctx) {
		return getInstance(ctx).getBoolean(PREF_MINIMALTRACEFILTERING_ID, PREF_MINIMALTRACEFILTERING_DEFAULT);
	}

	// ===========================================================
	// OSMAccount-Data
	// ===========================================================

	public static void saveOSMAccountUsername(final Context ctx, final String aUsername) {
		getEditorInstance(ctx).putString(PREF_OSMACCOUNT_USERNAME_ID, aUsername).commit();
	}

	public static String getOSMAccountUsername(final Context ctx) {
		return getInstance(ctx).getString(PREF_OSMACCOUNT_USERNAME_ID, PREF_OSMACCOUNT_USERNAME_DEFAULT);
	}

	public static void saveOSMAccountPassword(final Context ctx, final String aUsername) {
		getEditorInstance(ctx).putString(PREF_OSMACCOUNT_PASSWORD_ID, aUsername).commit();
	}

	public static String getOSMAccountPassword(final Context ctx) {
		return getInstance(ctx).getString(PREF_OSMACCOUNT_PASSWORD_ID, PREF_OSMACCOUNT_PASSWORD_DEFAULT);
	}

	// ===========================================================
	// OSBCommentername
	// ===========================================================

	public static void saveOSBCommenterName(final Context ctx, final String aUsername) {
		getEditorInstance(ctx).putString(PREF_OSB_COMMENTERNAME_ID, aUsername).commit();
	}

	public static String getOSBCommenterName(final Context ctx) {
		return getInstance(ctx).getString(PREF_OSB_COMMENTERNAME_ID, PREF_COMMENTERNAME_DEFAULT);
	}

	// ===========================================================
	// FTPCConfirmationAddress
	// ===========================================================

	public static void saveFTPCConfirmationMail(final Context ctx, final String aMailAddress) {
		getEditorInstance(ctx).putString(PREF_FTPC_CONFIRMATIONMAIL_ID, aMailAddress).commit();
	}

	public static String getFTPCConfirmationMail(final Context ctx) {
		return getInstance(ctx).getString(PREF_FTPC_CONFIRMATIONMAIL_ID, PREF_FTPC_CONFIRMATIONMAIL_DEFAULT);
	}

	// ===========================================================
	// Show OSB-Instructions on OSBMap startup
	// ===========================================================

	public static void saveShowOSBInstructions(final Context ctx, final boolean pShowThem) {
		getEditorInstance(ctx).putBoolean(PREF_OSB_SHOWINSTRUCTIONS_ID, pShowThem).commit();
	}

	public static boolean getShowOSBInstructions(final Context ctx) {
		return getInstance(ctx).getBoolean(PREF_OSB_SHOWINSTRUCTIONS_ID, PREF_OSB_SHOWINSTRUCTIONS_DEFAULT);
	}

	// ===========================================================
	// MaxCacheSite
	// ===========================================================

	public static int getMaxCacheSizeUpperLimit(final Context ctx){
		return getMaxCacheSize(ctx, Preferences.getFilesystemCachePolicy(ctx));
	}

	/** Returns the Maximum Cachesize used for the OSMapTiles.
	 * 	@param ctx Activity-Context needed to retrieve the SharedPreferences. */
	public static int getMaxCacheSize(final Context ctx, final StoragePolicy aStoragePolicy){

		final int maxCacheDefault;
		switch(aStoragePolicy){
			case EXTERNAL:
				maxCacheDefault = PREF_CACHSIZEMAX_EXTERNAL_DEFAULT;
				break;
			case INTERNALROM:
			default:
				maxCacheDefault = PREF_CACHSIZEMAX_INTERNAL_DEFAULT;
				break;
		}
		return getInstance(ctx).getInt(PREF_CACHSIZEMAX_ID + " " + aStoragePolicy, maxCacheDefault);
	}

	public static void saveMaxCacheSize(final Context ctx, final int aMaximumCacheSize){
		saveMaxCacheSize(ctx, Preferences.getFilesystemCachePolicy(ctx), aMaximumCacheSize);
	}

	/** Save a DisplayQuality to be used by the whole application.
	 * @param ctx Activity-Context needed to retrieve the SharedPreferences.
	 * @param aMaximumCacheSize in MegaBytes. */
	public static void saveMaxCacheSize(final Context ctx, final StoragePolicy aStoragePolicy, final int aMaximumCacheSize){
		getEditorInstance(ctx).putInt(PREF_CACHSIZEMAX_ID + " " + aStoragePolicy, aMaximumCacheSize).commit();
	}

	// ===========================================================
	// OSMMapView-StoragePolicy
	// ===========================================================

	public static OSMMapTileFilesystemCache.StoragePolicy getFilesystemCachePolicy(final Context ctx){
		return StoragePolicy.values()[getInstance(ctx).getInt(PREF_FSCACHE_STORAGEPOLICY_ID, PREF_FSCACHE_STORAGEPOLICY_DEFAULT)];
	}

	public static void saveFilesystemCachePolicy(final Context ctx, final StoragePolicy aPolicy){
		getEditorInstance(ctx).putInt(PREF_FSCACHE_STORAGEPOLICY_ID, aPolicy.ordinal()).commit();
	}

	// ===========================================================
	// SnapToRoute
	// ===========================================================

	public static void saveSnapToRouteRadius(final Context ctx, final int aRadiusIndex) {
		getEditorInstance(ctx).putInt(PREF_SNAPTOROUTE_RADIUS_ID, aRadiusIndex).commit();
	}

	public static int getSnapToRouteRadiusIndex(final Context ctx) {
		return getInstance(ctx).getInt(PREF_SNAPTOROUTE_RADIUS_ID, PREF_SNAPTOROUTE_RADIUS_DEFAULT);
	}

	/**
	 * 
	 * @param ctx
	 * @return In Meters
	 */
	public static int getSnapToRouteRadius(final Context ctx) {
		final int[] valDist;
		final UnitSystem us = getUnitSystem(ctx);
		switch(us){
			case IMPERIAL:
				valDist = ctx.getResources().getIntArray(R.array.settings_snaptoroute_radius_imperial);
				break;
			case METRIC:
			default:
				valDist = ctx.getResources().getIntArray(R.array.settings_snaptoroute_radius_metric);
		}

		final int radiusIndex = getInstance(ctx).getInt(PREF_SNAPTOROUTE_RADIUS_ID, PREF_SNAPTOROUTE_RADIUS_DEFAULT);
		return (int)(valDist[Math.min(radiusIndex, valDist.length - 1)] / us.mScaleToMeters);
	}

	public static void saveSnapToRoute(final Context ctx, final boolean aEnabled) {
		getEditorInstance(ctx).putBoolean(PREF_SNAPTOROUTE_ID, aEnabled).commit();
	}

	public static boolean getSnapToRoute(final Context ctx) {
		return getInstance(ctx).getBoolean(PREF_SNAPTOROUTE_ID, PREF_SNAPTOROUTE_DEFAULT);
	}

	// ===========================================================
	// Stored-ProviderInfo
	// ===========================================================

	public static OSMMapTileProviderInfo getMapViewProviderInfoWhereAmI(final Context ctx){
		return OSMMapTileProviderInfo.fromName(getInstance(ctx).getString(PREF_OSMMAPVIEW_PROVIDERINFO_WHEREAMI_ID, PREF_OSMMAPVIEW_PROVIDERINFO_WHEREAMI_DEFAULT));
	}

	public static void saveMapViewProviderInfoWhereAmI(final Context ctx, final OSMMapTileProviderInfo aInfo){
		getEditorInstance(ctx).putString(PREF_OSMMAPVIEW_PROVIDERINFO_WHEREAMI_ID, aInfo.NAME).commit();
	}

	public static OSMMapTileProviderInfo getMapViewProviderInfoDDMap(final Context ctx){
		return OSMMapTileProviderInfo.fromName(getInstance(ctx).getString(PREF_OSMMAPVIEW_PROVIDERINFO_DDMAP_ID, PREF_OSMMAPVIEW_PROVIDERINFO_DDMAP_DEFAULT));
	}

	public static void saveMapViewProviderInfoDDMap(final Context ctx, final OSMMapTileProviderInfo aInfo){
		getEditorInstance(ctx).putString(PREF_OSMMAPVIEW_PROVIDERINFO_DDMAP_ID, aInfo.NAME).commit();
	}

	// ===========================================================
	// TracePolicy
	// ===========================================================

	public static boolean getTracePolicyAndnavOrg(final Context ctx) {
		return getInstance(ctx).getBoolean(PREF_TRACEPOLICY_ANDNAVORG_ID, PREF_TRACEPOLICY_ANDNAVORG_DEFAULT);
	}

	public static void saveTracePolicyAndnavOrg(final Context ctx, final boolean pEnabled){
		getEditorInstance(ctx).putBoolean(PREF_TRACEPOLICY_ANDNAVORG_ID, pEnabled).commit();
	}

	public static boolean getTracePolicyExternal(final Context ctx) {
		return getInstance(ctx).getBoolean(PREF_TRACEPOLICY_EXTERNAL_ID, PREF_TRACEPOLICY_EXTERNAL_DEFAULT);
	}

	public static void saveTracePolicyExternal(final Context ctx, final boolean pEnabled){
		getEditorInstance(ctx).putBoolean(PREF_TRACEPOLICY_EXTERNAL_ID, pEnabled).commit();
	}

	public static boolean getTracePolicyOSM(final Context ctx) {
		return getInstance(ctx).getBoolean(PREF_TRACEPOLICY_OSM_ID, PREF_TRACEPOLICY_OSM_DEFAULT);
	}

	public static void saveTracePolicyOSM(final Context ctx, final boolean pEnabled){
		getEditorInstance(ctx).putBoolean(PREF_TRACEPOLICY_OSM_ID, pEnabled).commit();
	}

	// ===========================================================
	// TTS
	// ===========================================================

	public static boolean showTTSNotInstalledInfo(final Context ctx) {
		return getInstance(ctx).getBoolean(PREF_SHOWNOTTSINSTALLEDINFO_ID, true);
	}

	public static void saveShowTTSNotInstalledInfo(final Context ctx, final boolean aShowIt) {
		getEditorInstance(ctx).putBoolean(PREF_SHOWNOTTSINSTALLEDINFO_ID, aShowIt).commit();
	}

	// ===========================================================
	// AdFree
	// ===========================================================

	public static void saveAdFreeState(final Context ctx, final boolean pState) {
		getEditorInstance(ctx).putBoolean(PREF_ADFREESTATE_ID, pState).commit();		
	}

	public static boolean getAdFreeState(final Context ctx) {
		return getInstance(ctx).getBoolean(PREF_ADFREESTATE_ID, PREF_ADFREESTATE_DEFAULT);
	}


	// ===========================================================
	// SaveInitialRoute
	// ===========================================================

	public static boolean getSaveInitialRoute(final Context ctx) {
		return getInstance(ctx).getBoolean(PREF_SAVEINITIALROUTE_ID, PREF_SAVEINITIALROUTE_DEFAULT);
	}

	public static void saveSaveInitialRoute(final Context ctx, final boolean aSaveIt) {
		getEditorInstance(ctx).putBoolean(PREF_SAVEINITIALROUTE_ID, aSaveIt).commit();
	}

	// ===========================================================
	// FIrst Start of the App
	// ===========================================================

	/**
	 * Internally increments the use!
	 * @param ctx
	 * @return
	 */
	public static boolean isFirstStart(final Context ctx) {
		final long useCount = getInstance(ctx).getLong(PREF_STARTUPCOUNT_ID, 0L);
		getEditorInstance(ctx).putLong(PREF_STARTUPCOUNT_ID, useCount + 1).commit();
		return useCount == 0L;
	}

	public static long getStartCount(final Context ctx) {
		return getInstance(ctx).getLong(PREF_STARTUPCOUNT_ID, 0L);
	}

	// ===========================================================
	// Ad Html-Code
	// ===========================================================

	public static void saveAdHtmlCode(final Context ctx, final String htmlData) {
		final String base64Encoded = new String(Base64.encodeToChar(htmlData.getBytes(), false));
		getEditorInstance(ctx).putString(PREF_ADHTMLCODE_ID, base64Encoded).commit();
	}

	public static String getAdHtmlCode(final Context ctx) {
		try{
			final String base64Encoded = getInstance(ctx).getString(PREF_ADHTMLCODE_ID, PREF_ADHTMLCODE_DEFAULT);
			final byte[] decoded = Base64.decode(base64Encoded.toCharArray());
			return new String(decoded);
		}catch(final Throwable t){
			return PREF_ADHTMLCODE_DEFAULT;
		}
	}

	// ===========================================================
	// ORSServer
	// ===========================================================

	public static boolean hasORSServer(final Context ctx) {
		final String name = getInstance(ctx).getString(PREF_ORSSERVER_ID, null);
		if(name == null) {
			return false;
		}

		final ORSServer resolved = ORSServer.fromName(name, false);
		return resolved != null;
	}

	public static ORSServer getORSServer(final Context ctx) {
		final String name = getInstance(ctx).getString(PREF_ORSSERVER_ID, null);
		if(name == null){
			return ORSServer.getDefault();
		}else{
			return ORSServer.fromName(name, true);
		}
	}

	public static void saveORSServer(final Context ctx, final ORSServer pServer) {
		getEditorInstance(ctx).putString(PREF_ORSSERVER_ID, pServer.name()).commit();
	}

	// ===========================================================
	// HUDs
	// ===========================================================

	public static IHUDImpl getHUDImpl(final Context ctx){
		final int hudID = getInstance(ctx).getInt(PREF_HUDID_ID, PREF_HUDID_DEFAULT);
		return HUDRegistry.resolve(hudID);
	}

	public static int getHUDImplVariationID(final Context ctx){
		return getInstance(ctx).getInt(PREF_HUDVARIATIONID_ID, PREF_HUDVARIATIONID_DEFAULT);
	}

	public static void saveHUDImpl(final Context ctx, final IHUDImpl pHUDImpl, final IHUDImplVariation pVariation){
		saveHUDImpl(ctx, pHUDImpl, pVariation.getVariationID());
	}

	public static void saveHUDImpl(final Context ctx, final IHUDImpl pHUDImpl, final int pVariationID){
		/* LITEVERSION */
		if(Constants.LITEVERSION && pHUDImpl.getID() != BasicHUDImpl.ID){
			Toast.makeText(ctx, R.string.toast_get_pro_version, Toast.LENGTH_SHORT).show();
		}else{
			getEditorInstance(ctx)
			.putInt(PREF_HUDID_ID, pHUDImpl.getID())
			.putInt(PREF_HUDVARIATIONID_ID, pVariationID)
			.commit();
		}
	}

	public static DirectionArrowDescriptor getHUDImplVariationDirectionArrowDescriptor(final Context ctx){
		return getHUDImpl(ctx).getVariation(getHUDImplVariationID(ctx)).getDirectionArrowDescriptor();
	}

	// ===========================================================
	// SkyHook Authentication
	// ===========================================================

	public static WPSAuthentication getSkyHookWPSAuthentication(final Context ctx, final boolean pUseFallback) {
		final String username = getInstance(ctx).getString(PREF_SKYHOOKUSERNAME_ID, null);
		if(username == null){
			if(pUseFallback){
				return getSkyHookWPSFallbackAuthentication();
			}else{
				return null;
			}
		}else{
			return new WPSAuthentication(username, PREF_SKYHOOKREGISTRATIONREALM_DEFAULT);
		}
	}

	public static WPSAuthentication getSkyHookWPSRegistrationAuthentication() {
		return new WPSAuthentication(PREF_SKYHOOKREGISTRATIONUSERNAME_DEFAULT, PREF_SKYHOOKREGISTRATIONREALM_DEFAULT);
	}

	public static WPSAuthentication getSkyHookWPSFallbackAuthentication() {
		return new WPSAuthentication(PREF_SKYHOOKFALLBACKUSERNAME_DEFAULT, PREF_SKYHOOKFALLBACKREALM_DEFAULT);
	}

	public static void saveSkyHookWPSAuthentication(final Context ctx, final WPSAuthentication newUser) {
		getEditorInstance(ctx).putString(PREF_SKYHOOKUSERNAME_ID, newUser.getUsername()).commit();
	}

	// ===========================================================
	// CDATA-Wrapper
	// ===========================================================

	private static final String CDATA_TAG_OPEN = "<![CDATA[";
	private static final String CDATA_TAG_CLOSE = "]]>";

	@SuppressWarnings("unused")
	private static String wrapWithCData(final String in){
		return new StringBuilder().append(CDATA_TAG_OPEN).append(in).append(CDATA_TAG_CLOSE).toString();
	}

	@SuppressWarnings("unused")
	private static String unwrapFromCData(final String in){
		if(in.startsWith(CDATA_TAG_OPEN) && in.endsWith(CDATA_TAG_CLOSE)){
			return in.substring(CDATA_TAG_OPEN.length(), in.length() - CDATA_TAG_CLOSE.length());
		}else{
			return in;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
