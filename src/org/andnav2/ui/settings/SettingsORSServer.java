// Created by plusminus on 21:11:40 - 13.06.2008
package org.andnav2.ui.settings;

import java.util.ArrayList;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.ORSServer;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.CommonCallbackAdapter;
import org.andnav2.ui.common.CommonDialogFactory;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.common.CommonDialogFactory.CreateORSServerSelectionOptions;
import org.andnav2.ui.common.adapters.ORSServerListAdapter;
import org.andnav2.ui.common.adapters.ORSServerListAdapter.ORSServerItem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SettingsORSServer extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	protected static final int CONTEXTMENU_USEITEM = 0;

	private static final int MENU_HELP_ID = 0;
	private static final int MENU_PINGALL_ID = MENU_HELP_ID + 1;

	private static final int DIALOG_SHOW_HELP = 0;
	protected static final int DIALOG_SHOW_SERVEROPTIONS = DIALOG_SHOW_HELP + 1;

	private static final String STATE_SERVER_ITEMS_ID = "state_server_items_id";

	// ===========================================================
	// Fields
	// ===========================================================

	protected Bundle bundleCreatedWith;
	protected ListView mServerList;
	protected ORSServerItem mSelectedServerItem;

	private ArrayList<ORSServerItem> mServerItems = new ArrayList<ORSServerItem>();

	private final Runnable mListInvalidationCallback = new Runnable(){
		public void run() {
			runOnUiThread(new Runnable(){
				public void run() {
					SettingsORSServer.this.mServerList.invalidateViews();
				}
			});
		}
	};

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, false);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_orsserver);

		/*
		 * Save the Extras Bundle of the Intent this Activity was created with,
		 * because it contains the Information, that will finally be used for a
		 * GeoCode API.
		 */
		this.bundleCreatedWith = this.getIntent().getExtras();
		this.mServerList = (ListView) this.findViewById(R.id.list_orsservers);

		final TextView empty = new TextView(this);
		empty.setText(R.string.list_empty);
		this.mServerList.setEmptyView(empty);

		this.applyTopMenuButtonListeners();

		initListView();

		if(savedInstanceState == null){ // First start
			updateServerListItems();
			setServerAdapter();
		}
	}

	private void updateServerListItems() {
		final ORSServer[] servers = ORSServer.values();
		this.mServerItems = new ArrayList<ORSServerItem>(servers.length);
		for(final ORSServer s : servers) {
			this.mServerItems.add(new ORSServerItem(s));
		}
	}

	private void pingAllServers(){
		for(final ORSServerItem o : this.mServerItems) {
			o.updatePingInformation(this.mListInvalidationCallback);
		}

		Toast.makeText(this, R.string.please_wait_a_moment, Toast.LENGTH_SHORT).show();
	}

	private void setServerAdapter() {
		final ORSServerListAdapter adapter = new ORSServerListAdapter(this);

		adapter.setListItems(this.mServerItems);
		this.mServerList.setAdapter(adapter);
		pingAllServers();
	}

	protected void initListView() {
		this.mServerList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				SettingsORSServer.this.mSelectedServerItem = (ORSServerItem)parent.getAdapter().getItem(position);
				showDialog(DIALOG_SHOW_SERVEROPTIONS);
			}
		});
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onSaveInstanceState(final Bundle out) {
		out.putParcelableArrayList(STATE_SERVER_ITEMS_ID, this.mServerItems);
	}

	@Override
	protected void onRestoreInstanceState(final Bundle in) {
		final ArrayList<ORSServerItem> restoredItems = in.getParcelableArrayList(STATE_SERVER_ITEMS_ID);
		if(restoredItems == null){
			updateServerListItems();
		}else{
			this.mServerItems = restoredItems;
			setServerAdapter();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, MENU_HELP_ID, Menu.NONE, R.string.instructions).setIcon(R.drawable.information).setAlphabeticShortcut('i');
		menu.add(0, MENU_PINGALL_ID, Menu.NONE, R.string.orsserver_pingall).setIcon(R.drawable.refresh).setAlphabeticShortcut('r');
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		switch(id){
			case DIALOG_SHOW_HELP:
				return CommonDialogFactory.createORSServerSelectionInstructionsDialog(this, new CommonCallbackAdapter<Void>(){
					@Override
					public void onSuccess(final Void result) {
						/* Nothing. */
					}
				});
			case DIALOG_SHOW_SERVEROPTIONS:
				return CommonDialogFactory.createORSServerSelectionOptionsDialog(this, new CommonCallbackAdapter<CreateORSServerSelectionOptions>(){
					@Override
					public void onSuccess(final CreateORSServerSelectionOptions result) {
						switch(result){
							case USE:
								Preferences.saveORSServer(SettingsORSServer.this, SettingsORSServer.this.mSelectedServerItem.mORSServer);
								Toast.makeText(SettingsORSServer.this, R.string.save, Toast.LENGTH_SHORT).show();
								break;
							case PING:
								Toast.makeText(SettingsORSServer.this, R.string.please_wait_a_moment, Toast.LENGTH_SHORT).show();
								SettingsORSServer.this.mSelectedServerItem.updatePingInformation(SettingsORSServer.this.mListInvalidationCallback);
								break;
							case INFORMATION:
								new AlertDialog.Builder(SettingsORSServer.this)
								.setIcon(R.drawable.information)
								.setTitle(R.string.orsserver_serverinfo_title)
								.setMessage(SettingsORSServer.this.mSelectedServerItem.mORSServer.SERVERDESCRIPTION)
								.setPositiveButton(R.string.ok, null)
								.create()
								.show();
								break;
						}
					}
				});
			default:
				return null;
		}
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch(item.getItemId()){
			case MENU_HELP_ID:
				showDialog(DIALOG_SHOW_HELP);
				return true;
			case MENU_PINGALL_ID:
				pingAllServers();
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

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

	protected void applyTopMenuButtonListeners() {
		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_orsserver_close)) {

			@Override
			public void onBoth(final View me, final boolean focused) {
				if (focused && SettingsORSServer.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsORSServer.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/*
				 * Set ResultCode that the calling activity knows that we want
				 * to go back to the Base-Menu
				 */
				SettingsORSServer.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SettingsORSServer.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
