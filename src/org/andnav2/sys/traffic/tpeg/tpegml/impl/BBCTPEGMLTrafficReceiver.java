// Created by plusminus on 21:38:52 - 17.01.2009
package org.andnav2.sys.traffic.tpeg.tpegml.impl;

import org.andnav2.traffic.tpeg.tpegml.AbstractTPEGMLTrafficReceiver;
import org.andnav2.traffic.tpeg.util.IInputModifier;
import org.andnav2.traffic.tpeg.util.ITPEGDocumentConverter;


public class BBCTPEGMLTrafficReceiver<RESULT> extends AbstractTPEGMLTrafficReceiver<RESULT> {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String BBC_TPEGML_URL = "http://www.bbc.co.uk/travelnews/tpeg/en/local/rtm/rtm_tpeg.xml";

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public BBCTPEGMLTrafficReceiver(final ITPEGDocumentConverter<RESULT> pConverter){
		this(pConverter, null);
	}

	public BBCTPEGMLTrafficReceiver(final ITPEGDocumentConverter<RESULT> pConverter, final IInputModifier pInputModifier){
		super(BBC_TPEGML_URL, pConverter, pInputModifier);
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

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
