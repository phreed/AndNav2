package org.andnav2.ui.common.views;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.andnav2.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The View capable of showing a WeatehrIcon + a Temperature-TextView.
 */
public class SingleWeatherInfoView extends LinearLayout {

	// ===========================================================
	// Fields
	// ===========================================================

	protected TextView mDayTextView = null;
	protected ImageView mWeatherImageView = null;
	protected TextView mTempTextView = null;

	// ===========================================================
	// Constructors
	// ===========================================================

	public SingleWeatherInfoView(final Context context) {
		super(context);
	}

	public SingleWeatherInfoView(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		this.mDayTextView = new TextView(context);
		this.mDayTextView.setTextSize(16);
		this.mDayTextView.setTypeface(Typeface.create("Tahoma", Typeface.BOLD));

		/* Setup the ImageView that will show weather-icon. */
		this.mWeatherImageView = new ImageView(context);
		this.mWeatherImageView.setImageDrawable(getResources().getDrawable(R.drawable.refresh));
		this.mWeatherImageView.setPadding(3,0,3,0);

		/* Setup the textView that will show the temperature. */
		this.mTempTextView = new TextView(context);
		this.mTempTextView.setText("? °C");
		this.mTempTextView.setTextSize(16);
		this.mTempTextView.setTypeface(Typeface.create("Tahoma", Typeface.BOLD));

		/* Add child views to this object. */
		this.addView(this.mDayTextView, new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.addView(this.mWeatherImageView, new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.addView(this.mTempTextView, new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void reset() {
		this.mWeatherImageView.setImageDrawable(getResources().getDrawable(R.drawable.questionmark));
		this.mTempTextView.setText("? °C");
	}

	/** Sets the Child-ImageView of this to the URL passed. */
	public void setRemoteImage(final URL aURL) {
		try {
			final URLConnection conn = aURL.openConnection();
			conn.connect();
			final InputStream is = conn.getInputStream();
			final BufferedInputStream bis = new BufferedInputStream(is);
			final Bitmap bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
			this.mWeatherImageView.setImageBitmap(bm);
		} catch (final IOException e) {
			/* Reset to 'Dunno' on any error. */
			this.mWeatherImageView.setImageDrawable(getResources().getDrawable(R.drawable.questionmark));
		}
	}

	public void setDayString(final String aDayString) {
		this.mDayTextView.setText(aDayString);
	}

	public void setTempString(final String aTempString) {
		this.mTempTextView.setText(aTempString);
	}
}
