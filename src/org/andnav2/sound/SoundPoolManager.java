// Created by plusminus on 22:39:36 - 10.11.2008
package org.andnav2.sound;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


public class SoundPoolManager implements ISoundManager {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static ISoundManager mSoundManagerInstance;

	private final Context mCtx;
	private AudioManager mAudioManager;
	private SoundPool mSoundPool;
	private HashMap<Integer, Integer> mSoundPoolMap;

	// ===========================================================
	// Constructors
	// ===========================================================

	public static ISoundManager getInstance(final Context ctx){
		if(mSoundManagerInstance == null) {
			mSoundManagerInstance = new SoundPoolManager(ctx);
		}

		return mSoundManagerInstance;
	}

	private SoundPoolManager(final Context ctx){
		this.mCtx = ctx;

		initSoundPool();
	}

	private void initSoundPool() {
		this.mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		this.mSoundPoolMap = new HashMap<Integer, Integer>();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	private Context getContext() {
		return this.mCtx;
	}

	private AudioManager getAudioManager(){
		if(this.mAudioManager == null) {
			this.mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
		}

		return this.mAudioManager;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void preloadSound(final int resID) {
		if(this.mSoundPoolMap.containsKey(resID)) {
			return;
		}

		this.mSoundPoolMap.put(resID, this.mSoundPool.load(getContext(), resID, 1));
	}

	public void preloadSounds(final int[] pResIDs){
		for (final int r : pResIDs) {
			preloadSound(r);
		}
	}

	public void playSound(final int pSoundResId) {
		if(!this.mSoundPoolMap.containsKey(pSoundResId)) {
			this.mSoundPoolMap.put(pSoundResId, this.mSoundPool.load(getContext(), pSoundResId, 1));
		}

		final int streamVolume = getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC);
		this.mSoundPool.play(this.mSoundPoolMap.get(pSoundResId), streamVolume, streamVolume, 1, 0, 1f);
	}

	public void playFollowUpSounds(final int ... soundResIds) {
		throw new UnsupportedOperationException("Not yet supported method");
	}

	public void releaseAll() {
		for(final int i : this.mSoundPoolMap.values()) {
			this.mSoundPool.unload(i);
		}

		this.mSoundPoolMap.clear();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
