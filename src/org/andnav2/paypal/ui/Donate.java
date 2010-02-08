// Created by plusminus on 15:43:09 - 16.02.2009
package org.andnav2.paypal.ui;

import org.andnav2.R;
import org.andnav2.paypal.util.DonationCurrency;
import org.andnav2.paypal.util.DonationFrequency;
import org.andnav2.paypal.util.DonationPeriod;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;


public class Donate extends AndNavBaseActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int REQUESTCODE_PAYPALDONATE = 0;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preferences.applySharedSettings(this);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.setContentView(R.layout.donate);

		this.initViews();
	}

	private void initViews() {
		final ArrayAdapter<DonationCurrency> currencies = new ArrayAdapter<DonationCurrency>(this, android.R.layout.simple_spinner_item, DonationCurrency.values());
		currencies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		/* Recurring donations. */
		final TextView tvCurrencySymbolOnce = (TextView)this.findViewById(R.id.tv_donate_once_currency_symbol);
		final EditText etAmountOnce = (EditText)this.findViewById(R.id.et_donate_once_amount);

		final Spinner spinCurrencyOnce = (Spinner)this.findViewById(R.id.spin_donate_once_currency);
		spinCurrencyOnce.setAdapter(currencies);
		spinCurrencyOnce.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id){
				final DonationCurrency c = currencies.getItem(position);
				tvCurrencySymbolOnce.setText(c.SYMBOL);
			}

			public void onNothingSelected(final AdapterView<?> arg0) { }
		});

		final ImageButton ibtnDonateOnce = (ImageButton)this.findViewById(R.id.ibtn_donate_once);
		ibtnDonateOnce.setOnClickListener(new OnClickListener(){
			public void onClick(final View arg0) {
				if(etAmountOnce.getText().toString().length() == 0){

				}else{
					final Uri onceURL = buildOnceURL();
					final Intent i = new Intent(android.content.Intent.ACTION_VIEW, onceURL);
					startActivityForResult(i, REQUESTCODE_PAYPALDONATE);
				}
			}

			private Uri buildOnceURL() {
				return Uri.parse("https://www.paypal.com/cgi-bin/webscr")
				.buildUpon()
				.appendQueryParameter("cmd", "_donations")
				.appendQueryParameter("business", "donate@anddev.org")
				.appendQueryParameter("item_name", "Supporting the AndNav2 Project")
				.appendQueryParameter("amount", String.valueOf(Integer.parseInt(etAmountOnce.getText().toString())))
				.appendQueryParameter("no_shipping", "1")
				.appendQueryParameter("currency_code", ((DonationCurrency)spinCurrencyOnce.getSelectedItem()).ABBREVIATION)
				.appendQueryParameter("tax", "0")
				.appendQueryParameter("lc", "US")
				.appendQueryParameter("bn", "PP-DonationsBF")
				.build();
			}
		});



		/* Recurring donations. */
		final ArrayAdapter<DonationPeriod> periods = new ArrayAdapter<DonationPeriod>(this, android.R.layout.simple_spinner_item, DonationPeriod.values()){
			@Override
			public View getView(final int position, final View convertView, final ViewGroup parent) {
				final TextView tv = (convertView != null && convertView instanceof TextView) ? (TextView)convertView : new TextView(Donate.this);
				tv.setText(this.getItem(position).NAMERESID);
				tv.setTextColor(Color.BLACK);
				return tv;
			}

			@Override
			public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
				final TextView tv = (TextView)getView(position, convertView, parent);
				tv.setTextSize(20);
				return tv;
			}
		};
		periods.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		final ArrayAdapter<DonationFrequency> frequencies = new ArrayAdapter<DonationFrequency>(this, android.R.layout.simple_spinner_item, DonationFrequency.values()){
			@Override
			public View getView(final int position, final View convertView, final ViewGroup parent) {
				final TextView tv = (convertView != null && convertView instanceof TextView) ? (TextView)convertView : new TextView(Donate.this);
				tv.setText(this.getItem(position).NAMERESID);
				tv.setTextColor(Color.BLACK);
				return tv;
			}

			@Override
			public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
				final TextView tv = (TextView)getView(position, convertView, parent);
				tv.setTextSize(20);
				return tv;
			}
		};
		frequencies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		final TextView tvCurrencySymbolRecurring = (TextView)this.findViewById(R.id.tv_donate_recurring_currency_symbol);
		final EditText etAmountRecurring = (EditText)this.findViewById(R.id.et_donate_recurring_amount);

		final Spinner spinCurrencyRecurring = (Spinner)this.findViewById(R.id.spin_donate_recurring_currency);
		spinCurrencyRecurring.setAdapter(currencies);
		spinCurrencyRecurring.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id){
				final DonationCurrency c = currencies.getItem(position);
				tvCurrencySymbolRecurring.setText(c.SYMBOL);
			}

			public void onNothingSelected(final AdapterView<?> arg0) { }
		});

		final Spinner spinFrequencyRecurring = (Spinner)this.findViewById(R.id.spin_donate_recurring_frequency);
		spinFrequencyRecurring.setAdapter(frequencies);
		spinFrequencyRecurring.setSelection(frequencies.getPosition(DonationFrequency.SIX_TIMES));


		final Spinner spinPeriodsRecurring = (Spinner)this.findViewById(R.id.spin_donate_recurring_periods);
		spinPeriodsRecurring.setAdapter(periods);
		spinPeriodsRecurring.setSelection(periods.getPosition(DonationPeriod.MONTHLY));


		final ImageButton ibtnDonateRecurring = (ImageButton)this.findViewById(R.id.ibtn_donate_recurring);
		ibtnDonateRecurring.setOnClickListener(new OnClickListener(){
			public void onClick(final View arg0) {
				if(etAmountRecurring.getText().toString().length() == 0){

				}else{
					final Uri onceURL = buildRecurringURL();
					final Intent i = new Intent(android.content.Intent.ACTION_VIEW, onceURL);
					startActivityForResult(i, REQUESTCODE_PAYPALDONATE);
				}
			}

			private Uri buildRecurringURL() {
				return Uri.parse("https://www.paypal.com/cgi-bin/webscr")
				.buildUpon()
				.appendQueryParameter("cmd", "_xclick-subscriptions")
				.appendQueryParameter("business", "donate@anddev.org")
				.appendQueryParameter("bn", "PP-SubscriptionsBF")
				.appendQueryParameter("item_name", "Supporting the AndNav Project")
				.appendQueryParameter("no_shipping", "1")
				.appendQueryParameter("no_note", "1")
				.appendQueryParameter("charset", "UTF-8")
				.appendQueryParameter("src", "1")
				.appendQueryParameter("currency_code", ((DonationCurrency)spinCurrencyRecurring.getSelectedItem()).ABBREVIATION)
				.appendQueryParameter("a3", String.valueOf(Integer.parseInt(etAmountRecurring.getText().toString())))
				.appendQueryParameter("t3", String.valueOf(((DonationPeriod)spinPeriodsRecurring.getSelectedItem()).IDENTIFIER))
				.appendQueryParameter("p3", String.valueOf(((DonationFrequency)spinFrequencyRecurring.getSelectedItem()).PERIODS))
				.build();
			}
		});
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
