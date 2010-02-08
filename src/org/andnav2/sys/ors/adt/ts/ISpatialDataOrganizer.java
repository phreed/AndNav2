// Created by plusminus on 6:25:54 PM - Mar 27, 2009
package org.andnav2.sys.ors.adt.ts;

import java.util.Collection;
import java.util.List;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.adt.IGeoPoint;

/**
 * 
 * @author Nicolas Gramlich
 *
 * @param <T>
 */
public interface ISpatialDataOrganizer<T extends IGeoPoint> {
	public abstract List<T> getItems();

	public abstract void add(final T pItem);

	public abstract void addAll(final Collection<T> pItems);

	public abstract void buildIndex();

	public abstract GetMode getGetMode();

	public abstract List<T> getClosest(final IGeoPoint pGeoPoint, final int pCount);
	public abstract List<T> getWithinBoundingBox(final BoundingBoxE6 pBoundingBoxE6, final int pCount);

	public void clearIndex();

	public abstract boolean isIndexBuilt();

	public static enum GetMode{
		CLOSEST, BOUNDINGBOX;
	}
}