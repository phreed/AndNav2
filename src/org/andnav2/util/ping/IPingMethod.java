package org.andnav2.util.ping;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.andnav2.util.constants.Constants;

import android.util.Log;




/**
 * @author Nicolas Gramlich
 * @since 16:37:39 - 23.06.2009
 */
public interface IPingMethod {

	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public PingResult ping() throws IOException;

	// ===========================================================
	// Inner Classes
	// ===========================================================

	public static class URLResponsePing implements IPingMethod {
		// ===========================================================
		// Fields
		// ===========================================================

		private URL mURL;

		// ===========================================================
		// Constructors
		// ===========================================================

		/**
		 * @param pHostName Hostname or URL.
		 * @throws MalformedURLException
		 */
		public URLResponsePing(final String pURL) {
			try {
				this.mURL = new URL(pURL);
			} catch (final MalformedURLException e) {
				this.mURL = null;
				Log.e(Constants.DEBUGTAG, "Error", e);
			}
		}

		// ===========================================================
		// Methods from SuperClass/Interfaces
		// ===========================================================

		public PingResult ping() throws IOException {
			final String host = this.mURL.getHost();
			final URLConnection conn = mURL.openConnection();

			final long startMs = System.currentTimeMillis();
			try {
				conn.connect();
			} catch (final Exception e) {
				return new PingResult(host, false, 0);
			}

			final long endMs = System.currentTimeMillis();
			return new PingResult(host, true, (int)(endMs - startMs));
		}
	}

	public static class HostNamePing implements IPingMethod {
		// ===========================================================
		// Constants
		// ===========================================================

		private static final int TIMEOUT_MS = 6000;

		// ===========================================================
		// Fields
		// ===========================================================

		private final String mHostName;

		// ===========================================================
		// Constructors
		// ===========================================================

		/**
		 * @param pHostName Hostname or URL.
		 */
		public HostNamePing(final String pHostName) {
			this.mHostName = pHostName;
		}

		// ===========================================================
		// Methods from SuperClass/Interfaces
		// ===========================================================

		public PingResult ping() throws IOException {
			final InetAddress address = InetAddress.getByName(this.mHostName);
			final String hostName = address.getHostName();

			final long startMS = System.currentTimeMillis();
			final boolean isReachable = address.isReachable(TIMEOUT_MS);
			final long endMS = System.currentTimeMillis();
			return new PingResult(hostName, isReachable, (int)(endMS - startMS));
		}
	}
}

