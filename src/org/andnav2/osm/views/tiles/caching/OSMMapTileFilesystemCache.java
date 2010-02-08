// Created by plusminus on 21:46:41 - 25.09.2008
package org.andnav2.osm.views.tiles.caching;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.andnav2.exc.Exceptor;
import org.andnav2.osm.exceptions.EmptyCacheException;
import org.andnav2.osm.exceptions.ExternalStorageNotMountedException;
import org.andnav2.osm.util.Util;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.osm.views.tiles.OSMMapTileProviderInfo;
import org.andnav2.osm.views.tiles.adt.OSMTileInfo;
import org.andnav2.osm.views.util.NoMagicNumberException;
import org.andnav2.osm.views.util.StreamUtils;
import org.andnav2.osm.views.util.constants.OSMMapViewConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class OSMMapTileFilesystemCache extends AbstractOSMMapTileFilesystemCache implements OSMConstants, OSMMapViewConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final String EXTERNAL_STORAGE_BASEDIRECTORY;

	private OSMMapTileFilesystemCacheDataBase mDatabase;

	private final StoragePolicy mStoragePolicy;

	private final Set<String> mTilesAlreadyUpdatedInDatabase = new HashSet<String>();

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * @param ctx
	 * @param aStoragePolicy
	 * @param aMaxFSCacheByteSize the size of the cached MapTiles will not exceed this size.
	 * @param aCache to load fs-tiles to. Can be null, i.e. to get just the stats
	 * @throws ExternalStorageNotMountedException when Policy is {@link StoragePolicy.EXTERNAL} and it is not mounted.
	 */
	public OSMMapTileFilesystemCache(final Context ctx, final StoragePolicy aStoragePolicy, final int aMaxFSCacheByteSize, final OSMMapTileMemoryCache aCache, final OSMMapTileProviderInfo pProviderInfo) throws ExternalStorageNotMountedException {
		super(ctx, aMaxFSCacheByteSize, aCache, pProviderInfo);

		switch(aStoragePolicy){
			case EXTERNAL:
				if(!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
					throw new ExternalStorageNotMountedException();
				}else{
					this.EXTERNAL_STORAGE_BASEDIRECTORY = Util.getAndNavExternalStoragePath();

					// Ensure the tiles-directory exists.
					new File(this.EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_TILE_PATH).mkdirs();
				}
				break;
			case INTERNALROM:
				this.EXTERNAL_STORAGE_BASEDIRECTORY = null;
				break;
			default:
				throw new IllegalArgumentException("Unknown StoragePolicy");
		}

		this.mStoragePolicy = aStoragePolicy;

		this.mDatabase = new OSMMapTileFilesystemCacheDataBase(this.mContext, aStoragePolicy);
		this.mCurrentFSCacheByteSize = this.updateCurrentFSCacheByteSize();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public StoragePolicy getStoragePolicy(){
		return this.mStoragePolicy;
	}

	public void release() {
		super.release();
		this.mDatabase.release();
	}

	/**
	 * Deletes a file based on the StoragePolicy of this instance.
	 * @param aFormattedFileName
	 * @throws FileNotFoundException on non-existence of the file.
	 */
	private void deleteFileBasedOnStoragePolicy(final String aFormattedFileName) throws FileNotFoundException {
		switch(this.getStoragePolicy()){
			case INTERNALROM:
				this.mContext.deleteFile(aFormattedFileName);
				break;
			case EXTERNAL:
				final File f = new File(this.EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_TILE_PATH + aFormattedFileName);
				if(f.exists()){
					f.delete();
				}else{
					throw new FileNotFoundException("Filename: " + aFormattedFileName);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown StoragePolicy");
		}
	}

	/**
	 * Opens a file for input based on the StoragePolicy of this instance.
	 * @param aFormattedFileName
	 * @return on existence the {@link InputStream}, on non-existence: <code>null</code> or a <code>FileNotFoundException</code>.
	 * @throws FileNotFoundException
	 */
	private InputStream openFileInputBasedOnStoragePolicy(final String aFormattedFileName) throws FileNotFoundException{
		switch(this.getStoragePolicy()){
			case INTERNALROM:
				final FileInputStream fileIn = this.mContext.openFileInput(aFormattedFileName);
				return fileIn;
			case EXTERNAL:
				final File f = new File(this.EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_TILE_PATH + aFormattedFileName);

				final long fileSize = f.length();
				if(fileSize < 10){
					Log.w(DEBUGTAG, "Deleted empty MapTile: " + aFormattedFileName);
					f.delete();
					return null;
				}

				if(f.exists()) {
					return new FileInputStream(f);
				} else {
					return null;
				}
			default:
				throw new IllegalArgumentException("Unknown StoragePolicy");
		}
	}

	/**
	 * Opens a file for output based on the {@link StoragePolicy} of this instance.
	 * @param aFormattedFileName
	 * @return the {@link OutputStream}
	 * @throws FileNotFoundException
	 */
	private OutputStream openFileOutputBasedOnStoragePolicy(final String aFileName) throws FileNotFoundException{
		switch(this.getStoragePolicy()){
			case INTERNALROM:
				return this.mContext.openFileOutput(aFileName, Context.MODE_WORLD_READABLE);
			case EXTERNAL:
				final String fullPath = this.EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_TILE_PATH + aFileName;
				/* Crop the filename and ensure the folder exists. */
				new File(fullPath.substring(0, fullPath.lastIndexOf('/'))).mkdirs(); // TODO Zieh performance?
				return new FileOutputStream(new File(fullPath));
			default:
				throw new IllegalArgumentException("Unknown StoragePolicy");
		}
	}

	public boolean exists(final String pRawTileURLString, final String pSaveableURLString){
		switch(this.getStoragePolicy()){
			case INTERNALROM:
				InputStream in = null;
				try{
					in = openFileInputBasedOnStoragePolicy(pSaveableURLString);
					return in != null;
				}catch(final Exception e){
					return false;
				}finally{
					StreamUtils.closeStream(in);
				}
			case EXTERNAL:
				return new File(this.EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_TILE_PATH + pRawTileURLString).exists();
			default:
				throw new IllegalArgumentException("Unknown StoragePolicy");
		}
	}

	/**
	 * 
	 * @param aTileInfo
	 * @param aSaveableTileURLString
	 * @param loadCallbackHandler
	 * @return
	 */
	public boolean tryLoadMapTileByParentToMemCacheAsync(final OSMTileInfo aTileInfo, final String aSaveableTileURLString, final Handler loadCallbackHandler) {
		final OSMTileInfo tileInfoParent = aTileInfo.getParentTile();
		final String aSaveableTileURLStringParent = this.mProviderInfo.getSaveableTileURLString(tileInfoParent, this.getStoragePolicy());

		final InputStream in;
		try{
			final InputStream fileIn = this.openFileInputBasedOnStoragePolicy(aSaveableTileURLStringParent);

			if(fileIn == null) {
				return false;
			}

			in = new BufferedInputStream(fileIn, StreamUtils.IO_BUFFER_SIZE);
		}catch (final FileNotFoundException fnfe){
			return false;
		}

		final int positionOfChildInParent = aTileInfo.getPositionInParent(tileInfoParent);

		this.mPendingDirty.add(aSaveableTileURLString);

		this.mThreadPool.execute(new Runnable(){
			public void run() {
				Process.setThreadPriority(Process.THREAD_PRIORITY_LESS_FAVORABLE);
				try {
					Bitmap bmpParent;
					if(".png".equals(OSMMapTileFilesystemCache.this.mProviderInfo.IMAGE_FILENAMEENDING)){
						try{
							bmpParent = StreamUtils.loadBitmapFromStreamDetectingPNGMagicNumber(in);
						}catch(final NoMagicNumberException nmne){
							deleteFileBasedOnStoragePolicy(aSaveableTileURLString);
							bmpParent = null;
						}
					}else{
						bmpParent = StreamUtils.loadBitmapFromStream(in);
					}

					if(bmpParent != null) {
						OSMMapTileFilesystemCache.this.mCache.putMapTile(aSaveableTileURLString, bmpParent);
					}

					final Matrix m = new Matrix();
					m.preScale(2,2);

					final int maptile_sizepx = OSMMapTileFilesystemCache.this.mProviderInfo.MAPTILE_SIZEPX;

					switch(positionOfChildInParent){
						case OSMTileInfo.POSITION_IN_PARENT_TOPLEFT:
							break;
						case OSMTileInfo.POSITION_IN_PARENT_TOPRIGHT:
							m.postTranslate(-maptile_sizepx, 0);
							break;
						case OSMTileInfo.POSITION_IN_PARENT_BOTTOMLEFT:
							m.postTranslate(0, -maptile_sizepx);
							break;
						case OSMTileInfo.POSITION_IN_PARENT_BOTTOMRIGHT:
							m.postTranslate(-maptile_sizepx, -maptile_sizepx);
							break;
					}

					final Bitmap bmpChild = Bitmap.createBitmap(maptile_sizepx, maptile_sizepx, Config.RGB_565);
					new Canvas(bmpChild).drawBitmap(bmpParent, m, new Paint());

					bmpParent.recycle();

					OSMMapTileFilesystemCache.this.mCache.putMapTile(aSaveableTileURLString + OSMMapTileMemoryCache.FLAG_DIRTY, bmpChild);

					final Message successMessage = Message.obtain(loadCallbackHandler, MAPTILEFSCACHE_SUCCESS_ID);
					successMessage.arg1 = aTileInfo.x;
					successMessage.arg2 = aTileInfo.y;
					successMessage.sendToTarget();

					if(DEBUGMODE) {
						Log.d(DEBUGTAG, "Loaded: " + aSaveableTileURLString + " to MemCache.");
					}
				} catch (final Exception e) {
					final Message failMessage = Message.obtain(loadCallbackHandler, MAPTILEFSCACHE_FAIL_ID);
					failMessage.sendToTarget();
					if(DEBUGMODE) {
						Log.e(DEBUGTAG, "Error Loading MapTile from FS. Exception: " + e.getClass().getSimpleName(), e);
					}
				} finally {
					StreamUtils.closeStream(in);
				}

				OSMMapTileFilesystemCache.this.mPendingDirty.remove(aSaveableTileURLString);
			}
		});
		return true;
	}

	/**
	 * 
	 * @param coords
	 * @param aSaveableTileURLString
	 * @param loadCallbackHandler
	 * @return <code>true</code> when file is now loaded. <code>false</code> if file did not exist and should be downloaded/rendered.
	 * @throws FileNotFoundException
	 */
	public boolean loadMapTileToMemCacheAsync(final OSMTileInfo aTileInfo, final String aSaveableTileURLString, final Handler loadCallbackHandler) {
		final InputStream in;
		try{
			final InputStream fileIn = this.openFileInputBasedOnStoragePolicy(aSaveableTileURLString);

			if(fileIn == null) {
				return false;
			}

			in = new BufferedInputStream(fileIn, StreamUtils.IO_BUFFER_SIZE);
		}catch (final FileNotFoundException fnfe){
			return false;
		}

		this.mPending.add(aSaveableTileURLString);

		this.mThreadPool.execute(new Runnable(){
			public void run() {
				Process.setThreadPriority(Process.THREAD_PRIORITY_LESS_FAVORABLE);
				try {
					// File exists, otherwise a FileNotFoundException would have been thrown
					OSMMapTileFilesystemCache.this.mDatabase.incrementUse(aSaveableTileURLString);
					Bitmap bmp;
					if(".png".equals(OSMMapTileFilesystemCache.this.mProviderInfo.IMAGE_FILENAMEENDING)){
						try{
							bmp = StreamUtils.loadBitmapFromStreamDetectingPNGMagicNumber(in);
						}catch(final NoMagicNumberException nmne){
							deleteFileBasedOnStoragePolicy(aSaveableTileURLString);
							bmp = null;
						}
					}else{
						bmp = StreamUtils.loadBitmapFromStream(in);
					}

					if(bmp != null) {
						OSMMapTileFilesystemCache.this.mCache.putMapTile(aSaveableTileURLString, bmp);
					}

					final Message successMessage = Message.obtain(loadCallbackHandler, MAPTILEFSCACHE_SUCCESS_ID);
					successMessage.arg1 = aTileInfo.x;
					successMessage.arg2 = aTileInfo.y;
					successMessage.sendToTarget();

					if(DEBUGMODE) {
						Log.d(DEBUGTAG, "Loaded: " + aSaveableTileURLString + " to MemCache.");
					}
				} catch (final Exception e) {
					final Message failMessage = Message.obtain(loadCallbackHandler, MAPTILEFSCACHE_FAIL_ID);
					failMessage.sendToTarget();
					if(DEBUGMODE) {
						Log.e(DEBUGTAG, "Error Loading MapTile from FS. Exception: " + e.getClass().getSimpleName(), e);
					}
				} finally {
					StreamUtils.closeStream(in);
				}

				OSMMapTileFilesystemCache.this.mPending.remove(aSaveableTileURLString);
			}
		});

		//		if(this.mDatabase.addTileOrIncrement(aSaveableTileURLString, 0) < 0)
		//			return false;
		//		else
		return true;
	}

	/**
	 * Saves the file based on this instances StoragePolicy
	 * @param coords
	 * @param zoomLevel
	 * @param someData
	 * @throws IOException
	 */
	public void saveFile(final String aSaveableURLString, final byte[] someData) throws IOException{

		final OutputStream os = this.openFileOutputBasedOnStoragePolicy(aSaveableURLString);
		final BufferedOutputStream bos = new BufferedOutputStream(os, StreamUtils.IO_BUFFER_SIZE);
		bos.write(someData);

		bos.flush();
		bos.close();

		if(!this.mTilesAlreadyUpdatedInDatabase.contains(aSaveableURLString)){
			updateCacheSize(aSaveableURLString, someData.length);
			this.mTilesAlreadyUpdatedInDatabase.add(aSaveableURLString);
		}
	}

	/**
	 * 
	 * @param aSaveableURLString
	 * @param pDataSize in Bytes
	 */
	private void updateCacheSize(final String aSaveableURLString, final int pDataSize) {
		//		synchronized (this) {
		final int bytesGrown = this.mDatabase.addTileOrIncrement(aSaveableURLString, pDataSize);
		if(bytesGrown >= 0) {
			this.mCurrentFSCacheByteSize += bytesGrown;
		}

		if(DEBUGMODE) {
			Log.i(DEBUGTAG, "FSCache Size is now: " + this.mCurrentFSCacheByteSize + " Bytes");
		}

		/* If Cache is full... */
		try {

			if(this.mCurrentFSCacheByteSize > this.mMaxFSCacheByteSize){
				if(DEBUGMODE) {
					Log.d(DEBUGTAG, "Freeing FS cache...");
				}
				// 5% of cache , but less than 500kB
				final int sizeToFree = Math.min(500000, (int)(this.mMaxFSCacheByteSize * 0.05f));
				this.mCurrentFSCacheByteSize -= this.mDatabase.deleteOldest(sizeToFree);
			}
		} catch (final EmptyCacheException e) {
			if(DEBUGMODE) {
				Log.e(DEBUGTAG, "Cache empty", e);
			}
		}
		//		}
	}

	public void cutCurrentFSCacheBy(final int bytesToCut){
		try {
			final int sizeGained = this.mDatabase.deleteOldest(bytesToCut);
			this.mCurrentFSCacheByteSize = Math.max(0, this.mCurrentFSCacheByteSize - sizeGained);
		} catch (final EmptyCacheException e) {
			if(DEBUGMODE) {
				Log.e(DEBUGTAG, "Cache empty", e);
			}
		}
	}

	@Override
	protected int updateCurrentFSCacheByteSize() {
		return this.mDatabase.getCurrentFSCacheByteSize();
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static enum StoragePolicy {
		EXTERNAL(OSMMapTileFilesystemCacheDataBaseConstants.SDCARD_DATABASE_FILENAME),
		INTERNALROM(OSMMapTileFilesystemCacheDataBaseConstants.DATABASE_NAME);

		final String DATABASE_PATH;

		private StoragePolicy(final String aDatabasePath){
			this.DATABASE_PATH = aDatabasePath;
		}
	}

	private interface OSMMapTileFilesystemCacheDataBaseConstants{
		public static final String DATABASE_NAME = "osm_maptile_fscache_db.db";

		public static final String SDCARD_DATABASE_FILENAME = DATABASE_NAME;

		public static final int DATABASE_VERSION = 5;


		final String DROP_TABLE_ = "DROP TABLE IF EXISTS ";

		public static final String T_FSCACHE = "t_fscache";
		public static final String T_FSCACHE_NAME = "name_id";
		public static final String T_FSCACHE_CREATION_TIMESTAMP = "creation";
		public static final String T_FSCACHE_LASTUSE_TIMESTAMP = "lastuse";
		public static final String T_FSCACHE_USAGECOUNT = "countused";
		public static final String T_FSCACHE_FILESIZE = "filesize";

		public static final String T_FSCACHE_CREATE_COMMAND = "CREATE TABLE IF NOT EXISTS " + T_FSCACHE
		+ " ("
		+ T_FSCACHE_NAME + " VARCHAR(255),"
		+ T_FSCACHE_LASTUSE_TIMESTAMP + " INTEGER NOT NULL,"
		+ T_FSCACHE_CREATION_TIMESTAMP + " INTEGER NOT NULL,"
		+ T_FSCACHE_USAGECOUNT + " INTEGER NOT NULL DEFAULT 1,"
		+ T_FSCACHE_FILESIZE + " INTEGER NOT NULL,"
		+ " PRIMARY KEY(" + T_FSCACHE_NAME + ")"
		+ ");";

//		public static final String T_FSCACHE_SELECT_LEAST_USED = "SELECT " + T_FSCACHE_NAME  + "," + T_FSCACHE_FILESIZE + " FROM " + T_FSCACHE + " WHERE "  + T_FSCACHE_USAGECOUNT + " = (SELECT MIN(" + T_FSCACHE_USAGECOUNT + ") FROM "  + T_FSCACHE + ")";
		public static final String T_FSCACHE_SELECT_OLDEST = "SELECT " + T_FSCACHE_NAME + "," + T_FSCACHE_FILESIZE + " FROM " + T_FSCACHE + " ORDER BY " + T_FSCACHE_LASTUSE_TIMESTAMP + " ASC";
	}

	private class OSMMapTileFilesystemCacheDataBase implements OSMMapTileFilesystemCacheDataBaseConstants, OSMMapViewConstants{
		// ===========================================================
		// Fields
		// ===========================================================

		//		private Context mContext;
		private final SQLiteDatabase mDatabase;
		//		private final SimpleDateFormat DATE_FORMAT_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

		// ===========================================================
		// Constructors
		// ===========================================================

		public OSMMapTileFilesystemCacheDataBase(final Context context, final StoragePolicy aStoragePolicy) {
			//			this.mContext = context;

			switch(aStoragePolicy){
				case EXTERNAL:
					// TODO java.lang.IllegalStateException: /sdcard/andnav2/osm_maptile_fscache_db.db SQLiteDatabase created and never closed
					this.mDatabase = SQLiteDatabase.openDatabase(OSMMapTileFilesystemCache.this.EXTERNAL_STORAGE_BASEDIRECTORY + aStoragePolicy.DATABASE_PATH, null, SQLiteDatabase.CREATE_IF_NECESSARY);

					/* Determine if the database needs to be updated. */
					final int oldVersion = getDatabaseVersion();
					if(DATABASE_VERSION != oldVersion){
						this.mDatabase.execSQL(DROP_TABLE_ + T_FSCACHE);
						try {
							setDatabaseVersion(DATABASE_VERSION);
						} catch (final IOException e) {
							Exceptor.e("Error setting new database-version", e, context);
						}
					}

					this.mDatabase.execSQL(T_FSCACHE_CREATE_COMMAND);
					break;
				case INTERNALROM:
					this.mDatabase = new AndNavDatabaseHelper(context, aStoragePolicy).getWritableDatabase();
					break;
				default:
					throw new IllegalArgumentException("Unknown StoragePolicy");
			}
		}

		private int getDatabaseVersion() {
			int out = NOT_SET;
			InputStream is = null;
			Scanner sc = null;
			try {
				is = openFileInputBasedOnStoragePolicy(StoragePolicy.EXTERNAL.DATABASE_PATH + ".version");
				if(is == null){
					return NOT_SET;
				}else{
					sc = new Scanner(is);
					if(sc.hasNextInt()){
						out = sc.nextInt();
					}
				}
			} catch (final FileNotFoundException e) {
				return NOT_SET;
			} finally {
				if(sc != null) {
					sc.close();
				}
			}
			return out;
		}

		private void setDatabaseVersion(final int aVersion) throws IOException {
			OutputStream out = null;
			try {
				out = openFileOutputBasedOnStoragePolicy(StoragePolicy.EXTERNAL.DATABASE_PATH + ".version");
				out.write(String.valueOf(aVersion).getBytes());
			} finally {
				StreamUtils.closeStream(out);
			}
		}

		public void incrementUse(final String aFormattedTileURLString) {
			Cursor c = null;
			try{
				c = this.mDatabase.rawQuery("UPDATE " + T_FSCACHE + " SET " + T_FSCACHE_USAGECOUNT + " = " + T_FSCACHE_USAGECOUNT + " + 1 , " + T_FSCACHE_LASTUSE_TIMESTAMP + " = '" + getNowAsTimeStamp() + "' WHERE " + T_FSCACHE_NAME + " = '" + aFormattedTileURLString + "'", null);
			}finally{
				if(c != null) {
					c.close();
				}
			}
		}

		/**
		 * 
		 * @param aFormattedTileURLString
		 * @param aByteFilesize
		 * @return <ul><li><code>-1</code> if tile existed but is old and so should be re-downloaded</li>
		 * <li><code>0</code> if tile existed and is fine</li>
		 * <li><code>&gt;0</code> if tile is new, the returned value is the added size</li></ul>
		 */
		public int addTileOrIncrement(final String aFormattedTileURLString, final int aByteFilesize) {
			final Cursor c = this.mDatabase.rawQuery("SELECT " + T_FSCACHE_NAME + " FROM " + T_FSCACHE + " WHERE " + T_FSCACHE_NAME + " = '" + aFormattedTileURLString + "'", null);
			if(c == null){
				return 0;
			}else{
				try{
					final boolean existed = c.getCount() > 0;
					if(DEBUGMODE) {
						Log.d(DEBUGTAG, "Tile existed=" + existed);
					}
					if(existed){
						incrementUse(aFormattedTileURLString);
						//				final long creationTimestamp = getCreationTimestamp(aFormattedTileURLString);
						//				if(getNowAsTimeStamp() - creationTimestamp > TimeConstants.SECONDSPERMONTH * 1000)
						//					return - 1; // Tile is old and should be re-downloaded.
						//				else
						return 0;
					}else{
						insertNewTileInfo(aFormattedTileURLString, aByteFilesize);
						return aByteFilesize;
					}
				}finally{
					c.close();
				}
			}
		}

		//		private long getCreationTimestamp(final String aFormattedTileURLString) {
		//			final Cursor c = this.mDatabase.rawQuery("SELECT " + T_FSCACHE_CREATION_TIMESTAMP + " FROM " + T_FSCACHE + " WHERE " + T_FSCACHE_NAME + " = '" + aFormattedTileURLString + "'", null);
		//			final long timestamp;
		//			if(c != null){
		//				if(c.moveToFirst()){
		//						timestamp = c.getLong(c.getColumnIndexOrThrow(T_FSCACHE_CREATION_TIMESTAMP));
		//
		//						if(DEBUGMODE)
		//							Log.i(DEBUGTAG, "Timestamp read from TIle: " + aFormattedTileURLString + " is " + timestamp);
		//				}else{
		//					timestamp = Long.MAX_VALUE;
		//				}
		//				c.close();
		//			}else{
		//				timestamp = Long.MAX_VALUE;
		//			}
		//			return timestamp;
		//		}

		private void insertNewTileInfo(final String aFormattedTileURLString, final int aByteFilesize) {
			final long nowAsTimestamp = getNowAsTimeStamp();

			final ContentValues cv = new ContentValues();
			cv.put(T_FSCACHE_NAME, aFormattedTileURLString);
			cv.put(T_FSCACHE_LASTUSE_TIMESTAMP, nowAsTimestamp);
			cv.put(T_FSCACHE_CREATION_TIMESTAMP, nowAsTimestamp);
			cv.put(T_FSCACHE_FILESIZE, aByteFilesize);
			this.mDatabase.insert(T_FSCACHE, null, cv);
		}

		private int deleteOldest(final int pSizeNeeded) throws EmptyCacheException {
			final Cursor c = this.mDatabase.rawQuery(T_FSCACHE_SELECT_OLDEST, null);
			int sizeGained = 0;
			if(c != null){
				try{
					final ArrayList<String> deleteFromDB = new ArrayList<String>();
					String fileNameOfDeleted;
					final int colFilesize = c.getColumnIndexOrThrow(T_FSCACHE_FILESIZE);
					final int colName = c.getColumnIndexOrThrow(T_FSCACHE_NAME);
					if(c.moveToFirst()){
						do{
							final int sizeItem = c.getInt(colFilesize);
							sizeGained += sizeItem;
							fileNameOfDeleted = c.getString(colName);

							deleteFromDB.add(fileNameOfDeleted);
							try {
								deleteFileBasedOnStoragePolicy(fileNameOfDeleted);
							} catch (final FileNotFoundException e) {
								// If the file doesn't exist it probably was manually deleted.
							}

							if(DEBUGMODE) {
								Log.i(DEBUGTAG, "Deleted from FS: " + fileNameOfDeleted + " for " + sizeItem + " Bytes");
							}
						}while(c.moveToNext() && sizeGained < pSizeNeeded);
					}else{
						throw new EmptyCacheException("Cache seems to be empty.");
					}

					/*
					 * E/AndroidRuntime(  316): Uncaught handler: thread Preloader-Thread exiting due to uncaught exception
					 * E/AndroidRuntime(  316): android.database.sqlite.SQLiteDiskIOException: error code 10
					 * 
					 * java.lang.IllegalStateException: database not open
					 */
					for(final String fn : deleteFromDB) {
						this.mDatabase.delete(T_FSCACHE, T_FSCACHE_NAME + "='" + fn + "'", null);
					}
				}catch(final Throwable t){
					//					Exceptor.e(DEBUGTAG, t, OSMMapTileFilesystemCache.this.mContext);
				}finally{
					c.close();
				}
			}
			return sizeGained;
		}

		// ===========================================================
		// Methods
		// ===========================================================

		public void release() {
			//			this.mContext = null;
			if(this.mDatabase != null && this.mDatabase.isOpen()) {
				this.mDatabase.close();
			}
		}

		private final String TMP_COLUMN = "tmp";
		public int getCurrentFSCacheByteSize() {
			final Cursor c = this.mDatabase.rawQuery("SELECT SUM(" + T_FSCACHE_FILESIZE + ") AS " + this.TMP_COLUMN + " FROM " + T_FSCACHE, null);
			final int ret;
			if(c != null){
				try{
					if(c.moveToFirst()){
						ret = c.getInt(c.getColumnIndexOrThrow(this.TMP_COLUMN));
					}else{
						ret = 0;
					}
				}finally{
					c.close();
				}
			}else{
				ret = 0;
			}

			return ret;
		}

		//		/**
		//		 * Get at the moment within ISO8601 format.
		//		 * @return
		//		 * Date and time in ISO8601 format.
		//		 */
		//		private String getNowAsIso8601() {
		//			return DATE_FORMAT_ISO8601.format(new Date(System.currentTimeMillis()));
		//		}

		private long getNowAsTimeStamp() {
			return System.currentTimeMillis();
		}

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================

		private class AndNavDatabaseHelper extends SQLiteOpenHelper {
			AndNavDatabaseHelper(final Context context, final StoragePolicy aStoragePolicy) {
				super(context, aStoragePolicy.DATABASE_PATH, null, DATABASE_VERSION);
			}

			@Override
			public void onCreate(final SQLiteDatabase db) {
				db.execSQL(T_FSCACHE_CREATE_COMMAND);
			}

			@Override
			public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
				if(DEBUGMODE) {
					Log.w(DEBUGTAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
				}

				db.execSQL(DROP_TABLE_ + T_FSCACHE);

				onCreate(db);
			}
		}
	}
}