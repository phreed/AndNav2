// Created by plusminus on 10:12:13 - 14.04.2008
package org.andnav2.ui;

import org.andnav2.R;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class About extends ListActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================
	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		this.setTheme(android.R.style.Theme_Dialog);
		this.setContentView(R.layout.about);

		this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.getResources().getStringArray(R.array.aboutcreditsnames)));
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		final Intent webIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(getResources().getStringArray(R.array.aboutcreditslinks)[position]));
		startActivity(webIntent);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
