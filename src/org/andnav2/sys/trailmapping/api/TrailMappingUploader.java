package org.andnav2.sys.trailmapping.api;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.api.traces.util.GPXFormatter;
import org.andnav2.util.constants.Constants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

public class TrailMappingUploader implements Constants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String URL = "http://trailmapping.com/api/trips/create/";

	private static final String username = "plusminus";
	private static final String password = "test123trailmapping";

	private static final String ACCEPT_ENCODING = "Accept-Encoding";

	private static final String DEFLATE_ENCODINGS = "gzip,deflate";

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

	public static void uploadAsync(final List<GeoPoint> recordedGeoPoints){
		new Thread(new Runnable(){
			public void run() {
				upload(recordedGeoPoints);
			}
		}).start();
	}

	public static void upload(final List<GeoPoint> recordedGeoPoints) {
		try{
			// The filename the gpx-data is stored in.
			//			final String filename = System.currentTimeMillis() + ".gpx";

			final String gpxTraceString = GPXFormatter.create(recordedGeoPoints);

			final DefaultHttpClient httpClient = new DefaultHttpClient();

			// we only support basic authentication
			final BasicScheme basicScheme = new BasicScheme();
			final AuthSchemeRegistry authRegistry = new AuthSchemeRegistry();
			authRegistry.register(basicScheme.getSchemeName(), new BasicSchemeFactory());
			httpClient.setAuthSchemes(authRegistry);
			httpClient.setCredentialsProvider(new BasicCredentialsProvider());

			httpClient.getCredentialsProvider().setCredentials(
					new AuthScope(AuthScope.ANY_HOST,
							AuthScope.ANY_PORT,
							AuthScope.ANY_REALM,
							AuthScope.ANY_SCHEME),
							new UsernamePasswordCredentials(username, password));

			final HttpPost request = new HttpPost(URL);
			final HttpParams params = request.getParams();

			/* ask the server if we can use gzip */
			request.setHeader(ACCEPT_ENCODING, DEFLATE_ENCODINGS);

			// create the multipart request and add the parts to it
			final MultipartEntity requestEntity = new MultipartEntity();
			requestEntity.addPart("title", new StringBody("test title"));
			requestEntity.addPart("body", new StringBody("test body"));
			requestEntity.addPart("gpx_file", new StringBody(gpxTraceString));

			final long contentLength = requestEntity.getContentLength();
			params.setParameter("Content-Length", "" + contentLength);

			System.out.println("Content-Length = " + contentLength);
			//			Log.e(DEBUGTAG, "Content-Length = " + contentLength);


			httpClient.getParams().setBooleanParameter("http.protocol.expect-continue", false);

			request.setEntity(requestEntity);

			final HttpResponse response = httpClient.execute(request);
			final int status = response.getStatusLine().getStatusCode();

			if (status != HttpStatus.SC_OK) {
				System.out.println("(status == " + status + ") != HttpStatus.SC_OK");
				//				Log.e("GPXUploader", "(status == " + status + ") != HttpStatus.SC_OK");
			} else {
				final Reader r = new InputStreamReader(new BufferedInputStream(response.getEntity().getContent()));
				// see above
				final char[] buf = new char[8 * 1024];
				int read;
				final StringBuilder sb = new StringBuilder();
				while((read = r.read(buf)) != -1) {
					sb.append(buf, 0, read);
				}

				System.out.println("Response: " + sb.toString());
				//				Log.d("TrailMappingUploader", "Response: " + sb.toString());
			}
		}catch (final Exception e){
			e.printStackTrace();
			//			Log.e(DEBUGTAG, "OSMUpload Error", e);
			//			e.printStackTrace();
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
