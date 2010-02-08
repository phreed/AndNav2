// Created by plusminus on 01:15:59 - 20.11.2008
package org.andnav2.app.veecheck;

import android.content.Context;

import com.tomgibara.android.veecheck.VeecheckReceiver;
import com.tomgibara.android.veecheck.VeecheckSettings;
import com.tomgibara.android.veecheck.VeecheckState;
import com.tomgibara.android.veecheck.util.PrefSettings;
import com.tomgibara.android.veecheck.util.PrefState;

public class AndNav2VeecheckRetriever  extends VeecheckReceiver {
	// ===========================================================
	// Constants
	// ===========================================================

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

	@Override
	protected VeecheckSettings createSettings(final Context context) {
		return new PrefSettings(context);
	}

	@Override
	protected VeecheckState createState(final Context context) {
		return new PrefState(context);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
