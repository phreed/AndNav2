// Created by plusminus on 22:15:46 - 14.11.2008
package org.andnav2.sound;

public interface ISoundManager {

	public abstract void preloadSound(final int pResID);

	public abstract void preloadSounds(final int[] pResIDs);

	public abstract void releaseAll();

	/**
	 * @param pSoundResId like <code>R.raw.alarmsound</code>
	 */
	public abstract void playSound(final int pSoundResId);

	/**
	 * Plays sounds queued after each other.
	 * @param soundResIds multiple, like <code>R.raw.alarmsound</code>
	 */
	public abstract void playFollowUpSounds(final int ... soundResIds);
}