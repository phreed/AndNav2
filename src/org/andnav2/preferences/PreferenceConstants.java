// Created by plusminus on 17:05:18 - 11.08.2008
package org.andnav2.preferences;

import org.andnav2.adt.UnitSystem;
import org.andnav2.adt.keyboardlayouts.KeyBoardLayoutImpls;
import org.andnav2.osm.views.tiles.OSMMapTileProviderInfo;
import org.andnav2.osm.views.tiles.caching.OSMMapTileFilesystemCache;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.ui.map.hud.IHUDImplVariation;
import org.andnav2.ui.map.hud.impl.basic.BasicHUDImpl;

import android.content.pm.ActivityInfo;


public interface PreferenceConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	/* ########## PREFERENCES IDENTIFIERS ########## */

	public static final String MODE_SD = "mode_sd";
	public static final int MODE_SD_DESTINATION = 0;
	public static final int MODE_SD_WAYPOINT = MODE_SD_DESTINATION + 1;
	public static final int MODE_SD_SETHOME = MODE_SD_WAYPOINT + 1;
	public static final int MODE_SD_RESOLVE = MODE_SD_SETHOME + 1;

	/** Name of the Shared Preferences-File. */
	public static final String SHARED_PREFERENCES_NAME = "andnav_preferences";
	public static final String PREF_CENTERMODE_ID = "centermode_id";
	public static final String PREF_ROTATEMODE_ID = "rotatemode_id";
	public static final String PREF_SHOWTITLEBARINMAP_ID = "showtitlebarinmap_id";
	public static final String PREF_DISPLAYQUALITY_ID = "displayquality_id";
	public static final String PREF_DRIVINGDIRECTIONSLANGUAGE_ID = "drivingdirectionslanguage_id";
	public static final String PREF_MOSTRECENTUSED_COUNTRY_ID = "mostrecentused_sdcountry_id";
	public static final String PREF_MOSTRECENTUSED_COUNTRYSUBDIVISION_ID = "mostrecentused_sdcountrysubdivision_id";
	public static final String PREF_KEYBOARDLAYOUT_ID = "keyboardlayout_id";
	public static final String PREF_MENUVOICE_ID = "menuvoice_id";
	public static final String PREF_STATISTICS_ID = "statistics_id";
	public static final String PREF_DIRECTIONVOICE_ID = "directionvoice_id";
	public static final String PREF_HOMEGEOPOINT_ID = "homemappoint_id";
	public static final String PREF_UNITSYSTEM_ID = "unitsystem_id";
	public static final String PREF_DISTANCEVOICESAYLIST_ID = "distancevoicesaylist_id";
	public static final String PREF_TURNVOICESAYLIST_ID = "turnvoicesaylist_id";
	public static final String PREF_ROUTEPREFERENCETYPE_ID = "routepreferencetype_id";
	public static final String PREF_AVOIDTOLLS_ID = "avoidtolls_id";
	public static final String PREF_AVOIDHIGHWAYS_ID = "avoidhighways_id";
	public static final String PREF_NAVFLAGS_REMEMBER_ID = "navflags_remember_id";
	public static final String PREF_STARTUPWARNING_SHOW_NEVERAGAIN_ID = "startupwarning_neveragain_id";
	public static final String PREF_AUTOZOOM_ID = "autozoom_id";
	public static final String PREF_AUTOZOOM_MAXLEVEL_ID = "autozoom_maxlevel_id";
	public static final String PREF_REALTIMENAV_ID = "unlocked_id";
	public static final String PREF_CACHSIZEMAX_ID = "cachsizemax_id";
	public static final String PREF_EULA_ACCEPTED_ID = "eula_accepted_id";
	public static final String PREF_SCREENROTATION_ID = "screenrotation_id";
	public static final String PREF_FSCACHE_STORAGEPOLICY_ID = "fscache_storagepolicy_id";
	public static final String PREF_SNAPTOROUTE_RADIUS_ID = "pref_snaptoroute_radius_id";
	public static final String PREF_SNAPTOROUTE_ID = "pref_snaptoroute_id";
	public static final String PREF_TRACEPOLICY_OSM_ID = "tracepolicy_osm_id";
	public static final String PREF_TRACEPOLICY_ANDNAVORG_ID = "tracepolicy_andnavorg_id";
	public static final String PREF_TRACEPOLICY_EXTERNAL_ID = "tracepolicy_external_id";
	public static final String PREF_OSMACCOUNT_USERNAME_ID = "osmaccount_username_id";
	public static final String PREF_OSMACCOUNT_PASSWORD_ID = "osmaccount_password_id";
	public static final String PREF_OSB_COMMENTERNAME_ID = "osb_commentername_id";
	public static final String PREF_FTPC_CONFIRMATIONMAIL_ID = "ftpc_confirmationmail_id";
	public static final String PREF_OSB_SHOWINSTRUCTIONS_ID = "osb_showinstructions_id";
	public static final String PREF_STARTUPCOUNT_ID = "startupcount_id";
	public static final String PREF_SHOWNOTTSINSTALLEDINFO_ID = "shownottsinstalledinfot_id";
	public static final String PREF_ADFREESTATE_ID = "adfreestate_id";
	public static final String PREF_SAVEINITIALROUTE_ID = "saveinitialroute_id";
	public static final String PREF_ADHTMLCODE_ID = "adhtmlcode_id";
	public static final String PREF_MINIMALTRACEFILTERING_ID = "minimaltracefiltering_id";
	public static final String PREF_SKYHOOKUSERNAME_ID = "skyhookusername_id";
	public static final String PREF_OSMMAPVIEW_PROVIDERINFO_WHEREAMI_ID = "pref_osmmapview_providerinfo_whereami_id";
	public static final String PREF_OSMMAPVIEW_PROVIDERINFO_DDMAP_ID = "pref_osmmapview_providerinfo_ddmap_id";
	public static final String PREF_HUDID_ID = "pref_hudid_id";
	public static final String PREF_HUDVARIATIONID_ID = "pref_hudvariationid_id";
	public static final String PREF_ORSSERVER_ID = "pref_orsserver_id";


	public static final String PREF_STATISTICS_SESSIONSTART_ID = "statistics_sessionstart_id";
	public static final String PREF_STATISTICS_SESSIONEND_ID = "statistics_sessionend_id";
	public static final String PREF_STATISTICS_SESSIONMAXSPEED_ID = "statistics_sessionmaxspeed_id";
	public static final String PREF_STATISTICS_SESSIONMETERS_ID = "statistics_sessionmeters_id";
	public static final String PREF_STATISTICS_BEFORESESSIONMAXSPEED_ID = "statistics_beforemaxspeed_id";
	public static final String PREF_STATISTICS_BEFORESESSIONMETERS_ID = "statistics_beforemeters_id";
	public static final String PREF_STATISTICS_BEFORESESSIONSECONDS_ID = "statistics_beforeseconds_id";


	public static final String PREF_ADHTMLCODE_DEFAULT = "Loading Ad...";

	public static final boolean PREF_SHOWINTITLEBAR_DEFAULT = false;
	public static final boolean PREF_REALTIMENAV_DEFAULT = true;
	public static final int PREF_CACHSIZEMAX_INTERNAL_DEFAULT = 5;
	public static final int PREF_CACHSIZEMAX_EXTERNAL_DEFAULT = 256;
	public static final int PREF_FSCACHE_STORAGEPOLICY_DEFAULT = OSMMapTileFilesystemCache.StoragePolicy.EXTERNAL.ordinal();
	public static final String PREF_OSMMAPVIEW_PROVIDERINFO_WHEREAMI_DEFAULT = OSMMapTileProviderInfo.MAPNIK.NAME;
	public static final String PREF_OSMMAPVIEW_PROVIDERINFO_DDMAP_DEFAULT = OSMMapTileProviderInfo.MAPNIK.NAME;
	public static final boolean PREF_TRACEPOLICY_OSM_DEFAULT = false;
	public static final boolean PREF_ADFREESTATE_DEFAULT = false;
	public static final boolean PREF_TRACEPOLICY_ANDNAVORG_DEFAULT = true;
	public static final boolean PREF_TRACEPOLICY_EXTERNAL_DEFAULT = true;
	public static final String PREF_OSMACCOUNT_USERNAME_DEFAULT = "";
	public static final String PREF_OSMACCOUNT_PASSWORD_DEFAULT = "";
	public static final String PREF_COMMENTERNAME_DEFAULT = "AndNav2-User";
	public static final String PREF_FTPC_CONFIRMATIONMAIL_DEFAULT = "your@mail.com";
	public static final boolean PREF_SAVEINITIALROUTE_DEFAULT = true;
	public static final boolean PREF_MINIMALTRACEFILTERING_DEFAULT = true;
	public static final String PREF_SKYHOOKREGISTRATIONREALM_DEFAULT = "andnav.org";
	public static final String PREF_SKYHOOKREGISTRATIONUSERNAME_DEFAULT = "andnav2reg";
	public static final String PREF_SKYHOOKFALLBACKREALM_DEFAULT = "andnav.org";
	public static final String PREF_SKYHOOKFALLBACKUSERNAME_DEFAULT = "andnav2";
	public static final int PREF_HUDID_DEFAULT = BasicHUDImpl.ID;
	public static final int PREF_HUDVARIATIONID_DEFAULT = IHUDImplVariation.VARIATION_DEFAULT_ID;

	public static final boolean PREF_OSB_SHOWINSTRUCTIONS_DEFAULT = true;
	public static final boolean PREF_STATISTICS_DEFAULT = true;
	public static final boolean PREF_MENUVOICE_DEFAULT = false;
	public static final boolean PREF_DIRECTIONVOICE_DEFAULT = true;
	public static final boolean PREF_NAVFLAGS_REMEMBER_DEFAULT = false;
	public static final boolean PREF_STARTUPWARNING_SHOW_NEVERAGAIN_DEFAULT = false;
	public static final boolean PREF_EULA_ACCEPTED_DEFAULT = false;

	public static final int PREF_SCREENORIENTATION_DEFAULT = ActivityInfo.SCREEN_ORIENTATION_USER;

	public static final String PREF_HOMEGEOPOINT_DEFAULT = null;


	public static final String PREF_DRIVINGDIRECTIONSLANGUAGE_DEFAULT = Country.UNITEDKINGDOM.COUNTRYCODE;
	public static final String PREF_UNITSYSTEM_DEFAULT = UnitSystem.IMPERIAL.mAbbreviation;

	public static final boolean PREF_AUTOZOOM_DEFAULT = true;
	public static final int PREF_AUTOZOOM_MAXLEVEL_DEFAULT = Integer.MAX_VALUE;

	public static final boolean PREF_SNAPTOROUTE_DEFAULT = true;
	public static final int PREF_SNAPTOROUTE_RADIUS_DEFAULT = 3; // 40 meters/yards

	public static final int PREF_DISPLAYQUALITY_BEST = 0;
	public static final int PREF_DISPLAYQUALITY_HIGH = PREF_DISPLAYQUALITY_BEST + 1;
	public static final int PREF_DISPLAYQUALITY_STANDARD = PREF_DISPLAYQUALITY_HIGH + 1;
	public static final int PREF_DISPLAYQUALITY_LOW = PREF_DISPLAYQUALITY_STANDARD + 1;
	public static final int PREF_DISPLAYQUALITY_DEFAULT = PREF_DISPLAYQUALITY_BEST;

	public static final int PREF_CENTERMODE_CENTERUSER = 0;
	public static final int PREF_CENTERMODE_UPTO_NEXTTURN = PREF_CENTERMODE_CENTERUSER + 1;
	public static final int PREF_CENTERMODE_DEFAULT = PREF_CENTERMODE_UPTO_NEXTTURN;

	public static final int PREF_ROTATEMODE_NORTH_UP = 0; /* North is always to the top. */
	public static final int PREF_ROTATEMODE_DRIVINGDIRECTION_UP = PREF_ROTATEMODE_NORTH_UP + 1; /* Driving direction is always to the top. */
	public static final int PREF_ROTATEMODE_DEFAULT = PREF_ROTATEMODE_NORTH_UP;

	public static final int PREF_KEYBOARDLAYOUT_ABCDEF = KeyBoardLayoutImpls.ABCKeyboardLayout.ID;
	public static final int PREF_KEYBOARDLAYOUT_QWERTY = KeyBoardLayoutImpls.QWERTYKeyBoardLayout.ID;
	public static final int PREF_KEYBOARDLAYOUT_QWERTZ = KeyBoardLayoutImpls.QWERTZKeyBoardLayout.ID;
	public static final int PREF_KEYBOARDLAYOUT_CYRILLIC = KeyBoardLayoutImpls.CyrillicKeyBoardLayout.ID;
	public static final int PREF_KEYBOARDLAYOUT_DEFAULT = PREF_KEYBOARDLAYOUT_ABCDEF;

	public static final String PREF_THEME_RESID_ID = "theme_resid";

	public static final int PREF_THEME_DEFAULT_RESID = android.R.style.Theme;
	public static final int PREF_THEME_NIGHT_RESID = android.R.style.Theme_Black;
	public static final int PREF_THEME_DAY_RESID = android.R.style.Theme_Light;


	public static final int PREF_TURNVOICESAYLIST_SAY_NOTHING  = 0;
	public static final int PREF_TURNVOICESAYLIST_SAY_DISTANCE = PREF_TURNVOICESAYLIST_SAY_NOTHING + 1;
	public static final int PREF_TURNVOICESAYLIST_SAY_TURN = PREF_TURNVOICESAYLIST_SAY_DISTANCE + 1;
	public static final int PREF_TURNVOICESAYLIST_SAY_DISTANCE_AND_TURN = PREF_TURNVOICESAYLIST_SAY_TURN + 1;
	public static final int PREF_TURNVOICESAYLIST_SPEECH_DISTANCE = PREF_TURNVOICESAYLIST_SAY_DISTANCE_AND_TURN + 1;
	public static final int PREF_TURNVOICESAYLIST_SPEECH_TURN = PREF_TURNVOICESAYLIST_SPEECH_DISTANCE + 1;
	public static final int PREF_TURNVOICESAYLIST_SPEECH_DISTANCE_AND_TURN = PREF_TURNVOICESAYLIST_SPEECH_TURN + 1;

	public static final int PREF_TURNVOICE_ELEMENT_50 = 50;
	public static final int PREF_TURNVOICE_ELEMENT_100 = 100;
	public static final int PREF_TURNVOICE_ELEMENT_200 = 200;
	public static final int PREF_TURNVOICE_ELEMENT_500 = 500;
	public static final int PREF_TURNVOICE_ELEMENT_1000 = 1000;
	public static final int PREF_TURNVOICE_ELEMENT_2000 = 2000;
	public static final int PREF_TURNVOICE_ELEMENT_5000 = 5000;
	public static final int PREF_TURNVOICE_ELEMENT_10000 = 10000;
	public static final int PREF_TURNVOICE_ELEMENT_25000 = 25000;

	public static final int[] PREF_TURNVOICE_ELEMENTS = new int[]{PREF_TURNVOICE_ELEMENT_50, PREF_TURNVOICE_ELEMENT_100, PREF_TURNVOICE_ELEMENT_200, PREF_TURNVOICE_ELEMENT_500, PREF_TURNVOICE_ELEMENT_1000, PREF_TURNVOICE_ELEMENT_2000, PREF_TURNVOICE_ELEMENT_5000, PREF_TURNVOICE_ELEMENT_10000, PREF_TURNVOICE_ELEMENT_25000};
	public static final int[] PREF_TURNVOICESAYLIST_DEFAULT = new int[]{
		PREF_TURNVOICESAYLIST_SAY_NOTHING, // 50
		PREF_TURNVOICESAYLIST_SPEECH_DISTANCE_AND_TURN, // 100
		PREF_TURNVOICESAYLIST_SPEECH_DISTANCE_AND_TURN, // 200
		PREF_TURNVOICESAYLIST_SPEECH_DISTANCE_AND_TURN, // 500
		PREF_TURNVOICESAYLIST_SPEECH_DISTANCE_AND_TURN, // 1000
		PREF_TURNVOICESAYLIST_SPEECH_DISTANCE_AND_TURN, // 2000
		PREF_TURNVOICESAYLIST_SPEECH_DISTANCE_AND_TURN, // 5000
		PREF_TURNVOICESAYLIST_SPEECH_DISTANCE, // 10000
		PREF_TURNVOICESAYLIST_SPEECH_DISTANCE // 25000
	};

	public static final int PREF_ROUTEPREFERENCETYPE_DEFAULT = 0;
	public static final boolean PREF_AVOIDTOLLS_DEFAULT = false;
	public static final boolean PREF_AVOIDHIGHWAYS_DEFAULT = false;

	// ===========================================================
	// Methods
	// ===========================================================
}
