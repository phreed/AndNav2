/**
 * 
 * 
 */
package org.andnav2.osm.views.overlay;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.Point;

/**
 * The marker object knows how to draw himself focused and not. 
 * The default is a bitmap centered at its upper left corner.
 * 
 * @author phreed
 *
 */
@SuppressWarnings("unused")
public class OSMMapViewMarkerForFocus 
extends OSMMapViewMarkerSimple 
{
	private Point mLocation;
	
	private int mBackgroundColor;
	/**
	 * 
	 */
	public OSMMapViewMarkerForFocus(
			final Drawable marker, 
			final Point hotspot,
			final int backgroundColor)
	{
		super(marker, hotspot);
		this.mBackgroundColor = backgroundColor;
//		if(pFocusedBackgroundColor != NOT_SET) {
//			this.mMarkerFocusedBackgroundColor = mMarkerFocusedBase.getBackgroundColor();
//			pFocusedBackgroundColor;
//		} else {
//			this.mMarkerFocusedBackgroundColor = DEFAULTMARKER_BACKGROUNDCOLOR;
//		}
	}

	/**
	 * If marker cannot be drawn it returns false.
	 * 
	 */
	
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
	
	public int getBackgroundColor() { return this.mBackgroundColor; }

}

