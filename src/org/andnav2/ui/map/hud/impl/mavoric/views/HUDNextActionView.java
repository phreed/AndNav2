// Created by plusminus on 7:56:23 PM - Feb 23, 2009
package org.andnav2.ui.map.hud.impl.mavoric.views;

import org.andnav2.R;
import org.andnav2.ui.map.hud.impl.basic.views.HUDBaseNextActionView;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;


public class HUDNextActionView extends HUDBaseNextActionView {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int HUD_TURNARROW_LEFT_OFFSET = 14;
	private static final int HUD_TURNARROW_TOP_OFFSET = 10;

	private static final int HUD_TURNLDISTANCELEFT_LEFT_OFFSET = 14;
	private static final int HUD_TURNLDISTANCELEFT_TOP_OFFSET = 92;
	private static final int HUD_TURNDISTANCELEFT_UNIT_LEFT_OFFSET = 63;
	private static final int HUD_TURNLDISTANCELEFT_UNIT_TOP_OFFSET = 90;

	private static final int HUD_SPEED_LEFT_OFFSET = HUD_TURNLDISTANCELEFT_LEFT_OFFSET;
	private static final int HUD_SPEED_TOP_OFFSET = HUD_TURNLDISTANCELEFT_TOP_OFFSET;
	private static final int HUD_SPEED_UNIT_LEFT_OFFSET = HUD_TURNDISTANCELEFT_UNIT_LEFT_OFFSET - 9; // -9 because "km" vs. "kmh"
	private static final int HUD_SPEED_UNIT_TOP_OFFSET = HUD_TURNLDISTANCELEFT_UNIT_TOP_OFFSET;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public HUDNextActionView(final Context context, final AttributeSet attrs) {
		super(context, attrs,
				HUD_TURNARROW_LEFT_OFFSET,
				HUD_TURNARROW_TOP_OFFSET,
				HUD_TURNLDISTANCELEFT_LEFT_OFFSET,
				HUD_TURNLDISTANCELEFT_TOP_OFFSET,
				HUD_TURNDISTANCELEFT_UNIT_LEFT_OFFSET,
				HUD_TURNLDISTANCELEFT_UNIT_TOP_OFFSET,
				HUD_SPEED_LEFT_OFFSET,
				HUD_SPEED_TOP_OFFSET,
				HUD_SPEED_UNIT_LEFT_OFFSET,
				HUD_SPEED_UNIT_TOP_OFFSET,
				/* Initially load the turn-arrows. */
				BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_left_90_white),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_left_45_white),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_left_25_white),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_right_90_white),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_right_45_white),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_right_25_white),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_straight_white),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.flag_destination));
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onInitiatePaint(final Paint pHudTextPaint) {
		pHudTextPaint.setColor(Color.WHITE);
	}

	@Override
	protected void onConfigureUnitPaint(final Paint pPaint) {
		pPaint.setTextSize(25);
		pPaint.setTextScaleX(1.0f);
	}

	@Override
	protected void onConfigureValuePaint(final Paint pPaint) {
		pPaint.setTextSize(40);
		pPaint.setTextScaleX(1.1f);
	}

	public void setNextActionOnClickListener(final OnClickListener pOnClickListener) {
		this.setOnClickListener(pOnClickListener);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
