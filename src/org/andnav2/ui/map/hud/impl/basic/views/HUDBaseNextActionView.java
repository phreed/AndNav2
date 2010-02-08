// Created by plusminus on 17:36:42 - 03.02.2009
package org.andnav2.ui.map.hud.impl.basic.views;

import org.andnav2.adt.UnitSystem;
import org.andnav2.ui.map.hud.IHUDNextActionView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;


public abstract class HUDBaseNextActionView extends HUDBaseView implements IHUDNextActionView {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final int HUD_TURNARROW_LEFT_OFFSET;
	private final int HUD_TURNARROW_TOP_OFFSET;

	private final int HUD_TURNLDISTANCELEFT_LEFT_OFFSET;
	private final int HUD_TURNLDISTANCELEFT_TOP_OFFSET;
	private final int HUD_TURNDISTANCELEFT_UNIT_LEFT_OFFSET;
	private final int HUD_TURNLDISTANCELEFT_UNIT_TOP_OFFSET;

	private final int HUD_SPEED_LEFT_OFFSET;
	private final int HUD_SPEED_TOP_OFFSET;
	private final int HUD_SPEED_UNIT_LEFT_OFFSET;
	private final int HUD_SPEED_UNIT_TOP_OFFSET;

	private final Bitmap TURN_ARROW_LEFT_90;
	private final Bitmap TURN_ARROW_LEFT_45;
	private final Bitmap TURN_ARROW_LEFT_25;
	private final Bitmap TURN_ARROW_RIGHT_90;
	private final Bitmap TURN_ARROW_RIGHT_45;
	private final Bitmap TURN_ARROW_RIGHT_25;
	private final Bitmap TURN_ARROW_STRAIGHT;
	private final Bitmap TURN_TARGET_REACHED;

	protected float mNextTurnAngle = NOT_SET;
	protected float mCurrentSpeed = NOT_SET;

	protected boolean showTargetReachedInsteadOfAngle = false;

	// ===========================================================
	// Constructors
	// ===========================================================

