// Created by plusminus on 21:38:18 - 15.05.2008
package org.andnav2.ui.common.adapters;

import org.andnav2.R;
import org.andnav2.adt.keyboardlayouts.AbstractKeyBoardLayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

/** A Adapter wrapping around a KeyLayout. */
public class KeyLayoutAdapter extends BaseAdapter {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final Context ctx;
	protected final OnClickListener ocl;
	protected final AbstractKeyBoardLayout itsKeyBoardLayout;

	// ===========================================================
	// Constructors
	// ===========================================================

	public KeyLayoutAdapter(final Context context, final AbstractKeyBoardLayout aKeyBoardLayout, final OnClickListener aOnClickListener) {
		this.ocl = aOnClickListener;
		this.ctx = context;
		this.itsKeyBoardLayout = aKeyBoardLayout;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public int getCount() {
		return this.itsKeyBoardLayout.getGridFieldSum();
	}

	public Object getItem(final int position) {
		return this.itsKeyBoardLayout.getKey(position);
	}

	public long getItemId(final int position) {
		return position;
	}

	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final Button b = new Button(this.ctx);

		final String caption = getItem(position).toString();
		if(caption.equals(AbstractKeyBoardLayout.BUTTONGRID_BACKCAPTION)) {
			b.setTextColor(Color.RED);
		}

		b.setText(caption);
		b.setTextSize(20);
		b.setTypeface(Typeface.DEFAULT_BOLD);
		b.setPadding(0,0,0,0);
		b.setBackgroundResource(R.drawable.btn_keyboard);

		/* Apply the shared OnClickListener to 'each' button. */
		b.setOnClickListener(this.ocl);
		b.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		return b;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}