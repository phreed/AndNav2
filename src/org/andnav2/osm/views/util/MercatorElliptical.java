package org.andnav2.osm.views.util;

/**
 * 
 * @author Nicolas Gramlich
 * UNTESTED !
 */
public class MercatorElliptical {
	// ===========================================================
	// Constants
	// ===========================================================

	final private static double R_MAJOR = 6378137.0;
	final private static double R_MINOR = 6356752.3142;

	// ===========================================================
	// Static Methods
	// ===========================================================

	public static double[] merc(final double x, final double y) {
		return new double[] {mercX(x), mercY(y)};
	}

	private static double  mercX(final double lon) {
		return R_MAJOR * Math.toRadians(lon);
	}

	private static double mercY(double lat) {
		if (lat > 89.5) {
			lat = 89.5;
		}
		if (lat < -89.5) {
			lat = -89.5;
		}
		final double temp = R_MINOR / R_MAJOR;
		final double es = 1.0 - (temp * temp);
		final double eccent = Math.sqrt(es);
		final double phi = Math.toRadians(lat);
		final double sinphi = Math.sin(phi);
		double con = eccent * sinphi;
		final double com = 0.5 * eccent;
		con = Math.pow(((1.0-con)/(1.0+con)), com);
		final double ts = Math.tan(0.5 * ((Math.PI*0.5) - phi))/con;
		final double y = 0 - R_MAJOR * Math.log(ts);
		return y;
	}
}
