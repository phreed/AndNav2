// Created by plusminus on 18:54:36 - 09.09.2008
package org.andnav2.ui.map.hud.impl.basic.views;

import org.andnav2.adt.UnitSystem;
import org.andnav2.ui.map.hud.IHUDRemainingSummaryView;
import org.andnav2.util.TimeUtils;
import org.andnav2.util.constants.Constants;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;


public class HUDRemainingSummaryView extends HUDBaseView implements IHUDRemainingSummaryView {
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final int HUD_TOTALDISTANCELEFT_LEFT_OFFSET = 5;
	protected static final int HUD_TOTALDISTANCELEFT_TOP_OFFSET = 28;
	protected static final int HUD_TOTALDISTANCELEFT_UNIT_LEFT_OFFSET = 66;
	protected static final int HUD_TOTALDISTANCELEFT_UNIT_TOP_OFFSET = 15;
	protected static final int HUD_TOTALTIMELEFT_CENTER_OFFSET = 40;
	protected static final int HUD_TOTALTIMELEFT_LEFT_OFFSET = HUD_TOTALDISTANCELEFT_LEFT_OFFSET;
	protected static final int HUD_TOTALTIMELEFT_TOP_OFFSET = 58;
	protected static final int HUD_TOTALTIMELEFT_AMPM_LEFT_OFFSET = 71;
	protected static final int HUD_TOTALTIMELEFT_AMPM_TOP_OFFSET = 38;

	// ===========================================================
	// Fields
	// ===========================================================

	//	protected final java.text.DateFormat mTimeFormat; // this.mTimeFormat = DateFormat.getTimeFormat(context);

	protected int mEstimatedRestSeconds = NOT_SET;
	/** In units specific to the unitsytsem!!! */
	protected int mDrivenInSession = NOT_SET;

	protected final Paint dataStrengthPaint;
	protected final Paint gpsStrengthPaint;

	private int mDataConnectionStrength = 4;
	private int mGPSConnectionStrength = 0;

	protected boolean mArrivalTimeInsteadOfRestTime = false;

	// ===========================================================
	// Constructors
	// ===========================================================

