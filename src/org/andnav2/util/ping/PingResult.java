/**
 * 
 */
package org.andnav2.util.ping;

/**
 * @author Nicolas Gramlich
 * @since 17:13:57 - 23.06.2009
 */
public class PingResult{
	// ===========================================================
	// Fields
	// ===========================================================

	public final String mHostname;
	public final boolean mReachable;
	public final int mLatency;

	// ===========================================================
	// Constructors
	// ===========================================================

	public PingResult(final String pHostname, final boolean pReachable, final int pLatency) {
		this.mHostname = pHostname;
		this.mReachable = pReachable;
		this.mLatency = pLatency;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
}
