// Created by plusminus on 18:13:45 - 20.11.2008
package org.andnav2.ui.common.views;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.andnav2.exc.Exceptor;
import org.andnav2.osm.views.util.StreamUtils;
import org.andnav2.preferences.PreferenceConstants;
import org.andnav2.preferences.Preferences;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;


public class AdView extends WebView {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String ADURLBASE = "http://www.andnav.org/index.php/menuitemondeviceads";
	public static final String ADURLSTRING = "http://www.andnav.org/index.php/menuitemondeviceads";
	public static final int AD_WIDTH = 266;
	public static final int AD_HEIGHT = 76;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public AdView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	public AdView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public AdView(final Context context) {
		super(context);
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

	public void loadAd(){
		loadUrl(ADURLSTRING);
	}

	public static void preloadAdAsync(final Context ctx){
		new Thread(new Runnable(){
			public void run() {
				try {
					if(!Preferences.getAdHtmlCode(ctx).equals(PreferenceConstants.PREF_ADHTMLCODE_DEFAULT)) {
						return;
					}

					final URL requestURL = new URL(AdView.ADURLSTRING);

					final StringBuilder sb = new StringBuilder();

					int read = 0;
					final char[] buf = new char[StreamUtils.IO_BUFFER_SIZE];
					final InputStreamReader isr = new InputStreamReader(new BufferedInputStream(requestURL.openStream()));
					while((read = isr.read(buf)) != -1) {
						sb.append(buf, 0, read);
					}

					final String htmlData = sb.toString();

					Preferences.saveAdHtmlCode(ctx, htmlData);
				} catch (final Exception e) {
					Exceptor.e("Error loading ad.", e);
				}
			}
		}).start();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
