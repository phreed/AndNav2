// Created by plusminus on 6:20:06 PM - Feb 21, 2009
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.preferences.PreferenceConstants;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.andnav2.ui.map.hud.HUDRegistry;
import org.andnav2.ui.map.hud.IHUDImpl;
import org.andnav2.ui.map.hud.IHUDImplVariation;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemSelectedListener;


public class SettingsHUD extends AndNavBaseActivity implements PreferenceConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private ViewFlipper mVariationFlipper;
	private IHUDImpl[] mHUDImpls;
	private IHUDImpl mCurrentHUDImpl;
	private int mCurrentVariation;
	private Spinner mSpinHUDImpls;
	private ImageButton mIbtnVariationNext;
	private ImageButton mIbtnVariationPrevious;
	private TextView mTvVariationDescription;
	private Animation mAnimSlideOutLeft;
	private Animation mAnimSlideInRight;
	private Animation mAnimSlideOutRight;
	private Animation mAnimSlideInLeft;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_hud);

		this.mTvVariationDescription = (TextView)this.findViewById(R.id.tv_settings_hud_variation_description);

		this.mVariationFlipper = (ViewFlipper)findViewById(R.id.flip_settings_hud_variations);

		this.mAnimSlideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
		this.mAnimSlideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
		this.mAnimSlideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
		this.mAnimSlideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);

		this.mSpinHUDImpls = (Spinner)this.findViewById(R.id.spin_settings_hud_implementations);
		this.mIbtnVariationNext = (ImageButton)this.findViewById(R.id.ibtn_settings_hud_variation_next);
		this.mIbtnVariationPrevious = (ImageButton)this.findViewById(R.id.ibtn_settings_hud_variation_previous);

		initHUDSelectionViews();
		applyTopButtonListeners();

		initVariationSwitcher();
	}

	private void initHUDSelectionViews() {
		this.mHUDImpls = HUDRegistry.getAll();

		/* We need to convert the HudImpls to a String representation to show the in the Spinner. */
		final String[] hudImplNames = new String[this.mHUDImpls.length];
		for (int i = 0; i < this.mHUDImpls.length; i++) {
			final IHUDImpl h = this.mHUDImpls[i];

			hudImplNames[i] = getString(h.getNameResourceID());
		}

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hudImplNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		this.mSpinHUDImpls.setAdapter(adapter);

		this.mSpinHUDImpls.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id){
				if(SettingsHUD.this.mCurrentHUDImpl.getID() != SettingsHUD.this.mHUDImpls[position].getID()){
					SettingsHUD.this.mCurrentHUDImpl = SettingsHUD.this.mHUDImpls[position];
					SettingsHUD.this.mCurrentVariation = 0;
					Preferences.saveHUDImpl(SettingsHUD.this, SettingsHUD.this.mCurrentHUDImpl, IHUDImplVariation.VARIATION_DEFAULT_ID);
					initVariationSwitcher();
				}
			}

			public void onNothingSelected(final AdapterView<?> arg0) {

			}
		});


		this.mIbtnVariationNext.setOnClickListener(new OnClickListener(){
			public void onClick(final View arg0) {
				SettingsHUD.this.mVariationFlipper.setInAnimation(SettingsHUD.this.mAnimSlideInLeft);
				SettingsHUD.this.mVariationFlipper.setOutAnimation(SettingsHUD.this.mAnimSlideOutRight);
				SettingsHUD.this.mCurrentVariation++;
				SettingsHUD.this.mVariationFlipper.showNext();
				SettingsHUD.this.refreshVariationNameAndFlipperButtons();
			}
		});

		this.mIbtnVariationPrevious.setOnClickListener(new OnClickListener(){
			public void onClick(final View arg0) {
				SettingsHUD.this.mVariationFlipper.setInAnimation(SettingsHUD.this.mAnimSlideInRight);
				SettingsHUD.this.mVariationFlipper.setOutAnimation(SettingsHUD.this.mAnimSlideOutLeft);

				SettingsHUD.this.mCurrentVariation--;
				SettingsHUD.this.mVariationFlipper.showPrevious();
				SettingsHUD.this.refreshVariationNameAndFlipperButtons();
			}
		});

		/* Finally select the HUD currently set. */
		this.mCurrentHUDImpl = Preferences.getHUDImpl(this);

		for(int i = 0; i < this.mHUDImpls.length; i++){
			if(this.mHUDImpls[i].getID() == this.mCurrentHUDImpl.getID()){
				this.mSpinHUDImpls.setSelection(i);
				break;
			}
		}

		this.mCurrentVariation = Preferences.getHUDImplVariationID(this);
	}

	private void initVariationSwitcher(){
		this.mVariationFlipper.removeAllViews();
		for (final IHUDImplVariation v : this.mCurrentHUDImpl.getVariations()) {
			final ImageView iv = new ImageView(this);
			iv.setImageResource(v.getScreenshotResourceID());
			this.mVariationFlipper.addView(iv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		}

		this.refreshVariationNameAndFlipperButtons();
	}

	private void refreshVariationNameAndFlipperButtons(){
		final boolean hasNextVariation = this.mCurrentVariation < this.mCurrentHUDImpl.getCountOfVariations() - 1;
		this.mIbtnVariationNext.setEnabled(hasNextVariation);

		final boolean hasPrevoiusVariation = this.mCurrentVariation > 0;
		this.mIbtnVariationPrevious.setEnabled(hasPrevoiusVariation);

		final IHUDImplVariation v = this.mCurrentHUDImpl.getVariations()[this.mCurrentVariation];

		this.mTvVariationDescription.setText(v.getDescriptionStringID());

		Preferences.saveHUDImpl(this, this.mCurrentHUDImpl, v);

		this.mVariationFlipper.setDisplayedChild(this.mCurrentVariation);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private void applyTopButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_hud_close)){
			@Override
			public void onBoth(final View arg0, final boolean focused) {
				if(SettingsHUD.super.mMenuVoiceEnabled && focused) {
					MediaPlayer.create(SettingsHUD.this, R.raw.close).start();
				}
			}

			// No onFocusChange, because there is no Quickinfo-Text

			@Override
			public void onClicked(final View me) {
				SettingsHUD.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
