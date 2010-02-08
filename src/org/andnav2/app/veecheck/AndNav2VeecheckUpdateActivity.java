// Created by plusminus on 01:22:04 - 20.11.2008
package org.andnav2.app.veecheck;

import org.andnav2.R;
import org.andnav2.app.veecheck.util.constants.VeecheckConstants;

import android.os.Bundle;
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;

import com.tomgibara.android.veecheck.VeecheckActivity;
import com.tomgibara.android.veecheck.VeecheckState;
import com.tomgibara.android.veecheck.util.PrefState;


public class AndNav2VeecheckUpdateActivity extends VeecheckActivity implements VeecheckConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.veecheck_updater);

		final Bundle b = this.getUpdateIntent().getExtras();
		if(b != null){
			final String importance = b.getString(EXTRAS_IMPORTANCE);
			final String type = b.getString(EXTRAS_TYPE);
			String description = b.getString(EXTRAS_DESCRIPTION);

			if(importance != null && description != null && type != null){
				/* Construct Body-Message*/
				description = description.replace("\\n", "\n"); // linebreaks
				description = description.replace("\\t", "\t"); // tabs

				final String msg = String.format(getString(R.string.veecheck_confirm_text),
						importance, type, description);

				((TextView)this.findViewById(R.id.veecheck_body)).setText(msg);
			}
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================



	@Override
	protected VeecheckState createState() {
		return new PrefState(this);
	}

	@Override
	protected View getNoButton() {
		return findViewById(R.id.veecheck_no);
	}

	@Override
	protected View getYesButton() {
		return findViewById(R.id.veecheck_yes);
	}

	@Override
	protected Checkable getStopCheckBox() {
		return (Checkable) findViewById(R.id.veecheck_stop);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
