// Created by plusminus on 20:49:09 - 04.12.2008
package org.andnav2.osm.api.traces;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.api.traces.util.GPXFormatter;
import org.andnav2.osm.util.Util;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.osm.views.util.StreamUtils;

import android.util.Log;


public class GPXToFileWriter implements OSMConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd_EEEE_HH-mm-ss");

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

	// ===========================================================
	// Methods
	// ===========================================================

	public static void writeToFileAsync(final ArrayList<GeoPoint> recordedGeoPoints){
		new Thread(new Runnable(){
			public void run() {
				try {
					// Ensure folder exists
					final String traceFolderPath = Util.getAndNavExternalStoragePath() + SDCARD_SAVEDTRACES_PATH;
					new File(traceFolderPath).mkdirs();

					// Create file and ensure that needed folders exist.
					final String filename = traceFolderPath + SDF.format(new Date(System.currentTimeMillis())) + ".gpx";
					final File dest = new File(filename + ".zip");

					// Write Data
					final OutputStream out = new BufferedOutputStream(new FileOutputStream(dest),StreamUtils.IO_BUFFER_SIZE);
					final byte[] data = org.andnav2.osm.api.traces.util.Util.zipBytes(GPXFormatter.create(recordedGeoPoints).getBytes(), filename);

					out.write(data);
					out.flush();
					out.close();
				} catch (final Exception e) {
					Log.e(OSMConstants.DEBUGTAG, "File-Writing-Error", e);
				}
			}
		}, "GPXToFileSaver-Thread").start();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
