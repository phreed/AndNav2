// Created by plusminus on 22:14:44 - 14.11.2008
package org.andnav2.sound;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;


public class MediaPlayerManager implements ISoundManager {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static ISoundManager mSoundManagerInstance;

	private final Context mCtx;

	private MediaPlayerPool mMediaPlayerPool;

	private final HashMap<Integer, Integer> mFollowUpSounds = new HashMap<Integer, Integer>();

	// ===========================================================
	// Constructors
	// ===========================================================

	public static ISoundManager getInstance(final Context ctx){
		if(mSoundManagerInstance == null) {
			mSoundManagerInstance = new MediaPlayerManager(ctx);
		}

		return mSoundManagerInstance;
	}

	private MediaPlayerManager(final Context ctx){
		this.mCtx = ctx;

		initMediaPlayerPool();
	}

	private void initMediaPlayerPool() {
		this.mMediaPlayerPool = new MediaPlayerPool();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	private Context getContext() {
		return this.mCtx;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void playSound(final int soundResId) {
		this.mMediaPlayerPool.get(soundResId).start();
	}

	public void playFollowUpSounds(final int ... soundResIds) {
		for(int i = 1; i < soundResIds.length; i++) {
			this.mFollowUpSounds.put(soundResIds[i-1], soundResIds[i]);
		}

		/* Start with the first, all others will be player as followups. */
		this.mMediaPlayerPool.get(soundResIds[0]).start();
	}

	public void preloadSound(final int resID) {
		// Nothing to do
	}

	public void preloadSounds(final int[] resIDs) {
		// Nothing to do
	}

	public void releaseAll() {
		this.mMediaPlayerPool.releaseAll();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected void completed(final int soundResId) {
		final Integer followUpSound = this.mFollowUpSounds.remove(soundResId);
		if(followUpSound != null) {
			playSound(followUpSound);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class MediaPlayerPool{
		private static final int DESIRED_PLAYERS_READY = 1;

		private final HashMap<Integer, ArrayList<MediaPlayer>> mPools = new HashMap<Integer, ArrayList<MediaPlayer>>();

		public synchronized MediaPlayer get(final int soundResId){
			final ArrayList<MediaPlayer> existingPool = this.mPools.get(soundResId);
			if(existingPool != null && existingPool.size() > 0){
				/* If there is one found, get it by removing it from the pool.
				 * At this point, the player already got the completion listener set,
				 * which makes it return to the pool. */
				return existingPool.remove(0);
			}else{
				/* Create a new MediaPlayer, which later will return to the pool. */
				final MediaPlayer mp = MediaPlayer.create(getContext(), soundResId);
				mp.setOnCompletionListener(new OnCompletionListener(){
					public void onCompletion(final MediaPlayer mp) {
						mp.seekTo(0);
						returnToPool(soundResId, mp);
						completed(soundResId);
					}
				});
				return mp;
			}
		}

		private synchronized void returnToPool(final int soundResId, final MediaPlayer mp) {
			final ArrayList<MediaPlayer> existingPool = this.mPools.get(soundResId);
			if(existingPool != null){
				if(existingPool.size() < DESIRED_PLAYERS_READY){
					/* Add the returning MediaPlayer to existing Pool. */
					existingPool.add(mp);
				}else{
					mp.release();
				}
			}else{
				/* Create new ArrayList an add the returning MediaPlayer to it. */
				final ArrayList<MediaPlayer> newPool = new ArrayList<MediaPlayer>(2);
				newPool.add(mp);
				this.mPools.put(soundResId, newPool);
			}
		}

		public synchronized void releaseAll() {
			for(final ArrayList<MediaPlayer> pool : this.mPools.values()){
				for(final MediaPlayer p : pool){
					p.release();
				}
				pool.clear();
			}
			this.mPools.clear();
		}
	}
}