	public HUDBaseNextActionView(final Context context, final AttributeSet attrs, final int hud_turnarrow_left_offset,
			final int hud_turnarrow_top_offset,
			final int hud_turnldistanceleft_left_offset,
			final int hud_turnldistanceleft_top_offset,
			final int hud_turndistanceleft_unit_left_offset,
			final int hud_turnldistanceleft_unit_top_offset,
			final int hud_speed_left_offset,
			final int hud_speed_top_offset,
			final int hud_speed_unit_left_offset,
			final int hud_speed_unit_top_offset,
			final Bitmap turn_arrow_left_90,
			final Bitmap turn_arrow_left_45,
			final Bitmap turn_arrow_left_25,
			final Bitmap turn_arrow_right_90,
			final Bitmap turn_arrow_right_45,
			final Bitmap turn_arrow_right_25,
			final Bitmap turn_arrow_straight,
			final Bitmap turn_target_reaced) {
		super(context, attrs);

		this.TURN_ARROW_LEFT_90 = turn_arrow_left_90;
		this.TURN_ARROW_LEFT_45 = turn_arrow_left_45;
		this.TURN_ARROW_LEFT_25 = turn_arrow_left_25;
		this.TURN_ARROW_RIGHT_90 = turn_arrow_right_90;
		this.TURN_ARROW_RIGHT_45 = turn_arrow_right_45;
		this.TURN_ARROW_RIGHT_25 = turn_arrow_right_25;
		this.TURN_ARROW_STRAIGHT = turn_arrow_straight;
		this.TURN_TARGET_REACHED = turn_target_reaced;

		this.HUD_TURNARROW_LEFT_OFFSET = hud_turnarrow_left_offset;
		this.HUD_TURNARROW_TOP_OFFSET = hud_turnarrow_top_offset;

		this.HUD_TURNLDISTANCELEFT_LEFT_OFFSET = hud_turnldistanceleft_left_offset;
		this.HUD_TURNLDISTANCELEFT_TOP_OFFSET = hud_turnldistanceleft_top_offset;
		this.HUD_TURNDISTANCELEFT_UNIT_LEFT_OFFSET = hud_turndistanceleft_unit_left_offset;
		this.HUD_TURNLDISTANCELEFT_UNIT_TOP_OFFSET = hud_turnldistanceleft_unit_top_offset;

		this.HUD_SPEED_LEFT_OFFSET = hud_speed_left_offset;
		this.HUD_SPEED_TOP_OFFSET = hud_speed_top_offset;
		this.HUD_SPEED_UNIT_LEFT_OFFSET = hud_speed_unit_left_offset;
		this.HUD_SPEED_UNIT_TOP_OFFSET = hud_speed_unit_top_offset;

		onInitiatePaint(super.mHudTextPaint);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setTurnAngle(final float aAngle){
		this.mNextTurnAngle = aAngle;
		this.showTargetReachedInsteadOfAngle = false;
	}

	public void setCurrentMeterSpeed(final float aMeterSpeed){
		this.mCurrentSpeed = aMeterSpeed * this.mUnitSystem.mScaleToMetersPerSecond;
	}

	@Override
	public void reset(){
		super.reset();
		this.mNextTurnAngle = NOT_SET;
	}

	public void showTargetReached() {
		this.showTargetReachedInsteadOfAngle = true;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	protected abstract void onInitiatePaint(final Paint pHudTextPaint);
	protected abstract void onConfigureValuePaint(final Paint pHudTextPaint);
	protected abstract void onConfigureUnitPaint(final Paint pHudTextPaint);

	public void recycle() {
		this.TURN_ARROW_LEFT_90.recycle();
		this.TURN_ARROW_LEFT_45.recycle();
		this.TURN_ARROW_LEFT_25.recycle();
		this.TURN_ARROW_RIGHT_90.recycle();
		this.TURN_ARROW_RIGHT_45.recycle();
		this.TURN_ARROW_RIGHT_25.recycle();
		this.TURN_ARROW_STRAIGHT.recycle();
		this.TURN_TARGET_REACHED.recycle();
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		super.onDraw(canvas);

		if(this.showTargetReachedInsteadOfAngle){
			canvas.drawBitmap(this.TURN_TARGET_REACHED, this.HUD_TURNARROW_LEFT_OFFSET, this.HUD_TURNARROW_TOP_OFFSET, this.mFullOpaquePaint);
		}else{
			if(this.mNextTurnAngle != NOT_SET) {
				canvas.drawBitmap(getArrowFromAngle(), this.HUD_TURNARROW_LEFT_OFFSET, this.HUD_TURNARROW_TOP_OFFSET, this.mFullOpaquePaint);
			}

		}

		if(this.mDistance != NOT_SET){
			refreshDistanceStrings();

			onConfigureValuePaint(this.mHudTextPaint);
			canvas.drawText(this.mDistanceStrings[UnitSystem.DISTSTRINGS_DIST_ID], this.HUD_TURNLDISTANCELEFT_LEFT_OFFSET, this.HUD_TURNLDISTANCELEFT_TOP_OFFSET, this.mHudTextPaint);

			/* And the unit above. */
			onConfigureUnitPaint(this.mHudTextPaint);
			canvas.drawText(this.mDistanceStrings[UnitSystem.DISTSTRINGS_UNIT_ID], this.HUD_TURNDISTANCELEFT_UNIT_LEFT_OFFSET, this.HUD_TURNLDISTANCELEFT_UNIT_TOP_OFFSET, this.mHudTextPaint);
		}else if(this.mCurrentSpeed != NOT_SET){
			onConfigureValuePaint(this.mHudTextPaint);
			canvas.drawText("" + (int)this.mCurrentSpeed, this.HUD_SPEED_LEFT_OFFSET, this.HUD_SPEED_TOP_OFFSET, this.mHudTextPaint);

			/* And the unit above. */
			onConfigureUnitPaint(this.mHudTextPaint);
			canvas.drawText("" + this.mUnitSystem.mAbbrKilometersPerHourScale, this.HUD_SPEED_UNIT_LEFT_OFFSET, this.HUD_SPEED_UNIT_TOP_OFFSET, this.mHudTextPaint);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected final Bitmap getArrowFromAngle() {
		final int turnAngle = (int)this.mNextTurnAngle;
		if(turnAngle > 60) {
			return this.TURN_ARROW_LEFT_90;
		} else if(turnAngle > 35) {
			return this.TURN_ARROW_LEFT_45;
		} else if(turnAngle > 15) {
			return this.TURN_ARROW_LEFT_25;
		} else if(turnAngle <= 15 && turnAngle >= -15) {
			return this.TURN_ARROW_STRAIGHT;
		} else if(turnAngle > -35) {
			return this.TURN_ARROW_RIGHT_25;
		} else if(turnAngle > -60) {
			return this.TURN_ARROW_RIGHT_45;
		} else {
			return this.TURN_ARROW_RIGHT_90;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
