//Created by plusminus on 15:47:12 - 15.02.2008
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.sd.SDFavorites;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;


public class SettingsMenu extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================
	/* REQUEST-CODES for SubActivities. */
	protected static final int REQUESTCODE_QUALITY = 0x1937;
	protected static final int REQUESTCODE_DIRECTIONSLANGUAGE = REQUESTCODE_QUALITY + 1;
	protected static final int REQUESTCODE_KEYLAYOUT = REQUESTCODE_DIRECTIONSLANGUAGE + 1;
	protected static final int REQUESTCODE_SETHOME = REQUESTCODE_KEYLAYOUT + 1;
	protected static final int REQUESTCODE_UNITSYSTEM = REQUESTCODE_SETHOME + 1;
	protected static final int REQUESTCODE_FAVORITES = REQUESTCODE_UNITSYSTEM + 1;
	protected static final int REQUESTCODE_STATISTICS = REQUESTCODE_FAVORITES + 1;
	protected static final int REQUESTCODE_ROUTINGFLAGS_SETTINGS = REQUESTCODE_STATISTICS + 1;
	protected static final int REQUESTCODE_CACHE_SETTINGS = REQUESTCODE_ROUTINGFLAGS_SETTINGS + 1;
	protected static final int REQUESTCODE_SCREENORIENTATION = REQUESTCODE_CACHE_SETTINGS + 1;
	protected static final int REQUESTCODE_TRACEPOLICY = REQUESTCODE_SCREENORIENTATION + 1;
	protected static final int REQUESTCODE_COLORSCHEME = REQUESTCODE_TRACEPOLICY + 1;
	protected static final int REQUESTCODE_NAVIGATIONSETTINGS = REQUESTCODE_COLORSCHEME + 1;
	protected static final int REQUESTCODE_HUDSETTING = REQUESTCODE_NAVIGATIONSETTINGS + 1;
	protected static final int REQUESTCODE_SERVER = REQUESTCODE_HUDSETTING + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	protected TabHost myTabHost;

	// ===========================================================
	// Constructors
	// ===========================================================
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_menu);

		this.myTabHost = (TabHost)this.findViewById(R.id.th_settings_menu_tabhost);
		this.setupTabs();

		this.updateThemeButtonImage(false);

		this.applySettingsButtonListenersPageDevice();
		this.applySettingsButtonListenersPageNav();
		this.applySettingsButtonListenersPageUser();
	}

	protected void updateThemeButtonImage() {
		updateThemeButtonImage(true);
	}

	protected void updateThemeButtonImage(final boolean changeQuickInfo) {
		final int themeID = Preferences.getSharedThemeID(SettingsMenu.this);

		final int drawableID;
		final int resTextID;
		switch(themeID){
			case PREF_THEME_NIGHT_RESID:
				drawableID = R.drawable.settings_colorscheme_nightmode;
				resTextID = R.string.tv_settings_quickinfo_colorscheme_night;
				break;
			case PREF_THEME_DAY_RESID:
				drawableID = R.drawable.settings_colorscheme_daymode;
				resTextID = R.string.tv_settings_quickinfo_colorscheme_day;
				break;
			case PREF_THEME_DEFAULT_RESID:
			default:
				drawableID = R.drawable.settings_colorscheme_defaultmode;
				resTextID = R.string.tv_settings_quickinfo_colorscheme_default;
				break;
		}
		if(changeQuickInfo) {
			setQuickinfoPageDeviceTextByID(resTextID);
		}
		((ImageView)this.findViewById(R.id.ibtn_settings_colorscheme)).setImageResource(drawableID);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	protected void setQuickinfoPageNavTextByID(final int resTextId){
		((TextView)this.findViewById(R.id.tv_settings_quickinfo_page_nav)).setText(resTextId);
	}

	protected void setQuickinfoPageDeviceTextByID(final int resTextId){
		((TextView)this.findViewById(R.id.tv_settings_quickinfo_page_device)).setText(resTextId);
	}

	protected void setQuickinfoPageUserTextByID(final int resTextId){
		((TextView)this.findViewById(R.id.tv_settings_quickinfo_page_user)).setText(resTextId);
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	/** React on returning Activities (like centermode and Rotatemode), i.e. to update the button-images. */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch(requestCode){
			case REQUESTCODE_COLORSCHEME:
				this.updateThemeButtonImage(true);
				break;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void applySettingsButtonListenersPageNav() {

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_directionslanguage)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageNavTextByID(R.string.tv_settings_quickinfo_directionslanguage_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent ddLanguageIntent = new Intent(SettingsMenu.this, SettingsDirectionLanguage.class);
				SettingsMenu.this.startActivityForResult(ddLanguageIntent, REQUESTCODE_DIRECTIONSLANGUAGE);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_routingflags)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageNavTextByID(R.string.tv_settings_quickinfo_routingflags_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent advancedNavSettingsIntent = new Intent(SettingsMenu.this, SettingsRoutingFlags.class);
				SettingsMenu.this.startActivityForResult(advancedNavSettingsIntent, REQUESTCODE_ROUTINGFLAGS_SETTINGS);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_server)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageNavTextByID(R.string.tv_settings_quickinfo_orsserver_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent serverIntent = new Intent(SettingsMenu.this, SettingsORSServer.class);
				SettingsMenu.this.startActivityForResult(serverIntent, REQUESTCODE_SERVER);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_navigation)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageNavTextByID(R.string.tv_settings_quickinfo_navigation_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent centermodeIntent = new Intent(SettingsMenu.this, SettingsNavigation.class);
				SettingsMenu.this.startActivityForResult(centermodeIntent, REQUESTCODE_NAVIGATIONSETTINGS);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_hud)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageNavTextByID(R.string.tv_settings_quickinfo_hud_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent hudIntent = new Intent(SettingsMenu.this, SettingsHUD.class);
				SettingsMenu.this.startActivityForResult(hudIntent, REQUESTCODE_HUDSETTING);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_close_page_nav)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SettingsMenu.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsMenu.this, R.raw.close).start();
				}
			}

			@Override
			public void onFocusChange(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageNavTextByID(R.string.tv_settings_quickinfo_close_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Close this (Settings)-Activity. */
				SettingsMenu.this.finish();
			}
		};
	}

	private void applySettingsButtonListenersPageDevice() {

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_cache)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageDeviceTextByID(R.string.tv_settings_quickinfo_cache_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent cacheSettingsIntent = new Intent(SettingsMenu.this, SettingsCache.class);
				SettingsMenu.this.startActivityForResult(cacheSettingsIntent, REQUESTCODE_CACHE_SETTINGS);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_colorscheme)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageDeviceTextByID(R.string.tv_settings_quickinfo_colorscheme_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent colorschemeIntent = new Intent(SettingsMenu.this, SettingsColorscheme.class);
				SettingsMenu.this.startActivityForResult(colorschemeIntent, REQUESTCODE_COLORSCHEME);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_keylayout)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageUserTextByID(R.string.tv_settings_quickinfo_keylayout_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent keyLayoutIntent = new Intent(SettingsMenu.this, SettingsKeyLayout.class);
				SettingsMenu.this.startActivityForResult(keyLayoutIntent, REQUESTCODE_KEYLAYOUT);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_quality)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageDeviceTextByID(R.string.tv_settings_quickinfo_quality_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent displayQualityIntent = new Intent(SettingsMenu.this, SettingsQuality.class);
				SettingsMenu.this.startActivityForResult(displayQualityIntent, REQUESTCODE_QUALITY);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_screenorientation)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageDeviceTextByID(R.string.tv_settings_quickinfo_screenorientation_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent screenorientationIntent = new Intent(SettingsMenu.this, SettingsScreenOrientation.class);
				SettingsMenu.this.startActivityForResult(screenorientationIntent, REQUESTCODE_SCREENORIENTATION);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_close_page_device)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SettingsMenu.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsMenu.this, R.raw.close).start();
				}
			}

			@Override
			public void onFocusChange(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageDeviceTextByID(R.string.tv_settings_quickinfo_close_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Close this (Settings)-Activity. */
				SettingsMenu.this.finish();
			}
		};
	}


	private void applySettingsButtonListenersPageUser() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_osmcontribution)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageDeviceTextByID(R.string.tv_settings_quickinfo_tracepolicy_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent tracePolicyIntent = new Intent(SettingsMenu.this, SettingsTracePolicy.class);
				SettingsMenu.this.startActivityForResult(tracePolicyIntent, REQUESTCODE_TRACEPOLICY);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_sethome)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageUserTextByID(R.string.tv_settings_quickinfo_sethome_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent selectHomeLocationIntent = new Intent(SettingsMenu.this, SettingsSelectHome.class);
				SettingsMenu.this.startActivityForResult(selectHomeLocationIntent, REQUESTCODE_SETHOME);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_favorites)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageUserTextByID(R.string.tv_settings_quickinfo_favorites_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent favIntent = new Intent(SettingsMenu.this, SDFavorites.class);
				final Bundle b = new Bundle();
				b.putBoolean(SDFavorites.EXTRAS_FAVORITES_REFER, false);
				favIntent.putExtras(b);
				SettingsMenu.this.startActivityForResult(favIntent, REQUESTCODE_FAVORITES);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_unitsystem)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageUserTextByID(R.string.tv_settings_quickinfo_unitsystem_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent unitsystemIntent = new Intent(SettingsMenu.this, SettingsUnitSystem.class);
				SettingsMenu.this.startActivityForResult(unitsystemIntent, REQUESTCODE_UNITSYSTEM);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_statistics)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageUserTextByID(R.string.tv_settings_quickinfo_statistics_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				final Intent statisticsIntent = new Intent(SettingsMenu.this, SettingsStatistics.class);
				SettingsMenu.this.startActivityForResult(statisticsIntent, REQUESTCODE_STATISTICS);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_close_page_user)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SettingsMenu.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsMenu.this, R.raw.close).start();
				}
			}

			@Override
			public void onFocusChange(final View me, final boolean focused) {
				if(focused) {
					SettingsMenu.this.setQuickinfoPageUserTextByID(R.string.tv_settings_quickinfo_close_focused);
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Close this (Settings)-Activity. */
				SettingsMenu.this.finish();
			}
		};
	}

	private void setupTabs() {
		this.myTabHost.setup();

		final TabSpec tsNav = this.myTabHost.newTabSpec("NAV");
		tsNav.setIndicator("", getResources().getDrawable(R.drawable.map));
		tsNav.setContent(R.id.grid_settings_menu_page_nav);
		this.myTabHost.addTab(tsNav);

		final TabSpec tsDevice = this.myTabHost.newTabSpec("DEV");
		tsDevice.setIndicator("", getResources().getDrawable(R.drawable.device));
		tsDevice.setContent(R.id.grid_settings_menu_page_device);
		this.myTabHost.addTab(tsDevice);

		final TabSpec tsUser = this.myTabHost.newTabSpec("USR");
		tsUser.setIndicator("", getResources().getDrawable(R.drawable.user_settings));
		tsUser.setContent(R.id.grid_settings_menu_page_user);
		this.myTabHost.addTab(tsUser);

		this.myTabHost.setCurrentTab(0);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
