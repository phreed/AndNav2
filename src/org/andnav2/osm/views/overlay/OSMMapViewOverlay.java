// Created by plusminus on 20:32:01 - 27.09.2008
package org.andnav2.osm.views.overlay;

import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.osm.views.OSMMapView;

import android.graphics.Canvas;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;


/**
 * Base class representing an overlay which may be displayed on top of a {@link OSMMapView}. To add an overlay, subclass this class, create an instance, and add it to the list obtained from getOverlays() of {@link OSMMapView}.
 * @author Nicolas Gramlich
 */
public abstract class OSMMapViewOverlay implements OSMConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean mVisible = true;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setVisible(final boolean pVisible){
		this.mVisible = pVisible;
	}

	public boolean isVisible(){
		return this.mVisible;
	}

	// ===========================================================
	// Methods for SuperClass/Interfaces
	// ===========================================================

	/**
	 * Managed Draw calls gives Overlays the possibility to first draw manually and after that do a final draw. This is very useful, i sth. to be drawn needs to be <b>topmost</b>.
	 */
	public void onManagedDraw(final Canvas c, final OSMMapView osmv){
		try{
			if(this.mVisible){
				onDraw(c, osmv);
				onDrawFinished(c, osmv);
			}
		}catch(final Throwable t){
			Log.e(DEBUGTAG, "Error in onManagedDraw();", t);
		}
	}

	protected abstract void onDraw(final Canvas c, final OSMMapView osmv);

	protected abstract void onDrawFinished(final Canvas c, final OSMMapView osmv);

	public abstract void release();

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * By default does nothing (<code>return false</code>). If you handled the Event, return <code>true</code>, otherwise return <code>false</code>.
	 * If you returned <code>true</code> none of the following Overlays or the underlying {@link OSMMapView} has the chance to handle this event.
	 */
	public boolean onKeyDown(final int keyCode, final KeyEvent event, final OSMMapView mapView) {
		return false;
	}

	/**
	 * By default does nothing (<code>return false</code>). If you handled the Event, return <code>true</code>, otherwise return <code>false</code>.
	 * If you returned <code>true</code> none of the following Overlays or the underlying {@link OSMMapView} has the chance to handle this event.
	 */
	public boolean onKeyUp(final int keyCode, final KeyEvent event, final OSMMapView mapView) {
		return false;
	}

	/**
	 * <b>You can prevent all(!) other Touch-related events from happening!</b><br />
	 * By default does nothing (<code>return false</code>). If you handled the Event, return <code>true</code>, otherwise return <code>false</code>.
	 * If you returned <code>true</code> none of the following Overlays or the underlying {@link OSMMapView} has the chance to handle this event.
	 */
	public boolean onTouchEvent(final MotionEvent event, final OSMMapView mapView) {
		return false;
	}

	/**
	 * By default does nothing (<code>return false</code>). If you handled the Event, return <code>true</code>, otherwise return <code>false</code>.
	 * If you returned <code>true</code> none of the following Overlays or the underlying {@link OSMMapView} has the chance to handle this event.
	 */
	public boolean onTrackballEvent(final MotionEvent event, final OSMMapView mapView) {
		return false;
	}

	/**
	 * By default does nothing (<code>return false</code>). If you handled the Event, return <code>true</code>, otherwise return <code>false</code>.
	 * If you returned <code>true</code> none of the following Overlays or the underlying {@link OSMMapView} has the chance to handle this event.
	 */
	public boolean onSingleTapUp(final MotionEvent e, final OSMMapView openStreetMapView) {
		return false;
	}

	/**
	 * By default does nothing (<code>return false</code>). If you handled the Event, return <code>true</code>, otherwise return <code>false</code>.
	 * If you returned <code>true</code> none of the following Overlays or the underlying {@link OSMMapView} has the chance to handle this event.
	 */
	public boolean onLongPress(final MotionEvent e, final OSMMapView openStreetMapView) {
		return false;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
