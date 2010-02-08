/**
 * 
 */
package org.andnav2.osm.views.overlay;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.Point;

/**
 * I'm creating a drawable AbstractOSMMarker.java class to hold the drawable and its hotspot. 
 * I'm thinking that the drawable object should know how to draw himself focused and not. 
 * The default is a bitmap centered at its upper left corner.
 * 
 * Mostly this is a wrapper for Drawable objects.
 * 
 * @author phreed
 *
 */
public class AbstractOSMMapViewMarker {

	final private Drawable mMarker;
	final private Point mHotspot;
	final private int mWidth, mHeight;
	
	final private Drawable mFocusMarker;
	@SuppressWarnings("unused")
	final private Point mFocusHotspot;
	@SuppressWarnings("unused")
	final private int mFocusWidth, mFocusHeight;
	
	private Point mLocation;
	/**
	 * 
	 */
	public AbstractOSMMapViewMarker(final Drawable marker, final Point hotspot) {
		this.mMarker = marker;
		this.mHotspot = hotspot;
		this.mWidth = this.mMarker.getIntrinsicWidth();
		this.mHeight = this.mMarker.getIntrinsicHeight();
		
		this.mFocusMarker = null;
		this.mFocusHotspot = null;
		this.mFocusWidth = 0;
		this.mFocusHeight = 0;
	}
	
	public AbstractOSMMapViewMarker(final Drawable marker, final Point hotspot,
			final Drawable focusMarker, final Point focusHotspot) 
	{
		this.mMarker = marker;
		this.mHotspot = hotspot;
		this.mWidth = this.mMarker.getIntrinsicWidth();
		this.mHeight = this.mMarker.getIntrinsicHeight();
		
		this.mFocusMarker = focusMarker;
		this.mFocusHotspot = focusHotspot;
		this.mFocusWidth = this.mFocusMarker.getIntrinsicWidth();
		this.mFocusHeight = this.mFocusMarker.getIntrinsicHeight();
	}

	/**
	 * 
	 */
	public void relocate(final Point location) {
		this.mLocation = location;
	}
	/**
	 * If marker cannot be drawn it returns false.
	 * 
	 */
	public boolean onDraw(final Canvas canvas, final Point location) {
		if (location == null && this.mLocation == null) return false;
		Point locate = (location == null) ? this.mLocation : location;
		
		final int left = locate.x - this.mHotspot.x;
		final int top = locate.y - this.mHotspot.y;
		
		final int right = left + this.mWidth;
		final int bottom = top + this.mHeight;

		if(right < 0) return false;
		if(bottom < 0) return false;
		
		final int height = canvas.getHeight() * 2;
		final int width = canvas.getWidth() * 2;
		
		if(left > width) return false;
		if(top > height) return false;
			
		this.mMarker.setBounds(left, top, right, bottom);
		this.mMarker.draw(canvas);
		return true;
	}

