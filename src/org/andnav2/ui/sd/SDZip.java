//Created by plusminus on 23:19:02 - 03.02.2008
package org.andnav2.ui.sd;

import java.util.List;

import org.andnav2.R;
import org.andnav2.adt.keyboardlayouts.AbstractKeyBoardLayout;
import org.andnav2.adt.keyboardlayouts.KeyBoardLayoutImpls;
import org.andnav2.adt.voice.Voice;
import org.andnav2.db.DBManager;
import org.andnav2.db.DataBaseException;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.InlineAutoCompleterConstant;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.common.adapters.KeyLayoutAdapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class SDZip extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	/* REQUEST-CODES for SubActivities. */
	protected static final int REQUESTCODE_SD_STREET = 0x1537;

	// ===========================================================
	// Fields
	// ===========================================================

	protected GridView keyBoardGrid;
	protected EditText zipCodeEditText;
	protected Bundle bundleCreatedWith;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		setContentView(R.layout.sd_zip);

		/* Save the Extras Bundle of the Intent this Activity
		 * was created with, because it contains the Information,
		 * that will finally be used for the Yahoo GeoCode API. */
		this.bundleCreatedWith = this.getIntent().getExtras();

		final AbstractKeyBoardLayout aKeyBoardLayout = KeyBoardLayoutImpls.getNumberedVersion(Preferences.getKeyboardLayout(this));
		this.keyBoardGrid = (GridView)findViewById(R.id.grid_sd_zip_keyboard);
		this.keyBoardGrid.setNumColumns(aKeyBoardLayout.getColumnsByDisplay(getWindowManager().getDefaultDisplay()));

		this.zipCodeEditText = (EditText)findViewById(R.id.et_sd_zip_zipentered);

		/* Make the Country-Grid be filled with all Countries available. */
		this.keyBoardGrid.setAdapter(new KeyLayoutAdapter(this, aKeyBoardLayout, this.mGridButtonListener));
		this.applyNumberPadGridOnItemClickListener();

		this.applyTopButtonListeners();
		this.applyAutoCompleteListeners();
		this.applyOkButtonListener();

		if(super.mMenuVoiceEnabled) {
			MediaPlayer.create(this, R.raw.enter_a_zipcode).start();
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch(resultCode){
			case SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS:
				this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS, data);
				this.finish();
				break;
			case SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED:
				this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED, data);
				this.finish();
				break;
		}
		/* Finally call the super()-method. */
		super.onActivityResult(requestCode, resultCode, data);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected void applyAutoCompleteListeners() {
		try {
			final Country nat = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
			final List<String> usedZipCodes = DBManager.getZipCodes(this, nat.COUNTRYCODE);

			new InlineAutoCompleterConstant(this.zipCodeEditText, usedZipCodes, false){
				@Override
				public boolean onEnter() {
					SDZip.this.advanceToNextScreen();
					return true;
				}
			};
		} catch (final DataBaseException e) {
			Log.e(DEBUGTAG, "Error on loading ZipCodes", e);
		}
	}

	protected void advanceToNextScreen() {
		if(this.zipCodeEditText.getText().length() == 0){
			Toast.makeText(this, R.string.toast_sd_zipcode_empty, Toast.LENGTH_SHORT).show();
		}else{
			final String zipCode = SDZip.this.zipCodeEditText.getText().toString();
			try {
				final Country nat = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
				DBManager.addZipCode(this, zipCode, nat.COUNTRYCODE);
			} catch (final DataBaseException e) {
				Log.e(DEBUGTAG, "Error on inserting ZipCode", e);
			}

			/* Then create an Intent to open the SubActivity. */
			final Intent sdStreetSearchIntent = new Intent(SDZip.this, SDStreet.class);
			/* Add the ZipCode to the Bundle to be passed further. */
			SDZip.this.bundleCreatedWith.putString(EXTRAS_ZIPCODE_ID, zipCode);
			/* Pass the Bundle this Activity was created with further. */
			sdStreetSearchIntent.putExtras(SDZip.this.bundleCreatedWith);

			startActivityForResult(sdStreetSearchIntent, REQUESTCODE_SD_STREET);
		}
	}

	protected void handleButtonClickByCaption(final String buttonCaption){
		final Editable curText = this.zipCodeEditText.getText();
		if(buttonCaption.equals("" + AbstractKeyBoardLayout.BUTTONGRID_BACKCAPTION)){
			final int len = curText.length();
			if (len > 0) {
				curText.delete(len - 1, len);
			}
		}else{
			/* Append the Buttons caption it to the streetNameEditText. */
			final int selStart = this.zipCodeEditText.getSelectionStart();
			final int selEnd = this.zipCodeEditText.getSelectionEnd();
			if(selStart < selEnd) {
				curText.replace(selStart, selEnd, "");
			}

			/* If previous character is a letter, the next one should be lowercase. */
			if(curText.length() > 0 && Character.isLetter(curText.charAt(curText.length() - 1))) {
				curText.append(buttonCaption.toLowerCase());
			} else {
				curText.append(buttonCaption.toUpperCase());
			}

			this.zipCodeEditText.invalidate();
		}
	}

	/** Applies a OnItemClickListener to the numberPadGrid
	 * which calls handleButtonClick(String caption).*/
	protected void applyNumberPadGridOnItemClickListener() {
		this.keyBoardGrid.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(final AdapterView<?> arg0, final View v, final int arg2, final long arg3) {
				/* Extract the Caption of the Button. */
				final String theCaption = ((Button)v).getText().toString();
				SDZip.this.handleButtonClickByCaption(theCaption);
			}
		});
	}

	protected void applyOkButtonListener() {
		/* Set OnClickListener for OK-Button. */
		findViewById(R.id.btn_sd_zip_ok).setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				SDZip.this.advanceToNextScreen();
			}
		});
	}

	protected void applyTopButtonListeners() {

		this.zipCodeEditText.setOnKeyListener(new OnKeyListener(){
			public boolean onKey(final View arg0, final int arg1, final KeyEvent ke) {
				if(ke.getAction() == KeyEvent.ACTION_DOWN){
					if(SDZip.super.mMenuVoiceEnabled){
						final int resID = Voice.getNumberVoiceFromKeyCode(ke.getKeyCode());
						Log.d(DEBUGTAG, "" + ke.getKeyCode());
						if(resID != NOT_SET) {
							MediaPlayer.create(SDZip.this, resID).start();
						}
					}
					if(ke.getKeyCode() == KeyEvent.KEYCODE_ENTER || ke.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER){
						if(SDZip.super.mMenuVoiceEnabled) {
							MediaPlayer.create(SDZip.this, R.raw.ok).start();
						}

						advanceToNextScreen();
						return true;
					}
				}
				return false;
			}
		});

		/* Set OnClickListener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_zip_back)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDZip.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDZip.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Back one level. */
				SDZip.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDZip.this.finish();
			}
		};

		/* Set OnClickListener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_zip_close)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDZip.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDZip.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Set ResultCode that the calling
				 * activity knows that we want
				 * to go back to the Base-Menu */
				SDZip.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDZip.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	/** Create an OnItemClickListener to add a digit to the EditText or to
	 * advance to the next screen(Street-entry).
	 * This Listener will get called on actual CLICKS to the BUTTONS! */
	protected OnClickListener mGridButtonListener = new OnClickListener() {
		public void onClick(final View v) {
			/* Extract the Caption of the Button. */
			final String theCaption = ((Button)v).getText().toString();
			SDZip.this.handleButtonClickByCaption(theCaption);
		};
	};
}