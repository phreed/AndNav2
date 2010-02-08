// Created by plusminus on 13:43:23 - 16.05.2008
package org.andnav2.ui.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.andnav2.util.LexicographicalComparator;

import android.widget.EditText;

public abstract class InlineAutoCompleterConstant extends InlineAutoCompleterDynamic{
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	final protected List<String> mStrings;
	final protected boolean mProgressive;

	// ===========================================================
	// Constructors
	// ===========================================================

	public InlineAutoCompleterConstant(final EditText editTextToApplyTo, final List<String> aStrings, final boolean aProgressive) {
		super(editTextToApplyTo);
		Collections.sort(aStrings, new LexicographicalComparator());
		this.mStrings = aStrings;

		this.mProgressive = aProgressive;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void clearStatic(){
		this.mStrings.clear();
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public abstract boolean onEnter();

	@Override
	public ArrayList<String> onGetAutoCompletionStrings() {
		final ArrayList<String> out = new ArrayList<String>();

		final String strBefore = super.getCurrent();

		for (final String s : this.mStrings) {
			if(s != null && s.toLowerCase().startsWith(strBefore.toLowerCase())) {
				if(this.mProgressive && s.compareToIgnoreCase(strBefore) != 0) {
					out.add(s);
				} else {
					out.add(s);
				}
			}
		}

		if(out.size() > 0) {
			return out;
		}

		throw new NoSuchElementException();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
