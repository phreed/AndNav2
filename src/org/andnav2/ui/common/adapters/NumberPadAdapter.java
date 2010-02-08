//Created by plusminus on 23:25:41 - 17.05.2008
package org.andnav2.ui.common.adapters;

import org.andnav2.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;


/** A Adapter representing a KeyPad (0-9, DEL, OK). */
public class NumberPadAdapter extends BaseAdapter {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final String BUTTONGRID_OKCAPTION = "OK";
	public static final String BUTTONGRID_RESETCAPTION = "DEL";

	// ===========================================================
	// Fields
	// ===========================================================

	protected final Context mContext;
	protected final OnClickListener mGridButtonClickListener;

	// ===========================================================
	// Constructors
	// ===========================================================

	public NumberPadAdapter(final Context context, final OnClickListener aGridButtonClickListener) {
		this.mContext = context;
		this.mGridButtonClickListener = aGridButtonClickListener;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public int getCount() {
		return 12; // 12 = {9-0 + Spacer + "OK"}
	}

	public Object getItem(final int position) {
		return position;
	}

	public long getItemId(final int position) {
		return position;
	}

	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final Button b = new Button(this.mContext);

		b.setText(getCaptionByPosition(position));
		b.setTextSize(22);
		b.setTypeface(Typeface.DEFAULT_BOLD);
		b.setPadding(0,0,0,0);
		b.setBackgroundResource(R.drawable.btn_keyboard);

		/* Apply the shared OnClickListener to 'each' button. */
		b.setOnClickListener(this.mGridButtonClickListener);

		b.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		return b;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	/** Maps position to a String, which is the number appearing on the button. */
	protected String getCaptionByPosition(final int position){
		switch(position){
			case 0: case 1: case 2: return "" + (position + 7);
			case 3: case 4: case 5: return "" + (position + 1);
			case 6: case 7: case 8: return "" + (position - 5);
			case 9: return BUTTONGRID_RESETCAPTION;
			case 10: return "0";
			case 11: return BUTTONGRID_OKCAPTION;
			default: return "ERROR";
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
