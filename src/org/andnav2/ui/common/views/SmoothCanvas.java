// Created by plusminus on 11:53:35 - 29.08.2008
package org.andnav2.ui.common.views;

import javax.microedition.khronos.opengles.GL;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class SmoothCanvas extends Canvas {
	protected Canvas delegate;

	private final Paint mSmooth = new Paint(Paint.FILTER_BITMAP_FLAG);
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void setBitmap(final Bitmap bitmap) {
		this.delegate.setBitmap(bitmap);
	}

	@Override
	public void setViewport(final int width, final int height) {
		this.delegate.setViewport(width, height);
	}

	@Override
	public boolean isOpaque() {
		return this.delegate.isOpaque();
	}

	@Override
	public int getWidth() {
		return this.delegate.getWidth();
	}

	@Override
	public int getHeight() {
		return this.delegate.getHeight();
	}

	@Override
	public int save() {
		return this.delegate.save();
	}

	@Override
	public int save(final int saveFlags) {
		return this.delegate.save(saveFlags);
	}

	@Override
	public int saveLayer(final RectF bounds, final Paint paint, final int saveFlags) {
		return this.delegate.saveLayer(bounds, paint, saveFlags);
	}

	@Override
	public int saveLayer(final float left, final float top, final float right, final float
			bottom, final Paint paint,
			final int saveFlags) {
		return this.delegate.saveLayer(left, top, right, bottom, paint,
				saveFlags);
	}

	@Override
	public int saveLayerAlpha(final RectF bounds, final int alpha, final int saveFlags) {
		return this.delegate.saveLayerAlpha(bounds, alpha, saveFlags);
	}

	@Override
	public int saveLayerAlpha(final float left, final float top, final float right,
			final float bottom, final int alpha,
			final int saveFlags) {
		return this.delegate.saveLayerAlpha(left, top, right, bottom,
				alpha, saveFlags);
	}

	@Override
	public void restore() {
		this.delegate.restore();
	}

	@Override
	public int getSaveCount() {
		return this.delegate.getSaveCount();
	}

	@Override
	public void restoreToCount(final int saveCount) {
		this.delegate.restoreToCount(saveCount);
	}

	@Override
	public void translate(final float dx, final float dy) {
		this.delegate.translate(dx, dy);
	}

	@Override
	public void scale(final float sx, final float sy) {
		this.delegate.scale(sx, sy);
	}

	@Override
	public void rotate(final float degrees) {
		this.delegate.rotate(degrees);
	}

	@Override
	public void skew(final float sx, final float sy) {
		this.delegate.skew(sx, sy);
	}

	@Override
	public void concat(final Matrix matrix) {
		this.delegate.concat(matrix);
	}

	@Override
	public void setMatrix(final Matrix matrix) {
		this.delegate.setMatrix(matrix);
	}

	@Override
	public void getMatrix(final Matrix ctm) {
		this.delegate.getMatrix(ctm);
	}

	@Override
	public boolean clipRect(final RectF rect, final Region.Op op) {
		return this.delegate.clipRect(rect, op);
	}

	@Override
	public boolean clipRect(final Rect rect, final Region.Op op) {
		return this.delegate.clipRect(rect, op);
	}

	@Override
	public boolean clipRect(final RectF rect) {
		return this.delegate.clipRect(rect);
	}

	@Override
	public boolean clipRect(final Rect rect) {
		return this.delegate.clipRect(rect);
	}

	@Override
	public boolean clipRect(final float left, final float top, final float right,
			final float bottom, final Region.Op op) {
		return this.delegate.clipRect(left, top, right, bottom, op);
	}

	@Override
	public boolean clipRect(final float left, final float top, final float right,
			final float bottom) {
		return this.delegate.clipRect(left, top, right, bottom);
	}

	@Override
	public boolean clipRect(final int left, final int top, final int right, final int bottom) {
		return this.delegate.clipRect(left, top, right, bottom);
	}

	@Override
	public boolean clipPath(final Path path, final Region.Op op) {
		return this.delegate.clipPath(path, op);
	}

	@Override
	public boolean clipPath(final Path path) {
		return this.delegate.clipPath(path);
	}

	@Override
	public boolean clipRegion(final Region region, final Region.Op op) {
		return this.delegate.clipRegion(region, op);
	}

	@Override
	public boolean clipRegion(final Region region) {
		return this.delegate.clipRegion(region);
	}

	@Override
	public DrawFilter getDrawFilter() {
		return this.delegate.getDrawFilter();
	}

	@Override
	public void setDrawFilter(final DrawFilter filter) {
		this.delegate.setDrawFilter(filter);
	}

	@Override
	public GL getGL() {
		return this.delegate.getGL();
	}

	@Override
	public boolean quickReject(final RectF rect, final EdgeType type) {
		return this.delegate.quickReject(rect, type);
	}

	@Override
	public boolean quickReject(final Path path, final EdgeType type) {
		return this.delegate.quickReject(path, type);
	}

	@Override
	public boolean quickReject(final float left, final float top, final float right,
			final float bottom,
			final EdgeType type) {
		return this.delegate.quickReject(left, top, right, bottom, type);
	}

	@Override
	public boolean getClipBounds(final Rect bounds) {
		return this.delegate.getClipBounds(bounds);
	}

	@Override
	public void drawRGB(final int r, final int g, final int b) {
		this.delegate.drawRGB(r, g, b);
	}

	@Override
	public void drawARGB(final int a, final int r, final int g, final int b) {
		this.delegate.drawARGB(a, r, g, b);
	}

	@Override
	public void drawColor(final int color) {
		this.delegate.drawColor(color);
	}

	@Override
	public void drawColor(final int color, final PorterDuff.Mode mode) {
		this.delegate.drawColor(color, mode);
	}

	@Override
	public void drawPaint(final Paint paint) {
		this.delegate.drawPaint(paint);
	}

	@Override
	public void drawPoints(final float[] pts, final int offset, final int count,
			final Paint paint) {
		this.delegate.drawPoints(pts, offset, count, paint);
	}

	@Override
	public void drawPoints(final float[] pts, final Paint paint) {
		this.delegate.drawPoints(pts, paint);
	}

	@Override
	public void drawPoint(final float x, final float y, final Paint paint) {
		this.delegate.drawPoint(x, y, paint);
	}

	@Override
	public void drawLine(final float startX, final float startY, final float stopX,
			final float stopY, final Paint paint) {
		this.delegate.drawLine(startX, startY, stopX, stopY, paint);
	}

	@Override
	public void drawLines(final float[] pts, final int offset, final int count, final Paint paint) {
		this.delegate.drawLines(pts, offset, count, paint);
	}

	@Override
	public void drawLines(final float[] pts, final Paint paint) {
		this.delegate.drawLines(pts, paint);
	}

	@Override
	public void drawRect(final RectF rect, final Paint paint) {
		this.delegate.drawRect(rect, paint);
	}

	@Override
	public void drawRect(final Rect r, final Paint paint) {
		this.delegate.drawRect(r, paint);
	}

	@Override
	public void drawRect(final float left, final float top, final float right, final float
			bottom, final Paint paint) {
		this.delegate.drawRect(left, top, right, bottom, paint);
	}

	@Override
	public void drawOval(final RectF oval, final Paint paint) {
		this.delegate.drawOval(oval, paint);
	}

	@Override
	public void drawCircle(final float cx, final float cy, final float radius, final Paint paint) {
		this.delegate.drawCircle(cx, cy, radius, paint);
	}

	@Override
	public void drawArc(final RectF oval, final float startAngle, final float
			sweepAngle, final boolean useCenter,
			final Paint paint) {
		this.delegate.drawArc(oval, startAngle, sweepAngle, useCenter, paint);
	}

	@Override
	public void drawRoundRect(final RectF rect, final float rx, final float ry, final Paint paint) {
		this.delegate.drawRoundRect(rect, rx, ry, paint);
	}

	@Override
	public void drawPath(final Path path, final Paint paint) {
		this.delegate.drawPath(path, paint);
	}

	@Override
	public void drawBitmap(final Bitmap bitmap, final float left, final float top,
			Paint paint) {
		if (paint == null) {
			paint = this.mSmooth;
		} else {
			paint.setFilterBitmap(true);
		}
		this.delegate.drawBitmap(bitmap, left, top, paint);
	}

	@Override
	public void drawBitmap(final Bitmap bitmap, final Rect src, final RectF dst,
			Paint paint) {
		if (paint == null) {
			paint = this.mSmooth;
		} else {
			paint.setFilterBitmap(true);
		}
		this.delegate.drawBitmap(bitmap, src, dst, paint);
	}

	@Override
	public void drawBitmap(final Bitmap bitmap, final Rect src, final Rect dst, Paint paint) {
		if (paint == null) {
			paint = this.mSmooth;
		} else {
			paint.setFilterBitmap(true);
		}
		this.delegate.drawBitmap(bitmap, src, dst, paint);
	}

	@Override
	public void drawBitmap(final int[] colors, final int offset, final int stride,
			final int x, final int y, final int width,
			final int height, final boolean hasAlpha, Paint paint) {
		if (paint == null) {
			paint = this.mSmooth;
		} else {
			paint.setFilterBitmap(true);
		}
		this.delegate.drawBitmap(colors, offset, stride, x, y, width,
				height, hasAlpha, paint);
	}

	@Override
	public void drawBitmap(final Bitmap bitmap, final Matrix matrix, Paint paint) {
		if (paint == null) {
			paint = this.mSmooth;
		} else {
			paint.setFilterBitmap(true);
		}
		this.delegate.drawBitmap(bitmap, matrix, paint);
	}

	@Override
	public void drawBitmapMesh(final Bitmap bitmap, final int meshWidth, final int
			meshHeight, final float[] verts,
			final int vertOffset, final int[] colors, final int colorOffset, final Paint paint) {
		this.delegate.drawBitmapMesh(bitmap, meshWidth, meshHeight,
				verts, vertOffset, colors,
				colorOffset, paint);
	}

	@Override
	public void drawVertices(final VertexMode mode, final int vertexCount,
			final float[] verts, final int vertOffset,
			final float[] texs, final int texOffset, final int[] colors, final int
			colorOffset, final short[] indices,
			final int indexOffset, final int indexCount, final Paint paint) {
		this.delegate.drawVertices(mode, vertexCount, verts,
				vertOffset, texs, texOffset, colors,
				colorOffset, indices, indexOffset, indexCount, paint);
	}

	@Override
	public void drawText(final char[] text, final int index, final int count, final float
			x, final float y, final Paint paint) {
		this.delegate.drawText(text, index, count, x, y, paint);
	}

	@Override
	public void drawText(final String text, final float x, final float y, final Paint paint) {
		this.delegate.drawText(text, x, y, paint);
	}

	@Override
	public void drawText(final String text, final int start, final int end, final float x,
			final float y, final Paint paint) {
		this.delegate.drawText(text, start, end, x, y, paint);
	}

	@Override
	public void drawText(final CharSequence text, final int start, final int end,
			final float x, final float y, final Paint paint) {
		this.delegate.drawText(text, start, end, x, y, paint);
	}

	@Override
	public void drawPosText(final char[] text, final int index, final int count,
			final float[] pos, final Paint paint) {
		this.delegate.drawPosText(text, index, count, pos, paint);
	}

	@Override
	public void drawPosText(final String text, final float[] pos, final Paint paint) {
		this.delegate.drawPosText(text, pos, paint);
	}

	@Override
	public void drawTextOnPath(final char[] text, final int index, final int count,
			final Path path, final float hOffset,
			final float vOffset, final Paint paint) {
		this.delegate.drawTextOnPath(text, index, count, path, hOffset,
				vOffset, paint);
	}

	@Override
	public void drawTextOnPath(final String text, final Path path, final float
			hOffset, final float vOffset,
			final Paint paint) {
		this.delegate.drawTextOnPath(text, path, hOffset, vOffset, paint);
	}

	@Override
	public void drawPicture(final Picture picture) {
		this.delegate.drawPicture(picture);
	}

	@Override
	public void drawPicture(final Picture picture, final RectF dst) {
		this.delegate.drawPicture(picture, dst);
	}

	@Override
	public void drawPicture(final Picture picture, final Rect dst) {
		this.delegate.drawPicture(picture, dst);
	}

	// ===========================================================
		// Methods
		// ===========================================================

			// ===========================================================
				// Inner and Anonymous Classes
				// ===========================================================
}
