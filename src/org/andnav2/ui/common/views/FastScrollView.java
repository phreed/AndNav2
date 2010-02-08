// Created by plusminus on 19:03:37 - 02.12.2008
package org.andnav2.ui.common.views;

import org.andnav2.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * FastScrollView is meant for embedding {@link ListView}s that contain a large number of
 * items that can be indexed in some fashion. It displays a special scroll bar that allows jumping
 * quickly to indexed sections of the list in touch-mode. Only one child can be added to this
 * view group and it must be a {@link ListView}, with an adapter that is derived from
 * {@link BaseAdapter}.
 */
public class FastScrollView extends FrameLayout implements OnScrollListener, OnHierarchyChangeListener {

	private Drawable mCurrentThumb;
	private Drawable mOverlayDrawable;

	private int mThumbH;
	private int mThumbW;
	private int mThumbY;

	private RectF mOverlayPos;

	// Hard coding these for now
	private final int mOverlaySize = 104;

	private boolean mDragging;
	private ListView mList;
	private boolean mScrollCompleted;
	private boolean mThumbVisible;
	private int mVisibleItem;
	private Paint mPaint;
	private int mListOffset;

	private Object [] mSections;
	private String mSectionText;
	private boolean mDrawOverlay;
	private ScrollFade mScrollFade;

	private final Handler mHandler = new Handler();

	private BaseAdapter mListAdapter;

	private boolean mChangedBounds;

	public interface SectionIndexer {
		Object[] getSections();

		int getPositionForSection(int section);

		int getSectionForPosition(int position);
	}

	public FastScrollView(final Context context) {
		super(context);

		init(context);
	}


	public FastScrollView(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		init(context);
	}

