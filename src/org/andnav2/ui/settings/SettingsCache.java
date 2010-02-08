// Created by plusminus on 18:41:23 - 10.04.2008
package org.andnav2.ui.settings;

import java.io.File;

import org.andnav2.R;
import org.andnav2.osm.exceptions.ExternalStorageNotMountedException;
import org.andnav2.osm.views.tiles.OSMMapTileProviderInfo;
import org.andnav2.osm.views.tiles.caching.OSMMapTileFilesystemCache;
import org.andnav2.osm.views.tiles.caching.OSMMapTileFilesystemCache.StoragePolicy;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class SettingsCache extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static final int EXTERNAL_MEDIA_MEGABYTESIZE_DEFAULT = 1024;
	private int EXTERNAL_MEDIA_MEGABYTESIZE = EXTERNAL_MEDIA_MEGABYTESIZE_DEFAULT;

	protected TextView mTvCurrentCacheSize;

	protected OSMMapTileFilesystemCache mFSTileProvider;

	private SeekBar mSeekBarMaxCacheSize;
	private TextView mTvMaxCacheSizeTitle;

	private StoragePolicy mFilesystemCachePolicy;

	private ToggleButton mTbUseSD;

	private SeekBar.OnSeekBarChangeListener mMaxSizeChangeListener;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_cache);

		this.mTbUseSD = (ToggleButton)this.findViewById(R.id.ibtn_settings_cache_use_sdcard);

		this.mFilesystemCachePolicy = Preferences.getFilesystemCachePolicy(this);

		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			this.findViewById(R.id.ibtn_settings_cache_sdcard).setEnabled(false);
			this.mTbUseSD.setEnabled(false);
			this.mFilesystemCachePolicy = StoragePolicy.INTERNALROM;
		}else{
			try {
				final File path = Environment.getExternalStorageDirectory();
				final StatFs stat = new StatFs(path.getPath());
				final long blockSize = stat.getBlockSize();
				final long totalBlocks = stat.getBlockCount();
				//				final long availableBlocks = stat.getAvailableBlocks();

				this.EXTERNAL_MEDIA_MEGABYTESIZE = (int)(blockSize * totalBlocks / (1024 * 1024));

			} catch (final IllegalArgumentException e) {
				// this can occur if the SD card is removed, but we haven't
				// received the ACTION_MEDIA_REMOVED Intent yet.

				this.EXTERNAL_MEDIA_MEGABYTESIZE = EXTERNAL_MEDIA_MEGABYTESIZE_DEFAULT; // DEFAULT
			}
		}
		switch(this.mFilesystemCachePolicy){
			case EXTERNAL:
				this.mTbUseSD.setChecked(true);
				break;
			case INTERNALROM:
				this.mTbUseSD.setChecked(false);
				break;
		}

		try {
			this.mFSTileProvider = new OSMMapTileFilesystemCache(this, this.mFilesystemCachePolicy, Integer.MAX_VALUE, null, OSMMapTileProviderInfo.MAPNIK);
		} catch (final ExternalStorageNotMountedException e) {
			Log.e(DEBUGTAG, "Error initializing FSCache.", e);
		}

		this.mMaxSizeChangeListener = new OnSeekBarChangeListener(){
			public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromTouch) {
				Preferences.saveMaxCacheSize(SettingsCache.this, SettingsCache.this.mFilesystemCachePolicy, progress + 1);
				updateCurrentSizeText();
			}

			public void onStartTrackingTouch(final SeekBar seekBar) {
				// Nothing...
			}

			public void onStopTrackingTouch(final SeekBar seekBar) {
				// Nothing...
			}
		};

		this.mTvCurrentCacheSize = (TextView)this.findViewById(R.id.tv_settings_cache_currentsize);

		this.mSeekBarMaxCacheSize = (SeekBar)this.findViewById(R.id.hsl_settings_cache_max_slider);
		this.mTvMaxCacheSizeTitle = (TextView)this.findViewById(R.id.tv_settings_cache_max_slider_title);

		this.mSeekBarMaxCacheSize.setProgress(Preferences.getMaxCacheSize(this, this.mFilesystemCachePolicy) - 1);

		this.applyButtonListeners();

		updateCurrentSizeText();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private void updateProgressBar(){
		switch(this.mFilesystemCachePolicy){
			case EXTERNAL:
				this.mSeekBarMaxCacheSize.setMax(this.EXTERNAL_MEDIA_MEGABYTESIZE - 1);
				this.mTvMaxCacheSizeTitle.setText(R.string.tv_settings_cache_max_size_external);
				break;
			case INTERNALROM:
				this.mSeekBarMaxCacheSize.setMax(30 - 1);
				this.mTvMaxCacheSizeTitle.setText(R.string.tv_settings_cache_max_size_internal);
				break;
		}

		final int curMaxSize = Preferences.getMaxCacheSize(this, this.mFilesystemCachePolicy) - 1;

		this.mSeekBarMaxCacheSize.setOnSeekBarChangeListener(null);
		this.mSeekBarMaxCacheSize.setProgress(curMaxSize);
		this.mSeekBarMaxCacheSize.setOnSeekBarChangeListener(this.mMaxSizeChangeListener);
	}

	private void updateCurrentSizeText() {
		updateProgressBar();

		final float aMBytes = this.mFSTileProvider.getCurrentFSCacheByteSize() / (1024f * 1024f);
		final int maxCacheSize = Preferences.getMaxCacheSize(this, this.mFilesystemCachePolicy);

		this.mTvCurrentCacheSize.setText(String.format(getString(R.string.tv_settings_cache_current_size), aMBytes, maxCacheSize));

		if(maxCacheSize < aMBytes) {
			this.mTvCurrentCacheSize.setTextColor(Color.rgb(255, 150, 0));
		} else {
			this.mTvCurrentCacheSize.setTextColor(Color.GREEN);
		}
	}

	protected void applyButtonListeners() {
		this.mSeekBarMaxCacheSize.setOnSeekBarChangeListener(this.mMaxSizeChangeListener);

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.btn_settings_cache_clear)){
			@Override
			public void onClicked(final View me) {
				switch(SettingsCache.this.mFSTileProvider.getStoragePolicy()){
					case EXTERNAL:
						new AlertDialog.Builder(SettingsCache.this)
						.setIcon(R.drawable.information)
						.setTitle(R.string.settings_cache_clear_external_warning_title)
						.setMessage(R.string.settings_cache_clear_external_warning_message)
						.setPositiveButton(R.string.proceed, new DialogInterface.OnClickListener(){
							public void onClick(final DialogInterface d, final int which) {
								d.dismiss();
								doClearCache();
							}
						})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
							public void onClick(final DialogInterface d, final int which) {
								d.dismiss();
							}
						})
						.create().show();
						break;
					case INTERNALROM:
						doClearCache();
						break;
				}
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_cache_sdcard)){
			@Override
			public void onClicked(final View me) {
				SettingsCache.this.mFilesystemCachePolicy = StoragePolicy.EXTERNAL;
				try {
					SettingsCache.this.mFSTileProvider = new OSMMapTileFilesystemCache(SettingsCache.this, SettingsCache.this.mFilesystemCachePolicy, Integer.MAX_VALUE, null,OSMMapTileProviderInfo.MAPNIK);
					SettingsCache.this.updateCurrentSizeText();
				} catch (final ExternalStorageNotMountedException e) {
					// TODO Msg
				}
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_cache_device)){
			@Override
			public void onClicked(final View me) {
				SettingsCache.this.mFilesystemCachePolicy = StoragePolicy.INTERNALROM;
				try {
					SettingsCache.this.mFSTileProvider = new OSMMapTileFilesystemCache(SettingsCache.this, SettingsCache.this.mFilesystemCachePolicy, Integer.MAX_VALUE, null, OSMMapTileProviderInfo.MAPNIK);
					SettingsCache.this.updateCurrentSizeText();
				} catch (final ExternalStorageNotMountedException e) {
					Toast.makeText(SettingsCache.this, "No SD-Card inserted!", Toast.LENGTH_LONG).show();
				}
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_cache_use_sdcard)){
			@Override
			public void onClicked(final View me) {
				if(((ToggleButton)me).isChecked()) {
					Preferences.saveFilesystemCachePolicy(SettingsCache.this, StoragePolicy.EXTERNAL);
				} else {
					Preferences.saveFilesystemCachePolicy(SettingsCache.this, StoragePolicy.INTERNALROM);
				}
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_cache_close)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SettingsCache.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsCache.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				final float aMBytes = SettingsCache.this.mFSTileProvider.getCurrentFSCacheByteSize() / (1024f * 1024f);
				final int maxCacheSize = Preferences.getMaxCacheSize(SettingsCache.this, SettingsCache.this.mFilesystemCachePolicy);

				if(maxCacheSize < aMBytes){
					final ProgressDialog pd = ProgressDialog.show(SettingsCache.this, getString(R.string.pdg_settings_cache_clear_cache_message), getString(R.string.pdg_settings_cache_clear_cache_title), true);
					new Thread(new Runnable(){
						public void run() {
							SettingsCache.this.mFSTileProvider.cutCurrentFSCacheBy((int)(1024 * 1024 * (aMBytes - maxCacheSize)));
							runOnUiThread(new Runnable(){
								public void run() {
									updateCurrentSizeText();
									pd.dismiss();
									SettingsCache.this.finish();
								}
							});
						}
					}).start();
				}else{
					SettingsCache.this.finish();
				}
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_cache_info)){
			@Override
			public void onClicked(final View me) {
				new AlertDialog.Builder(SettingsCache.this)
				.setIcon(R.drawable.information)
				.setTitle(R.string.instructions)
				.setMessage(R.string.tv_settings_cache_quickinfo_default_caption)
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){
					public void onClick(final DialogInterface d, final int which) {
						d.dismiss();
					}
				}).create().show();
			}
		};
	}

	private void doClearCache() {
		final ProgressDialog pd = ProgressDialog.show(SettingsCache.this, getString(R.string.pdg_settings_cache_clear_cache_message), getString(R.string.pdg_settings_cache_clear_cache_title), true);
		new Thread(new Runnable(){
			public void run() {
				SettingsCache.this.mFSTileProvider.clearCurrentFSCache();
				runOnUiThread(new Runnable(){
					public void run() {
						// TODO Add warning, if SD-Card needs to be formatted. Inform that using the PC would be much more efficient!
						updateCurrentSizeText();
						pd.dismiss();
					}
				});
			}
		}).start();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
