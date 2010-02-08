/**
 * 
 */
package org.andnav2.ui.common.adapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.andnav2.R;
import org.andnav2.sys.ors.adt.ORSServer;
import org.andnav2.sys.ors.adt.ORSServer.ServerStatus;
import org.andnav2.util.constants.Constants;
import org.andnav2.util.ping.PingResult;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Nicolas Gramlich
 * @since 15:09:22 - 17.07.2009
 */
public class ORSServerListAdapter extends BaseAdapter implements Constants {

	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	/** Remember our context so we can use it when constructing views. */
	private final Context mContext;
	private List<ORSServerItem> mItems = new ArrayList<ORSServerItem>();

	// ===========================================================
	// Constructors
	// ===========================================================

	public ORSServerListAdapter(final Context context) {
		this.mContext = context;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void addItem(final ORSServerItem it) {
		this.mItems.add(it);
		Collections.sort(this.mItems);
	}

	public void setListItems(final List<ORSServerItem> lit) {
		this.mItems = lit;
		Collections.sort(this.mItems);
	}

	@Override
	public boolean isEmpty() {
		return this.mItems == null || this.mItems.size() == 0;
	}

	/** @return The number of items in the */
	public int getCount() { return this.mItems.size(); }

	public Object getItem(final int position) { return this.mItems.get(position); }

	public long getItemId(final int position) { return position; }

	// ===========================================================
	// Methods
	// ===========================================================

	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final View v;
		if (convertView != null){
			v = convertView;
		}else{
			final LayoutInflater inflater = LayoutInflater.from(this.mContext);
			v = inflater.inflate(R.layout.settings_orsserver_listrow, null);
		}

		// Reuse/Overwrite the View passed
		final ORSServerItem serverItem = this.mItems.get(position);

		/* Coverage-Flag. */
		((ImageView)v.findViewById(R.id.iv_settings_orsserver_listrow_coverageflag))
		.setImageResource(serverItem.mORSServer.COVERAGE.FLAGRESID);

		/* Name. */
		((TextView)v.findViewById(R.id.tv_settings_orsserver_listrow_name))
		.setText(serverItem.mORSServer.SERVERNAME);

		/* Location-Flag. */
		((ImageView)v.findViewById(R.id.iv_settings_orsserver_listrow_locationflag))
		.setImageResource(serverItem.mORSServer.LOCATION.FLAGRESID);

		/* LocationName. */
		((TextView)v.findViewById(R.id.tv_settings_orsserver_listrow_location))
		.setText(serverItem.mORSServer.LOCATIONNAME);

		/* ServerStatus. */
		final ImageView serverStatusImage = (ImageView)v.findViewById(R.id.iv_settings_orsserver_listrow_status);
		final TextView serverStatusText = (TextView)v.findViewById(R.id.tv_settings_orsserver_listrow_status);
		final TextView serverLatencyText = (TextView)v.findViewById(R.id.tv_settings_orsserver_listrow_latency);
		switch(serverItem.mStatus){
			case ONLINE:
				serverStatusImage.setImageResource(R.drawable.status_orsserver_green);
				serverStatusText.setText(R.string.online);
				serverLatencyText.setText(serverItem.mLatencyMS + " ms");
				break;
			case OFFLINE:
				serverStatusImage.setImageResource(R.drawable.status_orsserver_red);
				serverStatusText.setText(R.string.offline);
				serverLatencyText.setText(R.string.unknown);
				break;
			case UNKNOWN:
				serverStatusImage.setImageResource(R.drawable.status_orsserver_unknown);
				serverStatusText.setText(R.string.unknown);
				serverLatencyText.setText(R.string.unknown);
				break;
		}

		return v;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static class ORSServerItem implements Comparable<ORSServerItem>, Parcelable {
		public final ORSServer mORSServer;
		protected ORSServer.ServerStatus mStatus = ServerStatus.UNKNOWN;
		protected int mLatencyMS = NOT_SET;

		public ORSServerItem(final ORSServer pORServer) {
			this.mORSServer = pORServer;
		}

		public int compareTo(final ORSServerItem another) {
			return this.mORSServer.SERVERNAME.compareToIgnoreCase(another.mORSServer.SERVERNAME);
		}

		public void updatePingInformation(final Runnable callback) {
			this.mLatencyMS = NOT_SET;
			this.mStatus = ServerStatus.UNKNOWN;
			new Thread(new Runnable(){
				public void run() {
					try {
						final PingResult pr = ORSServerItem.this.mORSServer.ping();
						if(pr.mReachable){
							ORSServerItem.this.mStatus = ServerStatus.ONLINE;
							ORSServerItem.this.mLatencyMS = pr.mLatency;
						}else{
							ORSServerItem.this.mStatus = ServerStatus.OFFLINE;
							ORSServerItem.this.mLatencyMS = NOT_SET;
						}
					} catch (final IOException e) {
						ORSServerItem.this.mStatus = ServerStatus.OFFLINE;
						ORSServerItem.this.mLatencyMS = NOT_SET;
					}
					callback.run();
				}
			}).start();
		}

		// ===========================================================
		// Parcelable
		// ===========================================================

		public static final Parcelable.Creator<ORSServerItem> CREATOR = new Parcelable.Creator<ORSServerItem>() {
			public ORSServerItem createFromParcel(final Parcel in) {
				return readFromParcel(in);
			}

			public ORSServerItem[] newArray(final int size) {
				return new ORSServerItem[size];
			}
		};

		public int describeContents() {
			return 0;
		}

		public void writeToParcel(final Parcel out, final int flags) {
			out.writeParcelable(this.mORSServer, 0);
		}

		private static ORSServerItem readFromParcel(final Parcel in){
			final ORSServer server = in.readParcelable(null);

			return new ORSServerItem(server);
		}
	}
}