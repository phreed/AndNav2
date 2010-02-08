//Created by plusminus on 13:33:21 - 16.05.2008
package org.andnav2.ui.common;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.andnav2.util.constants.Constants;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;


public abstract class InlineAutoCompleterDynamic implements OnKeyListener, TextWatcher, Constants{
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected EditText mEditText;

	// ===========================================================
	// Constructors
	// ===========================================================

	public InlineAutoCompleterDynamic(final EditText aEditTextToApplyTo){
		this.mEditText = aEditTextToApplyTo;

		this.mEditText.setOnKeyListener(this);

		this.mEditText.addTextChangedListener(this);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public boolean onKey(final View arg0, final int arg1, final KeyEvent ke) {
		if(ke.getAction() == KeyEvent.ACTION_DOWN){
			if(ke.getKeyCode() == KeyEvent.KEYCODE_ENTER || ke.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER){
				return InlineAutoCompleterDynamic.this.onEnter();
			}else if(ke.getKeyCode() == KeyEvent.KEYCODE_CLEAR || ke.getKeyCode() == KeyEvent.KEYCODE_DEL){
				final EditText et = InlineAutoCompleterDynamic.this.mEditText;
				final int selStart = et.getSelectionStart();
				final int selEnd = et.getSelectionEnd();
				if(selStart != selEnd){
					et.getText().delete(selStart, selEnd);
					return true;
				}
			}
		}
		return false;
	}

	public void afterTextChanged(final Editable s) { }

	public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after){ }

	public void onTextChanged(final CharSequence s, final int start, final int before, final int count){
		// Log.d(Constants.DEBUGTAG, "ON: s=" + s + "   start=" + start + "   before=" + before + "   count=" + count);
		if((count - before <= 1 // > 1 when this method was invoked by a completion (users type only 1 char each call). <= 0 when overwriting the selection.
				&& count != 0 // count == 0, when letters are deleted
				&& start != 0 // on the first letter
		) || (s.length() == 1 && start == 0 && before == 0 && count == 1)) {
			InlineAutoCompleterDynamic.this.performAutoCompletion();
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected String getCurrent(){
		final int selStart = this.mEditText.getSelectionStart();
		//		Log.d(DEBUGTAG, "SelStart: " + selStart);
		if(selStart >= 0) {
			return this.mEditText.getText().toString().substring(0, selStart);
		} else {
			return this.mEditText.getText().toString();
		}
	}

	private void complete(final String aCandidate){
		this.mEditText.post(new Runnable(){
			public void run() {
				final String strBefore = InlineAutoCompleterDynamic.this.getCurrent();

				/* The Content of the EditText may have changed since the candidate was determined. */
				if(!aCandidate.toLowerCase().startsWith(strBefore.toLowerCase())) {
					return;
				}

				InlineAutoCompleterDynamic.this.mEditText.setText(aCandidate);

				final int lenBefore = strBefore.length();
				final int lenNew = aCandidate.length();
				if(lenBefore <= lenNew) {
					InlineAutoCompleterDynamic.this.mEditText.setSelection(lenBefore, lenNew);
				}

			}
		});
	}

	public abstract boolean onEnter();

	private void performAutoCompletion(){
		new Thread(new Runnable(){
			public void run() {
				try {
					final String strNew = InlineAutoCompleterDynamic.this.onGetAutoCompletionStrings().get(0);
					InlineAutoCompleterDynamic.this.complete(strNew);
				} catch (final NoSuchElementException e) {
					return;
				}
			}
		}, "AutoCompletion-Thread").start();
	}

	public abstract ArrayList<String> onGetAutoCompletionStrings() throws NoSuchElementException;

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
