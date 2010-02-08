// Created by plusminus on 21:49:02 - 17.01.2009
package org.andnav2.sys.traffic.tpeg.tpegml.impl;

import java.util.ArrayList;
import java.util.List;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.ts.TrafficItem;
import org.andnav2.sys.traffic.tpeg.tpegml.impl.util.TypeConverter;
import org.andnav2.traffic.tpeg.adt.TPEGDocument;
import org.andnav2.traffic.tpeg.adt.TPEGMessage;
import org.andnav2.traffic.tpeg.adt.loc.LocationContainer;
import org.andnav2.traffic.tpeg.adt.loc.LocationCoordinates;
import org.andnav2.traffic.tpeg.adt.loc.LocationPoint;
import org.andnav2.traffic.tpeg.adt.rtm.RoadTrafficMessage;
import org.andnav2.traffic.tpeg.adt.rtm.table.RTM31_general_magnitude;
import org.andnav2.traffic.tpeg.util.ITPEGDocumentConverter;

/**
 * Class capable of converting a TPEGDocument to a List of AOI.
 * @author Nicolas 'plusminus' Gramlich
 *
 */
public class TPEGDocumentToTrafficItemConverter implements ITPEGDocumentConverter<List<TrafficItem>>{

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

	public List<TrafficItem> convert(final TPEGDocument pDocument) {
		final List<TrafficItem> out = new ArrayList<TrafficItem>();

		/* Convert the RoadTrafficMessages of the Document. */
		final List<RoadTrafficMessage> roadTrafficMessages = pDocument.getRoadTrafficMessages();
		if(roadTrafficMessages != null){
			convertRTMToTrafficItem(out, roadTrafficMessages, null);
		}

		/* Convert the RoadTrafficMessages of each TPEGMessage of the Document. */
		final ArrayList<TPEGMessage> tpegMessages = pDocument.getTPEGMessages();
		if(tpegMessages != null){
			for(final TPEGMessage tm : tpegMessages){
				String description = null;
				try {
					description = tm.getSummaries().get(0).getSummary();
				} catch (final Exception e) {
					e.printStackTrace();
				}
				convertRTMToTrafficItem(out, tm.getRoadTrafficMessages(), description);
			}
		}

		return out;
	}

	private static void convertRTMToTrafficItem(final List<TrafficItem> out, final List<RoadTrafficMessage> roadTrafficMessages, final String pDescription) {
		if(roadTrafficMessages != null){
			for(final RoadTrafficMessage rtm : roadTrafficMessages){
				final ArrayList<LocationContainer> locationContainers = rtm.getLocationContainers();
				if(locationContainers != null){
					for(final LocationContainer lc : locationContainers){
						final ArrayList<LocationCoordinates> locationCoordinates = lc.getLocationCoordinates();
						if(locationCoordinates != null){
							for(final LocationCoordinates lco : locationCoordinates){
								final ArrayList<LocationPoint> locationPoints = lco.getLocationPoints();
								if(locationPoints != null){
									for(final LocationPoint lp : locationPoints){
										final GeoPoint gp = TypeConverter.convertWGS84ToGeoPoint(lp.getCoordinates());
										final RTM31_general_magnitude severity = rtm.getSeverityFactor();
										final String description = (pDescription != null) ? pDescription : "No description found..."; // TODO i18n

										final TrafficItem tp = new TrafficItem();
										tp.setGeoPoint(gp);
										tp.setDescription(description);
										tp.setSeverity(severity);

										out.add(tp);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
