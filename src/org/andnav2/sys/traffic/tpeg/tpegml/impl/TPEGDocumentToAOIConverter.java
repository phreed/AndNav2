// Created by plusminus on 21:49:02 - 17.01.2009
package org.andnav2.sys.traffic.tpeg.tpegml.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.aoi.AreaOfInterest;
import org.andnav2.sys.ors.adt.aoi.CircleByCenterPoint;
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
public class TPEGDocumentToAOIConverter implements ITPEGDocumentConverter<List<AreaOfInterest>>{

	// ===========================================================
	// Constants
	// ===========================================================

	private static final HashMap<RTM31_general_magnitude, Integer> MAGNITUDE_TO_RADIUS_MAPPER = new HashMap<RTM31_general_magnitude, Integer>();
	static{
		MAGNITUDE_TO_RADIUS_MAPPER.put(RTM31_general_magnitude.VERY_SLIGHT, 250);
		MAGNITUDE_TO_RADIUS_MAPPER.put(RTM31_general_magnitude.SLIGHT, 500);
		MAGNITUDE_TO_RADIUS_MAPPER.put(RTM31_general_magnitude.MEDIUM, 1000);;
		MAGNITUDE_TO_RADIUS_MAPPER.put(RTM31_general_magnitude.SEVERE, 2000);
		MAGNITUDE_TO_RADIUS_MAPPER.put(RTM31_general_magnitude.VERY_SEVERE, 5000);
		MAGNITUDE_TO_RADIUS_MAPPER.put(RTM31_general_magnitude.UNKNOWN, 500);
		MAGNITUDE_TO_RADIUS_MAPPER.put(RTM31_general_magnitude.UNSPECIFIED, 500);
	}

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

	public List<AreaOfInterest> convert(final TPEGDocument pDocument) {
		final List<AreaOfInterest> out = new ArrayList<AreaOfInterest>();

		/* Convert the RoadTrafficMessages of the Document. */
		final List<RoadTrafficMessage> roadTrafficMessages = pDocument.getRoadTrafficMessages();
		if(roadTrafficMessages != null){
			convertRTMToAOI(out, roadTrafficMessages);
		}

		/* Convert the RoadTrafficMessages of each TPEGMessage of the Document. */
		final ArrayList<TPEGMessage> tpegMessages = pDocument.getTPEGMessages();
		if(tpegMessages != null){
			for(final TPEGMessage tm : tpegMessages){
				convertRTMToAOI(out, tm.getRoadTrafficMessages());
			}
		}

		return out;
	}

	private static void convertRTMToAOI(final List<AreaOfInterest> out, final List<RoadTrafficMessage> roadTrafficMessages) {
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
										final int radiusMeters = MAGNITUDE_TO_RADIUS_MAPPER.get(rtm.getSeverityFactor());
										out.add(new CircleByCenterPoint(gp, radiusMeters));
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
