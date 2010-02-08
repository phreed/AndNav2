// Created by plusminus on 16:55:27 - 16.02.2009
package org.andnav2.paypal.util;


public enum DonationCurrency {
	// ===========================================================
	// Elements
	// ===========================================================

	DOLLAR_US("USD", CurrencyConstants.CURRENCY_SYMBOL_DOLLAR),
	EURO("EUR", "€"),
	POUNDS_BRITISH("GBP", "£"),
	DOLLAR_AUSTRALIAN("AUD", CurrencyConstants.CURRENCY_SYMBOL_DOLLAR),
	DOLLAR_CANADIAN("CAD", CurrencyConstants.CURRENCY_SYMBOL_DOLLAR),
	YEN("JPY", "¥"),
	FRANKEN("CHF"),
	CZK("CZK"),
	DKK("DKK"),
	DOLLAR_HONKONG("HKD", CurrencyConstants.CURRENCY_SYMBOL_DOLLAR),
	HUF("HUF"),
	NOK("NOK"),
	DOLLAR_NEW_ZEALAND("NZD", CurrencyConstants.CURRENCY_SYMBOL_DOLLAR),
	PLN("PLN"),
	SEL("SEK"),
	SGD("SGD", CurrencyConstants.CURRENCY_SYMBOL_DOLLAR);

	// ===========================================================
	// Constants
	// ===========================================================


	// ===========================================================
	// Fields
	// ===========================================================

	public final String ABBREVIATION;
	public final String SYMBOL;

	// ===========================================================
	// Constructors
	// ===========================================================

	private DonationCurrency(final String pAbbreviation){
		this(pAbbreviation, pAbbreviation);
	}

	private DonationCurrency(final String pAbbreviation, final String pSymbol){
		this.ABBREVIATION = pAbbreviation;
		this.SYMBOL = pSymbol;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public String toString(){
		return this.ABBREVIATION;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private static interface CurrencyConstants{
		public static final String CURRENCY_SYMBOL_DOLLAR = "$";
	}
}
