// Created by plusminus on 14:51:34 - 02.11.2008
package org.andnav2.ui.sd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.andnav2.R;
import org.andnav2.exc.Exceptor;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.common.views.FastScrollView;
import org.andnav2.util.UserTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.provider.Contacts;
import android.provider.Contacts.ContactMethodsColumns;
import android.provider.Contacts.People;
import android.provider.Contacts.PeopleColumns;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class SDContacts extends AndNavBaseActivity{
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int REQUESTCODE_DDMAP = 0x1337;

	private static final String STATE_CONTACTSWITHADDRESSES_ITEMS_ID = "state_contactswithaddresses_items_id";

	// ===========================================================
	// Fields
	// ===========================================================

	private Bundle bundleCreatedWith;
	private ListView mContactsList;

	private ArrayList<ContactItem> mContactsWithAddress = new ArrayList<ContactItem>();

	private boolean mContactsWithAddressInitFinished = false;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.sd_contacts);

		this.bundleCreatedWith = this.getIntent().getExtras();
		this.mContactsList = (ListView) this.findViewById(R.id.list_contacts);

		final TextView empty = new TextView(this);
		empty.setText(R.string.list_empty);
		this.mContactsList.setEmptyView(empty);

		initListView();

		this.applyTopMenuButtonListeners();

		if(savedInstanceState == null) {
			updateContactListItems();
		}
	}

	private void updateContactListItems() {
		final ContactListAdapter cla = new ContactListAdapter(this);

		final ProgressDialog pd = ProgressDialog.show(this, getString(R.string.sd_contacts_loading_title), getString(R.string.please_wait_a_moment), false); // TODO Make determinate, when SDK supports this.

		final String progressBaseString = getString(R.string.sd_contacts_loading_progress);

		new UserTask<Void, Integer, Void>(){
			@Override
			public Void doInBackground(final Void... params) {
				try{
					final Map<Integer, List<ContactItemToResolve>> unresolvedPersonsAddresses = new HashMap<Integer, List<ContactItemToResolve>>();

					/* Get a cursor on all Contacts.KIND_POSTAL entries. */
					final Cursor addrCursor = managedQuery(
							android.provider.Contacts.ContactMethods.CONTENT_URI, null,
							ContactMethodsColumns.KIND + "=" + Contacts.KIND_POSTAL, null,
							null);
					if(addrCursor == null){
						runOnUiThread(new Runnable(){
							public void run() {
								Toast.makeText(SDContacts.this, "Problem loading contacts. Please retry.", Toast.LENGTH_LONG).show(); // TODO i18n
							}
						});
					}else{
						final int colData = addrCursor.getColumnIndexOrThrow(ContactMethodsColumns.DATA);
						final int colKind = addrCursor.getColumnIndexOrThrow(ContactMethodsColumns.KIND);
						final int colType = addrCursor.getColumnIndexOrThrow(android.provider.Contacts.ContactMethodsColumns.TYPE);
						final int colPersonID = addrCursor.getColumnIndexOrThrow(android.provider.Contacts.ContactMethods.PERSON_ID);

						/* Loop through all results and remember the personID and the textual address. */
						while(addrCursor.moveToNext()){
							final int kind = addrCursor.getInt(colKind);
							if(kind == Contacts.KIND_POSTAL){
								final String typeAppendix;
								switch(addrCursor.getInt(colType)){
									case android.provider.Contacts.ContactMethodsColumns.TYPE_HOME:
										typeAppendix = " (" + getString(R.string.sd_contacts_address_type_home) + ")";
										break;
									case android.provider.Contacts.ContactMethodsColumns.TYPE_WORK:
										typeAppendix = " (" + getString(R.string.sd_contacts_address_type_work) + ")";
										break;
									default:
										typeAppendix = "";
										break;
								}
								String address = addrCursor.getString(colData);
								if(address != null){
									address = address.replace('\n', ' ');
									final int personID = addrCursor.getInt(colPersonID);

									final ContactItemToResolve newContactItem = new ContactItemToResolve(address, personID, typeAppendix);
									/* Maybe the personId existed,
									 * so we simply add it to the existing list,
									 * instead of adding a new list. */
									final List<ContactItemToResolve> existing = unresolvedPersonsAddresses.get(personID);
									if(existing == null){
										final List<ContactItemToResolve> newList = new ArrayList<ContactItemToResolve>();
										newList.add(newContactItem);
										unresolvedPersonsAddresses.put(personID, newList);
									}else{
										existing.add(newContactItem);
									}
								}
							}
						}

						final int contactsToResolveCount = unresolvedPersonsAddresses.size();

						final StringBuilder selectionBuilder = new StringBuilder();
						/* Build the 'OR'-ed query. */
						final String peopleTable = People.CONTENT_URI.getLastPathSegment();
						for(final Iterator<Integer> iterator = unresolvedPersonsAddresses.keySet().iterator(); iterator.hasNext(); /* none */){
							final int personID = iterator.next();

							selectionBuilder
							.append(peopleTable)
							.append('.')
							.append(BaseColumns._ID)
							.append("=")
							.append(personID);

							if(iterator.hasNext()) {
								selectionBuilder.append(" OR ");
							}
						}

						/* Open a Cursor for each person-ID that has an address to look up the contact-name behind that ID. */
						final Cursor cur = managedQuery(People.CONTENT_URI, new String[]{PeopleColumns.NAME, BaseColumns._ID}, selectionBuilder.toString(), null, null );

						int i = 0;
						if(cur != null && cur.moveToFirst()){

							/* Resolve columnIndices first. */
							final int colName = cur.getColumnIndexOrThrow(PeopleColumns.NAME);
							final int colID = cur.getColumnIndexOrThrow(BaseColumns._ID);

							do{
								publishProgress(i++, contactsToResolveCount);

								final int id = cur.getInt(colID);
								final List<ContactItemToResolve> cts = unresolvedPersonsAddresses.get(id);

								if(cts != null){
									for(final ContactItemToResolve ct : cts){
										final String name = cur.getString(colName) + ct.mAddressTypeAppendix;
										SDContacts.this.mContactsWithAddress.add(new ContactItem(name, ct.mAddressDescription));
									}
								}
							}while(cur.moveToNext());
						}
					}

					/* Adapt the list to the Adapter. */
					cla.setListItems(SDContacts.this.mContactsWithAddress);/* Orders by name, ascending. */
					SDContacts.this.mContactsWithAddressInitFinished = true;
				}catch(final Exception e){
					Exceptor.e("Contacts-Exception", e, SDContacts.this);
				}
				return null;
			}

			@Override
			public void onProgressUpdate(final Integer... progress) {
				pd.setMessage(String.format(progressBaseString, (int)(100*((float)progress[0] / progress[1])), progress[0], progress[1]));
			}

			@Override
			public void onPostExecute(final Void result) {
				/* Adapt the Adapter to the ListView. */
				SDContacts.this.mContactsList.setAdapter(cla);
				try{
					pd.dismiss();
				}catch(final IllegalArgumentException ia){
					// Nothing
				}
			}
		}.execute();
	}

	protected void initListView() {
		this.mContactsList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
				final ContactItem c = (ContactItem)parent.getAdapter().getItem(position);

				final String addr = c.mAddressDescription;

				final Intent resolverIntent = new Intent(SDContacts.this, SDResolver.class);

				SDContacts.this.bundleCreatedWith.putInt(EXTRAS_MODE, EXTRAS_MODE_FREEFORMSEARCH);

				SDContacts.this.bundleCreatedWith.putString(EXTRAS_FREEFORM_ID, addr);

				resolverIntent.putExtras(SDContacts.this.bundleCreatedWith);
				SDContacts.this.startActivityForResult(resolverIntent, REQUESTCODE_DDMAP);
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
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		final char c;
		switch(keyCode){
			case KeyEvent.KEYCODE_A: c = 'a'; break;
			case KeyEvent.KEYCODE_B: c = 'b'; break;
			case KeyEvent.KEYCODE_C: c = 'c'; break;
			case KeyEvent.KEYCODE_D: c = 'd'; break;
			case KeyEvent.KEYCODE_E: c = 'e'; break;
			case KeyEvent.KEYCODE_F: c = 'f'; break;
			case KeyEvent.KEYCODE_G: c = 'g'; break;
			case KeyEvent.KEYCODE_H: c = 'h'; break;
			case KeyEvent.KEYCODE_I: c = 'i'; break;
			case KeyEvent.KEYCODE_J: c = 'j'; break;
			case KeyEvent.KEYCODE_K: c = 'k'; break;
			case KeyEvent.KEYCODE_L: c = 'l'; break;
			case KeyEvent.KEYCODE_M: c = 'n'; break;
			case KeyEvent.KEYCODE_N: c = 'm'; break;
			case KeyEvent.KEYCODE_O: c = 'o'; break;
			case KeyEvent.KEYCODE_P: c = 'p'; break;
			case KeyEvent.KEYCODE_Q: c = 'q'; break;
			case KeyEvent.KEYCODE_R: c = 'r'; break;
			case KeyEvent.KEYCODE_S: c = 's'; break;
			case KeyEvent.KEYCODE_T: c = 't'; break;
			case KeyEvent.KEYCODE_U: c = 'u'; break;
			case KeyEvent.KEYCODE_V: c = 'v'; break;
			case KeyEvent.KEYCODE_W: c = 'w'; break;
			case KeyEvent.KEYCODE_X: c = 'x'; break;
			case KeyEvent.KEYCODE_Y: c = 'y'; break;
			case KeyEvent.KEYCODE_Z: c = 'z'; break;
			default:
				return super.onKeyDown(keyCode, event);
		}
		int position = Collections.binarySearch(this.mContactsWithAddress, new ContactItem(String.valueOf(c), ""));

		if(position < 0){
			/* Negative result means the insertion-point.
			 * See definition of Collections.binarySearch */
			position = -(position + 1);
		}

		this.mContactsList.setSelectionFromTop(position, 0);
		return true;
	}

	@Override
	public void onSaveInstanceState(final Bundle out) {
		if(this.mContactsWithAddressInitFinished) {
			out.putParcelableArrayList(STATE_CONTACTSWITHADDRESSES_ITEMS_ID, this.mContactsWithAddress);
		}
	}

	@Override
	protected void onRestoreInstanceState(final Bundle in) {
		final ArrayList<ContactItem> restoredItems = in.getParcelableArrayList(STATE_CONTACTSWITHADDRESSES_ITEMS_ID);
		if(this.mContactsWithAddress == null){
			updateContactListItems();
		}else{
			this.mContactsWithAddress = restoredItems;
			final ContactListAdapter cla = new ContactListAdapter(this);
			cla.setListItems(this.mContactsWithAddress);
			this.mContactsList.setAdapter(cla);
			this.mContactsWithAddressInitFinished = true;
		}
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
		/* Set Listener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_contacts_back)) {
			@Override
			public void onClicked(final View me) {
				if (SDContacts.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDContacts.this, R.raw.close).start();
				}

				/* Back one level. */
				SDContacts.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDContacts.this.finish();
			}
		};

		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_contacts_close)) {
			@Override
			public void onClicked(final View me) {
				if (SDContacts.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDContacts.this, R.raw.close).start();
				}
				/*
				 * Set ResultCode that the calling activity knows that we want
				 * to go back to the Base-Menu
				 */
				SDContacts.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDContacts.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static class ContactItemToResolve{
		protected final String mAddressDescription;
		protected final String mAddressTypeAppendix;

		private ContactItemToResolve(final String addressDescription, final int personID, final String addressTypeAppendix) {
			this.mAddressDescription = addressDescription;
			this.mAddressTypeAppendix = addressTypeAppendix;
		}
	}

	static class ContactItem implements Comparable<ContactItem>, Parcelable{
		protected final String mName;
		protected final String mAddressDescription;

		private ContactItem(final String pName, final String pAddressDescrption) {
			this.mName = pName;
			this.mAddressDescription = pAddressDescrption;
		}

		public int compareTo(final ContactItem another) {
			return this.mName.compareToIgnoreCase(another.mName);
		}

		// ===========================================================
		// Parcelable
		// ===========================================================

		public static final Parcelable.Creator<ContactItem> CREATOR = new Parcelable.Creator<ContactItem>() {
			public ContactItem createFromParcel(final Parcel in) {
				return readFromParcel(in);
			}

			public ContactItem[] newArray(final int size) {
				return new ContactItem[size];
			}
		};

		public int describeContents() {
			return 0;
		}

		public void writeToParcel(final Parcel out, final int flags) {
			out.writeString(this.mName);
			out.writeString(this.mAddressDescription);
		}

		private static ContactItem readFromParcel(final Parcel in){
			final String name = in.readString();
			final String addressDescription = in.readString();

			return new ContactItem(name, addressDescription);
		}
	}

	private class POIListItemView extends LinearLayout{

		private final TextView mTVAddress;
		private final TextView mTVName;

		public POIListItemView(final Context context, final ContactItem aPOIItem) {
			super(context);

			this.setOrientation(VERTICAL);

			this.mTVName = new TextView(context);
			this.mTVName.setText(aPOIItem.mName);
			this.mTVName.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
			this.mTVName.setPadding(10,0,20,0);

			addView(this.mTVName, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));


			this.mTVAddress = new TextView(context);
			this.mTVAddress.setText(aPOIItem.mAddressDescription);
			this.mTVAddress.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12);

			addView(this.mTVAddress, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		}

	}

	class ContactListAdapter extends BaseAdapter implements FastScrollView.SectionIndexer{

		/** Remember our context so we can use it when constructing views. */
		private final Context mContext;

		private List<ContactItem> mItems = new ArrayList<ContactItem>();

		private String[] mAlphabet;

		public ContactListAdapter(final Context context) {
			this.mContext = context;
			initAlphabet(context);
		}

		public void addItem(final ContactItem it) {
			this.mItems.add(it);
			Collections.sort(this.mItems);
		}

		public void setListItems(final List<ContactItem> lit) {
			this.mItems = lit;
			Collections.sort(this.mItems);
		}

		/** @return The number of items in the */
		public int getCount() { return this.mItems.size(); }

		public Object getItem(final int position) { return this.mItems.get(position); }

		public long getItemId(final int position) { return position; }

		public View getView(final int position, final View convertView, final ViewGroup parent) {
			POIListItemView btv;
			if (convertView == null) {
				btv = new POIListItemView(this.mContext, this.mItems.get(position));
			} else { // Reuse/Overwrite the View passed
				// We are assuming(!) that it is castable!
				btv = (POIListItemView) convertView;
				btv.mTVAddress.setText(this.mItems.get(position).mAddressDescription);
				btv.mTVName.setText( this.mItems.get(position).mName);
			}
			return btv;
		}

		// ===========================================================
		// FastScrollView-Methods
		// ===========================================================

		public int getPositionForSection(final int section) {

			final String firstChar = this.mAlphabet[section];

			/* Find the index, of the firstchar within the Contact-Items */
			int position = Collections.binarySearch(this.mItems, new ContactItem(firstChar, null));

			if(position < 0){
				/* Negative result means the insertion-point.
				 * See definition of Collections.binarySearch */
				position = -(position + 1);
			}

			return position;
		}

		public int getSectionForPosition(final int position) {
			return 0;
		}

		public Object[] getSections() {
			return this.mAlphabet;
		}

		private void initAlphabet(final Context context) {
			final String alphabetString = context.getResources().getString(R.string.alphabet); // TODO Use Systems Alphabet!
			this.mAlphabet = new String[alphabetString.length()];

			for (int i = 0; i < this.mAlphabet.length; i++) {
				this.mAlphabet[i] = String.valueOf(alphabetString.charAt(i));
			}
		}
	}
}