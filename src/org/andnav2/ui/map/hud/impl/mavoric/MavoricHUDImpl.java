// Created by plusminus on 6:43:53 PM - Feb 23, 2009
package org.andnav2.ui.map.hud.impl.mavoric;

import org.andnav2.R;
import org.andnav2.adt.UnitSystem;
import org.andnav2.osm.views.overlay.util.DirectionArrowDescriptor;
import org.andnav2.ui.map.hud.IHUDImpl;
import org.andnav2.ui.map.hud.IHUDImplVariation;
import org.andnav2.ui.map.hud.IHUDNextActionView;
import org.andnav2.ui.map.hud.IHUDRemainingSummaryView;
import org.andnav2.ui.map.hud.IHUDTurnDescriptionView;
import org.andnav2.ui.map.hud.impl.mavoric.views.HUDNextActionView;
import org.andnav2.ui.map.hud.impl.mavoric.views.HUDUpperNextActionView;
import org.andnav2.ui.map.hud.util.Util;
import org.andnav2.util.TimeUtils;
import org.andnav2.util.constants.Constants;

import android.graphics.Point;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class MavoricHUDImpl implements IHUDImpl {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final int ID = 1;

	// ===========================================================
	// Fields
	// ===========================================================

	private TextView mTvTurnDescription;
	private TextView mTvTimeRemaining;
	private TextView mTvLengthRemaining;
	private HUDNextActionView mHUDNextActionView;
	private HUDUpperNextActionView mHUDUpperNextActionView;

	private DescriptionAndSummaryWrapper mDescriptionAndSummaryWrapper;

	// ===========================================================
	// Constructors
	// ===========================================================

	public void init(final View pRootView) {
		this.mHUDNextActionView = (HUDNextActionView) pRootView.findViewById(R.id.hud_ddmap_mavoric_nextaction);
		this.mHUDUpperNextActionView = (HUDUpperNextActionView) pRootView.findViewById(R.id.hud_ddmap_mavoric_uppernextaction);
		this.mTvLengthRemaining = (TextView) pRootView.findViewById(R.id.hud_ddmap_mavoric_restlength);
		this.mTvTimeRemaining = (TextView) pRootView.findViewById(R.id.hud_ddmap_mavoric_time);
		this.mTvTurnDescription = (TextView) pRootView.findViewById(R.id.hud_ddmap_mavoric_turndescription);

		this.mDescriptionAndSummaryWrapper = new DescriptionAndSummaryWrapper();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public int getCountOfVariations() {
		return VARIATIONS.length;
	}

	public int getDescriptionResourceID() {
		return R.string.hud_mavoric_description;
	}

	public int getID() {
		return ID;
	}

	public int getNameResourceID() {
		return R.string.hud_mavoric_name;
	}

	public IHUDNextActionView getNextActionView() {
		return this.mHUDNextActionView;
	}

	public IHUDNextActionView getUpperNextActionView() {
		return this.mHUDUpperNextActionView;
	}

	public IHUDRemainingSummaryView getRemainingSummaryView() {
		return this.mDescriptionAndSummaryWrapper;
	}

	public IHUDTurnDescriptionView getTurnTurnDescriptionView() {
		return this.mDescriptionAndSummaryWrapper;
	}

	public IHUDImplVariation getVariation(final int pVariationID) throws IllegalArgumentException {
		return Util.getVariation(VARIATIONS, pVariationID);
	}

	public IHUDImplVariation[] getVariations() {
		return VARIATIONS;
	}

	public void invalidateViews() {

	}

	public void setUpperNextActionViewNecessary(final boolean necessary) {
		/* Nothing. */
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class DescriptionAndSummaryWrapper implements IHUDRemainingSummaryView, IHUDTurnDescriptionView{
		private UnitSystem mUnitSystem;
		protected boolean mArrivalTimeInsteadOfRestTime = false;
		private int mDrivenInSession;

		public void setDataConnectionStrength(final int dataConnectionStrength) {
			/* Not displayed. */
		}

		public void setEstimatedRestSeconds(final int pEstimatedRestSeconds) {
			final int estimatedTimeLeftMinutes;
			if(pEstimatedRestSeconds == Constants.NOT_SET) {
				return;
			}

			estimatedTimeLeftMinutes = (pEstimatedRestSeconds + 15)/ 60; /* The last 15 seconds will show "0:00" */

			if(this.mArrivalTimeInsteadOfRestTime){
				MavoricHUDImpl.this.mTvTimeRemaining.setText(TimeUtils.getTimeString(estimatedTimeLeftMinutes));
			}else{
				MavoricHUDImpl.this.mTvTimeRemaining.setText(TimeUtils.getTimeDurationString(estimatedTimeLeftMinutes));
			}
		}

		public void setGPSConnectionStrength(final int pConnectionStrength) {
			/* Not displayed. */
		}

		public void setMetersDrivenSession(final int pMetersDriven) {
			if(pMetersDriven == Constants.NOT_SET) {
				return;
			}

			this.mDrivenInSession = (int)(pMetersDriven * this.mUnitSystem.mScaleToMeters);

			final String[] pParts = this.mUnitSystem.getDistanceString(pMetersDriven, null);
			MavoricHUDImpl.this.mTvLengthRemaining.setText(pParts[UnitSystem.DISTSTRINGS_DIST_ID] + " " + pParts[UnitSystem.DISTSTRINGS_UNIT_ID]);
		}

		public void onClick() {
			this.mArrivalTimeInsteadOfRestTime = !this.mArrivalTimeInsteadOfRestTime;
		}

		public void recycle() {

		}

		public void reset() {
			MavoricHUDImpl.this.mTvLengthRemaining.setText("");
			MavoricHUDImpl.this.mTvTimeRemaining.setText("");
			MavoricHUDImpl.this.mTvTurnDescription.setText("");
		}

		public void setDisplayQuality(final int pDisplayQuality) {
			/* Nothing, because the views are TextViews only. */
		}

		public void setDistance(final int pMeterDistance) {
			if(pMeterDistance == Constants.NOT_SET) {
				return;
			}

			final String[] pParts = this.mUnitSystem.getDistanceString(pMeterDistance, null);
			MavoricHUDImpl.this.mTvLengthRemaining.setText(pParts[UnitSystem.DISTSTRINGS_DIST_ID] + " " + pParts[UnitSystem.DISTSTRINGS_UNIT_ID]);
		}

		public void setUnitSystem(final UnitSystem pUnitSystem) {
			this.mUnitSystem = pUnitSystem;
			this.setMetersDrivenSession(this.mDrivenInSession);
			// TODO More like the one above
		}

		public String getTurnDescription() {
			return MavoricHUDImpl.this.mTvTurnDescription.getText().toString();
		}

		public void setTurnDescription(final String pTurnDescription) {
			MavoricHUDImpl.this.mTvTurnDescription.setText(pTurnDescription);
		}

		public void setTurnDescriptionOnClickListener(final OnClickListener pOnClickListener) {
			MavoricHUDImpl.this.mTvTurnDescription.setOnClickListener(pOnClickListener);
		}

		public void setRemainingSummaryOnClickListener(final OnClickListener pOnClickListener) {
			MavoricHUDImpl.this.mTvTimeRemaining.setOnClickListener(pOnClickListener);
			MavoricHUDImpl.this.mTvLengthRemaining.setOnClickListener(pOnClickListener);
		}
	}

	private static final IHUDImplVariation[] VARIATIONS = new IHUDImplVariation[]{
		new IHUDImplVariation(){
			public int getLayoutID() {
				return  R.layout.ddmap_hud_mavoric_default;
			}

			public int getVariationID() {
				return VARIATION_DEFAULT_ID;
			}

			public DirectionArrowDescriptor getDirectionArrowDescriptor() {
				return new DirectionArrowDescriptor(new Point(24,30), R.drawable.hud_mavoric_variation_default_direction_arrow);
			}

			public int getDescriptionStringID() {
				return R.string.hud_mavoric_variation_default;
			}

			public int getScreenshotResourceID() {
				return R.drawable.hud_mavoric_variation_default_sample;
			}
		}
	};
}