	public FastScrollView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);

		init(context);
	}

	private void useThumbDrawable(final Drawable drawable) {
		this.mCurrentThumb = drawable;
		this.mThumbW = 64; //mCurrentThumb.getIntrinsicWidth();
		this.mThumbH = 52; //mCurrentThumb.getIntrinsicHeight();
		this.mChangedBounds = true;
	}

	private void init(final Context context) {
		// Get both the scrollbar states drawables
		final Resources res = context.getResources();
		useThumbDrawable(res.getDrawable(R.drawable.scrollbar_handle_accelerated_anim2));

		this.mOverlayDrawable = res.getDrawable(R.drawable.dialog_full_dark);

		this.mScrollCompleted = true;
		setWillNotDraw(false);

		// Need to know when the ListView is added
		setOnHierarchyChangeListener(this);

		this.mOverlayPos = new RectF();
		this.mScrollFade = new ScrollFade();
		this.mPaint = new Paint();
		this.mPaint.setAntiAlias(true);
		this.mPaint.setTextAlign(Paint.Align.CENTER);
		this.mPaint.setTextSize(this.mOverlaySize / 2);
		this.mPaint.setColor(0xFFFFFFFF);
		this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	}

	private void removeThumb() {

		this.mThumbVisible = false;
		// Draw one last time to remove thumb
		invalidate();
	}

	@Override
	public void draw(final Canvas canvas) {
		super.draw(canvas);

		if (!this.mThumbVisible) {
			// No need to draw the rest
			return;
		}

		final int y = this.mThumbY;
		final int viewWidth = getWidth();
		final FastScrollView.ScrollFade scrollFade = this.mScrollFade;

		int alpha = -1;
		if (scrollFade.mStarted) {
			alpha = scrollFade.getAlpha();
			if (alpha < ScrollFade.ALPHA_MAX / 2) {
				this.mCurrentThumb.setAlpha(alpha * 2);
			}
			final int left = viewWidth - (this.mThumbW * alpha) / ScrollFade.ALPHA_MAX;
			this.mCurrentThumb.setBounds(left, 0, viewWidth, this.mThumbH);
			this.mChangedBounds = true;
		}

		canvas.translate(0, y);
		this.mCurrentThumb.draw(canvas);
		canvas.translate(0, -y);

		// If user is dragging the scroll bar, draw the alphabet overlay
		if (this.mDragging && this.mDrawOverlay) {
			this.mOverlayDrawable.draw(canvas);
			final Paint paint = this.mPaint;
			final float descent = paint.descent();
			final RectF rectF = this.mOverlayPos;
			canvas.drawText(this.mSectionText, (int) (rectF.left + rectF.right) / 2,
					(int) (rectF.bottom + rectF.top) / 2 + this.mOverlaySize / 4 - descent, paint);
		} else if (alpha == 0) {
			scrollFade.mStarted = false;
			removeThumb();
		} else {
			invalidate(viewWidth - this.mThumbW, y, viewWidth, y + this.mThumbH);
		}
	}

	@Override
	protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (this.mCurrentThumb != null) {
			this.mCurrentThumb.setBounds(w - this.mThumbW, 0, w, this.mThumbH);
		}
		final RectF pos = this.mOverlayPos;
		pos.left = (w - this.mOverlaySize) / 2;
		pos.right = pos.left + this.mOverlaySize;
		pos.top = h / 10; // 10% from top
		pos.bottom = pos.top + this.mOverlaySize;
		this.mOverlayDrawable.setBounds((int) pos.left, (int) pos.top,
				(int) pos.right, (int) pos.bottom);
	}

	public void onScrollStateChanged(final AbsListView view, final int scrollState) {
	}

	public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
			final int totalItemCount) {

		if (totalItemCount - visibleItemCount > 0 && !this.mDragging) {
			this.mThumbY = ((getHeight() - this.mThumbH) * firstVisibleItem) / (totalItemCount - visibleItemCount);
			if (this.mChangedBounds) {
				final int viewWidth = getWidth();
				this.mCurrentThumb.setBounds(viewWidth - this.mThumbW, 0, viewWidth, this.mThumbH);
				this.mChangedBounds = false;
			}
		}
		this.mScrollCompleted = true;
		if (firstVisibleItem == this.mVisibleItem) {
			return;
		}
		this.mVisibleItem = firstVisibleItem;
		if (!this.mThumbVisible || this.mScrollFade.mStarted) {
			this.mThumbVisible = true;
			this.mCurrentThumb.setAlpha(ScrollFade.ALPHA_MAX);
		}
		this.mHandler.removeCallbacks(this.mScrollFade);
		this.mScrollFade.mStarted = false;
		if (!this.mDragging) {
			this.mHandler.postDelayed(this.mScrollFade, 1500);
		}
	}


	private void getSections() {
		Adapter adapter = this.mList.getAdapter();
		if (adapter instanceof HeaderViewListAdapter) {
			this.mListOffset = ((HeaderViewListAdapter)adapter).getHeadersCount();
			adapter = ((HeaderViewListAdapter)adapter).getWrappedAdapter();
		}
		if (adapter instanceof SectionIndexer) {
			this.mListAdapter = (BaseAdapter) adapter;
			this.mSections = ((SectionIndexer) this.mListAdapter).getSections();
		}
	}

	public void onChildViewAdded(final View parent, final View child) {
		if (child instanceof ListView) {
			this.mList = (ListView)child;

			this.mList.setOnScrollListener(this);
			getSections();
		}
	}

	public void onChildViewRemoved(final View parent, final View child) {
		if (child == this.mList) {
			this.mList = null;
			this.mListAdapter = null;
			this.mSections = null;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(final MotionEvent ev) {
		if (this.mThumbVisible && ev.getAction() == MotionEvent.ACTION_DOWN) {
			if (ev.getX() > getWidth() - this.mThumbW && ev.getY() >= this.mThumbY &&
					ev.getY() <= this.mThumbY + this.mThumbH) {
				this.mDragging = true;
				return true;
			}
		}
		return false;
	}

	private void scrollTo(final float position) {
		final int count = this.mList.getCount();
		this.mScrollCompleted = false;
		final Object[] sections = this.mSections;
		int sectionIndex;
		if (sections != null && sections.length > 1) {
			final int nSections = sections.length;
			int section = (int) (position * nSections);
			if (section >= nSections) {
				section = nSections - 1;
			}
			sectionIndex = section;
			final SectionIndexer baseAdapter = (SectionIndexer) this.mListAdapter;
			int index = baseAdapter.getPositionForSection(section);

			// Given the expected section and index, the following code will
			// try to account for missing sections (no names starting with..)
			// It will compute the scroll space of surrounding empty sections
			// and interpolate the currently visible letter's range across the
			// available space, so that there is always some list movement while
			// the user moves the thumb.
			int nextIndex = count;
			int prevIndex = index;
			int prevSection = section;
			int nextSection = section + 1;
			// Assume the next section is unique
			if (section < nSections - 1) {
				nextIndex = baseAdapter.getPositionForSection(section + 1);
			}

			// Find the previous index if we're slicing the previous section
			if (nextIndex == index) {
				// Non-existent letter
				while (section > 0) {
					section--;
					prevIndex = baseAdapter.getPositionForSection(section);
					if (prevIndex != index) {
						prevSection = section;
						sectionIndex = section;
						break;
					}
				}
			}
			// Find the next index, in case the assumed next index is not
			// unique. For instance, if there is no P, then request for P's
			// position actually returns Q's. So we need to look ahead to make
			// sure that there is really a Q at Q's position. If not, move
			// further down...
			int nextNextSection = nextSection + 1;
			while (nextNextSection < nSections &&
					baseAdapter.getPositionForSection(nextNextSection) == nextIndex) {
				nextNextSection++;
				nextSection++;
			}
			// Compute the beginning and ending scroll range percentage of the
			// currently visible letter. This could be equal to or greater than
			// (1 / nSections).
			final float fPrev = (float) prevSection / nSections;
			final float fNext = (float) nextSection / nSections;
			index = prevIndex + (int) ((nextIndex - prevIndex) * (position - fPrev)
					/ (fNext - fPrev));
			// Don't overflow
			if (index > count - 1) {
				index = count - 1;
			}

			this.mList.setSelectionFromTop(index + this.mListOffset, 0);
		} else {
			final int index = (int) (position * count);
			this.mList.setSelectionFromTop(index + this.mListOffset, 0);
			sectionIndex = -1;
		}

		if (sectionIndex >= 0) {
			final String text = this.mSectionText = sections[sectionIndex].toString();
			this.mDrawOverlay = (text.length() != 1 || text.charAt(0) != ' ') &&
			sectionIndex < sections.length;
		} else {
			this.mDrawOverlay = false;
		}
	}

	private void cancelFling() {
		// Cancel the list fling
		final MotionEvent cancelFling = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0);
		this.mList.onTouchEvent(cancelFling);
		cancelFling.recycle();
	}

	@Override
	public boolean onTouchEvent(final MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			if (me.getX() > getWidth() - this.mThumbW
					&& me.getY() >= this.mThumbY
					&& me.getY() <= this.mThumbY + this.mThumbH) {

				this.mDragging = true;
				if (this.mListAdapter == null && this.mList != null) {
					getSections();
				}

				cancelFling();
				return true;
			}
		} else if (me.getAction() == MotionEvent.ACTION_UP) {
			if (this.mDragging) {
				this.mDragging = false;
				final Handler handler = this.mHandler;
				handler.removeCallbacks(this.mScrollFade);
				handler.postDelayed(this.mScrollFade, 1000);
				return true;
			}
		} else if (me.getAction() == MotionEvent.ACTION_MOVE) {
			if (this.mDragging) {
				final int viewHeight = getHeight();
				this.mThumbY = (int) me.getY() - this.mThumbH + 10;
				if (this.mThumbY < 0) {
					this.mThumbY = 0;
				} else if (this.mThumbY + this.mThumbH > viewHeight) {
					this.mThumbY = viewHeight - this.mThumbH;
				}
				// If the previous scrollTo is still pending
				if (this.mScrollCompleted) {
					scrollTo((float) this.mThumbY / (viewHeight - this.mThumbH));
				}
				return true;
			}
		}

		return super.onTouchEvent(me);
	}

	public class ScrollFade implements Runnable {

		long mStartTime;
		long mFadeDuration;
		boolean mStarted;
		static final int ALPHA_MAX = 255;
		static final long FADE_DURATION = 200;

		void startFade() {
			this.mFadeDuration = FADE_DURATION;
			this.mStartTime = SystemClock.uptimeMillis();
			this.mStarted = true;
		}

		int getAlpha() {
			if (!this.mStarted) {
				return ALPHA_MAX;
			}
			int alpha;
			final long now = SystemClock.uptimeMillis();
			if (now > this.mStartTime + this.mFadeDuration) {
				alpha = 0;
			} else {
				alpha = (int) (ALPHA_MAX - ((now - this.mStartTime) * ALPHA_MAX) / this.mFadeDuration);
			}
			return alpha;
		}

		public void run() {
			if (!this.mStarted) {
				startFade();
				invalidate();
			}

			if (getAlpha() > 0) {
				final int y = FastScrollView.this.mThumbY;
				final int viewWidth = getWidth();
				invalidate(viewWidth - FastScrollView.this.mThumbW, y, viewWidth, y + FastScrollView.this.mThumbH);
			} else {
				this.mStarted = false;
				removeThumb();
			}
		}
	}
}

