package org.andnav2.util.settings;

public class Version {

	public static final int PROVERSION = 0;
	public static final int LITEVERSION = 1;
	
	private static int _version = PROVERSION;
	
	public static int version() { return _version; }
	public static boolean is_lite() { return _version == LITEVERSION; }
	public static boolean is_pro() { return _version == PROVERSION; }
}