	public boolean onDrawFocused(Canvas canvas, final Point location) {
		return onDraw(canvas, location);
//			final List<T> overlayItems = this.getOverlayItems();
//			if(this.mFocusedItem == null) return;
//			if(overlayItems == null) return;
//			final Point screenCoords = new Point();
//
//			osmv.getProjection().toPixels(this.mFocusedItem, screenCoords);
//
//			if(this.mDrawBaseIntemUnderFocusedItem) {
//				this.mMarker.onDraw(c,screenCoords);
//			}
//
//			/* Calculate and set the bounds of the marker. */
//			final int left = screenCoords.x - this.mMarker.focusedHotSpot.x;
//			final int right = left + this.mMarkerFocusedWidth;
//			final int top = screenCoords.y - this.mMarker.focusedHotSpot.y;
//			final int bottom = top + this.mMarkerFocusedHeight;
//			this.mMarkerFocusedBase.setBounds(left, top, right, bottom);
//
//			/* Strings of the OverlayItem, we need. */
//			final String itemTitle = (this.mFocusedItem.mTitle == null) ? this.UNKNOWN : this.mFocusedItem.mTitle;
//			final String itemDescription = (this.mFocusedItem.mDescription == null) ? this.UNKNOWN : this.mFocusedItem.mDescription;
//
//			/* Store the width needed for each char in the description to a float array. This is pretty efficient. */
//			final float[] widths = new float[itemDescription.length()];
//			this.mDescriptionPaint.getTextWidths(itemDescription, widths);
//
//			final StringBuilder sb = new StringBuilder();
//			int maxWidth = 0;
//			int curLineWidth = 0;
//			int lastStop = 0;
//			int i;
//			int lastwhitespace = 0;
//			/* Loop through the charwidth array and harshly insert a linebreak,
//			 * when the width gets bigger than DESCRIPTION_MAXWIDTH. */
//			for (i = 0; i < widths.length; i++) {
//				final char curChar = itemDescription.charAt(i);
//				if(!Character.isLetter(curChar)) {
//					lastwhitespace = i;
//				}
//
//				final float charwidth = widths[i];
//
//				if(curLineWidth + charwidth > DESCRIPTION_MAXWIDTH || curChar == '\n'){
//					if(lastStop == lastwhitespace) {
//						i--;
//					} else {
//						i = lastwhitespace;
//					}
//
//					sb.append(itemDescription.subSequence(lastStop, i));
//					if(curChar != '\n') {
//						sb.append('\n');
//					}
//
//					lastStop = i;
//					maxWidth = Math.max(maxWidth, curLineWidth);
//					curLineWidth = 0;
//				}
//
//				curLineWidth += charwidth;
//			}
//			/* Add the last line to the rest to the buffer. */
//			if(i != lastStop){
//				final String rest = itemDescription.substring(lastStop, i);
//
//				maxWidth = Math.max(maxWidth, (int)this.mDescriptionPaint.measureText(rest));
//
//				sb.append(rest);
//			}
//			final String[] lines = sb.toString().split("\n");
//
//			/* The title also needs to be taken into consideration for the width calculation. */
//			final int titleWidth = (int)this.mDescriptionPaint.measureText(itemTitle) + this.mPaddingTitleLeft + this.mPaddingTitleRight;
//
//			maxWidth = Math.max(maxWidth, titleWidth);
//			final int descWidth = Math.min(maxWidth, DESCRIPTION_MAXWIDTH);
//
//			/* Calculate the bounds of the Description box that needs to be drawn. */
//			final int descBoxLeft = left - descWidth / 2 - DESCRIPTION_BOX_PADDING + this.mMarkerFocusedWidth / 2;
//			final int descBoxRight = descBoxLeft + descWidth + 2 * DESCRIPTION_BOX_PADDING;
//			final int descBoxBottom = top;
//			final int descBoxTop = descBoxBottom
//				- DESCRIPTION_TITLE_EXTRA_LINE_HEIGHT
//				- (lines.length + 1) * DESCRIPTION_LINE_HEIGHT /* +1 because of the title. */
//				- 2 * DESCRIPTION_BOX_PADDING;
//
//			/* Twice draw a RoundRect, once in black with 1px as a small border. */
//			this.mMarkerBackgroundPaint.setColor(Color.BLACK);
//			c.drawRoundRect(new RectF(descBoxLeft - 1, descBoxTop - 1, descBoxRight + 1, descBoxBottom + 1),
//					DESCRIPTION_BOX_CORNERWIDTH, DESCRIPTION_BOX_CORNERWIDTH,
//					this.mDescriptionPaint);
//			this.mMarkerBackgroundPaint.setColor(this.mMarkerFocusedBackgroundColor);
//			c.drawRoundRect(new RectF(descBoxLeft, descBoxTop, descBoxRight, descBoxBottom),
//					DESCRIPTION_BOX_CORNERWIDTH, DESCRIPTION_BOX_CORNERWIDTH,
//					this.mMarkerBackgroundPaint);
//
//			final int descLeft = descBoxLeft + DESCRIPTION_BOX_PADDING;
//			int descTextLineBottom = descBoxBottom - DESCRIPTION_BOX_PADDING;
//
//			/* Draw all the lines of the description. */
//			for(int j = lines.length - 1; j >= 0; j--){
//				c.drawText(lines[j].trim(), descLeft, descTextLineBottom, this.mDescriptionPaint);
//				descTextLineBottom -= DESCRIPTION_LINE_HEIGHT;
//			}
//			/* Draw the title. */
//			c.drawText(itemTitle, descLeft + this.mPaddingTitleLeft, descTextLineBottom - DESCRIPTION_TITLE_EXTRA_LINE_HEIGHT, this.mTitlePaint);
//			c.drawLine(descBoxLeft, descTextLineBottom, descBoxRight, descTextLineBottom, this.mDescriptionPaint);
//
//			/* Finally draw the marker base. This is done in the end to make it look better. */
//			this.mMarkerFocusedBase.draw(c);
//		}

	}

	/**
	 * wrapper methods - they pass through to the wrapped object.
	 */
	public int getOpacity() { return this.mMarker.getOpacity(); }
	public void setAlpha(int alpha) { this.mMarker.setAlpha(alpha); }
	
	public int getIntrinsicWidth() { return this.mMarker.getIntrinsicWidth(); }
	public int getIntrinsicHeight() { return this.mMarker.getIntrinsicHeight(); }
	
	public void setColorFilter(ColorFilter cf) { this.mMarker.setColorFilter(cf); }
	
	public Point getHotSpot() { return this.mHotspot; }

}
