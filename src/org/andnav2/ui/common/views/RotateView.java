// Created by plusminus on 11:52:08 - 29.08.2008
package org.andnav2.ui.common.views;

import org.andnav2.util.constants.Constants;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class RotateView extends ViewGroup implements Constants{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected static final float SQ2 = 1.414213562373095f;
	protected final SmoothCanvas mCanvas = new SmoothCanvas();
	protected float mHeading = Constants.NOT_SET;

	private final Camera mCamera = new Camera();

	private float mLastRefreshPerspectiveAngle = Constants.NOT_SET;
	private float mLastRefreshWidth = Constants.NOT_SET;
	private float mPerspectiveAngle = 0f;
	private final Matrix mPerspectiveMatrix = new Matrix();

	// ===========================================================
	// Constructors
	// ===========================================================

	public RotateView(final Context context) {
		super(context);
		refreshPerspectiveMatrix();
	}

	public RotateView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		refreshPerspectiveMatrix();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setPerspectiveEnabled(final boolean pEnable){
		this.mPerspectiveAngle = (pEnable) ? -45f : 0f;
	}

	public boolean setPerspectiveEnabled(){
		return this.mPerspectiveAngle == -45f;
	}

	public void setRotationDegree(final float aHeading) {
		this.mHeading = aHeading;
	}

	public void setPerspectiveAngle(final float pAngle) {
		this.mPerspectiveAngle = pAngle;
		refreshPerspectiveMatrix();
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	private void refreshPerspectiveMatrix() {
		final float w_2 = getWidth() * 0.5f;
		final float perspectiveAngle = this.mPerspectiveAngle;
		/* Avoid unnecessary updates. */
		if(this.mLastRefreshPerspectiveAngle == perspectiveAngle && this.mLastRefreshWidth == w_2) {
			return;
		}

		final float h_2 = getHeight() * 0.5f;

		this.mLastRefreshWidth = w_2;
		this.mLastRefreshPerspectiveAngle = perspectiveAngle;

		final Matrix perspectiveMatrix = this.mPerspectiveMatrix;
		final Camera camera = this.mCamera;

		// Save the current Camera matrix
		camera.save();

		// Setup the camera rotations
		camera.rotateX(-perspectiveAngle);

		// Output the camera rotations to a matrix
		camera.getMatrix(perspectiveMatrix);
		camera.restore();
		if(perspectiveAngle != 0f){
			// Perform some other transforms on the matrix
			perspectiveMatrix.preTranslate(-w_2, -h_2);
			perspectiveMatrix.postTranslate(w_2,  h_2);
			perspectiveMatrix.postScale(1.3f, 1.3f, w_2,  h_2);
		}
	}

	@Override
	protected void dispatchDraw(final Canvas canvas) {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);

		refreshPerspectiveMatrix();

		if(this.mPerspectiveAngle != 0f){
			// Apply the matrix to the canvas
			canvas.concat(this.mPerspectiveMatrix);
		}

		final float w_2 = getWidth() * 0.5f;
		final float h_2 = getHeight() * 0.5f;

		if(this.mHeading != Constants.NOT_SET) {
			canvas.rotate(-this.mHeading, w_2, h_2); // (this.getRight() - this.getLeft()) * 0.5f, (this.getBottom() - this.getTop()) * 0.5f);
		}

		this.mCanvas.delegate = canvas;
		super.dispatchDraw(this.mCanvas);
		canvas.restore();
	}

	@Override
	protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {
		final int width = this.getRight() - this.getLeft();
		final int height = this.getBottom() - this.getTop();
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View view = getChildAt(i);
			final int childWidth = view.getMeasuredWidth();
			final int childHeight = view.getMeasuredHeight();
			final int childLeft = (width - childWidth) / 2;
			final int childTop = (height - childHeight) / 2;
			view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
		}
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		final int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		final int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		final int sizeSpec;
		if (w > h) {
			sizeSpec = MeasureSpec.makeMeasureSpec((int) (w * SQ2), MeasureSpec.EXACTLY);
		} else {
			sizeSpec = MeasureSpec.makeMeasureSpec((int) (h * SQ2), MeasureSpec.EXACTLY);
		}
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(sizeSpec, sizeSpec);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private static final int X = 0;
	private static final int Y = 1;

	@Override
	public boolean dispatchTouchEvent(final MotionEvent ev) {
		if(this.mHeading == Constants.NOT_SET) {
			return super.dispatchTouchEvent(ev);
		}

		final float[] pts = {ev.getX(), ev.getY()};
		final Matrix m = new Matrix();
		m.setRotate(this.mHeading);

		m.mapPoints(pts);
		ev.setLocation(pts[X], pts[Y]);

		return super.dispatchTouchEvent(ev);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
