package org.andnav2.ui.map.overlay.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Indicates whether an entity should be visible or not.
 * @author phreed
 *
 */
public class VisibilityFilter {

	private List<Integer> mFilterSet = new ArrayList<Integer>();
	
	public void set(int location, Integer filter) {
		// TODO Auto-generated method stub
		this.mFilterSet.set(location, filter);
	}

	public boolean get(int location) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clear() {
		// TODO Auto-generated method stub
		this.mFilterSet.clear();
	}

}
