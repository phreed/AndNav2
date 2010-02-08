package org.andnav2.osm.api.traces;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.api.traces.util.GPXFormatter;
import org.andnav2.osm.api.traces.util.Util;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;


public class GpxToPHPUploader {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String URL = "http://www.andnav.org/sys/gpxuploader/upload.php";

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

	public static void uploadAsync(final ArrayList<GeoPoint> recordedGeoPoints){
		new Thread(new Runnable(){
			public void run() {
				try{
					// The filename the gpx-data is stored in.
					final String filename = System.currentTimeMillis() + ".gpx";
					final String filenameZipContainer = filename + ".zip";

					final InputStream zippedGpxInputStream = new ByteArrayInputStream(Util.zipBytes(GPXFormatter.create(recordedGeoPoints).getBytes(), filename));
					final HttpClient httpClient = new DefaultHttpClient();

					final HttpPost request = new HttpPost(URL);


					// create the multipart request and add the parts to it
					final MultipartEntity requestEntity = new MultipartEntity();
					requestEntity.addPart("gpxfile", new InputStreamBody(zippedGpxInputStream, filenameZipContainer));

					httpClient.getParams().setBooleanParameter("http.protocol.expect-continue", false);

					request.setEntity(requestEntity);

					final HttpResponse response = httpClient.execute(request);
					final int status = response.getStatusLine().getStatusCode();

					if (status != HttpStatus.SC_OK) {
						Log.e("GPXUploader", "status != HttpStatus.SC_OK");
					} else {
						final Reader r = new InputStreamReader(new BufferedInputStream(response.getEntity().getContent()));
						// see above
						final char[] buf = new char[8 * 1024];
						int read;
						final StringBuilder sb = new StringBuilder();
						while((read = r.read(buf)) != -1) {
							sb.append(buf, 0, read);
						}

						Log.d("GPXUploader", "Response: " + sb.toString());
					}
				}catch (final Exception e){
					//					Log.e(DEBUGTAG, "OSMUpload Error", e);
				}
			}
		}).start();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
