//Created by plusminus on 16:36:15 - 16.05.2008
package org.andnav2.ui.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.andnav2.util.LexicographicalComparator;

import android.util.Log;
import android.widget.EditText;


public abstract class InlineAutoCompleterCombined extends InlineAutoCompleterDynamic {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final List<String> mStrings;
	protected final boolean mProgressive;

	// ===========================================================
	// Constructors
	// ===========================================================

	public InlineAutoCompleterCombined(final EditText editTextToApplyTo, final List<String> aStrings, final boolean aProgressive) {
		super(editTextToApplyTo);
		Collections.sort(aStrings, new LexicographicalComparator());
		this.mStrings = aStrings;

		this.mProgressive = aProgressive;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public abstract boolean onEnter();

	@Override
	public ArrayList<String> onGetAutoCompletionStrings() throws NoSuchElementException{
		final ArrayList<String> out = new ArrayList<String>();

		final String strBefore = super.getCurrent();
		Log.d(DEBUGTAG, "StrBefore: " + strBefore);

		// All dynamic Strings (higher Priority)
		final ArrayList<String> dyns = onGetDynamic();
		if(dyns != null) {
			for (final String dyn : dyns) {
				if(dyn != null && dyn.toLowerCase().startsWith(strBefore.toLowerCase())) {
					if(this.mProgressive && dyn.compareToIgnoreCase(strBefore) != 0) {
						out.add(dyn);
					} else {
						out.add(dyn);
					}
				}
			}
		}

		// All 'constant' Strings (lower Priority)
		if(this.mStrings != null) {
			for (final String s : this.mStrings) {
				if(s != null && s.toLowerCase().startsWith(strBefore.toLowerCase())) {
					if(this.mProgressive && s.compareToIgnoreCase(strBefore) != 0) {
						out.add(s);
					} else {
						out.add(s);
					}
				}
			}
		}

		if(out.size() > 0) {
			return out;
		}

		throw new NoSuchElementException();
	}

	public abstract ArrayList<String> onGetDynamic();

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
