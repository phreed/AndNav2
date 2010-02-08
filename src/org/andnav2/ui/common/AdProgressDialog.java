// Created by plusminus on 22:58:34 - 20.11.2008
package org.andnav2.ui.common;


import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.common.views.AdView;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AdProgressDialog extends AlertDialog {

	// ===========================================================
	// Constants
	// ===========================================================

	private ProgressBar mProgress;
	private TextView mMessageView;
	private AdView mAdView;
	private Drawable mIndeterminateDrawable;
	private CharSequence mMessage;

	private Context mContext;
	private LinearLayout mBodyLL;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	private AdProgressDialog(final Context context, final int theme) {
		super(context, theme);
	}

	private AdProgressDialog(final Context context) {
		super(context);
		this.mContext = context;
	}

	public static AdProgressDialog show(final Context context, final CharSequence title, final CharSequence message) {
		return show(context, title, message, false, null, null);
	}

	public static AdProgressDialog show(final Context context, final CharSequence title, final CharSequence message, final AdView adView) {
		return show(context, title, message, false, null, adView);
	}

	public static AdProgressDialog show(final Context context, final CharSequence title, final CharSequence message, final boolean cancelable, final AdView adView) {
		return show(context, title, message, cancelable, null, adView);
	}

	public static AdProgressDialog show(final Context context, final CharSequence title, final CharSequence message, final boolean cancelable, final OnCancelListener cancelListener) {
		return show(context, title, message, cancelable, cancelListener, null);
	}

	public static AdProgressDialog show(final Context context, final CharSequence title, final CharSequence message, final boolean cancelable, final OnCancelListener cancelListener, final AdView adView) {
		final AdProgressDialog dialog = new AdProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);

		if(Preferences.getAdFreeState(context) == false){
			dialog.setAdView(adView);
		}
		
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		dialog.show();
		return dialog;
	}

	private void setAdView(final AdView adView) {
		this.mAdView = adView;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		final LayoutInflater inflater = LayoutInflater.from(this.mContext);
		final FrameLayout fl = (FrameLayout)inflater.inflate(R.layout.ad_progress_dialog, null);

		this.mBodyLL = (LinearLayout)fl.findViewById(R.id.ad_progress_body);
		this.mProgress = (ProgressBar) this.mBodyLL.findViewById(R.id.adprogress_progress);
		this.mMessageView = (TextView) this.mBodyLL.findViewById(R.id.adprogress_message);
		

		final AdView hidden = (AdView)this.mBodyLL.findViewById(R.id.adprogress_adview);
		if(hidden != null)
			hidden.setVisibility(View.GONE);

		if(this.mAdView == null){
//			throw new IllegalStateException("AdView needs to be set.");
			//			mAdView = (AdView) fl.findViewById(R.id.adprogress_adview);
		}else{
			this.mBodyLL.addView(this.mAdView, AdView.AD_WIDTH, AdView.AD_HEIGHT);
		}

		setView(fl);
		if (this.mIndeterminateDrawable != null) {
			this.mProgress.setIndeterminateDrawable(this.mIndeterminateDrawable);
		}
		if (this.mMessage != null) {
			setMessage(this.mMessage);
		}

		super.onCreate(savedInstanceState);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(this.mAdView != null)
			this.mBodyLL.removeView(this.mAdView);
	}

	@Override
	public void setMessage(final CharSequence message) {
		if (this.mProgress != null) {
			this.mMessageView.setText(message);
		} else {
			this.mMessage = message;
		}
	}

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
