// Created by plusminus on 01:17:12 - 20.11.2008
package org.andnav2.app.veecheck;

import org.andnav2.R;

import android.content.Intent;

import com.tomgibara.android.veecheck.VeecheckNotifier;
import com.tomgibara.android.veecheck.VeecheckService;
import com.tomgibara.android.veecheck.VeecheckState;
import com.tomgibara.android.veecheck.util.DefaultNotifier;
import com.tomgibara.android.veecheck.util.PrefState;

public class AndNav2VeecheckService extends VeecheckService{

	// ===========================================================
	// Constants
	// ===========================================================

	public static final int NOTIFICATION_ID = 1;

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
	protected VeecheckNotifier createNotifier() {
		// it's good practice to set up filters to help guard against malicious
		// intents
		//		IntentFilter[] filters = new IntentFilter[1];
		//		try {
		//			IntentFilter filter = new IntentFilter(Intent.ACTION_VIEW);
		//			filter.addDataType("text/html");
		//			filter.addDataScheme("http");
		//			filters[0] = filter;
		//		} catch (MalformedMimeTypeException e) {
		//			Log.e("veechecksample", "Invalid data type for filter.", e);
		//		}

		// return a default notifier implementation
		return new DefaultNotifier(this, NOTIFICATION_ID, null,
				new Intent(this, AndNav2VeecheckUpdateActivity.class),
				R.drawable.icon,
				R.string.veecheck_notify_ticker,
				R.string.veecheck_notify_title,
				R.string.veecheck_notify_message);
	}

	@Override
	protected VeecheckState createState() {
		return new PrefState(this);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
