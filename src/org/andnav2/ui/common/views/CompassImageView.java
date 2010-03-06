// Created by plusminus on 21:59:00 - 16.09.2008
package org.andnav2.ui.common.views;

import org.andnav2.R;
import org.andnav2.util.constants.Constants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.SensorListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;


public class CompassImageView extends ImageView implements SensorListener, Constants {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected float mRotationDegree = NOT_SET;
	protected final Paint mPaint;
	protected final Bitmap mNeedleBitmap;

	// ===========================================================
	// Constructors
	// ===========================================================

	public CompassImageView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.mNeedleBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.overlay_compass_needle);
		this.mPaint = new Paint();
		this.mPaint.setStrokeWidth(4);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void onSensorChanged(final int sensor, final float[] values) {
		this.mRotationDegree = -values[0];
		if(this.getVisibility() == View.VISIBLE) {
			this.invalidate();
		}
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		if(this.mRotationDegree == NOT_SET){
			super.onDraw(canvas);
		}else{
			canvas.save();
			final int halfWidth = getWidth() / 2;
			final int halfHeight = getHeight() / 2;

			canvas.rotate(this.mRotationDegree, halfWidth, halfHeight);
			super.onDraw(canvas);

			canvas.restore();
			canvas.drawBitmap(this.mNeedleBitmap, 22 + this.getPaddingLeft(), 0 + this.getPaddingTop(), this.mPaint);
		}
	}

	public void onAccuracyChanged(final int arg0, final int arg1) {
		// TODO ...
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
