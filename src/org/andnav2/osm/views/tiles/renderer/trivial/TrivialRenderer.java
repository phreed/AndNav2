// Created by plusminus on 13:54:29 - 08.02.2009
package org.andnav2.osm.views.tiles.renderer.trivial;

import java.util.List;

import org.andnav2.db.DataBaseException;
import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.osm.views.tiles.adt.OSMTileInfo;
import org.andnav2.osm.views.tiles.renderer.IOSMRenderer;
import org.andnav2.osm.views.tiles.renderer.db.OSMDatabaseManager;
import org.andnav2.osm.views.tiles.renderer.db.adt.OSMNode;
import org.andnav2.osm.views.tiles.renderer.db.adt.OSMWay;
import org.andnav2.osm.views.tiles.renderer.db.adt.constants.OSMWayConstants;
import org.andnav2.osm.views.util.Util;
import org.andnav2.ui.map.overlay.util.ManagedLinePath;
import org.andnav2.util.constants.Constants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathMeasure;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;


public class TrivialRenderer implements IOSMRenderer, Constants, OSMConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final float BOUNDINGBOX_PADDING_RELATIVE = 1.3f; // 30% padding of the boundingboxes

	// ===========================================================
	// Fields
	// ===========================================================

	private final Context mCtx;
	private final Paint mNodePaint = new Paint();
	private final Paint mTextPaint = new Paint();
	private final Paint mBackgroundPaint = new Paint();

	private final Paint mHighwayMotorwayPaint = new Paint();
	private final Paint mHighwayFootwayPaint = new Paint();
	private final Paint mHighwayMotorwayLinkPaint = new Paint();
	private final Paint mHighwayPrimaryPaint = new Paint();
	private final Paint mHighwayPrimaryLinkPaint = new Paint();
	private final Paint mHighwayResidentialPaint = new Paint();
	private final Paint mHighwaySecondaryPaint = new Paint();
	private final Paint mHighwayStepsPaint = new Paint();
	private final Paint mHighwayUnclassifiedPaint = new Paint();
	private final Paint mHighwayCyclewayPaint = new Paint();
	private final Paint mHighwayServicePaint = new Paint();

	// ===========================================================
	// Constructors
	// ===========================================================

	public TrivialRenderer(final Context ctx){
		this.mCtx = ctx;

		{ /* Initiate paints. */
			initPaints();
		}
	}

	private void initPaints(){
		initWayPaints();

		this.mNodePaint.setColor(Color.rgb(142,142,254));
		this.mNodePaint.setTextSize(14);
		this.mNodePaint.setFakeBoldText(true);
		this.mNodePaint.setAntiAlias(true);

		this.mTextPaint.setColor(Color.BLACK);
		this.mTextPaint.setAntiAlias(true);

		this.mBackgroundPaint.setColor(Color.rgb(230, 230, 230));
	}

	private void initWayPaints() {
		this.mHighwayFootwayPaint.setColor(Color.rgb(219,146,85));
		this.mHighwayFootwayPaint.setStrokeWidth(2);
		this.mHighwayFootwayPaint.setStyle(Style.STROKE);
		this.mHighwayFootwayPaint.setAntiAlias(true);


		this.mHighwayMotorwayPaint.setColor(Color.rgb(146,146,170));
		this.mHighwayMotorwayPaint.setStrokeWidth(10);
		this.mHighwayMotorwayPaint.setStyle(Style.STROKE);
		this.mHighwayMotorwayPaint.setAntiAlias(true);

		this.mHighwayMotorwayLinkPaint.setColor(Color.rgb(146,146,170));
		this.mHighwayMotorwayLinkPaint.setStrokeWidth(6);
		this.mHighwayMotorwayLinkPaint.setStyle(Style.STROKE);
		this.mHighwayMotorwayLinkPaint.setAntiAlias(true);


		this.mHighwayPrimaryPaint.setColor(Color.rgb(219,109,85));
		this.mHighwayPrimaryPaint.setStrokeWidth(8);
		this.mHighwayPrimaryPaint.setStyle(Style.STROKE);
		this.mHighwayPrimaryPaint.setAntiAlias(true);

		this.mHighwayPrimaryLinkPaint.setColor(Color.rgb(219,109,85));
		this.mHighwayPrimaryLinkPaint.setStrokeWidth(5);
		this.mHighwayPrimaryLinkPaint.setStyle(Style.STROKE);
		this.mHighwayPrimaryLinkPaint.setAntiAlias(true);


		this.mHighwayResidentialPaint.setColor(Color.rgb(255,255,255));
		this.mHighwayResidentialPaint.setStrokeWidth(7);
		this.mHighwayResidentialPaint.setStyle(Style.STROKE);
		this.mHighwayResidentialPaint.setAntiAlias(true);


		this.mHighwaySecondaryPaint.setColor(Color.rgb(255,182,85));
		this.mHighwaySecondaryPaint.setStrokeWidth(6);
		this.mHighwaySecondaryPaint.setStyle(Style.STROKE);
		this.mHighwaySecondaryPaint.setAntiAlias(true);


		this.mHighwayStepsPaint.setColor(Color.rgb(255,155,92));
		this.mHighwayStepsPaint.setStrokeWidth(2);
		this.mHighwayStepsPaint.setStyle(Style.STROKE);
		this.mHighwayStepsPaint.setPathEffect(new DashPathEffect(new float[]{3,3}, 0));
		this.mHighwayStepsPaint.setAntiAlias(true);


		this.mHighwayCyclewayPaint.setColor(Color.rgb(255,155,92));
		this.mHighwayCyclewayPaint.setStrokeWidth(3);
		this.mHighwayCyclewayPaint.setStyle(Style.STROKE);
		this.mHighwayCyclewayPaint.setAntiAlias(true);


		this.mHighwayServicePaint.setColor(Color.rgb(255,0,255));
		this.mHighwayServicePaint.setStrokeWidth(4);
		this.mHighwayServicePaint.setStyle(Style.STROKE);
		this.mHighwayServicePaint.setAntiAlias(true);


		this.mHighwayUnclassifiedPaint.setColor(Color.rgb(255,0,255));
		this.mHighwayUnclassifiedPaint.setStrokeWidth(5);
		this.mHighwayUnclassifiedPaint.setStyle(Style.STROKE);
		this.mHighwayUnclassifiedPaint.setAntiAlias(true);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public Bitmap renderTile(final OSMTileInfo pTileInfo) {
		final Bitmap out = Bitmap.createBitmap(256, 256, Config.ARGB_4444);
		final BoundingBoxE6 bbE6 = Util.getBoundingBoxFromMapTile(pTileInfo);

		final BoundingBoxE6 bbE6Padded = bbE6.increaseByScale(BOUNDINGBOX_PADDING_RELATIVE);
		try {
			/* Retrieve Nodes and Ways of the BoundingBox. */
			final List<OSMNode> nodes = OSMDatabaseManager.getNodesFromBoundingBox(this.mCtx, bbE6Padded);
			final List<OSMWay> ways = OSMDatabaseManager.getWaysFromBoundingBox(this.mCtx, bbE6Padded);

			final float[] reuse = new float[2];
			final Canvas c = new Canvas(out);
			c.drawRect(0,0,256,256, this.mBackgroundPaint);

			/* Draw the ways. */
			this.mTextPaint.setTextSize(10);
			for(final OSMWay e : ways){
				final ManagedLinePath p = new ManagedLinePath();
				for(final OSMNode n : e){
					bbE6.getRelativePositionOfGeoPointInBoundingBoxWithExactGudermannInterpolation(n.getLatitudeE6(), n.getLongitudeE6(), reuse);

					final float posX = reuse[X] * 256;
					final float posY = reuse[Y] * 256;

					p.lineTo(posX, posY);
				}

				final Paint paintToUse = resolvePaintFromHighwayID(e.getHighwayID());
				c.drawPath(p, paintToUse);

				if(e.hasName()){
					final PathMeasure pm = new PathMeasure(p, false);
					final float pathLength = pm.getLength();
					final float textLength = this.mTextPaint.measureText(e.getName());
					if(textLength < pathLength) {
						c.drawTextOnPath(e.getName(), p, (pathLength / 2 - textLength / 2), 4, this.mTextPaint);
					}
				}
			}


			/* Draw the nodes. */
			this.mTextPaint.setTextSize(8);
			for(final OSMNode n : nodes){
				bbE6.getRelativePositionOfGeoPointInBoundingBoxWithExactGudermannInterpolation(n.getLatitudeE6(), n.getLongitudeE6(), reuse);

				final float posX = reuse[X] * 256;
				final float posY = reuse[Y] * 256;

				c.drawCircle(posX, posY, 3, this.mNodePaint);
				if(n.hasName()){
					final float textWidth = this.mTextPaint.measureText(n.getName());
					c.drawText(n.getName(), posX - (textWidth / 2), posY, this.mTextPaint);
				}
			}
		} catch (final DataBaseException e) {
			// Simply return what has been drawn so far.
		}
		return out;
	}

	public void release() {
		// Noting
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private Paint resolvePaintFromHighwayID(final int pHighwayID) {
		switch(pHighwayID){
			case OSMWayConstants.OSMWAY_HIGHWAY_FOOTWAY_ID:
				return this.mHighwayFootwayPaint;
			case OSMWayConstants.OSMWAY_HIGHWAY_MOTORWAY_ID:
				return this.mHighwayMotorwayPaint;
			case OSMWayConstants.OSMWAY_HIGHWAY_MOTORWAY_LINK_ID:
				return this.mHighwayMotorwayLinkPaint;
			case OSMWayConstants.OSMWAY_HIGHWAY_PRIMARY_ID:
				return this.mHighwayPrimaryPaint;
			case OSMWayConstants.OSMWAY_HIGHWAY_PRIMARY_LINK_ID:
				return this.mHighwayPrimaryLinkPaint;
			case OSMWayConstants.OSMWAY_HIGHWAY_RESIDENTIAL_ID:
				return this.mHighwayResidentialPaint;
			case OSMWayConstants.OSMWAY_HIGHWAY_SECONDARY_ID:
				return this.mHighwaySecondaryPaint;
			case OSMWayConstants.OSMWAY_HIGHWAY_STEPS_ID:
				return this.mHighwayStepsPaint;
			case OSMWayConstants.OSMWAY_HIGHWAY_CYCLEWAY_ID:
				return this.mHighwayCyclewayPaint;
			case OSMWayConstants.OSMWAY_HIGHWAY_SERVICE_ID:
				return this.mHighwayServicePaint;
			case OSMWayConstants.OSMWAY_HIGHWAY_UNCLASSIFIED_ID:
			default:
				return this.mHighwayUnclassifiedPaint;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
