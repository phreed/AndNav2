// Created by plusminus on 22:13:10 - 28.09.2008
package org.andnav2.osm.views.tiles.caching;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Simple LRU cache for String-Bitmaps-Pairs. Implemented as an extended
 * <code>HashMap</code> with a maximum size and an aggregated <code>List</code>
 * as LRU queue.
 * @author Nicolas Gramlich
 *
 */
public class LRUCache<KEY, VALUE> extends HashMap<KEY, VALUE> {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final long serialVersionUID = 3345124753192560741L;

	// ===========================================================
	// Fields
	// ===========================================================

	/** Maximum cache size. */
	private final int maxCacheSize;
	/** LRU list. */
	private final LinkedList<KEY> list;

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * Constructs a new LRU cache instance.
	 * 
	 * @param maxCacheSize the maximum number of entries in this cache before entries are aged off.
	 */
	public LRUCache(final int maxCacheSize) {
		super(maxCacheSize);
		this.maxCacheSize = Math.max(0, maxCacheSize);
		this.list = new LinkedList<KEY>();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	/**
	 * Overrides clear() to also clear the LRU list.
	 */
	@Override
	public synchronized void clear() {
		super.clear();
		this.list.clear();
	}

	/**
	 * Overrides <code>put()</code> so that it also updates the LRU list.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the key
	 * @return previous value associated with key or <code>null</code> if there
	 *         was no mapping for key; a <code>null</code> return can also
	 *         indicate that the cache previously associated <code>null</code>
	 *         with the specified key
	 */
	@Override
	public synchronized VALUE put(final KEY key, final VALUE value) {
		if (this.maxCacheSize == 0){
			return null;
		}

		// if the key isn't in the cache and the cache is full...
		if (!super.containsKey(key) && !this.list.isEmpty() && this.list.size() + 1 > this.maxCacheSize) {
			final KEY deadKey = this.list.removeLast();
			super.remove(deadKey);
		}

		updateKey(key);
		return super.put(key, value);
	}

	/**
	 * Overrides <code>get()</code> so that it also updates the LRU list.
	 * 
	 * @param key
	 *            key with which the expected value is associated
	 * @return the value to which the cache maps the specified key, or
	 *         <code>null</code> if the map contains no mapping for this key
	 */
	@Override
	public VALUE get(final Object key) {
		final VALUE value = super.get(key);
		if (value != null) {
			updateKey(key);
		}
		return value;
	}

	@Override
	public synchronized VALUE remove(final Object key) {
		this.list.remove(key);
		return super.remove(key);
	}

	/**
	 * Moves the specified value to the top of the LRU list (the bottom of the
	 * list is where least recently used items live).
	 * 
	 * @param key of the value to move to the top of the list
	 */
	@SuppressWarnings("unchecked")
	private void updateKey(final Object key) {
		this.list.remove(key);
		this.list.addFirst((KEY) key);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
