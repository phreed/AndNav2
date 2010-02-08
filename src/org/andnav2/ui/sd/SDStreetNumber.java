//Created by plusminus on 13:59:14 - 12.02.2008
package org.andnav2.ui.sd;

import org.andnav2.R;
import org.andnav2.adt.voice.Voice;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.common.adapters.NumberPadAdapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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


public class SDStreetNumber extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	/* REQUEST-CODES for SubActivities. */
	protected static final int REQUESTCODE_RESOLVER = 0x1737;

	// ===========================================================
	// Fields
	// ===========================================================

	protected GridView numberPadGrid;
	protected EditText streetNumberEditText;
	protected Bundle bundleCreatedWith;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		setContentView(R.layout.sd_streetnumber);

		/* Save the Extras Bundle of the Intent this Activity
		 * was created with, because it contains the Information,
		 * that will finally be used for a GeoCode API. */
		this.bundleCreatedWith = this.getIntent().getExtras();

		this.numberPadGrid = (GridView)findViewById(R.id.grid_sd_streetnumber_numberpad);

		this.streetNumberEditText = (EditText)findViewById(R.id.et_sd_streetnumber_numberentered);

		/* Make the Country-Grid be filled with all Countries available. */
		this.numberPadGrid.setAdapter(new NumberPadAdapter(this, this.gridButtonListener));
		this.applyNumberPadGridOnItemClickListener();

		this.applyTopButtonListeners();

		if(super.mMenuVoiceEnabled) {
			MediaPlayer.create(this, R.raw.enter_a_streetnumber).start();
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


	private void advanceToNextScreen(final boolean skipThis) {
		if(!skipThis && this.streetNumberEditText.getText().length() == 0){
			Toast.makeText(this, R.string.toast_sd_streetnumber_empty, Toast.LENGTH_SHORT).show();
		}else{
			final String streetNumber = SDStreetNumber.this.streetNumberEditText.getText().toString();

			if(skipThis){
				/* Remove Info from the bundle that may be included in it. */
				SDStreetNumber.this.bundleCreatedWith.remove(EXTRAS_STREETNUMBER_ID);
			}else{
				if(streetNumber.length() == 0){
					Toast.makeText(this, R.string.toast_sd_streetnumber_empty, Toast.LENGTH_SHORT).show();
					return;
				}
				if (SDStreetNumber.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDStreetNumber.this, R.raw.ok).start();
				}

				/* Add the StreetNumber to the Bundle to be passed further. */
				SDStreetNumber.this.bundleCreatedWith.putString(EXTRAS_STREETNUMBER_ID, streetNumber);
			}

			/* Create an Intent to open the Map as a SubActivity. */
			final Intent resolveIntent = new Intent(SDStreetNumber.this, SDResolver.class);

			/* Pass the Bundle this Activity was created with further. */
			resolveIntent.putExtras(SDStreetNumber.this.bundleCreatedWith);

			startActivityForResult(resolveIntent, REQUESTCODE_RESOLVER);
		}
	}

	protected void handleButtonClickByCaption(final String buttonCaption){
		if(buttonCaption.equals(NumberPadAdapter.BUTTONGRID_OKCAPTION)){
			advanceToNextScreen(false);
		}else if(buttonCaption.equals(NumberPadAdapter.BUTTONGRID_RESETCAPTION)){
			/* Delete all digits entered so far. */
			SDStreetNumber.this.streetNumberEditText.getText().clear();
		}else{
			try{
				/* Parse the caption to an Integer. */
				final int i = Integer.parseInt(buttonCaption);
				/* And append it to the streetNumberEditText. */

				/* Check if anything is selected. */
				final int selStart = this.streetNumberEditText.getSelectionStart();
				final int selEnd = this.streetNumberEditText.getSelectionEnd();
				if(selStart != selEnd) {
					this.streetNumberEditText.getText().delete(selStart, selEnd);
				}

				this.streetNumberEditText.getText().append("" + i);

				if(super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDStreetNumber.this, Voice.getNumberVoice(i)).start();
				}
			}catch(final NumberFormatException nfe){
				Log.e(DEBUGTAG, "numberPadGrid-onItemClick", nfe);
			}
		}
	}

	/** Applies a OnItemClickListener to the numberPadGrid
	 * which calls handleButtonClick(String caption).*/
	protected void applyNumberPadGridOnItemClickListener() {
		this.numberPadGrid.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(final AdapterView<?> arg0, final View v, final int arg2, final long arg3) {
				/* Extract the Caption of the Button. */
				final String theCaption = ((Button)v).getText().toString();
				SDStreetNumber.this.handleButtonClickByCaption(theCaption);
			}
		});
	}

	protected void applyTopButtonListeners() {
		this.streetNumberEditText.setOnKeyListener(new OnKeyListener(){
			public boolean onKey(final View arg0, final int arg1, final KeyEvent ke) {
				if(ke.getAction() == KeyEvent.ACTION_DOWN){
					if(ke.getKeyCode() == KeyEvent.KEYCODE_ENTER || ke.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER){
						if(SDStreetNumber.super.mMenuVoiceEnabled) {
							MediaPlayer.create(SDStreetNumber.this, R.raw.ok).start();
						}

						advanceToNextScreen(false);
						return true;
					}
				}
				return false;
			}
		});

		/* Set OnClickListener for Skip-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_streetnumber_skip)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDStreetNumber.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDStreetNumber.this, R.raw.skip).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				advanceToNextScreen(true);
			}
		};

		/* Set OnClickListener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_streetnumber_back)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDStreetNumber.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDStreetNumber.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Back one level. */
				SDStreetNumber.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDStreetNumber.this.finish();
			}
		};

		/* Set OnClickListener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_streetnumber_close)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDStreetNumber.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDStreetNumber.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Set ResultCode that the calling
				 * activity knows that we want
				 * to go back to the Base-Menu */
				SDStreetNumber.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDStreetNumber.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	/** Create an OnItemClickListener to add a digit to the EditText or to
	 * advance to the next screen(Street-entry).
	 * This Listener will get called on actual CLICKS to the BUTTONS! */
	protected OnClickListener gridButtonListener = new OnClickListener() {
		public void onClick(final View v) {
			/* Extract the Caption of the Button. */
			final String theCaption = ((Button)v).getText().toString();
			SDStreetNumber.this.handleButtonClickByCaption(theCaption);
		};
	};
}