// Created by plusminus on 19:28:20 - 10.04.2008
package org.andnav2.ui.common.adapters;

import java.util.ArrayList;

import org.andnav2.R;
import org.andnav2.sys.ors.adt.lus.Country;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** A Adapter wrapping around the Country-Enum. */
public class CountryAdapter extends BaseAdapter {

	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final Context mContext;
	protected final ArrayList<Country> mNations;
	// ===========================================================
	// Constructors
	// ===========================================================

	public CountryAdapter(final Context pContext, final ArrayList<Country> pCountries) {
		this.mContext = pContext;
		this.mNations = pCountries;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final View v;
		if (convertView != null){
			v = convertView;
		}else{
			final LayoutInflater inflater = LayoutInflater.from(this.mContext);
			v = inflater.inflate(R.layout.sd_flagview_listrow, null);
		}

		final Country n = this.mNations.get(position);

		((ImageView)v.findViewById(R.id.iv_flagview_listrow_flag)).setImageResource(n.FLAGRESID);
		((TextView)v.findViewById(R.id.tv_flagview_listrow_name)).setText(n.NAMERESID);

		return v;
	}

	public final int getCount() {
		return this.mNations.size();
	}

	public final Country getItem(final int position) {
		return this.mNations.get(position);
	}

	public final long getItemId(final int position) {
		return position;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
