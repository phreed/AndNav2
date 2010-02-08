// Created by plusminus on 22:59:38 - 12.09.2008
package org.andnav2.osm.views.overlay;

import org.andnav2.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class OSMMapViewItemizedOverlayControlView extends LinearLayout{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final ImageButton mIbtnPrevious;
	private final ImageButton mIbtnNext;
	private final ImageButton mIbtnCenterTo;
	private final ImageButton mIbtnNavTo;

	private ItemizedOverlayControlViewListener mLis;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewItemizedOverlayControlView(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		this.mIbtnPrevious = new ImageButton(context);
		this.mIbtnPrevious.setImageDrawable(context.getResources().getDrawable(R.drawable.previous));

		this.mIbtnNext = new ImageButton(context);
		this.mIbtnNext.setImageDrawable(context.getResources().getDrawable(R.drawable.next));

		this.mIbtnCenterTo = new ImageButton(context);
		this.mIbtnCenterTo.setImageDrawable(context.getResources().getDrawable(R.drawable.center));

		this.mIbtnNavTo = new ImageButton(context);
		this.mIbtnNavTo.setImageDrawable(context.getResources().getDrawable(R.drawable.navto_small));

		this.addView(this.mIbtnPrevious, new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.addView(this.mIbtnCenterTo, new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.addView(this.mIbtnNavTo, new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.addView(this.mIbtnNext, new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		initViewListeners();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setItemizedOverlayControlViewListener(final ItemizedOverlayControlViewListener lis) {
		this.mLis = lis;
	}

	public void setNextEnabled(final boolean pEnabled){
		this.mIbtnNext.setEnabled(pEnabled);
	}

	public void setPreviousEnabled(final boolean pEnabled){
		this.mIbtnPrevious.setEnabled(pEnabled);
	}

	public void setNavToVisible(final int pVisibility){
		this.mIbtnNavTo.setVisibility(pVisibility);
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private void initViewListeners(){
		this.mIbtnNext.setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				if(OSMMapViewItemizedOverlayControlView.this.mLis != null) {
					OSMMapViewItemizedOverlayControlView.this.mLis.onNext();
				}
			}
		});

		this.mIbtnPrevious.setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				if(OSMMapViewItemizedOverlayControlView.this.mLis != null) {
					OSMMapViewItemizedOverlayControlView.this.mLis.onPrevious();
				}
			}
		});

		this.mIbtnCenterTo.setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				if(OSMMapViewItemizedOverlayControlView.this.mLis != null) {
					OSMMapViewItemizedOverlayControlView.this.mLis.onCenter();
				}
			}
		});

		this.mIbtnNavTo.setOnClickListener(new OnClickListener(){
			public void onClick(final View v) {
				if(OSMMapViewItemizedOverlayControlView.this.mLis != null) {
					OSMMapViewItemizedOverlayControlView.this.mLis.onNavTo();
				}
			}
		});
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public interface ItemizedOverlayControlViewListener{
		public void onPrevious();
		public void onNext();
		public void onCenter();
		public void onNavTo();
	}
}
