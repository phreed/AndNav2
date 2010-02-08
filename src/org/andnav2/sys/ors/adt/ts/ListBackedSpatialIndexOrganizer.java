// Created by plusminus on 6:15:04 PM - Mar 27, 2009
package org.andnav2.sys.ors.adt.ts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.adt.IGeoPoint;
import org.andnav2.util.constants.Constants;

import android.util.Log;

import com.att.research.rtree.RTree;
import com.att.research.spatialindex.IData;
import com.att.research.spatialindex.INode;
import com.att.research.spatialindex.ISpatialIndex;
import com.att.research.spatialindex.IVisitor;
import com.att.research.spatialindex.Point;
import com.att.research.storagemanager.MemoryStorageManager;
import com.att.research.storagemanager.PropertySet;

/**
 * 
 * @author Nicolas Gramlich
 *
 * @param <T>
 */
public class ListBackedSpatialIndexOrganizer<T extends IGeoPoint> implements ISpatialDataOrganizer<T> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean mIndexBuilt = false;

	private final ISpatialIndex mSpatialIndex;

	protected final List<T> mFeatureList = new ArrayList<T>();

	// ===========================================================
	// Constructors
	// ===========================================================

	public ListBackedSpatialIndexOrganizer() {
		final MemoryStorageManager sm = new MemoryStorageManager();
		final PropertySet ps = new PropertySet();

		ps.setProperty("FillFactor", 0.7d);

		ps.setProperty("IndexCapacity", 20);
		ps.setProperty("LeafCapacity", 20);

		ps.setProperty("Dimension", 2);

		this.mSpatialIndex = new RTree(ps, sm);
	}

	public ListBackedSpatialIndexOrganizer(final List<T> pItems) {
		this();
		addAll(pItems);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean isIndexBuilt() {
		return this.mIndexBuilt;
	}

	public List<T> getItems() {
		return this.mFeatureList;
	}

	public void add(final T pItem) {
		if(this.mIndexBuilt) {
			throw new IllegalStateException("Trying to add after index was built.");
		}

		if(pItem != null) {
			this.mFeatureList.add(pItem);
		}
	}

	public void addAll(final Collection<T> pItems){
		if(this.mIndexBuilt) {
			throw new IllegalStateException("Trying to add after index was built.");
		}

		if(pItems != null) {
			this.mFeatureList.addAll(pItems);
		}
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void clearIndex(){
		this.mIndexBuilt = false;
		this.mFeatureList.clear();
	}

	public void buildIndex(){
		this.mIndexBuilt = true;

		final double[] coords = new double[2];
		for(int i = 0; i < this.mFeatureList.size(); i++){
			Log.d(Constants.DEBUGTAG, "Inserting: " + i);
			final T ti = this.mFeatureList.get(i);

			coords[0] = ti.getLatitudeAsDouble();
			coords[1] = ti.getLongitudeAsDouble();
			this.mSpatialIndex.insertData(null, new Point(coords), i);
		}
	}

	public List<T> getClosest(final IGeoPoint pGeoPoint, final int pCount){
		if(!this.mIndexBuilt) {
			throw new IllegalStateException("Trying to query before index was built.");
		}

		final List<T> out = new ArrayList<T>();

		final double[] pointCoords = new double[]{pGeoPoint.getLatitudeAsDouble(), pGeoPoint.getLongitudeAsDouble()};
		this.mSpatialIndex.nearestNeighborQuery(pCount, new Point(pointCoords ), new IVisitor(){

			public void visitData(final IData d) {
				out.add(ListBackedSpatialIndexOrganizer.this.mFeatureList.get(d.getIdentifier()));
			}

			public void visitNode(final INode n) { }

		});

		return out;
	}

	public List<T> getWithinBoundingBox(final BoundingBoxE6 boundingBoxE6, final int count) {
		throw new IllegalStateException("Wrong method!");
	}

	public GetMode getGetMode() {
		return GetMode.CLOSEST;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
