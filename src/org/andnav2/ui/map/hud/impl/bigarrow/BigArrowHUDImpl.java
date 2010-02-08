// Created by plusminus on 11:17:18 AM - Feb 27, 2009
package org.andnav2.ui.map.hud.impl.bigarrow;

import org.andnav2.R;
import org.andnav2.adt.UnitSystem;
import org.andnav2.osm.views.overlay.util.DirectionArrowDescriptor;
import org.andnav2.ui.map.hud.IHUDImpl;
import org.andnav2.ui.map.hud.IHUDImplVariation;
import org.andnav2.ui.map.hud.IHUDNextActionView;
import org.andnav2.ui.map.hud.IHUDRemainingSummaryView;
import org.andnav2.ui.map.hud.IHUDTurnDescriptionView;
import org.andnav2.ui.map.hud.impl.bigarrow.views.HUDNextActionView;
import org.andnav2.ui.map.hud.util.Util;
import org.andnav2.util.TimeUtils;
import org.andnav2.util.constants.Constants;

import android.graphics.Point;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class BigArrowHUDImpl implements IHUDImpl {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final int ID = 2;

	// ===========================================================
	// Fields
	// ===========================================================

	private TextView mTvTurnDescription;
	private TextView mTvTimeRemaining;
	private TextView mTvLengthRemaining;
	private HUDNextActionView mHUDNextActionView;

	private DescriptionAndSummaryWrapper mDescriptionAndSummaryWrapper;

	private IHUDNextActionView mHUDUpperNextActionDummyAdapter;

	// ===========================================================
	// Constructors
	// ===========================================================

	public void init(final View pRootView) {
		this.mHUDNextActionView = (HUDNextActionView) pRootView.findViewById(R.id.hud_ddmap_bigarrow_nextaction);
		this.mTvLengthRemaining = (TextView) pRootView.findViewById(R.id.hud_ddmap_bigarrow_restlength);
		this.mTvTimeRemaining = (TextView) pRootView.findViewById(R.id.hud_ddmap_bigarrow_time);
		this.mTvTurnDescription = (TextView) pRootView.findViewById(R.id.hud_ddmap_bigarrow_turndescription);

		this.mDescriptionAndSummaryWrapper = new DescriptionAndSummaryWrapper();

		this.mHUDUpperNextActionDummyAdapter = new IHUDNextActionView.IHUDNextActionViewAdapter();
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
		return R.string.hud_bigarrow_description;
	}

	public int getID() {
		return ID;
	}

	public int getNameResourceID() {
		return R.string.hud_bigarrow_name;
	}

	public IHUDNextActionView getNextActionView() {
		return this.mHUDNextActionView;
	}

	public IHUDRemainingSummaryView getRemainingSummaryView() {
		return this.mDescriptionAndSummaryWrapper;
	}

	public IHUDTurnDescriptionView getTurnTurnDescriptionView() {
		return this.mDescriptionAndSummaryWrapper;
	}

	public IHUDNextActionView getUpperNextActionView() {
		return this.mHUDUpperNextActionDummyAdapter;
	}

	public IHUDImplVariation getVariation(final int pVariationID) throws IllegalArgumentException {
		return Util.getVariation(VARIATIONS, pVariationID);
	}

	public IHUDImplVariation[] getVariations() {
		return VARIATIONS;
	}


	public void invalidateViews() {

	}

	public void setUpperNextActionViewNecessary(final boolean pNecessary) {
		/* Nothing. */
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class DescriptionAndSummaryWrapper implements IHUDRemainingSummaryView, IHUDTurnDescriptionView {
		private UnitSystem mUnitSystem = UnitSystem.METRIC;
		protected boolean mArrivalTimeInsteadOfRestTime = false;

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
				BigArrowHUDImpl.this.mTvTimeRemaining.setText(TimeUtils.getTimeString(estimatedTimeLeftMinutes));
			}else{
				BigArrowHUDImpl.this.mTvTimeRemaining.setText(TimeUtils.getTimeDurationString(estimatedTimeLeftMinutes));
			}
		}

		public void setGPSConnectionStrength(final int pConnectionStrength) {
			/* Not displayed. */
		}

		public void setMetersDrivenSession(final int pMetersDriven) {
			final String[] pParts = this.mUnitSystem.getDistanceString(pMetersDriven, null);
			BigArrowHUDImpl.this.mTvLengthRemaining.setText(pParts[UnitSystem.DISTSTRINGS_DIST_ID] + " " + pParts[UnitSystem.DISTSTRINGS_UNIT_ID]);
		}

		public void onClick() {
			this.mArrivalTimeInsteadOfRestTime = !this.mArrivalTimeInsteadOfRestTime;
		}

		public void recycle() {

		}

		public void reset() {
			BigArrowHUDImpl.this.mTvLengthRemaining.setText("");
			BigArrowHUDImpl.this.mTvTimeRemaining.setText("");
			BigArrowHUDImpl.this.mTvTurnDescription.setText("");
		}

		public void setDisplayQuality(final int pDisplayQuality) {
			/* Nothing, because the views are TextViews only. */
		}

		public void setDistance(final int pMeterDistance) {
			if(pMeterDistance != Constants.NOT_SET){
				final String[] pParts = this.mUnitSystem.getDistanceString(pMeterDistance, null);
				BigArrowHUDImpl.this.mTvLengthRemaining.setText(pParts[UnitSystem.DISTSTRINGS_DIST_ID] + " " + pParts[UnitSystem.DISTSTRINGS_UNIT_ID]);
			}
		}

		public void setUnitSystem(final UnitSystem pUnitSystem) {
			this.mUnitSystem = pUnitSystem;
		}

		public void setRemainingSummaryOnClickListener(final OnClickListener pOnClickListener) {
			BigArrowHUDImpl.this.mTvTimeRemaining.setOnClickListener(pOnClickListener);
			BigArrowHUDImpl.this.mTvLengthRemaining.setOnClickListener(pOnClickListener);
		}

		public String getTurnDescription() {
			return BigArrowHUDImpl.this.mTvTurnDescription.getText().toString();
		}

		public void setTurnDescription(final String pTurnDescription) {
			BigArrowHUDImpl.this.mTvTurnDescription.setText(pTurnDescription);
		}

		public void setTurnDescriptionOnClickListener(final OnClickListener pOnClickListener) {
			BigArrowHUDImpl.this.mTvTurnDescription.setOnClickListener(pOnClickListener);
		}
	}

	private static final IHUDImplVariation[] VARIATIONS = new IHUDImplVariation[]{
		new IHUDImplVariation(){
			public int getLayoutID() {
				return  R.layout.ddmap_hud_bigarrow_default;
			}

			public int getVariationID() {
				return VARIATION_DEFAULT_ID;
			}

			public DirectionArrowDescriptor getDirectionArrowDescriptor() {
				return new DirectionArrowDescriptor(new Point(20,20), R.drawable.hud_basic_variation_default_direction_arrow);
			}

			public int getDescriptionStringID() {
				return R.string.hud_bigarrow_variation_default;
			}

			public int getScreenshotResourceID() {
				return R.drawable.hud_bigarrow_variation_default_sample;
			}
		},
		new IHUDImplVariation(){
			public int getLayoutID() {
				return  R.layout.ddmap_hud_bigarrow_variation_1;
			}

			public int getVariationID() {
				return VARIATION_DEFAULT_ID + 1;
			}

			public DirectionArrowDescriptor getDirectionArrowDescriptor() {
				return new DirectionArrowDescriptor(new Point(20,20), R.drawable.hud_basic_variation_default_direction_arrow);
			}

			public int getDescriptionStringID() {
				return R.string.hud_bigarrow_variation_1;
			}

			public int getScreenshotResourceID() {
				return R.drawable.hud_bigarrow_variation_1_sample;
			}
		}
	};
}
