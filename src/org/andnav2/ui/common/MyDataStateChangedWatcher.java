//Created by plusminus on 13:23:23 - 18.05.2008
package org.andnav2.ui.common;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;


public class MyDataStateChangedWatcher extends PhoneStateListener{
	// ===========================================================
	// Final Fields
	// ===========================================================

	protected static final int CONNECTIONSTATE_WHAT_ID = 0x1337;

	// ===========================================================
	// Fields
	// ===========================================================

	final DataStateChangedListener mLis;
	final Context mCtx;
	final TelephonyManager mTelephonyManager;

	// ===========================================================
	// Constructors
	// ===========================================================

	public MyDataStateChangedWatcher(final Context ctx, final DataStateChangedListener lis){
		this.mLis = lis;
		this.mCtx = ctx;

		this.mTelephonyManager = (TelephonyManager)this.mCtx.getSystemService(Context.TELEPHONY_SERVICE);
		assert(this.mTelephonyManager != null);
		this.mTelephonyManager.listen(this, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onDataConnectionStateChanged(final int state) {
		if(this.mLis != null){
			final int strength;
			switch(state){
				case TelephonyManager.DATA_CONNECTED:
					strength = 5;
					break;
				case TelephonyManager.DATA_CONNECTING:
					strength = 1;
					break;
				case TelephonyManager.DATA_DISCONNECTED:
				case TelephonyManager.DATA_SUSPENDED:
				default:
					strength = 0;
			}
			this.mLis.onDataStateChanged(strength);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void unregister() {
		assert(this.mTelephonyManager != null);
		this.mTelephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
