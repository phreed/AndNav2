// Created by plusminus on 23:44:27 - 18.12.2008
package org.andnav2.ui.common;

import java.util.List;


import org.andnav2.R;
import org.andnav2.adt.Direction;
import org.andnav2.adt.TrafficFeed;
import org.andnav2.db.DBManager;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.sys.ors.adt.rs.DirectionsLanguage;
import org.andnav2.sys.osb.adt.OpenStreetBug;
import org.andnav2.ui.ProVersion;
import org.andnav2.ui.sd.SDPOICategories;
import org.andnav2.ui.sd.SDPOIEntry;
import org.openstreetmap.api.exceptions.OSMAPIException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.ClipboardManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class CommonDialogFactory {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int ALTITUDEPROFILEDIALOG_CONTAINERVIEW_ID = 0;

	// ===========================================================
	// Dialog Creations
	// ===========================================================

	public static Dialog createSelectDialectFromNationality(final Context ctx, final Country pFrom, final CommonCallback<DirectionsLanguage> pCallback){
		final DirectionsLanguage[] drivingDiectionsLanguages = pFrom.getDrivingDiectionsLanguages();
		final String[] choices = new String[drivingDiectionsLanguages.length];
		for (int i = 0; i < drivingDiectionsLanguages.length; i++) {
			choices[i] = ctx.getString(drivingDiectionsLanguages[i].NAMERESID);
		}

		return new AlertDialog.Builder(ctx).setSingleChoiceItems(choices , 0, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();

				if(which < 0) {
					pCallback.onFailure(new IllegalAccessException("Nothing was selected."));
				} else {
					pCallback.onSuccess(drivingDiectionsLanguages[which]);
				}
			}
		}).create();
	}

	public static Dialog createOSMAccountInfoDialog(final Context ctx, final CommonCallback<Boolean> pCallback){
		final LayoutInflater inflater = LayoutInflater.from(ctx);
		final FrameLayout fl = (FrameLayout)inflater.inflate(R.layout.dlg_input_osm_account, null);

		final EditText etUsername = (EditText)fl.findViewById(R.id.et_input_osm_account_username);
		final EditText etPassword = (EditText)fl.findViewById(R.id.et_input_osm_account_password);

		etUsername.setText(Preferences.getOSMAccountUsername(ctx));
		etPassword.setText(Preferences.getOSMAccountPassword(ctx));

		return new AlertDialog.Builder(ctx)
		.setView(fl)
		.setTitle(R.string.dlg_input_osm_account_title)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				final String username = etUsername.getText().toString();
				final String password = etPassword.getText().toString();

				if(username.length() == 0){
					Toast.makeText(ctx, R.string.dlg_input_osm_account_username_tooshort, Toast.LENGTH_SHORT).show();
					pCallback.onSuccess(false);
					return;
				}else if(password.length() == 0){
					Toast.makeText(ctx, R.string.dlg_input_osm_account_password_tooshort, Toast.LENGTH_SHORT).show();
					pCallback.onSuccess(false);
					return;
				}

				Preferences.saveOSMAccountUsername(ctx, username);
				Preferences.saveOSMAccountPassword(ctx, password);

				pCallback.onSuccess(true);
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
				pCallback.onSuccess(false);
			}
		}).create();
	}

	public static Dialog createInputLatLonDialog(final Context ctx, final CommonCallback<GeoPoint> pCallback) {
		final LayoutInflater inflater = LayoutInflater.from(ctx);
		final FrameLayout fl = (FrameLayout)inflater.inflate(R.layout.dlg_input_direct_lat_lon, null);

		final EditText etLat = (EditText)fl.findViewById(R.id.et_input_direct_lat_lon_latitude);
		final EditText etLon = (EditText)fl.findViewById(R.id.et_input_direct_lat_lon_longitude);

		return new AlertDialog.Builder(ctx)
		.setView(fl)
		.setTitle(R.string.dlg_input_direct_lat_lon_title)
		.setIcon(R.drawable.world_small)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				try{
					final double lat;
					final double lon;

					final String latString = etLat.getText().toString();
					if(latString.contains(",")) {
						lat = Double.parseDouble(latString.replace(",", "."));
					} else {
						lat = Double.parseDouble(latString);
					}


					final String lonString = etLon.getText().toString();
					if(lonString.contains(",")) {
						lon = Double.parseDouble(lonString.replace(",", "."));
					} else {
						lon = Double.parseDouble(lonString);
					}

					/* If no exception received until here, values are valid! */
					d.dismiss();

					pCallback.onSuccess(new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6)));
				}catch(final Exception e){
					pCallback.onFailure(e);
				}
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
			}
		}).create();
	}

	/**
	 * @param ctx
	 * @param pCallback 0 for config, 1 for sample
	 */
	public static Dialog createTTSConfigOrTestDialog(final Context ctx, final CommonCallback<Integer> pCallback) {
		final String[] choices = {ctx.getString(R.string.dlg_settings_voice_tts_change_settings), ctx.getString(R.string.dlg_settings_voice_tts_play_sample)};
		return new AlertDialog.Builder(ctx).setSingleChoiceItems(choices , 0, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();

				if(which < 0) {
					pCallback.onFailure(new IllegalAccessException("Nothing was selected."));
				} else {
					pCallback.onSuccess(which);
				}
			}
		}).create();
	}

	/**
	 * @param ctx
	 * @param pCallback 0 for freeform, 1 for structured
	 */
	public static Dialog createFreeformOrStructuredAddressSelectorDialog(final Context ctx, final CommonCallback<Integer> pCallback) {
		final String[] choices = {ctx.getString(R.string.sd_search_address_freeform), ctx.getString(R.string.sd_search_address_structured)};
		return new AlertDialog.Builder(ctx).setSingleChoiceItems(choices , 0, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();

				if(which < 0) {
					pCallback.onFailure(new IllegalAccessException("Nothing was selected."));
				} else {
					pCallback.onSuccess(which);
				}
			}
		}).create();
	}

	/**
	 * @param ctx
	 * @param pCallback 0 for osb, 1 for ftpc,
	 */
	public static Dialog createAddOSBorFTPCDialog(final Context ctx, final CommonCallback<OSBMapLongAddSelectorResult> pCallback) {
		final String[] choices = {ctx.getString(R.string.dlg_osb_choose_what_to_add_osmpoi), ctx.getString(R.string.dlg_osb_choose_what_to_add_osb), ctx.getString(R.string.dlg_osb_choose_what_to_add_ftpc)};
		return new AlertDialog.Builder(ctx)
		.setTitle(R.string.dlg_osb_choose_what_to_add)
		.setSingleChoiceItems(choices , 0, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();

				if(which < 0){
					pCallback.onFailure(new IllegalAccessException("Nothing was selected."));
				}else{
					switch(which){
						case 0:
							pCallback.onSuccess(OSBMapLongAddSelectorResult.OSMPOI);
							break;
						case 1:
							pCallback.onSuccess(OSBMapLongAddSelectorResult.OSB);
							break;
						case 2:
							pCallback.onSuccess(OSBMapLongAddSelectorResult.FTPC);
							break;
					}
				}
			}
		}).create();
	}

	public static Dialog createInputFavoriteNameDialog(final Context ctx, final CommonCallback<String> pCallback) {
		final LayoutInflater inflater = LayoutInflater.from(ctx);
		final FrameLayout fl = (FrameLayout)inflater.inflate(R.layout.dlg_input_favorite_name, null);

		final EditText etName = (EditText)fl.findViewById(R.id.et_dlg_input_favorite_name_name);

		return new AlertDialog.Builder(ctx)
		.setView(fl)
		.setTitle(R.string.dlg_input_favorite_name_title)
		.setIcon(R.drawable.settingsmenu_favorites)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				try{
					final String name = etName.getText().toString();
					if(name.length() < 3) {
						pCallback.onFailure(new IllegalArgumentException("Name not long enough.")); // TODO i18n
					}

					/* If no exception received until here, values are valid! */
					d.dismiss();

					pCallback.onSuccess(name);
				}catch(final Exception e){
					pCallback.onFailure(e);
				}
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
			}
		}).create();
	}

	public static Dialog createFreeformOrCategorizedPOISelectorDialog(final Context ctx, final CommonCallback<Intent> pCallback) {
		final String[] choices = {ctx.getString(R.string.poi_search_categorized), ctx.getString(R.string.poi_search_freeform)};
		return new AlertDialog.Builder(ctx).setSingleChoiceItems(choices , 0, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();

				final Intent poiSearchIntent;
				switch(which){
					case 0:
						poiSearchIntent = new Intent(ctx, SDPOICategories.class);
						break;
					case 1:
					default:
						poiSearchIntent = new Intent(ctx, SDPOIEntry.class);
						break;
				}
				pCallback.onSuccess(poiSearchIntent);
			}
		}).create();
	}

	/**
	 * Will return null, when no Feed is available, so instantly the Add-Feed activity can be launched.
	 * @param ctx
	 * @param pCallback
	 */
	public static Dialog createAddCustomTrafficFeedDialog(final Activity ctx, final CommonCallback<TrafficFeed> pCallback) {
		final LayoutInflater inflater = LayoutInflater.from(ctx);
		final FrameLayout fl = (FrameLayout)inflater.inflate(R.layout.dlg_input_new_trafficfeed, null);

		final EditText etName = (EditText)fl.findViewById(R.id.et_dlg_customtrafficfeed_name);
		final EditText etURL = (EditText)fl.findViewById(R.id.et_dlg_customtrafficfeed_url);
		final Spinner spinCountry = (Spinner)fl.findViewById(R.id.spin_dlg_customtrafficfeed_country);
		final Button btnFeedList = (Button)fl.findViewById(R.id.btn_dlg_customtrafficfeed_showfeedlist);

		btnFeedList.setOnClickListener(new View.OnClickListener(){

			private boolean mListShown = false;

			public void onClick(final View v) {
				if(!this.mListShown){
					btnFeedList.setText(R.string.dlg_customtrafficfeed_clipboardpaste);
					final Intent webIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://trafficfeeds.andnav.org/#feedlist"));
					ctx.startActivity(webIntent);
				}else{
					final ClipboardManager cb = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
					if(cb.hasText()) {
						final String text = cb.getText().toString();
						if(text.startsWith("http://")) {
							etURL.setText(text);
						}
					}
					btnFeedList.setText(R.string.dlg_customtrafficfeed_showfeedlist);
				}

				this.mListShown = !this.mListShown;
			}
		});

		final ArrayAdapter<Country> countryAdapter = new ArrayAdapter<Country>(ctx, android.R.layout.simple_spinner_item, Country.values());
		countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinCountry.setAdapter(countryAdapter);

		return new AlertDialog.Builder(ctx)
		.setView(fl)
		.setTitle(R.string.dlg_customtrafficfeed_title)
		.setIcon(R.drawable.traffic_feed)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				try{
					final String name = etName.getText().toString().trim();

					if(name.length() < 3) {
						pCallback.onFailure(new IllegalArgumentException("Name not long enough.")); // TODO i18n
					}

					final String url = etURL.getText().toString().trim();
					if(url.length() < 3 || !url.startsWith("http://")) {
						pCallback.onFailure(new IllegalArgumentException("Invalid URL. Should start with 'http://'.")); // TODO i18n
					}

					final Country nat = (Country)spinCountry.getSelectedItem();

					final TrafficFeed trafficFeed = new TrafficFeed(name, url, nat);
					DBManager.addTrafficFeed(ctx, trafficFeed);

					/* If no exception received until here, values are valid! */
					d.dismiss();

					pCallback.onSuccess(trafficFeed);
				}catch(final Exception e){
					pCallback.onFailure(e);
				}
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
			}
		}).create();
	}

	/**
	 * Will return null, when no Feed is available, so instantly the Add-Feed activity can be launched.
	 * @param ctx
	 * @param pCallback
	 */
	public static Dialog createSelectCustomTrafficFeedDialog(final Context ctx, final List<TrafficFeed> customFeeds, final CommonCallback<TrafficFeed> pCallback) {
		final int feedCount = customFeeds.size();

		final String[] choices = new String[feedCount + 1];

		for(int i = 0; i < feedCount; i++) {
			choices[i+1] = customFeeds.get(i).getName();
		}

		choices[0] = "Add new..."; //TODO i18n

		return new AlertDialog.Builder(ctx).setSingleChoiceItems(choices , 0, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				if(which == 0) {
					pCallback.onSuccess(null);
				} else {
					pCallback.onSuccess(customFeeds.get(which - 1));
				}

				d.dismiss();
			}
		}).create();
	}

	/**
	 * 
	 * @param ctx
	 * @param pCallback
	 * @return 0 for user, 1 for delete
	 */
	public static Dialog createFavoriteUseOrDeleteDialog(final Context ctx, final CommonCallback<Integer> pCallback){
		return new AlertDialog.Builder(ctx)
		.setSingleChoiceItems(new String[]{ctx.getString(R.string.settings_favortites_contextmenu_use), ctx.getString(R.string.settings_favortites_contextmenu_delete)},
				0,
				new OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
				pCallback.onSuccess(which);
			}
		})
		.create();
	}

	/**
	 * Present the End User License Agreement dialog so the user can accept it.
	 * @param ctx
	 * @param pCallback
	 * @return
	 */
	public static Dialog createAcceptEulaDialog(final Context ctx, final CommonCallback<Boolean> pCallback){
		
		CharSequence msg = ctx.getString(R.string.eula_disclaim) 
		                 + Html.fromHtml(ctx.getString(R.string.eula_gpl3));
		return new AlertDialog.Builder(ctx)
		.setTitle(R.string.eula_title)
		.setMessage(msg)
		.setPositiveButton(R.string.eula_accept, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
				pCallback.onSuccess(true);
			}
		})
		.setNegativeButton(R.string.eula_decline, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
				pCallback.onSuccess(false);
			}
		})
		.create();
	}

	public static Dialog createEnableGPSDialog(final Context ctx, final CommonCallback<Boolean> pCallback){
		return new AlertDialog.Builder(ctx)
		.setTitle(R.string.startup_gpsrequried_title)
		.setMessage(R.string.startup_gpsrequried_message)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				pCallback.onSuccess(true);
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				pCallback.onSuccess(false);
			}
		})
		.create();
	}

	public static Dialog createOpenStreetBugInstructionsDialog(final Context ctx, final CommonCallback<Boolean> pCallback){
		return new AlertDialog.Builder(ctx)
		.setIcon(R.drawable.information)
		.setTitle(R.string.instructions)
		.setMessage(R.string.osb_instructions)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which){
				pCallback.onSuccess(true);
			}
		})
		.setNegativeButton(R.string.nevershowagain, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which){
				pCallback.onSuccess(false);
			}
		})
		.create();
	}

	public static Dialog createInputDialog(final Context ctx, final int pTextualHintResID, final CommonCallback<String> pCallback){
		return createInputDialog(ctx, pTextualHintResID, R.string.cdf_inputbox_title, pCallback);
	}

	public static Dialog createInputDialog(final Context ctx, final int pTextualHintResID, final int pTextualTitleResID, final CommonCallback<String> pCallback){
		final LayoutInflater inflater = LayoutInflater.from(ctx);
		final FrameLayout fl = (FrameLayout)inflater.inflate(R.layout.cdf_inputbox, null);

		((TextView)fl.findViewById(R.id.tv_cdf_inputbox_hint)).setText(pTextualHintResID);
		final EditText etInput = (EditText)fl.findViewById(R.id.et_cdf_inputbox_input);

		return new AlertDialog.Builder(ctx)
		.setIcon(R.drawable.comment_write)
		.setTitle(pTextualTitleResID)
		.setView(fl)
		.setPositiveButton(R.string.ok, new OnClickListener(){
			public void onClick(final DialogInterface d, final int arg1) {
				pCallback.onSuccess(etInput.getText().toString());
				d.dismiss();
			}
		})
		.setNegativeButton(R.string.cancel, new OnClickListener(){
			public void onClick(final DialogInterface d, final int arg1) {
				d.dismiss();
			}
		})
		.create();
	}

	public static Dialog createSavedRouteSelectionOptions(final Context ctx, final CommonCallback<CreateSavedRouteSelectionOptions> pCallback){
		final String[] choices = new String[]{ctx.getString(R.string.dlg_sd_savedroutechooser_contextmenu_use),
				ctx.getString(R.string.dlg_sd_savedroutechooser_contextmenu_rename),
				ctx.getString(R.string.dlg_sd_savedroutechooser_contextmenu_information)};

		return new AlertDialog.Builder(ctx).setSingleChoiceItems(choices , 0, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
				// TODO SHARE
				switch(which){
					case 0:
						pCallback.onSuccess(CreateSavedRouteSelectionOptions.USE);
						break;
					case 1:
						pCallback.onSuccess(CreateSavedRouteSelectionOptions.RENAME);
						break;
					case 2:
						pCallback.onSuccess(CreateSavedRouteSelectionOptions.INFORMATION);
						break;
				}
			}
		}).create();
	}

	public static Dialog createSavedTraceSelectionOptions(final Context ctx, final CommonCallback<CreateSavedTraceSelectionOptions> pCallback){
		final String[] choices = new String[]{ctx.getString(R.string.dlg_sd_savedtracechooser_contextmenu_use),
				ctx.getString(R.string.dlg_sd_savedtracechooser_contextmenu_rename),
				ctx.getString(R.string.dlg_sd_savedtracechooser_contextmenu_information),
				ctx.getString(R.string.dlg_sd_savedtracechooser_contextmenu_share_trailmapping)};

		return new AlertDialog.Builder(ctx).setSingleChoiceItems(choices , 0, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
				switch(which){
					case 0:
						pCallback.onSuccess(CreateSavedTraceSelectionOptions.USE);
						break;
					case 1:
						pCallback.onSuccess(CreateSavedTraceSelectionOptions.RENAME);
						break;
					case 2:
						pCallback.onSuccess(CreateSavedTraceSelectionOptions.INFORMATION);
						break;
					case 3:
						pCallback.onSuccess(CreateSavedTraceSelectionOptions.SHARE_TRAILMAPPING);
						break;
				}
			}
		}).create();
	}

	public static Dialog createORSServerSelectionOptionsDialog(final Context ctx, final CommonCallback<CreateORSServerSelectionOptions> pCallback){
		final String[] choices = new String[]{ctx.getString(R.string.settings_orsserver_contextmenu_use),
				ctx.getString(R.string.settings_orsserver_contextmenu_ping),
				ctx.getString(R.string.settings_orsserver_contextmenu_information)};

		return new AlertDialog.Builder(ctx).setSingleChoiceItems(choices , 0, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
				switch(which){
					case 0:
						pCallback.onSuccess(CreateORSServerSelectionOptions.USE);
						break;
					case 1:
						pCallback.onSuccess(CreateORSServerSelectionOptions.PING);
						break;
					case 2:
						pCallback.onSuccess(CreateORSServerSelectionOptions.INFORMATION);
						break;
				}
			}
		}).create();
	}

	public static Dialog createDiagonalDirectionDialog(final Context ctx, final CommonCallback<Direction> pCallback) {
		final String[] choices = {Direction.NORTHEAST.NAME, Direction.SOUTHEAST.NAME, Direction.SOUTHWEST.NAME, Direction.NORTHWEST.NAME};
		return new AlertDialog.Builder(ctx).setSingleChoiceItems(choices , 0, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
				switch(which){
					case 0:
						pCallback.onSuccess(Direction.NORTHEAST);
						break;
					case 1:
						pCallback.onSuccess(Direction.SOUTHEAST);
						break;
					case 2:
						pCallback.onSuccess(Direction.SOUTHWEST);
						break;
					case 3:
						pCallback.onSuccess(Direction.NORTHWEST);
						break;
				}
			}
		}).create();
	}

	public static Dialog createReportBugDialog(final Context ctx, final CommonCallback<Boolean> pCallback) {
		return new AlertDialog.Builder(ctx).setMessage("Please report bugs to support@andnav.org with a brief description of the problem. Thank you!") // TODO i18n
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				pCallback.onSuccess(true);
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				pCallback.onSuccess(false);
			}
		})
		.create();
	}

	public static Dialog createVersionInfoDialog(final Context ctx, final CommonCallback<Void> pCallback) {
		final StringBuilder sb = new StringBuilder();
		sb.append(ctx.getString(R.string.app_name))
		.append('\n')
		.append("VersionName: ").append(org.andnav2.util.Util.getVersionName(ctx))
		.append('\n')
		.append("VersionNumber: ").append(org.andnav2.util.Util.getVersionNumber(ctx))
		.append('\n');

		return new AlertDialog.Builder(ctx)
		.setTitle(R.string.versioninfo)
		.setMessage(sb.toString())
		.setIcon(R.drawable.hardhat)
		.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
			}
		})
		.create();
	}

	public static Dialog createAltitudeProfileDialog(final Context ctx, final CommonCallback<Void> pCallback) {
		final LinearLayout container = new LinearLayout(ctx);
		container.setId(ALTITUDEPROFILEDIALOG_CONTAINERVIEW_ID);
		final AlertDialog.Builder ab = new AlertDialog.Builder(ctx)
		.setIcon(R.drawable.altitude_profile)
		.setTitle(R.string.maps_menu_altitude_profile_title)
		.setView(container)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
				pCallback.onSuccess(null);
			}
		});

		return ab.create();
	}

	public static Dialog createOSBRefreshBugsDialog(final Context ctx, final int pMessageResID, final CommonCallback<Boolean> pCallback){
		return new AlertDialog.Builder(ctx)
		.setIcon(R.drawable.refresh)
		.setTitle(R.string.success)
		.setMessage(pMessageResID)
		.setPositiveButton(R.string.refresh, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
				pCallback.onSuccess(true);
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
			}
		})
		.create();
	}

	public static Dialog createAddOSBBugDialog(final Context ctx, final CommonCallback<String> pCallback){
		final LayoutInflater inflater = LayoutInflater.from(ctx);
		final FrameLayout fl = (FrameLayout)inflater.inflate(R.layout.dlg_osb_add_bug, null);

		final EditText etComment = (EditText)fl.findViewById(R.id.et_dlg_osb_add_bug_new_comment);
		final EditText etCommenterName = (EditText)fl.findViewById(R.id.et_dlg_osb_add_bug_name);

		etCommenterName.setText(Preferences.getOSBCommenterName(ctx));
		etComment.selectAll();

		return new AlertDialog.Builder(ctx)
		.setView(fl)
		.setTitle(R.string.dlg_osb_edit_bug_title)
		.setPositiveButton(R.string.save, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				final String newComment = etComment.getText().toString();

				if(newComment.length() == 0){
					Toast.makeText(ctx, R.string.dlg_osb_bug_invalid_comment, Toast.LENGTH_SHORT).show();
					pCallback.onFailure(new IllegalArgumentException("Invalid comment"));
					return;
				}

				final String commenterName = etCommenterName.getText().toString();

				Toast.makeText(ctx, R.string.please_wait_a_moment, Toast.LENGTH_LONG).show();
				new Thread(new Runnable(){
					public void run() {
						final String commentWithName;

						/* Append the name in brackets and append the comment. */
						if(commenterName.length() == 0){
							commentWithName = newComment + " [" + ctx.getString(R.string.dlg_osb_commentername) + "]";
						}else{
							Preferences.saveOSBCommenterName(ctx, commenterName);
							commentWithName = newComment + " [" + commenterName + "]";
						}

						pCallback.onSuccess(commentWithName);
					}
				}).start();

				d.dismiss();
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
			}
		}).create();
	}

	public static Dialog createEditOSBBugDialog(final Context ctx, final OpenStreetBug pOSB, final CommonCallback<CreateEditOSBBugDialogResult> pCallback){
		final LayoutInflater inflater = LayoutInflater.from(ctx);
		final FrameLayout fl = (FrameLayout)inflater.inflate(R.layout.dlg_osb_edit_bug, null);

		final TextView tvPreviousComments = (TextView)fl.findViewById(R.id.tv_dlg_osb_edit_bug_previous_comments);
		tvPreviousComments.setText(pOSB.getDesription());

		final EditText etNewComment = (EditText)fl.findViewById(R.id.et_dlg_osb_edit_bug_new_comment);
		final EditText etCommenterName = (EditText)fl.findViewById(R.id.et_dlg_osb_edit_bug_name);
		final CheckBox cbCloseBug = (CheckBox)fl.findViewById(R.id.chk_dlg_osb_edit_bug_close_bug);

		etCommenterName.setText(Preferences.getOSBCommenterName(ctx));
		etNewComment.selectAll();

		final Dialog d = new AlertDialog.Builder(ctx)
		.setView(fl)
		.setTitle(R.string.dlg_osb_edit_bug_title)
		.setPositiveButton(R.string.save, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				final String newComment = etNewComment.getText().toString();
				final boolean closeBug = cbCloseBug.isChecked();

				if(newComment.length() == 0){
					Toast.makeText(ctx, R.string.dlg_osb_bug_invalid_comment, Toast.LENGTH_SHORT).show();
					pCallback.onFailure(new IllegalArgumentException("Invalid comment"));
					return;
				}

				final String commenterName = etCommenterName.getText().toString();

				Toast.makeText(ctx, R.string.please_wait_a_moment, Toast.LENGTH_LONG).show();
				new Thread(new Runnable(){
					public void run() {
						final String commentWithName;
						if(commenterName.length() == 0){
							commentWithName = newComment + " [" + ctx.getString(R.string.dlg_osb_commentername) + "]";
						}else{
							Preferences.saveOSBCommenterName(ctx, commenterName);
							commentWithName = newComment + " [" + commenterName + "]";
						}

						pCallback.onSuccess(new CreateEditOSBBugDialogResult(closeBug, commentWithName));
					}
				}).start();

				d.dismiss();
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
			}
		}).create();

		return d;
	}

	public static Dialog createInputOSMPOIDialog(final Context ctx, final POIType pCategoryType, final CommonCallback<String> pCallback) {
		final LayoutInflater inflater = LayoutInflater.from(ctx);
		final FrameLayout fl = (FrameLayout)inflater.inflate(R.layout.dlg_input_poiname, null);

		final TextView tvCategoryName = (TextView)fl.findViewById(R.id.tv_dlg_input_osmpoiname_name);
		tvCategoryName.setText(pCategoryType.READABLENAMERESID);

		final EditText etPOIName = (EditText)fl.findViewById(R.id.et_dlg_input_osmpoiname_name);

		return new AlertDialog.Builder(ctx)
		.setView(fl)
		.setTitle(R.string.dlg_osb_edit_bug_title)
		.setPositiveButton(R.string.save, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				pCallback.onSuccess(etPOIName.getText().toString());
				d.dismiss();
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				pCallback.onFailure(new OSMAPIException("Aborted"));
				d.dismiss();
			}
		})
		.create();
	}

	public static Dialog createAddOSMPOIFailedDialog(final Context ctx, final CommonCallback<Void> pCallback) {
		// TODO Add icon

		return new AlertDialog.Builder(ctx)
		.setTitle(R.string.dlg_osb_add_osmpoi_failed_title)
		.setMessage(R.string.dlg_osb_add_osmpoi_failed_message)
		.create();
	}

	public static Dialog createAddOSMPOISuccessDialog(final Context ctx, final CommonCallback<Void> pCallback) {
		// TODO Add icon

		return new AlertDialog.Builder(ctx)
		.setTitle(R.string.dlg_osb_add_osmpoi_success_title)
		.setMessage(R.string.dlg_osb_add_osmpoi_success_message)
		.create();
	}

	public static Dialog createNotInLiteVersionDialog(final Context ctx){
		return createNotInLiteVersionDialog(ctx, new CommonCallbackAdapter<Boolean>(){
			@Override
			public void onSuccess(final Boolean result) {
				if(result){
					final Intent proversionIntent = new Intent(ctx, ProVersion.class);
					ctx.startActivity(proversionIntent);
				}
			}
		});
	}

	public static Dialog createNotInLiteVersionDialog(final Context ctx, final CommonCallback<Boolean> pCallback){
		return new AlertDialog.Builder(ctx)
		.setIcon(R.drawable.market_bag)
		.setTitle(R.string.dlg_notinliteversion_title)
		.setMessage(R.string.dlg_notinliteversion_message)
		.setPositiveButton(R.string.dlg_notinliteversion_moreinfo, new OnClickListener(){
			public void onClick(final DialogInterface arg0, final int arg1) {
				pCallback.onSuccess(true);
			}
		})
		.setNegativeButton(R.string.cancel, new OnClickListener(){
			public void onClick(final DialogInterface arg0, final int arg1) {
				pCallback.onSuccess(false);
			}
		})
		.create();
	}

	public static Dialog createORSServerSelectionInstructionsDialog(final Context ctx, final CommonCallback<Void> pCallback) {
		return new AlertDialog.Builder(ctx)
		.setIcon(R.drawable.information)
		.setTitle(R.string.instructions)
		.setMessage(R.string.orsserver_selection_instructions)
		.setPositiveButton(R.string.ok, null)
		.create();
	}

	public static Dialog createORSServerChangeableInfoDialog(final Context ctx, final CommonCallback<Void> pCallback) {
		return new AlertDialog.Builder(ctx)
		.setIcon(R.drawable.information)
		.setTitle(R.string.instructions)
		.setMessage(R.string.orsserver_selection_changeable)
		.setPositiveButton(R.string.ok, new OnClickListener(){
			public void onClick(final DialogInterface arg0, final int arg1) {
				pCallback.onSuccess(null);
			}
		})
		.create();
	}

	public static Dialog createNationalitySelectionDialog(final Context ctx, final List<Country> pCountries, final CommonCallback<Country> pCallback){
		final String[] choices = new String[pCountries.size()];

		for (int i = 0; i < choices.length; i++) {
			choices[i] = ctx.getString(pCountries.get(i).NAMERESID);
		}

		return new AlertDialog.Builder(ctx).setSingleChoiceItems(choices , 0, new DialogInterface.OnClickListener(){
			public void onClick(final DialogInterface d, final int which) {
				d.dismiss();
				switch(which){
					case 0:
						pCallback.onSuccess(pCountries.get(which));
						break;
					default:
						break;
				}
			}
		}).create();
	}

	// ===========================================================
	// Dialog-Preparations
	// ===========================================================

	public static void prepareInputOSMPOIDialog(final Context ctx, final Dialog d, final POIType pCategoryType){
		final TextView tvCategoryName = (TextView)d.findViewById(R.id.tv_dlg_input_osmpoiname_name);
		tvCategoryName.setText(pCategoryType.READABLENAMERESID);
	}

	public static void prepareEditOSBBugDialog(final Context ctx, final Dialog d, final OpenStreetBug pOSB) {
		final TextView tvPreviousComments = (TextView)d.findViewById(R.id.tv_dlg_osb_edit_bug_previous_comments);
		tvPreviousComments.setText(pOSB.getDesription());
	}

	public static void prepareAltitudeProfileDialog(final Context ctx, final Dialog d, final Bitmap result){
		if(result == null){
			final TextView tv = new TextView(ctx);
			tv.setText(R.string.maps_menu_altitude_profile_error);

			final LinearLayout ll = (LinearLayout)d.findViewById(ALTITUDEPROFILEDIALOG_CONTAINERVIEW_ID);
			ll.removeAllViews();
			ll.addView(tv);
		}else{
			final ImageView iv = new ImageView(ctx);
			iv.setImageBitmap(result);
			final LinearLayout ll = (LinearLayout)d.findViewById(ALTITUDEPROFILEDIALOG_CONTAINERVIEW_ID);
			ll.removeAllViews();
			ll.addView(iv);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static enum OSBMapLongAddSelectorResult {
		OSB, FTPC, OSMPOI;
	}

	public static class CreateEditOSBBugDialogResult {
		public final boolean mCloseBug;
		public final String mCommentWithName;
		public CreateEditOSBBugDialogResult(final boolean closeBug, final String commentWithName) {
			this.mCloseBug = closeBug;
			this.mCommentWithName = commentWithName;
		}
	}

	public static enum CreateORSServerSelectionOptions {
		USE, PING, INFORMATION;
	}

	public static enum CreateSavedRouteSelectionOptions {
		USE, RENAME, INFORMATION, SHARE;
	}

	public static enum CreateSavedTraceSelectionOptions {
		USE, RENAME, INFORMATION, SHARE_TRAILMAPPING;
	}
}
