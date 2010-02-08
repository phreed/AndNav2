// Created by plusminus on 19:28:15 - 16.08.2008
package org.andnav2.ui;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.os.Bundle;
import android.view.View;

public class StartupWarning extends AndNavBaseActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	// @Override
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		this.setTheme(android.R.style.Theme_Dialog);
		this.setContentView(R.layout.startupwarning);

		this.applyButtonListeners();
	}

	private void applyButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(findViewById(R.id.btn_startupwarning_neveragain)) {
			@Override
			public void onClicked(final View me) {
				Preferences.saveShowStartupWarningNeverAgain(StartupWarning.this);
				StartupWarning.this.finish();
			}
		};

		new OnClickOnFocusChangedListenerAdapter(findViewById(R.id.btn_startupwarning_ok)) {
			@Override
			public void onClicked(final View me) {
				StartupWarning.this.finish();
			}
		};
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

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
