// Created by plusminus on 18:54:36 - 09.09.2008
package org.andnav2.ui.map.hud.impl.basic.views;

import org.andnav2.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;


public class HUDUpperNextActionView extends HUDBaseNextActionView {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int HUD_TURNARROW_LEFT_OFFSET = 1;
	private static final int HUD_TURNARROW_TOP_OFFSET = 4;

	private static final int HUD_TURNLDISTANCELEFT_LEFT_OFFSET = 38;
	private static final int HUD_TURNLDISTANCELEFT_TOP_OFFSET = 33;
	private static final int HUD_TURNDISTANCELEFT_UNIT_LEFT_OFFSET = 74;
	private static final int HUD_TURNLDISTANCELEFT_UNIT_TOP_OFFSET = 10;

	private static final int HUD_SPEED_LEFT_OFFSET = HUD_TURNLDISTANCELEFT_LEFT_OFFSET;
	private static final int HUD_SPEED_TOP_OFFSET = HUD_TURNLDISTANCELEFT_TOP_OFFSET;
	private static final int HUD_SPEED_UNIT_LEFT_OFFSET = HUD_TURNDISTANCELEFT_UNIT_LEFT_OFFSET - 6; // -6 because "km" vs. "kmh"
	private static final int HUD_SPEED_UNIT_TOP_OFFSET = HUD_TURNLDISTANCELEFT_UNIT_TOP_OFFSET;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public HUDUpperNextActionView(final Context context, final AttributeSet attrs) {
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
				BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_left_90_small),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_left_45_small),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_left_25_small),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_right_90_small),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_right_45_small),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_right_25_small),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_straight_small),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.flag_destination_small));
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onInitiatePaint(final Paint pHudTextPaint) {
		pHudTextPaint.setColor(Color.BLACK);
	}

	@Override
	protected void onConfigureUnitPaint(final Paint pHudTextPaint) {
		pHudTextPaint.setTextSize(8);
		pHudTextPaint.setTextScaleX(1.0f);
	}

	@Override
	protected void onConfigureValuePaint(final Paint pHudTextPaint) {
		pHudTextPaint.setTextSize(25);
		pHudTextPaint.setTextScaleX(1.1f);
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