	public HUDRemainingSummaryView(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		this.dataStrengthPaint = new Paint();
		this.dataStrengthPaint.setARGB(255,255,0,0); // full opaque, red
		this.dataStrengthPaint.setStyle(Paint.Style.FILL);

		this.gpsStrengthPaint = new Paint(this.dataStrengthPaint);
		this.gpsStrengthPaint.setARGB(255,0,0,255); // full opaque, blue
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void recycle() {
		// Nothing
	}

	@Override
	public void onClick() {
		this.mArrivalTimeInsteadOfRestTime = !this.mArrivalTimeInsteadOfRestTime;
		postInvalidate();
	}

	public void setEstimatedRestSeconds(final int estimatedRestSeconds) {
		this.mEstimatedRestSeconds = estimatedRestSeconds;
		postInvalidate();
	}

	public void setMetersDrivenSession(final int aMetersDriven) {
		this.mDrivenInSession = (int)(aMetersDriven * this.mUnitSystem.mScaleToMeters);
		postInvalidate();
	}

	public void setDataConnectionStrength(final int dataConnectionStrength) {
		this.mDataConnectionStrength = dataConnectionStrength;
		postInvalidate();
	}

	public void setGPSConnectionStrength(final int connectionStrength) {
		this.mGPSConnectionStrength = connectionStrength;
		postInvalidate();
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		super.onDraw(canvas);

		final int estimatedRestSeconds = this.mEstimatedRestSeconds; // Drag to local field
		/* And draw i.e.the distance and time left to the info-menu.*/
		if(this.mDistance != NOT_SET && this.mEstimatedRestSeconds != NOT_SET){
			final int estimatedTimeLeftMinutes;
			if(estimatedRestSeconds == NOT_SET) {
				estimatedTimeLeftMinutes = NOT_SET;
			} else {
				estimatedTimeLeftMinutes = (estimatedRestSeconds + 15)/ 60; /* The last 15 seconds will show "0:00" */
			}

			refreshDistanceStrings();


			/* And the unit above. */
			this.mHudTextPaint.setTextSize(12);
			this.mHudTextPaint.setTextScaleX(1.0f); // No X-stretching
			canvas.drawText(this.mDistanceStrings[UnitSystem.DISTSTRINGS_UNIT_ID], HUD_TOTALDISTANCELEFT_UNIT_LEFT_OFFSET, HUD_TOTALDISTANCELEFT_UNIT_TOP_OFFSET, this.mHudTextPaint);

			/* Draw total distance left. */
			this.mHudTextPaint.setTextSize(30);
			this.mHudTextPaint.setTextScaleX(1.1f); // Little X-stretching
			canvas.drawText(this.mDistanceStrings[UnitSystem.DISTSTRINGS_DIST_ID], HUD_TOTALDISTANCELEFT_LEFT_OFFSET, HUD_TOTALDISTANCELEFT_TOP_OFFSET, this.mHudTextPaint);

			/* Draw total time left. */
			this.mHudTextPaint.setTextScaleX(1.25f); // Draw the time a X-stretched

			if(estimatedTimeLeftMinutes == NOT_SET){
				final float textWidth = this.mHudTextPaint.measureText("?:??");
				canvas.drawText("?:??", HUD_TOTALTIMELEFT_CENTER_OFFSET - textWidth/2, HUD_TOTALTIMELEFT_TOP_OFFSET, this.mHudTextPaint);
			}else{
				if(this.mArrivalTimeInsteadOfRestTime){
					this.mHudTextPaint.setTextSize(28);

					final String timeString = TimeUtils.getTimeString(estimatedTimeLeftMinutes);

					this.mHudTextPaint.setTextScaleX(1.0f); // Draw the time a X-stretched
					final float textWidth = this.mHudTextPaint.measureText(timeString);

					canvas.drawText(timeString, HUD_TOTALTIMELEFT_CENTER_OFFSET - textWidth/2 - 1, HUD_TOTALTIMELEFT_TOP_OFFSET - 1, this.mHudTextPaint);


					/* Draw AM or PM */
					this.mHudTextPaint.setTextSize(9);
					this.mHudTextPaint.setFakeBoldText(false);

					if(TimeUtils.isAMFromNow(estimatedTimeLeftMinutes)){ /* AM */
						canvas.drawText("am", HUD_TOTALTIMELEFT_AMPM_LEFT_OFFSET, HUD_TOTALTIMELEFT_AMPM_TOP_OFFSET, this.mHudTextPaint);
					}else{ /* PM */
						canvas.drawText("pm", HUD_TOTALTIMELEFT_AMPM_LEFT_OFFSET, HUD_TOTALTIMELEFT_AMPM_TOP_OFFSET, this.mHudTextPaint);
					}
					this.mHudTextPaint.setFakeBoldText(true);

				}else{
					final String timeString = TimeUtils.getTimeDurationString(estimatedTimeLeftMinutes);
					final float textWidth = this.mHudTextPaint.measureText(timeString);
					canvas.drawText(timeString, HUD_TOTALTIMELEFT_CENTER_OFFSET - textWidth/2, HUD_TOTALTIMELEFT_TOP_OFFSET, this.mHudTextPaint);
				}
			}


		}else if(this.mDrivenInSession != Constants.NOT_SET){
			refreshDistanceStrings(this.mDrivenInSession);

			/* Draw total distance left. */
			this.mHudTextPaint.setTextSize(30);
			this.mHudTextPaint.setTextScaleX(1.1f); // Little X-stretching
			canvas.drawText(this.mDistanceStrings[UnitSystem.DISTSTRINGS_DIST_ID], HUD_TOTALDISTANCELEFT_LEFT_OFFSET, HUD_TOTALDISTANCELEFT_TOP_OFFSET, this.mHudTextPaint);
			/* And the unit above. */
			this.mHudTextPaint.setTextSize(12);
			this.mHudTextPaint.setTextScaleX(1.0f); // No X-stretching
			canvas.drawText(this.mDistanceStrings[UnitSystem.DISTSTRINGS_UNIT_ID], HUD_TOTALDISTANCELEFT_UNIT_LEFT_OFFSET, HUD_TOTALDISTANCELEFT_UNIT_TOP_OFFSET, this.mHudTextPaint);
		}

		/* Draw the Data Connection/GPS -Strength bars. */
		{
			/* Draw the Data Connection Strength bars. */
			final int BAR_OFFSET_HORIZONTAL = 87;
			final int GPSBAR_BASE_VERTICAL = 11;
			final int BAR_HEIGHT_INCREASE = 4;
			final int BAR_LEFT_DIFF = 7;
			final int BAR_WIDTH = 3;

			int left, right, top, bottom;
			/* Starting from 8 satellites, all 5 bars should be filled */
			switch(Math.min(5, (int)(0.5f + this.mGPSConnectionStrength / (8.0f / 5.0f)))){
				case 5:
					left = BAR_OFFSET_HORIZONTAL + 4 * BAR_LEFT_DIFF;
					right = left + BAR_WIDTH;
					top = GPSBAR_BASE_VERTICAL;
					bottom = top + 8;
					canvas.drawRect(left, top, right, bottom, this.gpsStrengthPaint);
				case 4:
					left = BAR_OFFSET_HORIZONTAL + 3 * BAR_LEFT_DIFF;
					right = left + BAR_WIDTH;
					top = GPSBAR_BASE_VERTICAL + 1*BAR_HEIGHT_INCREASE;
					bottom = top + 4;
					canvas.drawRect(left, top, right, bottom, this.gpsStrengthPaint);
				case 3:
					left = BAR_OFFSET_HORIZONTAL + 2 * BAR_LEFT_DIFF;
					right = left + BAR_WIDTH;
					top = GPSBAR_BASE_VERTICAL + 2*BAR_HEIGHT_INCREASE;
					bottom = top + 10;
					canvas.drawRect(left, top, right, bottom, this.gpsStrengthPaint);
				case 2:
					left = BAR_OFFSET_HORIZONTAL + 1 * BAR_LEFT_DIFF;
					right = left + BAR_WIDTH;
					top = GPSBAR_BASE_VERTICAL + 3*BAR_HEIGHT_INCREASE;
					bottom = top + 6;
					canvas.drawRect(left, top, right, bottom, this.gpsStrengthPaint);
				case 1:
					left = BAR_OFFSET_HORIZONTAL;
					right = left + BAR_WIDTH;
					top = GPSBAR_BASE_VERTICAL + 4*BAR_HEIGHT_INCREASE;
					bottom = top + 2;
					canvas.drawRect(left, top, right, bottom, this.gpsStrengthPaint);
			}

			/* Draw the GPSStrength bars. */
			final int DATABAR_BASE_VERTICAL = 35;

			switch(this.mDataConnectionStrength){
				case 5:
					left = BAR_OFFSET_HORIZONTAL + 4 * BAR_LEFT_DIFF;
					right = left + BAR_WIDTH;
					top = DATABAR_BASE_VERTICAL;
					bottom = top + 8;
					canvas.drawRect(left, top, right, bottom, this.dataStrengthPaint);
				case 4:
					left = BAR_OFFSET_HORIZONTAL + 3 * BAR_LEFT_DIFF;
					right = left + BAR_WIDTH;
					top = DATABAR_BASE_VERTICAL + 1*BAR_HEIGHT_INCREASE;
					bottom = top + 4;
					canvas.drawRect(left, top, right, bottom, this.dataStrengthPaint);
				case 3:
					left = BAR_OFFSET_HORIZONTAL + 2 * BAR_LEFT_DIFF;
					right = left + BAR_WIDTH;
					top = DATABAR_BASE_VERTICAL + 2*BAR_HEIGHT_INCREASE;
					bottom = top + 10;
					canvas.drawRect(left, top, right, bottom, this.dataStrengthPaint);
				case 2:
					left = BAR_OFFSET_HORIZONTAL + 1 * BAR_LEFT_DIFF;
					right = left + BAR_WIDTH;
					top = DATABAR_BASE_VERTICAL + 3*BAR_HEIGHT_INCREASE;
					bottom = top + 6;
					canvas.drawRect(left, top, right, bottom, this.dataStrengthPaint);
				case 1:
					left = BAR_OFFSET_HORIZONTAL;
					right = left + BAR_WIDTH;
					top = DATABAR_BASE_VERTICAL + 4*BAR_HEIGHT_INCREASE;
					bottom = top + 2;
					canvas.drawRect(left, top, right, bottom, this.dataStrengthPaint);
			}
		}
	}

	public void setRemainingSummaryOnClickListener(final OnClickListener pOnClickListener) {
		this.setOnClickListener(pOnClickListener);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
