//Created by plusminus on 14:01:14 - 15.02.2008
package org.andnav2.db;

import java.util.ArrayList;
import java.util.List;

import org.andnav2.adt.DBPOI;
import org.andnav2.adt.Favorite;
import org.andnav2.adt.TrafficFeed;
import org.andnav2.db.util.constants.DatabaseConstants;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.util.constants.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager implements DatabaseConstants{
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 2;

	private static SQLiteDatabase mInstance;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private static SQLiteDatabase ensureDBInstanceExists(final Context ctx) {
		if(mInstance == null) {
			mInstance = new AndNavDatabaseHelper(ctx, AndNavSQLTableInfo.values()).getWritableDatabase();
		}
		/* Create the Database (no Errors if it already exists) */
		return mInstance;
	}

	private static void closeDB() {
		//		if (mInstance != null)
		//			mInstance.close();
	}

	private static void ensureCountryCodeIDTableExists(final SQLiteDatabase myDB) {
		/* Create another Table in the Database. */
		myDB.execSQL(CREATE_COUNTRYCODE_TABLE);
	}

	private static void ensureFavoritesTableExists(final SQLiteDatabase myDB) {
		/* Create another Table in the Database. */
		myDB.execSQL(CREATE_FAVORITES_TABLE);
	}

	private static void ensureZipsAndCitiesTableExists(final SQLiteDatabase myDB) {
		/* Create another Table in the Database. */
		myDB.execSQL(CREATE_ZIPSANDCITIES_TABLE);
	}

	private static void ensureStreetsTableExists(final SQLiteDatabase myDB) {
		/* Create another Table in the Database. */
		myDB.execSQL(CREATE_STREETS_TABLE);
	}

	private static void ensurePOIHistoryTableExists(final SQLiteDatabase myDB) {
		/* Create another Table in the Database. */
		myDB.execSQL(CREATE_POIHISTORY_TABLE);
	}

	private static void ensureTrafficFeedTableExists(final SQLiteDatabase myDB) {
		/* Create another Table in the Database. */
		myDB.execSQL(CREATE_TRAFFICFEED_TABLE);
	}

	public static int getCustomTrafficFeedCount(final Context ctx) throws DataBaseException {
		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);

		try{
			ensureTrafficFeedTableExists(myDB);

			/* Query for all feeds. */
			final Cursor c = myDB.query(T_TRAFFICFEEDS, new String[] {T_TRAFFICFEEDS_COUNTRYCODE},
					null, null, null, null, null);

			/* Check if our query was valid. */
			if (c != null) {
				final int count = c.getCount();
				c.close();
				return count;
			}else{
				throw new DataBaseException("Failed on getting trafficfeed count pois");
			}

		}finally{
			/* Close Database */
			closeDB();
		}
	}

	public static List<TrafficFeed> getCustomTrafficFeeds(final Context ctx) throws DataBaseException {
		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);
		ArrayList<TrafficFeed> feedList = null;

		try{
			ensureTrafficFeedTableExists(myDB);

			/* Query for all feeds. */
			final Cursor c = myDB.query(T_TRAFFICFEEDS, new String[] {T_TRAFFICFEEDS_NAME, T_TRAFFICFEEDS_URL, T_TRAFFICFEEDS_COUNTRYCODE},
					null, null, null, null, T_TRAFFICFEEDS_NAME + " ASC");

			/* Check if our query was valid. */
			if (c != null) {
				feedList = new ArrayList<TrafficFeed>(c.getCount());

				/* Get the indices of the Columns we will need */
				final int nameColumn = c.getColumnIndexOrThrow(T_TRAFFICFEEDS_NAME);
				final int urlColumn = c.getColumnIndexOrThrow(T_TRAFFICFEEDS_URL);
				final int countrycodeColumn = c.getColumnIndexOrThrow(T_TRAFFICFEEDS_COUNTRYCODE);

				/* Check if at least one Result was returned. */
				if (c.moveToFirst()) {
					/* Loop through all Results */
					do {
						/* Retrieve the values of the Entry the Cursor is providing. */
						feedList.add(new TrafficFeed(c.getString(nameColumn),
								c.getString(urlColumn),
								Country.fromAbbreviation(c.getString(countrycodeColumn))));

					} while (c.moveToNext());
				}
				c.close();
			}else{
				throw new DataBaseException("Failed on reading trafficfeeds.");
			}

		}finally{
			/* Close Database */
			closeDB();
		}
		return feedList;
	}

	public static void addTrafficFeed(final Context ctx, final TrafficFeed aTrafficFeed) throws DataBaseException {
		if(aTrafficFeed == null) {
			return;
		}

		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);
		try{
			ensureTrafficFeedTableExists(myDB);
			/* Query for the fav-name passed as parameter. */
			final Cursor c = myDB.query(T_TRAFFICFEEDS, null, T_TRAFFICFEEDS_URL + "=?", new String[]{aTrafficFeed.getURL()}, null, null, null);
			/* If nothing was found... */
			if(c == null || c.getCount() == 0){
				/* Add new dataset. */
				final ContentValues cv = new ContentValues();
				cv.put(T_TRAFFICFEEDS_NAME, aTrafficFeed.getName());
				cv.put(T_TRAFFICFEEDS_URL, aTrafficFeed.getURL());
				cv.put(T_TRAFFICFEEDS_COUNTRYCODE, aTrafficFeed.getNationality().COUNTRYCODE);

				final long retVal = myDB.insert(T_TRAFFICFEEDS, null, cv);
				if(retVal == -1) {
					throw new DataBaseException("Failed on inserting new trafficfeed");
				}
			}
			c.close();
		}finally{
			/* Close Database */
			closeDB();
		}
	}

	public static void addFavorite(final Context ctx, final String aFavName, final int aLatitude, final int aLongitude) throws DataBaseException {
		if(aFavName == null || aFavName.length() == 0) {
			return;
		}

		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);
		try{
			ensureFavoritesTableExists(myDB);

			final int usedBefore = checkFavoriteExists(myDB, aFavName);
			if(usedBefore != org.andnav2.util.constants.Constants.NOT_SET){
				incrementFavoriteUses(myDB, aFavName, usedBefore);
			}else{
				/* Query for the fav-name passed as parameter. */
				final Cursor c = myDB.query(T_FAVS, null, T_FAVS_COL_FAVNAME + "=?", new String[]{aFavName}, null, null, null);
				/* If nothing was found... */
				if(c == null || c.getCount() == 0){
					/* Add new dataset. */
					final ContentValues cv = new ContentValues();
					cv.put(T_FAVS_COL_FAVNAME, aFavName);
					cv.put(T_FAVS_COL_LAT, aLatitude);
					cv.put(T_FAVS_COL_LNG, aLongitude);
					cv.put(T_FAVS_COL_LAST_USE, DATETIME_NOW);

					final long retVal = myDB.insert(T_FAVS, null, cv);
					if(retVal == -1) {
						throw new DataBaseException("Failed on inserting new favorite");
					}
				}
				c.close();
			}
		}finally{
			/* Close Database */
			closeDB();
		}
	}

	public static void addPOIToHistory(final Context ctx, final String aPOIName, final int aLatitude, final int aLongitude) throws DataBaseException {
		if(aPOIName == null || aPOIName.length() == 0) {
			return;
		}

		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);
		try{
			ensurePOIHistoryTableExists(myDB);

			final boolean exists = checkPOIExists(myDB, aPOIName);
			if(exists){
				return;
			}else{
				/* Query for the fav-name passed as parameter. */
				final Cursor c = myDB.query(T_POIHISTORY, null, T_POIHISTORY_COL_POINAME + "=?", new String[]{aPOIName}, null, null, null);
				/* If nothing was found... */
				if(c == null || c.getCount() == 0){
					/* Add new dataset. */
					final ContentValues cv = new ContentValues();
					cv.put(T_POIHISTORY_COL_POINAME, aPOIName);
					cv.put(T_POIHISTORY_COL_LAT, aLatitude);
					cv.put(T_POIHISTORY_COL_LNG, aLongitude);

					final long retVal = myDB.insert(T_POIHISTORY, null, cv);
					if(retVal == -1) {
						throw new DataBaseException("Failed on inserting new favorite");
					}
				}
				c.close();
			}
		}finally{
			/* Close Database */
			closeDB();
		}
	}

	private static void incrementFavoriteUses(final SQLiteDatabase aDB, final String aFavName, final int aValue) throws DataBaseException{
		final ContentValues cv = new ContentValues();
		cv.put(T_FAVS_COL_USES, aValue + 1);
		cv.put(T_FAVS_COL_LAST_USE, DATETIME_NOW);
		final int changes = aDB.update(T_FAVS, cv, T_FAVS_COL_FAVNAME + "=?", new String[]{aFavName});
		if(changes != 1) {
			throw new DataBaseException("Incrementing use failed.");
		}
	}

	public static void deleteFavoriteByName(final Context ctx, final String aFavName) throws DataBaseException {
		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);
		try {
			ensureFavoritesTableExists(myDB);

			/* Remove the favorite by its name passed as parameter. */
			final int affected = myDB.delete(T_FAVS, T_FAVS_COL_FAVNAME + "=?", new String[]{aFavName});

			if(affected != 1) {
				throw new DataBaseException("Could not delete Favorite.");
			}
		} finally {
			/* Close Database */
			closeDB();
		}
	}

	/**
	 * @param aDB
	 * @param aFavName
	 * @return if favorite did not exists: Constants.NOT_SET. Otherwise the number of uses before.
	 */
	private static int checkFavoriteExists(final SQLiteDatabase aDB, final String aFavName) {
		final Cursor c = aDB.query(T_FAVS, new String[]{T_FAVS_COL_USES}, T_FAVS_COL_FAVNAME + "=?", new String[]{aFavName}, null, null, null);

		int uses;
		if(c != null && c.getCount() > 0 && c.moveToFirst()){
			uses = c.getInt(c.getColumnIndexOrThrow(T_FAVS_COL_USES));
		}else{
			uses = org.andnav2.util.constants.Constants.NOT_SET;
		}

		if(c != null) {
			c.close();
		}

		return uses;
	}

	/**
	 * @param aDB
	 * @param aPOIName
	 * @return if favorite did not exists: Constants.NOT_SET. Otherwise the number of uses before.
	 */
	private static boolean checkPOIExists(final SQLiteDatabase aDB, final String aPOIName) {
		final Cursor c = aDB.query(T_POIHISTORY, null, T_POIHISTORY_COL_POINAME + "=?", new String[]{aPOIName}, null, null, null);

		if(c != null && c.getCount() > 0 && c.moveToFirst()){
			if(c != null) {
				c.close();
			}
			return true;
		}else{
			if(c != null) {
				c.close();
			}
			return false;
		}
	}

	public static List<DBPOI> getPOIHistory(final Context ctx) throws DataBaseException {
		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);
		ArrayList<DBPOI> poiList = null;

		try{
			ensurePOIHistoryTableExists(myDB);

			/* Query for all pois. */
			final Cursor c = myDB.query(T_POIHISTORY, new String[] {T_POIHISTORY_COL_POINAME, T_POIHISTORY_COL_LAT, T_POIHISTORY_COL_LNG},
					null, null, null, null, T_POIHISTORY_COL_POINAME + " ASC");

			/* Check if our query was valid. */
			if (c != null) {
				poiList = new ArrayList<DBPOI>(c.getCount());

				/* Get the indices of the Columns we will need */
				final int poiNameColumn = c.getColumnIndexOrThrow(T_POIHISTORY_COL_POINAME);
				final int latColumn = c.getColumnIndexOrThrow(T_POIHISTORY_COL_LAT);
				final int lngColumn = c.getColumnIndexOrThrow(T_POIHISTORY_COL_LNG);

				/* Check if at least one Result was returned. */
				if (c.moveToFirst()) {
					/* Loop through all Results */
					do {
						/* Retrieve the values of the Entry the Cursor is providing. */
						poiList.add(new DBPOI(c.getString(poiNameColumn),
								new GeoPoint(c.getInt(latColumn), c.getInt(lngColumn))));

					} while (c.moveToNext());
				}
				c.close();
			}else{
				throw new DataBaseException("Failed on reading pois");
			}

		}finally{
			/* Close Database */
			closeDB();
		}
		return poiList;
	}

	public static void clearPOIHistory(final Context ctx) throws DataBaseException {
		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);
		try{
			ensurePOIHistoryTableExists(myDB);

			myDB.delete(T_POIHISTORY, null, null);
		}finally{
			/* Close Database */
			closeDB();
		}
	}

	public static void clearFavorites(final Context ctx) throws DataBaseException {
		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);
		try{
			ensureFavoritesTableExists(myDB);

			myDB.delete(T_FAVS, null, null);
		}finally{
			/* Close Database */
			closeDB();
		}
	}

	public static List<Favorite> getFavorites(final Context ctx) throws DataBaseException {
		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);
		ArrayList<Favorite> favList = null;

		try{
			ensureFavoritesTableExists(myDB);

			/* Query for all favourites. */
			final Cursor c = myDB.query(T_FAVS, new String[] {
					T_FAVS_COL_FAVNAME, T_FAVS_COL_LAT,
					T_FAVS_COL_LNG, T_FAVS_COL_USES }, null, null, null, null, T_FAVS_COL_LAST_USE + " DESC");

			/* Check if our query was valid. */
			if (c != null) {
				favList = new ArrayList<Favorite>(c.getCount());

				/* Get the indices of the Columns we will need */
				final int favNameColumn = c.getColumnIndexOrThrow(T_FAVS_COL_FAVNAME);
				final int latColumn = c.getColumnIndexOrThrow(T_FAVS_COL_LAT);
				final int lngColumn = c.getColumnIndexOrThrow(T_FAVS_COL_LNG);
				final int usesColumn = c.getColumnIndexOrThrow(T_FAVS_COL_USES);

				/* Check if at least one Result was returned. */
				if (c.moveToFirst()) {
					/* Loop through all Results */
					do {
						/* Retrieve the values of the Entry the Cursor is providing. */
						favList.add(new Favorite(c.getString(favNameColumn),
								c.getInt(latColumn),
								c.getInt(lngColumn),
								c.getInt(usesColumn)));

					} while (c.moveToNext());
				}
				c.close();
			}else{
				throw new DataBaseException("Failed on reading favourties");
			}

		}finally{
			/* Close Database */
			closeDB();
		}
		return favList;
	}

	public static int getFavoritesCount(final Context ctx) throws DataBaseException {
		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);
		try {
			ensureFavoritesTableExists(myDB);

			/* Query for all favorites. */
			final Cursor c = myDB.query(T_FAVS, new String[] { T_FAVS_COL_ID }, null, null, null, null, null);

			/* Check if our query was valid. */
			int count;
			if (c != null) {
				count = c.getCount();
			} else {
				count = 0;
			}
			c.close();
			return count;
		} finally {
			/* Close Database */
			closeDB();
		}
	}

	public static void addZipCode(final Context ctx, final String aZipCode, final String aCountryCode) throws DataBaseException {
		addZipCodeOrCityName(ctx, aZipCode, aCountryCode, true);
	}

	public static void addCityName(final Context ctx, final String aZipCode, final String aCountryCode) throws DataBaseException {
		addZipCodeOrCityName(ctx, aZipCode, aCountryCode, false);
	}

	private static void addZipCodeOrCityName(final Context ctx, final String aZipCode, final String aCountryCode, final boolean aIsZip) throws DataBaseException {
		if(aZipCode == null || aZipCode.length() == 0) {
			return;
		}

		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);
		try {
			ensureZipsAndCitiesTableExists(myDB);

			final int isZip;
			if(aIsZip) {
				isZip = 1;
			} else {
				isZip = 0;
			}

			final int countryCodeID = getCountryCodeID(myDB, aCountryCode);
			/* Query for the zipcode passed as parameter. */
			final Cursor c = myDB.query(T_ZIPSNCITIES, null,
					T_ZIPSNCITIES_NAME + "=? AND " + T_ZIPSNCITIES_COUNTRYCODE_ID + "=" + countryCodeID,
					new String[]{aZipCode}, null, null, null);
			/* If nothing was found... */
			if (c == null || c.getCount() == 0) {
				/* Add new dataset. */
				final ContentValues cv = new ContentValues();
				cv.put(T_ZIPSNCITIES_NAME, aZipCode);
				cv.put(T_ZIPSNCITIES_COUNTRYCODE_ID, countryCodeID);
				cv.put(T_ZIPSNCITIES_ISZIP, isZip);

				final long retVal = myDB.insert(T_ZIPSNCITIES, null, cv);
				if (retVal == -1) {
					throw new DataBaseException(
					"Failed on inserting new zipcode");
				}
			}
			c.close();
		}finally{
			/* Close Database */
			closeDB();
		}
	}

	public static List<String> getCityNames(final Context ctx, final String aCountryCode) throws DataBaseException {
		return getZipCodesOrCityNames(ctx, aCountryCode, false);
	}

	public static List<String> getZipCodes(final Context ctx, final String aCountryCode) throws DataBaseException {
		return getZipCodesOrCityNames(ctx, aCountryCode, true);
	}

	public static List<String> getZipCodesOrCityNames(final Context ctx, final String aCountryCode, final boolean aGetZips) throws DataBaseException {
		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);
		ArrayList<String> out = null;

		try{
			ensureZipsAndCitiesTableExists(myDB);
			final int isZip;
			if(aGetZips) {
				isZip = 1;
			} else {
				isZip = 0;
			}

			/* Query for all zipcodes used for the CountryCode passed. */
			final Cursor c = myDB.query(T_ZIPSNCITIES,
					new String[] {T_ZIPSNCITIES_NAME},
					T_ZIPSNCITIES_COUNTRYCODE_ID + "=" + getCountryCodeID(myDB, aCountryCode) + " AND " + T_ZIPSNCITIES_ISZIP + "=" + isZip,
					null, null, null, null);

			/* Check if our query was valid. */
			if (c != null) {
				out = new ArrayList<String>(c.getCount());

				/* Get the indices of the Columns we will need */
				final int zipOrNameColumn = c.getColumnIndexOrThrow(T_ZIPSNCITIES_NAME);

				/* Check if at least one Result was returned. */
				if (c.moveToFirst()) {
					/* Loop through all Results */
					do {
						/* Retrieve the values of the Entry the Cursor is providing. */
						out.add(c.getString(zipOrNameColumn));
					} while (c.moveToNext());
				}
				c.close();
			}else{
				throw new DataBaseException("Failed on reading used zipcodes for countrycode");
			}

		}finally{
			/* Close Database */
			closeDB();
		}
		return out;
	}

	private static int getCountryCodeID(final SQLiteDatabase myDB, final String aCountryCode) throws DataBaseException{
		ensureCountryCodeIDTableExists(myDB);

		final Cursor c = myDB.query(T_COUNTRYCODES, new String[] { T_COUNTRYCODES_ID }, T_COUNTRYCODES_NAME + "=?", new String[]{aCountryCode}, null, null, null);

		if (c == null || c.getCount() == 0) { // Not found --> create a new entry
			insertCountryCode(myDB, aCountryCode);
			if(!c.requery()) {
				throw new DataBaseException("Failed on requerying after inserting countrycode");
			}
		}
		/* It is now ensured, that something will be returned. */
		final int countryCodeIDColumn = c.getColumnIndexOrThrow(T_COUNTRYCODES_ID);
		if(c.moveToFirst()){
			final int i = c.getInt(countryCodeIDColumn);
			c.close();
			return i;
		}else{
			throw new DataBaseException("Failed on reading countrycode");
		}
	}

	private static int getZipCodeOrCityNameID(final SQLiteDatabase myDB, final String aZipCodeOrCityName, final String aCountryCode) throws DataBaseException{
		ensureZipsAndCitiesTableExists(myDB);

		final Cursor c = myDB.query(T_ZIPSNCITIES,
				new String[] { T_ZIPSNCITIES_ID },
				T_ZIPSNCITIES_COUNTRYCODE_ID + "=" + getCountryCodeID(myDB, aCountryCode) + " AND " + T_ZIPSNCITIES_NAME + "=?",
				new String[]{aZipCodeOrCityName}, null, null, null);

		if (c == null || c.getCount() == 0) { // Not found --> create a new entry
			throw new DataBaseException("Failed on getting ZipCodeOrCitynameID. Logically it is ensured that something is returned here.");
		}
		/* It is now ensured, that something will be returned. */
		final int zipCodeIDColumn = c.getColumnIndexOrThrow(T_ZIPSNCITIES_ID);
		if(c.moveToFirst()){
			final int i = c.getInt(zipCodeIDColumn);
			c.close();
			return i;
		}else{
			throw new DataBaseException("Failed on reading countrycode");
		}
	}

	private static void insertCountryCode(final SQLiteDatabase myDB, final String aCountryCode) throws DataBaseException{
		/* Add new dataset. */
		final ContentValues cv = new ContentValues();
		cv.put(T_COUNTRYCODES_NAME, aCountryCode);

		final long retVal = myDB.insert(T_COUNTRYCODES, null, cv);
		if (retVal == -1) {
			throw new DataBaseException("Failed on inserting new countrycode");
		}
	}

	public static List<String> getStreetNames(final Context ctx, final String aZipCodeOrCityName, final String aCountryCode) throws DataBaseException{
		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);
		ArrayList<String> streetsList = null;

		try{
			ensureStreetsTableExists(myDB);

			/* Query for all streetnames used for the ZipOrCity/CountryCode-Combination passed. */
			final Cursor c = myDB.query(T_STREETS, new String[] {T_STREETS_NAME}, T_STREETS_ZIPORCITY_ID + "=" + getZipCodeOrCityNameID(myDB, aZipCodeOrCityName, aCountryCode), null, null, null, null);

			/* Check if our query was valid. */
			if (c != null) {
				streetsList = new ArrayList<String>(c.getCount());

				/* Get the indices of the Columns we will need */
				final int zipCodeColumn = c.getColumnIndexOrThrow(T_STREETS_NAME);

				/* Check if at least one Result was returned. */
				if (c.moveToFirst()) {
					/* Loop through all Results */
					do {
						/* Retrieve the values of the Entry the Cursor is providing. */
						streetsList.add(c.getString(zipCodeColumn));
					} while (c.moveToNext());
				}
				c.close();
			}else{
				throw new DataBaseException("Failed on reading used zipcodes for countrycode");
			}

		}finally{
			/* Close Database */
			closeDB();
		}
		return streetsList;
	}

	public static void addStreet(final Context ctx, final String aStreetName, final String aZipCodeOrCityName, final String aCountryCode) throws DataBaseException{
		if(aStreetName == null || aStreetName.length() == 0) {
			return;
		}

		/* Prepare Database */
		final SQLiteDatabase myDB = ensureDBInstanceExists(ctx);
		try {
			ensureStreetsTableExists(myDB);
			final int zipCodeOrCityNameID = getZipCodeOrCityNameID(myDB, aZipCodeOrCityName, aCountryCode);
			/* Query for the zipcode passed as parameter. */
			final Cursor c = myDB.query(T_STREETS, null,
					T_STREETS_NAME + "=? AND " + T_STREETS_ZIPORCITY_ID + "=" + zipCodeOrCityNameID,
					new String[]{aStreetName}, null, null, null);
			/* If nothing was found... */
			if (c == null || c.getCount() == 0) {
				/* Add new dataset. */
				final ContentValues cv = new ContentValues();
				cv.put(T_STREETS_NAME, aStreetName);
				cv.put(T_STREETS_ZIPORCITY_ID, zipCodeOrCityNameID);

				final long retVal = myDB.insert(T_STREETS, null, cv);
				if (retVal == -1) {
					throw new DataBaseException(
					"Failed on inserting new street");
				}
			}
			c.close();
		}finally{
			/* Close Database */
			closeDB();
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static enum AndNavSQLTableInfo {
		FAVORITES(T_FAVS, CREATE_FAVORITES_TABLE),
		COUNTRYCODES(T_COUNTRYCODES, CREATE_COUNTRYCODE_TABLE),
		STREETS(T_STREETS, CREATE_FAVORITES_TABLE),
		ZIPSANDCITIES(T_ZIPSNCITIES, CREATE_ZIPSANDCITIES_TABLE);

		public final String mTableName;
		public final String mCreateCommand;

		private AndNavSQLTableInfo(final String tableName, final String createommand) {
			this.mTableName = tableName;
			this.mCreateCommand = createommand;
		}
	}

	private static class AndNavDatabaseHelper extends SQLiteOpenHelper {

		protected final AndNavSQLTableInfo[] mTableInfo;

		AndNavDatabaseHelper(final Context context, final AndNavSQLTableInfo ... aTableInfo) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.mTableInfo = aTableInfo;
		}

		@Override
		public void onCreate(final SQLiteDatabase db) {
			for (final AndNavSQLTableInfo i : this.mTableInfo) {
				db.execSQL(i.mCreateCommand);
			}
		}

		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
			Log.w(Constants.DEBUGTAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

			for (final AndNavSQLTableInfo i : this.mTableInfo) {
				db.execSQL("DROP TABLE IF EXISTS " + i.mTableName);
			}

			onCreate(db);
		}
	}
}
